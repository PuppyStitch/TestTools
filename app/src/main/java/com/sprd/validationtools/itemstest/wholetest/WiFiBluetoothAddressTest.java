package com.sprd.validationtools.itemstest.wholetest;

import android.bluetooth.BluetoothAdapter;
import android.content.Context;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.Handler;
import android.widget.TextView;
import android.widget.Toast;

import com.simcom.testtools.R;
import com.sprd.validationtools.BaseActivity;
import com.sprd.validationtools.utils.BtTestUtil;
import com.sprd.validationtools.utils.WifiTestUtil;

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
        disablePassButton();
        init();
    }

    @Override
    protected void onResume() {
        super.onResume();
        btTestUtil.startTest(this);
        mHandler.postDelayed(runnable, TIMEOUT);
    }

    @Override
    protected void onPause() {
        super.onPause();
        btTestUtil.stopTest();
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


        wifiTestUtil = new WifiTestUtil(mWifiManager);
        WifiAddressString = wifiTestUtil.getWifiManager().getConnectionInfo().getMacAddress();
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
