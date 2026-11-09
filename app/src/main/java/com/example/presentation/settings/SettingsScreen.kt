package com.example.presentation.settings

import android.content.Intent
import androidx.compose.foundation.BorderStroke
import androidx.core.net.toUri
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Animation
import androidx.compose.material.icons.filled.DeleteForever
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Palette
import androidx.compose.material.icons.filled.PlayCircleOutline
import androidx.compose.material.icons.filled.PrivacyTip
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.StarRate
import androidx.compose.material.icons.filled.Vibration
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.domain.model.AccentPalette
import com.example.domain.model.AppLanguage
import com.example.domain.model.AppSettings
import com.example.domain.model.AppThemeMode
import com.example.presentation.components.ModernLanguageSwitcher
import com.example.presentation.components.ResponsiveScreenContainer
import com.example.utils.LocalizedUiStrings
import com.example.utils.ShareAndExportHelper

@Composable
fun SettingsScreen(
    settings: AppSettings,
    strings: LocalizedUiStrings,
    onSelectThemeMode: (AppThemeMode) -> Unit,
    onSelectAccentPalette: (AccentPalette) -> Unit,
    onRequestRewardAdUnlock: () -> Unit,
    onSelectLanguage: (AppLanguage) -> Unit,
    onToggleHaptics: (Boolean) -> Unit,
    onToggleAnimations: (Boolean) -> Unit,
    onClearAllHistory: () -> Unit
) {
    val context = LocalContext.current
    val lang = settings.language
    var showPrivacyDialog by remember { mutableStateOf(false) }
    var showAboutDialog by remember { mutableStateOf(false) }

    if (showPrivacyDialog) {
        PrivacyPolicyDialog(
            language = lang,
            onDismiss = { showPrivacyDialog = false }
        )
    }

    if (showAboutDialog) {
        AboutAppDialog(
            language = lang,
            onDismiss = { showAboutDialog = false }
        )
    }

    ResponsiveScreenContainer {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 16.dp, vertical = 12.dp)
                .testTag("settings_screen")
        ) {
            Text(
                text = strings.settingsTitle,
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )

            Spacer(modifier = Modifier.height(14.dp))

            // 1. Appearance & Theme Mode Card
            SettingsSectionCard(title = strings.appearanceSection) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    FilterChip(
                        selected = settings.themeMode == AppThemeMode.LIGHT,
                        onClick = { onSelectThemeMode(AppThemeMode.LIGHT) },
                        label = { Text(strings.themeLight) },
                        modifier = Modifier
                            .weight(1f)
                            .testTag("theme_light_chip")
                    )
                    FilterChip(
                        selected = settings.themeMode == AppThemeMode.DARK,
                        onClick = { onSelectThemeMode(AppThemeMode.DARK) },
                        label = { Text(strings.themeDark) },
                        modifier = Modifier
                            .weight(1f)
                            .testTag("theme_dark_chip")
                    )
                    FilterChip(
                        selected = settings.themeMode == AppThemeMode.SYSTEM,
                        onClick = { onSelectThemeMode(AppThemeMode.SYSTEM) },
                        label = { Text(strings.themeSystem) },
                        modifier = Modifier
                            .weight(1f)
                            .testTag("theme_system_chip")
                    )
                }

                Spacer(modifier = Modifier.height(14.dp))
                HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)
                Spacer(modifier = Modifier.height(12.dp))

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Filled.Palette,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = strings.accentPaletteTitle,
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                AccentPalette.entries.forEach { palette ->
                    val unlocked = palette in settings.unlockedPalettes || !palette.requiresRewardUnlock
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { onSelectAccentPalette(palette) }
                            .padding(vertical = 6.dp)
                            .testTag("palette_option_${palette.id}"),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            RadioButton(
                                selected = settings.accentPalette == palette,
                                onClick = { onSelectAccentPalette(palette) }
                            )
                            Text(
                                text = palette.title(lang),
                                style = MaterialTheme.typography.bodyMedium,
                                fontWeight = FontWeight.Medium
                            )
                        }
                        if (!unlocked) {
                            Icon(
                                imageVector = Icons.Filled.Lock,
                                contentDescription = "Locked Bonus Theme",
                                tint = MaterialTheme.colorScheme.tertiary,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                    }
                }

                val hasLockedPalettes = AccentPalette.entries.any { it !in settings.unlockedPalettes }
                if (hasLockedPalettes) {
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedButton(
                        onClick = onRequestRewardAdUnlock,
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("unlock_reward_themes_button")
                    ) {
                        Icon(imageVector = Icons.Filled.PlayCircleOutline, contentDescription = null)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(strings.unlockWithRewardAdBtn)
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // 2. Language Selection Card (English Default, Bangla, Hindi)
            SettingsSectionCard(title = strings.languageSection) {
                ModernLanguageSwitcher(
                    selectedLanguage = settings.language,
                    onSelectLanguage = onSelectLanguage,
                    testTagForLanguage = { langOption ->
                        when (langOption) {
                            AppLanguage.ENGLISH -> "lang_english_chip"
                            AppLanguage.BENGALI -> "lang_bengali_chip"
                            AppLanguage.HINDI -> "lang_hindi_chip"
                        }
                    }
                )
            }

            Spacer(modifier = Modifier.height(14.dp))

            // 3. Interaction & Motion Card
            SettingsSectionCard(title = strings.preferencesSection) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Filled.Vibration,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary
                        )
                        Spacer(modifier = Modifier.width(12.dp))
                        Text(
                            text = strings.hapticFeedbackLabel,
                            style = MaterialTheme.typography.bodyLarge
                        )
                    }
                    Switch(
                        checked = settings.hapticFeedbackEnabled,
                        onCheckedChange = onToggleHaptics,
                        modifier = Modifier.testTag("toggle_haptics_switch")
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))
                HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)
                Spacer(modifier = Modifier.height(8.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.weight(1f)
                    ) {
                        Icon(
                            imageVector = Icons.Filled.Animation,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary
                        )
                        Spacer(modifier = Modifier.width(12.dp))
                        Text(
                            text = strings.animationsLabel,
                            style = MaterialTheme.typography.bodyLarge
                        )
                    }
                    Switch(
                        checked = settings.animationsEnabled,
                        onCheckedChange = onToggleAnimations,
                        modifier = Modifier.testTag("toggle_animations_switch")
                    )
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // 4. Privacy, About, Share, Rate, Clear History
            SettingsSectionCard(title = strings.dataPrivacySection) {
                SettingsActionRow(
                    icon = Icons.Filled.PrivacyTip,
                    title = strings.privacyPolicyLabel,
                    testTag = "settings_privacy_policy_row",
                    onClick = { showPrivacyDialog = true }
                )
                HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)
                SettingsActionRow(
                    icon = Icons.Filled.Info,
                    title = strings.aboutAppLabel,
                    testTag = "settings_about_app_row",
                    onClick = { showAboutDialog = true }
                )
                HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)
                SettingsActionRow(
                    icon = Icons.Filled.Share,
                    title = strings.shareAppLabel,
                    testTag = "settings_share_app_row",
                    onClick = {
                        ShareAndExportHelper.shareTextReport(
                            context = context,
                            reportText = "Smart Age & Zodiac Calculator — Accurate java.time age dashboard & English/Bangla/Hindi Western Zodiac insights!",
                            title = strings.shareAppLabel
                        )
                    }
                )
                HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)
                SettingsActionRow(
                    icon = Icons.Filled.StarRate,
                    title = strings.rateAppLabel,
                    testTag = "settings_rate_app_row",
                    onClick = {
                        val uri = "market://details?id=${context.packageName}".toUri()
                        val intent = Intent(Intent.ACTION_VIEW, uri).apply {
                            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                        }
                        runCatching { context.startActivity(intent) }
                    }
                )
                HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)
                SettingsActionRow(
                    icon = Icons.Filled.DeleteForever,
                    title = strings.clearAllHistoryBtn,
                    testTag = "settings_clear_history_row",
                    onClick = onClearAllHistory
                )
            }

            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

