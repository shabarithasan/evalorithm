package com.evalorithm.ui.common

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Path
import android.util.AttributeSet
import android.view.View
import com.evalorithm.R

class LineChartView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {

    private var data: List<Pair<String, Float>> = emptyList()
    private var maxVal: Float = 100f

    private val linePaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = context.getColor(R.color.chart_blue)
        style = Paint.Style.STROKE
        strokeWidth = 6f
        isAntiAlias = true
    }

    private val dotPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = context.getColor(R.color.blue_600)
        style = Paint.Style.FILL
        isAntiAlias = true
    }

    private val fillPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = 0x3042A5F5
        style = Paint.Style.FILL
        isAntiAlias = true
    }

    private val textPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = Color.DKGRAY
        textSize = 28f
        textAlign = Paint.Align.CENTER
    }

    private val labelPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = Color.GRAY
        textSize = 24f
        textAlign = Paint.Align.CENTER
    }

    private val gridPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = 0xFFEEEEEE.toInt()
        style = Paint.Style.STROKE
        strokeWidth = 1f
    }

    fun setData(chartData: List<Pair<String, Float>>) {
        data = chartData
        maxVal = chartData.maxOfOrNull { it.second }?.coerceAtLeast(1f) ?: 100f
        maxVal = (maxVal / 10f).toInt() * 10f + 10f
        invalidate()
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val desiredWidth = 800
        val desiredHeight = 400
        val width = resolveSize(desiredWidth, widthMeasureSpec)
        val height = resolveSize(desiredHeight, heightMeasureSpec)
        setMeasuredDimension(width, height)
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        if (data.isEmpty()) return

        val paddingLeft = 60f
        val paddingRight = 30f
        val paddingTop = 40f
        val paddingBottom = 80f

        val chartWidth = width - paddingLeft - paddingRight
        val chartHeight = height - paddingTop - paddingBottom

        for (i in 0..4) {
            val y = paddingTop + chartHeight * i / 4f
            canvas.drawLine(paddingLeft, y, width - paddingRight, y, gridPaint)
            val labelVal = maxVal * (4 - i) / 4f
            labelPaint.textSize = 22f
            canvas.drawText(String.format("%.0f", labelVal), paddingLeft - 10f, y + 8f, labelPaint)
        }

        if (data.size < 2) return

        val stepX = chartWidth / (data.size - 1)

        val linePath = Path()
        val fillPath = Path()
        val points = mutableListOf<Pair<Float, Float>>()

        for (i in data.indices) {
            val x = paddingLeft + i * stepX
            val y = paddingTop + chartHeight - (data[i].second / maxVal) * chartHeight
            points.add(x to y)

            if (i == 0) {
                linePath.moveTo(x, y)
                fillPath.moveTo(x, paddingTop + chartHeight)
                fillPath.lineTo(x, y)
            } else {
                linePath.lineTo(x, y)
                fillPath.lineTo(x, y)
            }
        }

        fillPath.lineTo(points.last().first, paddingTop + chartHeight)
        fillPath.close()
        canvas.drawPath(fillPath, fillPaint)
        canvas.drawPath(linePath, linePaint)

        for (i in points.indices) {
            canvas.drawCircle(points[i].first, points[i].second, 8f, dotPaint)
            canvas.drawText(
                data[i].first,
                points[i].first,
                height - 20f,
                labelPaint
            )
        }
    }
}
