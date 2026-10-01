package com.ZfQnLmK.vJxRpT.core.ui

import android.animation.ValueAnimator
import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Path
import android.graphics.RectF
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import android.view.animation.OvershootInterpolator
import androidx.core.content.ContextCompat
import com.ZfQnLmK.vJxRpT.R
import com.ZfQnLmK.vJxRpT.core.config.GameConfig
import kotlin.math.abs
import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.hypot
import kotlin.math.min
import kotlin.math.sin

class RingBoardView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {

    private val framePaint = Paint(Paint.ANTI_ALIAS_FLAG).apply { style = Paint.Style.STROKE }
    private val trackPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply { style = Paint.Style.STROKE }
    private val arcPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.STROKE
        strokeCap = Paint.Cap.BUTT
    }
    private val glowPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.STROKE
        strokeCap = Paint.Cap.BUTT
    }
    private val nodePaint = Paint(Paint.ANTI_ALIAS_FLAG)
    private val nodeStrokePaint = Paint(Paint.ANTI_ALIAS_FLAG).apply { style = Paint.Style.STROKE }
    private val corePaint = Paint(Paint.ANTI_ALIAS_FLAG)
    private val coreGlowPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply { style = Paint.Style.STROKE }

    private val oval = RectF()
    private val corePath = Path()

    private val frameInset = resources.getDimension(R.dimen.board_frame)
    private val maxSide = resources.getDimensionPixelSize(R.dimen.board_max)

    private val colorPrimary = color(R.color.color_primary)
    private val colorPrimaryBright = color(R.color.color_primary_bright)
    private val colorSecondary = color(R.color.color_secondary)
    private val colorGold = color(R.color.color_gold)
    private val colorDanger = color(R.color.color_danger)
    private val colorIdle = color(R.color.color_ring_idle)
    private val colorDim = color(R.color.color_ring_dim)
    private val colorStroke = color(R.color.color_stroke)

    private var render: BoardRender = BoardRender.EMPTY
    private var displayOffsets = FloatArray(GameConfig.RING_COUNT)
    private val ringAnimators = arrayOfNulls<ValueAnimator>(GameConfig.RING_COUNT)
    private var energyAnimator: ValueAnimator? = null
    private var corePulseAnimator: ValueAnimator? = null
    private var shakeAnimator: ValueAnimator? = null
    private var energyProgress = 1f
    private var corePulse = 0f

    var interactive: Boolean = true
    var onRingTap: ((Int) -> Unit)? = null
    var onRingLongTap: ((Int) -> Unit)? = null

    private var downRing = -1
    private var downTime = 0L

    init {
        isClickable = true
        isFocusable = true
        contentDescription = context.getString(R.string.cd_board)
    }

    private fun color(id: Int): Int = ContextCompat.getColor(context, id)

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val width = MeasureSpec.getSize(widthMeasureSpec)
        val height = MeasureSpec.getSize(heightMeasureSpec)
        var side = min(width, height)
        if (side <= 0) side = if (width > 0) width else height
        if (side <= 0) side = maxSide
        if (side > maxSide) side = maxSide
        setMeasuredDimension(side, side)
    }

    fun submit(state: BoardRender, animatedRing: Int) {
        val previous = render
        render = state
        if (displayOffsets.size < state.offsets.size) {
            displayOffsets = FloatArray(state.offsets.size)
        }
        for (ring in state.offsets.indices) {
            val target = state.offsets[ring].toFloat()
            if (ring == animatedRing && previous.segments == state.segments) {
                animateRing(ring, target, state.segments)
            } else {
                ringAnimators[ring]?.cancel()
                displayOffsets[ring] = target
            }
        }
        if (state.mood == BoardMood.CHECKING || state.mood == BoardMood.WIN) {
            startEnergySweep()
        } else {
            energyAnimator?.cancel()
            energyProgress = 1f
        }
        if (isAttachedToWindow) startCorePulse()
        invalidate()
    }

    fun shake() {
        shakeAnimator?.cancel()
        val amplitude = resources.displayMetrics.density * 6f
        val animator = ValueAnimator.ofFloat(0f, 1f)
        animator.duration = GameConfig.SHAKE_MS
        animator.addUpdateListener { value ->
            try {
                val fraction = value.animatedFraction
                translationX = amplitude * sin(fraction * 6.0 * Math.PI).toFloat() * (1f - fraction)
            } catch (e: Exception) {
                translationX = 0f
            }
        }
        shakeAnimator = animator
        animator.start()
    }

    private fun animateRing(ring: Int, target: Float, segments: Int) {
        ringAnimators[ring]?.cancel()
        var from = displayOffsets[ring]
        if (target - from > segments / 2f) from += segments
        if (from - target > segments / 2f) from -= segments
        val animator = ValueAnimator.ofFloat(from, target)
        animator.duration = GameConfig.ROTATE_ANIM_MS
        animator.interpolator = OvershootInterpolator(0.8f)
        animator.addUpdateListener { value ->
            try {
                val next = value.animatedValue
                if (next is Float) {
                    displayOffsets[ring] = next
                    invalidate()
                }
            } catch (e: Exception) {
                invalidate()
            }
        }
        ringAnimators[ring] = animator
        animator.start()
    }

    private fun startEnergySweep() {
        energyAnimator?.cancel()
        val animator = ValueAnimator.ofFloat(0f, 1f)
        animator.duration = GameConfig.CHECK_ANIM_MS
        animator.addUpdateListener { value ->
            try {
                val next = value.animatedValue
                if (next is Float) {
                    energyProgress = next
                    invalidate()
                }
            } catch (e: Exception) {
                energyProgress = 1f
            }
        }
        energyAnimator = animator
        animator.start()
    }

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
        startCorePulse()
    }

    private fun startCorePulse() {
        corePulseAnimator?.cancel()
        val animator = ValueAnimator.ofFloat(0f, 1f)
        animator.duration = GameConfig.CORE_PULSE_MS
        animator.repeatCount = CORE_PULSE_LOOPS
        animator.repeatMode = ValueAnimator.REVERSE
        animator.addUpdateListener { value ->
            try {
                val next = value.animatedValue
                if (next is Float) {
                    corePulse = next
                    invalidate()
                }
            } catch (e: Exception) {
                corePulse = 0f
            }
        }
        corePulseAnimator = animator
        animator.start()
    }

    override fun onDetachedFromWindow() {
        cancelAnimators()
        super.onDetachedFromWindow()
    }

    fun cancelAnimators() {
        for (index in ringAnimators.indices) {
            ringAnimators[index]?.cancel()
            ringAnimators[index] = null
        }
        energyAnimator?.cancel()
        energyAnimator = null
        corePulseAnimator?.cancel()
        corePulseAnimator = null
        shakeAnimator?.cancel()
        shakeAnimator = null
        translationX = 0f
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        val side = min(width, height).toFloat()
        if (side <= 0f) return
        val cx = width / 2f
        val cy = height / 2f
        val inner = side - 2f * frameInset
        if (inner <= 0f) return

        framePaint.strokeWidth = resources.displayMetrics.density * 2f
        framePaint.color = colorStroke
        canvas.drawCircle(cx, cy, inner / 2f, framePaint)

        val thickness = inner * 0.072f
        val radii = floatArrayOf(inner * 0.18f, inner * 0.29f, inner * 0.40f)
        arcPaint.strokeWidth = thickness
        glowPaint.strokeWidth = thickness * 1.55f
        trackPaint.strokeWidth = thickness

        val segments = if (render.segments > 0) render.segments else 8
        val sweep = 360f / segments
        val gap = sweep * 0.14f

        for (ring in radii.indices) {
            val radius = radii[ring]
            oval.set(cx - radius, cy - radius, cx + radius, cy + radius)
            trackPaint.color = withAlpha(colorDim, 0.55f)
            canvas.drawCircle(cx, cy, radius, trackPaint)

            val base = render.baseConductors.getOrNull(ring) ?: emptySet()
            if (base.isEmpty()) continue
            val offset = displayOffsets.getOrElse(ring) { 0f }
            val live = render.energized.getOrNull(ring) ?: emptySet()
            val snapped = render.offsets.getOrNull(ring) ?: 0

            for (sector in base) {
                val absolute = ((sector + snapped) % segments + segments) % segments
                val lit = live.contains(absolute) && render.mood != BoardMood.LOSE
                val start = (sector + offset) * sweep - 90f + gap / 2f
                val span = sweep - gap
                arcPaint.color = when {
                    render.mood == BoardMood.LOSE -> withAlpha(colorDim, 0.9f)
                    lit -> colorSecondary
                    else -> if (ring == 2) colorPrimaryBright else colorPrimary
                }
                if (lit) {
                    glowPaint.color = withAlpha(colorSecondary, 0.22f * energyProgress)
                    canvas.drawArc(oval, start, span, false, glowPaint)
                }
                canvas.drawArc(oval, start, span, false, arcPaint)
            }
        }

        drawTargets(canvas, cx, cy, radii[2], thickness, sweep)
        drawCore(canvas, cx, cy, inner)
    }

    private fun drawTargets(canvas: Canvas, cx: Float, cy: Float, outer: Float, thickness: Float, sweep: Float) {
        val targets = if (render.targets.isEmpty()) listOf(0) else render.targets
        val nodeRadius = thickness * 0.45f
        val nodeDistance = outer + thickness * 0.78f
        nodeStrokePaint.strokeWidth = thickness * 0.16f
        for (target in targets) {
            val angle = Math.toRadians(((target + 0.5f) * sweep - 90f).toDouble())
            val x = cx + nodeDistance * cos(angle).toFloat()
            val y = cy + nodeDistance * sin(angle).toFloat()
            val active = render.reached.contains(target) && render.mood != BoardMood.LOSE
            if (active) {
                nodePaint.color = withAlpha(colorSecondary, 0.28f)
                canvas.drawCircle(x, y, nodeRadius * 1.8f, nodePaint)
                nodePaint.color = colorSecondary
                canvas.drawCircle(x, y, nodeRadius, nodePaint)
            } else {
                nodePaint.color = withAlpha(colorDim, 0.85f)
                canvas.drawCircle(x, y, nodeRadius, nodePaint)
                nodeStrokePaint.color = if (render.mood == BoardMood.LOSE) colorDanger else colorIdle
                canvas.drawCircle(x, y, nodeRadius, nodeStrokePaint)
            }
        }
    }

    private fun drawCore(canvas: Canvas, cx: Float, cy: Float, inner: Float) {
        val pulse = 0.88f + corePulse * 0.2f
        val radius = inner * 0.075f * pulse
        coreGlowPaint.strokeWidth = inner * 0.012f
        coreGlowPaint.color = withAlpha(if (render.mood == BoardMood.LOSE) colorDanger else colorGold, 0.45f)
        canvas.drawCircle(cx, cy, radius * 1.7f, coreGlowPaint)
        corePath.reset()
        corePath.moveTo(cx, cy - radius)
        corePath.lineTo(cx + radius, cy)
        corePath.lineTo(cx, cy + radius)
        corePath.lineTo(cx - radius, cy)
        corePath.close()
        corePaint.color = if (render.mood == BoardMood.LOSE) colorDanger else colorGold
        canvas.drawPath(corePath, corePaint)
    }

    private fun withAlpha(base: Int, factor: Float): Int {
        val alpha = (Color.alpha(base) * factor).toInt().coerceIn(0, 255)
        return Color.argb(alpha, Color.red(base), Color.green(base), Color.blue(base))
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        if (!interactive) return false
        when (event.actionMasked) {
            MotionEvent.ACTION_DOWN -> {
                downRing = ringAt(event.x, event.y)
                downTime = System.currentTimeMillis()
                return downRing >= 0
            }
            MotionEvent.ACTION_UP -> {
                val ring = downRing
                val elapsed = System.currentTimeMillis() - downTime
                downRing = -1
                if (ring >= 0) {
                    performClick()
                    if (elapsed > LONG_PRESS_MS) onRingLongTap?.invoke(ring) else onRingTap?.invoke(ring)
                    return true
                }
                return false
            }
            MotionEvent.ACTION_CANCEL -> {
                downRing = -1
                return false
            }
        }
        return super.onTouchEvent(event)
    }

    override fun performClick(): Boolean {
        super.performClick()
        return true
    }

    private fun ringAt(x: Float, y: Float): Int {
        val side = min(width, height).toFloat()
        if (side <= 0f) return -1
        val inner = side - 2f * frameInset
        val cx = width / 2f
        val cy = height / 2f
        val distance = hypot(x - cx, y - cy)
        val thickness = inner * 0.072f
        val radii = floatArrayOf(inner * 0.18f, inner * 0.29f, inner * 0.40f)
        if (distance < radii[0] - thickness) return 0
        var best = -1
        var bestDelta = Float.MAX_VALUE
        for (ring in radii.indices) {
            val delta = abs(distance - radii[ring])
            if (delta < bestDelta) {
                bestDelta = delta
                best = ring
            }
        }
        return if (bestDelta <= thickness * 1.4f) best else -1
    }

    fun angleLabel(x: Float, y: Float): Float {
        val cx = width / 2f
        val cy = height / 2f
        return Math.toDegrees(atan2((y - cy).toDouble(), (x - cx).toDouble())).toFloat()
    }

    private companion object {
        const val LONG_PRESS_MS = 420L
        const val CORE_PULSE_LOOPS = 3
    }
}
