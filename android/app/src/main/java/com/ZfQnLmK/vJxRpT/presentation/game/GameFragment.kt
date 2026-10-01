package com.ZfQnLmK.vJxRpT.presentation.game

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
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
import com.ZfQnLmK.vJxRpT.databinding.FragmentGameBinding
import com.ZfQnLmK.vJxRpT.presentation.dialog.ConfirmExitDialog
import com.ZfQnLmK.vJxRpT.presentation.gameover.ResultFragment
import kotlinx.coroutines.launch

class GameFragment : Fragment() {

    private var _binding: FragmentGameBinding? = null
    private var lastShakeTick = 0
    private var navigated = false

    private val viewModel: GameViewModel by viewModels {
        ViewModelFactory {
            GameViewModel(
                ServiceLocator.schemeRepository(requireContext()),
                ServiceLocator.progressRepository(requireContext()),
                ServiceLocator.rotateRing,
                ServiceLocator.checkCircuit,
                ServiceLocator.calculateScore,
                ServiceLocator.pendingSchemeId
            )
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val created = FragmentGameBinding.inflate(inflater, container, false)
        _binding = created
        return created.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val views = _binding ?: return
        ButtonDecor.primary(views.gameGo)
        ButtonDecor.icon(views.gameRestart, views.gameBack)
        PressScaleHelper.apply(views.gameGo, views.gameRestart, views.gameBack)
        views.gameBoard.onRingTap = { ring -> viewModel.rotate(ring, true) }
        views.gameBoard.onRingLongTap = { ring -> viewModel.rotate(ring, false) }
        views.gameGo.setOnClickListener { viewModel.runCheck() }
        views.gameRestart.setOnClickListener { viewModel.restart() }
        views.gameBack.setOnClickListener { confirmExit() }
        observeState()
    }

    private fun observeState() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.uiState.collect { state -> render(state) }
            }
        }
    }

    private fun render(state: GameUiState) {
        val views = _binding ?: return
        val context = context ?: return
        views.gameSchemeName.text = state.scheme.name
        views.gameMoves.text = getString(R.string.fmt_moves, pad(state.movesLeft))
        views.gameScore.text = getString(R.string.label_score) + " " + state.score
        renderChecks(state.checksLeft)
        views.gameGo.isEnabled = state.inputEnabled
        views.gameGo.alpha = if (state.inputEnabled) 1f else DISABLED_ALPHA
        views.gameBoard.interactive = state.inputEnabled
        views.gameBoard.submit(
            BoardRender(
                segments = state.scheme.segments,
                baseConductors = state.baseConductors,
                offsets = state.ringState.offsets,
                energized = state.energized,
                targets = state.scheme.targetIndices,
                reached = state.reached,
                mood = moodOf(state.phase)
            ),
            state.animatedRing
        )
        ViewCompat.setStateDescription(views.gameBoard, statusLabel(state))
        ViewCompat.setStateDescription(
            views.gameMoves,
            getString(R.string.label_moves) + " " + state.movesLeft
        )
        if (state.shakeTick != lastShakeTick) {
            lastShakeTick = state.shakeTick
            views.gameBoard.shake()
        }
        val accent = if (state.phase == GamePhase.LOSE) R.color.color_danger else R.color.color_gold
        views.gameMoves.setTextColor(ContextCompat.getColor(context, accent))
        if (state.outcome != null && !navigated) {
            navigated = true
            ServiceLocator.lastRound = viewModel.summary()
            openResult()
        }
    }

    private fun statusLabel(state: GameUiState): String = when (state.phase) {
        GamePhase.CHECKING -> getString(R.string.state_checking)
        GamePhase.WIN -> getString(R.string.state_solved)
        GamePhase.LOSE -> getString(R.string.state_broken)
        else -> getString(R.string.state_idle)
    }

    private fun moodOf(phase: GamePhase): BoardMood = when (phase) {
        GamePhase.CHECKING -> BoardMood.CHECKING
        GamePhase.WIN -> BoardMood.WIN
        GamePhase.LOSE -> BoardMood.LOSE
        else -> BoardMood.IDLE
    }

    private fun renderChecks(checksLeft: Int) {
        val views = _binding ?: return
        val pips = listOf(views.gameCheck1, views.gameCheck2, views.gameCheck3)
        for (index in pips.indices) {
            val drawable = if (index < checksLeft) R.drawable.chip_pip_on else R.drawable.chip_pip_off
            pips[index].setBackgroundResource(drawable)
        }
    }

    private fun pad(value: Int): String = if (value < 10) "0" + value else value.toString()

    private fun confirmExit() {
        if (!isAdded) return
        val manager = activity?.supportFragmentManager ?: return
        ConfirmExitDialog().show(manager, ConfirmExitDialog.TAG)
    }

    private fun openResult() {
        if (!isAdded) return
        val manager = activity?.supportFragmentManager ?: return
        Navigator.pushFade(manager, ResultFragment(), Navigator.TAG_RESULT)
    }

    override fun onDestroyView() {
        _binding?.gameBoard?.cancelAnimators()
        _binding = null
        super.onDestroyView()
    }

    private companion object {
        const val DISABLED_ALPHA = 0.45f
    }
}
