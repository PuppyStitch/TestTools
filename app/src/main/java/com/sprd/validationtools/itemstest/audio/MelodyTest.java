
package com.sprd.validationtools.itemstest.audio;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.media.AudioManager;
import android.media.AudioSystem;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnPreparedListener;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.util.Log;
import android.view.Gravity;
import android.view.WindowManager;
import android.widget.TextView;
import android.widget.Toast;

import com.simcom.testtools.R;
import com.sprd.validationtools.BaseActivity;
import com.sprd.validationtools.Const;
import com.sprd.validationtools.utils.StorageUtil;

public class MelodyTest extends BaseActivity {
    private final static String TAG = "MelodyTest";
    TextView mContent;
    private int backupMode = 0;
    private List<String> mFilePaths;
    private File mFile;
    MediaPlayer mPlayer = null;
    private Vibrator mVibrator = null;
    private static final long V_TIME = 100000;

    private static final String DEFAULT_AUDIO = "Orion.ogg";
    private boolean isSearchFinished = false;

    private AudioManager mAudioManager = null;

    /*SPRD bug 755106:Repeat vibrate support*/
    private boolean mVibratePattern = Const.isBoardISharkL210c10();
    private static final long[] PATTERN = new long[]{1000, 10000, 1000, 10000};
    /*@}*/

    /* SPRD Bug 771294: The Ringtones motor test need to use the specific ringtone. @{ */
    private static final boolean USE_SPECIFIC_RINGTONE = true;
    private static final String SPECIFIC_RINGTONE_NAME = "mixtone.wav";
    /* @} */
    private String mFilePath = null;
    private static final boolean PLAY_LOOP = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON,
                WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        mAudioManager = (AudioManager) this
                .getSystemService(Context.AUDIO_SERVICE);
        mAudioManager.setSpeakerphoneOn(false);
        mContent = new TextView(this);
        mContent.setGravity(Gravity.CENTER);
        mContent.setTextSize(25);
        setContentView(mContent);
        setTitle(R.string.melody_test);
        mFilePaths = new ArrayList<String>();

