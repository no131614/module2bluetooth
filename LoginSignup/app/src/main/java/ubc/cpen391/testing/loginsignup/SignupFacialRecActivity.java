package ubc.cpen391.testing.loginsignup;

import android.Manifest;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.Toast;

import com.kairos.*;

import org.json.JSONException;

import java.io.UnsupportedEncodingException;

public class SignupFacialRecActivity extends Activity {

    boolean isEnabled = false;
    static final int REQUEST_IMAGE_CAPTURE = 1;
    static final int MY_PERMISSIONS_REQUEST_IMAGE_CAPTURE = 2;
    static ProgressDialog progressDialog;

    @Override
    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_signup_facial_rec);

        final Switch enable = (Switch) findViewById(R.id.enableFacialRecSwitch);
        Button continueButton = (Button) findViewById(R.id.btn_continue);

        progressDialog = new ProgressDialog(getApplicationContext(),
                R.style.AppTheme_Dark_Dialog);

        enable.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean bChecked) {
                if (bChecked) {
                    isEnabled = true;
                } else {
                    isEnabled = false;
                }
            }
        });


        continueButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                if (isEnabled) {
                    displayPermission();
                    //TODO: Get permission
                } else {
                    finish();
                }
            }
        });
    }

    private void dispatchTakePictureIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            Bundle extras = data.getExtras();
            Bitmap imageBitmap = (Bitmap) extras.get("data");
            KairosListener listener = new KairosListener() {

                @Override
                public void onSuccess(String response) {
                    Log.d("KAIROS TESTING", response);
                    progressDialog.dismiss();
                    Toast.makeText(getApplicationContext(), "Image Registration Complete", Toast.LENGTH_SHORT).show();
                    finish();
                }

                @Override
                public void onFail(String response) {
                    Log.d("KAIROS TESTING", response);
                    progressDialog.dismiss();
                    Toast.makeText(getApplicationContext(), "Image Registration Failed, Please Try Again...", Toast.LENGTH_LONG).show();

                }
            };

            Kairos myKairos = new Kairos();

            String app_id = "524ef96a";
            String api_key = "2b16a2a8590056f57fee7fed1060faf5";
            myKairos.setAuthentication(getApplicationContext(), app_id, api_key);

            try {
                String subjectId = "TestImage"; //TODO: input userId from Firebase registration
                String galleryId = "RecognitionTesting";
                myKairos.enroll(imageBitmap, subjectId, galleryId, null, null, null, listener);

            } catch (JSONException e) {
                e.printStackTrace();
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }

        }
    }

    private void displayPermission(){
        // Here, thisActivity is the current activity
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.CAMERA)
                != PackageManager.PERMISSION_GRANTED) {

            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.CAMERA)) {

                // Show an explanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.

            } else {

                // No explanation needed, we can request the permission.

                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.CAMERA},
                        MY_PERMISSIONS_REQUEST_IMAGE_CAPTURE);

                // MY_PERMISSIONS_REQUEST_READ_CONTACTS is an
                // app-defined int constant. The callback method gets the
                // result of the request.
            }
        }
        else{
            dispatchTakePictureIntent();
        }
    }
    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_IMAGE_CAPTURE: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    dispatchTakePictureIntent();
                    // permission was granted, yay! Do the
                    // contacts-related task you need to do.

                } else {
                    Toast.makeText(getApplicationContext(), "Camera Permission Revoked", Toast.LENGTH_SHORT).show();
                    System.out.println("Permission rejected? WTF");
                    finish();
                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                }
                return;
            }

            // other 'case' lines to check for other
            // permissions this app might request
        }
    }

<<<<<<< Updated upstream
=======



>>>>>>> Stashed changes
}
