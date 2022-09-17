package com.sprd.validationtools;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.sprd.validationtools.utils.ByteUtils;

import android.net.LocalSocket;
import android.net.LocalSocketAddress;
import android.util.Log;

/*Parse the phasecheck as the little endian*/
public class PhaseCheckParse {
    private static String TAG = "PhaseCheckParse";

    private static final int TYPE_GET_SN1 = 0;
    private static final int TYPE_GET_SN2 = 1;
    private static final int TYPE_WRITE_STATION_TESTED = 2;
    private static final int TYPE_WRITE_STATION_PASS = 3;
    private static final int TYPE_WRITE_STATION_FAIL = 4;
    private static final int TYPE_GET_PHASECHECK = 5;
    private static final int TYPE_WRITE_CHARGE_SWITCH = 6;

    private static final int TYPE_WRITE_OFFSET = 17;
    private static final int TYPE_READ_OFFSET = 18;
    private static final int TYPE_EXECUTE_OFFSET_CALIBRATION = 23;
    private static final int TYPE_EXECUTE_XTALK_CALIBRATION = 24;
    private static final int TYPE_EXECUTE_AI_TEST = 29;

    private AdaptBinder binder;
    private static final int BUF_SIZE = 4096;

    private static PhaseCheckParse mPhaseCheckParse = null;

    public static PhaseCheckParse getInstance() {
        if (mPhaseCheckParse == null) {
            mPhaseCheckParse = new PhaseCheckParse();
        }
        return mPhaseCheckParse;
    }

    private PhaseCheckParse() {
        binder = new AdaptBinder();
        Log.d(TAG, "Get The service connect!");
    }

    private boolean hasDigit(String content) {
        boolean flag = false;
        Pattern p = Pattern.compile(".*\\d+.*");
        Matcher m = p.matcher(content);
        if (m.matches())
            flag = true;
        return flag;
    }

    private String StationTested(char testSign, char item) {
        if (testSign == '0' && item == '0')
            return "PASS";
        if (testSign == '0' && item == '1')
            return "FAIL";
        return "UnTested";
    }

    private int getStationTest(String station_name) {
        Log.d(TAG, "getStationTest: " + station_name);
        int ret = -1;
        try {
            Parcel data = new Parcel();
            Parcel reply = new Parcel();
            binder.transact(TYPE_GET_PHASECHECK, data, reply, 0);
            Log.d(TAG, "transact SUCESS!!");
            int testSign = reply.readInt();
            int item = reply.readInt();
            String stationName = reply.readString();
            Log.d(TAG, "stationName = " + stationName);
            String[] str = stationName.split(Pattern.quote("|"));
            String strTestSign = ByteUtils.intToBinary32(testSign,32);
            String strItem = ByteUtils.intToBinary32(item,32);
            Log.d(TAG, "strTestSign = " + strTestSign + " strItem = " + strItem);
            for (int i = 0; i < str.length; i++) {
                Log.d(TAG, "str =  " + str[i]);
                if (station_name.equalsIgnoreCase(str[i])) {
                    ret = i;
                    break;
                }
            }
            data.recycle();
            reply.recycle();
        } catch (NullPointerException | IndexOutOfBoundsException | UnsupportedEncodingException ex) {
            ex.printStackTrace();
            return -1;
        }
        Log.d(TAG, "getStationTest return " + ret);
        return ret;
    }

