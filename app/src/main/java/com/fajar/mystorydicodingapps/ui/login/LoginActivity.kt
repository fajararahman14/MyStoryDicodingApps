package com.fajar.mystorydicodingapps.ui.login

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.fajar.mystorydicodingapps.R
import com.fajar.mystorydicodingapps.data.local.datastore.UserPreference
import com.fajar.mystorydicodingapps.databinding.ActivityLoginBinding
import com.fajar.mystorydicodingapps.ui.main.MainActivity
import com.fajar.mystorydicodingapps.ui.register.RegisterActivity
import com.fajar.mystorydicodingapps.viewmodelfactory.ViewModelFactory
import kotlinx.coroutines.launch

class LoginActivity : AppCompatActivity() {
    private lateinit var binding: ActivityLoginBinding
    private var viewModel: LoginViewModel? = null
    private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "user_preferences")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)


        viewModel = ViewModelProvider(
            this,
            ViewModelFactory(
                UserPreference.getInstance(dataStore)
            )
        )[LoginViewModel::class.java]

        binding.btnRegister.setOnClickListener {
            val intent = Intent(this, RegisterActivity::class.java)
            startActivity(intent)
            finish()
        }

        viewModel?.let { vm ->
            vm.error.observe(this) { error ->
                if (error.isNotBlank()) {
                    Toast.makeText(this, error, Toast.LENGTH_SHORT).show()
                }
            }
            vm.loginResult.observe(this) { login ->
                if (!login.error) {
                    Toast.makeText(this, R.string.UI_info_successful_login_user, Toast.LENGTH_SHORT)
                        .show()
                    lifecycleScope.launch {
                        val intent = Intent(this@LoginActivity, MainActivity::class.java)
                        startActivity(intent)
                        finish()
                    }
                } else {
                    Toast.makeText(this, R.string.UI_info_failed_login_user, Toast.LENGTH_SHORT)
                        .show()
                }
            }
            vm.loading.observe(this) { state ->
                binding.progressBar.visibility = state
            }
        }

        binding.btnLogin.setOnClickListener {
            val email = binding.edEmail.text.toString()
            val password = binding.edPassword.text.toString()
            when {
                !binding.edEmail.error.isNullOrEmpty() -> {
                    Toast.makeText(
                        this,
                        getString(R.string.UI_validate_empty_email),
                        Toast.LENGTH_SHORT
                    ).show()
                }
                !binding.edPassword.error.isNullOrEmpty() -> {
                    Toast.makeText(
                        this,
                        getString(R.string.UI_validate_empty_password),
                        Toast.LENGTH_SHORT
                    ).show()
                }
                else -> {
                    viewModel?.login(email, password)
                }
            }
        }
    }
}
