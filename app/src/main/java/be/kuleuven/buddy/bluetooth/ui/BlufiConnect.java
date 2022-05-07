package be.kuleuven.buddy.bluetooth.ui;


import android.annotation.SuppressLint;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCallback;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattDescriptor;
import android.bluetooth.BluetoothGattService;
import android.bluetooth.BluetoothProfile;
import android.content.Intent;
import android.graphics.Color;
import android.media.Image;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.core.content.res.ResourcesCompat;
import androidx.recyclerview.widget.RecyclerView;

import be.kuleuven.buddy.R;
import be.kuleuven.buddy.bluetooth.app.BaseActivity;
import be.kuleuven.buddy.bluetooth.app.BlufiLog;
import be.kuleuven.buddy.bluetooth.constants.BlufiConstants;

import java.util.LinkedList;
import java.util.List;
import java.util.Locale;

import be.kuleuven.buddy.other.InfoFragment;
import blufi.espressif.BlufiCallback;
import blufi.espressif.BlufiClient;
import blufi.espressif.params.BlufiConfigureParams;
import blufi.espressif.params.BlufiParameter;
import blufi.espressif.response.BlufiScanResult;
import blufi.espressif.response.BlufiStatusResponse;
import blufi.espressif.response.BlufiVersionResponse;

@SuppressLint("MissingPermission")
public class BlufiConnect extends BaseActivity {
    private static final int REQUEST_CONFIGURE = 0x20;

    private final BlufiLog mLog = new BlufiLog(getClass());

    private BluetoothDevice mDevice;
    private BlufiClient mBlufiClient;
    private volatile boolean mConnected;

    private RecyclerView mMsgRecyclerView;
    private List<Message> mMsgList;
    private MsgAdapter mMsgAdapter;

