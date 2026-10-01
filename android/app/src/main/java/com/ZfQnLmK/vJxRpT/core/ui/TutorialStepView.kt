package com.ZfQnLmK.vJxRpT.core.ui

import android.content.Context
import android.util.AttributeSet
import android.view.Gravity
import android.view.LayoutInflater
import android.widget.LinearLayout
import androidx.core.content.ContextCompat
import com.ZfQnLmK.vJxRpT.R
import com.ZfQnLmK.vJxRpT.databinding.ViewTutorialStepBinding

class TutorialStepView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : LinearLayout(context, attrs, defStyleAttr) {

    private val binding = ViewTutorialStepBinding.inflate(LayoutInflater.from(context), this)

    init {
        orientation = HORIZONTAL
        gravity = Gravity.CENTER_VERTICAL
        background = ContextCompat.getDrawable(context, R.drawable.shape_card_surface)
        val pad = resources.getDimensionPixelSize(R.dimen.space_lg)
        setPadding(pad, pad, pad, pad)
    }

    fun bind(number: Int, title: String, body: String, useNodeSprite: Boolean) {
        binding.stepNumber.text = number.toString()
        binding.stepTitle.text = title
        binding.stepBody.text = body
        val sprite = if (useNodeSprite) R.drawable.sprite_node else R.drawable.sprite_ring_hint
        binding.stepSprite.setImageResource(sprite)
        val description = if (useNodeSprite) R.string.cd_node else R.string.cd_ring_hint
        binding.stepSprite.contentDescription = context.getString(description)
    }
}
