package org.forestguardian.View;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
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

import org.forestguardian.DataAccess.Local.AuthData;
import org.forestguardian.DataAccess.Local.SessionData;
import org.forestguardian.DataAccess.Local.User;
import org.forestguardian.DataAccess.WebServer.ForestGuardianService;
import org.forestguardian.ForestGuardianApplication;
import org.forestguardian.Helpers.AuthenticationController;
import org.forestguardian.Helpers.HeadersHelper;
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
    @BindView(R.id.signup_name) EditText mName;

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
            String name = mName.getText().toString();

            /* Check Local validations */

            // TODO: Add username validation
            if (TextUtils.isEmpty(email) || TextUtils.isEmpty(pass)) {
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

            if (TextUtils.isEmpty(name)) {
                String error = "Please introduce a name.";
                Log.d(getLocalClassName(),error);
                mName.setError(error);
                mName.requestFocus();
                return;
            }

            // Create Model
            User user = new User();
            user.setEmail(email);
            user.setUsername(username);
            user.setPassword(pass);
            user.setPasswordConfirmation(confirmation);
            user.setName(name);

            // Send SignUp Request
            Observable<Result<SessionData>> sessionService = ForestGuardianService.global().service().signUp(user);
            sessionService.subscribeOn(Schedulers.newThread())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(this::onSignUpResult);
        });
    }

    /**
     * Handles the result for the sign_up request.
     * @param pSessionDataResult
     */
    private void onSignUpResult( Result<SessionData>  pSessionDataResult){
     /* Check web service response validations. */

            if ( !pSessionDataResult.response().isSuccessful() ){
                                /* TODO: Handle authentication error case. */
                Log.e( getLocalClassName(), pSessionDataResult.response().message() );

                if ( pSessionDataResult.response().code() == 422 ){
                    Toast.makeText(this, R.string.already_registered_account , Toast.LENGTH_LONG ).show();
                } else {
                    Toast.makeText(this, R.string.server_generic_error , Toast.LENGTH_LONG ).show();
                }
                return;
            }

            // Save authentication headers for future requests.
            Headers authHeaders = pSessionDataResult.response().headers();
            AuthData authData = HeadersHelper.parseHeaders(this, authHeaders );
            if ( authData == null ){
                            /* Check for error messages are ready for user viewing. */
                Log.e( getLocalClassName(), "Auth headers are invalid." );
                return;
            }

            User authenticatedUser = pSessionDataResult.response().body().getUser();
            authenticatedUser.setAuth( authData );
            AuthenticationController.shared().setCurrentUser(authenticatedUser);

            // Uncomment addApiAuthorizationHeader() when ApiAuthorization feature is enabled from backend.
            // ForestGuardianService.global().addApiAuthorizationHeader();

            Toast.makeText(this, "Bienvenido!", Toast.LENGTH_SHORT).show();

            // Load MapActivity.
            Intent intent = new Intent(getApplicationContext(),MapActivity.class);
            startActivity(intent);
            finish();

    }
}
