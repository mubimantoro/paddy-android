package com.example.sipaddy.data.local

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.example.sipaddy.data.model.response.UserResponse
import com.example.sipaddy.utils.Constants
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

val Context.daaStore: DataStore<Preferences> by preferencesDataStore(name = Constants.DATASTORE_NAME)

class TokenPreferences private constructor(private val dataStore: DataStore<Preferences>) {

    suspend fun saveLoginSession(
        accessToken: String,
        refreshToken: String,
        userId: Int,
        username: String,
        namaLengkap: String,
        role: String
    ) {
        dataStore.edit { preferences ->
            preferences[KEY_ACCESS_TOKEN] = accessToken
            preferences[KEY_REFRESH_TOKEN] = refreshToken
            preferences[KEY_USER_ID] = userId
            preferences[KEY_USERNAME] = username
            preferences[KEY_NAMA_LENGKAP] = namaLengkap
            preferences[KEY_ROLE] = role
        }
    }

    fun getAccessToken(): Flow<String?> {
        return dataStore.data.map { preferences ->
            preferences[KEY_ACCESS_TOKEN]
        }
    }

    fun getRefreshToken(): Flow<String?> {
        return dataStore.data.map { preferences ->
            preferences[KEY_REFRESH_TOKEN]
        }
    }

    fun getUserData(): Flow<UserResponse?> {
        return dataStore.data.map { preferences ->
            val userId = preferences[KEY_USER_ID]
            val username = preferences[KEY_USERNAME]
            val namaLengkap = preferences[KEY_NAMA_LENGKAP]
            val role = preferences[KEY_ROLE]

            if (userId != null && username != null && role != null) {
                UserResponse(
                    id = userId,
                    username = username,
                    namaLengkap = namaLengkap,
                    role = role
                )
            } else {
                null
            }
        }
    }

    fun isLoggedIn(): Flow<Boolean> {
        return dataStore.data.map { preferences ->
            preferences[KEY_ACCESS_TOKEN] != null
        }
    }

    suspend fun clearSession() {
        dataStore.edit { preferences ->
            preferences.clear()
        }
    }


    companion object {
        @Volatile
        private var INSTANCE: TokenPreferences? = null

        private val KEY_ACCESS_TOKEN = stringPreferencesKey(Constants.KEY_ACCESS_TOKEN)
        private val KEY_REFRESH_TOKEN = stringPreferencesKey(Constants.KEY_REFRESH_TOKEN)
        private val KEY_USER_ID = intPreferencesKey(Constants.KEY_USER_ID)
        private val KEY_USERNAME = stringPreferencesKey(Constants.KEY_USERNAME)
        private val KEY_NAMA_LENGKAP = stringPreferencesKey(Constants.KEY_NAMA_LENGKAP)
        private val KEY_ROLE = stringPreferencesKey(Constants.KEY_ROLE)


        fun getInstance(dataStore: DataStore<Preferences>): TokenPreferences {
            return INSTANCE ?: synchronized(this) {
                val instance = TokenPreferences(dataStore)
                INSTANCE = instance
                instance
            }
        }
    }
}