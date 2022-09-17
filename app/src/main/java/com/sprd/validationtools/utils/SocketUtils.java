package com.sprd.validationtools.utils;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;

import android.net.LocalSocket;
import android.net.LocalSocketAddress;
import android.net.LocalSocketAddress.Namespace;
import android.util.Log;

public class SocketUtils {

    private static final String TAG = "SocketUtils";
    public static final String OK = "OK";
    public static final String FAIL = "FAIL";

//    String mSocketName = null;
//    LocalSocket mSocketClient = null;
//    OutputStream mOutputStream;
//    InputStream mInputStream;
//    LocalSocketAddress mSocketAddress;

    public static synchronized String sendByteAndRecResult(String socketName, Namespace namespace,byte[] sendBuf,int rsplen) {
        Log.d(TAG, "sendByteAndRecResult rsplen="+rsplen);
        if(rsplen <= 0){
            Log.d(TAG, "sendByteAndRecResult len == 0");
            return null;
        }
        String result = null;
        byte[] rspBuff = new byte[rsplen];
        String mSocketName = null;
        LocalSocket mSocketClient = null;
        OutputStream mOutputStream = null;
        InputStream mInputStream = null;
        LocalSocketAddress mSocketAddress = null;

        try {
            mSocketClient = new LocalSocket();
            mSocketName = socketName;
            mSocketAddress = new LocalSocketAddress(mSocketName, namespace);
            if (!mSocketClient.isConnected()) {
                Log.d(TAG, "isConnected...");
                mSocketClient.connect(mSocketAddress);
            }
            // mSocketClient.connect(mSocketAddress);
            Log.d(TAG, "mSocketClient connect is " + mSocketClient.isConnected());
            mOutputStream = mSocketClient.getOutputStream();
            if (mOutputStream != null) {
                mOutputStream.write(sendBuf);
                mOutputStream.flush();
            }
            mInputStream = mSocketClient.getInputStream();
            int count = mInputStream.read(rspBuff, 0, rsplen);
            result = new String(rspBuff, "utf-8");
            Log.d(TAG, "count = " + count + ", result is " + result);
        } catch (IOException e) {
            Log.e(TAG, "Failed get output stream: " + e.toString());
            return null;
        } finally {
            try {
                rspBuff = null;
                if (mOutputStream != null) {
                    mOutputStream.close();
                }
                if (mInputStream != null) {
                    mInputStream.close();
                }
                if (mSocketClient != null) {
                    mSocketClient.close();
                    mSocketClient = null;
                }
            } catch (IOException e) {
                Log.d(TAG, "catch exception is " + e);
                return null;
            }
        }
        return result;
    }

    public static synchronized byte[] sendByteAndRecResult2(String socketName, Namespace namespace,byte[] sendBuf,int rsplen) {
        String result = null;
        byte[] rspBuff = new byte[4096];
        String mSocketName = null;
        LocalSocket mSocketClient = null;
        OutputStream mOutputStream = null;
        InputStream mInputStream = null;
        LocalSocketAddress mSocketAddress = null;
        try {
            mSocketClient = new LocalSocket();
            mSocketName = socketName;
            mSocketAddress = new LocalSocketAddress(mSocketName, namespace);
            if (!mSocketClient.isConnected()) {
                Log.d(TAG, "isConnected...");
                mSocketClient.connect(mSocketAddress);
            }
            // mSocketClient.connect(mSocketAddress);
            Log.d(TAG, "mSocketClient connect is " + mSocketClient.isConnected());
            mOutputStream = mSocketClient.getOutputStream();
            if (mOutputStream != null) {
                mOutputStream.write(sendBuf);
                mOutputStream.flush();
            }
            mInputStream = mSocketClient.getInputStream();
            int count = mInputStream.read(rspBuff, 0, 4096);
            result = new String(rspBuff, 0, count, "utf-8");
            Log.d(TAG, "count = " + count + ", result is " + result);
            if(count > 0 && count < 4096){
                byte[] rspBuff2 = new byte[count];
                for(int i=0;i<count;i++){
                    rspBuff2[i] = rspBuff[i];
                }
                return rspBuff2;
            }else{
                return null;
            }
        } catch (IOException e) {
            Log.e(TAG, "Failed get output stream: " + e.toString());
            return null;
        } finally {
            try {
                rspBuff = null;
                if (mOutputStream != null) {
                    mOutputStream.close();
                }
                if (mInputStream != null) {
                    mInputStream.close();
                }
                if (mSocketClient != null) {
                    mSocketClient.close();
                    mSocketClient = null;
                }
            } catch (IOException e) {
                Log.d(TAG, "catch exception is " + e);
                return null;
            }
        }
    }

