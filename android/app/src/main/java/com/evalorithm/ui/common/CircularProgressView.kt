package com.evalorithm.ui.common

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.RectF
import android.util.AttributeSet
import android.view.View
import com.evalorithm.R

class CircularProgressView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {

    private var value: Float = 0f
    private var label: String = ""
    private var progressColor: Int = context.getColor(R.color.chart_green)
    private var bgColor: Int = 0xFFE0E0E0.toInt()

    private val bgPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = bgColor
        style = Paint.Style.STROKE
        strokeWidth = 24f
        strokeCap = Paint.Cap.ROUND
    }

    private val progressPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.STROKE
        strokeWidth = 24f
        strokeCap = Paint.Cap.ROUND
    }

    private val textPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = Color.DKGRAY
        textSize = 48f
        textAlign = Paint.Align.CENTER
        isFakeBoldText = true
    }

    private val labelPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = Color.GRAY
        textSize = 28f
        textAlign = Paint.Align.CENTER
    }

    fun setProgress(value: Float, color: Int = progressColor, lbl: String = label) {
        this.value = value.coerceIn(0f, 100f)
        this.progressColor = color
        this.label = lbl
        progressPaint.color = color
        invalidate()
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val size = resolveSize(300, widthMeasureSpec)
        setMeasuredDimension(size, size)
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        val padding = 30f
        val rect = RectF(padding, padding, width - padding, height - padding)

        canvas.drawArc(rect, 0f, 360f, false, bgPaint)
        val sweepAngle = (value / 100f) * 360f
        canvas.drawArc(rect, -90f, sweepAngle, false, progressPaint)

        textPaint.textSize = width * 0.18f
        canvas.drawText(
            String.format("%.0f%%", value),
            width / 2f,
            height / 2f - 10f,
            textPaint
        )

        if (label.isNotEmpty()) {
            labelPaint.textSize = width * 0.09f
            canvas.drawText(label, width / 2f, height / 2f + 40f, labelPaint)
        }
    }
}
