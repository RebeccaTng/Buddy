package be.kuleuven.buddy.bluetooth.ui;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.text.Editable;
import android.text.InputFilter;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.Toolbar;

import be.kuleuven.buddy.R;
import be.kuleuven.buddy.bluetooth.app.BaseActivity;
import be.kuleuven.buddy.bluetooth.app.BlufiLog;
import be.kuleuven.buddy.bluetooth.constants.BlufiConstants;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import blufi.espressif.params.BlufiConfigureParams;
import blufi.espressif.params.BlufiParameter;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.schedulers.Schedulers;

public class BlufiConfigure extends BaseActivity {

    private static final String PREF_AP = "blufi_conf_aps";

    private BlufiLog mLog = new BlufiLog(getClass());

    private WifiManager mWifiManager;
    private List<ScanResult> mWifiList;
    private boolean mScanning = false;
    private AutoCompleteTextView mStationSsidET;
    private EditText mStationPasswordET;

    private HashMap<String, String> mApMap;
    private List<String> mAutoCompleteSSIDs;
    private ArrayAdapter<String> mAutoCompleteSSIDAdapter;

    private SharedPreferences mApPref;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.blufi_configure_option_activity);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        setHomeAsUpEnable(true);

        mWifiManager = (WifiManager) getApplicationContext().getSystemService(WIFI_SERVICE);

        mApPref = getSharedPreferences(PREF_AP, MODE_PRIVATE);

        mApMap = new HashMap<>();
        mAutoCompleteSSIDs = new LinkedList<>();
        loadAPs();
        mWifiList = new ArrayList<>();
        mStationSsidET = findViewById(R.id.station_ssid);
        mStationPasswordET = findViewById(R.id.station_wifi_password);
        findViewById(R.id.station_wifi_scan).setOnClickListener(v -> scanWifi());
        mAutoCompleteSSIDAdapter = new ArrayAdapter<>(this, android.R.layout.simple_dropdown_item_1line, mAutoCompleteSSIDs);

        InputFilter filter = (source, start, end, dest, dstart, dend) -> {
            for (int i = start; i < end; i++) {
                if (Character.isWhitespace(source.charAt(i))) {
                    return "";
                }
            }
            return null;
        };
        mStationSsidET.setFilters(new InputFilter[] { filter });
        mStationSsidET.setAdapter(mAutoCompleteSSIDAdapter);
        mStationSsidET.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                String pwd = mApMap.get(s.toString());
                mStationPasswordET.setText(pwd);
                mStationSsidET.setTag(null);
            }
        });
        mStationSsidET.setText(getConnectionSSID());
        WifiInfo info = mWifiManager.getConnectionInfo();
        if (info != null) {
            byte[] ssidBytes = getSSIDRawData(info);
            mStationSsidET.setTag(ssidBytes);
        }

        findViewById(R.id.confirm).setOnClickListener(v -> configure());

        Observable.just(this)
                .subscribeOn(Schedulers.io())
                .doOnNext(BlufiConfigure::updateWifi)
                .subscribe();
    }

    private boolean is5GHz(int freq) {
        return freq > 4900 && freq < 5900;
    }

    private byte[] getSSIDRawData(WifiInfo info) {
        try {
            Method method = info.getClass().getMethod("getWifiSsid");
            method.setAccessible(true);
            Object wifiSsid = method.invoke(info);
            if (wifiSsid == null) {
                return null;
            }
            method = wifiSsid.getClass().getMethod("getOctets");
            method.setAccessible(true);
            return (byte[]) method.invoke(wifiSsid);
        } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException | NullPointerException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static byte[] getSSIDRawData(ScanResult scanResult) {
        try {
            Field field = scanResult.getClass().getField("wifiSsid");
            field.setAccessible(true);
            Object wifiSsid = field.get(scanResult);
            if (wifiSsid == null) {
                return null;
            }
            Method method = wifiSsid.getClass().getMethod("getOctets");
            method.setAccessible(true);
            return (byte[]) method.invoke(wifiSsid);
        } catch (NoSuchFieldException | IllegalAccessException | NoSuchMethodException | InvocationTargetException e) {
            e.printStackTrace();
        }

        return null;
    }

    private String getConnectionSSID() {
        if (!mWifiManager.isWifiEnabled()) {
            return null;
        }

        WifiInfo wifiInfo = mWifiManager.getConnectionInfo();
        if (wifiInfo == null) {
            return null;
        }

        String ssid = wifiInfo.getSSID();
        if (ssid.startsWith("\"") && ssid.endsWith("\"") && ssid.length() >= 2) {
            ssid = ssid.substring(1, ssid.length() - 1);
        }

        return ssid;
    }

    private int getConnectionFrequency() {
        if (!mWifiManager.isWifiEnabled()) {
            return -1;
        }

        WifiInfo wifiInfo = mWifiManager.getConnectionInfo();
        if (wifiInfo == null) {
            return -1;
        }

        return wifiInfo.getFrequency();
    }

    private void loadAPs() {
        Map<String, ?> aps = mApPref.getAll();
        for (Map.Entry<String, ?> entry : aps.entrySet()) {
            mApMap.put(entry.getKey(), entry.getValue().toString());
            mAutoCompleteSSIDs.add(entry.getKey());
        }
    }

    private void scanWifi() {
        if (!mWifiManager.isWifiEnabled()) {
            Toast.makeText(this, R.string.configure_wifi_disable_msg, Toast.LENGTH_SHORT).show();
            return;
        }

        if (mScanning) {
            return;
        }

        mScanning = true;

        final ProgressDialog dialog = new ProgressDialog(this);
        dialog.setCancelable(false);
        dialog.setMessage(getString(R.string.configure_station_wifi_scanning));
        dialog.show();

        Observable.just(mWifiManager)
                .subscribeOn(Schedulers.io())
                .doOnNext(wm -> {
                    wm.startScan();
                    try {
                        Thread.sleep(1500L);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }

                    updateWifi();
                })
                .observeOn(AndroidSchedulers.mainThread())
                .doOnComplete(() -> {
                    dialog.dismiss();
                    showWifiListDialog();
                    mScanning = false;
                })
                .subscribe();
    }

    private void updateWifi() {
        final List<ScanResult> scans = new LinkedList<>();
        Observable.fromIterable(mWifiManager.getScanResults())
                .filter(scanResult -> {
                    if (TextUtils.isEmpty(scanResult.SSID)) {
                        return false;
                    }

                    boolean contain = false;
                    for (ScanResult srScaned : scans) {
                        if (srScaned.SSID.equals(scanResult.SSID)) {
                            contain = true;
                            break;
                        }
                    }
                    return !contain;
                })
                .doOnNext(scans::add)
                .observeOn(AndroidSchedulers.mainThread())
                .doOnComplete(() -> {
                    mWifiList.clear();
                    mWifiList.addAll(scans);

                    mAutoCompleteSSIDs.clear();
                    Set<String> apDBSet = mApMap.keySet();
                    mAutoCompleteSSIDs.addAll(apDBSet);
                    Observable.fromIterable(mWifiList)
                            .filter(scanResult -> !apDBSet.contains(scanResult.SSID))
                            .doOnNext(scanResult -> mAutoCompleteSSIDs.add(scanResult.SSID))
                            .subscribe();
                    mAutoCompleteSSIDAdapter.notifyDataSetChanged();
                })
                .subscribe();
    }

    private void showWifiListDialog() {
        int count = mWifiList.size();
        if (count == 0) {
            Toast.makeText(this, R.string.configure_station_wifi_scanning_nothing, Toast.LENGTH_SHORT).show();
            return;
        }

        int checkedItem = -1;
        String inputSsid = mStationSsidET.getText().toString();
        final String[] wifiSSIDs = new String[count];
        for (int i = 0; i < count; i++) {
            ScanResult sr = mWifiList.get(i);
            wifiSSIDs[i] = sr.SSID;
            if (inputSsid.equals(sr.SSID)) {
                checkedItem = i;
            }
        }
        new AlertDialog.Builder(this)
                .setSingleChoiceItems(wifiSSIDs, checkedItem, (dialog, which) -> {
                    mStationSsidET.setText(wifiSSIDs[which]);
                    ScanResult scanResult = mWifiList.get(which);
                    byte[] ssidBytes = getSSIDRawData(scanResult);
                    mStationSsidET.setTag(ssidBytes);
                    dialog.dismiss();
                })
                .show();
    }

    private BlufiConfigureParams checkInfo() {
        BlufiConfigureParams params = new BlufiConfigureParams();
        params.setOpMode(BlufiParameter.OP_MODE_STA);
        if (checkSta(params)) return params;
        else return null;
    }

    private boolean checkSta(BlufiConfigureParams params) {
        String ssid = mStationSsidET.getText().toString();
        if (TextUtils.isEmpty(ssid)) {
            mStationSsidET.setError(getString(R.string.configure_station_ssid_error));
            return false;
        }
        byte[] ssidBytes = (byte[]) mStationSsidET.getTag();
        params.setStaSSIDBytes(ssidBytes != null ? ssidBytes : ssid.getBytes());
        String password = mStationPasswordET.getText().toString();
        params.setStaPassword(password);

        int freq = -1;
        if (ssid.equals(getConnectionSSID())) {
            freq = getConnectionFrequency();
        }
        if (freq == -1) {
            for (ScanResult sr : mWifiList) {
                if (ssid.equals(sr.SSID)) {
                    freq = sr.frequency;
                    break;
                }
            }
        }
        if (is5GHz(freq)) {
            mStationSsidET.setError(getString(R.string.configure_station_wifi_5g_error));
            new AlertDialog.Builder(this)
                    .setMessage(R.string.configure_station_wifi_5g_dialog_message)
                    .setPositiveButton(R.string.configure_station_wifi_5g_dialog_continue, (dialog, which) -> {
                        finishWithParams(params);
                    })
                    .setNegativeButton(R.string.configure_station_wifi_5g_dialog_cancel, null)
                    .show();
            return false;
        }

        return true;
    }

    private void configure() {
        mStationSsidET.setError(null);

        final BlufiConfigureParams params = checkInfo();
        if (params == null) {
            mLog.w("Generate configure params null");
            return;
        }

        finishWithParams(params);
    }

    private void saveAP(BlufiConfigureParams params) {
        switch (params.getOpMode()) {
            case BlufiParameter.OP_MODE_STA:
            case BlufiParameter.OP_MODE_STASOFTAP:
                String ssid = new String(params.getStaSSIDBytes());
                String pwd = params.getStaPassword();
                mApPref.edit().putString(ssid, pwd).apply();
                break;
            default:
                break;
        }
    }

    private void finishWithParams(BlufiConfigureParams params) {
        Intent intent = new Intent();
        intent.putExtra(BlufiConstants.KEY_CONFIGURE_PARAM, params);

        saveAP(params);

        setResult(RESULT_OK, intent);
        finish();
    }
}
