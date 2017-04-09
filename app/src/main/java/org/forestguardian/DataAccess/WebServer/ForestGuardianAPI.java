package org.forestguardian.DataAccess.WebServer;

import org.forestguardian.DataAccess.Local.SessionData;
import org.forestguardian.DataAccess.Local.User;

import io.reactivex.Observable;

import retrofit2.adapter.rxjava2.Result;
import retrofit2.http.Body;
import retrofit2.http.POST;

/**
 * Created by emma on 19/03/17.
 */

public interface ForestGuardianAPI {

    /**
     * ENVIRONMENTS
     */
    String PRODUCTION_ENDPOINT = "http://app.forestguardian.org";
    String TEST_ENDPOINT = "http://52.40.32.3:3000";

    /**
     * Asyncronous callback, because it is needed to get the authorization headers from
     * the response. See ForestGuardianServiceTest class.
     */
    @POST("api/v1/users/sign_in.json")
    Observable<Result<SessionData>> signIn(@Body User pUser);

    @POST("api/v1/users.json")
    Observable<Result<SessionData>> signUp(@Body User pUser);

}
