package ubc.cpen391.testing.loginsignup;

import com.google.firebase.database.Exclude;
import com.google.firebase.database.ServerValue;


/**
 * Created by james on 2017-03-31.
 */

public class GPSCoor {

    private Double latitude;
    private Double longitude;
    private Object timestamp;
    private String uid;
    private String image;

    public GPSCoor() {

    }

    public GPSCoor(Double latitude, Double longitude, String id, String image) {
        this.latitude = latitude;
        this.longitude = longitude;
        timestamp = ServerValue.TIMESTAMP;
        this.uid = id;
        this.image = image;
    }


    public Double getLatitude() {
        return this.latitude;
    }

    public Double getLongitude() {
        return this.longitude;
    }

    public Object getTimestamp(){
        return this.timestamp;
    }

    @Exclude
    public long getTimestampLong(){
        return (long) this.timestamp;
    }

    public String getUid() {
        return this.uid;
    }

    public String getImage() {
        return this.image;
    }

}
