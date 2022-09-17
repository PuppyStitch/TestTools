
package com.sprd.validationtools.itemstest.camera;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;

import android.app.Activity;
import android.content.pm.ActivityInfo;
import android.content.pm.FeatureInfo;
import android.content.pm.PackageManager;
import android.graphics.PixelFormat;
import android.graphics.Point;
import android.graphics.SurfaceTexture;
import android.hardware.Camera;
import android.hardware.Camera.Parameters;
import android.hardware.Camera.PictureCallback;
import android.hardware.Camera.ShutterCallback;
import android.hardware.Camera.Size;
import android.os.Bundle;
import android.os.Handler;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.TextureView;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.sprd.validationtools.BaseActivity;
import com.sprd.validationtools.Const;
import com.simcom.testtools.R;
import com.sprd.validationtools.utils.StorageUtil;

public class FrontCameraTestActivity extends BaseActivity implements TextureView.SurfaceTextureListener{

    private static final String TAG = "FrontCameraTestActivity";
    private CameraScreenNailProxy mCameraScreenNailProxy;

    public static final int CAMERA_START = 0;

    private Camera mCamera = null;

    private TextureView mTextureView = null;
    private SurfaceTexture mSurfaceTexture = null;
    private PreviewFrameLayout mPreviewFrameLayout;
    private TextView mLightMsg = null;


    private ComboPreferences mPreferences;
    private boolean mFlag = false;
    private static final int FRONT_CAMERA = 1;

    private int groupId;
    private Handler mHandler;
    private Button mTakePhotoBtn;
    /*SPRD bug 753903:Freeze screen in some board*/
    private boolean mIsFreezeScreen = Const.isBoardISharkL210c10();
    /*@}*/
    private static final boolean mIsAutoPass = false;
    private Runnable mTimeOut = new Runnable() {
        public void run() {
            FrontCameraTestActivity.this.storeRusult(false);
            FrontCameraTestActivity.this.finish();
        }
    };

    protected class CameraScreenNailProxy {
        private static final String TAG = "CameraScreenNailProxy";

        public static final int KEY_SIZE_PICTURE = 0;
        public static final int KEY_SIZE_PREVIEW = 1;

        private Tuple<Integer, Integer> mScreenSize;

        protected CameraScreenNailProxy() {
            initializeScreenSize();
        }

        private void initializeScreenSize() {
            Display display = getWindowManager().getDefaultDisplay();
            DisplayMetrics metrics = new DisplayMetrics();
            display.getMetrics(metrics);
            mScreenSize = new Tuple<Integer, Integer>(
                    metrics.widthPixels, metrics.heightPixels);
            Log.d(TAG,
                    String.format("screen size = { %dx%d }",
                            new Object[] {
                                    mScreenSize.first, mScreenSize.second
                            }));
        }

        protected Tuple<Integer, Integer>
                getOptimalSize(int key, ComboPreferences pref) {

            Tuple<Integer, Integer> result = null;
            Size size = null;
            boolean b_full_screen = getScreenState(pref);
            int orientation = getOrientation();
            int width = mScreenSize.first, height = mScreenSize.second;
            Parameters mParameters = null;
            mParameters = mCamera.getParameters();
            if (KEY_SIZE_PICTURE == key) {
                size = mParameters.getPictureSize();
                width = size.width;
                height = size.height;
                result = Util.getOptimalSize(
                        mScreenSize.first, mScreenSize.second, width, height, b_full_screen);
                width = result.first;
                height = result.second;
                if (orientation % 180 == 0) {
                    int tmp = width;
                    width = height;
                    height = tmp;
                }
            }

            if (KEY_SIZE_PREVIEW == key) {
                size = mParameters.getPreviewSize();
                width = size.width;
                height = size.height;
                result = Util.getOptimalSize(
                        mScreenSize.first, mScreenSize.second, width, height, b_full_screen);
                width = result.first;
                height = result.second;
                if (orientation % 180 == 0) {
                    int tmp = width;
                    width = height;
                    height = tmp;
                }
            }

            result = new Tuple<Integer, Integer>(width, height);
            Log.d(TAG,
                    String.format(
                            "get optimal size: key = %d, is_full_screen = %b, size = { %dx%d }",
                            new Object[] {
                                    key, b_full_screen, result.first, result.second
                            }));
            return result;
        }

        private int getOrientation() {
            return getCameraDisplayOrientation(FRONT_CAMERA, mCamera);
        }
    }

