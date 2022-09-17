package com.sprd.validationtools.utils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;

import android.content.Context;

public class StreamUtils {

    public static String get(Context context, int rawId) {
        InputStream stream = context.getResources().openRawResource(rawId);
        return read(stream);
    }

    public static String read(InputStream stream) {
        return read(stream, "utf-8");
    }

    public static String read(InputStream is, String encode) {
        if (is != null) {
            try (BufferedReader reader = new BufferedReader(
                    new InputStreamReader(is, encode));){
                StringBuilder sb = new StringBuilder();
                String line = null;
                while ((line = reader.readLine()) != null) {
                    sb.append(line + "\n");
                }
                return sb.toString();
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return "";
    }
}
