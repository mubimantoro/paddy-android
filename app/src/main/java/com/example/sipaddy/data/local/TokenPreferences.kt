package com.example.sipaddy.data.local

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.example.sipaddy.data.model.response.UserData
import com.example.sipaddy.utils.Constants
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = Constants.DATASTORE_NAME)

class TokenPreferences private constructor(private val dataStore: DataStore<Preferences>) {

    fun getAccessToken(): Flow<String?> {
        return dataStore.data.map { preferences ->
            preferences[ACCESS_TOKEN_KEY]
        }
    }

    fun getRefreshToken(): Flow<String?> {
        return dataStore.data.map { preferences ->
            preferences[REFRESH_TOKEN_KEY]
        }
    }

    suspend fun saveLoginSession(
        accessToken: String,
        refreshToken: String,
        user: UserData
    ) {
        dataStore.edit { preferences ->
            preferences[ACCESS_TOKEN_KEY] = accessToken
            preferences[REFRESH_TOKEN_KEY] = refreshToken
            preferences[USER_ID_KEY] = user.id.toString()
            preferences[USERNAME_KEY] = user.username
            user.namaLengkap?.let { preferences[NAMA_LENGKAP_KEY] = it }
            user.nomorHp?.let { preferences[NOMOR_HP_KEY] = it }
            user.role?.let { preferences[ROLE_KEY] = it }
            user.kelompokTani?.let { preferences[KELOMPOK_TANI_KEY] = it }
        }
    }

    fun getUserData(): Flow<UserData?> {
        return dataStore.data.map { preferences ->
            val id = preferences[USER_ID_KEY]?.toIntOrNull()
            val username = preferences[USERNAME_KEY]

            if (id != null && username != null) {
                UserData(
                    id = id,
                    username = username,
                    namaLengkap = preferences[NAMA_LENGKAP_KEY],
                    nomorHp = preferences[NOMOR_HP_KEY],
                    roles = preferences[ROLE_KEY]?.let { listOf(it) },
                    kelompokTani = preferences[KELOMPOK_TANI_KEY]
                )
            } else {
                null
            }
        }
    }

    fun isLoggedIn(): Flow<Boolean> {
        return dataStore.data.map { preferences ->
            preferences[ACCESS_TOKEN_KEY] != null
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

        private val ACCESS_TOKEN_KEY = stringPreferencesKey(Constants.ACCESS_TOKEN_KEY)
        private val REFRESH_TOKEN_KEY = stringPreferencesKey(Constants.REFRESH_TOKEN_KEY)
        private val USER_ID_KEY = stringPreferencesKey(Constants.USER_ID_KEY)
        private val USERNAME_KEY = stringPreferencesKey(Constants.USERNAME_KEY)
        private val NAMA_LENGKAP_KEY = stringPreferencesKey(Constants.NAMA_LENGKAP_KEY)
        private val NOMOR_HP_KEY = stringPreferencesKey(Constants.NOMOR_HP_KEY)
        private val KELOMPOK_TANI_KEY = stringPreferencesKey(Constants.KELOMPOK_TANI_KEY)
        private val ROLE_KEY = stringPreferencesKey(Constants.ROLE_KEY)


        fun getInstance(dataStore: DataStore<Preferences>): TokenPreferences {
            return INSTANCE ?: synchronized(this) {
                val instance = TokenPreferences(dataStore)
                INSTANCE = instance
                instance
            }
        }
    }
}