    private Button mBlufiConnectBtn;
    private Button mBlufiDisconnectBtn;
    private Button mBlufiConfigureBtn;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.blufi_activity);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        setHomeAsUpEnable(true);

        ImageView infoBtn = findViewById(R.id.infoIconBlufiConnect);
        infoBtn.setOnClickListener(view -> {
            String title = getResources().getString(R.string.howToBlufi);
            String body = getResources().getString(R.string.infoBlufiConnect);
            InfoFragment info = InfoFragment.newInstance(title, body);
            info.show(getSupportFragmentManager(), "infoFragment");
        });

        mDevice = getIntent().getParcelableExtra(BlufiConstants.KEY_BLE_DEVICE);
        assert mDevice != null;
        String deviceName = mDevice.getName() == null ? getString(R.string.string_unknown) : mDevice.getName();
        setTitle(deviceName);

        mMsgRecyclerView = findViewById(R.id.recycler_view);
        mMsgList = new LinkedList<>();
        mMsgAdapter = new MsgAdapter();
        mMsgRecyclerView.setAdapter(mMsgAdapter);

        BlufiButtonListener clickListener = new BlufiButtonListener();

        mBlufiConnectBtn = findViewById(R.id.blufi_connect);
        mBlufiConnectBtn.setOnClickListener(clickListener);
        mBlufiConnectBtn.setOnLongClickListener(clickListener);

        mBlufiDisconnectBtn = findViewById(R.id.blufi_disconnect);
        mBlufiDisconnectBtn.setOnClickListener(clickListener);
        mBlufiDisconnectBtn.setOnLongClickListener(clickListener);
        mBlufiDisconnectBtn.setEnabled(false);

        mBlufiConfigureBtn = findViewById(R.id.blufi_configure);
        mBlufiConfigureBtn.setOnClickListener(clickListener);
        mBlufiConfigureBtn.setOnLongClickListener(clickListener);
        mBlufiConfigureBtn.setEnabled(false);

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        if (mBlufiClient != null) {
            mBlufiClient.close();
            mBlufiClient = null;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_CONFIGURE) {
            if (!mConnected) {
                return;
            }
            if (resultCode == RESULT_OK) {
                BlufiConfigureParams params = (BlufiConfigureParams) data.getSerializableExtra(BlufiConstants.KEY_CONFIGURE_PARAM);
                configure(params);
            }

            return;
        }

        super.onActivityResult(requestCode, resultCode, data);
    }

    private void updateMessage(String message, boolean isNotificaiton) {
        runOnUiThread(() -> {
            Message msg = new Message();
            msg.text = message;
            msg.isNotification = isNotificaiton;
            mMsgList.add(msg);
            mMsgAdapter.notifyItemInserted(mMsgList.size() - 1);
            mMsgRecyclerView.scrollToPosition(mMsgList.size() - 1);
        });
    }

    /**
     * Try to connect device
     */
    private void connect() {
        mBlufiConnectBtn.setEnabled(false);

        if (mBlufiClient != null) {
            mBlufiClient.close();
            mBlufiClient = null;
        }

        mBlufiClient = new BlufiClient(getApplicationContext(), mDevice);
        mBlufiClient.setGattCallback(new GattCallback());
        mBlufiClient.setBlufiCallback(new BlufiCallbackMain());
        mBlufiClient.connect();
    }

    /**
     * Request device disconnect the connection.
     */
    private void disconnectGatt() {
        mBlufiDisconnectBtn.setEnabled(false);

        if (mBlufiClient != null) {
            mBlufiClient.requestCloseConnection();
        }
    }


    /**
     * Go to configure options
     */
    private void configureOptions() {
        Intent intent = new Intent(BlufiConnect.this, ConfigureOptionsActivity.class);
        startActivityForResult(intent, REQUEST_CONFIGURE);
    }

    /**
     * Request to configure station or softap
     *
     * @param params configure params
     */
    private void configure(BlufiConfigureParams params) {
        mBlufiConfigureBtn.setEnabled(false);

        mBlufiClient.configure(params);
    }


    private void onGattConnected() {
        mConnected = true;
        runOnUiThread(() -> {
            mBlufiConnectBtn.setEnabled(false);

            mBlufiDisconnectBtn.setEnabled(true);
        });
    }

    private void onGattServiceCharacteristicDiscovered() {
        runOnUiThread(() -> {
            mBlufiConfigureBtn.setEnabled(true);
        });
    }

    private void onGattDisconnected() {
        mConnected = false;
        runOnUiThread(() -> {
            mBlufiConnectBtn.setEnabled(true);

            mBlufiDisconnectBtn.setEnabled(false);
            mBlufiConfigureBtn.setEnabled(false);
        });
    }

    /**
     * mBlufiClient call onCharacteristicWrite and onCharacteristicChanged is required
     */
    private class GattCallback extends BluetoothGattCallback {
        @Override
        public void onConnectionStateChange(BluetoothGatt gatt, int status, int newState) {
            String devAddr = gatt.getDevice().getAddress();
            mLog.d(String.format(Locale.ENGLISH, "onConnectionStateChange addr=%s, status=%d, newState=%d",
                    devAddr, status, newState));
            if (status == BluetoothGatt.GATT_SUCCESS) {
                switch (newState) {
                    case BluetoothProfile.STATE_CONNECTED:
                        onGattConnected();
                        updateMessage(String.format("Connected %s", devAddr), false);
                        break;
                    case BluetoothProfile.STATE_DISCONNECTED:
                        gatt.close();
                        onGattDisconnected();
                        updateMessage(String.format("Disconnected %s", devAddr), false);
                        break;
                }
            } else {
                gatt.close();
                onGattDisconnected();
                updateMessage(String.format(Locale.ENGLISH, "Disconnect %s, status=%d", devAddr, status),
                        false);
            }
        }

        @Override
        public void onMtuChanged(BluetoothGatt gatt, int mtu, int status) {
            mLog.d(String.format(Locale.ENGLISH, "onMtuChanged status=%d, mtu=%d", status, mtu));
            if (status == BluetoothGatt.GATT_SUCCESS) {
                updateMessage(String.format(Locale.ENGLISH, "Set mtu complete, mtu=%d ", mtu), false);
            } else {
                mBlufiClient.setPostPackageLengthLimit(20);
                updateMessage(String.format(Locale.ENGLISH, "Set mtu failed, mtu=%d, status=%d", mtu, status), false);
            }

            onGattServiceCharacteristicDiscovered();
        }

        @Override
        public void onServicesDiscovered(BluetoothGatt gatt, int status) {
            mLog.d(String.format(Locale.ENGLISH, "onServicesDiscovered status=%d", status));
            if (status != BluetoothGatt.GATT_SUCCESS) {
                gatt.disconnect();
                updateMessage(String.format(Locale.ENGLISH, "Discover services error status %d", status), false);
            }
        }

        @Override
        public void onDescriptorWrite(BluetoothGatt gatt, BluetoothGattDescriptor descriptor, int status) {
            mLog.d(String.format(Locale.ENGLISH, "onDescriptorWrite status=%d", status));
            if (descriptor.getUuid().equals(BlufiParameter.UUID_NOTIFICATION_DESCRIPTOR) &&
                    descriptor.getCharacteristic().getUuid().equals(BlufiParameter.UUID_NOTIFICATION_CHARACTERISTIC)) {
                String msg = String.format(Locale.ENGLISH, "Set notification enable %s", (status == BluetoothGatt.GATT_SUCCESS ? " complete" : " failed"));
                updateMessage(msg, false);
            }
        }

        @Override
        public void onCharacteristicWrite(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic, int status) {
            if (status != BluetoothGatt.GATT_SUCCESS) {
                gatt.disconnect();
                updateMessage(String.format(Locale.ENGLISH, "WriteChar error status %d", status), false);
            }
        }
    }

    private class BlufiCallbackMain extends BlufiCallback {
        @Override
        public void onGattPrepared(BlufiClient client, BluetoothGatt gatt, BluetoothGattService service,
                                   BluetoothGattCharacteristic writeChar, BluetoothGattCharacteristic notifyChar) {
            if (service == null) {
                mLog.w("Discover service failed");
                gatt.disconnect();
                updateMessage("Discover service failed", false);
                return;
            }
            if (writeChar == null) {
                mLog.w("Get write characteristic failed");
                gatt.disconnect();
                updateMessage("Get write characteristic failed", false);
                return;
            }
            if (notifyChar == null) {
                mLog.w("Get notification characteristic failed");
                gatt.disconnect();
                updateMessage("Get notification characteristic failed", false);
                return;
            }

            updateMessage("Discover service and characteristics success", false);

            int mtu = BlufiConstants.DEFAULT_MTU_LENGTH;
            mLog.d("Request MTU " + mtu);
            boolean requestMtu = gatt.requestMtu(mtu);
            if (!requestMtu) {
                mLog.w("Request mtu failed");
                updateMessage(String.format(Locale.ENGLISH, "Request mtu %d failed", mtu), false);
                onGattServiceCharacteristicDiscovered();
            }
        }

        @Override
        public void onNegotiateSecurityResult(BlufiClient client, int status) {
            if (status == STATUS_SUCCESS) {
                updateMessage("Negotiate security complete", false);
            } else {
                updateMessage("Negotiate security failed， code=" + status, false);
            }

        }

        @Override
        public void onPostConfigureParams(BlufiClient client, int status) {
            if (status == STATUS_SUCCESS) {
                updateMessage("Post configure params complete", false);
            } else {
                updateMessage("Post configure params failed, code=" + status, false);
            }

            mBlufiConfigureBtn.setEnabled(mConnected);
        }

        @Override
        public void onDeviceStatusResponse(BlufiClient client, int status, BlufiStatusResponse response) {
            if (status == STATUS_SUCCESS) {
                updateMessage(String.format("Receive device status response:\n%s", response.generateValidInfo()),
                        true);
            } else {
                updateMessage("Device status response error, code=" + status, false);
            }

        }

        @Override
        public void onDeviceScanResult(BlufiClient client, int status, List<BlufiScanResult> results) {
            if (status == STATUS_SUCCESS) {
                StringBuilder msg = new StringBuilder();
                msg.append("Receive device scan result:\n");
                for (BlufiScanResult scanResult : results) {
                    msg.append(scanResult.toString()).append("\n");
                }
                updateMessage(msg.toString(), true);
            } else {
                updateMessage("Device scan result error, code=" + status, false);
            }
        }

        @Override
        public void onDeviceVersionResponse(BlufiClient client, int status, BlufiVersionResponse response) {
            if (status == STATUS_SUCCESS) {
                updateMessage(String.format("Receive device version: %s", response.getVersionString()),
                        true);
            } else {
                updateMessage("Device version error, code=" + status, false);
            }
        }

        @Override
        public void onPostCustomDataResult(BlufiClient client, int status, byte[] data) {
            String dataStr = new String(data);
            String format = "Post data %s %s";
            if (status == STATUS_SUCCESS) {
                updateMessage(String.format(format, dataStr, "complete"), false);
            } else {
                updateMessage(String.format(format, dataStr, "failed"), false);
            }
        }

        @Override
        public void onReceiveCustomData(BlufiClient client, int status, byte[] data) {
            if (status == STATUS_SUCCESS) {
                String customStr = new String(data);
                updateMessage(String.format("Receive custom data:\n%s", customStr), true);
            } else {
                updateMessage("Receive custom data error, code=" + status, false);
            }
        }

        @Override
        public void onError(BlufiClient client, int errCode) {
            updateMessage(String.format(Locale.ENGLISH, "Receive error code %d", errCode), false);
        }
    }

    private class BlufiButtonListener implements View.OnClickListener, View.OnLongClickListener {
        private Toast mToast;

        @Override
        public void onClick(View v) {
            if (v == mBlufiConnectBtn) {
                connect();
            } else if (v == mBlufiDisconnectBtn) {
                disconnectGatt();
            }  else if (v == mBlufiConfigureBtn) {
                configureOptions();
            }
        }

        @Override
        public boolean onLongClick(View v) {
            if (mToast != null) {
                mToast.cancel();
            }

            int msgRes = 0;
            if (v == mBlufiConnectBtn) {
                msgRes = R.string.blufi_function_connect_msg;
            } else if (v == mBlufiDisconnectBtn) {
                msgRes = R.string.blufi_function_disconnect_msg;
            } else if (v == mBlufiConfigureBtn) {
                msgRes = R.string.blufi_function_configure_msg;
            }

            mToast = Toast.makeText(BlufiConnect.this, msgRes, Toast.LENGTH_SHORT);
            mToast.show();

            return true;
        }
    }

    private static class MsgHolder extends RecyclerView.ViewHolder {
        TextView text1;

        MsgHolder(View itemView) {
            super(itemView);

            text1 = itemView.findViewById(android.R.id.text1);
        }
    }

    private class MsgAdapter extends RecyclerView.Adapter<MsgHolder> {

        @NonNull
        @Override
        public MsgHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = getLayoutInflater().inflate(R.layout.blufi_message_item, parent, false);
            return new MsgHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull MsgHolder holder, int position) {
            Message msg = mMsgList.get(position);
            holder.text1.setText(msg.text);
            holder.text1.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
            holder.text1.setTypeface(ResourcesCompat.getFont(getApplicationContext(), R.font.mulish_regular));
            holder.text1.setTextColor(msg.isNotification ? ContextCompat.getColor(getApplicationContext(), R.color.red) : ContextCompat.getColor(getApplicationContext(), R.color.black));
        }

        @Override
        public int getItemCount() {
            return mMsgList.size();
        }
    }

    private static class Message {
        String text;
        boolean isNotification;
    }
}
