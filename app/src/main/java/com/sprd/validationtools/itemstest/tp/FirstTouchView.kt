package com.sprd.validationtools.itemstest.tp

import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.util.Log
import android.view.MotionEvent
import android.view.View
import androidx.core.graphics.toRegion

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

    private var isPass : Boolean = false;

    public var touchResultListener : TouchResultListener? = null

    @JvmName("setTouchResultListener1")
    fun setTouchResultListener(listener: TouchResultListener) {
        touchResultListener = listener
    }

    private val boxPaint by lazy {
        Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = Color.GRAY
            style = Paint.Style.STROKE
            strokeWidth = 2F
        }
    }

    /**
     *
     */
    private val positiveCrossPath = TouchPath()
    private val positiveCrossRegion = Region()

    private val linePathMeasure = PathMeasure()

    /**
     *
     */
    private val reverseCrossPath = TouchPath()
    private val reverseCrossRegion = Region()

    class TouchPath  {
        var path = Path()
        var isReDrawable = false
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

        Log.i(TAG, "widthCount = " + widthCount + " viewWidth = " + viewWidth + " itemWidthBasic = " + itemWidthBasic)
        Log.i(TAG, "heightCount = " + heightCount + " viewHeight = " + viewHeight + " itemHeightBasic = " + itemHeightBasic)

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

        // 重置 Path
        positiveCrossPath.path.reset()
        reverseCrossPath.path.reset()

        // PositiveCross

        // 获取左下角单元格坐标
        val lbRectF = bottomRectFList.first().rectF

        // 获取右上角单元格坐标
        val rtRectF = topRectFList.last().rectF

        with(positiveCrossPath.path) {
            // 移动 Path 至左下角单元格的左上角顶点
            moveTo(lbRectF.left, lbRectF.top)

            // 连接直线至右上角单元格的左上角顶点
            lineTo(rtRectF.left, rtRectF.top)

            // 以右上角单元格的右上角顶点坐标为基准计算屏幕外一点为控制点, 右上角单元格的右下角顶点为结束点绘制二阶贝赛尔曲线
            quadTo(
                    rtRectF.right + itemWidth,
                    rtRectF.top - itemHeight,
                    rtRectF.right,
                    rtRectF.bottom
            )

            // 连接直线至左下角单元格的右下角顶点
            lineTo(lbRectF.right, lbRectF.bottom)

            // 以左下角单元格的左下角顶点坐标为基准计算屏幕外一点为控制点, 左下角单元格的左上角顶点为结束点绘制二阶贝赛尔曲线
            quadTo(
                    lbRectF.left - itemWidth,
                    lbRectF.bottom + itemHeight,
                    lbRectF.left,
                    lbRectF.top
            )

            // 闭合 Path
            close()
        }

        // 计算正向管道 Path 区域
        val positiveCrossRectF = RectF()
        positiveCrossPath.path.computeBounds(positiveCrossRectF, true)
        positiveCrossRegion.setPath(positiveCrossPath.path, positiveCrossRectF.toRegion())

        // ReverseCross

        // 获取左上角单元格坐标
        val ltRectF = topRectFList.first().rectF

        // 获取右下角单元格坐标
        val rbRectF = bottomRectFList.last().rectF

        with(reverseCrossPath.path) {
            // 移动 Path 至左上角单元格的右上角顶点
            moveTo(ltRectF.right, ltRectF.top)

            // 连接直线只右下角单元格的右上角顶点
            lineTo(rbRectF.right, rbRectF.top)

            // 以右下角单元格的右下角顶点坐标为基准计算屏幕外一点为控制点, 右下角单元格的左下角顶点为结束点绘制二阶贝赛尔曲线
            quadTo(
                    rbRectF.right + itemWidth,
                    rbRectF.bottom + itemHeight,
                    rbRectF.left,
                    rbRectF.bottom
            )

            // 连接直线至左上角单元格的左下角顶点
            lineTo(ltRectF.left, ltRectF.bottom)

            // 以左上角单元格的左下角顶点坐标为基准计算屏幕外一点为控制点, 左上角单元格的右上角顶点为结束点绘制二阶贝赛尔曲线
            quadTo(
                    ltRectF.left - itemWidth,
                    ltRectF.top - itemHeight,
                    ltRectF.right,
                    ltRectF.top
            )

            // 闭合 Path
            close()
        }

        // 计算反向管道 Path 区域
        val reverseCrossRectF = RectF()
        reverseCrossPath.path.computeBounds(reverseCrossRectF, true)
        reverseCrossRegion.setPath(reverseCrossPath.path, reverseCrossRectF.toRegion())
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

//        drawPositiveCross(canvas)
//        drawReverseCross(canvas)
    }

    private fun drawReverseCross(canvas: Canvas) {
        if (reverseCrossPath.isReDrawable) {
            canvas.drawPath(reverseCrossPath.path, fillPaint)
        }
        canvas.drawPath(reverseCrossPath.path, boxPaint)
    }

    private fun drawPositiveCross(canvas: Canvas) {
        if (positiveCrossPath.isReDrawable) {
            canvas.drawPath(positiveCrossPath.path, fillPaint)
        }
        canvas.drawPath(positiveCrossPath.path, boxPaint)
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
                    // 新增重绘管道逻辑
                    findReDrawableCross()
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
            MotionEvent.ACTION_MOVE -> {
                // 判断当前坐标是否在单元格和管道区域内

            }
        }
        return true
    }

    private fun findReDrawableCross() {

        // 轨迹线 Path 为空返回
        if (linePath.isEmpty) {
            return
        }

        Log.i(TAG, "findReDrawableCross")

        // 把轨迹线 Path 设置给路径测量器
        linePathMeasure.setPath(linePath, false)

        // 定义起点与终点坐标数组
        val startPoint = FloatArray(2)
        val endPoint = FloatArray(2)

        // 获取 Path 长度
        val linePathLength = linePathMeasure.length

        // 计算起点与终点坐标
        linePathMeasure.getPosTan(0F, startPoint, null)
        linePathMeasure.getPosTan(linePathLength, endPoint, null)

        // 校验起点坐标
        val startX = startPoint[0]
        val startY = startPoint[1]
        if (startX == 0F || startY == 0F) {
            return
        }

        // 校验终点坐标
        val endX = endPoint[0]
        val endY = endPoint[1]
        if (endX == 0F || endY == 0F) {
            return
        }

        val lbRectF = bottomRectFList.first().rectF
        val rtRectF = topRectFList.last().rectF

        // 判断轨迹线的起点与终点坐标是否都在管道两端的单元格区域内
        if (((lbRectF.contains(startX, startY) && rtRectF.contains(endX, endY)) ||
                        (lbRectF.contains(endX, endY) && rtRectF.contains(startX, startY)))
        ) {
            // 定义 mark 变量为 true
            var mark = true

            // 遍历轨迹线
            for (i in 1 until linePathLength.toInt()) {

                // 获取轨迹线上点的坐标
                val point = FloatArray(2)
                val posTan = linePathMeasure.getPosTan(i.toFloat(), point, null)
                if (!posTan) {
                    mark = false
                    break
                }

                // 坐标校验
                val x = point[0]
                val y = point[1]
                if (x == 0F || y == 0F) {
                    mark = false
                    break
                }

                // 判断轨迹线上点的坐标是否在管道区域内
                if (!positiveCrossRegion.contains(x.toInt(), y.toInt())) {
                    mark = false
                    break
                }
            }

            if (mark) {
                // 标记正向管道可重绘
                markPositiveCrossReDrawable()
            }
        }

        val ltRectF = topRectFList.first().rectF
        val rbRectF = bottomRectFList.last().rectF

        Log.i(TAG, "start")
        // 判断轨迹线的起点与终点坐标是否都在管道两端的单元格区域内
        if (((ltRectF.contains(startX, startY) && rbRectF.contains(endX, endY)) ||
                        (ltRectF.contains(endX, endY) && rbRectF.contains(startX, startY)))
        ) {
            // 定义 mark 变量为 true
            var mark = true
            Log.i(TAG, "end")
            // 遍历轨迹线
            for (i in 1 until linePathLength.toInt()) {

                // 获取轨迹线上点的坐标
                val point = FloatArray(2)
                val posTan = linePathMeasure.getPosTan(i.toFloat(), point, null)
                if (!posTan) {
                    mark = false
                    break
                }

                // 坐标校验
                val x = point[0]
                val y = point[1]
                if (x == 0F || y == 0F) {
                    mark = false
                    break
                }

                // 判断轨迹线上点的坐标是否在管道区域内
                if (!reverseCrossRegion.contains(x.toInt(), y.toInt())) {
                    mark = false
                    break
                }
            }

            if (mark) {
                // 标记反向管道可重绘
                markReverseCrossReDrawable()
            }
        }
    }

    private fun markPositiveCrossReDrawable() {
        Log.i(TAG, "markPositiveCrossReDrawable")
        if (!positiveCrossPath.isReDrawable) {
            positiveCrossPath.isReDrawable = true
        }
    }

    // 标记反向管道可重绘
    private fun markReverseCrossReDrawable() {
        Log.i(TAG, "markReverseCrossReDrawable")
        if (!reverseCrossPath.isReDrawable) {
            reverseCrossPath.isReDrawable = true
        }
    }

    // 判断当前坐标是否在单元格和管道区域内
    private fun isInTouchableRegion(x: Float, y: Float): Boolean {
        return leftRectFList.any { it.rectF.contains(x, y) } ||
                topRectFList.any { it.rectF.contains(x, y) } ||
                rightRectFList.any { it.rectF.contains(x, y) } ||
                bottomRectFList.any { it.rectF.contains(x, y) } ||
                centerHorizontalRectFList.any { it.rectF.contains(x, y) } ||
                centerVerticalRectFList.any {
                    it.rectF.contains(x, y) } ||
                positiveCrossRegion.contains(x.toInt(), y.toInt()) ||
                reverseCrossRegion.contains(x.toInt(), y.toInt())
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

            if (isTouchPass()) {
                touchResultListener?.onTouchPass(true)
            }
        }
    }

    private val linePath = Path()

    private val linePaint by lazy {
        Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = Color.RED
            style = Paint.Style.STROKE
            strokeWidth = 8F
            strokeJoin = Paint.Join.ROUND
            strokeCap = Paint.Cap.ROUND
        }
    }

    private val fillPaint by lazy {
        Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = Color.BLUE
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

    private fun touchPass() {
        isPass = true
        touchResultListener?.onTouchPass(true)
    }

    private fun isTouchPass(): Boolean {
        return leftRectFList.all { it.isReDrawable } &&
                topRectFList.all { it.isReDrawable } &&
                rightRectFList.all { it.isReDrawable } &&
                bottomRectFList.all { it.isReDrawable }
//                centerHorizontalRectFList.all { it.isReDrawable } &&
//                centerVerticalRectFList.all { it.isReDrawable } &&
//                positiveCrossPath.isReDrawable &&
//                reverseCrossPath.isReDrawable
    }
}