    public String getSn() {
        String result = null;
        try {
            Parcel data = new Parcel();
            Parcel reply = new Parcel();
            binder.transact(0, data, reply, 0);
            Log.e(TAG, "transact end");
            String sn1 = reply.readString();
            for (int i = 0; i < 5; i++) {
                if (hasDigit(sn1)) {
                    break;
                }
                binder.transact(TYPE_GET_SN1, data, reply, 0);
                sn1 = reply.readString();
            }
            binder.transact(TYPE_GET_SN2, data, reply, 0);
            String sn2 = reply.readString();
            for (int i = 0; i < 5; i++) {
                if (hasDigit(sn2)) {
                    break;
                }
                binder.transact(1, data, reply, 0);
                sn2 = reply.readString();
            }
            if (!sn1.isEmpty() && sn1.length() > 24 && !sn2.isEmpty()
                    && sn2.length() > 0) {
                /* SPRD bug 838344:Read sn issue. */
                if (sn1.length() > sn2.length() && sn1.contains(sn2)) {
                    sn1 = sn1.substring(0, sn1.length() - sn2.length());
                    Log.e(TAG, "sn1 contains sn2 ,SN1 = " + sn1 + "\n SN2="
                            + sn2);
                }
                /* @} */
            }
            result = "SN1:" + sn1 + "\n" + "SN2:" + sn2;
            Log.e(TAG, "SN1 = " + sn1 + " SN2=" + sn2);
            data.recycle();
            reply.recycle();
        } catch (NullPointerException | IndexOutOfBoundsException | UnsupportedEncodingException ex) {
            Log.e(TAG, "Exception :" + ex);
            ex.printStackTrace();
            result = "get SN fail:" + ex.getMessage();
        }
        return result;
    }

    public String getSnByIndex(int index) {
        String result = null;
        try{
            int type_sn = index;
            Parcel data = new Parcel();
            Parcel reply = new Parcel();

            Log.d(TAG, "getSnByIndex type_sn="+type_sn);
            String sn = "";
            binder.transact(type_sn, data, reply, 0);
            sn = reply.readString();

            Log.d(TAG, "SN" + index + " = " +  sn);
            result = sn;
            data.recycle();
            reply.recycle();
        } catch (NullPointerException | IndexOutOfBoundsException | UnsupportedEncodingException ex) {
            Log.e(TAG, "Exception :" + ex);
            ex.printStackTrace();
            result = "get SN fail:" + ex.getMessage();
        }
        return result;
    }

    public boolean writeStationTested(int station) {
        try {
            Parcel data = new Parcel();
            Parcel replay = new Parcel();
            data.writeInt(station);
            binder.transact(TYPE_WRITE_STATION_TESTED, data, replay, 0);
            data.recycle();
            replay.recycle();
            return true;
        } catch (NullPointerException | IndexOutOfBoundsException | UnsupportedEncodingException ex) {
            Log.e(TAG, "Exception " + ex.getMessage());
            return false;
        }
    }

    public boolean writeStationTested(String station) {
        int stat = getStationTest(station);
        Log.d(TAG, "stat = " + stat);
        if (stat == -1) {
            return false;
        } else {
            return writeStationTested(stat);
        }
    }

    public boolean writeStationPass(int station) {
        try {
            Parcel data = new Parcel();
            Parcel replay = new Parcel();
            data.writeInt(station);
            binder.transact(TYPE_WRITE_STATION_PASS, data, replay, 0);
            data.recycle();
            replay.recycle();
            return true;
        } catch (NullPointerException | IndexOutOfBoundsException | UnsupportedEncodingException ex) {
            Log.e(TAG, "Exception " + ex.getMessage());
            return false;
        }
    }

    public boolean writeStationPass(String station) {
        int stat = getStationTest(station);
        Log.d(TAG, "stat = " + stat);
        if (stat == -1) {
            return false;
        } else {
            return writeStationPass(stat);
        }
    }

    public boolean writeChargeSwitch(int value) {
        try {
            Parcel data = new Parcel();
            Parcel reply = new Parcel();
            data.writeInt(value);
            binder.transact(TYPE_WRITE_CHARGE_SWITCH, data, reply, 0);
            data.recycle();
            reply.recycle();
            return true;
        } catch (NullPointerException | IndexOutOfBoundsException | UnsupportedEncodingException ex) {
            Log.e(TAG, "Exception ", ex);
            ex.printStackTrace();
            return false;
        }
    }

