package com.sprd.validationtools.itemstest.tp

import android.content.Context
import android.graphics.Path
import android.graphics.RectF
import android.graphics.Region
import android.util.AttributeSet
import android.view.View
import androidx.core.graphics.toRegion

class SecondTouchView : View {

    constructor(context: Context) : this(context, null)
    constructor(context: Context, attrs: AttributeSet?) : this(context, attrs, 0)
    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
    }

    private val positiveCrossPath = TouchPath()
    private val positiveCrossRegion = Region()

    private val reverseCrossPath = TouchPath()
    private val reverseCrossRegion = Region()

    private fun computeRectF() {

        // 省略计算单元格的代码

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
        // 省略绘制单元格代码

        drawPositiveCross(canvas)
        drawReverseCross(canvas)
    }

    private fun drawReverseCross(canvas: Canvas) {
        canvas.drawPath(reverseCrossPath.path, boxPaint)
    }

    private fun drawPositiveCross(canvas: Canvas) {
        canvas.drawPath(positiveCrossPath.path, boxPaint)
    }

}