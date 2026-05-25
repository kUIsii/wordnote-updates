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
    private var numWeeks = 20

    private var cellSize = 16f * resources.displayMetrics.density
    private val cellGap = 4f * resources.displayMetrics.density
    private val cornerRadius = 4f * resources.displayMetrics.density

    private val paint = Paint(Paint.ANTI_ALIAS_FLAG)

    // Color levels: empty, low, medium, high, very high
    private val colorLevels = intArrayOf(
        Color.parseColor("#EBEDF0"),  // empty
        Color.parseColor("#9BE9A8"),  // low
        Color.parseColor("#40C463"),  // medium
        Color.parseColor("#30A14E"),  // high
        Color.parseColor("#216E39")   // very high
    )

    // Click listener interface
    interface OnDayClickListener {
        fun onDayClick(date: String, count: Int)
    }

    private var onDayClickListener: OnDayClickListener? = null
    private var startDate: Long = 0

    fun setOnDayClickListener(listener: OnDayClickListener?) {
        onDayClickListener = listener
    }

    fun setData(wordCounts: Map<Long, Int>, weeks: Int = 20) {
        data = wordCounts
        maxCount = wordCounts.values.maxOrNull() ?: 0
        numWeeks = weeks
        requestLayout()
        invalidate()
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val parentWidth = MeasureSpec.getSize(widthMeasureSpec)
        cellSize = ((parentWidth - cellGap) / numWeeks - cellGap).coerceAtLeast(10f * resources.displayMetrics.density)
        val width = ((cellSize + cellGap) * numWeeks + cellGap).toInt()
        val height = ((cellSize + cellGap) * 7 + cellGap).toInt()
        setMeasuredDimension(width, height)
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        val calendar = Calendar.getInstance()
        val today = calendar.timeInMillis

        calendar.add(Calendar.WEEK_OF_YEAR, -numWeeks)
        calendar.set(Calendar.DAY_OF_WEEK, calendar.firstDayOfWeek)
        calendar.set(Calendar.HOUR_OF_DAY, 0)
        calendar.set(Calendar.MINUTE, 0)
        calendar.set(Calendar.SECOND, 0)
        calendar.set(Calendar.MILLISECOND, 0)

        startDate = calendar.timeInMillis

        var dayIndex = 0
        val totalDays = (numWeeks + 1) * 7

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

    override fun onTouchEvent(event: android.view.MotionEvent): Boolean {
        if (event.action == android.view.MotionEvent.ACTION_DOWN) {
            val x = event.x
            val y = event.y

            // Calculate which cell was clicked
            val col = (x / (cellSize + cellGap)).toInt()
            val row = (y / (cellSize + cellGap)).toInt()

            // Check if click is within valid range
            if (col in 0 until numWeeks && row in 0 until 7) {
                val dayIndex = col * 7 + row
                val dayTime = startDate + dayIndex * 24L * 60 * 60 * 1000
                val today = Calendar.getInstance().timeInMillis

                if (dayTime <= today) {
                    val count = data[getDayKey(dayTime)] ?: 0
                    val dateFormat = java.text.SimpleDateFormat("yyyy-MM-dd", java.util.Locale.getDefault())
                    val dateStr = dateFormat.format(java.util.Date(dayTime))
                    onDayClickListener?.onDayClick(dateStr, count)
                    return true
                }
            }
        }
        return super.onTouchEvent(event)
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
