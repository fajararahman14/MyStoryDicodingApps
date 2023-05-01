package com.fajar.mystorydicodingapps.local.datastore

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import com.fajar.mystorydicodingapps.entity.EntityUser
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class UserPreference private constructor(private val dataStore: DataStore<Preferences>) {
    companion object {
        @Volatile
        private var INSTANCE: UserPreference? = null

        private val KEY_NAME = stringPreferencesKey("name")
        private val KEY_TOKEN = stringPreferencesKey("token")
        private val KEY_LOGIN_STATE = booleanPreferencesKey("login_state")

        fun getInstance(dataStore: DataStore<Preferences>): UserPreference {
            return INSTANCE ?: synchronized(this) {
                val instance = UserPreference(dataStore)
                INSTANCE = instance
                instance
            }
        }
    }

    fun getUser(): Flow<EntityUser> {
        return dataStore.data.map { preferences ->
            EntityUser(
                preferences[KEY_NAME] ?: "",
                preferences[KEY_TOKEN] ?: "",
                preferences[KEY_LOGIN_STATE] ?: false
            )
        }
    }
    suspend fun saveUser(user: EntityUser) {
        dataStore.edit { preferences ->
            preferences[KEY_NAME] = user.name
            preferences[KEY_TOKEN] = user.token
            preferences[KEY_LOGIN_STATE] = user.isLogin
        }
    }
    suspend fun login(){
        dataStore.edit { preferences ->
            preferences[KEY_LOGIN_STATE] = true
        }
    }
    suspend fun logout() {
        dataStore.edit { preferences ->
            preferences[KEY_LOGIN_STATE] = false
            preferences.remove(KEY_LOGIN_STATE)
        }
    }

}