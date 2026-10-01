package com.ZfQnLmK.vJxRpT.core.navigation

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import com.ZfQnLmK.vJxRpT.R

object Navigator {

    const val TAG_SPLASH = "splash"
    const val TAG_MENU = "menu"
    const val TAG_GAME = "game"
    const val TAG_RESULT = "result"
    const val TAG_SCHEMES = "schemes"
    const val TAG_TUTORIAL = "tutorial"

    fun replaceRoot(manager: FragmentManager, fragment: Fragment, tag: String) {
        if (manager.isStateSaved) return
        manager.beginTransaction()
            .setCustomAnimations(R.anim.fade_in, R.anim.fade_out)
            .replace(R.id.fragment_container, fragment, tag)
            .commitAllowingStateLoss()
    }

    fun push(manager: FragmentManager, fragment: Fragment, tag: String) {
        if (manager.isStateSaved) return
        manager.beginTransaction()
            .setCustomAnimations(
                R.anim.slide_in_right,
                R.anim.slide_out_left,
                R.anim.slide_in_left,
                R.anim.slide_out_right
            )
            .replace(R.id.fragment_container, fragment, tag)
            .addToBackStack(tag)
            .commitAllowingStateLoss()
    }

    fun pushFade(manager: FragmentManager, fragment: Fragment, tag: String) {
        if (manager.isStateSaved) return
        manager.beginTransaction()
            .setCustomAnimations(
                R.anim.fade_in,
                R.anim.scale_out,
                R.anim.fade_in,
                R.anim.slide_out_right
            )
            .replace(R.id.fragment_container, fragment, tag)
            .addToBackStack(tag)
            .commitAllowingStateLoss()
    }

    fun back(manager: FragmentManager) {
        if (manager.isStateSaved) return
        if (manager.backStackEntryCount > 0) manager.popBackStack()
    }

    fun backToMenu(manager: FragmentManager) {
        if (manager.isStateSaved) return
        manager.popBackStack(TAG_GAME, FragmentManager.POP_BACK_STACK_INCLUSIVE)
    }

    fun restartGame(manager: FragmentManager, fragment: Fragment) {
        if (manager.isStateSaved) return
        manager.popBackStackImmediate(TAG_GAME, FragmentManager.POP_BACK_STACK_INCLUSIVE)
        push(manager, fragment, TAG_GAME)
    }
}
