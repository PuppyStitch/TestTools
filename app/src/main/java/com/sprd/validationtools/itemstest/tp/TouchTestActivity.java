package com.sprd.validationtools.itemstest.tp;

import android.os.Bundle;
import android.view.View;

import com.simcom.testtools.R;
import com.sprd.validationtools.BaseActivity;

public class TouchTestActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.my_finger_print_test);
//        myFingerPrintViewFirst = new MyFingerPrintViewFirst(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        mPassButton.setVisibility(View.INVISIBLE);
        mFailButton.setVisibility(View.INVISIBLE);
    }

    //    public MyFingerPrintTestActivity(Context context) {
//        super(context);
//        init();
//    }
//
//    public MyFingerPrintTestActivity(Context context, @Nullable AttributeSet attrs) {
//        super(context, attrs);
//    }
//
//    public MyFingerPrintTestActivity(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
//        super(context, attrs, defStyleAttr);
//    }
//
//    public MyFingerPrintTestActivity(Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
//        super(context, attrs, defStyleAttr, defStyleRes);
//    }
//
//    private int itemWidthBasic = 90;            // 基准宽高
//    private int itemHeightBasic = 80;           //
//
//    private float itemWidth = -1F;              // 最终宽高
//    private float itemHeight = -1F;
//
//    private int viewWidth = -1;                 // 自定义view的宽高
//    private int viewHeight = -1;
//
//    private int widthCount = -1;                // 宽高在方向上的数量变量
//    private int heightCount = -1;
//
//    private Paint paint = new Paint();
//
//    private void init() {
//        paint.setColor(Color.GRAY);
//        paint.setStyle(Paint.Style.STROKE);
//        paint.setStrokeWidth(2F);
//    }
//
//    class TouchRectF {
//        RectF rectF;
//        boolean isReDrawable = false;
//
//        void reset() {
//            isReDrawable = false;
//        }
//    }
//
//    // 屏幕左侧单元格的坐标容器
//    private MutableList leftRectFList = mutableListOf<TouchRectF>()
//
//    // 屏幕顶部单元格的坐标容器
//    private val topRectFList = mutableListOf<TouchRectF>()
//
//    // 屏幕右侧单元格的坐标容器
//    private val rightRectFList = mutableListOf<TouchRectF>()
//
//    // 屏幕底部单元格的坐标容器
//    private val bottomRectFList = mutableListOf<TouchRectF>()
//
//    // 屏幕水平居中单元格的坐标容器
//    private val centerHorizontalRectFList = mutableListOf<TouchRectF>()
//
//    // 屏幕垂直居中单元格的坐标容器
//    private val centerVerticalRectFList = mutableListOf<TouchRectF>()


}
