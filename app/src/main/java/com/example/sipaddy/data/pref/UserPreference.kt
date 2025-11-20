package com.example.sipaddy.data.pref

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "session")

class UserPreference private constructor(private val dataStore: DataStore<Preferences>) {

    suspend fun saveSession(namaLengkap: String, username: String, token: String, role: String) {
        dataStore.edit { preferences ->
            preferences[NAMALENGKAP_KEY] = namaLengkap
            preferences[USERNAME_KEY] = username
            preferences[TOKEN_KEY] = token
            preferences[ROLE_KEY] = role
        }
    }

    fun getSession(): Flow<String> {
        return dataStore.data.map { preferences ->
            preferences[TOKEN_KEY] ?: ""
        }
    }

    fun getUsername(): Flow<String> {
        return dataStore.data.map { preferences ->
            preferences[USERNAME_KEY] ?: ""
        }
    }

    fun getNamaLengkap(): Flow<String> {
        return dataStore.data.map {
            it[NAMALENGKAP_KEY] ?: ""
        }
    }

    fun getRole(): Flow<String> {
        return dataStore.data.map { preferences ->
            preferences[ROLE_KEY] ?: ""
        }
    }

    suspend fun logout() {
        dataStore.edit { preferences ->
            preferences.clear()
        }
    }

    companion object {
        @Volatile
        private var INSTANCE: UserPreference? = null

        private val USERNAME_KEY = stringPreferencesKey("username")
        private val NAMALENGKAP_KEY = stringPreferencesKey("namaLengkap")
        private val TOKEN_KEY = stringPreferencesKey("token")
        private val ROLE_KEY = stringPreferencesKey("role")

        fun getInstance(dataStore: DataStore<Preferences>): UserPreference {
            return INSTANCE ?: synchronized(this) {
                val instance = UserPreference(dataStore)
                INSTANCE = instance
                instance
            }
        }


    }
}