package com.sprd.validationtools.itemstest.tp;

import android.app.ActionBar.LayoutParams;
import android.app.Presentation;
import android.content.Context;
import android.content.Intent;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PointF;
import android.hardware.display.DisplayManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.sprd.validationtools.BaseActivity;
import com.simcom.testtools.R;

public class MutiTouchDoubleScreenTest extends BaseActivity {

    private static final String TAG = "MutiTouchDoubleScreenTest";

    private static final int MAIN_SCREEN_TEST_CODE = 1;
    private static final int AUXILIARY_SCREEN_TEST_CODE = 2;
    private int mainTest = 0;
    private int auxiliaryTest = 0;
    private int mNavigationBarHeight = 0;
    private DisplayMetrics mDisplayMetrics;
    private Context mContext;
    private Context mWindowContext;
    private WindowManager mWm;

    private DisplayManager mDisplayManager;
    private DemoPresentation mPresentation;
    private Display mDisplay;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mPassButton.setVisibility(View.GONE);
        setContentView(R.layout.multi_touch_test);

        Button button1, button2;
        button1 = findViewById(R.id.singletouchpointtest1);
        button2 = findViewById(R.id.singletouchpointtest2);
        mDisplay = getSystemService(DisplayManager.class).getDisplay(1);
        mContext = this.createDisplayContext(mDisplay);
        mWindowContext = this.createDisplayContext(mDisplay);
        mNavigationBarHeight = getRealHeight(mWindowContext) - getHeight(mWindowContext);

        final Display display = getDisplay();
        final int displayId = display.getDisplayId();

        button1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "Start to enter the main screen test interface! Screen ID is：" + displayId);
                Intent intent = new Intent(MutiTouchDoubleScreenTest.this, MutiTouchTest.class);
                startActivityForResult(intent, 1);
            }
        });

        button2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mDisplayManager = (DisplayManager) MutiTouchDoubleScreenTest.this.getSystemService(Context.DISPLAY_SERVICE);
                Display[] displays = mDisplayManager.getDisplays();
                if (displays.length > 1) {
                    mPresentation = new DemoPresentation(MutiTouchDoubleScreenTest.this, displays[1]);
                    Log.d(TAG, "Start to enter the auxiliary screen test interface! The screen ID is:" + displays[1]);
                    mPresentation.show();
                }
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.d(TAG, "resultCode = " + resultCode);
        Log.d(TAG, "requestCode = " + requestCode);
        if(requestCode == 1){
            mHandler.sendEmptyMessage(MAIN_SCREEN_TEST_CODE);
        }
    }

    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case MAIN_SCREEN_TEST_CODE:
                    mainTest = MAIN_SCREEN_TEST_CODE;
                    Log.d(TAG, "Main screen test passed！");
                    if(mainTest + auxiliaryTest == 3){
                        storeRusult(true);
                        finish();
                    }
                    break;
                case AUXILIARY_SCREEN_TEST_CODE:
                    mPresentation.dismiss();
                    auxiliaryTest = AUXILIARY_SCREEN_TEST_CODE;
                    Log.d(TAG, "Auxiliary screen test passed！");
                    if(mainTest + auxiliaryTest == 3){
                        storeRusult(true);
                        finish();
                    }
                    break;
            }
            super.handleMessage(msg);
        }
    };

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(mPresentation != null) {
            mPresentation.dismiss();
        }
    }

    private final class DemoPresentation extends Presentation {

        public DemoPresentation(Context context, Display display) {
            super(context, display);
        }

        private MuiltImageView mImgView;
        private TextView mTextView;
        private DisplayMetrics mDisplayMetrics;
        private boolean isShowNavigationBar = false;

        @Override
        public void onCreate(Bundle savedInstanceState){
            super.onCreate(savedInstanceState);
            mPassButton.setVisibility(View.GONE);
            requestWindowFeature(Window.FEATURE_NO_TITLE);
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
            mDisplayMetrics = new DisplayMetrics();
            mDisplay.getMetrics(mDisplayMetrics);
            setContentView(createView());
        }

        private View createView(){
            LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                    LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT);
            LinearLayout view = new LinearLayout(mContext);
            view.setLayoutParams(lp);
            view.setOrientation(LinearLayout.VERTICAL);
            view.setBackgroundColor(Color.BLACK);
            ViewGroup.LayoutParams vlp = new ViewGroup.LayoutParams(
                    ViewGroup.LayoutParams.WRAP_CONTENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT);
            mTextView = new TextView(mContext);
            mImgView = new MuiltImageView(mContext, mDisplayMetrics.widthPixels,
                    mDisplayMetrics.heightPixels, mHandler);
            mTextView.setLayoutParams(vlp);
            mImgView.setLayoutParams(vlp);
            mTextView.setText(getString(R.string.muti_touchpoint_info));
            view.addView(mTextView);
            view.addView(mImgView);
            return view;
        }

        private class MuiltImageView extends View {
            private static final float RADIUS = 75f;
            private PointF pointf = new PointF();
            private PointF points = new PointF();
            private Handler mHandler;
            private boolean mPass = false;
            private int mWidth, mHeight;
            private Paint mPaint = null;

            public MuiltImageView(Context context, int width, int height, Handler handler){
                super(context);
                mWidth = width;
                mHeight = height;
                mHandler = handler;
                initData();
                initPaint();
            }

            private void initData(){
                pointf.set(mWidth - RADIUS, RADIUS);
                if (isShowNavigationBar) {
                    points.set(RADIUS, mHeight - RADIUS);
                } else {
                    points.set(RADIUS, mHeight - RADIUS - 150);
                }
            }

            private void initPaint(){
                mPaint = new Paint();
                mPaint.setAntiAlias(true);
                mPaint.setStyle(Paint.Style.FILL);
                mPaint.setColor(Color.YELLOW);
            }

            @Override
            protected void onDraw(Canvas canvas){
                super.onDraw(canvas);
                canvas.drawCircle(pointf.x, pointf.y, RADIUS, mPaint);
                canvas.drawCircle(points.x, points.y, RADIUS, mPaint);
            }

            @Override
            public boolean onTouchEvent(MotionEvent event){
                if (event.getPointerCount() == 2){
                    pointf.set(event.getX(0), event.getY(0));
                    points.set(event.getX(1), event.getY(1));
                    double distance = Math.sqrt((pointf.x - points.x) * (pointf.x - points.x)
                            + (pointf.y - points.y) * (pointf.y - points.y));
                    if (distance < (double)mWidth / 3 || distance > (double)mWidth / 3 * 2) {
                        mPass = true;
                    }
                }
                if (event.getAction() == MotionEvent.ACTION_UP && mPass) {
                    mHandler.sendEmptyMessage(AUXILIARY_SCREEN_TEST_CODE);
                }
                invalidate();
                return true;
            }
        }
    }
}