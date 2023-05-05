package com.fajar.mystorydicodingapps.ui.register

import android.content.Intent
import android.os.Bundle
import android.transition.TransitionInflater
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.fajar.mystorydicodingapps.R
import com.fajar.mystorydicodingapps.databinding.ActivityRegisterBinding
import com.fajar.mystorydicodingapps.ui.login.LoginActivity


class RegisterActivity : AppCompatActivity() {
    private lateinit var binding: ActivityRegisterBinding
    private var viewModel: RegisterViewModel? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)

        viewModel = ViewModelProvider(this).get(RegisterViewModel::class.java)

        window.sharedElementEnterTransition = TransitionInflater.from(this)
            .inflateTransition(android.R.transition.move)

        binding.btnRegister.setOnClickListener {
            val name = binding.edName.text.toString()
            val email = binding.edEmail.text.toString()
            val password = binding.edPassword.text.toString()
            when {
                email.isEmpty() || password.isEmpty() || name.isEmpty() -> {
                    Toast.makeText(
                        this,
                        getString(R.string.UI_validate_empty_name_email_password),
                        Toast.LENGTH_SHORT
                    ).show()
                }
                !email.contains("@") -> {
                    Toast.makeText(
                        this,
                        getString(R.string.UI_validate_invalid_email),
                        Toast.LENGTH_SHORT
                    ).show()
                }
                password.length <= 8 -> {
                    Toast.makeText(
                        this,
                        getString(R.string.UI_validate_invalid_password),
                        Toast.LENGTH_SHORT
                    ).show()
                }
                else -> {
                    viewModel?.register(name, email, password)
                }
            }
        }
        viewModel?.let { vm ->
            vm.error.observe(this) { error ->
                if (error.isNotBlank()) {
                    Toast.makeText(this, error, Toast.LENGTH_SHORT).show()
                }
            }
            vm.registerResult.observe(this) { register ->
                if (!register.error) {
                    Toast.makeText(
                        this,
                        getString(R.string.UI_info_successful_register_user),
                        Toast.LENGTH_SHORT
                    ).show()
                    startActivity(Intent(this@RegisterActivity, LoginActivity::class.java))
                    finish()
                }
            }
            vm.loading.observe(this) { state ->
                binding.progressBar.visibility = state
            }
        }
        binding.btnLogin.setOnClickListener {
            val i = Intent(this@RegisterActivity, LoginActivity::class.java)
            startActivity(i)
            finish()
        }
    }
}
