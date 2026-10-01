package com.ZfQnLmK.vJxRpT.core.ui

import androidx.core.content.ContextCompat
import com.google.android.material.button.MaterialButton
import com.ZfQnLmK.vJxRpT.R

object ButtonDecor {

    fun primary(vararg buttons: MaterialButton) {
        decorate(R.drawable.btn_primary_gradient, buttons)
    }

    fun secondary(vararg buttons: MaterialButton) {
        decorate(R.drawable.btn_secondary_outline, buttons)
    }

    fun icon(vararg buttons: MaterialButton) {
        decorate(R.drawable.btn_icon_bg, buttons)
    }

    private fun decorate(drawableRes: Int, buttons: Array<out MaterialButton>) {
        for (button in buttons) {
            val start = button.paddingStart
            val top = button.paddingTop
            val end = button.paddingEnd
            val bottom = button.paddingBottom
            val drawable = ContextCompat.getDrawable(button.context, drawableRes)
            if (drawable != null) {
                button.background = drawable
                button.setPaddingRelative(start, top, end, bottom)
            }
        }
    }
}