    public int executeOffsetCalibration() {
        try {
            Parcel data = new Parcel();
            Parcel reply = new Parcel();
            binder.transact(TYPE_EXECUTE_OFFSET_CALIBRATION, data, reply, 0);
            int retValue = reply.readInt();
            Log.d(TAG, "executeOffsetCalibration data = " + retValue
                    + " SUCESS!!");
            data.recycle();
            reply.recycle();
            return retValue;
        } catch (NullPointerException | IndexOutOfBoundsException | UnsupportedEncodingException ex) {
            Log.e(TAG, "Exception ", ex);
            ex.printStackTrace();
        }
        return 0;
    }

    public int executeXtalkCalibration() {
        try {
            Parcel data = new Parcel();
            Parcel reply = new Parcel();
            binder.transact(TYPE_EXECUTE_XTALK_CALIBRATION, data, reply, 0);
            int retValue = reply.readInt();
            Log.d(TAG, "executeOffsetCalibration data = " + retValue
                    + " SUCESS!!");
            data.recycle();
            reply.recycle();
            return retValue;
        } catch (NullPointerException | IndexOutOfBoundsException | UnsupportedEncodingException ex) {
            Log.e(TAG, "Exception ", ex);
            ex.printStackTrace();
        }
        return 0;
    }

    public int executeAITest(int ip) {
        try {
            Parcel data = new Parcel();
            Parcel reply = new Parcel();
            data.writeInt(ip);
            binder.transact(TYPE_EXECUTE_AI_TEST, data, reply, 0);
            int retValue = reply.readInt();
            Log.d(TAG, "executeAITest data=:" + retValue + " SUCESS!!");
            data.recycle();
            reply.recycle();
            return retValue;
        } catch (NullPointerException | IndexOutOfBoundsException | UnsupportedEncodingException ex) {
            Log.e(TAG, "Exception ", ex);
            ex.printStackTrace();
        }
        return 0;
    }

    public boolean writeStationFail(int station) {
        try {
            Parcel data = new Parcel();
            Parcel replay = new Parcel();
            data.writeInt(station);
            binder.transact(TYPE_WRITE_STATION_FAIL, data, replay, 0);
            data.recycle();
            replay.recycle();
            return true;
        } catch (NullPointerException | IndexOutOfBoundsException | UnsupportedEncodingException ex) {
            Log.e(TAG, "Exception " + ex.getMessage());
            ex.printStackTrace();
            return false;
        }
    }

    public boolean writeStationFail(String station) {
        int stat = getStationTest(station);
        Log.d(TAG, "stat = " + stat);
        if (stat == -1) {
            return false;
        } else {
            return writeStationFail(stat);
        }
    }

    public byte[] readMiscdataOffsetByteArray(int offset,int count) {
        if(count <= 0){
            Log.w(TAG, "Wrong count");
            return null;
        }
        byte[] value = new byte[count];
        try {
            Parcel data = new Parcel();
            Parcel reply = new Parcel();
            //Set offset
            data.writeInt(offset);
            //Set read byte count
            data.writeInt(count);
            binder.transact(TYPE_READ_OFFSET, data, reply, 0);
            reply.readByteArray(value);
            data.recycle();
            reply.recycle();
        } catch (NullPointerException | IndexOutOfBoundsException | UnsupportedEncodingException ex) {
            Log.e(TAG, "Exception " + ex.getMessage());
            ex.printStackTrace();
        }

        return value;
    }

    public boolean writeMiscdataOffsetByteArray(int offset, byte[] value) {
        if(value == null || value.length <= 0){
            Log.w(TAG, "Wrong value");
            return false;
        }
        try {
            Parcel data = new Parcel();
            Parcel reply = new Parcel();
            data.writeInt(offset);
            data.writeByteArray(value, 0, value.length);
            binder.transact(TYPE_WRITE_OFFSET, data, reply, 0);
            data.recycle();
            reply.recycle();
            return true;
        } catch (NullPointerException | IndexOutOfBoundsException | UnsupportedEncodingException ex) {
            Log.e(TAG, "Exception " + ex.getMessage());
            ex.printStackTrace();
            return false;
        }
    }

