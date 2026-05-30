package com.wordnote.app.ui

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.LinearGradient
import android.graphics.Paint
import android.graphics.Path
import android.graphics.Shader
import android.util.AttributeSet
import android.view.View

class TrendChartView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {

    private val linePaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.STROKE
        strokeWidth = 3f * resources.displayMetrics.density
        strokeCap = Paint.Cap.ROUND
        strokeJoin = Paint.Join.ROUND
    }

    private val fillPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.FILL
    }

    private val dotPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.FILL
    }

    private val textPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        textSize = 10f * resources.displayMetrics.density
        textAlign = Paint.Align.CENTER
        color = Color.parseColor("#767676")
    }

    private val gridPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.STROKE
        strokeWidth = 1f
        color = Color.parseColor("#EEEEEE")
    }

    private var data: List<Float> = emptyList()
    private var labels: List<String> = emptyList()
    private var lineColor: Int = Color.parseColor("#4CAF50")

    fun setData(values: List<Float>, labelList: List<String>, color: Int = Color.parseColor("#4CAF50")) {
        data = values
        labels = labelList
        lineColor = color
        invalidate()
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        if (data.isEmpty()) return

        val density = resources.displayMetrics.density
        val padding = 40f * density
        val chartWidth = width - padding * 2
        val chartHeight = height - padding * 2

        val maxValue = 100f
        val minValue = 0f
        val range = maxValue - minValue

        // Draw grid lines
        for (i in 0..4) {
            val y = padding + chartHeight * (1 - i / 4f)
            canvas.drawLine(padding, y, width - padding, y, gridPaint)

            // Draw Y axis labels
            val value = (minValue + range * i / 4f).toInt()
            textPaint.textAlign = Paint.Align.RIGHT
            canvas.drawText("$value%", padding - 8f * density, y + 4f, textPaint)
        }

        if (data.size < 2) {
            // Single point - just draw dot
            if (data.size == 1) {
                val x = width / 2f
                val y = padding + chartHeight * (1 - (data[0] - minValue) / range)
                dotPaint.color = lineColor
                canvas.drawCircle(x, y, 4f * density, dotPaint)
                dotPaint.color = Color.WHITE
                canvas.drawCircle(x, y, 2f * density, dotPaint)
                if (labels.isNotEmpty()) {
                    textPaint.textAlign = Paint.Align.CENTER
                    canvas.drawText(labels[0], x, height - 8f, textPaint)
                }
            }
            return
        }

        val stepX = chartWidth / (data.size - 1)

        // Draw line and fill
        val linePath = Path()
        val fillPath = Path()

        data.forEachIndexed { index, value ->
            val x = padding + index * stepX
            val y = padding + chartHeight * (1 - (value - minValue) / range)

            if (index == 0) {
                linePath.moveTo(x, y)
                fillPath.moveTo(x, padding + chartHeight)
                fillPath.lineTo(x, y)
            } else {
                linePath.lineTo(x, y)
                fillPath.lineTo(x, y)
            }
        }

        // Close fill path
        fillPath.lineTo(padding + (data.size - 1) * stepX, padding + chartHeight)
        fillPath.close()

        // Draw fill with gradient
        fillPaint.shader = LinearGradient(
            0f, padding,
            0f, padding + chartHeight,
            lineColor and 0x00FFFFFF or 0x40000000,
            Color.TRANSPARENT,
            Shader.TileMode.CLAMP
        )
        canvas.drawPath(fillPath, fillPaint)

        // Draw line
        linePaint.color = lineColor
        canvas.drawPath(linePath, linePaint)

        // Draw dots and X labels
        data.forEachIndexed { index, value ->
            val x = padding + index * stepX
            val y = padding + chartHeight * (1 - (value - minValue) / range)

            dotPaint.color = lineColor
            canvas.drawCircle(x, y, 4f * density, dotPaint)

            // White center
            dotPaint.color = Color.WHITE
            canvas.drawCircle(x, y, 2f * density, dotPaint)

            // X label
            if (index < labels.size) {
                textPaint.textAlign = Paint.Align.CENTER
                canvas.drawText(labels[index], x, height - 8f, textPaint)
            }
        }
    }
}
