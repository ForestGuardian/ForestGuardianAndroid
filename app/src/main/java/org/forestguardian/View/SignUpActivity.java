package org.forestguardian.View;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;
import io.realm.Realm;
import okhttp3.Headers;
import retrofit2.adapter.rxjava2.Result;

import org.forestguardian.DataAccess.Local.SessionData;
import org.forestguardian.DataAccess.Local.User;
import org.forestguardian.DataAccess.WebServer.ForestGuardianService;
import org.forestguardian.ForestGuardianApplication;
import org.forestguardian.Helpers.UserValidations;
import org.forestguardian.R;

import static junit.framework.Assert.assertEquals;

/**
 * Created by emma on 08/04/17.
 */

public class SignUpActivity  extends AppCompatActivity {

    @BindView(R.id.signup_btn) Button mSignUp;
    @BindView(R.id.signup_user) EditText mUsername;
    @BindView(R.id.signup_email) EditText mEmail;
    @BindView(R.id.signup_pass) EditText mPassword;
    @BindView(R.id.signup_pass_confirmation) EditText mPasswordConfirmation;

    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);
        ButterKnife.bind(this);

        mSignUp.setOnClickListener(pView -> {

            // Get data from form.
            String email = mEmail.getText().toString();
            String username = mUsername.getText().toString();
            String pass = mPassword.getText().toString();
            String confirmation = mPasswordConfirmation.getText().toString();

            /* Check Local validations */

            if (TextUtils.isEmpty(email)  || TextUtils.isEmpty(username) || TextUtils.isEmpty(pass)) {
                String error = "Please fill all fields.";
                Log.d(getLocalClassName(),error);
                Toast.makeText(this, error, Toast.LENGTH_LONG).show();
                return;
            }

            if (!UserValidations.isEmailValid(email)) {
                String error = "Email is invalid.";
                Log.d(getLocalClassName(),error);
                mEmail.setError(error);
                mEmail.requestFocus();
                return;
            }

            if (!UserValidations.isPasswordValid(pass)) {
                String error = "Password should have at least 8 characters.";
                Log.d(getLocalClassName(),error);
                mPassword.setError(error);
                mPassword.requestFocus();
                return;
            }

            if (!pass.equals(confirmation)) {
                String error = "Password and confirmation should be equal.";
                Log.d(getLocalClassName(),error);
                mPasswordConfirmation.setError(error);
                mPasswordConfirmation.requestFocus();
                return;
            }

            // Create Model
            User user = new User();
            user.setEmail(email);
            user.setUsername(username);
            user.setPassword(pass);
            user.setPasswordConfirmation(confirmation);

            // Send SignUp Request
            Observable<Result<SessionData>> sessionService = ForestGuardianService.global().service().signUp(user);
            sessionService.subscribeOn(Schedulers.newThread())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(pSessionDataResult -> {

                        /* Check web service response validations. */

                        if ( pSessionDataResult.isError() ){
                            /* TODO: Handle authentication error case. */
                            Log.e( getLocalClassName(), pSessionDataResult.error().getMessage() );
                            Toast.makeText(this, "Problems with online service..." , Toast.LENGTH_LONG ).show();
                            return;
                        }

                        if ( !pSessionDataResult.response().isSuccessful() ){
                            /* Check for error messages are ready for user viewing. */
                            Log.e( getLocalClassName(), "Problem processing request." );
                            Toast.makeText(this, "Problem processing request.", Toast.LENGTH_LONG ).show();
                            return;
                        }

                        // Save authentication headers for future requests.
                        Headers authHeaders = pSessionDataResult.response().headers();
                        String accessToken = authHeaders.get("Access-Token");
                        User authenticatedUser = pSessionDataResult.response().body().getUser();
                        authenticatedUser.setToken(accessToken);

                        ((ForestGuardianApplication)getApplication()).setCurrentUser(authenticatedUser);

                        // Uncomment addApiAuthorizationHeader() when ApiAuthorization feature is enabled from backend.
                        // ForestGuardianService.global().addApiAuthorizationHeader();

                        Toast.makeText(this, "Welcome!", Toast.LENGTH_SHORT).show();

                        // Load MapActivity.
                        Intent intent = new Intent(getApplicationContext(),MapActivity.class);
                        startActivity(intent);
                        finish();
                    });

        });
    }
}
