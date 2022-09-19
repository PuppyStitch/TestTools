package com.sprd.validationtools.itemstest.tp

import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.util.Log
import android.view.MotionEvent
import android.view.View

class FirstTouchView : View {

    private val TAG = "MyFingerPrintViewFirst"

    constructor(context: Context) : this(context, null)
    constructor(context: Context, attrs: AttributeSet?) : this(context, attrs, 0)
    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
    }

    private var itemWidthBasic = 80
    private var itemHeightBasic = 88

    private var itemWidth = -1F
    private var itemHeight = -1F

    private var viewWidth: Int = -1
    private var viewHeight: Int = -1


    private var widthCount = -1
    private var heightCount = -1

    private val boxPaint by lazy {
        Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = Color.GRAY
            style = Paint.Style.STROKE
            strokeWidth = 2F
        }
    }

    data class TouchRectF(val rectF: RectF, var isReDrawable: Boolean = false) {

        fun reset() {
            isReDrawable = false
        }
    }

    // 屏幕左侧单元格的坐标容器
    private val leftRectFList = mutableListOf<TouchRectF>()

    // 屏幕顶部单元格的坐标容器
    private val topRectFList = mutableListOf<TouchRectF>()

    // 屏幕右侧单元格的坐标容器
    private val rightRectFList = mutableListOf<TouchRectF>()

    // 屏幕底部单元格的坐标容器
    private val bottomRectFList = mutableListOf<TouchRectF>()

    // 屏幕水平居中单元格的坐标容器
    private val centerHorizontalRectFList = mutableListOf<TouchRectF>()

    // 屏幕垂直居中单元格的坐标容器
    private val centerVerticalRectFList = mutableListOf<TouchRectF>()

    override fun onLayout(changed: Boolean, left: Int, top: Int, right: Int, bottom: Int) {
        super.onLayout(changed, left, top, right, bottom)
        // 保存 View 的宽高
        viewWidth = width
        viewHeight = height

        computeRectF()
    }

    private fun computeRectF() {
        // 以单元格的基准宽高计算单元格宽高方向上的数量
        widthCount = viewWidth / itemWidthBasic
        heightCount = viewHeight / itemHeightBasic

        Log.i(TAG, "widthCount = " + widthCount + " viewWidth = " + viewWidth + " itemWidthBasic = " + itemWidthBasic);
        Log.i(TAG, "heightCount = " + heightCount + " viewHeight = " + viewHeight + " itemHeightBasic = " + itemHeightBasic);

        // 以单元格宽高方向上的数量再计算单元格的最终宽高
        itemWidth = viewWidth.toFloat() / widthCount
        itemHeight = viewHeight.toFloat() / heightCount

        // 清空之前计算的结果
        leftRectFList.clear()
        topRectFList.clear()
        rightRectFList.clear()
        bottomRectFList.clear()
        centerHorizontalRectFList.clear()
        centerVerticalRectFList.clear()

        // 计算并保存屏幕左侧单元格的坐标, 不包含头和尾, 去掉与顶部和底部重叠的单元格
        for (i in 1 until heightCount - 1) {
            val rectF = RectF(0F, itemHeight * i, itemWidth, itemHeight * (i + 1))
            leftRectFList.add(TouchRectF(rectF))
        }

        // 计算并保存屏幕顶部单元格的坐标
        for (i in 0 until widthCount) {
            val rectF = RectF(itemWidth * i, 0F, itemWidth * (i + 1), itemHeight)
            topRectFList.add(TouchRectF(rectF))
        }

        // 计算并保存屏幕右侧单元格的坐标, 不包含头和尾, 去掉与顶部和底部重叠的单元格
        for (i in 1 until heightCount - 1) {
            val rectF = RectF(
                    viewWidth - itemWidth,
                    itemHeight * i,
                    viewWidth.toFloat(),
                    itemHeight * (i + 1)
            )
            rightRectFList.add(TouchRectF(rectF))
        }

        // 计算并保存屏幕底部单元格的坐标
        for (i in 0 until widthCount) {
            val rectF = RectF(
                    itemWidth * i,
                    viewHeight - itemHeight,
                    itemWidth * (i + 1),
                    viewHeight.toFloat()
            )
            bottomRectFList.add(TouchRectF(rectF))
        }

        // 计算并保存屏幕水平居中单元格的坐标, 不包含头和尾, 去掉与左侧和右侧重叠的单元格
        val centerHIndex = heightCount / 2
        for (i in 1 until widthCount - 1) {
            val rectF = RectF(
                    itemWidth * i,
                    itemHeight * centerHIndex,
                    itemWidth * (i + 1),
                    itemHeight * (centerHIndex + 1)
            )
            centerHorizontalRectFList.add(TouchRectF(rectF))
        }

        // 计算并保存屏幕垂直居中单元格的坐标, 不包含头和尾, 去掉与顶部和底部重叠的单元格, 且去掉与水平居中重叠的单元格
        val centerVIndex = widthCount / 2
        val skipIndex: Int = centerHIndex

        for (i in 1 until heightCount - 1) {
            // 跳过与横轴交叉的部分
            if (i == skipIndex) {
                continue
            }

            val rectF = RectF(
                    itemWidth * centerVIndex,
                    itemHeight * i,
                    itemWidth * (centerVIndex + 1),
                    itemHeight * (i + 1)
            )
            centerVerticalRectFList.add(TouchRectF(rectF))
        }
    }

    override fun onDraw(canvas: Canvas) {
        // 单元格数量为 -1 时返回
        if (widthCount == -1 || heightCount == -1) {
            return
        }

        // 清空画布
        canvas.drawColor(Color.BLACK, PorterDuff.Mode.CLEAR)
        canvas.drawColor(Color.WHITE)

        // 绘制水平方向的单元格
        drawHorizontalBox(canvas)

        // 绘制垂直方向的单元格
        drawVerticalBox(canvas)

        drawTrackLine(canvas)
    }

    private fun drawTrackLine(canvas: Canvas) {
        // 判断轨迹线 Path 是否为空
        if (linePath.isEmpty) {
            return
        }

        // 绘制轨迹线
        canvas.drawPath(linePath, linePaint)
    }

    private fun drawHorizontalBox(canvas: Canvas) {
        for (rectF in topRectFList) {
            drawBox(rectF, canvas)
        }

//        for (rectF in centerHorizontalRectFList) {
//            drawBox(rectF, canvas)
//        }

        for (rectF in bottomRectFList) {
            drawBox(rectF, canvas)
        }
    }

    private fun drawVerticalBox(canvas: Canvas) {
        for (rectF in leftRectFList) {
            drawBox(rectF, canvas)
        }

//        for (rectF in centerVerticalRectFList) {
//            drawBox(rectF, canvas)
//        }

        for (rectF in rightRectFList) {
            drawBox(rectF, canvas)
        }
    }

    private fun drawBox(rectF: TouchRectF, canvas: Canvas) {
//        canvas.drawRect(rectF.rectF, boxPaint)
        if (rectF.isReDrawable) {
            // 重绘单元格
            canvas.drawRect(rectF.rectF, redrawBoxPaint)
            canvas.drawRect(rectF.rectF, fillPaint)
        } else {
            canvas.drawRect(rectF.rectF, boxPaint)
        }
    }

