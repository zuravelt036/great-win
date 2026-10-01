package com.ZfQnLmK.vJxRpT.presentation.tutorial

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.ZfQnLmK.vJxRpT.R
import com.ZfQnLmK.vJxRpT.core.di.ServiceLocator
import com.ZfQnLmK.vJxRpT.core.di.ViewModelFactory
import com.ZfQnLmK.vJxRpT.core.navigation.Navigator
import com.ZfQnLmK.vJxRpT.core.ui.BoardMood
import com.ZfQnLmK.vJxRpT.core.ui.BoardRender
import com.ZfQnLmK.vJxRpT.core.ui.ButtonDecor
import com.ZfQnLmK.vJxRpT.core.ui.PressScaleHelper
import com.ZfQnLmK.vJxRpT.core.ui.TutorialStepView
import com.ZfQnLmK.vJxRpT.databinding.FragmentTutorialBinding
import com.ZfQnLmK.vJxRpT.presentation.game.GameFragment
import kotlinx.coroutines.launch

class TutorialFragment : Fragment() {

    private var _binding: FragmentTutorialBinding? = null
    private var stepsBuilt = false

    private val viewModel: TutorialViewModel by viewModels {
        ViewModelFactory {
            TutorialViewModel(
                ServiceLocator.schemeRepository(requireContext()),
                ServiceLocator.rotateRing,
                ServiceLocator.checkCircuit
            )
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val created = FragmentTutorialBinding.inflate(inflater, container, false)
        _binding = created
        return created.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val views = _binding ?: return
        ButtonDecor.primary(views.tutorialStart)
        PressScaleHelper.apply(views.tutorialStart)
        views.tutorialToolbar.setNavigationOnClickListener { goBack() }
        views.tutorialStart.setOnClickListener { startGame() }
        views.tutorialBoard.onRingTap = { ring -> viewModel.rotate(ring, true) }
        views.tutorialBoard.onRingLongTap = { ring -> viewModel.rotate(ring, false) }
        observeState()
    }

    private fun observeState() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.uiState.collect { state -> render(state) }
            }
        }
    }

    private fun render(state: TutorialUiState) {
        val views = _binding ?: return
        views.tutorialBoard.submit(
            BoardRender(
                segments = state.scheme.segments,
                baseConductors = state.baseConductors,
                offsets = state.ringState.offsets,
                energized = state.energized,
                targets = state.scheme.targetIndices,
                reached = state.reached,
                mood = if (state.solved) BoardMood.WIN else BoardMood.IDLE
            ),
            state.animatedRing
        )
        views.tutorialHint.setText(if (state.solved) R.string.tutorial_nice else R.string.tutorial_hint)
        if (!stepsBuilt) {
            stepsBuilt = true
            buildSteps(state)
        }
    }

    private fun buildSteps(state: TutorialUiState) {
        val views = _binding ?: return
        val context = context ?: return
        val titles = intArrayOf(
            R.string.tutorial_step1_title,
            R.string.tutorial_step2_title,
            R.string.tutorial_step3_title
        )
        val bodies = intArrayOf(
            R.string.tutorial_step1_body,
            R.string.tutorial_step2_body,
            R.string.tutorial_step3_body
        )
        views.tutorialSteps.removeAllViews()
        for (step in state.steps) {
            val card = TutorialStepView(context)
            val params = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            )
            params.bottomMargin = resources.getDimensionPixelSize(R.dimen.space_md)
            card.layoutParams = params
            val index = step.index.coerceIn(0, titles.size - 1)
            card.bind(
                step.index + 1,
                getString(titles[index]),
                getString(bodies[index]),
                step.usesNodeSprite
            )
            views.tutorialSteps.addView(card)
        }
    }

    private fun startGame() {
        if (!isAdded) return
        val manager = activity?.supportFragmentManager ?: return
        Navigator.push(manager, GameFragment(), Navigator.TAG_GAME)
    }

    private fun goBack() {
        if (!isAdded) return
        val manager = activity?.supportFragmentManager ?: return
        Navigator.back(manager)
    }

    override fun onDestroyView() {
        _binding?.tutorialBoard?.cancelAnimators()
        _binding?.tutorialSteps?.removeAllViews()
        _binding = null
        super.onDestroyView()
    }
}
