package com.ZfQnLmK.vJxRpT.core.ui

import android.animation.ObjectAnimator
import android.animation.PropertyValuesHolder
import android.animation.StateListAnimator
import android.view.View
import android.view.animation.OvershootInterpolator

object PressScaleHelper {

    private const val PRESSED_SCALE = 0.96f
    private const val DOWN_MS = 90L
    private const val UP_MS = 160L

    fun apply(vararg views: View) {
        for (view in views) {
            val animator = StateListAnimator()
            animator.addState(intArrayOf(android.R.attr.state_pressed), scaleAnimator(view, PRESSED_SCALE, DOWN_MS, false))
            animator.addState(IntArray(0), scaleAnimator(view, 1f, UP_MS, true))
            view.stateListAnimator = animator
        }
    }

    private fun scaleAnimator(view: View, target: Float, duration: Long, overshoot: Boolean): ObjectAnimator {
        val animator = ObjectAnimator.ofPropertyValuesHolder(
            view,
            PropertyValuesHolder.ofFloat(View.SCALE_X, target),
            PropertyValuesHolder.ofFloat(View.SCALE_Y, target)
        )
        animator.duration = duration
        if (overshoot) animator.interpolator = OvershootInterpolator(1.1f)
        return animator
    }
}
