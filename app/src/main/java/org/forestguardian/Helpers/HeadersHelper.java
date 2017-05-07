package org.forestguardian.Helpers;

import android.content.Context;

import org.forestguardian.DataAccess.Local.AuthData;
import org.forestguardian.R;

import okhttp3.Headers;


/**
 * Created by emma on 06/05/17.
 */

public class HeadersHelper {

    public static AuthData parseHeaders(Context pContext, Headers pHeaders ){
        String pUid = pHeaders.get(pContext.getString(R.string.header_auth_uid));
        String pAccessToken = pHeaders.get(pContext.getString(R.string.header_auth_access_token));
        String pAuthClient = pHeaders.get(pContext.getString(R.string.header_auth_client));
        String pTokenType = pHeaders.get(pContext.getString(R.string.header_auth_token_type));
        String pAuthExpiry = pHeaders.get(pContext.getString(R.string.header_auth_expiry));

        // Validates data exists.
        if ( pUid == null || pAccessToken == null || pAuthClient == null || pTokenType == null || pAuthExpiry == null ){
            return null;
        }

        AuthData data = new AuthData();
        data.setUid(pUid);
        data.setAccessToken(pAccessToken);
        data.setClient(pAuthClient);
        data.setTokenType(pTokenType);
        data.setExpiry(pAuthExpiry);
        return data;
    }
}
