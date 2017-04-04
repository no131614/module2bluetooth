package ubc.cpen391.testing.loginsignup;

import com.google.firebase.database.Exclude;
import com.google.firebase.database.ServerValue;


/**
 * Created by james on 2017-03-31.
 */

public class GPSCoor {

    private Double latitude;
    private Double longitude;
    private Object timestampCreated;
    private String uid;
    private String image;

    public GPSCoor() {

    }

    public GPSCoor(Double latitude, Double longitude, String id, String image) {
        this.latitude = latitude;
        this.longitude = longitude;
        timestampCreated = ServerValue.TIMESTAMP;
        this.uid = id;
        this.image = image;
    }

    /*
    public HashMap<String, Object> getTimestampCreated(){
        return timestampCreated;
    }
*/



    public Double getLatitude() {
        return this.latitude;
    }

    public Double getLongitude() {
        return this.longitude;
    }
/*
    public HashMap<String, Object> getTimestampCreated(){
        return timestampCreated;
    }

    @Exclude
    public long getTimestampCreatedLong(){
        return (long) timestampCreated.get("timestamp");
    }
*/
    public Object getTimestampCreated(){
        return this.timestampCreated;
    }

    @Exclude
    public long getTimestampCreatedLong(){
        return (long) this.timestampCreated;
    }

    public String getUid() {
        return this.uid;
    }

    public String getImage() {
        return this.image;
    }

}
