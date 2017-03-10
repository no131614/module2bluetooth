package first.alexander.com.myapplication;

import android.app.ProgressDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.*;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.*;
import android.view.View;
import java.io.IOException;
import java.util.UUID;



public class ControllerActivity extends AppCompatActivity {

    //Initialize all variables and other objects
    Button   btnSend, btnReturn;
    EditText editText;

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
        editText = (EditText) findViewById(R.id.editText);

        btnSend.setOnClickListener(new View.OnClickListener() {

            public void onClick(View v) {
                sendText();
            }
        });


        //Create an intent to call the ConnectBT class
        Intent newint = getIntent();
        address = newint.getStringExtra(DeviceListActivity.EXTRA_ADDRESS); //receive the address of the bluetooth device

        new ConnectBT().execute(); //Call the class to connect


        //Initialize the functionality of Return button that disconnect the app
        //and return to the Device List Activity
        btnReturn = (Button)findViewById(R.id.btnReturn);
        btnReturn.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                Intent i = new Intent(v.getContext(), DeviceListActivity.class);
                Disconnect();//Disconnect the app from the DE1-Soc
                startActivity(i);//Open the DeviceList Page
            }
        });

    }


    //Function to send the text to DE1-Soc
    private void sendText()
    {
        if (btSocket!=null)
        {
            try
            {
                msg("Sending");
                btSocket.getOutputStream().write(editText.getText().toString().getBytes());
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
