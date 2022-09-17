package com.sprd.validationtools.itemstest.camera;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;

import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.sprd.validationtools.BaseActivity;
import com.sprd.validationtools.PhaseCheckParse;
import com.simcom.testtools.R;

public class TofCalibrationTest extends BaseActivity {
    private static final String TAG = "TofCalibrationTest";
    private static final String mOffsetCalibrationFile = "/mnt/vendor/vl53l0_offset_calibration.file";
    private static final String mCrossTakCalibrationFile = "/mnt/vendor/vl53l0_xtak_calibration.file";

    private TextView mSpadsCalibrationTV = null;
    private TextView mRefCalibrationTV = null;
    private TextView mOffsetCalibrationTV = null;
    private TextView mCrossTalkCalibrationTV = null;
    private TextView mOffsetCalibrationJudgeTV = null;
    private TextView mCrossTalkCalibrationJudgeTV = null;

    private Button mOffsetStartBtn = null;
    private Button mCrossTalkStartBtn = null;
    private Handler mHandler = new Handler();

    private String mSpadsResult = null;
    private String mRefResult = null;
    private String mOffsetResult = null;
    private String mCrossTalkResult = null;

    private boolean mIsOffsetTestPass = false;
    private boolean mIsCrossTalkTestPass = false;
    private boolean mHasOffsetTested = false;
    private boolean mHasXTalkTested = false;

