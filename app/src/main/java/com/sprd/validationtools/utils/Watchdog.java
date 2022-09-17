package com.sprd.validationtools.utils;

import android.os.Handler;
import android.util.Log;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.function.Consumer;

public class Watchdog {
    public static final String TAG = "SocketW";
    private volatile int food = 0;
    private String server = "SERVER";
    private String cmd = "CMD";
    private static final int WATCHDOG_TIME=180;
    private ExecutorService es;
    private Future<?> future;
    private Consumer consumer;
    private Object consumerObj;

    public Watchdog(String s, String c) {
        server = s;
        cmd = c;
    }

    void wantEat() {
        if(null==cmd){
            Log.d(TAG,   "invalid watching  " +server+" ["+cmd+"]");
            return;
        }
        food = 0;
        es = Executors.newSingleThreadExecutor();
        future = es.submit( () -> {
            try {
                int count=0;
                while(count<WATCHDOG_TIME){
                    if (1 == food){
                        return;
                    }
                    Thread.sleep(1* 1000);
                    count++;
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
                return;
            }

            if (0 == food) {
//                    try {
                    if ((cmd != null)
                        && (cmd.indexOf("apdumper") != -1
                        || cmd.contains("tar")
                        || cmd.contains("rm -rf") || cmd
                        .contains("rylogr")|| cmd.contains("clear"))) {
                        return;
                    }
                    if (consumer != null) {
                        consumer.accept(consumerObj);
                    }
            }
        });
    }

    public void feedFood() {
        food = 1;
        if(null != future){
            future.cancel(true);
        }
    }

    public void setTimeoutCallback(Consumer<Object> callback, Object obj) {
        consumer = callback;
        consumerObj = obj;
    }
}
