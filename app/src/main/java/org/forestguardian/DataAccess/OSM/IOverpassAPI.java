package org.forestguardian.DataAccess.OSM;

import hu.supercluster.overpasser.adapter.OverpassQueryResult;

/**
 * Created by luisalonsomurillorojas on 1/4/17.
 */

public interface IOverpassAPI {
    void overpassCallback(OverpassQueryResult result);
}
