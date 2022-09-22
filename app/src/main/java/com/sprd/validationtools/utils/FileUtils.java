package com.sprd.validationtools.utils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.Charset;

import android.util.Log;

public class FileUtils {

    private static final String TAG = "FileUtils";

    public static int getIntFromFile(String filename) {
        File file = new File(filename);
        InputStream fIn = null;
        InputStreamReader isr = null;
        try {
            fIn = new FileInputStream(file);
            isr = new InputStreamReader(fIn,
                    Charset.defaultCharset());
            char[] inputBuffer = new char[1024];
            int q = -1;
            q = isr.read(inputBuffer);
            if (q > 0)
                return Integer.parseInt(String.valueOf(inputBuffer, 0, q)
                        .trim());
        } catch (IOException e) {
            e.printStackTrace();
        } catch (NumberFormatException e) {
            e.printStackTrace();
        } finally {
            try {
                if (isr != null) {
                    isr.close();
                }
                if (fIn != null) {
                    fIn.close();
                }
            } catch (IOException e2) {
                e2.printStackTrace();
            }
        }
        return -1;
    }

    public static void writeFile(String filename, String content) {
        try (FileOutputStream fos = new FileOutputStream(filename)) {
            byte[] bytes = content.getBytes();
            fos.write(bytes);
            fos.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static boolean fileIsExists(String path) {
        try {
            File file = new File(path);
            Log.d(TAG, "fileIsExists path=" + path);
            if (!file.exists()) {
                Log.d(TAG, path + " fileIsExists false");
                return false;
            }
        } catch (NullPointerException e) {
            e.printStackTrace();
            return false;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
        Log.d(TAG, path + " fileIsExists true");
        return true;
    }

    public static synchronized String readFile(String path) {
        File file = new File(path);
        StringBuffer sBuffer = new StringBuffer();
        try (InputStream fIn = new FileInputStream(file);
             BufferedReader bReader = new BufferedReader(new InputStreamReader(fIn, Charset.defaultCharset()))) {
            String str = bReader.readLine();

            while (str != null) {
                sBuffer.append(str + "\n");
                str = bReader.readLine();
            }
            return sBuffer.toString();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
}
