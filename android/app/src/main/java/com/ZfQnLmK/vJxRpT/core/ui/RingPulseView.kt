package com.ZfQnLmK.vJxRpT.core.ui

import android.animation.ValueAnimator
import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Path
import android.graphics.RectF
import android.util.AttributeSet
import android.view.View
import androidx.core.content.ContextCompat
import com.ZfQnLmK.vJxRpT.R
import com.ZfQnLmK.vJxRpT.core.config.GameConfig
import kotlin.math.cos
import kotlin.math.min
import kotlin.math.sin

class RingPulseView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {

    private val arcPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.STROKE
        strokeCap = Paint.Cap.ROUND
    }
    private val trackPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply { style = Paint.Style.STROKE }
    private val nodePaint = Paint(Paint.ANTI_ALIAS_FLAG)
    private val corePaint = Paint(Paint.ANTI_ALIAS_FLAG)
    private val coreGlowPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply { style = Paint.Style.STROKE }

    private val oval = RectF()
    private val corePath = Path()

    private val colorPrimary = ContextCompat.getColor(context, R.color.color_primary)
    private val colorSecondary = ContextCompat.getColor(context, R.color.color_secondary)
    private val colorDanger = ContextCompat.getColor(context, R.color.color_danger)
    private val colorGold = ContextCompat.getColor(context, R.color.color_gold)
    private val colorDim = ContextCompat.getColor(context, R.color.color_ring_dim)

    private val rotations = FloatArray(3)
    private val animators = arrayOfNulls<ValueAnimator>(3)
    private var pulseAnimator: ValueAnimator? = null
    private var pulse = 0f

    var settled: Boolean = false
    var loops: Int = ValueAnimator.INFINITE

    fun configure(settledMode: Boolean, loopCount: Int) {
        settled = settledMode
        loops = loopCount
        if (isAttachedToWindow) {
            cancelAnimators()
            startOrbit(0, GameConfig.ORBIT_SLOW_MS, 1f)
            startOrbit(1, GameConfig.ORBIT_MID_MS, -1f)
            startOrbit(2, GameConfig.ORBIT_FAST_MS, 1f)
            startPulse()
        }
    }

    init {
        importantForAccessibility = IMPORTANT_FOR_ACCESSIBILITY_NO
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        val side = min(measuredWidth, measuredHeight)
        if (side > 0) setMeasuredDimension(side, side)
    }

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
        startOrbit(0, GameConfig.ORBIT_SLOW_MS, 1f)
        startOrbit(1, GameConfig.ORBIT_MID_MS, -1f)
        startOrbit(2, GameConfig.ORBIT_FAST_MS, 1f)
        startPulse()
    }

    override fun onDetachedFromWindow() {
        cancelAnimators()
        super.onDetachedFromWindow()
    }

    fun cancelAnimators() {
        for (index in animators.indices) {
            animators[index]?.cancel()
            animators[index] = null
        }
        pulseAnimator?.cancel()
        pulseAnimator = null
    }

    private fun startOrbit(index: Int, duration: Long, direction: Float) {
        animators[index]?.cancel()
        val animator = ValueAnimator.ofFloat(0f, 360f * direction)
        animator.duration = if (settled) SETTLE_SPIN_MS else duration
        animator.repeatCount = loops
        animator.interpolator = null
        animator.addUpdateListener { value ->
            try {
                val next = value.animatedValue
                if (next is Float) {
                    rotations[index] = next
                    invalidate()
                }
            } catch (e: Exception) {
                invalidate()
            }
        }
        animators[index] = animator
        animator.start()
    }

    private fun startPulse() {
        pulseAnimator?.cancel()
        val animator = ValueAnimator.ofFloat(0f, 1f)
        animator.duration = GameConfig.CORE_PULSE_MS
        animator.repeatCount = if (loops == ValueAnimator.INFINITE) ValueAnimator.INFINITE else SETTLE_PULSE_LOOPS
        animator.repeatMode = ValueAnimator.REVERSE
        animator.addUpdateListener { value ->
            try {
                val next = value.animatedValue
                if (next is Float) {
                    pulse = next
                    invalidate()
                }
            } catch (e: Exception) {
                pulse = 0f
            }
        }
        pulseAnimator = animator
        animator.start()
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        val side = min(width, height).toFloat()
        if (side <= 0f) return
        val cx = width / 2f
        val cy = height / 2f
        val inner = side * 0.92f
        val thickness = inner * 0.062f
        arcPaint.strokeWidth = thickness
        trackPaint.strokeWidth = thickness * 0.55f

        val radii = floatArrayOf(inner * 0.20f, inner * 0.31f, inner * 0.42f)
        val colors = intArrayOf(colorPrimary, colorSecondary, colorDanger)
        val spans = if (settled) floatArrayOf(96f, 108f, 120f) else floatArrayOf(72f, 86f, 64f)

        for (ring in radii.indices) {
            val radius = radii[ring]
            oval.set(cx - radius, cy - radius, cx + radius, cy + radius)
            trackPaint.color = alpha(colorDim, 0.6f)
            canvas.drawCircle(cx, cy, radius, trackPaint)
            arcPaint.color = colors[ring]
            val base = rotations[ring]
            val span = spans[ring]
            canvas.drawArc(oval, base, span, false, arcPaint)
            canvas.drawArc(oval, base + 180f, span * 0.6f, false, arcPaint)
        }

        val nodeRadius = thickness * 0.42f
        val nodeDistance = radii[2] + thickness * 0.85f
        for (index in 0 until 4) {
            val angle = Math.toRadians((index * 90f - 90f).toDouble())
            val x = cx + nodeDistance * cos(angle).toFloat()
            val y = cy + nodeDistance * sin(angle).toFloat()
            nodePaint.color = if (settled) colorSecondary else alpha(colorSecondary, 0.45f)
            canvas.drawCircle(x, y, nodeRadius, nodePaint)
        }

        val coreRadius = inner * 0.072f * (0.86f + pulse * 0.24f)
        coreGlowPaint.strokeWidth = inner * 0.011f
        coreGlowPaint.color = alpha(colorGold, 0.5f)
        canvas.drawCircle(cx, cy, coreRadius * 1.8f, coreGlowPaint)
        corePath.reset()
        corePath.moveTo(cx, cy - coreRadius)
        corePath.lineTo(cx + coreRadius, cy)
        corePath.lineTo(cx, cy + coreRadius)
        corePath.lineTo(cx - coreRadius, cy)
        corePath.close()
        corePaint.color = colorGold
        canvas.drawPath(corePath, corePaint)
    }

    private companion object {
        const val SETTLE_SPIN_MS = 1100L
        const val SETTLE_PULSE_LOOPS = 1
    }

    private fun alpha(base: Int, factor: Float): Int {
        val value = (Color.alpha(base) * factor).toInt().coerceIn(0, 255)
        return Color.argb(value, Color.red(base), Color.green(base), Color.blue(base))
    }
}