//    override fun onTouchEvent(event: MotionEvent): Boolean {
//        val x = event.x
//        val y = event.y
//        when (event.actionMasked) {
//            MotionEvent.ACTION_DOWN -> {
//                // 根据当前坐标查找可重绘的单元格
//                findReDrawableBox(x, y)
//
//                // 重绘 View
//                invalidate()
//            }
//        }
//        return true
//    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        val x = event.x
        val y = event.y
        when (event.actionMasked) {
            MotionEvent.ACTION_DOWN -> {
                // 清空轨迹线 Path
                linePath.reset()

                // 移动轨迹线起点至点击坐标
                linePath.moveTo(x, y)

                // 根据当前坐标查找可重绘的单元格
                findReDrawableBox(x, y)
            }
            MotionEvent.ACTION_MOVE -> {
                // 判断当前坐标是否在单元格和管道区域内
                if (isInTouchableRegion(x, y)) {
                    if (linePath.isEmpty) {
                        // 如果被重置了，先移动起点至当前坐标
                        linePath.moveTo(x, y)
                    } else {
                        // 没有被重置，连接直线至当前坐标
                        linePath.lineTo(x, y)
                    }

                    // 根据当前坐标查找可重绘的单元格
                    findReDrawableBox(x, y)
                } else {
                    // 清空轨迹线 Path
                    linePath.reset()
                }

                // 重绘View
                invalidate()
            }
            MotionEvent.ACTION_UP, MotionEvent.ACTION_CANCEL -> {
                // 清空轨迹线 Path
                linePath.reset()

                // 重绘View
                invalidate()
            }
        }
        return true
    }

    // 判断当前坐标是否在单元格和管道区域内
    private fun isInTouchableRegion(x: Float, y: Float): Boolean {
        return leftRectFList.any { it.rectF.contains(x, y) } ||
                topRectFList.any { it.rectF.contains(x, y) } ||
                rightRectFList.any { it.rectF.contains(x, y) } ||
                bottomRectFList.any { it.rectF.contains(x, y) } ||
                centerHorizontalRectFList.any { it.rectF.contains(x, y) } ||
                centerVerticalRectFList.any { it.rectF.contains(x, y) }
//                positiveCrossRegion.contains(x.toInt(), y.toInt()) ||
//                reverseCrossRegion.contains(x.toInt(), y.toInt())
    }

    // 查找可重绘的单元格
    private fun findReDrawableBox(x: Float, y: Float) {
        val touchRectF = (leftRectFList.find { it.rectF.contains(x, y) }
                ?: topRectFList.find { it.rectF.contains(x, y) }
                ?: rightRectFList.find { it.rectF.contains(x, y) }
                ?: bottomRectFList.find { it.rectF.contains(x, y) }
                ?: centerHorizontalRectFList.find { it.rectF.contains(x, y) }
                ?: centerVerticalRectFList.find { it.rectF.contains(x, y) })

        if (touchRectF != null) {
            // 标记可重绘的单元格
            markBoxReDrawable(touchRectF)
        }
    }

    // 标记可重绘的单元格
    private fun markBoxReDrawable(rectF: TouchRectF) {
        if (!rectF.isReDrawable) {
            rectF.isReDrawable = true
        }
    }

    private val linePath = Path()

    private val linePaint by lazy {
        Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = Color.BLUE
            style = Paint.Style.STROKE
            strokeWidth = 8F
            strokeJoin = Paint.Join.ROUND
            strokeCap = Paint.Cap.ROUND
        }
    }

    private val fillPaint by lazy {
        Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = Color.GREEN
            style = Paint.Style.FILL
        }
    }

    private val redrawBoxPaint by lazy {
        Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = Color.YELLOW
            style = Paint.Style.STROKE
            strokeWidth = 3F
        }
    }
}