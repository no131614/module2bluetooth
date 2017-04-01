package ubc.cpen391.testing.loginsignup;

import com.google.firebase.database.ServerValue;

import java.security.Timestamp;
import java.sql.Time;
import java.util.HashMap;

/**
 * Created by james on 2017-03-31.
 */

public class GPSCoor {

    private Double latitude;
    private Double longitude;
    private long timestamp;
    private String id;

    public GPSCoor() {

    }

    public GPSCoor(Double latitude, Double longitude, long stamp, String id) {
        this.latitude = latitude;
        this.longitude = longitude;
        this.timestamp = stamp;
        this.id = id;
    }


    public Double getLatitude() {
        return this.latitude;
    }

    public Double getLongitude() {
        return this.longitude;
    }

    public String getUid() {
        return this.id;
    }

}
