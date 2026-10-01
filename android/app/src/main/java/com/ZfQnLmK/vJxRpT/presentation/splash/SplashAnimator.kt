package com.ZfQnLmK.vJxRpT.presentation.splash

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.animation.ValueAnimator
import android.view.View
import android.view.animation.AccelerateDecelerateInterpolator
import android.view.animation.DecelerateInterpolator
import android.view.animation.LinearInterpolator
import com.ZfQnLmK.vJxRpT.core.config.GameConfig

class SplashAnimator {

    private val running = ArrayList<AnimatorSet>()
    private val loops = ArrayList<ObjectAnimator>()

    fun prepare(vararg views: View) {
        for (view in views) {
            view.alpha = 0f
        }
    }

    fun playTitleEntrance(title: View) {
        title.scaleX = ENTRANCE_SCALE
        title.scaleY = ENTRANCE_SCALE
        val fade = ObjectAnimator.ofFloat(title, View.ALPHA, 0f, 1f)
        val scaleX = ObjectAnimator.ofFloat(title, View.SCALE_X, ENTRANCE_SCALE, 1f)
        val scaleY = ObjectAnimator.ofFloat(title, View.SCALE_Y, ENTRANCE_SCALE, 1f)
        val set = AnimatorSet()
        set.playTogether(fade, scaleX, scaleY)
        set.duration = GameConfig.SPLASH_TITLE_MS
        set.interpolator = DecelerateInterpolator()
        register(set)
        set.start()
    }

    fun playTaglineEntrance(tagline: View) {
        val travel = tagline.resources.displayMetrics.density * TAGLINE_TRAVEL_DP
        tagline.translationY = travel
        val fade = ObjectAnimator.ofFloat(tagline, View.ALPHA, 0f, TAGLINE_ALPHA)
        val slide = ObjectAnimator.ofFloat(tagline, View.TRANSLATION_Y, travel, 0f)
        val set = AnimatorSet()
        set.playTogether(fade, slide)
        set.duration = GameConfig.SPLASH_TITLE_MS
        set.startDelay = GameConfig.SPLASH_SUBTITLE_DELAY_MS
        set.interpolator = DecelerateInterpolator()
        register(set)
        set.start()
    }

    fun playIndicatorEntrance(indicator: View, caption: View) {
        val fadeIndicator = ObjectAnimator.ofFloat(indicator, View.ALPHA, 0f, 1f)
        val fadeCaption = ObjectAnimator.ofFloat(caption, View.ALPHA, 0f, 1f)
        val set = AnimatorSet()
        set.playTogether(fadeIndicator, fadeCaption)
        set.duration = INDICATOR_FADE_MS
        set.startDelay = INDICATOR_DELAY_MS
        set.interpolator = DecelerateInterpolator()
        register(set)
        set.start()
    }

    fun playBackdropDrift(backdrop: View) {
        backdrop.alpha = BACKDROP_ALPHA
        val drift = ObjectAnimator.ofFloat(backdrop, View.SCALE_X, 1f, BACKDROP_SCALE)
        drift.duration = BACKDROP_MS
        drift.repeatCount = ValueAnimator.INFINITE
        drift.repeatMode = ValueAnimator.REVERSE
        drift.interpolator = AccelerateDecelerateInterpolator()
        val driftY = ObjectAnimator.ofFloat(backdrop, View.SCALE_Y, 1f, BACKDROP_SCALE)
        driftY.duration = BACKDROP_MS
        driftY.repeatCount = ValueAnimator.INFINITE
        driftY.repeatMode = ValueAnimator.REVERSE
        driftY.interpolator = AccelerateDecelerateInterpolator()
        loops.add(drift)
        loops.add(driftY)
        drift.start()
        driftY.start()
    }

    fun playGridSweep(grid: View) {
        grid.alpha = GRID_ALPHA_LOW
        val sweep = ObjectAnimator.ofFloat(grid, View.ALPHA, GRID_ALPHA_LOW, GRID_ALPHA_HIGH)
        sweep.duration = GRID_MS
        sweep.repeatCount = ValueAnimator.INFINITE
        sweep.repeatMode = ValueAnimator.REVERSE
        sweep.interpolator = LinearInterpolator()
        loops.add(sweep)
        sweep.start()
    }

    fun playTitleBreath(title: View) {
        val breath = ObjectAnimator.ofFloat(title, View.SCALE_X, 1f, TITLE_BREATH)
        breath.duration = TITLE_BREATH_MS
        breath.startDelay = GameConfig.SPLASH_TITLE_MS
        breath.repeatCount = ValueAnimator.INFINITE
        breath.repeatMode = ValueAnimator.REVERSE
        breath.interpolator = AccelerateDecelerateInterpolator()
        val breathY = ObjectAnimator.ofFloat(title, View.SCALE_Y, 1f, TITLE_BREATH)
        breathY.duration = TITLE_BREATH_MS
        breathY.startDelay = GameConfig.SPLASH_TITLE_MS
        breathY.repeatCount = ValueAnimator.INFINITE
        breathY.repeatMode = ValueAnimator.REVERSE
        breathY.interpolator = AccelerateDecelerateInterpolator()
        loops.add(breath)
        loops.add(breathY)
        breath.start()
        breathY.start()
    }

    fun playCaptionPulse(caption: View) {
        val pulse = ObjectAnimator.ofFloat(caption, View.ALPHA, CAPTION_ALPHA_LOW, 1f)
        pulse.duration = CAPTION_MS
        pulse.startDelay = INDICATOR_DELAY_MS + INDICATOR_FADE_MS
        pulse.repeatCount = ValueAnimator.INFINITE
        pulse.repeatMode = ValueAnimator.REVERSE
        pulse.interpolator = AccelerateDecelerateInterpolator()
        loops.add(pulse)
        pulse.start()
    }

    private fun register(set: AnimatorSet) {
        running.add(set)
    }

    fun cancelAll() {
        for (set in running) {
            set.cancel()
        }
        running.clear()
        for (animator in loops) {
            animator.cancel()
        }
        loops.clear()
    }

    private companion object {
        const val ENTRANCE_SCALE = 0.92f
        const val TAGLINE_TRAVEL_DP = 14f
        const val TAGLINE_ALPHA = 0.9f
        const val INDICATOR_FADE_MS = 420L
        const val INDICATOR_DELAY_MS = 520L
        const val BACKDROP_ALPHA = 0.5f
        const val BACKDROP_SCALE = 1.06f
        const val BACKDROP_MS = 7000L
        const val GRID_ALPHA_LOW = 0.1f
        const val GRID_ALPHA_HIGH = 0.22f
        const val GRID_MS = 2600L
        const val TITLE_BREATH = 1.03f
        const val TITLE_BREATH_MS = 2200L
        const val CAPTION_ALPHA_LOW = 0.35f
        const val CAPTION_MS = 900L
    }
}
