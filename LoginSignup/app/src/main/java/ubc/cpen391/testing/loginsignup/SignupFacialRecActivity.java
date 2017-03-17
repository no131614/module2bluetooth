package ubc.cpen391.testing.loginsignup;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.Toast;

import com.kairos.*;

import org.json.JSONException;

import java.io.UnsupportedEncodingException;

/**
 * Created by tony1 on 2017-03-16.
 */

public class SignupFacialRecActivity extends Activity {

    boolean isEnabled = false;
    static final int REQUEST_IMAGE_CAPTURE = 1;

    @Override
    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_signup_facial_rec);

        final Switch enable = (Switch) findViewById(R.id.enableFacialRecSwitch);
        Button continueButton = (Button) findViewById(R.id.btn_continue);

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
                    dispatchTakePictureIntent();
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
                    Toast.makeText(getApplicationContext(), "Image Registration Complete", Toast.LENGTH_SHORT).show();
                    finish();
                }

                @Override
                public void onFail(String response) {
                    Toast.makeText(getApplicationContext(), "Image Registration Failed", Toast.LENGTH_SHORT).show();
                    finish();
                }
            };


            Kairos myKairos = new Kairos();

            String app_id = "524ef96a";
            String api_key = "2b16a2a8590056f57fee7fed1060faf5";
            myKairos.setAuthentication(this, app_id, api_key);

            try {
                //String image = "http://historythings.com/wp-content/uploads/2016/06/SteveJobsBook.jpg";
                String subjectId = "Alex"; //TODO: input userId from Firebase registration
                String galleryId = "RecognitionTesting";
                myKairos.enroll(imageBitmap, subjectId, galleryId, null, null, null, listener);

            } catch (JSONException e) {
                e.printStackTrace();
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
        }
    }
}