    private PhaseCheckParse mPhaseCheckParse = null;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "onCreate");
        setContentView(R.layout.activity_tof_test);

        mSpadsCalibrationTV = (TextView) findViewById(R.id.spads_calibration_tv);
        mRefCalibrationTV = (TextView) findViewById(R.id.ref_calibration_tv);
        mOffsetCalibrationTV = (TextView) findViewById(R.id.offset_calibration_tv);
        mCrossTalkCalibrationTV = (TextView) findViewById(R.id.cross_talk_calibration_tv);
        mOffsetCalibrationJudgeTV = (TextView) findViewById(R.id.offset_calibration_judge_tv);
        mCrossTalkCalibrationJudgeTV = (TextView) findViewById(R.id.cross_talk_calibration_judge_tv);

        mOffsetStartBtn = (Button) findViewById(R.id.offset_start_btn);
        mCrossTalkStartBtn = (Button) findViewById(R.id.cross_talk_start_btn);

        mOffsetStartBtn.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(TofCalibrationTest.this, "Offset Calibration is Testing, Please Wait!", Toast.LENGTH_SHORT).show();
                executeOffsetCalibration();
            }
        });

        mCrossTalkStartBtn.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(TofCalibrationTest.this, "Cross-talk Calibration is Testing, Please Wait!", Toast.LENGTH_SHORT).show();
                executeCrossTalkCalibration();
            }
        });

        mOffsetCalibrationJudgeTV.setText("Not Test");
        mCrossTalkCalibrationJudgeTV.setText("Not Test");
        mPhaseCheckParse = PhaseCheckParse.getInstance();
    }

    public void executeOffsetCalibration () {
        Log.d(TAG, "executeOffsetCalibration run");
        mHasOffsetTested = true;
        new Thread(new Runnable() {
            @Override
            public void run() {
                Log.d(TAG, "executeOffsetCalibration thread run ");
                int offsetResult = mPhaseCheckParse.executeOffsetCalibration();
                if (offsetResult == 1) {
                    Log.d(TAG, "executeOffsetCalibration success!");
                } else {
                    Log.d(TAG, "executeOffsetCalibration failsed!");
                }
            }
        }).start();

        mHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                Log.d(TAG, "executeCrossTalkCalibration run");
                setOffsetCalibrationParams(mOffsetCalibrationFile);

                mSpadsCalibrationTV.setText(getString(R.string.spads_calibration_title) + mSpadsResult);
                mRefCalibrationTV.setText(getString(R.string.ref_calibration_title) + mRefResult);
                mOffsetCalibrationTV.setText(getString(R.string.offset_calibration_title) + mOffsetResult);

                mIsOffsetTestPass = judgeOffsetCalibrationPass();
                if (mIsOffsetTestPass) {
                    mOffsetCalibrationJudgeTV.setText("Pass");
                    mOffsetCalibrationJudgeTV.setTextColor(Color.GREEN);
                } else {
                    mOffsetCalibrationJudgeTV.setText("Fail");
                    mOffsetCalibrationJudgeTV.setTextColor(Color.RED);
                }

                if (mHasOffsetTested && mHasXTalkTested) {
                    mHandler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            if (mIsOffsetTestPass && mIsCrossTalkTestPass) {
                                judgeTofTestPass();
                            } else {
                                judgeTofTestFail();
                            }
                        }
                    }, 3000L);
                }
            }
        }, 3000L);
    }

    public void executeCrossTalkCalibration() {
        Log.d(TAG, "executeCrossTalkCalibration");
        mHasXTalkTested = true;
        new Thread(new Runnable() {
            @Override
            public void run() {
                Log.d(TAG, "executeCrossTalkCalibration thread run");
                int xtalkResult = mPhaseCheckParse.executeXtalkCalibration();
                if (xtalkResult == 1) {
                    Log.d(TAG, "execute_xtalk_calibration success!");
                } else {
                    Log.d(TAG, "execute_xtalk_calibration failsed!");
                }
            }
        }).start();

        mHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                Log.d(TAG, "executeCrossTalkCalibration run");

                setCrossTalkCalibrationParams(mCrossTakCalibrationFile);
                mCrossTalkCalibrationTV.setText(getString(R.string.cross_talk_calibration_title) + mCrossTalkResult);

                mIsCrossTalkTestPass = judgeCrossTalkCalibrationPass();
                if (mIsCrossTalkTestPass) {
                    mCrossTalkCalibrationJudgeTV.setText("Pass");
                    mCrossTalkCalibrationJudgeTV.setTextColor(Color.GREEN);
                } else {
                    mCrossTalkCalibrationJudgeTV.setText("Fail");
                    mCrossTalkCalibrationJudgeTV.setTextColor(Color.RED);
                }

                if (mHasOffsetTested && mHasXTalkTested) {
                    mHandler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            if (mIsOffsetTestPass && mIsCrossTalkTestPass) {
                                judgeTofTestPass();
                            } else {
                                judgeTofTestFail();
                            }
                        }
                    }, 3000L);
                }
            }
        }, 3000L);
    }

    public void judgeTofTestPass() {
        Log.d(TAG, "executeTofTestPass");

        Toast.makeText(TofCalibrationTest.this, "Tof Tested Pass!", Toast.LENGTH_SHORT).show();
    }

    public void judgeTofTestFail() {
        Log.d(TAG, "judgeTofTestFail");

        Toast.makeText(TofCalibrationTest.this, "Tof Tested Failed!", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    public void setOffsetCalibrationParams(String filePath){
        Log.d(TAG, "setOffsetCalibrationParams");
        String paramsValue = readFile(filePath);
        if (paramsValue == null) {
            Toast.makeText(TofCalibrationTest.this, "Read Offset Calibration Information Failed!", Toast.LENGTH_SHORT).show();
            return;
        }

        String[] params = paramsValue.split("\n");
        mOffsetResult = params[0];
        mRefResult = params[1] + "\t" + params[2];
        mSpadsResult = params[3];
    }

    public boolean judgeOffsetCalibrationPass(){
        Log.d(TAG, "judgeOffsetCalibrationPass");
        if (mOffsetResult == null) {
            return false;
        }
        int offsetValue = Integer.parseInt(mOffsetResult);
        Log.d(TAG, "offsetValue=:" + offsetValue);
        if (offsetValue >= -20000 && offsetValue <= 60000) {
            return true;
        } else {
            return false;
        }
    }

    public void setCrossTalkCalibrationParams(String filePath){
        Log.d(TAG, "setCrossTalkCalibrationParams");
        String paramValue = readFile(filePath);
        Log.d(TAG, "paramValue=:" + paramValue);
        if (paramValue == null) {
            Toast.makeText(TofCalibrationTest.this, "Read CrossTalk Calibration Information Failed!", Toast.LENGTH_SHORT).show();
            return;
        }

        String[] params = paramValue.split("\n");
        mCrossTalkResult = params[0];
    }

    public boolean judgeCrossTalkCalibrationPass(){
        Log.d(TAG, "judgeCrossTalkCalibrationPass");
        if (mCrossTalkResult == null) {
            return false;
        }
        int crossTalkValue = Integer.parseInt(mCrossTalkResult);
        int judgeValue = (int) (((double)crossTalkValue/65536) * 1000);
        if (judgeValue >= 0 && judgeValue <= 700) {
            return true;
        } else {
            return false;
        }
    }

    public String readFile(String filePath) {
        Log.d(TAG, "readFile");
        char[] buffer = new char[1024];
        FileReader file = null;
        String result = null;
        try {
            file = new FileReader(filePath);
            int len = file.read(buffer, 0, 1024);
            result = new String(buffer, 0, len);
            Log.d(TAG, "result is: " + result);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (file != null) {
                    file.close();
                    file = null;
                }
            } catch (IOException io) {
                io.printStackTrace();
            }
        }
        return result;
    }
}