@Composable
private fun SettingsSectionCard(
    title: String,
    content: @Composable () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )
            Spacer(modifier = Modifier.height(10.dp))
            content()
        }
    }
}

@Composable
private fun SettingsActionRow(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    title: String,
    testTag: String,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .padding(vertical = 12.dp)
            .testTag(testTag),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = icon,
            contentDescription = title,
            tint = MaterialTheme.colorScheme.primary,
            modifier = Modifier.size(22.dp)
        )
        Spacer(modifier = Modifier.width(12.dp))
        Text(
            text = title,
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurface
        )
    }
}

@Composable
private fun PrivacyPolicyDialog(
    language: AppLanguage,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                when (language) {
                    AppLanguage.ENGLISH -> "Privacy Policy"
                    AppLanguage.BENGALI -> "গোপনীয়তা নীতি (Privacy Policy)"
                    AppLanguage.HINDI -> "गोपनीयता नीति (Privacy Policy)"
                }
            )
        },
        text = {
            Column(
                modifier = Modifier.verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(
                    text = when (language) {
                        AppLanguage.ENGLISH ->
                            "1. Local-First Data Storage: All birth dates, saved profiles, and calculation history are stored exclusively on your device using Android Jetpack DataStore. We never transmit or collect your Date of Birth on external servers.\n\n" +
                                "2. Advertising & Analytics: This application uses Google AdMob with strict frequency limits and test/configurable ad units. Birth date inputs are never shared with advertising networks.\n\n" +
                                "3. Full User Control: You can delete individual records or wipe all locally stored history and preferences at any time from the History or Settings screens."
                        AppLanguage.BENGALI ->
                            "১. অফলাইন-ফার্স্ট ও স্থানীয় স্টোরেজ: আপনার জন্ম তারিখ, হিসাবের ইতিহাস এবং প্রোফাইলসমূহ শুধুমাত্র আপনার ডিভাইসের লোকাল DataStore-এ সংরক্ষিত থাকে। কোনো ব্যক্তিগত জন্মতারিখ সার্ভারে পাঠানো হয় না।\n\n" +
                                "২. বিজ্ঞাপন ও মনটাইজেশন: এই অ্যাপটি Google AdMob প্রদর্শন করে। জন্ম তারিখের তথ্য বিজ্ঞাপন টার্গেটিং বা অ্যানালিটিক্সের জন্য সংগ্রহ করা হয় না।\n\n" +
                                "৩. তথ্য মুছে ফেলার অধিকার: আপনি যেকোনো সময় 'ইতিহাস' বা 'সেটিংস' স্ক্রিন থেকে এক ক্লিকে সমস্ত স্থানীয় ডেটা মুছে ফেলতে পারেন।"
                        AppLanguage.HINDI ->
                            "1. ऑफ़लाइन और स्थानीय स्टोरेज: आपकी सभी जन्म तिथियाँ, सहेजे गए प्रोफ़ाइल और गणना इतिहास केवल आपके डिवाइस के लोकल DataStore में सुरक्षित रहते हैं। कोई भी जन्म तिथि किसी बाहरी सर्वर पर नहीं भेजी जाती।\n\n" +
                                "2. विज्ञापन और गोपनीयता: यह ऐप Google AdMob का उपयोग करता है। जन्म तिथि की जानकारी कभी भी विज्ञापन नेटवर्क के साथ साझा नहीं की जाती।\n\n" +
                                "3. पूर्ण नियंत्रण: आप किसी भी समय 'इतिहास' या 'सेटिंग्स' स्क्रीन से अपना सारा स्थानीय डेटा मिटा सकते हैं।"
                    },
                    style = MaterialTheme.typography.bodySmall
                )
            }
        },
        confirmButton = {
            Button(onClick = onDismiss) {
                Text(
                    when (language) {
                        AppLanguage.ENGLISH -> "Close"
                        AppLanguage.BENGALI -> "বুঝেছি"
                        AppLanguage.HINDI -> "बंद करें"
                    }
                )
            }
        }
    )
}

