package org.forestguardian.DataAccess.Location;

import android.app.SearchManager;
import android.content.ContentProvider;
import android.content.ContentValues;
import android.database.Cursor;
import android.database.MatrixCursor;
import android.location.Address;
import android.location.Location;
import android.net.Uri;
import android.provider.BaseColumns;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;

import org.forestguardian.Helpers.GeoHelper;

import java.io.IOException;
import java.util.List;

/**
 * Created by luisalonsomurillorojas on 15/7/17.
 */

public class AddressContentProvider extends ContentProvider {

    private static final String TAG = AddressContentProvider.class.getSimpleName();

    @Override
    public boolean onCreate() {
        return true;
    }

    @Nullable
    @Override
    public Cursor query(@NonNull Uri uri, @Nullable String[] projection, @Nullable String selection, @Nullable String[] selectionArgs, @Nullable String sortOrder) {
        //Get the query from the URI
        String query = uri.getLastPathSegment().toLowerCase();
        //Init the table cursor
        String [] columns = new String[] {BaseColumns._ID, SearchManager.SUGGEST_COLUMN_TEXT_1, SearchManager.SUGGEST_COLUMN_INTENT_DATA};
        MatrixCursor cursor = new MatrixCursor(columns);
        //Get the data from the geocoder
        try {
            List<Address> addressList =  GeoHelper.getPointsFromAddressName(getContext(), query);
            //Add the data to the table
            for (int index = 0; index < addressList.size(); index++) {
                Location addressPoint = new Location("");
                addressPoint.setLatitude(addressList.get(0).getLatitude());
                addressPoint.setLongitude(addressList.get(0).getLongitude());
                String[] newRow = new String[] {Integer.toString(index), addressList.get(index).getFeatureName(), GeoHelper.convertLocationToString(addressPoint)};
                cursor.addRow(newRow);
            }
        } catch (IOException e) {
            Log.e(TAG, "Error retrieving addresses: " + e.getMessage());
        }
        return cursor;
    }

    @Nullable
    @Override
    public String getType(@NonNull Uri uri) {
        return null;
    }

    @Nullable
    @Override
    public Uri insert(@NonNull Uri uri, @Nullable ContentValues values) {
        return null;
    }

    @Override
    public int delete(@NonNull Uri uri, @Nullable String selection, @Nullable String[] selectionArgs) {
        return 0;
    }

    @Override
    public int update(@NonNull Uri uri, @Nullable ContentValues values, @Nullable String selection, @Nullable String[] selectionArgs) {
        return 0;
    }
}
