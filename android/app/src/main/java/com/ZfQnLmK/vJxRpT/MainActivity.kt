package com.ZfQnLmK.vJxRpT

import android.os.Bundle
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AppCompatActivity
import com.ZfQnLmK.vJxRpT.core.di.ServiceLocator
import com.ZfQnLmK.vJxRpT.core.navigation.Navigator
import com.ZfQnLmK.vJxRpT.databinding.ActivityMainBinding
import com.ZfQnLmK.vJxRpT.presentation.splash.SplashFragment

class MainActivity : AppCompatActivity() {

    private var binding: ActivityMainBinding? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        ServiceLocator.init(applicationContext)
        val created = ActivityMainBinding.inflate(layoutInflater)
        binding = created
        setContentView(created.root)
        if (savedInstanceState == null) {
            supportFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, SplashFragment(), Navigator.TAG_SPLASH)
                .commitAllowingStateLoss()
        }
        onBackPressedDispatcher.addCallback(this, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                if (supportFragmentManager.backStackEntryCount > 0) {
                    supportFragmentManager.popBackStack()
                } else {
                    isEnabled = false
                    onBackPressedDispatcher.onBackPressed()
                }
            }
        })
    }

    override fun onDestroy() {
        binding = null
        super.onDestroy()
    }
}
