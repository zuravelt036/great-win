package com.ZfQnLmK.vJxRpT.presentation.schemeselect

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.GridLayoutManager
import com.ZfQnLmK.vJxRpT.core.di.ServiceLocator
import com.ZfQnLmK.vJxRpT.core.di.ViewModelFactory
import com.ZfQnLmK.vJxRpT.core.navigation.Navigator
import com.ZfQnLmK.vJxRpT.core.ui.ButtonDecor
import com.ZfQnLmK.vJxRpT.core.ui.PressScaleHelper
import com.ZfQnLmK.vJxRpT.databinding.FragmentSchemeSelectBinding
import com.ZfQnLmK.vJxRpT.presentation.game.GameFragment
import kotlinx.coroutines.launch

class SchemeSelectFragment : Fragment() {

    private var _binding: FragmentSchemeSelectBinding? = null
    private var adapter: SchemeAdapter? = null

    private val viewModel: SchemeSelectViewModel by viewModels {
        ViewModelFactory {
            SchemeSelectViewModel(
                ServiceLocator.getSchemes(requireContext()),
                ServiceLocator.progressRepository(requireContext())
            )
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val created = FragmentSchemeSelectBinding.inflate(inflater, container, false)
        _binding = created
        return created.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val views = _binding ?: return
        val created = SchemeAdapter { id -> viewModel.select(id) }
        adapter = created
        views.schemesList.layoutManager = GridLayoutManager(requireContext(), GRID_COLUMNS)
        views.schemesList.adapter = created
        ButtonDecor.primary(views.schemesLaunch)
        PressScaleHelper.apply(views.schemesLaunch)
        views.schemesToolbar.setNavigationOnClickListener { goBack() }
        views.schemesLaunch.setOnClickListener { launchSelected() }
        observeState()
    }

    override fun onResume() {
        super.onResume()
        viewModel.refresh()
    }

    private fun observeState() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.uiState.collect { state ->
                    adapter?.submit(state.cards, state.selectedId)
                }
            }
        }
    }

    private fun launchSelected() {
        if (!isAdded) return
        ServiceLocator.pendingSchemeId = viewModel.confirmSelection()
        val manager = activity?.supportFragmentManager ?: return
        Navigator.push(manager, GameFragment(), Navigator.TAG_GAME)
    }

    private fun goBack() {
        if (!isAdded) return
        val manager = activity?.supportFragmentManager ?: return
        Navigator.back(manager)
    }

    override fun onDestroyView() {
        _binding?.schemesList?.adapter = null
        adapter = null
        _binding = null
        super.onDestroyView()
    }

    private companion object {
        const val GRID_COLUMNS = 2
    }
}
