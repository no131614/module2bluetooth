package ubc.cpen391.testing.loginsignup;

/**
 * Created by james on 2017-04-01.
 */

import android.app.DialogFragment;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Set;

public class BluetoothFragment extends DialogFragment{
    Button btn;
    ListView listView;
    private BluetoothAdapter myBluetooth = null;
    private Set<BluetoothDevice> pairedDevices;
    String address = null;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.paired_devices, null);
        getDialog().setTitle("Paired Devices");
        listView = (ListView) rootView.findViewById(R.id.listDevices);
        btn = (Button) rootView.findViewById(R.id.button);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });

        myBluetooth = BluetoothAdapter.getDefaultAdapter();

        if (myBluetooth == null) {
            //Display a message if the bluetooth is not available on the android device
            Toast.makeText(getActivity().getApplicationContext(), "Bluetooth Device Not Available", Toast.LENGTH_LONG).show();

            //close the app

        } else if (!myBluetooth.isEnabled()) {
            //Ask to the user turn on the bluetooth on the device
            Intent turnBTon = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(turnBTon, 1);
        }

        pairedDevices = myBluetooth.getBondedDevices();
        ArrayList list = new ArrayList();
        if (pairedDevices.size() > 0) {
            for(BluetoothDevice bt : pairedDevices)
            {
                list.add(bt.getName() + "\n" + bt.getAddress()); //Get the device's name and the address
            }
        }

        final ArrayAdapter adapter = new ArrayAdapter(getActivity(), android.R.layout.simple_list_item_1, list);
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(myListClickListener); //Method called when the device from the list is clicked

        return rootView;
    }

    private AdapterView.OnItemClickListener myListClickListener = new AdapterView.OnItemClickListener()
    {
        public void onItemClick (AdapterView<?> av, View v, int arg2, long arg3)
        {
            String info = ((TextView) v).getText().toString();
            address = info.substring(info.length() - 17);

            //.Disconnect();
            ((ControllerActivity)getActivity()).connectBluetooth(address);

            Toast.makeText(getActivity(), address, Toast.LENGTH_SHORT).show();
            dismiss();
        }
    };
}
