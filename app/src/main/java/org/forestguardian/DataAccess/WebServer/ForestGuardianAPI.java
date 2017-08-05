package org.forestguardian.DataAccess.WebServer;

import org.forestguardian.DataAccess.Local.Report;
import org.forestguardian.DataAccess.Local.SessionData;
import org.forestguardian.DataAccess.Local.User;

import java.io.File;
import java.util.List;

import io.reactivex.Observable;

import retrofit2.adapter.rxjava2.Result;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;

/**
 * Created by emma on 19/03/17.
 */

public interface ForestGuardianAPI {

    /**
     * ENVIRONMENTS
     */
    String PRODUCTION_ENDPOINT = "http://app.forestguardian.org";
    String TEST_SERVER_ENDPOINT = "http://52.40.32.3:3000";

    /* Development environments assume you have a clone of the backend running in your network.
    * you can start DEVELOPMENT_DOCKER_ENDPOINT by cloning the ForestGuardianBackend repo and
    * running docker-compose up or docker-compose start commands.
    * */
    String DEVELOPMENT_DOCKER_ENDPOINT = "http://192.168.99.100:3000";
    String DEVELOPMENT_EMMA_ENDPOINT = "http://192.168.1.104:3000";
    String DEVELOPMENT_LUIS_ENDPOINT = "http://your-lan-ip-here:3000";

    /* For normal runs and automated tests. */
    String FOREST_GUARDIAN_WEB_SERVICE_ENDPOINT = PRODUCTION_ENDPOINT;

    /**
     * Observable<Result<SessionData>> is a async callback,
     * because it is needed to get the authorization headers from
     * the response. See ForestGuardianServiceTest class. Other apis
     * can return the created object directly.
     * Please read a lot about Retrofit and RxJava.
     */

    /**
     * Login.
     * @param pUser
     * @return
     */
    @POST("api/v1/users/sign_in.json")
    Observable<Result<SessionData>> signIn(@Body User pUser);

    /**
     * Register a new account.
     * @param pUser
     * @return
     */
    @POST("api/v1/users.json")
    Observable<Result<SessionData>> signUp(@Body User pUser);

    /**
     * Updates an user information.
     * @param pUser
     * @return
     */
    @PUT("api/v1/users.json")
    Observable<Result<SessionData>> updateAccount(@Body User pUser);

    @GET("users/{id}.json")
    Observable<User> showUser(@Path("id") long pUserId);

    /**
     * Creates a new report.
     * @param pReport
     * @return
     */
    @POST("reports.json")
    Observable<Report> createReport(@Body Report pReport);

    /**
     * Lists existing reports.
     */
    @GET("reports.json")
    Observable<List<Report>> listReports();
}
