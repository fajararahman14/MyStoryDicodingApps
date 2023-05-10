package com.fajar.mystorydicodingapps.viewmodelfactory

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.fajar.mystorydicodingapps.data.local.datastore.UserPreference
import com.fajar.mystorydicodingapps.ui.login.LoginViewModel
import com.fajar.mystorydicodingapps.ui.main.MainViewModel

class ViewModelFactory(
    private val preference: UserPreference
) : ViewModelProvider.NewInstanceFactory() {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return when {
            modelClass.isAssignableFrom(LoginViewModel::class.java) -> {
                LoginViewModel(preference) as T
            }
            modelClass.isAssignableFrom(MainViewModel::class.java) -> {
                MainViewModel(preference) as T
            }
            else -> throw IllegalArgumentException("Unknown ViewModel class: " + modelClass.name)
        }

    }
}