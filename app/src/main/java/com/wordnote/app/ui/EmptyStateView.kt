package com.wordnote.app.ui

import android.animation.ValueAnimator
import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Path
import android.util.AttributeSet
import android.view.View
import android.view.animation.LinearInterpolator

class EmptyStateView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {

    private val paint = Paint(Paint.ANTI_ALIAS_FLAG)
    private var phase = 0f
    private var animator: ValueAnimator? = null

    private var iconColor = Color.parseColor("#CCCCCC")
    private var waveColor = Color.parseColor("#E0E0E0")

    fun setColors(icon: Int, wave: Int) {
        iconColor = icon
        waveColor = wave
        invalidate()
    }

    fun startAnimation() {
        if (animator != null) return
        animator = ValueAnimator.ofFloat(0f, 360f).apply {
            duration = 3000
            repeatCount = ValueAnimator.INFINITE
            interpolator = LinearInterpolator()
            addUpdateListener {
                phase = it.animatedValue as Float
                invalidate()
            }
            start()
        }
    }

    fun stopAnimation() {
        animator?.cancel()
        animator = null
    }

    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()
        stopAnimation()
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        val centerX = width / 2f
        val centerY = height / 2f - 20f
        val radius = 40f * resources.displayMetrics.density

        // Draw floating book icon
        paint.color = iconColor
        paint.style = Paint.Style.FILL

        val bookWidth = radius * 1.2f
        val bookHeight = radius * 1.5f
        val bookTop = centerY - bookHeight / 2
        val floatOffset = Math.sin(Math.toRadians(phase.toDouble())).toFloat() * 8f

        canvas.drawRoundRect(
            centerX - bookWidth / 2, bookTop + floatOffset,
            centerX + bookWidth / 2, bookTop + bookHeight + floatOffset,
            8f, 8f, paint
        )

        // Book pages
        paint.color = Color.WHITE
        canvas.drawRoundRect(
            centerX - bookWidth / 2 + 4f, bookTop + floatOffset + 4f,
            centerX + bookWidth / 2 - 8f, bookTop + bookHeight + floatOffset - 4f,
            4f, 4f, paint
        )

        // Draw waves below
        paint.color = waveColor
        paint.style = Paint.Style.STROKE
        paint.strokeWidth = 2f * resources.displayMetrics.density

        for (i in 0..2) {
            val wavePath = Path()
            val yOffset = centerY + radius + 30f + i * 15f
            val waveAmplitude = 10f - i * 2f

            wavePath.moveTo(0f, yOffset)

            var x = 0f
            while (x < width) {
                val y = yOffset + Math.sin(Math.toRadians((x / width * 360 + phase + i * 60).toDouble())).toFloat() * waveAmplitude
                wavePath.lineTo(x, y)
                x += 5f
            }

            paint.alpha = 150 - i * 40
            canvas.drawPath(wavePath, paint)
        }
    }
}
