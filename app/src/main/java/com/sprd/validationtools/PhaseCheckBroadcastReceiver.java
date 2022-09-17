package com.sprd.validationtools;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.util.Log;

public class PhaseCheckBroadcastReceiver extends BroadcastReceiver {
    private static final String TAG = "PhaseCheckBroadcastReceiver";

    private static final String ACTION_SAVE_PHASECHECK = "com.sprd.validationtools.SAVE_PHASECHECK";
    private static final String SAVE_PHASECHECK_STATION_NAME = "station_name";
    private static final String SAVE_PHASECHECK_RESULT = "station_result";

    static class SavePhasecheckAsyncTask extends AsyncTask<String, Void, Void> {
        private String station = "";
        private String result = "";

        @Override
        protected Void doInBackground(String... params) {
            if (params != null && params.length >= 2) {
                station = params[0];
                result = params[1];
                Log.d(TAG, "doInBackground station = " + station + ",result="
                        + result);
                if ("0".equals(result)) {
                    PhaseCheckParse.getInstance().writeStationTested(station);
                    PhaseCheckParse.getInstance().writeStationPass(station);
                } else if ("1".equals(result)) {
                    PhaseCheckParse.getInstance().writeStationTested(station);
                    PhaseCheckParse.getInstance().writeStationFail(station);
                }
            }
            return null;
        }
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();
        Log.d(TAG, "onReceive action = " + action);
        if (action == null)
            return;
        if (action.equals(ACTION_SAVE_PHASECHECK)) {
            String stationName = intent
                    .getStringExtra(SAVE_PHASECHECK_STATION_NAME);
            String stationResult = intent
                    .getStringExtra(SAVE_PHASECHECK_RESULT);
            Log.d(TAG, "onReceive stationName = " + stationName
                    + ",stationResult=" + stationResult);
            SavePhasecheckAsyncTask asyncTask = new SavePhasecheckAsyncTask();
            asyncTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR,
                    stationName, stationResult);
        }
    }
}
