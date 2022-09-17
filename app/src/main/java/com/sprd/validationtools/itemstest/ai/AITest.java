package com.sprd.validationtools.itemstest.ai;

import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.SystemProperties;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import com.simcom.testtools.R;
import com.sprd.validationtools.BaseActivity;
import com.sprd.validationtools.PhaseCheckParse;

public class AITest extends BaseActivity {
    private static final String TAG = "AITest";
    private static final String AI_IP_PROPERTY = "persist.vendor.npu.version";

    private static final int AI_TEST_RUNNING = 1;
    private static final int AI_TEST_FINISH = 2;
    private static final int AI_TEST_SUCCESS = 3;
    private static final int AI_TEST_FAIL = 4;

    private TextView mStateTextView = null;
    private PhaseCheckParse mPhaseCheckParse = null;
    private boolean mTestSuccess = false;
    private int AiIpValue = 0;

    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch(msg.what) {
                case AI_TEST_RUNNING:
                    Log.d(TAG, "AI_TEST_RUNNING...");
                    mStateTextView.setText("Running......");
                    break;
                case AI_TEST_FINISH:
                    int val = msg.arg1;
                    Log.d(TAG, "val=:" + val);
                    mStateTextView.setText("Finished!");
                    if (val == AI_TEST_SUCCESS) {
                        mStateTextView.setTextColor(Color.GREEN);
                        Toast.makeText(AITest.this, "Success!", Toast.LENGTH_SHORT).show();
                    } else if (val == AI_TEST_FAIL) {
                        mStateTextView.setTextColor(Color.RED);
                        Toast.makeText(AITest.this, "Failed!", Toast.LENGTH_SHORT).show();
                    }
                    this.postDelayed(mRunnable, 20000L);
                    break;
            }
        }
    };

    private Runnable mRunnable = new Runnable(){
        @Override
        public void run() {
            storeRusult(false);
            Toast.makeText(AITest.this, "Timeout!", Toast.LENGTH_SHORT).show();
            finish();
        }
    };

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "onCreate");
        setContentView(R.layout.activity_ai_test);
        mStateTextView = (TextView) findViewById(R.id.state_textview);

        /* 0:Not Imagination nna Nor Cambricon npu used; 1:Imagination nna used; 2:Cambricon npu used. */
        AiIpValue = Integer.parseInt(SystemProperties.get(AI_IP_PROPERTY, "0"));
        Log.d(TAG, "AiIpValue=:" + AiIpValue);
        mPhaseCheckParse = PhaseCheckParse.getInstance();
    }

    @Override
    protected void onResume() {
        Log.d(TAG, "onResume");

        mHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                startAiTest();
            }
        }, 50L);

        super.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        mHandler.removeCallbacks(mRunnable);
    }

    protected void startAiTest() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                Message message1 = Message.obtain();
                message1.what = AI_TEST_RUNNING;
                mHandler.sendMessageDelayed(message1, 0L);

                int result = executeAICmd();
                Log.d(TAG, "result=:" + result);
                Message message2 = Message.obtain();
                message2.what = AI_TEST_FINISH;
                if (result == 1) {
                    message2.arg1 = AI_TEST_SUCCESS;
                    mTestSuccess = true;
                } else {
                    message2.arg1 = AI_TEST_FAIL;
                    mTestSuccess = false;
                }
                mHandler.sendMessageDelayed(message2, 1000L);
            }
        }).start();
    }

    protected int executeAICmd() {
        int result = mPhaseCheckParse.executeAITest(AiIpValue);
        return result;
    }
}