        /* SPRD Bug 771294: The Ringtones motor test need to use the specific ringtone. @{ */
        if (USE_SPECIFIC_RINGTONE) {
            String filePath = saveMediaFileToSdcard();
            mFilePath = filePath;
            mPlayer = new MediaPlayer();
            mHandler.sendEmptyMessage(SEARCH_FINISHED);
        } else {
            mPlayer = new MediaPlayer();

            new Thread() {
                public void run() {
                    //Maybe cause StackOverflowError toSearchFiles recursion.
                    File firstAudio = new File("/system/media/audio/ringtones", DEFAULT_AUDIO);
                    if (firstAudio.exists()) {
                        mFilePaths.add(firstAudio.getPath());
                    } else {
                        mFile = new File("/system/media/audio/ringtones");
                        toSearchFiles(mFile);
                    }
                    mHandler.sendEmptyMessage(SEARCH_FINISHED);
                }
            }.start();
        }
        mVibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
        mContent.setText(R.string.start_searching);
        /* @} */
    }

    private final int SEARCH_FINISHED = 0;
    Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case SEARCH_FINISHED:
                    isSearchFinished = true;
                    doPlay();
                    break;
            }
        }
    };

    private OnPreparedListener mOnPreparedListener = new OnPreparedListener() {

        @Override
        public void onPrepared(MediaPlayer mp) {
            // TODO Auto-generated method stub
            if (mp != null) {
                mp.setLooping(PLAY_LOOP);
                mp.start();
                int volumeMusic = mAudioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
                Log.d(TAG, "mOnPreparedListener volumeMusic = " + volumeMusic);
                mAudioManager.setStreamVolume(AudioManager.STREAM_MUSIC, volumeMusic, 0);
            } else {
                Log.w(TAG, "mOnPreparedListener prepare issue!");
            }
        }
    };

    private void doPlay() {
        int audioNumber = 0;

        /* SPRD Bug 771294: The Ringtones motor test need to use the specific ringtone. @{ */
        if (!USE_SPECIFIC_RINGTONE) {
            audioNumber = getRandom(mFilePaths.size());
        }
        /* @} */

        if (mPlayer == null) {
            return;
        }

        int volumeMusic = mAudioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
        Log.d(TAG, "volumeMusic = " + volumeMusic);
        mAudioManager.setStreamVolume(AudioManager.STREAM_MUSIC, volumeMusic, 0);
        Log.d(TAG, "doPlay audioNumber= " + audioNumber);
        try {
            /* SPRD Bug 771294: The Ringtones motor test need to use the specific ringtone. @{ */
            if (!USE_SPECIFIC_RINGTONE) {
                mPlayer.reset();
                mPlayer.setDataSource(mFilePaths.get(audioNumber));
                mPlayer.prepare();
                mPlayer.setOnPreparedListener(mOnPreparedListener);
            } else {
                mPlayer.reset();
                mPlayer.setDataSource(mFilePath);
                mPlayer.prepare();
                mPlayer.setOnPreparedListener(mOnPreparedListener);
            }
            /* @} */
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        } catch (IllegalStateException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        /*BEGIN BUG559287 2016/05/04 zhijie.yang mmi/ringtones test error */
        if (AudioSystem.DEVICE_STATE_AVAILABLE == AudioSystem.getDeviceConnectionState(
                AudioManager.DEVICE_OUT_EARPIECE, "")) {
            if (!Const.isBoardISharkL210c10()) {
                mAudioManager.setMode(AudioManager.MODE_IN_CALL);
            }
        }
        /*SPRD bug 755106:Repeat vibrate support*/
        Log.d(TAG, "onCreate mVibratePattern = " + mVibratePattern);
        if (mVibratePattern) {
            mVibrator.vibrate(PATTERN, 0);
        } else {
            VibrationEffect vibrationEffect = VibrationEffect.createOneShot(10000, 200);
            mVibrator.vibrate(vibrationEffect);
        }
        /*@}*/

        /* SPRD Bug 771294: The Ringtones motor test need to use the specific ringtone. @{ */
        if (USE_SPECIFIC_RINGTONE) {
            mContent.setText(getResources().getText(R.string.melody_play_tag) + SPECIFIC_RINGTONE_NAME);
        } else {
            mContent.setText(getResources().getText(R.string.melody_play_tag)
                    + mFilePaths.get(audioNumber));
        }
        /* @} */
    }

    @Override
    protected void onResume() {
        super.onResume();
        AudioManager audioManager = (AudioManager) this.getSystemService(Context.AUDIO_SERVICE);
        backupMode = audioManager.getMode();
        if (mPlayer == null) {
            mPlayer = new MediaPlayer();
        }
        if (isSearchFinished) {
            doPlay();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (mPlayer == null) {
            return;
        }
        mPlayer.stop();
        mPlayer.release();
        mPlayer = null;
        if (mVibrator != null) {
            mVibrator.cancel();
        }
        AudioManager audioManager = (AudioManager) this.getSystemService(Context.AUDIO_SERVICE);
        audioManager.setMode(backupMode);
    }

    public void toSearchFiles(File file) {
        File[] files = file.listFiles();
        if (files == null) {
            return;
        }
        for (File tf : files) {
            if (tf.isDirectory()) {
                toSearchFiles(tf);
            } else {
                try {
                    if (tf.getName().indexOf(".ogg") > -1) {
                        mFilePaths.add(tf.getPath());
                    }
                } catch (IndexOutOfBoundsException e) {
                    Toast.makeText(this, "pathError", Toast.LENGTH_SHORT).show();
                }
            }
        }
    }

    private int getRandom(int max) {
        /* SPRD bug 753871:Use fixed audio source. */
        if (Const.isBoardISharkL210c10()) {
            // Use fixed audio source.
            if (mFilePaths != null && mFilePaths.size() > 0) {
                int audioNumber = 0;
                return audioNumber;
            }
        }
        /* @} */
        double random = Math.random();
        int result = (int) Math.floor(random * max);
        return result;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    private String saveMediaFileToSdcard() {
        String filePath = StorageUtil.getInternalStoragePath() + "/mmi/" + SPECIFIC_RINGTONE_NAME;
        saveToRawResourceSDCard(this, R.raw.mixtone, filePath);
        return filePath;
    }

    public void saveToRawResourceSDCard(Context context, int sourceResId, String filePath) {
        InputStream inStream = null;
        FileOutputStream fileOutputStream = null;
        ByteArrayOutputStream outStream = null;
        File dstFile = new File(filePath);
        try {
            if (!dstFile.getParentFile().exists()) {
                dstFile.getParentFile().mkdirs();
            }
            if (!dstFile.exists()) {
                dstFile.createNewFile();
            }
            inStream = context.getResources().openRawResource(sourceResId);
            fileOutputStream = new FileOutputStream(dstFile);
            byte[] buffer = new byte[10];
            outStream = new ByteArrayOutputStream();
            int len = 0;
            while ((len = inStream.read(buffer)) != -1) {
                outStream.write(buffer, 0, len);
            }
            byte[] bs = outStream.toByteArray();
            fileOutputStream.write(bs);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {

            try {
                outStream.close();
                inStream.close();
                fileOutputStream.flush();
                fileOutputStream.close();
                outStream = null;
                inStream = null;
                fileOutputStream = null;
            } catch (IOException e2) {
                e2.printStackTrace();
            }
        }
    }
}
