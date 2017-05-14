package org.forestguardian;

import android.content.Context;
import android.support.test.runner.AndroidJUnit4;
import android.util.Log;

import junit.framework.Assert;

import org.forestguardian.DataAccess.Local.AuthData;
import org.forestguardian.DataAccess.Local.Report;
import org.forestguardian.DataAccess.Local.SessionData;
import org.forestguardian.DataAccess.Local.User;
import org.forestguardian.DataAccess.WebServer.ForestGuardianService;
import org.forestguardian.Helpers.HeadersHelper;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.HashMap;
import java.util.UUID;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;
import okhttp3.Headers;
import retrofit2.adapter.rxjava2.Result;

import static android.support.test.InstrumentationRegistry.getInstrumentation;
import static junit.framework.Assert.*;
import static org.forestguardian.R.string.user;

/**
 * Created by emma on 26/03/17.
 */

@RunWith(AndroidJUnit4.class)
public class ForestGuardianServiceTest {

    Context context = getInstrumentation().getTargetContext();

    @Test
    public void signIn() throws InterruptedException {

        User user = new User();
        user.setEmail("testing@forestguardian.org");
        user.setPassword("12341234");
        user.setPasswordConfirmation("12341234");

        Observable<Result<SessionData>> sessionService = ForestGuardianService.global().service().signIn(user);

        //Wait for sync.
        final Object syncObject = new Object();

        sessionService.subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe( pSessionDataResult -> {

                    Assert.assertFalse( pSessionDataResult.isError() );
                    Assert.assertTrue( pSessionDataResult.response().isSuccessful() );

                    Headers authHeaders = pSessionDataResult.response().headers();
                    AuthData authData = HeadersHelper.parseHeaders( context, authHeaders );

                    assertNotNull( authData );

                    User authenticatedUser = pSessionDataResult.response().body().getUser();
                    authenticatedUser.setAuth(authData);

                    Log.e("Current User", authenticatedUser.getEmail());
                    assertEquals(authenticatedUser.getEmail(), user.getEmail());

                    //Uncomment addApiAuthorizationHeader() when this feature is enabled from backend.
                    //addApiAuthorizationHeader();

                    ForestGuardianService.global().addAuthenticationHeaders(context,authData);

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
        String email = "test_signup_android_" + UUID.randomUUID().toString() + "@forestguardian.org";
        user.setEmail(email);
        user.setPassword("12341234");
        user.setPasswordConfirmation("12341234");

        Observable<Result<SessionData>> sessionService = ForestGuardianService.global().service().signUp(user);

        //Wait for sync.
        final Object syncObject = new Object();

        sessionService.subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe( pSessionDataResult -> {

                    Assert.assertFalse( pSessionDataResult.isError() );
                    Assert.assertTrue( pSessionDataResult.response().isSuccessful() );

                    Headers authHeaders = pSessionDataResult.response().headers();
                    AuthData authData = HeadersHelper.parseHeaders(context, authHeaders);

                    User authenticatedUser = pSessionDataResult.response().body().getUser();
                    Log.i("User", authenticatedUser.getEmail());
                    assertEquals(authenticatedUser.getEmail(), user.getEmail());
                    Log.i("Authenticated User", authenticatedUser.getEmail());

                    //Uncomment addApiAuthorizationHeader() when this feature is enabled from backend.
                    //addApiAuthorizationHeader();

                    ForestGuardianService.global().addAuthenticationHeaders(context, authData);

                    synchronized (syncObject){
                        syncObject.notify();
                    }
                });

        synchronized (syncObject){
            syncObject.wait();
        }
    }

    @Test
    public void createReport() throws InterruptedException {

        Report report = new Report();
        report.setTitle("ReportTitle_" + UUID.randomUUID().toString() );
        report.setComments("Just follow the right line from the mango tree, if it hasn't burned yet.");
        report.setDescription("A couple of evil smoking squirrels. Please send help!");
        report.setGeo_latitude(0.1);
        report.setGeo_longitude(0.2);
        report.setPicture(context.getString(R.string.sample_picture_base64));

        Observable<Report> reportService = ForestGuardianService.global().service().createReport(report);

        //Wait for sync.
        final Object syncObject = new Object();

        reportService.subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe( pCreatedReport -> {

                    Assert.assertNotNull(pCreatedReport.getId() );

                    Log.i("Created Report", "id:" + String.valueOf( pCreatedReport.getId() ) );
                    Log.i("Created Report", "title:" + pCreatedReport.getTitle() );
                    Log.i("Created Report", "description:" + pCreatedReport.getDescription() );
                    Log.i("Created Report", "comments:" + pCreatedReport.getComments() );
                    Log.i("Created Report", "latitude:" + String.valueOf(pCreatedReport.getGeo_latitude()) );
                    Log.i("Created Report", "longitude:" + String.valueOf(pCreatedReport.getGeo_longitude()) );

                    synchronized (syncObject){
                        syncObject.notify();
                    }
                });

        synchronized (syncObject){
            syncObject.wait();
        }
    }
}
