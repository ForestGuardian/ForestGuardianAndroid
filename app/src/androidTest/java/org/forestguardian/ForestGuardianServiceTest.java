package org.forestguardian;

import android.support.test.runner.AndroidJUnit4;
import android.util.Log;

import com.google.gson.GsonBuilder;

import junit.framework.Assert;

import org.forestguardian.DataAccess.Local.SessionData;
import org.forestguardian.DataAccess.Local.User;
import org.forestguardian.DataAccess.WebServer.ForestGuardianAPI;
import org.junit.Test;
import org.junit.runner.RunWith;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;
import okhttp3.Headers;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.Result;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertTrue;
import static org.forestguardian.DataAccess.WebServer.ForestGuardianAPI.TEST_ENDPOINT;

/**
 * Created by emma on 26/03/17.
 */

@RunWith(AndroidJUnit4.class)
public class ForestGuardianServiceTest {

    private OkHttpClient.Builder httpBuilder = new OkHttpClient.Builder();

    public void addApiAuthorizationHeader(){

        httpBuilder.addInterceptor(chain -> {
            Request original = chain.request();

            // Request customization: add request headers
            Request.Builder requestBuilder = original.newBuilder()
                    .addHeader("Authorization", "auth-value");

            Request request = requestBuilder.build();
            return chain.proceed(request);
        });
    }

    public void addAuthenticationHeaders( String email, String token ){

        httpBuilder.addInterceptor(chain -> {
            Request original = chain.request();

            // Request customization: add request headers
            Request.Builder requestBuilder = original.newBuilder()
                    .addHeader("X-User-Email", email)
                    .addHeader("X-User-Token", token);

            Request request = requestBuilder.build();
            return chain.proceed(request);
        });
    }

    @Test
    public void signIn() throws InterruptedException {

        User user = new User();
        user.setEmail("emmanuelmora06@gmail.com");
        user.setPassword("pojapoja");
        user.setPasswordConfirmation("pojapoja");

        OkHttpClient client = httpBuilder.build();

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(TEST_ENDPOINT)
                .addConverterFactory(GsonConverterFactory.create(new GsonBuilder().setLenient().create()))
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .client(client)
                .build();

        ForestGuardianAPI service = retrofit.create(ForestGuardianAPI.class);
        Observable<Result<SessionData>> sessionService = service.signIn(user);

        //Wait for sync.
        final Object syncObject = new Object();

        sessionService.subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe( pSessionDataResult -> {

                    Assert.assertFalse( pSessionDataResult.isError() );
                    Assert.assertTrue( pSessionDataResult.response().isSuccessful() );

                    Headers authHeaders = pSessionDataResult.response().headers();
                    User authenticatedUser = pSessionDataResult.response().body().getUser();
                    authenticatedUser.setToken( authHeaders.get("Access-Token") );

                    Log.e("Current User", authenticatedUser.getEmail());
                    assertEquals(authenticatedUser.getEmail(), user.getEmail());

                    synchronized (syncObject){
                        syncObject.notify();
                    }
                });

        synchronized (syncObject){
            syncObject.wait();
        }
    }
}
