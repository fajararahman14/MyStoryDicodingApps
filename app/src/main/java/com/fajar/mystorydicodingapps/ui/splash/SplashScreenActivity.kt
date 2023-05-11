package com.fajar.mystorydicodingapps.ui.splash

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityOptionsCompat
import androidx.core.util.Pair
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import androidx.lifecycle.ViewModelProvider
import com.fajar.mystorydicodingapps.data.local.datastore.UserPreference
import com.fajar.mystorydicodingapps.databinding.ActivitySplashScreenBinding
import com.fajar.mystorydicodingapps.ui.login.LoginViewModel
import com.fajar.mystorydicodingapps.ui.main.MainActivity
import com.fajar.mystorydicodingapps.ui.register.RegisterActivity
import com.fajar.mystorydicodingapps.viewmodelfactory.ViewModelFactory


class SplashScreenActivity : AppCompatActivity() {
    private val TIME_SPLASH_SCREEN: Long = 3000
    private lateinit var loginViewModel: LoginViewModel
    private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "user_preferences")

    lateinit var handler: Handler
    private lateinit var binding : ActivitySplashScreenBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySplashScreenBinding.inflate(layoutInflater)
        setContentView(binding.root)
        supportActionBar?.hide()
        handler = Handler()
        handler.postDelayed({
            setupViewModel()
        }, TIME_SPLASH_SCREEN)



    }

    private fun setupViewModel() {
        loginViewModel = ViewModelProvider(
            this,
            ViewModelFactory(
                UserPreference.getInstance(dataStore)
            )
        )[LoginViewModel::class.java]

        loginCheck()
    }

    private fun loginCheck() {
        loginViewModel.getUser().observe(this) { user ->
            if (user.isLogin) {

                startActivity(Intent(this, MainActivity::class.java))
                finish()
            } else {
                val options = ActivityOptionsCompat.makeSceneTransitionAnimation(
                    this,
                    Pair(binding.ivDicodingLogo, "image_transition")
                )
                val intent = Intent(this, RegisterActivity::class.java)
                startActivity(intent, options.toBundle())
                finish()
            }
        }
    }

}