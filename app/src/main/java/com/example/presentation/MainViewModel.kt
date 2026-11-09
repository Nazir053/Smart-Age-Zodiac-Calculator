package com.example.presentation

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.ads.AdManager
import com.example.data.local.UserPreferencesRepository
import com.example.domain.calculator.AgeCalculationEngine
import com.example.domain.calculator.ZodiacCalculator
import com.example.domain.model.AccentPalette
import com.example.domain.model.AgeCalculationResult
import com.example.domain.model.AppLanguage
import com.example.domain.model.AppSettings
import com.example.domain.model.AppThemeMode
import com.example.domain.model.CalculationValidationResult
import com.example.domain.model.HistorySortOrder
import com.example.domain.model.SavedCalculationEntry
import com.example.domain.model.ZodiacProfile
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.util.UUID

enum class AppDestination(val route: String) {
    HOME("home"),
    AGE_DETAILS("age_details"),
    ZODIAC("zodiac"),
    BIRTHDAY_COUNTDOWN("birthday_countdown"),
    HISTORY("history"),
    SETTINGS("settings")
}

class MainViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = UserPreferencesRepository(application.applicationContext)
    val adManager = AdManager()

    val settingsState: StateFlow<AppSettings> = repository.settingsFlow.stateIn(
        scope = viewModelScope,
        started = SharingStarted.Eagerly,
        initialValue = AppSettings()
    )

    private val _currentDestination = MutableStateFlow(AppDestination.HOME)
    val currentDestination: StateFlow<AppDestination> = _currentDestination.asStateFlow()

    private val defaultInitialDob: LocalDate = LocalDate.now().minusYears(25).minusMonths(4).minusDays(12)

    private val _selectedBirthDate = MutableStateFlow<LocalDate?>(defaultInitialDob)
    val selectedBirthDate: StateFlow<LocalDate?> = _selectedBirthDate.asStateFlow()

    private val _profileNameInput = MutableStateFlow("")
    val profileNameInput: StateFlow<String> = _profileNameInput.asStateFlow()

    private val _pinProfileChecked = MutableStateFlow(false)
    val pinProfileChecked: StateFlow<Boolean> = _pinProfileChecked.asStateFlow()

    private val _validationError = MutableStateFlow<String?>(null)
    val validationError: StateFlow<String?> = _validationError.asStateFlow()

    private val _calculationResult = MutableStateFlow<AgeCalculationResult?>(
        (AgeCalculationEngine.calculateAge(defaultInitialDob, LocalDate.now()) as? CalculationValidationResult.Success)?.result
    )
    val calculationResult: StateFlow<AgeCalculationResult?> = _calculationResult.asStateFlow()

    private val _selectedZodiacForInspection = MutableStateFlow<ZodiacProfile>(
        ZodiacCalculator.getZodiacForDate(defaultInitialDob)
    )
    val selectedZodiacForInspection: StateFlow<ZodiacProfile> = _selectedZodiacForInspection.asStateFlow()

    private val _historySearchQuery = MutableStateFlow("")
    val historySearchQuery: StateFlow<String> = _historySearchQuery.asStateFlow()

    private val _historySortOrder = MutableStateFlow(HistorySortOrder.NEWEST_FIRST)
    val historySortOrder: StateFlow<HistorySortOrder> = _historySortOrder.asStateFlow()

    private val _showBirthDayFinderSheet = MutableStateFlow(false)
    val showBirthDayFinderSheet: StateFlow<Boolean> = _showBirthDayFinderSheet.asStateFlow()

    private val _snackbarMessage = MutableStateFlow<String?>(null)
    val snackbarMessage: StateFlow<String?> = _snackbarMessage.asStateFlow()

    val allHistoryEntries: StateFlow<List<SavedCalculationEntry>> = repository.historyEntriesFlow
        .flowOn(Dispatchers.IO)
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.Eagerly,
            initialValue = emptyList()
        )

    val filteredHistoryEntries: StateFlow<List<SavedCalculationEntry>> = combine(
        allHistoryEntries,
        _historySearchQuery,
        _historySortOrder
    ) { entries, query, sortOrder ->
        val filtered = if (query.isBlank()) {
            entries
        } else {
            val q = query.trim().lowercase()
            entries.filter {
                it.profileName.lowercase().contains(q) ||
                    it.birthDateIso.lowercase().contains(q) ||
                    it.zodiacNameEn.lowercase().contains(q) ||
                    it.summaryAgeText.lowercase().contains(q)
            }
        }
        when (sortOrder) {
            HistorySortOrder.NEWEST_FIRST -> filtered.sortedByDescending { it.calculatedAtMillis }
            HistorySortOrder.OLDEST_FIRST -> filtered.sortedBy { it.calculatedAtMillis }
        }
    }
        .flowOn(Dispatchers.Default)
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = emptyList()
        )

    val pinnedProfiles: StateFlow<List<SavedCalculationEntry>> = allHistoryEntries
        .map { list -> list.filter { it.isPinnedProfile } }
        .flowOn(Dispatchers.Default)
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = emptyList()
        )

    init {
        viewModelScope.launch(Dispatchers.IO) {
            repository.savedPrimaryDobFlow.collect { savedIso ->
                if (!savedIso.isNullOrBlank()) {
                    runCatching { LocalDate.parse(savedIso) }.getOrNull()?.let { parsed ->
                        if (!parsed.isAfter(LocalDate.now())) {
                            _selectedBirthDate.value = parsed
                            val calc = AgeCalculationEngine.calculateAge(parsed, LocalDate.now())
                            if (calc is CalculationValidationResult.Success) {
                                _calculationResult.value = calc.result
                                _selectedZodiacForInspection.value = calc.result.zodiacProfile
                            }
                        }
                    }
                }
            }
        }
    }

    fun navigateTo(destination: AppDestination, recordAdTransition: Boolean = true) {
        if (_currentDestination.value != destination) {
            _currentDestination.value = destination
            if (recordAdTransition) {
                adManager.onNaturalNavigationTransition()
            }
        }
    }

    fun onProfileNameChange(name: String) {
        _profileNameInput.value = name
    }

    fun onPinProfileChange(checked: Boolean) {
        _pinProfileChecked.value = checked
    }

    fun onSelectBirthDate(date: LocalDate) {
        val lang = settingsState.value.language
        val validation = AgeCalculationEngine.calculateAge(date, LocalDate.now())
        when (validation) {
            is CalculationValidationResult.FutureDateError -> {
                _validationError.value = validation.message(lang)
            }
            is CalculationValidationResult.InvalidDateError -> {
                _validationError.value = validation.message(lang)
            }
            is CalculationValidationResult.Success -> {
                _validationError.value = null
                _selectedBirthDate.value = date
                _calculationResult.value = validation.result
                _selectedZodiacForInspection.value = validation.result.zodiacProfile
            }
        }
    }

    fun onCalculateAge(navigateToDetails: Boolean = true) {
        val lang = settingsState.value.language
        val currentDob = _selectedBirthDate.value
        val validation = AgeCalculationEngine.calculateAge(currentDob, LocalDate.now())
        when (validation) {
            is CalculationValidationResult.FutureDateError -> {
                _validationError.value = validation.message(lang)
            }
            is CalculationValidationResult.InvalidDateError -> {
                _validationError.value = validation.message(lang)
            }
            is CalculationValidationResult.Success -> {
                _validationError.value = null
                val res = validation.result
                _calculationResult.value = res
                _selectedZodiacForInspection.value = res.zodiacProfile

                val label = _profileNameInput.value.trim().ifBlank {
                    when (lang) {
                        AppLanguage.ENGLISH -> "DOB Calculation"
                        AppLanguage.BENGALI -> "জন্ম তারিখ হিসাব"
                        AppLanguage.HINDI -> "जन्म तिथि गणना"
                    }
                }
                val entry = SavedCalculationEntry(
                    id = UUID.randomUUID().toString(),
                    profileName = label,
                    relationshipTag = if (_pinProfileChecked.value) "Pinned Profile" else "History",
                    birthDateIso = res.birthDate.toString(),
                    calculatedAtMillis = System.currentTimeMillis(),
                    isPinnedProfile = _pinProfileChecked.value,
                    summaryAgeText = res.exactAgeSummary(AppLanguage.ENGLISH),
                    zodiacNameEn = res.zodiacProfile.englishName,
                    zodiacSymbol = res.zodiacProfile.symbol
                )

                viewModelScope.launch(Dispatchers.IO) {
                    repository.saveHistoryEntry(entry)
                    repository.setSavedPrimaryDob(res.birthDate.toString())
                }

                if (navigateToDetails) {
                    _currentDestination.value = AppDestination.AGE_DETAILS
                }
            }
        }
    }

    fun onClearInputs() {
        val resetDate = LocalDate.of(2000, 1, 1)
        _selectedBirthDate.value = resetDate
        _profileNameInput.value = ""
        _pinProfileChecked.value = false
        _validationError.value = null
        val calc = AgeCalculationEngine.calculateAge(resetDate, LocalDate.now())
        if (calc is CalculationValidationResult.Success) {
            _calculationResult.value = calc.result
            _selectedZodiacForInspection.value = calc.result.zodiacProfile
        }
    }

    fun onLoadHistoryEntry(entry: SavedCalculationEntry, targetDestination: AppDestination = AppDestination.AGE_DETAILS) {
        runCatching { LocalDate.parse(entry.birthDateIso) }.getOrNull()?.let { dob ->
            _selectedBirthDate.value = dob
            _profileNameInput.value = entry.profileName
            _pinProfileChecked.value = entry.isPinnedProfile
            val validation = AgeCalculationEngine.calculateAge(dob, LocalDate.now())
            if (validation is CalculationValidationResult.Success) {
                _validationError.value = null
                _calculationResult.value = validation.result
                _selectedZodiacForInspection.value = validation.result.zodiacProfile
                _currentDestination.value = targetDestination
            }
        }
    }

    fun onSelectZodiacSign(sign: ZodiacProfile) {
        _selectedZodiacForInspection.value = sign
    }

    fun onSearchHistoryChange(query: String) {
        _historySearchQuery.value = query
    }

    fun onSortOrderChange(order: HistorySortOrder) {
        _historySortOrder.value = order
    }

    fun onDeleteHistoryEntry(id: String) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.deleteHistoryEntry(id)
        }
    }

    fun onClearAllHistory() {
        viewModelScope.launch(Dispatchers.IO) {
            repository.clearAllHistory()
        }
        showSnackbar(
            when (settingsState.value.language) {
                AppLanguage.ENGLISH -> "All local history cleared"
                AppLanguage.BENGALI -> "সব ইতিহাস মুছে ফেলা হয়েছে"
                AppLanguage.HINDI -> "सारा स्थानीय इतिहास मिटा दिया गया है"
            }
        )
    }

    fun setThemeMode(mode: AppThemeMode) {
        viewModelScope.launch(Dispatchers.IO) { repository.setThemeMode(mode) }
    }

    fun setAccentPalette(palette: AccentPalette) {
        val currentSettings = settingsState.value
        if (palette in currentSettings.unlockedPalettes || !palette.requiresRewardUnlock) {
            viewModelScope.launch(Dispatchers.IO) { repository.setAccentPalette(palette) }
        } else {
            adManager.requestRewardedAd()
        }
    }

    fun unlockBonusThemesViaReward(selectPalette: AccentPalette = AccentPalette.CELESTIAL_GOLD) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.unlockRewardPalettes()
            repository.setAccentPalette(selectPalette)
        }
        showSnackbar(
            when (settingsState.value.language) {
                AppLanguage.ENGLISH -> "Bonus Celestial Gold & Cosmic Violet themes unlocked!"
                AppLanguage.BENGALI -> "বোনাস সেলেস্টিয়াল থিম আনলক হয়েছে!"
                AppLanguage.HINDI -> "बोनस सेलेस्टियल गोल्ड और कॉस्मिक वायलेट थीम अनलॉक हो गए!"
            }
        )
    }

    fun setLanguage(language: AppLanguage) {
        viewModelScope.launch(Dispatchers.IO) { repository.setLanguage(language) }
    }

    fun setHapticEnabled(enabled: Boolean) {
        viewModelScope.launch(Dispatchers.IO) { repository.setHapticEnabled(enabled) }
    }

    fun setAnimationsEnabled(enabled: Boolean) {
        viewModelScope.launch(Dispatchers.IO) { repository.setAnimationsEnabled(enabled) }
    }

    fun setShowBirthDayFinderSheet(show: Boolean) {
        _showBirthDayFinderSheet.value = show
    }

    fun showSnackbar(message: String) {
        _snackbarMessage.value = message
    }

    fun clearSnackbar() {
        _snackbarMessage.value = null
    }
}
