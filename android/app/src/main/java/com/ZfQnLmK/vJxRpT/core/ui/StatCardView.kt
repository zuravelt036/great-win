package com.ZfQnLmK.vJxRpT.core.ui

import android.content.Context
import android.content.res.ColorStateList
import android.util.AttributeSet
import android.view.Gravity
import android.view.LayoutInflater
import android.widget.LinearLayout
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import com.ZfQnLmK.vJxRpT.R
import com.ZfQnLmK.vJxRpT.databinding.ViewStatCardBinding

class StatCardView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : LinearLayout(context, attrs, defStyleAttr) {

    private val binding = ViewStatCardBinding.inflate(LayoutInflater.from(context), this)

    init {
        orientation = VERTICAL
        gravity = Gravity.CENTER
        background = ContextCompat.getDrawable(context, R.drawable.shape_card_surface)
        val horizontal = resources.getDimensionPixelSize(R.dimen.space_lg)
        val vertical = resources.getDimensionPixelSize(R.dimen.space_md)
        setPadding(horizontal, vertical, horizontal, vertical)
        minimumWidth = resources.getDimensionPixelSize(R.dimen.space_xxl) * 3
        minimumHeight = resources.getDimensionPixelSize(R.dimen.btn_min_touch)
    }

    fun bind(value: String, label: String, accentColor: Int) {
        binding.statValue.text = value
        binding.statLabel.text = label
        binding.statValue.setTextColor(accentColor)
        binding.statAccent.backgroundTintList = ColorStateList.valueOf(accentColor)
        ViewCompat.setStateDescription(this, label + " " + value)
    }

    fun bindResource(value: String, labelRes: Int, accentRes: Int) {
        bind(value, context.getString(labelRes), ContextCompat.getColor(context, accentRes))
    }
}