    public static synchronized String sendCmdAndRecResult(String socketName, Namespace namespace,
            String strcmd) {
        Log.d(TAG, "send cmd: " + strcmd);
        byte[] buf = new byte[255];
        String result = null;
        Log.d(TAG, "set cmd: " + strcmd);
        String mSocketName = null;
        LocalSocket mSocketClient = null;
        OutputStream mOutputStream = null;
        InputStream mInputStream = null;
        LocalSocketAddress mSocketAddress = null;
        try {
            mSocketClient = new LocalSocket();
            mSocketName = socketName;
            mSocketAddress = new LocalSocketAddress(mSocketName, namespace);
            if (!mSocketClient.isConnected()) {
                Log.d(TAG, "isConnected...");
                mSocketClient.connect(mSocketAddress);
            }
            // mSocketClient.connect(mSocketAddress);
            Log.d(TAG, "mSocketClient connect is " + mSocketClient.isConnected());
            mOutputStream = mSocketClient.getOutputStream();
            if (mOutputStream != null) {
                final StringBuilder cmdBuilder = new StringBuilder(strcmd).append('\0');
                final String cmd = cmdBuilder.toString();
                mOutputStream.write(cmd.getBytes(StandardCharsets.UTF_8));
                mOutputStream.flush();
            }
            mInputStream = mSocketClient.getInputStream();
            int count = mInputStream.read(buf, 0, 255);
            result = "";
            result = new String(buf, "utf-8");
            Log.d(TAG, "count = " + count + ", result is " + result);
        } catch (IOException e) {
            Log.e(TAG, "Failed get output stream: " + e.toString());
            return null;
        } finally {
            try {
                buf = null;
                if (mOutputStream != null) {
                    mOutputStream.close();
                }
                if (mInputStream != null) {
                    mInputStream.close();
                }
                if (mSocketClient != null) {
                    mSocketClient.close();
                    mSocketClient = null;
                }
            } catch (IOException e) {
                Log.d(TAG, "catch exception is " + e);
                return null;
            }
        }
        return result;
    }

    public static String sendCmdAndRecResult(String socketName, Namespace namespace,
            String strcmd, int time) {
        while (time-- != 0) {
            String tmp = sendCmdAndRecResult(socketName, namespace, strcmd);
            if (tmp != null)
                return tmp;
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            Log.d(TAG, "try again" + time);
        }
        return null;
    }

    public static String sendCmd(String socketName, String cmd) {

        Thread thread = Thread.currentThread();
        String caller = Thread.currentThread().getStackTrace()[2].getClassName();
        String strcmd =
                String.format("[%s][%d]<%s>", caller, thread.getId(), cmd.replace('\n', '\\'));
        Log.d(TAG, socketName + " send cmd: " + strcmd);

        LocalSocket socketClient = new LocalSocket();
        LocalSocketAddress mSocketAddress =
                new LocalSocketAddress(socketName, Namespace.ABSTRACT);

        OutputStream ops = null;
        InputStream ins = null;
        byte[] buf = new byte[1024];
        String result = null;
        Watchdog wd = null;
        try {
            socketClient.connect(mSocketAddress);

            Log.i(TAG, strcmd + "connect " + socketName + " success");
            ops = socketClient.getOutputStream();
            ins = socketClient.getInputStream();

//            ops.write(getBytes(cmd));
            ops.write(cmd.getBytes(StandardCharsets.UTF_8));
            ops.flush();
            Log.d(TAG, strcmd + " write cmd done , flush data done");
            //socketClient.setSoTimeout(30);

            wd = new Watchdog(socketName, cmd);
            wd.setTimeoutCallback(SocketUtils::timeout, socketClient);
            wd.wantEat();
            int count = ins.read(buf, 0, 1024);
            wd.feedFood();
            Log.d(TAG, strcmd + " result read done");
            result = "read count is -1";
            if (count != -1) {
                byte[] temp = new byte[count];
                System.arraycopy(buf, 0, temp, 0, count);
                result = new String(temp, StandardCharsets.UTF_8);
            } else {
                Log.e(TAG, strcmd + " read failed");
            }

            Log.d(TAG, strcmd + "count = " + count + ", result is " + result);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (wd != null) {
                wd.feedFood();
            }

            try {
                if (ops != null) {
                    ops.close();
                }
                if (ins != null) {
                    ins.close();
                }
                socketClient.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        Log.d(TAG, strcmd + "handle over and result is :" + result);
        return result;
    }

    private static void timeout(Object obj) {
        LocalSocket socket = (LocalSocket) obj;
        try {
            socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
