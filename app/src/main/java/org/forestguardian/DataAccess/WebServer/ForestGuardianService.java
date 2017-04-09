package org.forestguardian.DataAccess.WebServer;

import com.google.gson.GsonBuilder;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

import static org.forestguardian.DataAccess.WebServer.ForestGuardianAPI.FOREST_GUARDIAN_WEB_SERVICE_ENDPOINT;

/**
 * Created by emma on 08/04/17.
 */

public class ForestGuardianService {

    private Retrofit mRetrofit;

    private ForestGuardianAPI mService;

    private OkHttpClient mClient;

    private static ForestGuardianService mInstance;

    private okhttp3.OkHttpClient.Builder httpBuilder;

    private ForestGuardianService() {

        httpBuilder = new okhttp3.OkHttpClient.Builder();

        mClient = httpBuilder.build();

        mRetrofit = new retrofit2.Retrofit.Builder()
                .baseUrl(FOREST_GUARDIAN_WEB_SERVICE_ENDPOINT)
                .addConverterFactory(GsonConverterFactory.create(new GsonBuilder().setLenient().create()))
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .client(mClient)
                .build();


        mService = mRetrofit.create(ForestGuardianAPI.class);
    }

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


    public static ForestGuardianService global(){
        if (mInstance == null){
            mInstance = new ForestGuardianService();
        }
        return mInstance;
    }

    public ForestGuardianAPI service() {
        return mService;
    }
}
