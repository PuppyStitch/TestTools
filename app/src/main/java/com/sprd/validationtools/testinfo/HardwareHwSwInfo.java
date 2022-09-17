/*
* add by YangJianE 20160226 for HardwareInfo add HW/SW version
*  for cfzz1-fs006
*/

package com.sprd.validationtools.testinfo;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.LineNumberReader;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import android.telephony.TelephonyManager;
import android.util.Log;

import android.os.Bundle;

import android.app.Activity;

import android.widget.TextView;


import com.simcom.testtools.R;

import android.os.SystemProperties;

public class HardwareHwSwInfo extends Activity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.hardwarehwsw_info);

        String temp = getFormattedContent();

        StringBuilder sBuilderHwSw = new StringBuilder();
        String hw_ver = SystemProperties.get("ro.product.hardware","Sprd");
        String sw_ver = SystemProperties.get("ro.build.display.id","Unknown");
        
        sBuilderHwSw.append("[HW VERSION]").append("\n").append(hw_ver).append("\n").append("\n");
        sBuilderHwSw.append("[SW VERSION]").append("\n").append(sw_ver).append("\n").append("\n");

        sBuilderHwSw.append("[FLASH]").append("\n").append("Unknown").append("\n").append("\n");
        sBuilderHwSw.append("[TP FIRMWARE VERSION]").append("\n").append("Unknown").append("\n").append("\n");

        String tmp_hwsw = sBuilderHwSw.toString();

        temp = tmp_hwsw + temp;

        final TextView tvHardwareHwSwInfo = (TextView) findViewById(R.id.tv_hardwarehwswinfo);
        tvHardwareHwSwInfo.setText(temp);
    }

    public String readHardwareInfo()
    {
    String TAG = "HardwareHwSwInfo";
    String res = "Unknown";
    String str = "Unknown";
    try {
        Log.d(TAG, "readHardwareInfo \n");
        Process pp = Runtime.getRuntime().exec(
                        "cat /proc/zyt_peripherals");
        InputStreamReader ir = new InputStreamReader(pp.getInputStream(),Charset.defaultCharset());
        LineNumberReader input = new LineNumberReader(ir);
        //Log.d(TAG, "readHardwareInfo input.readLine():" + input.readLine());

        for (; null != str;) {
                str = input.readLine();
                if (str != null) {
                    res = str.trim();
                    break;
                }
        }
    } catch (IOException ex) {
        ex.printStackTrace();
    }
    Log.d(TAG, "readHardwareInfo res:" + res);
    return res;   
  
    }
    
    private String getFormattedContent() {
            StringBuilder sBuilder = new StringBuilder();
            String content = readHardwareInfo();
            String temp = "";
            String sub_str1 = "";
            String sub_str2 = "";
            int pos1 = 0;
            int pos2 = 0;
            
            if(content.equals("Unknown"))
                return content;
                
            //[TP]
            {
                pos1 = content.indexOf("[TP]");
                pos2 = content.indexOf(";",pos1);
                sub_str1 = content.substring(pos1,pos2);
                
                pos1 = sub_str1.indexOf("]")+1;
                sub_str2 = sub_str1.substring(pos1);
                sBuilder.append("[TP]").append("\n").append(sub_str2).append("\n").append("\n");
            }
            
            //[LCD]
            {
                pos1 = content.indexOf("[LCD]");
                pos2 = content.indexOf(";",pos1);
                sub_str1 = content.substring(pos1,pos2);
                
                pos1 = sub_str1.indexOf("]")+1;
                sub_str2 = sub_str1.substring(pos1);
                sBuilder.append("[LCD]").append("\n").append(sub_str2).append("\n").append("\n");
            }

            //[CAMERA_MAIN]
            {
                pos1 = content.indexOf("[CAMERA_MAIN]");
                pos2 = content.indexOf(";",pos1);
                sub_str1 = content.substring(pos1,pos2);
                
                pos1 = sub_str1.indexOf("]")+1;
                sub_str2 = sub_str1.substring(pos1);
                sBuilder.append("[CAMERA_MAIN]").append("\n").append(sub_str2).append("\n").append("\n");
            }
            
            //[CAMERA_SUB]
            {
                pos1 = content.indexOf("[CAMERA_SUB]");
                pos2 = content.indexOf(";",pos1);
                sub_str1 = content.substring(pos1,pos2);
                
                pos1 = sub_str1.indexOf("]")+1;
                sub_str2 = sub_str1.substring(pos1);
                sBuilder.append("[CAMERA_SUB]").append("\n").append(sub_str2).append("\n").append("\n");
            }

            //[GSENSOR]
            {           
                pos1 = content.indexOf("[GSENSOR]");
                pos2 = content.indexOf(";",pos1);
                sub_str1 = content.substring(pos1,pos2);
                
                pos1 = sub_str1.indexOf("]")+1;
                sub_str2 = sub_str1.substring(pos1);
                sBuilder.append("[GSENSOR]").append("\n").append(sub_str2).append("\n").append("\n");
            }

            //[PLSENSOR]
            {           
                pos1 = content.indexOf("[PLSENSOR]");
                pos2 = content.indexOf(";",pos1);
                sub_str1 = content.substring(pos1,pos2);
                
                pos1 = sub_str1.indexOf("]")+1;
                sub_str2 = sub_str1.substring(pos1);
                sBuilder.append("[PLSENSOR]").append("\n").append(sub_str2).append("\n").append("\n");
            }
            
            return sBuilder.toString();
    }
}
