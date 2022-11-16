package com.sprd.validationtools.utils;

import android.util.Log;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

public class RootCmdUtils {

    private static final String TAG = "RootCMDUtils";

    public static synchronized String execRootCmd(String cmd) {

        Process process = null;
        BufferedReader bufferedReader = null;
        InputStreamReader inputStreamReader = null;
        try {
            process = Runtime.getRuntime().exec(cmd);
            inputStreamReader = new InputStreamReader(process.getInputStream());
            bufferedReader = new BufferedReader(inputStreamReader);

            int read;
            char[] buffer = new char[4096];
            StringBuilder output = new StringBuilder();
            while ((read = bufferedReader.read(buffer)) > 0) {
                output.append(buffer, 0, read);
            }
            return output.toString();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        } finally {
            if (null != inputStreamReader) {
                try {
                    inputStreamReader.close();
                } catch (Throwable t) {
                    t.printStackTrace();
                }
            }
            if (null != bufferedReader) {
                try {
                    bufferedReader.close();
                } catch (Throwable t) {
                    t.printStackTrace();
                }
            }
            if (null != process) {
                try {
                    process.destroy();
                } catch (Throwable t) {
                    t.printStackTrace();
                }
            }
        }
    }

    public static void echo(String[] command) {
        try {
            Process process = Runtime.getRuntime().exec("sh");
            DataOutputStream outputStream = new DataOutputStream(process.getOutputStream());
            for (String tmpCmd : command) {
                Log.d(TAG, "tmpCmd: " + tmpCmd);
                outputStream.writeBytes(tmpCmd + "\n");
            }
            outputStream.flush();
            outputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
            Log.e(TAG, "ex" + e.toString());
        }
    }
}
