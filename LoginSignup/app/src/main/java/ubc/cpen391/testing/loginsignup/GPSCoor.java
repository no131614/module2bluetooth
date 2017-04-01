package ubc.cpen391.testing.loginsignup;

import java.security.Timestamp;

/**
 * Created by james on 2017-03-31.
 */

public class GPSCoor {

    private Double latitude;
    private Double longitude;
    private Timestamp stamp;
    private String id;

    public GPSCoor() {

    }

    public GPSCoor(Double latitude, Double longitude, Timestamp stamp, String id) {
        this.latitude = latitude;
        this.longitude = longitude;
        this.stamp = stamp;
        this.id = id;
    }


    public Double getLatitude() {
        return this.latitude;
    }

    public Double getLongitude() {
        return this.longitude;
    }

}
