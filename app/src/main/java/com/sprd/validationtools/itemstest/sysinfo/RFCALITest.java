
package com.sprd.validationtools.itemstest.sysinfo;

import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

import android.os.Bundle;
import android.os.Handler;
import android.os.SystemProperties;
import android.util.Log;
import android.widget.TextView;

import com.sprd.validationtools.BaseActivity;
import com.sprd.validationtools.Const;
import com.simcom.testtools.R;
import com.sprd.validationtools.TelephonyManagerSprd;
import com.sprd.validationtools.utils.IATUtils;

public class RFCALITest extends BaseActivity {

    private static final String TAG = "RFCALITest";
    //This is only for 9620
    private static final String ADC_PATH = Const.PRODUCTINFO_DIR + "/adc.bin";

    private String str = "loading...";
    private TextView txtViewlabel01;
    private Handler mUiHandler = new Handler();
    private DataInputStream mInputStream=null;
    private static final int ADCBYTES = 56;
    byte[] buffer = new byte[ADCBYTES];

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.rf_cali_test);
        setTitle(R.string.rf_cali_test);
        txtViewlabel01 = (TextView) findViewById(R.id.rfc_id);
        txtViewlabel01.setTextSize(18);
        txtViewlabel01.setText(str);

        initial();
    }

    private void initial() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                int modemType = TelephonyManagerSprd.getModemType();
                Log.d(TAG,"initial modemType="+modemType);
                if (modemType == TelephonyManagerSprd.MODEM_TYPE_TDSCDMA
                        || TelephonyManagerSprd.getRadioCapbility() == TelephonyManagerSprd.RadioCapbility.TDD_CSFB
                        || TelephonyManagerSprd.getRadioCapbility() == TelephonyManagerSprd.RadioCapbility.CSFB ) {
                    str = "GSM/TD ";
                } else {
                    str = "GSM ";
                }
                Log.d(TAG,"initial str="+str);
                str += IATUtils.sendATCmd("AT+SGMR=0,0,3,0", "atchannel0");
                //Support WCDMA
                if (modemType == TelephonyManagerSprd.MODEM_TYPE_WCDMA
                        || TelephonyManagerSprd.getRadioCapbility() == TelephonyManagerSprd.RadioCapbility.FDD_CSFB
                        || TelephonyManagerSprd.getRadioCapbility() == TelephonyManagerSprd.RadioCapbility.CSFB
                        || TelephonyManagerSprd.getRadioCapbility() == TelephonyManagerSprd.RadioCapbility.LWLW
                        /*SPRd bug 830737:Add for support WCDMA*/
                        || TelephonyManagerSprd.IsSupportWCDMA()) {
                    str += "WCDMA ";
                    str += IATUtils.sendATCmd("AT+SGMR=0,0,3,1", "atchannel0");
                } else if(modemType == TelephonyManagerSprd.MODEM_TYPE_LTE) {
                    /*SPRD bug 773421:Supprt WCDMA*/
                    if(TelephonyManagerSprd.getRadioCapbility() == TelephonyManagerSprd.RadioCapbility.WG){
                        str += "WCDMA ";
                        str += IATUtils.sendATCmd("AT+SGMR=0,0,3,1", "atchannel0");
                    }
                }
                if(modemType == TelephonyManagerSprd.MODEM_TYPE_LTE || TelephonyManagerSprd.IsSupportLTE()) {
                    //WG not support LTE
                    if(TelephonyManagerSprd.getRadioCapbility() != TelephonyManagerSprd.RadioCapbility.WG){
                        str += "LTE ";
                        //New at cmd for LTE band
                        String temp = IATUtils.sendAtCmd("AT+SGMR=1,0,3,3,1");
                        if(!IATUtils.AT_FAIL.equalsIgnoreCase(temp)){
                            str += temp;
                        }else{
                            str += IATUtils.sendATCmd("AT+SGMR=1,0,3,3", "atchannel0");
                        }
                    }
                }
                if(TelephonyManagerSprd.IsSupportCDMA()) {
                    str += "CDMA2000 ";
                    str += IATUtils.sendATCmd("AT+SGMR=0,0,3,2", "atchannel0");
                }
                if(TelephonyManagerSprd.IsSupportNR()) {
                    str += "NR ";
                    str += IATUtils.sendATCmd("AT+SGMR=1,0,3,4", "atchannel0");
                }
                mUiHandler.post(new Runnable() {
                    public void run() {
                        txtViewlabel01.setText(str);
                    }
                });
            }
        }).start();
    }

    public boolean readFile() {
        try {
            File adcFile = new File(ADC_PATH);
            int count = 0;
            if (!adcFile.exists()) {
                Log.d(TAG, "adcFile do not exists");
                return false;
            }
            mInputStream = new DataInputStream(new FileInputStream(adcFile));
            if (mInputStream != null) {
                count = mInputStream.read(buffer, 0, ADCBYTES);
            }
            if (buffer == null || buffer.length <= 0 || count < 0) {
                Log.d(TAG, "buffer == null or buffer.length <= 0");
                return false;
            }
            Log.d(TAG, "count = " + count + " size = " + buffer.length);
            int adcBit = buffer.length - 4;
            int adcResult = buffer[adcBit] | 0xFFFFFFFC;
            Log.d(TAG, "adcBit = " + adcBit + " buffer[" + adcBit + "] = 0x"
                    + Integer.toHexString(buffer[adcBit]) + " adcResult = 0x"
                    + Integer.toHexString(adcResult));
            if (adcResult == 0xFFFFFFFF) {
                return true;
            } else {
                return false;
            }
        } catch (Exception e) {
            Log.d(TAG, "Failed get outputStream: " + e);
            e.printStackTrace();
        }
        return false;
    }  

    @Override
    protected void onDestroy() {
        try {
            if (mInputStream != null) {
                mInputStream.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        super.onDestroy();
    }
}
