package com.ZfQnLmK.vJxRpT.presentation.dialog

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import com.ZfQnLmK.vJxRpT.core.di.ServiceLocator
import com.ZfQnLmK.vJxRpT.core.navigation.Navigator
import com.ZfQnLmK.vJxRpT.core.ui.ButtonDecor
import com.ZfQnLmK.vJxRpT.core.ui.PressScaleHelper
import com.ZfQnLmK.vJxRpT.databinding.DialogConfirmExitBinding

class ConfirmExitDialog : DialogFragment() {

    private var _binding: DialogConfirmExitBinding? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val created = DialogConfirmExitBinding.inflate(inflater, container, false)
        _binding = created
        return created.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val views = _binding ?: return
        ButtonDecor.secondary(views.confirmStay)
        ButtonDecor.primary(views.confirmLeave)
        PressScaleHelper.apply(views.confirmStay, views.confirmLeave)
        views.confirmStay.setOnClickListener { dismissAllowingStateLoss() }
        views.confirmLeave.setOnClickListener { leave() }
    }

    private fun leave() {
        val manager = activity?.supportFragmentManager
        ServiceLocator.lastRound = null
        dismissAllowingStateLoss()
        if (manager != null) Navigator.backToMenu(manager)
    }

    override fun onStart() {
        super.onStart()
        dialog?.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
    }

    override fun onDestroyView() {
        _binding = null
        super.onDestroyView()
    }

    companion object {
        const val TAG = "confirm_exit_dialog"
    }
}
