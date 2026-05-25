package com.wordnote.app.ui

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.RectF
import android.util.AttributeSet
import android.view.View

class PieChartView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {

    data class Slice(val label: String, val value: Int, val color: Int)

    private var slices: List<Slice> = emptyList()
    private val paint = Paint(Paint.ANTI_ALIAS_FLAG)
    private val textPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = Color.WHITE
        textSize = 12f * resources.displayMetrics.density
        textAlign = Paint.Align.CENTER
        isFakeBoldText = true
    }
    private val labelPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        textSize = 11f * resources.displayMetrics.density
    }

    private var chartSize = 0f
    private var centerX = 0f
    private var centerY = 0f
    private var radius = 0f

    fun setData(data: List<Slice>) {
        slices = data
        invalidate()
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val w = MeasureSpec.getSize(widthMeasureSpec)
        val desiredHeight = (180 * resources.displayMetrics.density).toInt()
        val h = resolveSize(desiredHeight, heightMeasureSpec)
        chartSize = minOf(w, h).toFloat()
        centerX = w / 2f
        centerY = h / 2f
        radius = chartSize * 0.32f
        setMeasuredDimension(w, h)
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        if (slices.isEmpty() || slices.all { it.value == 0 }) return

        val total = slices.sumOf { it.value }.toFloat()
        if (total == 0f) return

        val density = resources.displayMetrics.density
        val rect = RectF(centerX - radius, centerY - radius, centerX + radius, centerY + radius)

        // Draw slices
        var startAngle = -90f
        slices.forEach { slice ->
            val sweepAngle = (slice.value / total) * 360f
            paint.color = slice.color
            paint.style = Paint.Style.FILL
            canvas.drawArc(rect, startAngle, sweepAngle, true, paint)
            startAngle += sweepAngle
        }

        // Draw center hole for donut effect
        paint.color = resources.getColor(com.wordnote.app.R.color.card_background, null)
        paint.style = Paint.Style.FILL
        canvas.drawCircle(centerX, centerY, radius * 0.55f, paint)

        // Draw total in center
        textPaint.textSize = 22f * density
        canvas.drawText("${total.toInt()}", centerX, centerY - 2 * density, textPaint)
        textPaint.textSize = 10f * density
        textPaint.color = resources.getColor(com.wordnote.app.R.color.text_hint, null)
        canvas.drawText("个单词", centerX, centerY + 14 * density, textPaint)
        textPaint.color = Color.WHITE

        // Draw legend below chart
        val legendY = centerY + radius + 20 * density
        val legendItemWidth = 90 * density
        val cols = minOf(slices.size, 3)
        val rows = (slices.size + cols - 1) / cols
        val totalLegendWidth = cols * legendItemWidth
        val legendStartX = centerX - totalLegendWidth / 2

        slices.forEachIndexed { index, slice ->
            val col = index % cols
            val row = index / cols
            val x = legendStartX + col * legendItemWidth
            val y = legendY + row * 18 * density

            // Color dot
            paint.color = slice.color
            canvas.drawCircle(x + 6 * density, y, 4 * density, paint)

            // Label + count
            labelPaint.color = resources.getColor(com.wordnote.app.R.color.text_secondary, null)
            val label = if (slice.label.length > 5) slice.label.substring(0, 5) + ".." else slice.label
            canvas.drawText("$label ${slice.value}", x + 14 * density, y + 4 * density, labelPaint)
        }
    }
}
