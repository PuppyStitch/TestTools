
package com.sprd.validationtools.itemstest.tp;

import android.app.ActionBar.LayoutParams;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PointF;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.DisplayMetrics;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.sprd.validationtools.BaseActivity;
import com.simcom.testtools.R;
import com.sprd.validationtools.utils.ValidationToolsUtils;

public class MutiTouchTest extends BaseActivity {
    private static final String TAG = "MutiTouchTest";
    private MuiltImageView mImgView;
    private TextView mTextView;
    private DisplayMetrics mDisplayMetrics;
    private MainHandler mHandler;
    private Context mContext;
    private boolean isShowNavigationBar = false;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        isShowNavigationBar = ValidationToolsUtils.hasNavigationBar(this);
        isShowNavigationBar = true;
        mPassButton.setVisibility(View.GONE);
        mFailButton.setVisibility(View.GONE);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        mContext = this;
        //getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        mDisplayMetrics = new DisplayMetrics();
        ((WindowManager) getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay().getMetrics(mDisplayMetrics);
        mHandler = new MainHandler();
        setContentView(createView());
    }

    @Override
    public void onResume() {
        super.onResume();
        if (isShowNavigationBar) {
            hideNavigationBar();
        }
    }

    private View createView() {
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT);
        LinearLayout view = new LinearLayout(this);
        view.setLayoutParams(lp);
        view.setOrientation(LinearLayout.VERTICAL);
        view.setBackgroundColor(Color.BLACK);
        ViewGroup.LayoutParams vlp = new ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT);
        mTextView = new TextView(this);
        mImgView = new MuiltImageView(this, mDisplayMetrics.widthPixels,
                mDisplayMetrics.heightPixels, mHandler);
        mTextView.setLayoutParams(vlp);
        mImgView.setLayoutParams(vlp);
        mTextView.setText(getString(R.string.muti_touchpoint_info));
        view.addView(mTextView);
        view.addView(mImgView);
        return view;
    }

    private class MainHandler extends Handler {
        @Override
        public void handleMessage(Message msg) {
            if (msg.what == 1) {
                Toast.makeText(mContext, R.string.text_pass, Toast.LENGTH_SHORT).show();
                storeRusult(true);
                finish();
            }
        }
    }

    private class MuiltImageView extends View {
        private static final float RADIUS = 75f;
        private PointF pointf = new PointF();
        private PointF points = new PointF();
        private Handler mHandler;
        private boolean mPass = false;
        private int mWidth, mHeight;
        private Paint mPaint = null;

        public MuiltImageView(Context context, int width, int height, Handler handler) {
            super(context);
            mWidth = width;
            mHeight = height;
            mHandler = handler;
            initData();
            initPaint();
        }

        private void initData() {
            pointf.set(mWidth - RADIUS, RADIUS);
            if (isShowNavigationBar) {
                points.set(RADIUS, mHeight - RADIUS);
            } else {
                points.set(RADIUS, mHeight - RADIUS - 150);
            }
        }

        private void initPaint() {
            mPaint = new Paint();
            mPaint.setAntiAlias(true);
            mPaint.setStyle(Paint.Style.FILL);
            mPaint.setColor(Color.YELLOW);
        }

        @Override
        protected void onDraw(Canvas canvas) {
            super.onDraw(canvas);
            canvas.drawCircle(pointf.x, pointf.y, RADIUS, mPaint);
            canvas.drawCircle(points.x, points.y, RADIUS, mPaint);
        }

        @Override
        public boolean onTouchEvent(MotionEvent event) {
            if (event.getPointerCount() == 2) {
                pointf.set(event.getX(0), event.getY(0));
                points.set(event.getX(1), event.getY(1));
                double distance = Math.sqrt((pointf.x - points.x) * (pointf.x - points.x)
                        + (pointf.y - points.y) * (pointf.y - points.y));
                if (distance < (double) mWidth / 3 || distance > (double) mWidth / 3 * 2) {
                    mPass = true;
                }
            }
            if (event.getAction() == MotionEvent.ACTION_UP && mPass) {
                mHandler.sendEmptyMessage(1);
            }
            invalidate();
            return true;
        }
    }
}
