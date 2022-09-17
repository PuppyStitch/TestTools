package com.sprd.validationtools.itemstest.tp;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.Path;
import android.graphics.Rect;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.View.OnTouchListener;
import android.widget.Toast;

import com.sprd.validationtools.BaseActivity;
import com.simcom.testtools.R;

public class DrawRectTestAcvity extends BaseActivity {

    private static final String TAG = "DrawRectTestAcvity";

    private static int RECT_WIDTH = 240;
    private static int RECT_HEIGHT = 40;

    private int mScreenWidth;
    private int mScreenHeight;

    private List<RectInfo> mRectInfoList;

    private final Paint mGesturePaint = new Paint();
    private final Path mPath = new Path();

    private int mRectSize;
    private int mPassNum;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);
        mScreenWidth = dm.widthPixels;
        mScreenHeight = dm.heightPixels;
        Log.d(TAG, "mScreenWidth:" + mScreenWidth + "   mScreenHeight:"
                + mScreenHeight);
        if (mScreenWidth == 540) {
            RECT_WIDTH = 140;
            RECT_HEIGHT = 30;
        }
        if (mScreenWidth == 480) {
            RECT_WIDTH = 140;
            RECT_HEIGHT = 20;
        }
        if (mScreenWidth == 720) {
            RECT_WIDTH = 160;
            RECT_HEIGHT = 20;
        }
        initView();
        setContentView(new MySurfaceView(this));
        removeButton();
    }

    private void initView() {
        mRectInfoList = new ArrayList<RectInfo>();
        // Draw left
        drawLeft();
        // Draw top
        drawTop();
        // Draw right
        drawRight();
        // Draw bottom
        drawBottom();
        mRectSize = mRectInfoList.size();
    }

    private void drawLeft() {
        Rect r = new Rect(0, 0, RECT_WIDTH, RECT_HEIGHT);
        int num = (mScreenHeight - RECT_WIDTH) / RECT_HEIGHT;
        for (int i = 0; i < num; i++) {

            int left = r.left;
            int top = r.top + RECT_HEIGHT * i;
            int right = r.right;
            int bottom = r.bottom + RECT_HEIGHT * i;
            Rect rect = new Rect(left, top, right, bottom);

            Paint paint = new Paint();
            paint.setColor(Color.BLUE);

            mRectInfoList.add(new RectInfo(rect, false, paint));
        }
    }

    private void drawRight() {
        Rect r = new Rect(mScreenWidth - RECT_WIDTH, RECT_WIDTH, mScreenWidth,
                RECT_WIDTH + RECT_HEIGHT);
        int num = (mScreenHeight - RECT_WIDTH) / RECT_HEIGHT;
        for (int i = 0; i < num; i++) {
            int left = r.left;
            int top = r.top + RECT_HEIGHT * i;
            int right = r.right;
            int bottom = r.bottom + RECT_HEIGHT * i;
            Rect rect = new Rect(left, top, right, bottom);

            Paint paint = new Paint();
            paint.setColor(Color.BLUE);

            mRectInfoList.add(new RectInfo(rect, false, paint));
        }
    }

    private void drawTop() {
        Rect r = new Rect(RECT_WIDTH, 0, RECT_WIDTH + RECT_HEIGHT, RECT_WIDTH);
        int num = (mScreenWidth - RECT_WIDTH) / RECT_HEIGHT;
        for (int i = 0; i < num; i++) {
            int left = r.left + RECT_HEIGHT * i;
            int top = r.top;
            int right = r.right + RECT_HEIGHT * i;
            int bottom = r.bottom;
            Rect rect = new Rect(left, top, right, bottom);

            Paint paint = new Paint();
            paint.setColor(Color.BLUE);

            mRectInfoList.add(new RectInfo(rect, false, paint));
        }
    }

    private void drawBottom() {
        Rect r = new Rect(0, mScreenHeight - RECT_WIDTH, RECT_HEIGHT,
                mScreenHeight);
        int num = (mScreenWidth - RECT_WIDTH) / RECT_HEIGHT;
        for (int i = 0; i < num; i++) {
            int left = r.left + RECT_HEIGHT * i;
            int top = r.top;
            int right = r.right + RECT_HEIGHT * i;
            int bottom = r.bottom;
            Rect rect = new Rect(left, top, right, bottom);

            Paint paint = new Paint();
            paint.setColor(Color.BLUE);

            mRectInfoList.add(new RectInfo(rect, false, paint));
        }
    }

    static class RectInfo {

        Rect rect;
        boolean isTouched;
        Paint paint;

        public RectInfo(Rect r, boolean b, Paint p) {
            this.rect = r;
            this.isTouched = b;
            this.paint = p;
        }
    }

    class SurfaceViewThread extends Thread {
        public boolean isRun;
        private SurfaceHolder holder;

        private Paint paint;

        public SurfaceViewThread(SurfaceHolder holder) {
            this.holder = holder;
            paint = new Paint();
            paint.setColor(Color.GRAY);
        }

        public void run() {
            while (isRun) {
                Canvas canvas = null;
                try {
                    canvas = holder.lockCanvas();
                    if (canvas == null) {
                        return;
                    }
                    canvas.drawColor(Color.WHITE);
                    drawView(canvas);

                    canvas.drawPath(mPath, mGesturePaint);
                } catch (Exception e) {
                    Log.e(TAG, "draw exception", e);
                } finally {
                    if (canvas != null) {
                        holder.unlockCanvasAndPost(canvas);
                    }
                }
            }
        }
    }

    private void drawView(Canvas canvas) {
        for (int i = 0; i < mRectInfoList.size(); i++) {
            RectInfo ri = mRectInfoList.get(i);
            if (ri.isTouched) {
                ri.paint.setStyle(Style.FILL_AND_STROKE);
                ri.paint.setStrokeWidth(2);
            } else {
                ri.paint.setStyle(Style.STROKE);
                ri.paint.setStrokeWidth(2);
            }
            ri.paint.setAntiAlias(true);
            canvas.drawRect(ri.rect, ri.paint);
        }
    }

    private class MySurfaceView extends SurfaceView implements
            SurfaceHolder.Callback, OnTouchListener {

        private SurfaceHolder mHolder;
        private SurfaceViewThread mThread;

        private float mX;
        private float mY;

        public MySurfaceView(Context context) {
            super(context);
            mHolder = this.getHolder();
            mHolder.addCallback(this);

            mGesturePaint.setAntiAlias(true);
            mGesturePaint.setStyle(Style.STROKE);
            mGesturePaint.setStrokeWidth(2);
            mGesturePaint.setColor(Color.BLUE);

            this.setOnTouchListener(this);

        }

        @Override
        public void surfaceCreated(SurfaceHolder holder) {
            mThread = new SurfaceViewThread(mHolder);
            mThread.isRun = true;
            mThread.start();
        }

        @Override
        public void surfaceChanged(SurfaceHolder holder, int format, int width,
                int height) {

        }

        @Override
        public void surfaceDestroyed(SurfaceHolder holder) {
            mThread.isRun = false;
        }

        @Override
        public boolean onTouch(View v, MotionEvent event) {
            // TODO Auto-generated method stub
            switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                touchDown(event);
                break;
            case MotionEvent.ACTION_MOVE:
                touchMove(event);
                break;
            case MotionEvent.ACTION_UP:
                Log.d(TAG, "mPassNum:" + mPassNum + "  mRectSize:" + mRectSize);
                if (mPassNum == mRectSize) {
                    mHandler.sendEmptyMessage(0);
                }
                break;
            case MotionEvent.ACTION_CANCEL:
                Log.d(TAG, "mPassNum:" + mPassNum + "  mRectSize:" + mRectSize);
                if (mPassNum == mRectSize) {
                    mHandler.sendEmptyMessage(0);
                }
                break;
            }

            return true;
        }

        private void touchDown(MotionEvent event) {

            mPath.reset();
            float x = event.getX();
            float y = event.getY();

            mX = x;
            mY = y;

            mPath.moveTo(x, y);
        }

        private void touchMove(MotionEvent event) {
            final float x = event.getX();
            final float y = event.getY();

            final float previousX = mX;
            final float previousY = mY;

            final float dx = Math.abs(x - previousX);
            final float dy = Math.abs(y - previousY);

            if (dx >= 3 || dy >= 3) {

                float cX = (x + previousX) / 2;
                float cY = (y + previousY) / 2;

                mPath.quadTo(previousX, previousY, cX, cY);

                for (RectInfo ri : mRectInfoList) {
                    if (ri.rect.contains((int) (x), (int) (y)) && !ri.isTouched) {
                        ri.paint.setColor(Color.GREEN);
                        ri.isTouched = true;
                        mPassNum++;
                    }
                }

                mX = x;
                mY = y;
            }
        }
    }

    private Handler mHandler = new Handler() {

        public void handleMessage(Message msg) {
            Toast.makeText(DrawRectTestAcvity.this, R.string.text_pass,
                    Toast.LENGTH_SHORT).show();
            storeRusult(true);
            finish();
        }

    };
}
