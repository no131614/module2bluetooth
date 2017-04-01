package ubc.cpen391.testing.loginsignup;

/**
 * Created by james on 2017-03-31.
 */

import android.location.LocationManager;

/**
 * Created by james on 2017-03-29.
 */

public class GPSChecker {

    public LocationManager locationManager;

    public GPSChecker(LocationManager locationManager){
        this.locationManager = locationManager;
    }


    public boolean isLocationEnabled() {
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) ||
                locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
    }
}