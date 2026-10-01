package com.ZfQnLmK.vJxRpT.presentation.splash

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.ViewCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.ZfQnLmK.vJxRpT.R
import com.ZfQnLmK.vJxRpT.core.navigation.Navigator
import com.ZfQnLmK.vJxRpT.databinding.FragmentSplashBinding
import com.ZfQnLmK.vJxRpT.presentation.menu.MenuFragment
import kotlinx.coroutines.launch

class SplashFragment : Fragment() {

    private var _binding: FragmentSplashBinding? = null
    private val binding get() = _binding
    private val viewModel: SplashViewModel by viewModels()
    private val animator = SplashAnimator()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val created = FragmentSplashBinding.inflate(inflater, container, false)
        _binding = created
        return created.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val views = binding ?: return
        animator.prepare(views.splashTitle, views.splashTagline, views.splashProgress, views.splashLoading)
        animator.playBackdropDrift(views.splashBackdrop)
        animator.playGridSweep(views.splashGrid)
        animator.playTitleEntrance(views.splashTitle)
        animator.playTitleBreath(views.splashTitle)
        animator.playTaglineEntrance(views.splashTagline)
        animator.playIndicatorEntrance(views.splashProgress, views.splashLoading)
        animator.playCaptionPulse(views.splashLoading)
        ViewCompat.setStateDescription(views.splashProgress, getString(R.string.splash_loading))
        observeState()
        viewModel.start()
    }

    private fun observeState() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.uiState.collect { state ->
                    renderStage(state)
                    if (state.finished && !viewModel.hasNavigated()) {
                        viewModel.consumeNavigation()
                        goToMenu()
                    }
                }
            }
        }
    }

    private fun renderStage(state: SplashUiState) {
        val views = binding ?: return
        val caption = getString(R.string.splash_loading)
        val dots = StringBuilder()
        for (index in 0 until state.progressStage.coerceIn(0, MAX_DOTS)) {
            dots.append(DOT)
        }
        views.splashLoading.text = caption + dots.toString()
    }

    private fun goToMenu() {
        if (!isAdded) return
        val manager = activity?.supportFragmentManager ?: return
        Navigator.replaceRoot(manager, MenuFragment(), Navigator.TAG_MENU)
    }

    override fun onDestroyView() {
        animator.cancelAll()
        _binding?.splashRings?.cancelAnimators()
        _binding = null
        super.onDestroyView()
    }

    private companion object {
        const val MAX_DOTS = 4
        const val DOT = " ."
    }
}
