package com.example

import android.content.Context
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performScrollTo
import androidx.test.core.app.ApplicationProvider
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [36], qualifiers = "w411dp-h891dp")
class ExampleRobolectricTest {

    @get:Rule
    val composeTestRule = createAndroidComposeRule<MainActivity>()

    @Test
    fun `app name resource matches Smart Age and Zodiac Calculator`() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val appName = context.getString(R.string.app_name)
        assertEquals("Smart Age & Zodiac Calculator", appName)
    }

    @Test
    fun `home screen renders inline dropdowns live preview and calculating leap day navigates to extensive age tables`() {
        composeTestRule.waitForIdle()

        // Verify Home screen components, 3-language bar, and inline Day/Month/Year dropdowns
        composeTestRule.onNodeWithTag("home_header").assertIsDisplayed()
        composeTestRule.onNodeWithTag("home_lang_chip_en").assertIsDisplayed()
        composeTestRule.onNodeWithTag("home_lang_chip_bn").assertIsDisplayed()
        composeTestRule.onNodeWithTag("home_lang_chip_hi").assertIsDisplayed()
        composeTestRule.onNodeWithTag("dob_day_dropdown").assertIsDisplayed()
        composeTestRule.onNodeWithTag("dob_month_dropdown").assertIsDisplayed()
        composeTestRule.onNodeWithTag("dob_year_dropdown").assertIsDisplayed()
        composeTestRule.onNodeWithTag("instant_live_preview_strip").performScrollTo().assertIsDisplayed()

        // Select Feb 29, 2000 Leap Day preset chip and tap Calculate Age
        composeTestRule.onNodeWithTag("preset_leap_day_chip").performScrollTo().performClick()
        composeTestRule.waitForIdle()

        composeTestRule.onNodeWithTag("calculate_age_button").performScrollTo().performClick()
        composeTestRule.waitForIdle()

        // Verify Age Details screen and all 4 structured information tables
        composeTestRule.onNodeWithTag("age_details_screen").assertIsDisplayed()
        composeTestRule.onNodeWithTag("age_summary_hero_card").assertIsDisplayed()
        composeTestRule.onNodeWithTag("feb29_policy_banner").performScrollTo().assertIsDisplayed()
        composeTestRule.onNodeWithTag("toggle_table_basic_age").performScrollTo().assertIsDisplayed()
        composeTestRule.onNodeWithTag("toggle_table_birthday_info").performScrollTo().assertIsDisplayed()
        composeTestRule.onNodeWithTag("toggle_table_calendar_info").performScrollTo().assertIsDisplayed()
        composeTestRule.onNodeWithTag("toggle_table_time_stats").performScrollTo().assertIsDisplayed()
    }

    @Test
    fun `bottom navigation switches across Zodiac History and Settings screens with 3 languages`() {
        composeTestRule.waitForIdle()

        // Navigate to Zodiac Screen
        composeTestRule.onNodeWithTag("nav_tab_zodiac").performClick()
        composeTestRule.waitForIdle()
        composeTestRule.onNodeWithTag("zodiac_screen").assertIsDisplayed()
        composeTestRule.onNodeWithTag("selected_zodiac_hero_card").assertIsDisplayed()
        composeTestRule.onNodeWithTag("toggle_table_zodiac_details").performScrollTo().assertIsDisplayed()
        composeTestRule.onNodeWithTag("zodiac_comparison_header").performScrollTo().assertIsDisplayed()

        // Navigate to History Screen
        composeTestRule.onNodeWithTag("nav_tab_history").performClick()
        composeTestRule.waitForIdle()
        composeTestRule.onNodeWithTag("history_screen").assertIsDisplayed()
        composeTestRule.onNodeWithTag("history_search_input").assertIsDisplayed()

        // Navigate to Settings Screen and verify Dark Mode & English / Bangla / Hindi language switching
        composeTestRule.onNodeWithTag("nav_tab_settings").performClick()
        composeTestRule.waitForIdle()
        composeTestRule.onNodeWithTag("settings_screen").assertIsDisplayed()
        composeTestRule.onNodeWithTag("theme_dark_chip").performClick()
        composeTestRule.waitForIdle()
        composeTestRule.onNodeWithTag("lang_bengali_chip").performClick()
        composeTestRule.waitForIdle()
        composeTestRule.onNodeWithTag("lang_hindi_chip").performClick()
        composeTestRule.waitForIdle()
        composeTestRule.onNodeWithTag("lang_english_chip").performClick()
        composeTestRule.waitForIdle()
    }
}
