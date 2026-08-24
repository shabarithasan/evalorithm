package com.evalorithm.data.local

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.longPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.evalorithm.data.model.AuthResponse
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "evalorithm_prefs")

@Singleton
class TokenManager @Inject constructor(
    @ApplicationContext private val context: Context
) {
    private val dataStore = context.dataStore

    companion object {
        private val ACCESS_TOKEN = stringPreferencesKey("access_token")
        private val REFRESH_TOKEN = stringPreferencesKey("refresh_token")
        private val USER_EMAIL = stringPreferencesKey("user_email")
        private val USER_ROLE = stringPreferencesKey("user_role")
        private val USER_NAME = stringPreferencesKey("user_name")
        private val USER_ID = longPreferencesKey("user_id")
    }

    suspend fun saveAuthData(response: AuthResponse) {
        dataStore.edit { prefs ->
            prefs[ACCESS_TOKEN] = response.accessToken
            prefs[REFRESH_TOKEN] = response.refreshToken
            prefs[USER_EMAIL] = response.email
            prefs[USER_ROLE] = response.role
            prefs[USER_NAME] = "${response.firstName} ${response.lastName}"
            prefs[USER_ID] = response.userId
        }
    }

    fun getAccessToken(): Flow<String?> = dataStore.data.map { it[ACCESS_TOKEN] }
    fun getRefreshToken(): Flow<String?> = dataStore.data.map { it[REFRESH_TOKEN] }
    fun getUserEmail(): Flow<String?> = dataStore.data.map { it[USER_EMAIL] }
    fun getUserRole(): Flow<String?> = dataStore.data.map { it[USER_ROLE] }
    fun getUserName(): Flow<String?> = dataStore.data.map { it[USER_NAME] }
    fun getUserId(): Flow<Long> = dataStore.data.map { it[USER_ID] ?: 0L }

    suspend fun clearAll() {
        dataStore.edit { it.clear() }
    }
}
