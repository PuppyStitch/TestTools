
package com.sprd.validationtools.itemstest.audio;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.media.AudioFormat;
import android.media.AudioManager;
import android.media.AudioRecord;
import android.media.AudioSystem;
import android.media.AudioTrack;
import android.media.MediaRecorder;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.WindowManager;
import android.view.View;
import android.widget.Toast;
import android.widget.RadioButton;
import android.provider.Settings;
import android.media.AudioManager;

import com.simcom.testtools.R;
import com.sprd.validationtools.BaseActivity;
import com.sprd.validationtools.utils.IATUtils;
import com.sprd.validationtools.utils.Native;
import com.sprd.validationtools.utils.ValidationToolsUtils;
import com.sprd.validationtools.Const;


public class PhoneLoopBackTest extends BaseActivity {
    private static final String TAG = "PhoneLoopBackTest";
    public byte mPLBTestFlag[] = new byte[1];
    public Handler mUihandler = new Handler();
    private RadioButton mRadioSpeaker = null;
    private RadioButton mRadioReceiver = null;
    private static final int LOOPBACK_NONE = 0;
    private static final int LOOPBACK_SPEAKER = 1;
    private static final int LOOPBACK_RECEIVER = 2;
    private int mCurLoopback = LOOPBACK_NONE;
    private AudioManager mAudioManager = null;

    private Object mLock = new Object();

    private boolean isWhaleSupport = Const.isWhale2Support() || Const.isIWhale2Support();
    private boolean mAudioWhaleHalFlag = false;
    private boolean mPendingPaused = false;
    private boolean mRadioOpenSuccess = false;
    private boolean mIsRefMicSupport = Const.isRefMicSupport();

    private boolean mSavedSoundEffect = false;
    private boolean mSavedLockSound = false;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON,
                WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        setContentView(R.layout.micphone_test);
        setTitle(R.string.phone_loopback_test);
        mAudioManager = (AudioManager) this
                .getSystemService(Context.AUDIO_SERVICE);
        mRadioSpeaker = (RadioButton) findViewById(R.id.radio_speaker);
        mRadioReceiver = (RadioButton) findViewById(R.id.radio_earpiece);

        /* SPRD Bug 770367:the prompt of the PhoneLoopBack test item is wrong. @{ */
        if (Const.isBoardISharkL210c10()) {
            mRadioSpeaker.setText(R.string.phoneloopback_speaker_10c10);
        }
        /* @} */