    public String getPhaseCheck() {
        String result = null;
        try {
            Parcel data = new Parcel();
            Parcel reply = new Parcel();
            binder.transact(TYPE_GET_PHASECHECK, data, reply, 0);
            Log.e(TAG, "transact SUCESS!!");
            int testSign = reply.readInt();
            int item = reply.readInt();
            String stationName = reply.readString().trim();
            Log.d(TAG, "stationName = " + stationName);
            String[] str = stationName.split(Pattern.quote("|"));
//          String strTestSign = Integer.toBinaryString(testSign);
//          String strItem = Integer.toBinaryString(item);
            String strTestSign = ByteUtils.intToBinary32(testSign,32);
            String strItem = ByteUtils.intToBinary32(item,32);
            char[] charSign = strTestSign.toCharArray();
            char[] charItem = strItem.toCharArray();
            StringBuffer sb = new StringBuffer();
            Log.e(TAG, "strTestSign = " + strTestSign + " strItem = " + strItem);
            for (int i = 0; i < str.length; i++) {
                sb.append(str[i]
                        + ":"
                        + StationTested(charSign[charSign.length - i - 1],
                                charItem[charItem.length - i - 1]));
                if (i < str.length - 1) {
                    sb.append("\n");
                }
            }
            result = sb.toString();
            data.recycle();
            reply.recycle();
        } catch (NullPointerException | IndexOutOfBoundsException | UnsupportedEncodingException ex) {
            Log.e(TAG, "huasong Exception " + ex.getMessage());
            result = "get phasecheck fail:" + ex.getMessage();
        }
        return result;
    }

    public boolean getPhaseCheckStationTested(int station) {
        try{
            Log.e(TAG, "getPhaseCheckStationTested station="+station);
            if(station < 0){
                return false;
            }
            Parcel data = new Parcel();
            Parcel reply = new Parcel();
            binder.transact(TYPE_GET_PHASECHECK, data, reply, 0);

            int testSign = reply.readInt();
            int item = reply.readInt();
            String stationName = reply.readString().trim();
            String []str = stationName.split(Pattern.quote("|"));
//          String strTestSign = Integer.toBinaryString(testSign);
//          String strItem = Integer.toBinaryString(item);
            String strTestSign = ByteUtils.intToBinary32(testSign,32);
            String strItem = ByteUtils.intToBinary32(item,32);
            char[] charSign = strTestSign.toCharArray();

            Log.d(TAG, "getPhaseCheckStationTested strTestSign = " + strTestSign + " strItem = " + strItem);
            Log.d(TAG, "getPhaseCheckStationTested str.length="+str.length);
            Log.d(TAG, "getPhaseCheckStationTested stationName = " + stationName);
            if(station >=  str.length){
                data.recycle();
                reply.recycle();
                return false;
            }
            int stationIndex = charSign.length-station-1;
            Log.d(TAG, "getPhaseCheckStationPass stationIndex = " + stationIndex);
            Log.d(TAG, "getPhaseCheckStationPass charSign[stationIndex] = " + charSign[stationIndex]);
            if(charSign[stationIndex] =='0'){
                Log.d(TAG, "getPhaseCheckStationTested station tested!");
                data.recycle();
                reply.recycle();
                return true;
            }
            data.recycle();
            reply.recycle();
        }catch (Exception ex) {
            ex.printStackTrace();
        }
        return false;
    }

