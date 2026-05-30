package com.wordnote.app.ui

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.RectF
import android.util.AttributeSet
import android.view.View

class CircularProgressBar @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {

    private var progress = 0f
    private var strokeWidth = 12f
    private var bgColor = Color.parseColor("#20000000")
    private var progressColor = Color.WHITE

    private val bgPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.STROKE
        isAntiAlias = true
        color = bgColor
        this@apply.strokeWidth = this@CircularProgressBar.strokeWidth * resources.displayMetrics.density
        strokeCap = Paint.Cap.ROUND
    }

    private val progressPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.STROKE
        isAntiAlias = true
        color = progressColor
        this@apply.strokeWidth = this@CircularProgressBar.strokeWidth * resources.displayMetrics.density
        strokeCap = Paint.Cap.ROUND
    }

    private val rect = RectF()

    fun setProgress(value: Float) {
        progress = value.coerceIn(0f, 1f)
        invalidate()
    }

    fun setProgressColor(color: Int) {
        progressColor = color
        progressPaint.color = color
        invalidate()
    }

    fun setBgColor(color: Int) {
        bgColor = color
        bgPaint.color = color
        invalidate()
    }

    fun setStrokeWidth(width: Float) {
        strokeWidth = width
        val px = width * resources.displayMetrics.density
        bgPaint.strokeWidth = px
        progressPaint.strokeWidth = px
        invalidate()
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        val padding = strokeWidth * resources.displayMetrics.density / 2
        rect.set(padding, padding, width - padding, height - padding)

        canvas.drawArc(rect, 0f, 360f, false, bgPaint)
        canvas.drawArc(rect, -90f, progress * 360f, false, progressPaint)
    }
}