    protected boolean getScreenState(ComboPreferences pref) {
        boolean result = false;
        if (pref != null) {
            String str_on = getString(R.string.pref_entry_value_on);
            String str_val = pref.getString("pref_camera_video_full_screen_key", null);
            result = (str_val != null && str_val.equals(str_on));
        }
        return result;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        groupId = this.getIntent().getIntExtra("groupId", 0);
        Log.d(TAG, "groupId" + groupId);
        setTitle(getResources().getText(R.string.back_camera_title_text));
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON,
                WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        setContentView(R.layout.back_camera_result);
        setTitle(R.string.camera_test_title);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        mCameraScreenNailProxy = new CameraScreenNailProxy();
        mPreviewFrameLayout = (PreviewFrameLayout) findViewById(R.id.frame);

        mTextureView = (TextureView) findViewById(R.id.surfaceView);
        mTextureView.setSurfaceTextureListener(this);

        mLightMsg = (TextView) findViewById(R.id.light_msg);
        mLightMsg.setText(R.string.font_msg_text);

        mHandler = new Handler();
        /*BEGIN 2016/04/13 zhijie.yang BUG535005 mmi add take photo of camera test */
        mTakePhotoBtn = (Button) findViewById(R.id.start_take_picture);
        mTakePhotoBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (mCamera != null) {
                    mTakePhotoBtn.setEnabled(false);
                    mCamera.takePicture(shutterCallback, null, mPicture);

                } else {
                    Log.d(TAG, "mCamera is null.");
                    Toast.makeText(getApplicationContext(),
                            FrontCameraTestActivity.this.getString(R.string.front_camera_fail_tips), Toast.LENGTH_SHORT)
                            .show();
                    storeRusult(false);
                    finish();
                }
            }
        });
        mHandler.postDelayed(mTimeOut, 120000);
        if(!mIsAutoPass && mPassButton != null){
            mPassButton.setVisibility(View.INVISIBLE);
        }
    }

    private ShutterCallback shutterCallback = new ShutterCallback() {
        public void onShutter() {
        }
    };

    private PictureCallback mPicture = new PictureCallback() {

        @Override
        public void onPictureTaken(byte[] data, Camera camera) {
            String filePath = StorageUtil.getInternalStoragePath() + "/mmi/"
                    + "frontPhoto.jpg";
            File pictureFile = new File(filePath);
            FileOutputStream fos = null;
            try {
                if (!pictureFile.getParentFile().exists()) {
                    pictureFile.getParentFile().mkdirs();
                }
                if (!pictureFile.exists()) {
                    pictureFile.createNewFile();
                }
                fos = new FileOutputStream(pictureFile);
                fos.write(data);
                android.media.MediaScannerConnection.scanFile(
                        getApplicationContext(),
                        new String[] { pictureFile.getAbsolutePath() }, null,
                        null);
            } catch (FileNotFoundException e) {
                Log.d(TAG, "File not found: " + e.getMessage());
            } catch (IOException e) {
                Log.d(TAG, "Error accessing file: " + e.getMessage());
            } finally {
                try {
                    if (fos != null) {
                        fos.close();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            /* SPRD bug 753903:Freeze screen in some board */
            if (mIsFreezeScreen) {
                // Freeze screen here
                Log.d(TAG, "Freeze screen here!");
            } else {
                // Bug 614121
                mHandler.postDelayed(new Runnable() {
                    public void run() {
                        if (mCamera != null) {
                            mCamera.startPreview();
                            mTakePhotoBtn.setEnabled(true);
                        }
                    }
                }, 1000);
            }
            /* @} */
            if (!mIsAutoPass && mPassButton != null) {
                mPassButton.setVisibility(View.VISIBLE);
            }
        }
    };
    /*END 2016/04/13 zhijie.yang BUG535005 mmi add take photo of camera test */

    /***/
    private void startCamera() {
        if (mFlag) {
            Log.e(TAG, "stop & close");
            if (mCamera != null) {
                mCamera.stopPreview();
                mCamera.release();
                mFlag = false;
            }
        }
        try {
            Log.e(TAG, "open");
            mCamera = Camera.open(FRONT_CAMERA);
        } catch (RuntimeException e) {
            Log.e(TAG, "fail to open camera");
            e.printStackTrace();
            mCamera = null;
        }
        if (mCamera != null) {
            setCameraDisplayOrientation(FRONT_CAMERA, mCamera);
            Parameters parameters = null;
            parameters = mCamera.getParameters();
            parameters.setPictureFormat(PixelFormat.JPEG);
            parameters.set("orientation", "portrait");
            parameters.setRotation(270);
//            parameters.setFlashMode(Camera.Parameters.FLASH_MODE_TORCH);
            Size size = parameters.getPictureSize();
            List<Size> sizes = parameters.getSupportedPreviewSizes();
            Size optimalSize = getOptimalPreviewSize(this, sizes, (double) size.width / size.height);
            Size original = parameters.getPreviewSize();
            if (!original.equals(optimalSize)) {
                parameters.setPreviewSize(optimalSize.width, optimalSize.height);
            }
            Log.v(TAG, "Preview size is " + optimalSize.width + "x" + optimalSize.height);

            mCamera.setParameters(parameters);
            try {
                mCamera.setPreviewTexture(mSurfaceTexture);
                Log.e(TAG, "start preview");
                mCamera.startPreview();
                mFlag = true;
                initializeCameraOpenAfter();
            } catch (NullPointerException | IOException e) {
                mCamera.release();
            }
        }
    }

    public static Size getOptimalPreviewSize(Activity currentActivity,
            List<Size> sizes, double targetRatio) {
        final double ASPECT_TOLERANCE = 0.001;
        if (sizes == null)
            return null;

        Size optimalSize = null;
        double minDiff = Double.MAX_VALUE;

        Display display = currentActivity.getWindowManager().getDefaultDisplay();
        Point point = new Point();
        display.getSize(point);
        int targetHeight = Math.min(point.x, point.y);

        for (Size size : sizes) {
            double ratio = (double) size.width / size.height;
            if (Math.abs(ratio - targetRatio) > ASPECT_TOLERANCE)
                continue;
            if (Math.abs(size.height - targetHeight) < minDiff) {
                optimalSize = size;
                minDiff = Math.abs(size.height - targetHeight);
                Log.d(TAG, "getOptimalPreviewSize minDiff="+minDiff);
                break;
            }
        }

        if (optimalSize == null) {
            Log.w(TAG, "No preview size match the aspect ratio");
            double minDiff2 = Double.MAX_VALUE;
            for (Size size : sizes) {
                if (Math.abs(size.height - targetHeight) < minDiff2) {
                    optimalSize = size;
                    minDiff2 = Math.abs(size.height - targetHeight);
                }
            }
        }
        return optimalSize;
    }

    @Override
    public void onSurfaceTextureAvailable(SurfaceTexture surface, int width,
            int height) {
        Log.d(TAG, "onSurfaceTextureAvailable");
        mSurfaceTexture = surface;
        startCamera();
    }

    @Override
    public void onSurfaceTextureSizeChanged(SurfaceTexture surface, int width,
            int height) {
    }

    @Override
    public boolean onSurfaceTextureDestroyed(SurfaceTexture surface) {
        return false;
    }

    @Override
    public void onSurfaceTextureUpdated(SurfaceTexture surface) {
    }

    @Override
    protected void onResume() {
        super.onResume();
        if(mFlag) {
            startCamera();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        try {
            if (mCamera != null) {
                Parameters parameters = mCamera.getParameters();
                parameters.setFlashMode(Parameters.FLASH_MODE_OFF);
                mCamera.setParameters(parameters);
                mCamera.stopPreview();
                mCamera.release();
                mCamera = null;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static int getDisplayRotation() {
        return 0;
    }

    public static void setCameraDisplayOrientation(
            int cameraId, Camera camera) {
        int result = getCameraDisplayOrientation(cameraId, camera);
        camera.setDisplayOrientation(result);
    }

    public static int getCameraDisplayOrientation(
            int cameraId, Camera camera) {
        Camera.CameraInfo info = new Camera.CameraInfo();
        Camera.getCameraInfo(cameraId, info);
        int degrees = getDisplayRotation();
        int result;
        if (info.facing == Camera.CameraInfo.CAMERA_FACING_FRONT) {
            result = (info.orientation + degrees) % 360;
            result = (360 - result) % 360;
        } else {
            result = (info.orientation - degrees + 360) % 360;
        }
        return result;
    }

    private void initializeCameraOpenAfter() {
        // SPRD:Fixbug454827,The preview picture of take photo has some defective.
        Tuple<Integer, Integer> size =
                mCameraScreenNailProxy.getOptimalSize(
                        CameraScreenNailProxy.KEY_SIZE_PREVIEW, mPreferences);
        if (mPreviewFrameLayout != null) {
            mPreviewFrameLayout.setAspectRatio((double) size.first / (double) size.second, true);
        }
    }

    @Override
    public void onDestroy() {
        mHandler.removeCallbacks(mTimeOut);
        super.onDestroy();
    }
}