@Composable
private fun AboutAppDialog(
    language: AppLanguage,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                when (language) {
                    AppLanguage.ENGLISH -> "Smart Age & Zodiac Calculator"
                    AppLanguage.BENGALI -> "স্মার্ট বয়স ও রাশিফল ক্যালকুলেটর"
                    AppLanguage.HINDI -> "स्मार्ट आयु और राशिफल कैलकुलेटर"
                }
            )
        },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text(
                    text = "Version 1.0.0 • Package: com.nazir.smartagecalculator",
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.primary
                )
                Text(
                    text = when (language) {
                        AppLanguage.ENGLISH ->
                            "Precision chronological age calculator powered by java.time, featuring 4 structured analytical tables, leap-year February 29 handling, live birthday progress rings, and a complete 12-sign trilingual (English, Bangla, Hindi) Western Tropical Zodiac reference."
                        AppLanguage.BENGALI ->
                            "java.time ভিত্তিক শতভাগ নির্ভুল ক্যালেন্ডার বয়স গণনা, ৪টি বিস্তারিত টেবিল, জন্মদিন কাউন্টডাউন এবং ১২টি পাশ্চাত্য ট্রপিক্যাল রাশির বাংলা, হিন্দি ও ইংরেজি তথ্য ড্যাশবোর্ড।"
                        AppLanguage.HINDI ->
                            "java.time पर आधारित सटीक कैलेंडर आयु कैलकुलेटर, 4 विस्तृत तालिकाएँ, जन्मदिन काउंटडाउन रिंग और अंग्रेजी, बांग्ला व हिंदी में सभी 12 पश्चिमी राशियों का संपूर्ण डैशबोर्ड।"
                    },
                    style = MaterialTheme.typography.bodySmall
                )
            }
        },
        confirmButton = {
            Button(onClick = onDismiss) {
                Text(
                    when (language) {
                        AppLanguage.ENGLISH -> "OK"
                        AppLanguage.BENGALI -> "বন্ধ করুন"
                        AppLanguage.HINDI -> "ठीक है"
                    }
                )
            }
        }
    )
}
