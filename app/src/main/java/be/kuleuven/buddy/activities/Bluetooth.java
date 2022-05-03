package be.kuleuven.buddy.activities;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothManager;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import be.kuleuven.buddy.R;

public class Bluetooth extends AppCompatActivity {

    private static final int REQUEST_ENABLE_BT = 0;
    private static final int REQUEST_DISCOVER_BT = 1;
    TextView pairedDevices, status;
    Button turnOn, turnOff, discoverable, showDevices;

    BluetoothAdapter BtAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bluetooth);

        pairedDevices = findViewById(R.id.BT_devices);
        status = findViewById(R.id.BT_status);
        turnOn = findViewById(R.id.BT_turn_on);
        turnOff = findViewById(R.id.BT_turn_off);
        discoverable = findViewById(R.id.BT_discoverable);
        showDevices = findViewById(R.id.BT_paired_devices);

        //create context for the bt manager
        BluetoothManager bluetoothManager = getSystemService(BluetoothManager.class);
        BluetoothAdapter bluetoothAdapter = bluetoothManager.getAdapter();
        if (bluetoothAdapter == null) {
            // Device doesn't support Bluetooth
            showToast("Your device does not support Bluetooth!");
        }


    }

    private void showToast(String msg){
        Toast toast = Toast.makeText(this, msg, Toast.LENGTH_SHORT);
        toast.show();

    }

}