package org.forestguardian;

import android.support.test.runner.AndroidJUnit4;
import android.util.Log;

import junit.framework.Assert;

import org.forestguardian.DataAccess.Local.SessionData;
import org.forestguardian.DataAccess.Local.User;
import org.forestguardian.DataAccess.WebServer.ForestGuardianService;
import org.junit.Test;
import org.junit.runner.RunWith;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;
import okhttp3.Headers;
import retrofit2.adapter.rxjava2.Result;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertTrue;

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

        Observable<Result<SessionData>> sessionService = ForestGuardianService.global().service().signIn(user);

        //Wait for sync.
        final Object syncObject = new Object();

        sessionService.subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe( pSessionDataResult -> {

                    Assert.assertFalse( pSessionDataResult.isError() );
                    Assert.assertTrue( pSessionDataResult.response().isSuccessful() );

                    Headers authHeaders = pSessionDataResult.response().headers();
                    String accessToken = authHeaders.get("Access-Token");

                    User authenticatedUser = pSessionDataResult.response().body().getUser();
                    authenticatedUser.setToken( accessToken );

                    Log.e("Current User", authenticatedUser.getEmail());
                    assertEquals(authenticatedUser.getEmail(), user.getEmail());

                    //Uncomment addApiAuthorizationHeader() when this feature is enabled from backend.
                    //addApiAuthorizationHeader();

                    ForestGuardianService.global().addAuthenticationHeaders( authenticatedUser.getEmail(), accessToken );

                    synchronized (syncObject){
                        syncObject.notify();
                    }
                });

        synchronized (syncObject){
            syncObject.wait();
        }
    }

    @Test
    public void signUp() throws InterruptedException {

        User user = new User();
        String email = "test_signup_android_" + String.valueOf( (int)(Math.random() *100000) ) + "@forestguardian.org";
        user.setEmail(email);
        user.setPassword("pojapoja");
        user.setPasswordConfirmation("pojapoja");

        Observable<Result<SessionData>> sessionService = ForestGuardianService.global().service().signUp(user);

        //Wait for sync.
        final Object syncObject = new Object();

        sessionService.subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe( pSessionDataResult -> {

                    Assert.assertFalse( pSessionDataResult.isError() );
                    Assert.assertTrue( pSessionDataResult.response().isSuccessful() );

                    Headers authHeaders = pSessionDataResult.response().headers();
                    String accessToken = authHeaders.get("Access-Token");

                    User authenticatedUser = pSessionDataResult.response().body().getUser();
                    authenticatedUser.setToken( accessToken );

                    Log.e("Current User", authenticatedUser.getEmail());
                    assertEquals(authenticatedUser.getEmail(), user.getEmail());

                    //Uncomment addApiAuthorizationHeader() when this feature is enabled from backend.
                    //addApiAuthorizationHeader();

                    ForestGuardianService.global().addAuthenticationHeaders( authenticatedUser.getEmail(), accessToken );

                    synchronized (syncObject){
                        syncObject.notify();
                    }
                });

        synchronized (syncObject){
            syncObject.wait();
        }
    }
}
