package ubc.cpen391.testing.loginsignup;

import android.app.ProgressDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.*;
import android.os.AsyncTask;
import android.speech.RecognizerIntent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.KeyEvent;
import android.widget.*;
import android.view.View;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Locale;
import java.util.UUID;

public class ControllerActivity extends AppCompatActivity {

    //Initialize all variables and other objects
    Button   btnSend, btnReturn;
    TextView editText;

    private ProgressDialog progress;

    BluetoothAdapter myBluetooth = null;
    BluetoothSocket btSocket = null;

    private boolean isBtConnected = false;
    String address = null;

    //SPP UUID.
    static final UUID myUUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_controller);

        btnSend = (Button) findViewById(R.id.btnSend);
        editText = (TextView) findViewById(R.id.editText);

        btnSend.setOnClickListener(new View.OnClickListener() {

            public void onClick(View v) {
                //sendText();
                promptSpeech();
            }
        });


        //Create an intent to call the ConnectBT class
        Intent newint = getIntent();
        address = newint.getStringExtra(Bluetooth_detect.EXTRA_ADDRESS); //receive the address of the bluetooth device

        new ConnectBT().execute(); //Call the class to connect


        //Initialize the functionality of Return button that disconnect the app
        //and return to the Device List Activity
        btnReturn = (Button)findViewById(R.id.btnReturn);
        btnReturn.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                /*
                Intent i = new Intent(v.getContext(), Bluetooth_detect.class);
                Disconnect();//Disconnect the app from the DE1-Soc
                startActivity(i);//Open the DeviceList Page
                */

                Disconnect();
            }
        });

    }

    public void promptSpeech() {
        Intent i = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        i.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        i.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault());
        i.putExtra(RecognizerIntent.EXTRA_PROMPT, "Say something");

        try {
            startActivityForResult(i, 100);
        } catch(ActivityNotFoundException a) {
            Toast.makeText(ControllerActivity.this, "Your device does not support speech language", Toast.LENGTH_SHORT).show();

        }

    }

    public void onActivityResult(int request_code, int result_code, Intent i) {
        super.onActivityResult(request_code, result_code, i);
        switch(request_code) {
            case 100:
                if(result_code == RESULT_OK && i != null) {

                    ArrayList<String> result = i.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
                    if(result.get(0).equals("track") || result.get(0).equals("truck")) {
                        editText.setText("success");
                        sendText(result.get(0));

                    }
                    else {
                        editText.setText(result.get(0));
                        sendText(result.get(0));

                    }
                }
                break;
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
    //Function to send the text to DE1-Soc
    private void sendText(String s)
    {
        if (btSocket!=null)
        {
            try
            {
                msg("Sending");
                btSocket.getOutputStream().write(s.toString().getBytes());
            }
            catch (IOException e)
            {
                msg("Connection Error");
                isBtConnected = false;
                msg("Trying to reestablished connection");
                try
                {
                    btSocket.close(); //close connection
                }
                catch (IOException ei)
                { msg("CANNOT DISMISS");}
                new ConnectBT().execute();
            }
        }

    }




    //The purpose of this class is to create and maintain the bluetooth connection
    private class ConnectBT extends AsyncTask<Void, Void, Void>  // UI thread
    {
        private boolean ConnectSuccess = true;

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
                    btSocket = dispositivo.createInsecureRfcommSocketToServiceRecord(myUUID);//Create a RFCOMM (SPP) connection
                    BluetoothAdapter.getDefaultAdapter().cancelDiscovery();
                    btSocket.connect();//Start the connection
                }
            }
            catch (IOException e)
            {
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
            }
            else
            {
                msg("Connected.");
                isBtConnected = true;
            }
            progress.dismiss();
        }
    }

    //Method to disconnect the bluetooth connection
    private void Disconnect()
    {
        if (btSocket!=null) //If the btSocket is busy
        {
            try
            {
                btSocket.close(); //close connection
            }
            catch (IOException e)
            { msg("Error");}
        }

        finish();
    }


    //Method to display a Toast
    public void msg(String s)
    {
        Toast.makeText(getApplicationContext(), s, Toast.LENGTH_LONG).show();
    }

}

