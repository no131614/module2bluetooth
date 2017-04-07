package ubc.cpen391.testing.loginsignup;

import android.Manifest;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.app.ProgressDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.provider.Settings;
import android.speech.RecognizerIntent;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.OnStreetViewPanoramaReadyCallback;
import com.google.android.gms.maps.StreetViewPanorama;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.SupportStreetViewPanoramaFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.StreetViewPanoramaCamera;
import com.google.android.gms.maps.model.StreetViewPanoramaLink;
import com.google.android.gms.maps.model.StreetViewPanoramaLocation;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.IOException;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Set;
import java.util.UUID;

public class ControllerActivity extends AppCompatActivity implements OnMapReadyCallback,
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        GoogleMap.OnInfoWindowClickListener,
        GoogleMap.OnMapLongClickListener,
        GoogleMap.OnMapClickListener,
        GoogleMap.OnMarkerClickListener,
        StreetViewPanorama.OnStreetViewPanoramaChangeListener,
        OnStreetViewPanoramaReadyCallback {

    private LatLng ubc = new LatLng(49.2620311, -123.2503899);
    private LatLng school = new LatLng(49.2646801, -123.2528899);
    private LatLng vancouver = new LatLng(49.2876743, -123.1162331);
    private Location schooloc;
    private Location city;

    private GoogleMap mMap;
    private StreetViewPanorama svp;

    private boolean resetConnection = false;
    private boolean first = true;

    private final int[] MAP_TYPES = {
            GoogleMap.MAP_TYPE_SATELLITE,
            GoogleMap.MAP_TYPE_NORMAL,
            GoogleMap.MAP_TYPE_HYBRID,
            GoogleMap.MAP_TYPE_TERRAIN,
            GoogleMap.MAP_TYPE_NONE};

    private GPSChecker locationManager;

    private GoogleApiClient mGoogleApiClient;

    private LocationManager locationManagerContext;

    private Location mLastLocation;

    private BluetoothAdapter myBluetooth = null;
    private Set<BluetoothDevice> pairedDevices;
    private String address = null;

    static final UUID myUUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");

    private ProgressDialog progress;

    BluetoothSocket btSocket = null;
    OutputStream stream = null;
    private String userid;
    private boolean isBtConnected = false;

    private DatabaseReference databaseReference;
    private StorageReference mStorage;

    private android.app.FragmentManager fm;
    private BluetoothFragment bdevice;
    private ImageFragment imageFragment;
    private Button connectButton;
    private HashMap<Marker, GPSCoor> map;

    private static final int CAMERA_REQUEST_CODE = 1;

    private static final int PAN_BY_DEG = 30;

    private static final float ZOOM_BY = 0.5f;
    private static final long DEFAULT_ANIMATION_DURATION = 1000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_controller);
        connectButton = (Button) findViewById(R.id.devices);
        map = new HashMap<>();

        databaseReference = FirebaseDatabase.getInstance().getReference("Geolocation");
        mStorage = FirebaseStorage.getInstance().getReference();

        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        fm = getFragmentManager();
        bdevice = new BluetoothFragment();
        imageFragment = new ImageFragment();
        Intent newint = getIntent();
        userid = newint.getStringExtra("user_id");


        schooloc = new Location("school");
        schooloc.setLatitude(school.latitude);
        schooloc.setLongitude(school.longitude);

        city = new Location("city");
        city.setLongitude(vancouver.longitude);
        city.setLatitude(vancouver.latitude);

        setUpBluetooth();
        locationManagerContext = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);

        if (locationManager == null) {
            locationManager = new GPSChecker(locationManagerContext);
        }

        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();


        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        SupportStreetViewPanoramaFragment streetFragment = (SupportStreetViewPanoramaFragment) getSupportFragmentManager()
                .findFragmentById(R.id.streetviewpanorama);
        streetFragment.getStreetViewPanoramaAsync(this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.popup_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(mMap != null) {
            if (item.getTitle().equals("Satellite")) {
                mMap.setMapType(MAP_TYPES[0]);
            }
            else if (item.getTitle().equals("Normal")) {
                mMap.setMapType(MAP_TYPES[1]);
            }
            else if (item.getTitle().equals("Hybrid")) {
                mMap.setMapType(MAP_TYPES[2]);
            }
            else if (item.getTitle().equals("Terrain")) {
                mMap.setMapType(MAP_TYPES[3]);
            }
            else {
                mMap.setMapType(MAP_TYPES[4]);
            }
        }
        return super.onOptionsItemSelected(item);
    }


    public void setUpBluetooth() {
        myBluetooth = BluetoothAdapter.getDefaultAdapter();

        if (myBluetooth == null) {
            //Display a message if the bluetooth is not available on the android device
            Toast.makeText(getApplicationContext(), "Bluetooth Device Not Available", Toast.LENGTH_LONG).show();
            finish();
        } else {
            if (!myBluetooth.isEnabled()) {
                //Ask to the user turn on the bluetooth on the device
                Intent turnBTon = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                startActivityForResult(turnBTon, 1);
            }
        }
    }

    public void connectBluetooth(String s) {
        address = s;
        new ConnectBT().execute();
    }

    @Override
    protected void onStart() {

        super.onStart();
        mGoogleApiClient.connect();

        if(address != null && resetConnection) {
            connectBluetooth(address);
            resetConnection = false;
        }
    }

    @Override
    protected void onStop() {

        super.onStop();
        if (mGoogleApiClient != null && mGoogleApiClient.isConnected()) {
            mGoogleApiClient.disconnect();
        }
        Disconnect();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    private boolean checkReady() {
        if (mMap == null) {
            return false;
        }
        return true;
    }

    public void findLocation(View view) {
        if (!checkReady()) {
            return;
        }

        if (mLastLocation != null) {
            initCamera(mLastLocation);
        }
        svp.setPosition(ubc);
    }

    public void logOut(View view) {
        sendText("a" + "\0\n");
        Disconnect();
        finish();
    }


    private void initListeners() {
        mMap.setOnMarkerClickListener(this);
        mMap.setOnInfoWindowClickListener(this);
    }

    /**
     * Called when the left arrow button is clicked. This causes the camera to move to the left
     */
    public void onScrollLeft(View view) {

        if(svp != null) {
            svp.animateTo(
                    new StreetViewPanoramaCamera.Builder().zoom(svp.getPanoramaCamera().zoom)
                            .tilt(svp.getPanoramaCamera().tilt)
                            .bearing(svp.getPanoramaCamera().bearing - PAN_BY_DEG).build(), getDuration());
        }
    }

    /**
     * Called when the right arrow button is clicked. This causes the camera to move to the right.
     */
    public void onScrollRight(View view) {
        if(svp != null) {
            svp.animateTo(
                    new StreetViewPanoramaCamera.Builder().zoom(svp.getPanoramaCamera().zoom)
                            .tilt(svp.getPanoramaCamera().tilt)
                            .bearing(svp.getPanoramaCamera().bearing + PAN_BY_DEG).build(), getDuration());
        }
    }

    /**
     * Called when the up arrow button is clicked. The causes the camera to move up.
     */
    public void onScrollUp(View view) {
        if(svp != null) {
            float currentTilt = svp.getPanoramaCamera().tilt;
            float newTilt = currentTilt + PAN_BY_DEG;

            newTilt = (newTilt > 90) ? 90 : newTilt;

            svp.animateTo(
                    new StreetViewPanoramaCamera.Builder().zoom(svp.getPanoramaCamera().zoom)
                            .tilt(newTilt)
                            .bearing(svp.getPanoramaCamera().bearing).build(), getDuration());
        }
    }

    /**
     * Called when the down arrow button is clicked. This causes the camera to move down.
     */
    public void onScrollDown(View view) {

        if(svp != null) {
            float currentTilt = svp.getPanoramaCamera().tilt;
            float newTilt = currentTilt - PAN_BY_DEG;

            newTilt = (newTilt < -90) ? -90 : newTilt;

            svp.animateTo(
                    new StreetViewPanoramaCamera.Builder().zoom(svp.getPanoramaCamera().zoom)
                            .tilt(newTilt)
                            .bearing(svp.getPanoramaCamera().bearing).build(), getDuration());
        }

    }

    private long getDuration() {
        return DEFAULT_ANIMATION_DURATION;
    }


    @Override
    public void onConnected(Bundle bundle) {

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            msg("Please turn on Location Permissions in Settings");
            return;
        }
        mLastLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
        if (mLastLocation != null && mMap != null && first) {
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(ubc, 16));

            if(isNetworkAvailable()) {
                String address = getAddressFromLatLng(new LatLng(mLastLocation.getLatitude(), mLastLocation.getLongitude()));
                msg(address);
            }
            first = false;
        } else {
            if(!locationManager.isLocationEnabled()){
                showAlert();
            }
        }
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        msg("Connection Failed");
    }

    @Override
    public void onInfoWindowClick(Marker marker) {
        GPSCoor coor = (GPSCoor) marker.getTag();
        if(coor.getImage()!= null) {
            FragmentTransaction ft = getFragmentManager().beginTransaction();
            Fragment prev = getFragmentManager().findFragmentByTag("dialog");
            if (prev != null) {
                ft.remove(prev);
            }
            ft.addToBackStack(null);

            // Create and show the dialog.
            ImageFragment newFragment = ImageFragment.newInstance(coor.getImage());
            newFragment.show(ft, "dialog");
        }
    }


    @Override
    public void onMapLongClick(LatLng latLng) {

    }

    @Override
    public boolean onMarkerClick(Marker marker) {
        marker.showInfoWindow();
        Location mark = new Location("marker");
        mark.setLatitude(marker.getPosition().latitude);
        mark.setLongitude(marker.getPosition().longitude);
        moveMarker(mark);
        showWindow(marker.getTitle());
        return true;
    }

    public void showWindow(String s) {


    }

    @Override
    public void onMapReady(GoogleMap googleMap) {

        mMap = googleMap;
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            mMap.setMyLocationEnabled(true);
        } else {
            Toast.makeText(getApplicationContext(), "Please turn on Location Permissions in Settings", Toast.LENGTH_SHORT).show();
        }

        ValueEventListener postListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // Get Post object and use the values to update the UI

                for(DataSnapshot ds: dataSnapshot.getChildren()) {
                    GPSCoor coor = ds.getValue(GPSCoor.class);
                    if(coor.getImage() != null || coor.getUid().equals(userid)) {
                        addMarkers(coor);
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                msg("Error connecting to database");
            }
        };
        databaseReference.addListenerForSingleValueEvent(postListener);

        databaseReference.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                GPSCoor coor = dataSnapshot.getValue(GPSCoor.class);
                if(coor.getImage() != null || coor.getUid().equals(userid)) {
                    addMarkers(coor);
                }
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                msg("Error connecting to database");
            }
        });

        initListeners();
    }
    private void addMarkers(GPSCoor coor) {
        String title = coor.getUid();
        if(coor.getTimestamp() != null) {
            Date date = new Date(coor.getTimestampLong());
            SimpleDateFormat sfd = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
            sfd.format(date);
            title = sfd.format(date);
        }
        if(coor.getImage() != null) {
            Marker marker = mMap.addMarker(new MarkerOptions()
                    .position(new LatLng(coor.getLatitude(), coor.getLongitude()))
                    .title(title)
                    .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE)));
            marker.setTag(coor);
        }
        else {
            Marker marker = mMap.addMarker(new MarkerOptions()
                    .position(new LatLng(coor.getLatitude(), coor.getLongitude()))
                    .title(title)
                    .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED)));
            marker.setTag(coor);
        }

    }
    private void initCamera(Location location) {
        CameraPosition position = CameraPosition.builder()
                .target(new LatLng(location.getLatitude(),
                        location.getLongitude()))
                .zoom(17)
                .bearing(0.0f)
                .tilt(0.0f)
                .build();

        mMap.animateCamera(CameraUpdateFactory.newCameraPosition(position), null);
        mMap.setTrafficEnabled(true);
        mMap.getUiSettings().setZoomControlsEnabled( true );

    }

    private void moveMarker(Location location) {
        CameraPosition position = CameraPosition.builder()
                .target(new LatLng(location.getLatitude(),
                        location.getLongitude()))
                .zoom(20)
                .bearing(0.0f)
                .tilt(0.0f)
                .build();

        mMap.animateCamera(CameraUpdateFactory.newCameraPosition(position), null);
        mMap.setTrafficEnabled(true);
        mMap.getUiSettings().setZoomControlsEnabled( true );

    }

    public void connectBT(View view) {
        if(!isBtConnected) {
            bdevice.show(fm, "bluetooth");
        }
        else {
            msg("Phone is already connected!");
        }
    }

    public void alertCommand(View view) {
        if(mLastLocation != null && databaseReference != null) {
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                // TODO: Consider calling
                //    ActivityCompat#requestPermissions
                // here to request the missing permissions, and then overriding
                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                //                                          int[] grantResults)
                // to handle the case where the user grants the permission. See the documentation
                // for ActivityCompat#requestPermissions for more details.
                msg("Please turn on Camera Permissions in Settings");
                return;
            }
            resetConnection = true;
            Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            startActivityForResult(intent, CAMERA_REQUEST_CODE);
        }
    }

    public void walkCommand(View view) {
        if(svp != null) {
            StreetViewPanoramaLocation location = svp.getLocation();
            StreetViewPanoramaCamera camera = svp.getPanoramaCamera();
            if (location != null && location.links != null) {
                StreetViewPanoramaLink link = findClosestLinkToBearing(location.links, camera.bearing);
                svp.setPosition(link.panoId);
            }
        }
    }

    public void disconnectCommand(View view) {
        sendText("logout" + "\0\n");
    }

    public static StreetViewPanoramaLink findClosestLinkToBearing(StreetViewPanoramaLink[] links,
                                                                  float bearing) {
        float minBearingDiff = 360;
        StreetViewPanoramaLink closestLink = links[0];
        for (StreetViewPanoramaLink link : links) {
            if (minBearingDiff > findNormalizedDifference(bearing, link.bearing)) {
                minBearingDiff = findNormalizedDifference(bearing, link.bearing);
                closestLink = link;
            }
        }
        return closestLink;
    }

    // Find the difference between angle a and b as a value between 0 and 180
    public static float findNormalizedDifference(float a, float b) {
        float diff = a - b;
        float normalizedDiff = (float) (diff - (360.0f * Math.floor(diff / 360.0f)));
        return (normalizedDiff < 180.0f) ? normalizedDiff : 360.0f - normalizedDiff;
    }

    public void showAlert() {
        final AlertDialog.Builder dialog = new AlertDialog.Builder(this);
        dialog.setTitle("Enable Location")
                .setMessage("Your Locations Settings is set to 'Off'.\n Please Enable Location to " +
                        "use this app")
                .setPositiveButton("Location Settings", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface paramDialogInterface, int paramInt) {
                        Intent myIntent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                        startActivity(myIntent);
                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface paramDialogInterface, int paramInt) {
                    }
                });
        dialog.show();
    }

    //function that checks if the Wifi is available on the phone
    public boolean isNetworkAvailable() {
        ConnectivityManager cm = (ConnectivityManager)
                getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = cm.getActiveNetworkInfo();
        // if no network is available networkInfo will be null
        // otherwise check if we are connected
        if (networkInfo != null && networkInfo.isConnected()) {
            return true;
        }
        return false;
    }

    private String getAddressFromLatLng( LatLng latLng ) {
        Geocoder geocoder = new Geocoder(this);

        String address = "";
        try {
            address = geocoder.getFromLocation( latLng.latitude, latLng.longitude, 1 ).get( 0 ).getAddressLine( 0 );
        } catch (IOException e ) {
            e.printStackTrace();
        }
        return address;
    }

    public void promptSpeech() {
        Intent i = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        i.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        i.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault());
        i.putExtra(RecognizerIntent.EXTRA_PROMPT, "Say something");

        try {
            startActivityForResult(i, 100);
        } catch (ActivityNotFoundException a) {
            Toast.makeText(ControllerActivity.this, "Your device does not support speech language", Toast.LENGTH_SHORT).show();
        }
    }

    public void onActivityResult(int request_code, int result_code, Intent i) {
        super.onActivityResult(request_code, result_code, i);
        switch(request_code) {
            case 100:
                if(result_code == RESULT_OK && i != null) {

                    ArrayList<String> result = i.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
                    sendText(result.get(0) + "\0\n");
                    if(result.get(0).equals("school") || result.get(0).equals("School")) {

                        initCamera(schooloc);
                        svp.setPosition(school);
                    }
                    else if (result.get(0).equals("Vancouver")) {
                        initCamera(city);
                        svp.setPosition(vancouver);
                    }
                    else {
                        msg(result.get(0));
                    }
                }
                break;

            case CAMERA_REQUEST_CODE:
                if(result_code == RESULT_OK) {

                    Uri uri = i.getData();
                    StorageReference filepath = mStorage.child("Security").child(uri.getLastPathSegment());
                    filepath.putFile(uri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            Uri downloadUri = taskSnapshot.getDownloadUrl();
                            GPSCoor newcoor = new GPSCoor(mLastLocation.getLatitude(), mLastLocation.getLongitude(), userid, downloadUri.toString());
                            databaseReference.push().setValue(newcoor);
                            msg("File successfully uploaded");
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            e.printStackTrace();
                            msg("Failed to Upload Image");
                        }
                    });
                }
                break;
        }
    }

    @Override
    public void onMapClick(LatLng latLng) {

    }

    @Override
    public void onStreetViewPanoramaReady(StreetViewPanorama streetViewPanorama) {
        svp = streetViewPanorama;
        svp.setPosition(ubc);
    }

    @Override
    public void onStreetViewPanoramaChange(StreetViewPanoramaLocation streetViewPanoramaLocation) {

    }

    //The purpose of this class is to create and maintain the bluetooth connection
    private class ConnectBT extends AsyncTask<Void, Void, Void>  // UI thread
    {
        private boolean ConnectSuccess = true;
        private BluetoothSocket temp = null;

        @Override
        protected void onPreExecute()
        {
            progress = ProgressDialog.show(ControllerActivity.this, "Connecting...", "Please wait!!!");  //Show a progress dialog
        }

        @Override
        protected Void doInBackground(Void... devices) //While the progress dialog is shown, the connection is done in background
        {
            try
            {
                if (btSocket == null || !isBtConnected)
                {
                    myBluetooth = BluetoothAdapter.getDefaultAdapter();//Get the mobile bluetooth device
                    BluetoothDevice dispositivo = myBluetooth.getRemoteDevice(address);//Connects to the device's address and checks if it is available
                    temp = dispositivo.createInsecureRfcommSocketToServiceRecord(myUUID);//Create a RFCOMM (SPP) connection
                    BluetoothAdapter.getDefaultAdapter().cancelDiscovery();
                    temp.connect();//Start the connection
                }
            }
            catch (IOException e)
            {
                e.printStackTrace();
                ConnectSuccess = false;//If the try failed, you can check the exception here
            }
            return null;
        }
        @Override
        protected void onPostExecute(Void result) //after the doInBackground, it checks if everything went fine
        {
            super.onPostExecute(result);

            //Sends an error message if the connection is failed
            if (!ConnectSuccess)
            {
                msg("Connection Failed. Please try again.");
                isBtConnected = false;
                address = null;

            }
            else
            {
                msg("Bluetooth Connected");
                btSocket = temp;
                try {
                    stream = temp.getOutputStream();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                isBtConnected = true;
                sendText("JamesBest" + "\0\n");
                sendText(userid + "\0\n");
            }
            progress.dismiss();
        }
    }

    //Method to disconnect the bluetooth connection
    public void Disconnect() {
        if(stream != null) {
            try {
                stream.flush();
                stream.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
            stream = null;
        }

        if (btSocket!=null) //If the btSocket is busy
        {
            try
            {
                btSocket.close(); //close connection
                isBtConnected = false;
            }
            catch (IOException e)
            {
                e.printStackTrace();
            }
            btSocket = null;
            address = null;
        }
    }

    //Method to display a Toast
    public void msg(String s)
    {
        Toast.makeText(getApplicationContext(), s, Toast.LENGTH_SHORT).show();
    }

    //Function to send the text to DE1-Soc
    private void sendText(String s)
    {
        if (btSocket!=null && stream != null)
        {
            try
            {
                msg("Sending");
                stream.write(s.getBytes());
                stream.flush();
            }
            catch (IOException e)
            {
                e.printStackTrace();

                msg("Connection Error. Please Reconnect Bluetooth");
                Disconnect();
            }
        }

    }

    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {
        int action = event.getAction();
        int keyCode = event.getKeyCode();
        switch (keyCode) {
            case KeyEvent.KEYCODE_VOLUME_UP:
                if (action == KeyEvent.ACTION_DOWN) {
                    //TODO
                    promptSpeech();
                }
                return true;
            case KeyEvent.KEYCODE_VOLUME_DOWN:
                if (action == KeyEvent.ACTION_DOWN) {
                    //TODO
                    promptSpeech();
                }
                return true;
            default:
                return super.dispatchKeyEvent(event);
        }
    }
    @Override
    public void onBackPressed() {
        Disconnect();
        super.onBackPressed();
    }

}

