package com.ZfQnLmK.vJxRpT.presentation.schemeselect

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.recyclerview.widget.RecyclerView
import com.ZfQnLmK.vJxRpT.R
import com.ZfQnLmK.vJxRpT.databinding.ItemSchemeBinding

class SchemeAdapter(
    private val onSelect: (String) -> Unit
) : RecyclerView.Adapter<SchemeAdapter.SchemeHolder>() {

    private var items: List<SchemeCardModel> = emptyList()
    private var selectedId: String = ""

    fun submit(models: List<SchemeCardModel>, selected: String) {
        items = models
        selectedId = selected
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SchemeHolder {
        val binding = ItemSchemeBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return SchemeHolder(binding)
    }

    override fun getItemCount(): Int = items.size

    override fun onBindViewHolder(holder: SchemeHolder, position: Int) {
        val model = items.getOrNull(position) ?: return
        holder.bind(model, model.id == selectedId, onSelect)
    }

    class SchemeHolder(private val binding: ItemSchemeBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(model: SchemeCardModel, selected: Boolean, onSelect: (String) -> Unit) {
            val context = binding.root.context
            binding.schemeName.text = model.name
            binding.schemeMeta.text =
                context.getString(R.string.fmt_segments, model.segments, model.moveLimit)
            binding.schemePreview.setImageResource(previewFor(model.previewIndex))
            binding.schemeStars.text = starLabel(model.stars)
            binding.schemeLock.visibility = if (model.unlocked) android.view.View.GONE else android.view.View.VISIBLE
            binding.root.alpha = if (model.unlocked) 1f else LOCKED_ALPHA
            binding.root.isEnabled = model.unlocked
            val strokeColor = when {
                !model.unlocked -> R.color.color_ring_idle
                selected -> R.color.color_secondary
                else -> R.color.color_primary
            }
            binding.schemeCard.setStrokeColor(ContextCompat.getColor(context, strokeColor))
            renderPips(model.difficulty)
            ViewCompat.setStateDescription(
                binding.root,
                context.getString(if (model.unlocked) R.string.state_unlocked else R.string.state_locked)
            )
            binding.root.setOnClickListener {
                if (model.unlocked) onSelect(model.id)
            }
        }

        private fun renderPips(difficulty: Int) {
            val context = binding.root.context
            val pips = listOf(binding.schemePip1, binding.schemePip2, binding.schemePip3)
            val colors = listOf(R.color.color_secondary, R.color.color_gold, R.color.color_danger)
            for (index in pips.indices) {
                val active = index < difficulty
                val drawable = if (active) R.drawable.chip_pip_on else R.drawable.chip_pip_dim
                pips[index].setBackgroundResource(drawable)
                if (active) {
                    pips[index].backgroundTintList =
                        ContextCompat.getColorStateList(context, colors[index])
                } else {
                    pips[index].backgroundTintList = null
                }
            }
        }

        private fun previewFor(index: Int): Int = when (index) {
            1 -> R.drawable.sprite_scheme_b
            2 -> R.drawable.sprite_scheme_c
            else -> R.drawable.sprite_scheme_a
        }

        private fun starLabel(stars: Int): String {
            val builder = StringBuilder()
            for (index in 0 until TOTAL_STARS) {
                builder.append(if (index < stars) FILLED else EMPTY)
                if (index < TOTAL_STARS - 1) builder.append(" ")
            }
            return builder.toString()
        }

        private companion object {
            const val LOCKED_ALPHA = 0.45f
            const val TOTAL_STARS = 3
            const val FILLED = "★"
            const val EMPTY = "·"
        }
    }
}
