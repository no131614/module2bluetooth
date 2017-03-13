package first.alexander.com.myapplication;


import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.telecom.VideoProfile;




public class MainActivity extends AppCompatActivity {

    VideoProfile camCapture;
    Button btnStart, btnCamera;
    ImageView mImageView;
    static final int REQUEST_IMAGE_CAPTURE = 1;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);




        //Button to go to the DeviceList activity/start the app
        btnStart = (Button)findViewById(R.id.btnStart);
        btnStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(v.getContext(), DeviceListActivity.class);
                startActivity(i);//Open the DeviceList Page
            }
        });

        //Image view result of camera (the thumbnail)
        mImageView = (ImageView)findViewById(R.id.imgThumbnail);


        //Button to go to use the camera service (runs in background)
        btnCamera = (Button)findViewById(R.id.btnCamera);
        btnCamera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //dispatchTakePictureIntent();

                Intent front_translucent = new Intent(getApplication().getApplicationContext(), CameraService.class);
                front_translucent.putExtra("Front_Request", true);
               // front_translucent.putExtra("Quality_Mode", camCapture.getQuality());
                getApplication().getApplicationContext().startService(front_translucent);
                //startService(front_translucent);

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
            mImageView.setImageBitmap(imageBitmap);
        }
    }


}


