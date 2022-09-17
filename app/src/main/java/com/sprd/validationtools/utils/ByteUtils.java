package com.sprd.validationtools.utils;

public class ByteUtils {

    private static final String TAG = "ByteUtils";

    public static String intToBinary32(int i, int bitNum) {
        String binaryStr = Integer.toBinaryString(i);
        while (binaryStr.length() < bitNum) {
            binaryStr = "0" + binaryStr;
        }
        return binaryStr;
    }
}
