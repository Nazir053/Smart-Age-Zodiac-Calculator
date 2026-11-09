package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.domain.model.AppThemeMode
import com.example.presentation.AppDestination
import com.example.presentation.MainViewModel
import com.example.presentation.age.AgeDetailsScreen
import com.example.presentation.birthday.BirthdayCountdownScreen
import com.example.presentation.components.AdOverlays
import com.example.presentation.components.CustomBottomNavigation
import com.example.presentation.history.HistoryScreen
import com.example.presentation.home.HomeScreen
import com.example.presentation.settings.SettingsScreen
import com.example.presentation.zodiac.ZodiacScreen
import com.example.ui.theme.SmartAgeZodiacTheme
import com.example.utils.LocalizationCatalog

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            SmartAgeZodiacApp()
        }
    }
}

@Composable
fun SmartAgeZodiacApp(
    mainViewModel: MainViewModel = viewModel()
) {
    val settings by mainViewModel.settingsState.collectAsStateWithLifecycle()
    val currentDestination by mainViewModel.currentDestination.collectAsStateWithLifecycle()
    val selectedBirthDate by mainViewModel.selectedBirthDate.collectAsStateWithLifecycle()
    val profileNameInput by mainViewModel.profileNameInput.collectAsStateWithLifecycle()
    val pinProfileChecked by mainViewModel.pinProfileChecked.collectAsStateWithLifecycle()
    val validationError by mainViewModel.validationError.collectAsStateWithLifecycle()
    val calculationResult by mainViewModel.calculationResult.collectAsStateWithLifecycle()
    val selectedZodiac by mainViewModel.selectedZodiacForInspection.collectAsStateWithLifecycle()
    val pinnedProfiles by mainViewModel.pinnedProfiles.collectAsStateWithLifecycle()
    val allHistoryEntries by mainViewModel.allHistoryEntries.collectAsStateWithLifecycle()
    val filteredHistoryEntries by mainViewModel.filteredHistoryEntries.collectAsStateWithLifecycle()
    val historySearchQuery by mainViewModel.historySearchQuery.collectAsStateWithLifecycle()
    val historySortOrder by mainViewModel.historySortOrder.collectAsStateWithLifecycle()
    val showBirthDayFinderSheet by mainViewModel.showBirthDayFinderSheet.collectAsStateWithLifecycle()
    val snackbarMessage by mainViewModel.snackbarMessage.collectAsStateWithLifecycle()

    val showInterstitial by mainViewModel.adManager.showInterstitialOverlay.collectAsStateWithLifecycle()
    val showRewarded by mainViewModel.adManager.showRewardedAdOverlay.collectAsStateWithLifecycle()

    val strings = remember(settings.language) {
        LocalizationCatalog.forLanguage(settings.language)
    }

    val snackbarHostState = remember { SnackbarHostState() }
    val haptic = LocalHapticFeedback.current

    val triggerHaptic: () -> Unit = {
        if (settings.hapticFeedbackEnabled) {
            runCatching { haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove) }
        }
    }

    LaunchedEffect(snackbarMessage) {
        val msg = snackbarMessage
        if (!msg.isNullOrBlank()) {
            snackbarHostState.showSnackbar(msg)
            mainViewModel.clearSnackbar()
        }
    }

    if (currentDestination != AppDestination.HOME) {
        BackHandler {
            if (currentDestination == AppDestination.BIRTHDAY_COUNTDOWN) {
                mainViewModel.navigateTo(AppDestination.AGE_DETAILS, recordAdTransition = false)
            } else {
                mainViewModel.navigateTo(AppDestination.HOME, recordAdTransition = false)
            }
        }
    }

    SmartAgeZodiacTheme(
        themeMode = settings.themeMode,
        accentPalette = settings.accentPalette
    ) {
        Surface(
            modifier = Modifier.fillMaxSize(),
            color = MaterialTheme.colorScheme.background
        ) {
            Scaffold(
                contentWindowInsets = WindowInsets.safeDrawing,
                snackbarHost = { SnackbarHost(hostState = snackbarHostState) },
                bottomBar = {
                    CustomBottomNavigation(
                        currentDestination = currentDestination,
                        strings = strings,
                        onNavigate = { target ->
                            triggerHaptic()
                            mainViewModel.navigateTo(target)
                        }
                    )
                }
            ) { innerPadding ->
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(innerPadding)
                ) {
                    AnimatedContent(
                        targetState = currentDestination,
                        transitionSpec = {
                            if (settings.animationsEnabled) {
                                fadeIn(tween(140)) togetherWith fadeOut(tween(110))
                            } else {
                                fadeIn(tween(0)) togetherWith fadeOut(tween(0))
                            }
                        },
                        label = "screen_transition"
                    ) { destination ->
                        when (destination) {
                            AppDestination.HOME -> HomeScreen(
                                selectedBirthDate = selectedBirthDate,
                                profileNameInput = profileNameInput,
                                pinProfileChecked = pinProfileChecked,
                                validationError = validationError,
                                calculationResult = calculationResult,
                                pinnedProfiles = pinnedProfiles,
                                recentCalculations = allHistoryEntries,
                                settings = settings,
                                strings = strings,
                                bannerAdUnitId = mainViewModel.adManager.bannerAdUnitId,
                                showBirthDayFinderSheet = showBirthDayFinderSheet,
                                onSelectBirthDate = { date ->
                                    triggerHaptic()
                                    mainViewModel.onSelectBirthDate(date)
                                },
                                onProfileNameChange = mainViewModel::onProfileNameChange,
                                onPinProfileChange = mainViewModel::onPinProfileChange,
                                onCalculateClick = {
                                    triggerHaptic()
                                    mainViewModel.onCalculateAge(navigateToDetails = true)
                                },
                                onClearClick = {
                                    triggerHaptic()
                                    mainViewModel.onClearInputs()
                                },
                                onToggleTheme = {
                                    triggerHaptic()
                                    val nextMode = when (settings.themeMode) {
                                        AppThemeMode.DARK -> AppThemeMode.LIGHT
                                        else -> AppThemeMode.DARK
                                    }
                                    mainViewModel.setThemeMode(nextMode)
                                },
                                onSelectLanguage = { lang ->
                                    triggerHaptic()
                                    mainViewModel.setLanguage(lang)
                                },
                                onNavigate = { dest ->
                                    triggerHaptic()
                                    mainViewModel.navigateTo(dest)
                                },
                                onOpenBirthDayFinder = mainViewModel::setShowBirthDayFinderSheet,
                                onLoadSavedEntry = { entry ->
                                    triggerHaptic()
                                    mainViewModel.onLoadHistoryEntry(entry)
                                }
                            )

                            AppDestination.AGE_DETAILS -> AgeDetailsScreen(
                                result = calculationResult,
                                profileName = profileNameInput,
                                settings = settings,
                                strings = strings,
                                bannerAdUnitId = mainViewModel.adManager.bannerAdUnitId,
                                onNavigate = { dest ->
                                    triggerHaptic()
                                    mainViewModel.navigateTo(dest)
                                },
                                onShowSnackbar = mainViewModel::showSnackbar
                            )

                            AppDestination.ZODIAC -> ZodiacScreen(
                                selectedZodiac = selectedZodiac,
                                calculationResult = calculationResult,
                                settings = settings,
                                strings = strings,
                                bannerAdUnitId = mainViewModel.adManager.bannerAdUnitId,
                                onSelectSign = { sign ->
                                    triggerHaptic()
                                    mainViewModel.onSelectZodiacSign(sign)
                                },
                                onShowSnackbar = mainViewModel::showSnackbar
                            )

                            AppDestination.BIRTHDAY_COUNTDOWN -> BirthdayCountdownScreen(
                                result = calculationResult,
                                settings = settings,
                                strings = strings,
                                bannerAdUnitId = mainViewModel.adManager.bannerAdUnitId,
                                onBack = {
                                    mainViewModel.navigateTo(AppDestination.AGE_DETAILS, recordAdTransition = false)
                                },
                                onNavigateHome = {
                                    mainViewModel.navigateTo(AppDestination.HOME, recordAdTransition = false)
                                },
                                onShowSnackbar = mainViewModel::showSnackbar
                            )

                            AppDestination.HISTORY -> HistoryScreen(
                                entries = filteredHistoryEntries,
                                searchQuery = historySearchQuery,
                                sortOrder = historySortOrder,
                                settings = settings,
                                strings = strings,
                                bannerAdUnitId = mainViewModel.adManager.bannerAdUnitId,
                                onSearchQueryChange = mainViewModel::onSearchHistoryChange,
                                onSortOrderChange = mainViewModel::onSortOrderChange,
                                onOpenEntry = { entry ->
                                    triggerHaptic()
                                    mainViewModel.onLoadHistoryEntry(entry)
                                },
                                onDeleteEntry = { id ->
                                    triggerHaptic()
                                    mainViewModel.onDeleteHistoryEntry(id)
                                },
                                onClearAllHistory = {
                                    triggerHaptic()
                                    mainViewModel.onClearAllHistory()
                                },
                                onNavigateHome = {
                                    mainViewModel.navigateTo(AppDestination.HOME)
                                }
                            )

                            AppDestination.SETTINGS -> SettingsScreen(
                                settings = settings,
                                strings = strings,
                                onSelectThemeMode = { mode ->
                                    triggerHaptic()
                                    mainViewModel.setThemeMode(mode)
                                },
                                onSelectAccentPalette = { palette ->
                                    triggerHaptic()
                                    mainViewModel.setAccentPalette(palette)
                                },
                                onRequestRewardAdUnlock = {
                                    mainViewModel.adManager.requestRewardedAd()
                                },
                                onSelectLanguage = { lang ->
                                    triggerHaptic()
                                    mainViewModel.setLanguage(lang)
                                },
                                onToggleHaptics = mainViewModel::setHapticEnabled,
                                onToggleAnimations = mainViewModel::setAnimationsEnabled,
                                onClearAllHistory = mainViewModel::onClearAllHistory
                            )
                        }
                    }

                    AdOverlays(
                        showInterstitial = showInterstitial,
                        showRewarded = showRewarded,
                        interstitialUnitId = mainViewModel.adManager.interstitialAdUnitId,
                        rewardedUnitId = mainViewModel.adManager.rewardedAdUnitId,
                        onDismissInterstitial = mainViewModel.adManager::dismissInterstitial,
                        onCompleteRewarded = {
                            mainViewModel.adManager.completeRewardedAd {
                                mainViewModel.unlockBonusThemesViaReward()
                            }
                        },
                        onDismissRewarded = mainViewModel.adManager::dismissRewardedAd
                    )
                }
            }
        }
    }
}
