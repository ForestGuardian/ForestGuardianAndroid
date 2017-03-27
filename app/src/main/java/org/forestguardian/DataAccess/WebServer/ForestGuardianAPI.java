package org.forestguardian.DataAccess.WebServer;

import org.forestguardian.DataAccess.Local.Session;
import org.forestguardian.DataAccess.Local.User;

import io.reactivex.Observable;

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

    @POST("users/sign_in.json")
    Observable<User> signIn(@Body Session pUser);

}
