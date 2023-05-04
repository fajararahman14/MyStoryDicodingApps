package com.fajar.mystorydicodingapps.ui.main


import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.fajar.mystorydicodingapps.entity.EntityUser
import com.fajar.mystorydicodingapps.local.datastore.UserPreference
import kotlinx.coroutines.launch

class MainViewModel(private val preference: UserPreference) : ViewModel(){


    fun getUser(): LiveData<EntityUser> =
        preference.getUser().asLiveData()

    fun logout() {
        viewModelScope.launch {
            preference.logout()
        }
    }

}