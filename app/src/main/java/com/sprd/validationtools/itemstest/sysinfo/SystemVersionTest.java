
package com.sprd.validationtools.itemstest.sysinfo;

import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.os.SystemProperties;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.sprd.validationtools.BaseActivity;
import com.sprd.validationtools.Const;
import com.sprd.validationtools.PhaseCheckParse;
import com.simcom.testtools.R;
import com.sprd.validationtools.utils.FileUtils;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

public class SystemVersionTest extends BaseActivity {
    private static final String TAG = "SystemVersionTest";
    private static final String PROD_VERSION_FILE = "/proc/version";
    private TextView androidVersion;
    private TextView linuxVersion;
    private TextView platformVersion;
    private TextView platformSn, imei, kernel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.version);
        setTitle(R.string.version_test);
        androidVersion = (TextView) findViewById(R.id.android_version);
        linuxVersion = (TextView) findViewById(R.id.linux_version);
        platformVersion = (TextView) findViewById(R.id.platform_version);
        platformSn = (TextView) findViewById(R.id.platform_sn);
        imei = findViewById(R.id.imei);
        kernel = findViewById(R.id.kernel_version);

        androidVersion.setText("\n" + getString(R.string.android_version) + "\n" + Build.VERSION.RELEASE
                + "\n");
        linuxVersion.setText(getString(R.string.prop_version) + "\n" + getPropVersion() + "\n");
        platformVersion.setText(getString(R.string.build_number) + "\n" +
                SystemProperties.get("ro.build.display.id", "unknown") + "\n");
        platformSn.setText(getString(R.string.device_sn) + "\n" + getSn() + "\n");
        imei.setText("imei:" +"\n" + getImei());
        kernel.setText("kernel: " + "\n" + getKernelVersion());

        /*SPRD bug 855450:ZTE feature*/
        if (Const.isBoardISharkL210c10() && getIntent() != null && getIntent().getExtras() != null) {
            String securiy_code = getIntent().getExtras().getString(Const.SECURITY_CODE);
            Log.d(TAG, "onCreate securiy_code=" + securiy_code);
            if (!TextUtils.isEmpty(securiy_code) && "833".equals(securiy_code)) {
                platformVersion.setVisibility(View.GONE);
            }
        }
        /*@}*/
    }

    public String getImei() {
        TelephonyManager telephonyMgr = (TelephonyManager) this.getSystemService(TELEPHONY_SERVICE);
        String imei = telephonyMgr.getDeviceId();
        return imei ;
    }

    public String getKernelVersion() {
        String kernelVersion = "";
        InputStream inputStream = null;
        try {
            inputStream = new FileInputStream("/proc/version");
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            return kernelVersion;
        }
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream), 8 * 1024);
        String info = "";
        String line = "";
        try {
            while ((line = bufferedReader.readLine()) != null) {
                info += line;
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                bufferedReader.close();
                inputStream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        try {
            if (info != "") {
                final String keyword = "version ";
                int index = info.indexOf(keyword);
                line = info.substring(index + keyword.length());
                index = line.indexOf(" ");
                kernelVersion = line.substring(0, index);
            }
        } catch (IndexOutOfBoundsException e) {
            e.printStackTrace();
        }

        return kernelVersion;
    }

    private String getPropVersion() {
        return FileUtils.readFile(PROD_VERSION_FILE);
    }

    private String getSn() {
        PhaseCheckParse parse = PhaseCheckParse.getInstance();
        return parse.getSn();
    }
}