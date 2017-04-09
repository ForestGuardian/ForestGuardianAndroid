package org.forestguardian.Helpers;

/**
 * Created by emma on 08/04/17.
 */

public class UserValidations {

    public static Boolean validatePassword(String pass, String confirmationPass){
        return pass.compareTo(confirmationPass) == 0;
    }
}
