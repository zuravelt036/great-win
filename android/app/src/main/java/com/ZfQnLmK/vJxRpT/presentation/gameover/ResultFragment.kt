package com.ZfQnLmK.vJxRpT.presentation.gameover

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.OvershootInterpolator
import android.widget.ImageView
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.ZfQnLmK.vJxRpT.R
import com.ZfQnLmK.vJxRpT.core.config.GameConfig
import com.ZfQnLmK.vJxRpT.core.di.ServiceLocator
import com.ZfQnLmK.vJxRpT.core.di.ViewModelFactory
import com.ZfQnLmK.vJxRpT.core.navigation.Navigator
import com.ZfQnLmK.vJxRpT.core.ui.BoardRender
import com.ZfQnLmK.vJxRpT.core.ui.ButtonDecor
import com.ZfQnLmK.vJxRpT.core.ui.PressScaleHelper
import com.ZfQnLmK.vJxRpT.databinding.FragmentResultBinding
import com.ZfQnLmK.vJxRpT.presentation.game.GameFragment
import kotlinx.coroutines.launch

class ResultFragment : Fragment() {

    private var _binding: FragmentResultBinding? = null

    private val viewModel: ResultViewModel by viewModels {
        ViewModelFactory {
            ResultViewModel(
                ServiceLocator.lastRound,
                ServiceLocator.schemeRepository(requireContext()),
                ServiceLocator.progressRepository(requireContext()),
                ServiceLocator.checkCircuit
            )
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val created = FragmentResultBinding.inflate(inflater, container, false)
        _binding = created
        return created.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val views = _binding ?: return
        ButtonDecor.primary(views.resultPlayAgain)
        ButtonDecor.secondary(views.resultNext, views.resultMenu)
        PressScaleHelper.apply(views.resultPlayAgain, views.resultNext, views.resultMenu)
        views.resultBoard.interactive = false
        views.resultPlayAgain.setOnClickListener { replay(viewModel.currentSchemeId()) }
        views.resultNext.setOnClickListener { replay(viewModel.nextSchemeId()) }
        views.resultMenu.setOnClickListener { goToMenu() }
        observeState()
    }

    private fun observeState() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.uiState.collect { state -> render(state) }
            }
        }
    }

    private fun render(state: ResultUiState) {
        val views = _binding ?: return
        val context = context ?: return
        if (!state.hasRound) {
            goToMenu()
            return
        }
        val titleRes = if (state.isWin) R.string.result_win else R.string.result_lose
        val titleColor = if (state.isWin) R.color.color_secondary else R.color.color_danger
        views.resultTitle.setText(titleRes)
        views.resultTitle.setTextColor(ContextCompat.getColor(context, titleColor))
        views.resultRoot.setBackgroundResource(
            if (state.isWin) R.drawable.bg_result_gradient_win else R.drawable.bg_result_gradient_lose
        )
        views.resultSubtitle.text =
            getString(R.string.fmt_scheme_sub, state.schemeName, state.segments)
        views.resultSpark.visibility = if (state.isWin) View.VISIBLE else View.GONE
        views.resultNext.visibility = if (state.isWin) View.VISIBLE else View.GONE
        views.resultBoard.submit(
            BoardRender(
                segments = state.segmentsCount,
                baseConductors = state.baseConductors,
                offsets = state.offsets,
                energized = state.energized,
                targets = state.targets,
                reached = state.reached,
                mood = state.mood
            ),
            -1
        )
        renderStars(state.stars)
        renderStats(state)
        playTitleEntrance()
    }

    private fun renderStars(stars: Int) {
        val views = _binding ?: return
        val icons = listOf(views.resultStar1, views.resultStar2, views.resultStar3)
        for (index in icons.indices) {
            val filled = index < stars
            icons[index].setImageResource(if (filled) R.drawable.ic_star_on else R.drawable.ic_star_off)
            popStar(icons[index], index)
        }
        ViewCompat.setStateDescription(
            views.resultStars,
            getString(R.string.fmt_stars_state, stars)
        )
    }

    private fun popStar(icon: ImageView, index: Int) {
        icon.scaleX = STAR_FROM
        icon.scaleY = STAR_FROM
        icon.animate().cancel()
        icon.animate()
            .scaleX(1f)
            .scaleY(1f)
            .setStartDelay(GameConfig.STAR_STAGGER_MS * index)
            .setDuration(STAR_POP_MS)
            .setInterpolator(OvershootInterpolator(2f))
            .start()
    }

    private fun renderStats(state: ResultUiState) {
        val views = _binding ?: return
        val context = context ?: return
        views.resultStatMoves.bind(
            pad(state.movesLeft),
            getString(R.string.label_moves_left),
            ContextCompat.getColor(context, R.color.color_secondary)
        )
        views.resultStatTargets.bind(
            getString(R.string.fmt_targets, state.reachedTargets, state.totalTargets),
            getString(R.string.label_targets),
            ContextCompat.getColor(context, R.color.color_gold)
        )
        views.resultStatScore.bind(
            state.score.toString(),
            getString(R.string.label_score),
            ContextCompat.getColor(context, R.color.color_text_primary)
        )
    }

    private fun playTitleEntrance() {
        val views = _binding ?: return
        views.resultTitle.animate().cancel()
        views.resultTitle.scaleX = TITLE_FROM
        views.resultTitle.scaleY = TITLE_FROM
        views.resultTitle.alpha = 0f
        views.resultTitle.animate()
            .scaleX(1f)
            .scaleY(1f)
            .alpha(1f)
            .setDuration(TITLE_POP_MS)
            .setInterpolator(OvershootInterpolator(1.4f))
            .start()
    }

    private fun pad(value: Int): String = if (value < 10) "0" + value else value.toString()

    private fun replay(schemeId: String) {
        if (!isAdded) return
        ServiceLocator.pendingSchemeId = schemeId
        ServiceLocator.lastRound = null
        val manager = activity?.supportFragmentManager ?: return
        Navigator.restartGame(manager, GameFragment())
    }

    private fun goToMenu() {
        if (!isAdded) return
        ServiceLocator.lastRound = null
        val manager = activity?.supportFragmentManager ?: return
        Navigator.backToMenu(manager)
    }

    override fun onDestroyView() {
        _binding?.resultBoard?.cancelAnimators()
        _binding?.resultTitle?.animate()?.cancel()
        _binding?.resultStar1?.animate()?.cancel()
        _binding?.resultStar2?.animate()?.cancel()
        _binding?.resultStar3?.animate()?.cancel()
        _binding = null
        super.onDestroyView()
    }

    private companion object {
        const val STAR_FROM = 0.6f
        const val STAR_POP_MS = 260L
        const val TITLE_FROM = 0.88f
        const val TITLE_POP_MS = 380L
    }
}