    public boolean getPhaseCheckStationPass(int station) {
        try{
            Log.e(TAG, "getPhaseCheckStationPass station="+station);
            if(station < 0){
                return false;
            }
            Parcel data = new Parcel();
            Parcel reply = new Parcel();
            binder.transact(TYPE_GET_PHASECHECK, data, reply, 0);

            int testSign = reply.readInt();
            int item = reply.readInt();
            String stationName = reply.readString().trim();
            String []str = stationName.split(Pattern.quote("|"));
//          String strTestSign = Integer.toBinaryString(testSign);
//          String strItem = Integer.toBinaryString(item);
            String strTestSign = ByteUtils.intToBinary32(testSign,32);
            String strItem = ByteUtils.intToBinary32(item,32);
            char[] charSign = strTestSign.toCharArray();
            char[] charItem = strItem.toCharArray();

            Log.d(TAG, "getPhaseCheckStationPass strTestSign = " + strTestSign + " strItem = " + strItem);
            Log.d(TAG, "getPhaseCheckStationPass str.length="+str.length);
            Log.d(TAG, "getPhaseCheckStationPass stationName = " + stationName);
            if(station >=  charSign.length){
                data.recycle();
                reply.recycle();
                return false;
            }
            int stationIndex = charSign.length-station-1;
            int charItemIndex = charItem.length-station-1;
            Log.d(TAG, "getPhaseCheckStationPass stationIndex = " + stationIndex);
            Log.d(TAG, "getPhaseCheckStationPass charItemIndex = " + charItemIndex);
            Log.d(TAG, "getPhaseCheckStationPass charSign[stationIndex] = " + charSign[stationIndex]);
            Log.d(TAG, "getPhaseCheckStationPass charItem[stationIndex] = " + charItem[charItemIndex]);
            if(charSign[stationIndex] =='0' && charItem[charItemIndex] =='0'){
                Log.d(TAG, "getPhaseCheckStationPass station pass!");
                data.recycle();
                reply.recycle();
                return true;
            }
            data.recycle();
            reply.recycle();
        }catch (Exception ex) {
            ex.printStackTrace();
        }
        return false;
    }
    public boolean isStationTest(String stat) {
        int station = getStationTest(stat);
        Log.e(TAG, "isStationTest station = " + station);
        if (station == -1) {
          return false;
        }
        boolean result = getPhaseCheckStationTested(station);
        Log.e(TAG, "isStationTest result = " + result);
        return result;
    }
    public boolean isStationPass(String stat) {
        int station = getStationTest(stat);
        Log.e(TAG, "isStationPass station = " + station);
        if (station == -1) {
          return false;
        }
        boolean result = getPhaseCheckStationPass(station);
        Log.e(TAG, "isStationPass result = " + result);
        return result;
    }
    public boolean writeLedlightSwitch(int code, int value) {
        try {
            Parcel data = new Parcel();
            Parcel reply = new Parcel();
            Log.e(TAG, "writeLedlightSwitch light code = " + code + ",value="
                    + value);
            logLedLight(code);
            data.writeInt(value);
            binder.transact(code, data, reply, 0);
            data.recycle();
            reply.recycle();
            return true;
        } catch (NullPointerException | IndexOutOfBoundsException | UnsupportedEncodingException ex) {
            Log.e(TAG, "Exception ", ex);
            return false;
        }
    }

    private void logLedLight(int code) {
        switch (code) {
        case 7:
            Log.d(TAG, "Red light!");
            break;
        case 8:
            Log.d(TAG, "Blue light!");
            break;
        case 9:
            Log.d(TAG, "Green light!");
            break;
        default:
            Log.d(TAG, "Unknow light!");
            break;
        }
    }

    static class AdaptParcel {
        int code;
        int dataSize;
        int replySize;
        byte[] data;
    }

    private static String SOCKET_NAME = "phasecheck_srv";

    class AdaptBinder {
        private LocalSocket socket = new LocalSocket();
        private LocalSocketAddress socketAddr = new LocalSocketAddress(
                SOCKET_NAME, LocalSocketAddress.Namespace.ABSTRACT);
        private OutputStream mOutputStream;
        private InputStream mInputStream;
        private AdaptParcel mAdpt;

        public AdaptBinder() {
            mAdpt = new AdaptParcel();
            mAdpt.data = new byte[BUF_SIZE];
            mAdpt.code = 0;
            mAdpt.dataSize = 0;
            mAdpt.replySize = 0;
        }

        private void int2byte(byte[] dst, int offset, int value) {
            dst[offset + 3] = (byte) (value >> 24 & 0xff);
            dst[offset + 2] = (byte) (value >> 16 & 0xff);
            dst[offset + 1] = (byte) (value >> 8 & 0xff);
            dst[offset] = (byte) (value & 0xff);
        }

