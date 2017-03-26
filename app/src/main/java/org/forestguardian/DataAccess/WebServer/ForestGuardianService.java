package org.forestguardian.DataAccess.WebServer;

import org.forestguardian.DataAccess.Local.Session;

import java.util.List;

import io.reactivex.Observable;

import retrofit2.http.POST;

/**
 * Created by emma on 19/03/17.
 */

public interface ForestGuardianService {

    String SERVICE_ENDPOINT = "http://app.forestguardian.org";

    @POST("users/sign_in")
    Observable<List<Session>> signIn();

}
