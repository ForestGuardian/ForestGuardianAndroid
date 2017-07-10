package org.forestguardian.DataAccess.WebServer;

import android.content.Context;
import android.util.Log;

import com.google.gson.GsonBuilder;

import org.forestguardian.DataAccess.Local.AuthData;
import org.forestguardian.DataAccess.Local.User;
import org.forestguardian.R;

import java.util.HashMap;

import io.realm.Realm;
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

    private okhttp3.OkHttpClient.Builder mHttpBuilder;

    private ForestGuardianService() {

        mHttpBuilder = new okhttp3.OkHttpClient.Builder();

        mClient = mHttpBuilder.build();

        mRetrofit = new retrofit2.Retrofit.Builder()
                .baseUrl(FOREST_GUARDIAN_WEB_SERVICE_ENDPOINT)
                .addConverterFactory(GsonConverterFactory.create(new GsonBuilder().setLenient().create()))
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .client(mClient)
                .build();


        mService = mRetrofit.create(ForestGuardianAPI.class);
    }

    public void addApiAuthorizationHeader(){

        mHttpBuilder.addInterceptor(chain -> {
            Request original = chain.request();

            // Request customization: add request headers
            Request.Builder requestBuilder = original.newBuilder()
                    .addHeader("Authorization", "auth-value");

            Request request = requestBuilder.build();
            return chain.proceed(request);
        });
    }

    public void addAuthenticationHeaders( Context pContext, String pUID ){


        mHttpBuilder.addInterceptor(chain -> {
            Request original = chain.request();

            Realm realm = Realm.getDefaultInstance();
            AuthData authData = realm.where(AuthData.class).equalTo("uid",pUID).findFirst();

            // Request customization: add request headers
            Request.Builder requestBuilder = original.newBuilder();
            requestBuilder.removeHeader("uid")
                    .removeHeader("access-token")
                    .removeHeader("client")
                    .removeHeader("expiry")
                    .removeHeader("token-type");
            requestBuilder.addHeader(pContext.getString(R.string.header_auth_uid),
                            authData.getUid() )
                    .addHeader(pContext.getString(R.string.header_auth_access_token),
                            authData.getAccessToken() )
                    .addHeader(pContext.getString(R.string.header_auth_client),
                            authData.getClient() )
                    .addHeader(pContext.getString(R.string.header_auth_expiry),
                            authData.getExpiry() )
                    .addHeader(pContext.getString(R.string.header_auth_token_type),
                            authData.getTokenType() );

            Request request = requestBuilder.build();

            Log.e("AuthenticationHeaders", request.toString() );
            return chain.proceed(request);
        });

        mClient = mHttpBuilder.build();

        mRetrofit = new retrofit2.Retrofit.Builder()
                .baseUrl(FOREST_GUARDIAN_WEB_SERVICE_ENDPOINT)
                .addConverterFactory(GsonConverterFactory.create(new GsonBuilder().setLenient().create()))
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .client(mClient)
                .build();


        mService = mRetrofit.create(ForestGuardianAPI.class);
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