        public int byte2Int(byte[] bytes, int off) {
            int b0 = bytes[off] & 0xFF;
            int b1 = bytes[off + 1] & 0xFF;
            int b2 = bytes[off + 2] & 0xFF;
            int b3 = bytes[off + 3] & 0xFF;
            return b0 | (b1 << 8) | (b2 << 16) | (b3 << 24);
        }

        public synchronized void sendCmdAndRecResult(AdaptParcel adpt) {
            Log.d(TAG, "send cmd: ");
            // LogArray(adpt.data, 19);
            byte[] buf = new byte[BUF_SIZE];
            int2byte(buf, 0, adpt.code);
            int2byte(buf, 4, adpt.dataSize);
            int2byte(buf, 8, adpt.replySize);

            // LogArray(adpt.data, 19);
            System.arraycopy(adpt.data, 0, buf, 12, adpt.dataSize
                    + adpt.replySize);
            Log.d(TAG, "code = " + adpt.code);
            Log.d(TAG, "dataSize = " + adpt.dataSize);
            Log.d(TAG, "replySize = " + adpt.replySize);
            // LogArray(buf, 19);

            try {
                socket = new LocalSocket();
                if (!socket.isConnected()) {
                    Log.d(TAG, "isConnected...");
                    socket.connect(socketAddr);
                }

                Log.d(TAG, "mSocketClient connect is " + socket.isConnected());
                mOutputStream = socket.getOutputStream();
                if (mOutputStream != null) {
                    Log.d(TAG, "write...");
                    mOutputStream.write(buf);
                    mOutputStream.flush();
                    Log.d(TAG, "write succ...");
                }
                mInputStream = socket.getInputStream();
                Log.d(TAG, "read ....");
                int count = mInputStream.read(buf, 0, BUF_SIZE);
                Log.d(TAG, "count = " + count + "");
                // LogArray(buf, 19);

                adpt.code = byte2Int(buf, 0);
                adpt.dataSize = byte2Int(buf, 4);
                adpt.replySize = byte2Int(buf, 8);

                Log.d(TAG, "code = " + adpt.code);
                Log.d(TAG, "dataSize = " + adpt.dataSize);
                Log.d(TAG, "replySize = " + adpt.replySize);

                System.arraycopy(buf, 12, adpt.data, 0, adpt.dataSize
                        + adpt.replySize);

                // LogArray(adpt.data, 19);

            } catch (IOException e) {
                Log.e(TAG, "Failed get output stream: " + e.toString());
                return;
            } finally {
                try {
                    buf = null;
                    if (mOutputStream != null) {
                        mOutputStream.close();
                    }
                    if (mInputStream != null) {
                        mInputStream.close();
                    }
                    if (socket != null) {
                        if (socket.isConnected()) {
                            socket.close();
                            socket = null;
                        } else {
                            socket = null;
                        }
                    }
                } catch (IOException e) {
                    Log.d(TAG, "catch exception is " + e);
                    return;
                }
            }
        }

        private synchronized void convertParcel(AdaptParcel adpt, int code, Parcel data,
                Parcel reply) {
            data.setDataPosition(0);
            reply.setDataPosition(0);

            //code = adpt.code;
            data.writeByteArrayInternal(adpt.data, 0, adpt.dataSize);
            reply.writeByteArrayInternal(adpt.data, adpt.dataSize,
                    adpt.replySize);

            Log.e(TAG, "convertParcel: dataSize = " + data.dataSize()
                    + ", replySize = " + reply.dataSize());
            // Log.e(TAG, "data = "+adpt.data);
            // LogArray(adpt.data, 19);

            data.setDataPosition(0);
            reply.setDataPosition(0);
        }

