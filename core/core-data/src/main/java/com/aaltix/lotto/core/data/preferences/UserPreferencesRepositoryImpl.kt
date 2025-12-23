package com.aaltix.lotto.core.data.preferences

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.core.stringSetPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.aaltix.lotto.core.domain.repository.UserPreferencesRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "user_preferences")

/**
 * Implementation of UserPreferencesRepository using DataStore.
 */
class UserPreferencesRepositoryImpl(
    private val context: Context
) : UserPreferencesRepository {

    private object PreferencesKeys {
        val DISCLAIMER_ACCEPTED = booleanPreferencesKey("disclaimer_accepted")
        val SELECTED_LOTTERY_TYPE_ID = stringPreferencesKey("selected_lottery_type_id")
        val LAST_GENERATED_NUMBERS_ID = stringPreferencesKey("last_generated_numbers_id")
        val CONFETTI_SHOWN_FOR_TYPES = stringSetPreferencesKey("confetti_shown_for_types")
    }

    override fun hasAcceptedDisclaimer(): Flow<Boolean> {
        return context.dataStore.data.map { preferences ->
            preferences[PreferencesKeys.DISCLAIMER_ACCEPTED] ?: false
        }
    }

    override suspend fun acceptDisclaimer() {
        context.dataStore.edit { preferences ->
            preferences[PreferencesKeys.DISCLAIMER_ACCEPTED] = true
        }
    }

    override fun getSelectedLotteryTypeId(): Flow<String?> {
        return context.dataStore.data.map { preferences ->
            preferences[PreferencesKeys.SELECTED_LOTTERY_TYPE_ID]
        }
    }

    override suspend fun saveSelectedLotteryTypeId(typeId: String) {
        context.dataStore.edit { preferences ->
            preferences[PreferencesKeys.SELECTED_LOTTERY_TYPE_ID] = typeId
        }
    }

    override fun getLastGeneratedNumbersId(): Flow<String?> {
        return context.dataStore.data.map { preferences ->
            preferences[PreferencesKeys.LAST_GENERATED_NUMBERS_ID]
        }
    }

    override suspend fun saveLastGeneratedNumbersId(numbersId: String) {
        context.dataStore.edit { preferences ->
            preferences[PreferencesKeys.LAST_GENERATED_NUMBERS_ID] = numbersId
        }
    }

    override suspend fun clearLastGeneratedNumbers() {
        context.dataStore.edit { preferences ->
            preferences.remove(PreferencesKeys.LAST_GENERATED_NUMBERS_ID)
        }
    }

    override suspend fun hasShownConfettiForType(typeId: String): Boolean {
        val shownTypes = context.dataStore.data.map { preferences ->
            preferences[PreferencesKeys.CONFETTI_SHOWN_FOR_TYPES] ?: emptySet()
        }.first()
        return typeId in shownTypes
    }

    override suspend fun markConfettiShownForType(typeId: String) {
        context.dataStore.edit { preferences ->
            val currentSet = preferences[PreferencesKeys.CONFETTI_SHOWN_FOR_TYPES] ?: emptySet()
            preferences[PreferencesKeys.CONFETTI_SHOWN_FOR_TYPES] = currentSet + typeId
        }
    }
}