        mRadioSpeaker.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                switchLoopback(LOOPBACK_SPEAKER);
            }
        });
        mRadioReceiver.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                switchLoopback(LOOPBACK_RECEIVER);
            }
        });
        if (AudioSystem.DEVICE_STATE_AVAILABLE != AudioSystem
                .getDeviceConnectionState(AudioManager.DEVICE_OUT_EARPIECE, "")) {
            mRadioReceiver.setVisibility(View.GONE);
        }
        /*SPRD bug 850400:Aux device check.*/
        boolean mAuxDeviceSupport = true;
        if (AudioSystem.DEVICE_STATE_AVAILABLE != AudioSystem
                .getDeviceConnectionState(AudioManager.DEVICE_IN_BACK_MIC, "")) {
            mAuxDeviceSupport = false;
        }
        Log.d(TAG, "onCreate mAuxDeviceSupport="+mAuxDeviceSupport);
        mAudioWhaleHalFlag = ValidationToolsUtils.isSupportAGDSP(getApplicationContext());
        Log.d(TAG, "onCreate mAudioWhaleHalFlag="+mAudioWhaleHalFlag);
        mRadioReceiver.setVisibility(mIsRefMicSupport && mAuxDeviceSupport ? View.VISIBLE : View.GONE);
        /*@}*/
        mSavedSoundEffect = ValidationToolsUtils.setSoundEffect(this, false);
        mSavedLockSound = ValidationToolsUtils.setLockSound(this, false);
    }

    @Override
    protected void onResume() {
        super.onResume();
        mPendingPaused = false;
        startMmiAudio(mCurLoopback);
    }

    @Override
    protected void onPause() {
        if(mRadioOpenSuccess) {
            rollbackMmiAudio(mCurLoopback);
        }
        mPendingPaused = true;
        super.onPause();
    }

    private void switchLoopback(int loopbackType) {
        Log.i("PhoneLoopBackTest",
                "=== create thread to execute PhoneLoopBackTest switch command! ===");
        mRadioReceiver.setEnabled(false);
        mRadioSpeaker.setEnabled(false);
        if (!isWhaleSupport && !mAudioWhaleHalFlag) {
            if (loopbackType == LOOPBACK_RECEIVER) {
                new Thread() {
                    public void run() {
                        // String result = IATUtils.sendATCmd("AT+SSAM=0",
                        // "atchannel0");
                        String result = IATUtils.sendAtCmd("AT+SSAM=0");
                        setInMac(LOOPBACK_RECEIVER);
                        if (result.contains(IATUtils.AT_OK)) {
                            mUihandler.post(new Runnable() {
                                public void run() {
                                    mRadioSpeaker.setEnabled(true);
                                    mCurLoopback = LOOPBACK_RECEIVER;
                                    mRadioReceiver.setChecked(true);
                                    mRadioOpenSuccess = true;
                                    if(mPendingPaused) {
                                        rollbackMmiAudio(mCurLoopback);
                                    } else {
                                        Toast.makeText(
                                                PhoneLoopBackTest.this,
                                                R.string.receiver_loopback_success,
                                                Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });
                        } else {
                            mUihandler.post(new Runnable() {
                                public void run() {
                                    if(!mPendingPaused) {
                                        Toast.makeText(
                                                PhoneLoopBackTest.this,
                                                R.string.receiver_loopback_fail,
                                                Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });
                        }
                    }
                }.start();
            } else {
                new Thread() {
                    public void run() {
                        // String result = IATUtils.sendATCmd("AT+SSAM=1",
                        // "atchannel0");
                        String result = IATUtils.sendAtCmd("AT+SSAM=1");
                        setInMac(LOOPBACK_SPEAKER);
                        if (result.contains(IATUtils.AT_OK)) {
                            mUihandler.post(new Runnable() {
                                public void run() {
                                    mRadioReceiver.setEnabled(true);
                                    mCurLoopback = LOOPBACK_SPEAKER;
                                    mRadioSpeaker.setChecked(true);
                                    mRadioOpenSuccess = true;
                                    if(mPendingPaused) {
                                        rollbackMmiAudio(mCurLoopback);
                                    } else {
                                        Toast.makeText(
                                                PhoneLoopBackTest.this,
                                                R.string.speaker_loopback_success,
                                                Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });
                        } else {
                            mUihandler.post(new Runnable() {
                                public void run() {
                                    if(!mPendingPaused) {
                                        Toast.makeText(PhoneLoopBackTest.this,
                                                R.string.speaker_loopback_fail,
                                                Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });
                        }
                    }
                }.start();
            }
        } else {
            if (loopbackType == LOOPBACK_RECEIVER) {
                new Thread() {
                    public void run() {
                        Log.d(TAG, "audio whale loopback, LOOPBACK_RECEIVER");
                        mAudioManager.setParameter("dsp_loop", "0");
                        mAudioManager.setParameter("test_out_stream_route",
                                "0x2");
                        mAudioManager.setParameter("test_in_stream_route",
                                "0x80000080");
                        mAudioManager.setParameter("dsploop_type", "1");
                        mAudioManager.setParameter("dsp_loop", "1");
                        mRadioOpenSuccess = true;
                        mUihandler.post(new Runnable() {
                            public void run() {
                                mRadioSpeaker.setEnabled(true);
                                mCurLoopback = LOOPBACK_RECEIVER;
                                mRadioReceiver.setChecked(true);
                                if(!mPendingPaused) {
                                    Toast.makeText(
                                            PhoneLoopBackTest.this,
                                            R.string.receiver_loopback_success,
                                            Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
                    }
                }.start();

            } else {
                new Thread() {
                    public void run() {
                        Log.d(TAG, "audio whale loopback, LOOPBACK_SPEAKER");
                        mAudioManager.setParameter("dsp_loop", "0");
                        mAudioManager.setParameter("test_out_stream_route",
                                "0x2");
                        mAudioManager.setParameter("test_in_stream_route",
                                "0x80000004");
                        mAudioManager.setParameter("dsploop_type", "1");
                        mAudioManager.setParameter("dsp_loop", "1");
                        mRadioOpenSuccess = true;
                        mUihandler.post(new Runnable() {
                            public void run() {
                                mRadioReceiver.setEnabled(true);
                                mCurLoopback = LOOPBACK_SPEAKER;
                                mRadioSpeaker.setChecked(true);
                                if(!mPendingPaused) {
                                    Toast.makeText(PhoneLoopBackTest.this,
                                            R.string.speaker_loopback_success,
                                            Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
                    }
                }.start();

            }
        }

    }

    private boolean setInMac(int loopbackType) {
        String result = null;
        if (loopbackType == LOOPBACK_SPEAKER) {
            // result = IATUtils.sendATCmd("AT+SPVLOOP=4,,,,,,2,1",
            // "atchannel0");
            /*SPRD Bug 760989:the main mic loopback request a circuit test with receiver. @{*/
            if (Const.isBoardISharkL210c10()) {
                result = IATUtils.sendAtCmd("AT+SPVLOOP=4,,,,,,1,1");
            } else {
                result = IATUtils.sendAtCmd("AT+SPVLOOP=4,,,,,,2,1");
            }
            /*@}*/
        } else {
            // result = IATUtils.sendATCmd("AT+SPVLOOP=4,,,,,,2,2",
            // "atchannel0");
            result = IATUtils.sendAtCmd("AT+SPVLOOP=4,,,,,,2,2");
        }

        // if (result != null && result.length() >= 0 &&
        // result.contains(IATUtils.AT_OK)) {
        if (result != null && result.length() >= 0
                && IATUtils.AT_OK.contains(result)) {
            return true;
        }
        return false;
    }

    private void startMmiAudio(int loopbackType) {
        Log.i("PhoneLoopBackTest",
                "=== create thread to execute PhoneLoopBackTest start command! ===");
        mRadioReceiver.setEnabled(false);
        mRadioSpeaker.setEnabled(false);
        if (!isWhaleSupport && !mAudioWhaleHalFlag) {
            if (loopbackType == LOOPBACK_RECEIVER) {
                new Thread() {
                    public void run() {
                        synchronized (mLock) {
                            try {
                                sleep(1500);
                            } catch (Exception e) {

                            }
                            // String result =
                            // IATUtils.sendATCmd("AT+SPVLOOP=1,0,8,2,3,0",
                            // "atchannel0");
                            String result = IATUtils
                                    .sendAtCmd("AT+SPVLOOP=1,0,8,2,3,0");
                            setInMac(LOOPBACK_RECEIVER);
                            if (result.contains(IATUtils.AT_OK)) {
                                mUihandler.post(new Runnable() {
                                    public void run() {
                                        mRadioSpeaker.setEnabled(true);
                                        mCurLoopback = LOOPBACK_RECEIVER;
                                        mRadioReceiver.setChecked(true);
                                        mRadioOpenSuccess = true;
                                        if(mPendingPaused) {
                                            rollbackMmiAudio(mCurLoopback);
                                        } else {
                                            Toast.makeText(PhoneLoopBackTest.this,
                                                    R.string.receiver_loopback_open,
                                                    Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                });
                            } else {
                                mUihandler.post(new Runnable() {
                                    public void run() {
                                        if(!mPendingPaused) {
                                            Toast.makeText(PhoneLoopBackTest.this,
                                                    R.string.receiver_loopback_open_fail,
                                                    Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                });
                            }
                        }
                    }
                }.start();
            } else {
                new Thread() {
                    public void run() {
                        try {
                            sleep(1500);
                        } catch (Exception e) {

                        }
                        // String result =
                        // IATUtils.sendATCmd("AT+SPVLOOP=1,1,8,2,3,0",
                        // "atchannel0");
                        String result = IATUtils
                                .sendAtCmd("AT+SPVLOOP=1,1,8,2,3,0");
                        setInMac(LOOPBACK_SPEAKER);
                        if (result.contains(IATUtils.AT_OK)) {
                            mUihandler.post(new Runnable() {
                                public void run() {
                                    mRadioReceiver.setEnabled(true);
                                    mCurLoopback = LOOPBACK_SPEAKER;
                                    mRadioSpeaker.setChecked(true);
                                    mRadioOpenSuccess = true;
                                    if(mPendingPaused) {
                                        rollbackMmiAudio(mCurLoopback);
                                    } else {
                                        Toast.makeText(PhoneLoopBackTest.this,
                                                R.string.speaker_loopback_open,
                                                Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });
                        } else {
                            mUihandler.post(new Runnable() {
                                public void run() {
                                    if(!mPendingPaused) {
                                        Toast.makeText(PhoneLoopBackTest.this,
                                                R.string.speaker_loopback_open_fail,
                                                Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });
                        }
                    }
                }.start();
            }
        } else {
            /*
             * main mic need to send test_out_stream_route=0x2,test_in_stream_route=0x80000004,
             * dsploop_type=1,dsp_loop=1 auxiliarymic need to send
             * test_out_stream_route=0x2,test_in_stream_route=0x80000080 dsploop_type=1,dsp_loop=1
             * close need to send dsp_loop=0
             */
            if (loopbackType == LOOPBACK_RECEIVER) {
                new Thread() {
                    public void run() {
                        mAudioManager.setParameter("dsp_loop", "0");
                        mAudioManager.setParameter("test_out_stream_route",
                                "0x2");
                        mAudioManager.setParameter("test_in_stream_route",
                                "0x80000080");
                        mAudioManager.setParameter("dsploop_type", "1");
                        mAudioManager.setParameter("dsp_loop", "1");
                        mRadioOpenSuccess = true;
                        mUihandler.post(new Runnable() {
                            public void run() {
                                mRadioSpeaker.setEnabled(true);
                                mCurLoopback = LOOPBACK_RECEIVER;
                                mRadioReceiver.setChecked(true);
                                if(!mPendingPaused) {
                                    Toast.makeText(PhoneLoopBackTest.this,
                                            R.string.receiver_loopback_open,
                                            Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
                    }
                }.start();

            } else {
                new Thread() {
                    public void run() {
                        mAudioManager.setParameter("dsp_loop", "0");
                        mAudioManager.setParameter("test_out_stream_route",
                                "0x2");
                        mAudioManager.setParameter("test_in_stream_route",
                                "0x80000004");
                        mAudioManager.setParameter("dsploop_type", "1");
                        mAudioManager.setParameter("dsp_loop", "1");
                        mRadioOpenSuccess = true;
                        mUihandler.post(new Runnable() {
                            public void run() {
                                mRadioReceiver.setEnabled(true);
                                mCurLoopback = LOOPBACK_SPEAKER;
                                mRadioSpeaker.setChecked(true);
                                if(!mPendingPaused) {
                                    Toast.makeText(PhoneLoopBackTest.this,
                                            R.string.speaker_loopback_open,
                                            Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
                    }
                }.start();

            }
        }

    }

    private void rollbackMmiAudio(int loopbackType) {
        Log.i("PhoneLoopBackTest",
                "=== create thread to execute PhoneLoopBackTest stop command! ===");
        if (!isWhaleSupport && !mAudioWhaleHalFlag) {
            if (loopbackType == LOOPBACK_RECEIVER) {
                new Thread() {
                    public void run() {
                        // String result =
                        // IATUtils.sendATCmd("AT+SPVLOOP=0,0,8,2,3,0",
                        // "atchannel0");
                        String result = IATUtils
                                .sendAtCmd("AT+SPVLOOP=0,0,8,2,3,0");
                        Log.d(TAG, result);
                    }
                }.start();
            } else {
                new Thread() {
                    public void run() {
                        // String result =
                        // IATUtils.sendATCmd("AT+SPVLOOP=0,1,8,2,3,0",
                        // "atchannel0");
                        String result = IATUtils
                                .sendAtCmd("AT+SPVLOOP=0,1,8,2,3,0");
                        Log.d(TAG, result);
                    }
                }.start();
            }
        } else {
            /** close the function need to send "dsp_loop=0" **/
            new Thread() {
                public void run() {
                    mAudioManager.setParameter("dsp_loop", "0");
                }
            }.start();
        }
    }

    @Override
    public void onDestroy() {
        ValidationToolsUtils.setSoundEffect(this, mSavedSoundEffect);
        ValidationToolsUtils.setLockSound(this, mSavedLockSound);
        super.onDestroy();
    }
}
