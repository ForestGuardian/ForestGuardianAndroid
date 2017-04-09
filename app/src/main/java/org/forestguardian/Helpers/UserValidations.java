package org.forestguardian.Helpers;

/**
 * Created by emma on 08/04/17.
 */

public class UserValidations {


    public static boolean isEmailValid(String email) {
        //TODO: Replace this with your own logic
        return email.contains("@");
    }

    public static boolean isPasswordValid(String password) {
        //TODO: Replace this with your own logic
        return password.length() > 4;
    }
}
