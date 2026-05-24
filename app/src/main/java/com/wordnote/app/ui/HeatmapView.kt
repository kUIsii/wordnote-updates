package com.wordnote.app.ui

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.RectF
import android.util.AttributeSet
import android.view.View
import com.wordnote.app.R
import java.util.Calendar

class HeatmapView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {

    private var data: Map<Long, Int> = emptyMap()
    private var maxCount = 0

    private var cellSize = 14f * resources.displayMetrics.density
    private val cellGap = 3f * resources.displayMetrics.density
    private val cornerRadius = 3f * resources.displayMetrics.density

    private val paint = Paint(Paint.ANTI_ALIAS_FLAG)

    // Color levels: empty, low, medium, high, very high
    private val colorLevels = intArrayOf(
        Color.parseColor("#EBEDF0"),  // empty
        Color.parseColor("#9BE9A8"),  // low
        Color.parseColor("#40C463"),  // medium
        Color.parseColor("#30A14E"),  // high
        Color.parseColor("#216E39")   // very high
    )

    fun setData(wordCounts: Map<Long, Int>) {
        data = wordCounts
        maxCount = wordCounts.values.maxOrNull() ?: 0
        invalidate()
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val parentWidth = MeasureSpec.getSize(widthMeasureSpec)
        // Dynamically calculate cell size to fit 53 columns within parent width
        cellSize = ((parentWidth - cellGap) / 53 - cellGap).coerceAtLeast(8f)
        val width = ((cellSize + cellGap) * 53 + cellGap).toInt()
        val height = ((cellSize + cellGap) * 7 + cellGap).toInt()
        setMeasuredDimension(width, height)
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        val calendar = Calendar.getInstance()
        val today = calendar.timeInMillis

        // Go back ~52 weeks
        calendar.add(Calendar.WEEK_OF_YEAR, -52)
        calendar.set(Calendar.DAY_OF_WEEK, calendar.firstDayOfWeek)
        calendar.set(Calendar.HOUR_OF_DAY, 0)
        calendar.set(Calendar.MINUTE, 0)
        calendar.set(Calendar.SECOND, 0)
        calendar.set(Calendar.MILLISECOND, 0)

        val startDate = calendar.timeInMillis

        var dayIndex = 0
        val totalDays = 53 * 7

        for (day in 0 until totalDays) {
            val dayTime = startDate + day * 24L * 60 * 60 * 1000
            if (dayTime > today) break

            val col = dayIndex / 7
            val row = dayIndex % 7

            val count = data[getDayKey(dayTime)] ?: 0
            val level = getLevel(count)

            val x = col * (cellSize + cellGap)
            val y = row * (cellSize + cellGap)

            paint.color = colorLevels[level]
            canvas.drawRoundRect(
                RectF(x, y, x + cellSize, y + cellSize),
                cornerRadius,
                cornerRadius,
                paint
            )

            dayIndex++
        }
    }

    private fun getLevel(count: Int): Int {
        if (count == 0) return 0
        if (maxCount == 0) return 0
        val ratio = count.toFloat() / maxCount
        return when {
            ratio <= 0.25f -> 1
            ratio <= 0.50f -> 2
            ratio <= 0.75f -> 3
            else -> 4
        }
    }

    private fun getDayKey(timestamp: Long): Long {
        val cal = Calendar.getInstance().apply {
            timeInMillis = timestamp
            set(Calendar.HOUR_OF_DAY, 0)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }
        return cal.timeInMillis
    }
}
