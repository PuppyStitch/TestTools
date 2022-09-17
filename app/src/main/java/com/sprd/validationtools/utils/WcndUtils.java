package com.sprd.validationtools.utils;

import android.net.LocalSocketAddress;
import android.os.AsyncTask;

public class WcndUtils {

    private static final String TAG = "WcndUtils";
    private static final String SOCKET_NAME = "wcnd";
    private static final String CMD_FLUSHLOG = "wcn at+flushwcnlog\0";

    private static final String SOCKET_NAME_SLOGMODEM = "slogmodem";
    private static final String CMD_FLUSHLOG_SLOGMODEM = "SAVE_LAST_LOG WCN";

    public synchronized static String sendWcndCmdFlushLog() {
        return SocketUtils.sendCmdAndRecResult(SOCKET_NAME,
                LocalSocketAddress.Namespace.ABSTRACT, CMD_FLUSHLOG, 3);
    }
    public synchronized static String sendSlogModemCmdFlushLog() {
        return SocketUtils.sendCmdAndRecResult(SOCKET_NAME_SLOGMODEM,
                LocalSocketAddress.Namespace.ABSTRACT, CMD_FLUSHLOG_SLOGMODEM, 3);
    }

    public static final void dumpCPLog(){
        AsyncTask<Void, Void, Void> asyncTask = new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... params) {
                WcndUtils.sendWcndCmdFlushLog();
                WcndUtils.sendSlogModemCmdFlushLog();
                return null;
            }
        };
        asyncTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, null,
                null, null);
    }
}
