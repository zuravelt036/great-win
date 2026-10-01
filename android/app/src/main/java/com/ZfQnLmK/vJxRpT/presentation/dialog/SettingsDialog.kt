package com.ZfQnLmK.vJxRpT.presentation.dialog

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.ViewCompat
import androidx.fragment.app.DialogFragment
import com.ZfQnLmK.vJxRpT.R
import com.ZfQnLmK.vJxRpT.core.di.ServiceLocator
import com.ZfQnLmK.vJxRpT.core.ui.ButtonDecor
import com.ZfQnLmK.vJxRpT.core.ui.PressScaleHelper
import com.ZfQnLmK.vJxRpT.databinding.DialogSettingsBinding
import com.ZfQnLmK.vJxRpT.domain.repository.ProgressRepository

class SettingsDialog : DialogFragment() {

    private var _binding: DialogSettingsBinding? = null
    private var repository: ProgressRepository? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val created = DialogSettingsBinding.inflate(inflater, container, false)
        _binding = created
        return created.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val views = _binding ?: return
        val progress = ServiceLocator.progressRepository(requireContext())
        repository = progress
        views.settingsSound.isChecked = progress.soundEnabled()
        views.settingsVibration.isChecked = progress.vibrationEnabled()
        views.settingsContrast.isChecked = progress.highContrastEnabled()
        views.settingsSound.setOnCheckedChangeListener { _, checked ->
            repository?.setSoundEnabled(checked)
        }
        views.settingsVibration.setOnCheckedChangeListener { _, checked ->
            repository?.setVibrationEnabled(checked)
        }
        views.settingsContrast.setOnCheckedChangeListener { _, checked ->
            repository?.setHighContrastEnabled(checked)
        }
        ButtonDecor.secondary(views.settingsClose)
        PressScaleHelper.apply(views.settingsClose)
        views.settingsClose.setOnClickListener { dismissAllowingStateLoss() }
        ViewCompat.setStateDescription(views.settingsSound, getString(R.string.title_settings))
    }

    override fun onStart() {
        super.onStart()
        dialog?.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
    }

    override fun onDestroyView() {
        repository = null
        _binding = null
        super.onDestroyView()
    }

    companion object {
        const val TAG = "settings_dialog"
    }
}
