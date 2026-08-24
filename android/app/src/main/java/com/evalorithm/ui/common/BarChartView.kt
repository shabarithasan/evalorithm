package com.evalorithm.ui.common

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.RectF
import android.util.AttributeSet
import android.view.View
import com.evalorithm.R

class BarChartView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {

    private var data: List<Pair<String, Float>> = emptyList()
    private var maxVal: Float = 100f

    private val barPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = context.getColor(R.color.chart_blue)
        style = Paint.Style.FILL
    }

    private val textPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = Color.DKGRAY
        textSize = 32f
        textAlign = Paint.Align.CENTER
    }

    private val valuePaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = Color.WHITE
        textSize = 28f
        textAlign = Paint.Align.CENTER
        isFakeBoldText = true
    }

    private val labelPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = Color.GRAY
        textSize = 28f
        textAlign = Paint.Align.CENTER
    }

    private val barColors = intArrayOf(
        0xFF42A5F5.toInt(),
        0xFF66BB6A.toInt(),
        0xFFFFA726.toInt(),
        0xFFEF5350.toInt(),
        0xFFAB47BC.toInt(),
        0xFF26A69A.toInt()
    )

    fun setData(chartData: List<Pair<String, Float>>) {
        data = chartData
        maxVal = chartData.maxOfOrNull { it.second }?.coerceAtLeast(1f) ?: 100f
        invalidate()
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val desiredWidth = 800
        val desiredHeight = 500
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
        val barCount = data.size
        val barWidth = (chartWidth / barCount) * 0.6f
        val gap = (chartWidth / barCount) * 0.4f

        for (i in data.indices) {
            val (label, value) = data[i]
            val barHeight = (value / maxVal) * chartHeight
            val x = paddingLeft + i * (barWidth + gap) + gap / 2
            val top = paddingTop + chartHeight - barHeight

            barPaint.color = barColors[i % barColors.size]
            val rect = RectF(x, top, x + barWidth, paddingTop + chartHeight)
            canvas.drawRoundRect(rect, 8f, 8f, barPaint)

            valuePaint.color = if (barHeight > 40f) Color.WHITE else Color.DKGRAY
            canvas.drawText(
                String.format("%.0f%%", value),
                x + barWidth / 2,
                if (barHeight > 40f) top + barHeight / 2 + 10f else top - 10f,
                valuePaint
            )

            labelPaint.textSize = 26f
            canvas.drawText(label, x + barWidth / 2, height - 20f, labelPaint)
        }
    }
}
