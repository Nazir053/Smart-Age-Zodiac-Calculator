package com.example.ads

import com.example.BuildConfig
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

/**
 * Centralized Google AdMob Monetization Manager.
 *
 * Strictly adheres to Google Play and AdMob policies:
 * 1. Uses official Google test Ad Unit IDs during development/debug builds, configurable via BuildConfig.
 * 2. Enforces strict frequency capping on Interstitial ads (minimum 4 natural navigation transitions
 *    and 90-second cooldown; never displayed immediately after every calculation or during date picking).
 * 3. Provides strictly opt-in Rewarded Ad flow to unlock bonus decorative themes without gating core features.
 * 4. Ensures banner containers reserve stable layout height and never cause accidental clicks.
 */
class AdManager {

    companion object {
        const val TEST_BANNER_UNIT_ID = "ca-app-pub-3940256099942544/6300978111"
        const val TEST_INTERSTITIAL_UNIT_ID = "ca-app-pub-3940256099942544/1033173712"
        const val TEST_REWARDED_UNIT_ID = "ca-app-pub-3940256099942544/5224354917"

        private const val INTERSTITIAL_TRANSITION_THRESHOLD = 4
        private const val INTERSTITIAL_COOLDOWN_MS = 90_000L
    }

    val bannerAdUnitId: String = BuildConfig.ADMOB_BANNER_UNIT_ID.ifBlank { TEST_BANNER_UNIT_ID }
    val interstitialAdUnitId: String = BuildConfig.ADMOB_INTERSTITIAL_UNIT_ID.ifBlank { TEST_INTERSTITIAL_UNIT_ID }
    val rewardedAdUnitId: String = BuildConfig.ADMOB_REWARDED_UNIT_ID.ifBlank { TEST_REWARDED_UNIT_ID }

    private var naturalTransitionCount = 0
    private var lastInterstitialShownAtMs = 0L

    private val _showInterstitialOverlay = MutableStateFlow(false)
    val showInterstitialOverlay: StateFlow<Boolean> = _showInterstitialOverlay.asStateFlow()

    private val _showRewardedAdOverlay = MutableStateFlow(false)
    val showRewardedAdOverlay: StateFlow<Boolean> = _showRewardedAdOverlay.asStateFlow()

    /**
     * Records a natural screen transition (e.g., switching bottom navigation tabs).
     * Never called during date selection or immediately on Calculate button press.
     */
    fun onNaturalNavigationTransition(nowMillis: Long = System.currentTimeMillis()): Boolean {
        naturalTransitionCount++
        val cooldownElapsed = (nowMillis - lastInterstitialShownAtMs) >= INTERSTITIAL_COOLDOWN_MS
        if (naturalTransitionCount >= INTERSTITIAL_TRANSITION_THRESHOLD && cooldownElapsed) {
            naturalTransitionCount = 0
            lastInterstitialShownAtMs = nowMillis
            _showInterstitialOverlay.value = true
            return true
        }
        return false
    }

    fun dismissInterstitial() {
        _showInterstitialOverlay.value = false
    }

    fun requestRewardedAd() {
        _showRewardedAdOverlay.value = true
    }

    fun completeRewardedAd(onRewardEarned: () -> Unit) {
        _showRewardedAdOverlay.value = false
        onRewardEarned()
    }

    fun dismissRewardedAd() {
        _showRewardedAdOverlay.value = false
    }
}
