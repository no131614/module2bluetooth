package ubc.cpen391.testing.loginsignup;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.kairos.Kairos;
import com.kairos.KairosListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;

import butterknife.ButterKnife;
import butterknife.InjectView;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "LoginActivity";
    private static final int REQUEST_SIGNUP = 0;
    private FirebaseAuth firebaseAuth;

    private FirebaseUser user;
    static final int REQUEST_IMAGE_CAPTURE = 1;
    static final int MY_PERMISSIONS_REQUEST_IMAGE_CAPTURE = 2;

    @InjectView(R.id.input_userId) EditText _userIdText;
    @InjectView(R.id.input_password) EditText _passwordText;
    @InjectView(R.id.btn_login) Button _loginButton;
    @InjectView(R.id.link_signup) TextView _signupLink;
    @InjectView(R.id.btn_login_facial) Button _loginButtonFacial;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        firebaseAuth = FirebaseAuth.getInstance();
        setContentView(R.layout.activity_main);

        ButterKnife.inject(this);

        _loginButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                login();
            }
        });
        _loginButtonFacial.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                loginFacial();
            }
        });
        _signupLink.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // Start the Signup activity
                Intent intent = new Intent(getApplicationContext(), SignupActivity.class);
                startActivityForResult(intent, REQUEST_SIGNUP);
            }
        });
    }

    public void login() {
        Log.d(TAG, "Login");

        if (!validate()) {
            onLoginFailed();
            return;
        }

        _loginButton.setEnabled(false);

        final ProgressDialog progressDialog = new ProgressDialog(MainActivity.this,
                R.style.AppTheme_Dark_Dialog);
        progressDialog.setIndeterminate(true);
        progressDialog.setMessage("Authenticating...");
        progressDialog.show();

        String email = _userIdText.getText().toString();
        String password = _passwordText.getText().toString();

        // TODO: Implement your own authentication logic here.
        firebaseAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(MainActivity.this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(task.isSuccessful()) {
                            progressDialog.dismiss();
                            Toast.makeText(MainActivity.this, "Login Success!", Toast.LENGTH_SHORT).show();
                            user = firebaseAuth.getCurrentUser();
                            onLoginSuccess(user.getUid());
                        }
                        else {
                            progressDialog.dismiss();
                            Toast.makeText(MainActivity.this, "Login Failed!", Toast.LENGTH_SHORT).show();
                            _loginButton.setEnabled(true);
                        }
                    }
                });


    }

    public void loginFacial() {
        displayPermission();
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_SIGNUP) {
            if (resultCode == RESULT_OK) {
                //onLoginSuccess();
            }
        }

        else if(requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            Bundle extras = data.getExtras();
            Bitmap imageBitmap = (Bitmap) extras.get("data");
            KairosListener listenerLogin = new KairosListener() {

                String status = "";
                String galleryName = "";
                String subjectId = "";
                double confidence = 0.0;

                @Override
                public void onSuccess(String response) {
                    try {

                        JSONObject jObject = new JSONObject(response);

                        System.out.println(jObject);

                        JSONArray successJsonArray = jObject.getJSONArray("images");
                        JSONObject successData = successJsonArray.getJSONObject(0);
                        JSONObject data = successData.getJSONObject("transaction");

                        status = data.getString("status");
                        galleryName = data.getString("gallery_name");
                        subjectId = data.getString("subject_id");
                        confidence = data.getDouble("confidence");

                        System.out.println("Status: " + status + " ,Gallery Name: " + galleryName + " ,Subject Id: " + galleryName + " ,Confidence: " + confidence);

                        if(status.equals("success") && confidence > 0.6) {
                            Toast.makeText(getApplicationContext(), "Image Login Complete", Toast.LENGTH_SHORT).show();
                            onLoginSuccess(subjectId);
                        }

                        else{
                            onLoginFailed();
                        }
                    }
                    catch(JSONException e){
                        e.printStackTrace();
                    }
                }

                @Override
                public void onFail(String response) {
                    Log.d("KAIROS TESTING", response);
                    Toast.makeText(getApplicationContext(), "Image Login Failed, Please Try Again...", Toast.LENGTH_LONG).show();
                }
            };

            Kairos myKairos = new Kairos();

            String app_id = "524ef96a";
            String api_key = "2b16a2a8590056f57fee7fed1060faf5";
            myKairos.setAuthentication(getApplicationContext(), app_id, api_key);

            try {

                String galleryId = "RecognitionTesting";
                myKairos.recognize(imageBitmap, galleryId, null, null, null, null, listenerLogin);

            } catch (JSONException e) {
                e.printStackTrace();
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }

        }
    }

    @Override
    public void onBackPressed() {
        // disable going back to the MainActivity
        moveTaskToBack(true);
    }

    public void onLoginSuccess(String id) {
        _loginButton.setEnabled(true);
        Intent intent = new Intent(getApplicationContext(), ControllerActivity.class);
        intent.putExtra("user_id", id);
        startActivity(intent);
    }

    public void onLoginFailed() {
        Toast.makeText(getBaseContext(), "Login failed", Toast.LENGTH_LONG).show();

        _loginButton.setEnabled(true);
    }

    public boolean validate() {
        boolean valid = true;

        String userId = _userIdText.getText().toString();
        String password = _passwordText.getText().toString();

        if (userId.isEmpty()) {
            _userIdText.setError("enter a valid email address");
            valid = false;
        } else {
            _userIdText.setError(null);
        }

        if (password.isEmpty() || password.length() < 4 || password.length() > 10) {
            _passwordText.setError("between 4 and 10 alphanumeric characters");
            valid = false;
        } else {
            _passwordText.setError(null);
        }

        return valid;
    }

    private void dispatchTakePictureIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
        }
    }


    private void displayPermission(){
        // Here, thisActivity is the current activity
        if (ContextCompat.checkSelfPermission(this,
                android.Manifest.permission.CAMERA)
                != PackageManager.PERMISSION_GRANTED) {

            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    android.Manifest.permission.CAMERA)) {

                // Show an explanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.

            } else {

                // No explanation needed, we can request the permission.

                ActivityCompat.requestPermissions(this,
                        new String[]{android.Manifest.permission.CAMERA},
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
                    System.out.println("Permission rejected!");
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


}