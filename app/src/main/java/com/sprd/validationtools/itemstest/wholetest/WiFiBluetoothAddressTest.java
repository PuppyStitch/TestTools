package com.sprd.validationtools.itemstest.wholetest;

import android.bluetooth.BluetoothAdapter;
import android.content.Context;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import com.simcom.testtools.R;
import com.sprd.validationtools.BaseActivity;
import com.sprd.validationtools.utils.BtTestUtil;
import com.sprd.validationtools.utils.WifiTestUtil;

import java.net.NetworkInterface;
import java.util.Collections;
import java.util.List;

public class WiFiBluetoothAddressTest extends BaseActivity {

    private static final String TAG = "WiFiBluetoothAddressTes";

    private TextView tvWifiAddr = null;
    private TextView tvBtAddr = null;
    private BtTestUtil btTestUtil = null;
    private WifiTestUtil wifiTestUtil;
    private WifiManager mWifiManager = null;

    String BtAddressString = "", WifiAddressString = "";
    String address = "02:00";

    public Handler mHandler = new Handler();
    private static final int TIMEOUT = 8000;
    private boolean isOk = false;
    private Runnable runnable = new Runnable() {
        public void run() {
            if (!address.equals(BtAddressString) && !address.equals(WifiAddressString)) {
                Toast.makeText(WiFiBluetoothAddressTest.this, R.string.text_pass,
                        Toast.LENGTH_SHORT).show();
                storeRusult(true);
            } else {
                Toast.makeText(WiFiBluetoothAddressTest.this, R.string.text_fail,
                        Toast.LENGTH_SHORT).show();
                storeRusult(false);
            }
            mHandler.removeCallbacks(runnable);
            finish();
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wifi_bluetooth_address_layout);
        tvWifiAddr = (TextView) findViewById(R.id.wifi_addr_content);
        tvBtAddr = (TextView) findViewById(R.id.bt_addr_content);
        mWifiManager = (WifiManager) getSystemService(Context.WIFI_SERVICE);
        wifiTestUtil = new WifiTestUtil(mWifiManager);
    }

    @Override
    protected void onResume() {
        super.onResume();
        wifiTestUtil.startTest(this);
        init();
        btTestUtil.startTest(this);
        disablePassButton();
        mHandler.postDelayed(runnable, TIMEOUT);
    }

    @Override
    protected void onPause() {
        super.onPause();
        btTestUtil.stopTest();
        wifiTestUtil.stopTest();
    }

    private static String getMacFromHardware() {
        //LogUtils.logD(TAG, "获取WifiMac地址===》getMacFromHardware方法 当前系统版本==》" + Build.VERSION.SDK_INT);
        try {
            List<NetworkInterface> all = Collections.list(NetworkInterface.getNetworkInterfaces());
            for (NetworkInterface nif : all) {
                if (!nif.getName().equalsIgnoreCase("wlan0")) continue;

                byte[] macBytes = nif.getHardwareAddress();
                if (macBytes == null) {
                    return null;
                }

                StringBuilder res1 = new StringBuilder();
                for (byte b : macBytes) {
                    res1.append(String.format("%02X:", b));
                }

                if (res1.length() > 0) {
                    res1.deleteCharAt(res1.length() - 1);
                }
                Log.i(TAG, res1.toString());
                return res1.toString();
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return null;
    }

    private void init() {
        btTestUtil = new BtTestUtil() {

            public void btStateChange(int newState) {
                switch (newState) {
                    case BluetoothAdapter.STATE_ON:
                        // SPRD: update bluetooth address when bt power on
//                        if (ActivityCompat.checkSelfPermission(mContext, Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED) {
//                            // TODO: Consider calling
//                            //    ActivityCompat#requestPermissions
//                            // here to request the missing permissions, and then overriding
//                            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
//                            //                                          int[] grantResults)
//                            // to handle the case where the user grants the permission. See the documentation
//                            // for ActivityCompat#requestPermissions for more details.
//
//                            Log.e(TAG, "permission error");
//
//                            return;
//                        }
                        BtAddressString = btTestUtil.getBluetoothAdapter().getAddress() + "\n";
                        tvBtAddr.setText(BtAddressString);
                        if (BtAddressString.length() > 4) {
                            BtAddressString = BtAddressString.substring(0, 5);

                            if (!address.equals(BtAddressString) && !address.equals(WifiAddressString)
                                    && !"".equals(BtAddressString) && !"".equals(WifiAddressString)) {
                                enablePassButton();
                            }
                        }
                        break;
                    default:
                        break;
                }
            }
        };


//        WifiAddressString = wifiTestUtil.getWifiManager().getConnectionInfo().getMacAddress();
        WifiAddressString = getMacFromHardware();
        tvWifiAddr.setText(WifiAddressString);
        if (WifiAddressString.length() > 4) {
            WifiAddressString = WifiAddressString.substring(0, 5);
        }

        if (!address.equals(BtAddressString) && !address.equals(WifiAddressString)
                && !"".equals(BtAddressString) && !"".equals(WifiAddressString)) {
            enablePassButton();
        }
    }
}
