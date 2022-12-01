
package com.sprd.validationtools.itemstest.otg;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.List;

import android.annotation.SuppressLint;
import android.content.Context;
import android.hardware.input.InputManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.StatFs;
import android.os.storage.StorageManager;
import android.os.storage.StorageVolume;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.TextView;

import com.sprd.validationtools.Const;
import com.simcom.testtools.R;

import android.os.Message;
import android.view.InputDevice;

import androidx.annotation.RequiresApi;

import com.sprd.validationtools.BaseActivity;
import com.sprd.validationtools.utils.StorageUtil;

public class OTGTest extends BaseActivity {
    private String TAG = "OTGTest";
    private TextView mTextView, mCountDownView;
    private StorageManager mStorageManager = null;
    private boolean isUsb = false;
    private String usbMassStoragePath = "/storage/usbdisk";
    private static final String SPRD_OTG_TESTFILE = "otgtest.txt";
    private String otgPath = null;
    public byte mOTGTestFlag[] = new byte[1];
    byte[] result = new byte[1];
    byte[] mounted = new byte[1];
    private static final int UPDATE_TIME = 0;
    private long time = 20;

    long availSize = 0, totalSize = 0;

    Context mContext;
    TextView rangView, statusTextView, totalTextView, availTextView;

    private boolean isMousePass = false;
    private boolean isUsbStoragePass = false;
    Thread vtThread;
    boolean shouldNotChange = false;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.otg_test_layout);
        rangView = findViewById(R.id.rang_view);
        statusTextView = findViewById(R.id.statusTextView);
        totalTextView = findViewById(R.id.totalTextView);
        availTextView = findViewById(R.id.availTextView);
        mHandler.sendEmptyMessageDelayed(UPDATE_TIME, 1000);
        /*SPRD bug 760913:Test can pass/fail must click button*/
        if (Const.isBoardISharkL210c10()) {
            mPassButton.setVisibility(View.GONE);
        }
        /*@}*/

        mContext = this;
        mStorageManager = (StorageManager) getSystemService(Context.STORAGE_SERVICE);
        mStorageManager.getStorageVolumes();

        uDiskName(this);
        disablePassButton();
    }

    @Override
    public boolean onGenericMotionEvent(MotionEvent event) {

        Log.i(TAG, event.getX() + " " + event.getY());
        // todo
        int[] location = new int[2];
        rangView.getLocationOnScreen(location);
        if (event.getX() > location[0]
                && event.getY() > location[1]
                && event.getX() < location[0] + rangView.getWidth()
                && event.getY() < location[1] + rangView.getHeight()) {
            enablePassButton();
//            vtThread.interrupt();
            shouldNotChange = true;
            statusTextView.setText(getResources().getText(R.string.otg_test_success));
//            storeRusult(true);
//            finish();
        }
        return super.onGenericMotionEvent(event);
    }

    @RequiresApi(api = Build.VERSION_CODES.Q)
    private void checkOTGDevices() {
        /* SPRD Bug 766333: OTG test should use mouse not USB. @{ */
        if (Const.isBoardISharkL210c10()) {
            final int[] devices = InputDevice.getDeviceIds();
            for (int i = 0; i < devices.length; i++) {
                InputDevice device = InputDevice.getDevice(devices[i]);
                if (device.getSources() == InputDevice.SOURCE_MOUSE) {
                    mounted[0] = 0;
                    Log.i(TAG, "=== OTG mount succeed ===");
                } else {
                    mounted[0] = 1;
                    Log.i(TAG, "=== OTG mount Fail ===");
                }
            }
        } else {
            /* SPRD Bug 940774:OTG test fail, because permission denied. @{ */
            String otgPath = StorageUtil.getExternalStorageAppPath(getApplicationContext(), Const.OTG_UDISK_PATH);
            if (otgPath != null) {
                mounted[0] = 0;
                Log.i(TAG, "=== OTG mount succeed ===");
                usbMassStoragePath = otgPath;
                getSize(otgPath);
            } else {
                mounted[0] = 1;
                Log.i(TAG, "=== OTG mount Fail ===");
            }
            /* @} */

            InputManager mIm = (InputManager) getSystemService(INPUT_SERVICE);
            mIm.registerInputDeviceListener(listener, null);
            if (mIm == null) {
                mIm = (InputManager) getSystemService(INPUT_SERVICE);
                mIm.registerInputDeviceListener(listener, null);
            }
            final int[] devices = InputDevice.getDeviceIds();
            for (int i = 0; i < devices.length; i++) {
                InputDevice device = InputDevice.getDevice(devices[i]);
                if (device != null && !device.isVirtual() && device.isExternal()) {
//                    if(device.getName().contains("Mouse") || device.getName().contains("Keyboard")) {

                    Log.d(TAG, "device.getName() = " + device.getName() + " device.getId() " + device.getId() + " getDescriptor " + device.getDescriptor());
                    break;
//                    }
                }
            }
        }
        /* @} */
    }

    private void getSize(String path) {
        try {
            StatFs statFs = new StatFs(path);
            long blockSize = statFs.getBlockSize();
            long totalBlocks = statFs.getBlockCount();
            long availableBlocks = statFs.getAvailableBlocks();
            totalSize = totalBlocks * blockSize / 1024 / 1024;
            availSize = availableBlocks * blockSize / 1024 / 1024;
            Log.d(TAG, "total size is = " + totalBlocks * blockSize / 1024 / 1024);
            Log.d(TAG, "avail size is = " + availableBlocks * blockSize / 1024 / 1024);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void uDiskName(Context context) {
        StorageManager storageManager = context.getSystemService(StorageManager.class);
        List<StorageVolume> volumeList = storageManager.getStorageVolumes();

        Log.d("OTGTest", "volumeList len = " + volumeList.size() + " " + volumeList.get(0).isRemovable());

        for (StorageVolume volume : volumeList) {
            if (null != volume && volume.isRemovable()) {
                //这个其实就是U盘的名称
                String label = volume.getDescription(context);
                //设备挂载的状态，如:mounted、unmounted
                String status = volume.getState();
                //是否是内部存储设备
                boolean isEmulated = volume.isEmulated();
                //是否是可移除的外部存储设备
                boolean isRemovable = volume.isRemovable();
                //设备的路径
                String mPath = "";

                try {
                    Class myclass = Class.forName(volume.getClass().getName());
                    Method getPath = myclass.getDeclaredMethod("getPath", (Class<?>) null);
                    getPath.setAccessible(true);
                    mPath = (String) getPath.invoke(volume);
                    Log.i("OTGTest", "name: " + label + ", status: " + status
                            + ", isEmulated: " + isEmulated + ", isRemovable: " + isRemovable + ", mPath: " + mPath);
                } catch (ClassNotFoundException | NoSuchMethodException
                        | InvocationTargetException | IllegalAccessException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    InputManager.InputDeviceListener listener = new InputManager.InputDeviceListener() {
        @Override
        public void onInputDeviceAdded(int deviceId) {
            Log.d(TAG, "onInputDeviceAdded");
        }

        @Override
        public void onInputDeviceRemoved(int deviceId) {
            Log.d(TAG, "onInputDeviceRemoved");
        }

        @Override
        public void onInputDeviceChanged(int deviceId) {
            Log.d(TAG, "onInputDeviceChanged");
        }
    };

    @SuppressLint("HandlerLeak")
    public Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case UPDATE_TIME:
                    time--;
//                    mCountDownView.setText(time + "");
                    if (time == 0) {
                        Log.d(TAG, "time out");
//                        Toast.makeText(OTGTest.this, R.string.text_fail,
//                                Toast.LENGTH_SHORT).show();
                        /*SPRD bug 760913:Test can pass/fail must click button*/
                        if (Const.isBoardISharkL210c10()) {
                            Log.d("", "isBoardISharkL210c10 is return!");
                            return;
                        }
                        /*@}*/
//                        storeRusult(false);
//                        finish();
                    } else {
                        mHandler.sendEmptyMessageDelayed(UPDATE_TIME, 1000);
                    }
                    break;
            }
            super.handleMessage(msg);
        }
    };

    public Runnable mRunnable = new Runnable() {
        @SuppressLint("SetTextI18n")
        public void run() {
            Log.i(TAG, "=== display OTG test succeed info! ===");
            if (mounted[0] == 0) {
                if (result[0] == 0) {
                    enablePassButton();
                    statusTextView.setText(getResources().getText(R.string.otg_test_success));
                    availTextView.setText(availSize + " MB");
                    totalTextView.setText(totalSize + " MB");
                    /*SPRD bug 760913:Test can pass/fail must click button*/
                    if (Const.isBoardISharkL210c10()) {
                        Log.d("", "isBoardISharkL210c10 is return!");
                        mPassButton.setVisibility(View.VISIBLE);
                        return;
                    }
                    /*@}*/
                    storeRusult(true);
                } else {
                    if (!shouldNotChange) {
                        statusTextView.setText(getResources().getText(R.string.otg_test_fail));
                    }
                    /*SPRD bug 760913:Test can pass/fail must click button*/
                    if (Const.isBoardISharkL210c10()) {
                        Log.d("", "isBoardISharkL210c10 is return!");
                        return;
                    }
                    /*@}*/
                    storeRusult(false);
                    finish();
                }
            } else {
                if (!shouldNotChange) {
                    statusTextView.setText(getResources().getText(R.string.otg_test_fail));
                }
                /*SPRD bug 760913:Test can pass/fail must click button*/
                if (Const.isBoardISharkL210c10()) {
                    Log.d("", "isBoardISharkL210c10 is return!");
                    return;
                }
                /*@}*/
                storeRusult(false);
                finish();
            }
        }
    };
    public Runnable mCheckRunnable = new Runnable() {
        @RequiresApi(api = Build.VERSION_CODES.Q)
        public void run() {
            Log.i(TAG, "=== checkOTGdevices! ===");
            checkOTGDevices();
            if (mounted[0] != 0) {
                if (!shouldNotChange) {
                    statusTextView.setText(getResources().getText(R.string.otg_no_devices));
                }
                mHandler.postDelayed(mCheckRunnable, 1000);
            } else {
                startVtThread();
            }
        }
    };

    @RequiresApi(api = Build.VERSION_CODES.Q)
    @Override
    protected void onResume() {
        super.onResume();
        statusTextView.setText(getResources().getText(R.string.otg_begin_test));
        mounted[0] = 1;
        result[0] = 1;
        checkOTGDevices();
        if (mounted[0] != 0) {
            statusTextView.setText(getResources().getText(R.string.otg_no_devices));
            mHandler.postDelayed(mCheckRunnable, 1000);
        } else {
            startVtThread();
        }
    }

    @Override
    protected void onPause() {
        mHandler.removeCallbacks(mCheckRunnable);
        mHandler.removeMessages(UPDATE_TIME);
        super.onPause();
    }

    private void startVtThread() {
        Log.i(TAG,
                "=== create thread to execute OTG test command! ===");
        /* SPRD Bug 766333: OTG test should use mouse not USB. @{ */
        if (Const.isBoardISharkL210c10()) {
            result[0] = 0;
            mHandler.post(mRunnable);
        } else {
            if (vtThread != null) {
                return;
            }
            vtThread = new Thread(() -> {
                FileInputStream in = null;
                FileOutputStream out = null;
                try {
                    if (mounted[0] == 0) {
                        File fp = new File(usbMassStoragePath, SPRD_OTG_TESTFILE);
                        if (fp.exists())
                            fp.delete();
                        fp.createNewFile();
                        out = new FileOutputStream(fp);
                        mOTGTestFlag[0] = '7';
                        out.write(mOTGTestFlag, 0, 1);
                        out.close();
                        in = new FileInputStream(fp);
                        in.read(mOTGTestFlag, 0, 1);
                        in.close();
                        if (mOTGTestFlag[0] == '7') {
                            result[0] = 0;
                        } else {
                            result[0] = 1;
                        }
                    }
                    //mHandler.post(mRunnable);
                    mHandler.postDelayed(mRunnable, 2000);
                } catch (IOException e) {
                    Log.i(TAG, "=== error: Exception happens when OTG I/O! ===");
                    e.printStackTrace();
                } finally {
                    try {
                        if (out != null) {
                            out.close();
                            out = null;
                        }
                        if (in != null) {
                            in.close();
                            in = null;
                        }
                    } catch (IOException io) {
                        Log.e(TAG, "close in/out err");
                    }
                }
            });
            vtThread.start();
        }
        /* @} */
    }
}
