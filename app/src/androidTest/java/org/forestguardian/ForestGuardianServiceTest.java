package org.forestguardian;

import android.support.test.runner.AndroidJUnit4;
import android.util.Log;

import com.google.gson.GsonBuilder;

import org.forestguardian.DataAccess.Local.Session;
import org.forestguardian.DataAccess.Local.User;
import org.forestguardian.DataAccess.WebServer.ForestGuardianAPI;
import org.junit.Test;
import org.junit.runner.RunWith;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

import static junit.framework.Assert.assertEquals;
import static org.forestguardian.DataAccess.WebServer.ForestGuardianAPI.TEST_ENDPOINT;

/**
 * Created by emma on 26/03/17.
 */

@RunWith(AndroidJUnit4.class)
public class ForestGuardianServiceTest {

    @Test
    public void signIn() throws InterruptedException {

        User user = new User();
        user.setEmail("emmanuelmora05@gmail.com");
        user.setPassword("pojapoja");
        user.setPasswordConfirmation("pojapoja");

        Session session = new Session();
        session.user = user;

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(TEST_ENDPOINT)
                .addConverterFactory(GsonConverterFactory.create(new GsonBuilder().setLenient().create()))
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .build();

        ForestGuardianAPI service = retrofit.create(ForestGuardianAPI.class);
        Observable<User> sessionService = service.signIn(session);

        //Wait for sync.
        final Object syncObject = new Object();

        sessionService.subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(pUser -> {
                    Log.e("Current User", pUser.getEmail());
                    assertEquals(pUser.getEmail(), user.getEmail());

                    synchronized (syncObject){
                        syncObject.notify();
                    }
                });

        synchronized (syncObject){
            syncObject.wait();
        }
    }
}