        private synchronized void convertAdaptParcel(int code, Parcel data, Parcel reply) {
            if (mAdpt == null) {
                Log.e(TAG, "convertAdaptParcel2: mAdpt == null!");
                return;
            }
            mAdpt.code = code;

            data.setDataPosition(0);
            reply.setDataPosition(0);

            //data.LogArray();
            byte[] bData = new byte[data.dataSize()];
            data.readByteArray(bData);
            for (int i = 0; i < data.dataSize(); i++) {
                mAdpt.data[i] = bData[i];
            }

            byte[] bReply = new byte[reply.dataSize()];
            reply.readByteArray(bReply);
            for (int i = 0; i < reply.dataSize(); i++) {
                mAdpt.data[i + data.dataSize()] = bReply[i];
            }
            mAdpt.dataSize = data.dataSize();
            mAdpt.replySize = reply.dataSize();
            Log.e(TAG, "convertAdaptParcel2: dataSize = " + data.dataSize()
                    + ", replySize = " + reply.dataSize());
            //data.LogArray();
            // LogArray(mAdpt.data, 19);

            data.setDataPosition(0);
            reply.setDataPosition(0);
        }

        public synchronized void transact(int code, Parcel data, Parcel reply, int flags)
                throws NullPointerException,IndexOutOfBoundsException, UnsupportedEncodingException {
            Log.e(TAG, "transact start....");

            // convertAdaptParcel(mAdpt, code, data, reply);
            convertAdaptParcel(code, data, reply);
            sendCmdAndRecResult(mAdpt);
            convertParcel(mAdpt, code, data, reply);

            Log.e(TAG, "transact end....");
        }
    }

    static class Parcel {
        private int mDataSize;
        private int mPos;
        private byte[] mData;

        private Parcel() {
            mData = new byte[BUF_SIZE];
            mPos = 0;
            mDataSize = 0;
        }

        public void writeByteArray(byte[] b, int offset, int len) {
            if (len == 0)
                return;
            writeInt(len);
            writeByteArrayInternal(b, offset, len);
        }

        public void writeByteArrayInternal(byte[] b, int offset, int len) {
            if (len == 0)
                return;
            System.arraycopy(b, offset, mData, mPos, len);
            mPos += len;
            mDataSize += len;
        }

        public void readByteArray(byte[] val) {
            System.arraycopy(mData, mPos, val, 0, val.length);
            mPos += val.length;
        }

        public byte readByte() {
            byte b = mData[mPos];
            mPos += 1;
            return b;
        }

        public void writeByte(byte b) {
            Log.d(TAG, "ningbiao writeByte b=" + b);
            mData[mPos] = b;
            mPos += 1;
            mDataSize += 1;
        }

        public int dataSize() {
            return mDataSize;
        }

        public void writeInt(int i) {
            Log.d(TAG, "ningbiao writeInt i=" + i);
            mData[mPos + 3] = (byte) (i >> 24 & 0xff);
            mData[mPos + 2] = (byte) (i >> 16 & 0xff);
            mData[mPos + 1] = (byte) (i >> 8 & 0xff);
            mData[mPos] = (byte) (i & 0xff);
            mPos += 4;
            mDataSize += 4;
        }

        public int readInt() {
            int b0 = mData[mPos] & 0xFF;
            int b1 = mData[mPos + 1] & 0xFF;
            int b2 = mData[mPos + 2] & 0xFF;
            int b3 = mData[mPos + 3] & 0xFF;
            mPos += 4;
            return b0 | (b1 << 8) | (b2 << 16) | (b3 << 24);
        }

        public void setDataPosition(int i) {
            mPos = i;
        }

        public String readString() throws NullPointerException,IndexOutOfBoundsException, UnsupportedEncodingException {
            int nNum = readInt();
            Log.d(TAG, "readString num = " + nNum);
            byte[] b = new byte[nNum];
            readByteArray(b);

            return new String(b, 0, nNum, "utf-8").trim();
        }

        public void recycle() {
            reset();
        }

        public void reset() {
            mPos = 0;
            mDataSize = 0;
        }

        public void LogArray() {
            Log.e(TAG, "array length = " + mData.length);
            for (int i = 0; i < mData.length; i++) {
                if (i > 19)
                    break;
                Log.e(TAG, "Parcel LogArray : (" + i + ") = " + mData[i]);
            }
        }
    }
}
