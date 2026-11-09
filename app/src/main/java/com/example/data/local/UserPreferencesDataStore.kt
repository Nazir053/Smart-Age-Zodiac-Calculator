package com.example.data.local

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.example.domain.model.AccentPalette
import com.example.domain.model.AppLanguage
import com.example.domain.model.AppSettings
import com.example.domain.model.AppThemeMode
import com.example.domain.model.SavedCalculationEntry
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import org.json.JSONArray
import org.json.JSONObject

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "smart_age_zodiac_prefs")

class UserPreferencesRepository(private val context: Context) {

    companion object {
        private val KEY_THEME_MODE = stringPreferencesKey("theme_mode")
        private val KEY_ACCENT_PALETTE = stringPreferencesKey("accent_palette")
        private val KEY_UNLOCKED_PALETTES = stringPreferencesKey("unlocked_palettes")
        private val KEY_LANGUAGE = stringPreferencesKey("language")
        private val KEY_HAPTIC_ENABLED = booleanPreferencesKey("haptic_enabled")
        private val KEY_ANIMATIONS_ENABLED = booleanPreferencesKey("animations_enabled")
        private val KEY_SAVED_DOB_ISO = stringPreferencesKey("saved_dob_iso")
        private val KEY_HISTORY_JSON = stringPreferencesKey("history_entries_json")
    }

    val settingsFlow: Flow<AppSettings> = context.dataStore.data.map { prefs ->
        val themeMode = runCatching {
            AppThemeMode.valueOf(prefs[KEY_THEME_MODE] ?: AppThemeMode.SYSTEM.name)
        }.getOrDefault(AppThemeMode.SYSTEM)

        val accentPalette = runCatching {
            AccentPalette.valueOf(prefs[KEY_ACCENT_PALETTE] ?: AccentPalette.DEFAULT.name)
        }.getOrDefault(AccentPalette.DEFAULT)

        val unlockedRaw = prefs[KEY_UNLOCKED_PALETTES] ?: AccentPalette.DEFAULT.name
        val unlockedSet = unlockedRaw.split(",")
            .mapNotNull { token -> runCatching { AccentPalette.valueOf(token.trim()) }.getOrNull() }
            .toMutableSet()
            .apply { add(AccentPalette.DEFAULT) }

        val language = runCatching {
            AppLanguage.valueOf(prefs[KEY_LANGUAGE] ?: AppLanguage.ENGLISH.name)
        }.getOrDefault(AppLanguage.ENGLISH)

        val hapticEnabled = prefs[KEY_HAPTIC_ENABLED] ?: true
        val animationsEnabled = prefs[KEY_ANIMATIONS_ENABLED] ?: true

        AppSettings(
            themeMode = themeMode,
            accentPalette = if (accentPalette in unlockedSet) accentPalette else AccentPalette.DEFAULT,
            unlockedPalettes = unlockedSet,
            language = language,
            hapticFeedbackEnabled = hapticEnabled,
            animationsEnabled = animationsEnabled
        )
    }

    val savedPrimaryDobFlow: Flow<String?> = context.dataStore.data.map { prefs ->
        prefs[KEY_SAVED_DOB_ISO]
    }

    val historyEntriesFlow: Flow<List<SavedCalculationEntry>> = context.dataStore.data.map { prefs ->
        parseHistoryJson(prefs[KEY_HISTORY_JSON])
    }

    suspend fun setThemeMode(mode: AppThemeMode) {
        context.dataStore.edit { it[KEY_THEME_MODE] = mode.name }
    }

    suspend fun setAccentPalette(palette: AccentPalette) {
        context.dataStore.edit { it[KEY_ACCENT_PALETTE] = palette.name }
    }

    suspend fun unlockRewardPalettes() {
        context.dataStore.edit { prefs ->
            val allUnlocked = AccentPalette.entries.joinToString(",") { it.name }
            prefs[KEY_UNLOCKED_PALETTES] = allUnlocked
        }
    }

    suspend fun setLanguage(language: AppLanguage) {
        context.dataStore.edit { it[KEY_LANGUAGE] = language.name }
    }

    suspend fun setHapticEnabled(enabled: Boolean) {
        context.dataStore.edit { it[KEY_HAPTIC_ENABLED] = enabled }
    }

    suspend fun setAnimationsEnabled(enabled: Boolean) {
        context.dataStore.edit { it[KEY_ANIMATIONS_ENABLED] = enabled }
    }

    suspend fun setSavedPrimaryDob(isoDate: String?) {
        context.dataStore.edit { prefs ->
            if (isoDate == null) {
                prefs.remove(KEY_SAVED_DOB_ISO)
            } else {
                prefs[KEY_SAVED_DOB_ISO] = isoDate
            }
        }
    }

    suspend fun saveHistoryEntry(entry: SavedCalculationEntry) {
        context.dataStore.edit { prefs ->
            val currentList = parseHistoryJson(prefs[KEY_HISTORY_JSON]).toMutableList()
            // Remove existing non-pinned duplicate with same date and name to keep history clean
            currentList.removeAll {
                it.id == entry.id || (it.birthDateIso == entry.birthDateIso && it.profileName.equals(entry.profileName, ignoreCase = true))
            }
            currentList.add(0, entry)
            prefs[KEY_HISTORY_JSON] = serializeHistoryJson(currentList.take(50))
        }
    }

    suspend fun deleteHistoryEntry(id: String) {
        context.dataStore.edit { prefs ->
            val updated = parseHistoryJson(prefs[KEY_HISTORY_JSON]).filterNot { it.id == id }
            prefs[KEY_HISTORY_JSON] = serializeHistoryJson(updated)
        }
    }

    suspend fun clearAllHistory() {
        context.dataStore.edit { prefs ->
            prefs.remove(KEY_HISTORY_JSON)
            prefs.remove(KEY_SAVED_DOB_ISO)
        }
    }

    private fun parseHistoryJson(jsonString: String?): List<SavedCalculationEntry> {
        if (jsonString.isNullOrBlank()) return emptyList()
        return runCatching {
            val array = JSONArray(jsonString)
            buildList {
                for (i in 0 until array.length()) {
                    val obj = array.getJSONObject(i)
                    add(
                        SavedCalculationEntry(
                            id = obj.optString("id"),
                            profileName = obj.optString("profileName", ""),
                            relationshipTag = obj.optString("relationshipTag", "Personal"),
                            birthDateIso = obj.optString("birthDateIso"),
                            calculatedAtMillis = obj.optLong("calculatedAtMillis", 0L),
                            isPinnedProfile = obj.optBoolean("isPinnedProfile", false),
                            summaryAgeText = obj.optString("summaryAgeText", ""),
                            zodiacNameEn = obj.optString("zodiacNameEn", ""),
                            zodiacSymbol = obj.optString("zodiacSymbol", "")
                        )
                    )
                }
            }
        }.getOrDefault(emptyList())
    }

    private fun serializeHistoryJson(entries: List<SavedCalculationEntry>): String {
        val array = JSONArray()
        entries.forEach { entry ->
            val obj = JSONObject().apply {
                put("id", entry.id)
                put("profileName", entry.profileName)
                put("relationshipTag", entry.relationshipTag)
                put("birthDateIso", entry.birthDateIso)
                put("calculatedAtMillis", entry.calculatedAtMillis)
                put("isPinnedProfile", entry.isPinnedProfile)
                put("summaryAgeText", entry.summaryAgeText)
                put("zodiacNameEn", entry.zodiacNameEn)
                put("zodiacSymbol", entry.zodiacSymbol)
            }
            array.put(obj)
        }
        return array.toString()
    }
}
