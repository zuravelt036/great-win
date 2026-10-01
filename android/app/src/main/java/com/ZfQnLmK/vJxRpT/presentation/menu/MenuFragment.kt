package com.ZfQnLmK.vJxRpT.presentation.menu

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.ZfQnLmK.vJxRpT.R
import com.ZfQnLmK.vJxRpT.core.di.ServiceLocator
import com.ZfQnLmK.vJxRpT.core.di.ViewModelFactory
import com.ZfQnLmK.vJxRpT.core.navigation.Navigator
import com.ZfQnLmK.vJxRpT.core.ui.ButtonDecor
import com.ZfQnLmK.vJxRpT.core.ui.PressScaleHelper
import com.ZfQnLmK.vJxRpT.databinding.FragmentMenuBinding
import com.ZfQnLmK.vJxRpT.presentation.dialog.SettingsDialog
import com.ZfQnLmK.vJxRpT.presentation.game.GameFragment
import com.ZfQnLmK.vJxRpT.presentation.schemeselect.SchemeSelectFragment
import com.ZfQnLmK.vJxRpT.presentation.tutorial.TutorialFragment
import kotlinx.coroutines.launch

class MenuFragment : Fragment() {

    private var _binding: FragmentMenuBinding? = null

    private val viewModel: MenuViewModel by viewModels {
        ViewModelFactory {
            MenuViewModel(
                ServiceLocator.progressRepository(requireContext()),
                ServiceLocator.schemeRepository(requireContext())
            )
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val created = FragmentMenuBinding.inflate(inflater, container, false)
        _binding = created
        return created.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val views = _binding ?: return
        views.menuRings.configure(true, MENU_RING_LOOPS)
        ButtonDecor.primary(views.menuPlay)
        ButtonDecor.secondary(views.menuSchemes, views.menuTutorial)
        ButtonDecor.icon(views.menuSettings)
        PressScaleHelper.apply(views.menuPlay, views.menuSchemes, views.menuTutorial, views.menuSettings)
        views.menuPlay.setOnClickListener { openGame() }
        views.menuSchemes.setOnClickListener { openSchemes() }
        views.menuTutorial.setOnClickListener { openTutorial() }
        views.menuSettings.setOnClickListener { openSettings() }
        playEntrance()
        observeState()
    }

    override fun onResume() {
        super.onResume()
        viewModel.refresh()
    }

    private fun playEntrance() {
        val views = _binding ?: return
        val context = context ?: return
        views.menuPlay.startAnimation(AnimationUtils.loadAnimation(context, R.anim.menu_cta_in))
        views.menuTitle.startAnimation(AnimationUtils.loadAnimation(context, R.anim.splash_title_in))
    }

    private fun observeState() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.uiState.collect { state -> render(state) }
            }
        }
    }

    private fun render(state: MenuUiState) {
        val views = _binding ?: return
        val context = context ?: return
        views.menuLevel.bind(
            state.levelLabel,
            getString(R.string.label_level),
            ContextCompat.getColor(context, R.color.color_secondary)
        )
        if (state.showStats) {
            views.menuStats.visibility = View.VISIBLE
            views.menuStatBest.bind(
                getString(R.string.fmt_best_stars, state.bestStars),
                getString(R.string.label_best),
                ContextCompat.getColor(context, R.color.color_gold)
            )
            views.menuStatSolved.bind(
                formatCount(state.solvedCount),
                getString(R.string.label_solved),
                ContextCompat.getColor(context, R.color.color_secondary)
            )
        } else {
            views.menuStats.visibility = View.GONE
        }
    }

    private fun formatCount(value: Int): String = if (value < 10) "0" + value else value.toString()

    private fun openGame() {
        if (!isAdded) return
        ServiceLocator.pendingSchemeId = viewModel.activeSchemeId()
        val manager = activity?.supportFragmentManager ?: return
        Navigator.push(manager, GameFragment(), Navigator.TAG_GAME)
    }

    private fun openSchemes() {
        if (!isAdded) return
        val manager = activity?.supportFragmentManager ?: return
        Navigator.push(manager, SchemeSelectFragment(), Navigator.TAG_SCHEMES)
    }

    private fun openTutorial() {
        if (!isAdded) return
        val manager = activity?.supportFragmentManager ?: return
        Navigator.push(manager, TutorialFragment(), Navigator.TAG_TUTORIAL)
    }

    private fun openSettings() {
        if (!isAdded) return
        val manager = activity?.supportFragmentManager ?: return
        SettingsDialog().show(manager, SettingsDialog.TAG)
    }

    override fun onDestroyView() {
        _binding?.menuRings?.cancelAnimators()
        _binding?.menuPlay?.clearAnimation()
        _binding?.menuTitle?.clearAnimation()
        _binding = null
        super.onDestroyView()
    }

    private companion object {
        const val MENU_RING_LOOPS = 2
    }
}
