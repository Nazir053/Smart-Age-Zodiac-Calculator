package com.example.presentation.age

import androidx.compose.foundation.BorderStroke
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
import androidx.compose.material.icons.filled.Cake
import androidx.compose.material.icons.filled.Calculate
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Image
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.Button
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.domain.model.AgeCalculationResult
import com.example.domain.model.AppLanguage
import com.example.domain.model.AppSettings
import com.example.domain.model.TableRowItem
import com.example.presentation.AppDestination
import com.example.presentation.components.AdBanner
import com.example.presentation.components.AgeSummaryCard
import com.example.presentation.components.EmptyState
import com.example.presentation.components.InformationTable
import com.example.presentation.components.ResponsiveScreenContainer
import com.example.utils.LocalizedUiStrings
import com.example.utils.ShareAndExportHelper

@Composable
fun AgeDetailsScreen(
    result: AgeCalculationResult?,
    profileName: String,
    settings: AppSettings,
    strings: LocalizedUiStrings,
    bannerAdUnitId: String,
    onNavigate: (AppDestination) -> Unit,
    onShowSnackbar: (String) -> Unit
) {
    val context = LocalContext.current
    val lang = settings.language

    ResponsiveScreenContainer {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 16.dp, vertical = 12.dp)
                .testTag("age_details_screen")
        ) {
            if (result == null) {
                EmptyState(
                    icon = Icons.Filled.Calculate,
                    title = when (lang) {
                        AppLanguage.ENGLISH -> "No Birth Date Selected"
                        AppLanguage.BENGALI -> "কোনো জন্ম তারিখ নির্বাচন করা হয়নি"
                        AppLanguage.HINDI -> "कोई जन्म तिथि नहीं चुनी गई"
                    },
                    subtitle = strings.noRecentCalculations,
                    actionLabel = strings.navHome,
                    onActionClick = { onNavigate(AppDestination.HOME) }
                )
                return@Column
            }

            // Memoize all 4 tables for instant, zero-allocation scrolling
            val basicAgeRows = remember(result, lang) { result.getBasicAgeTable(lang) }
            val birthdayRows = remember(result, lang) { result.getBirthdayTable(lang) }
            val calendarRows = remember(result, lang) { result.getCalendarTable(lang) }
            val timeStatsRows = remember(result, lang) { result.getTimeStatsTable(lang) }

            AgeSummaryCard(
                result = result,
                language = lang,
                strings = strings,
                animationsEnabled = settings.animationsEnabled,
                onOpenBirthdayCountdown = { onNavigate(AppDestination.BIRTHDAY_COUNTDOWN) },
                onOpenZodiac = { onNavigate(AppDestination.ZODIAC) }
            )

            Spacer(modifier = Modifier.height(14.dp))

            // Top Action Buttons: Copy All, Share Text, Share Visual Card Image
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                FilledTonalButton(
                    onClick = {
                        val fullText = result.formatFullReportText(lang)
                        ShareAndExportHelper.copyToClipboard(context, "Age Report", fullText)
                        onShowSnackbar(strings.copiedToast)
                    },
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier
                        .weight(1f)
                        .testTag("copy_all_button")
                ) {
                    Icon(
                        imageVector = Icons.Filled.ContentCopy,
                        contentDescription = strings.copyAllBtn,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = strings.copyAllBtn,
                        style = MaterialTheme.typography.labelMedium,
                        maxLines = 1
                    )
                }

                OutlinedButton(
                    onClick = {
                        val fullText = result.formatFullReportText(lang)
                        ShareAndExportHelper.shareTextReport(context, fullText)
                    },
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier
                        .weight(1f)
                        .testTag("share_results_button")
                ) {
                    Icon(
                        imageVector = Icons.Filled.Share,
                        contentDescription = strings.shareResultsBtn,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = strings.shareResultsBtn,
                        style = MaterialTheme.typography.labelMedium,
                        maxLines = 1
                    )
                }

                Button(
                    onClick = {
                        ShareAndExportHelper.shareAgeSummaryCardImage(
                            context = context,
                            result = result,
                            language = lang,
                            profileName = profileName
                        )
                    },
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier
                        .weight(1f)
                        .testTag("share_card_image_button")
                ) {
                    Icon(
                        imageVector = Icons.Filled.Image,
                        contentDescription = strings.shareCardImageBtn,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = strings.shareCardImageBtn,
                        style = MaterialTheme.typography.labelMedium,
                        maxLines = 1
                    )
                }
            }

            // Leap-Year February 29 Policy Banner (if applicable)
            if (result.isFeb29Birth) {
                Spacer(modifier = Modifier.height(12.dp))
                Surface(
                    shape = RoundedCornerShape(14.dp),
                    color = MaterialTheme.colorScheme.tertiaryContainer,
                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.tertiary),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("feb29_policy_banner")
                ) {
                    Row(
                        modifier = Modifier.padding(14.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Filled.Info,
                            contentDescription = "Leap Year Policy",
                            tint = MaterialTheme.colorScheme.onTertiaryContainer
                        )
                        Spacer(modifier = Modifier.width(10.dp))
                        Text(
                            text = result.feb29PolicyNote(lang),
                            style = MaterialTheme.typography.bodySmall,
                            fontWeight = FontWeight.Medium,
                            color = MaterialTheme.colorScheme.onTertiaryContainer
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            val copySingleRow: (TableRowItem) -> Unit = { row ->
                ShareAndExportHelper.copyToClipboard(context, row.label, "${row.label}: ${row.value}")
                onShowSnackbar("${row.label} — ${strings.copiedToast}")
            }

            // Table 1: Basic Age Information
            InformationTable(
                tableId = "basic_age",
                title = strings.table1Title,
                subtitle = when (lang) {
                    AppLanguage.ENGLISH -> "Exact calendar Period & ChronoUnit breakdown"
                    AppLanguage.BENGALI -> "সঠিক ক্যালেন্ডার এবং সময় এককের বিস্তারিত হিসাব"
                    AppLanguage.HINDI -> "सटीक कैलेंडर और समय इकाइयों का विवरण"
                },
                headerLeft = strings.tableHeaderInformation,
                headerRight = strings.tableHeaderResult,
                rows = basicAgeRows,
                initiallyExpanded = true,
                animationsEnabled = settings.animationsEnabled,
                onCopyRow = copySingleRow
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Table 2: Birthday Information
            InformationTable(
                tableId = "birthday_info",
                title = strings.table2Title,
                subtitle = when (lang) {
                    AppLanguage.ENGLISH -> "Last birthday, next milestone & seasonal context"
                    AppLanguage.BENGALI -> "গত এবং পরবর্তী জন্মদিনের সম্পূর্ণ বিবরণ"
                    AppLanguage.HINDI -> "पिछले और अगले जन्मदिन का संपूर्ण विवरण"
                },
                headerLeft = strings.tableHeaderInformation,
                headerRight = strings.tableHeaderResult,
                rows = birthdayRows,
                initiallyExpanded = true,
                animationsEnabled = settings.animationsEnabled,
                onCopyRow = copySingleRow
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Table 3: Calendar Information
            InformationTable(
                tableId = "calendar_info",
                title = strings.table3Title,
                subtitle = when (lang) {
                    AppLanguage.ENGLISH -> "Leap year status, month lengths & ordinal day metrics"
                    AppLanguage.BENGALI -> "লিপ ইয়ার, মাসের দিন সংখ্যা ও কোয়ার্টার তথ্য"
                    AppLanguage.HINDI -> "लीप वर्ष स्थिति, महीने के दिन और तिमाही विवरण"
                },
                headerLeft = strings.tableHeaderInformation,
                headerRight = strings.tableHeaderResult,
                rows = calendarRows,
                initiallyExpanded = true,
                animationsEnabled = settings.animationsEnabled,
                onCopyRow = copySingleRow
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Table 4: Time Statistics
            InformationTable(
                tableId = "time_stats",
                title = strings.table4Title,
                subtitle = when (lang) {
                    AppLanguage.ENGLISH ->
                        "Distinguishes exact calendar totals from date-midnight approximate conversions"
                    AppLanguage.BENGALI ->
                        "সঠিক ক্যালেন্ডার দিন/সপ্তাহ/মাস বনাম মধ্যরাত-ভিত্তিক আনুমানিক রূপান্তর"
                    AppLanguage.HINDI ->
                        "सटीक कैलेंडर कुल बनाम मध्यरात्रि-आधारित अनुमानित समय रूपांतरण"
                },
                headerLeft = strings.tableHeaderInformation,
                headerRight = strings.tableHeaderResult,
                rows = timeStatsRows,
                initiallyExpanded = true,
                animationsEnabled = settings.animationsEnabled,
                onCopyRow = copySingleRow
            )

            Spacer(modifier = Modifier.height(16.dp))

            OutlinedButton(
                onClick = { onNavigate(AppDestination.BIRTHDAY_COUNTDOWN) },
                shape = RoundedCornerShape(14.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp)
                    .testTag("open_dedicated_countdown_btn")
            ) {
                Icon(imageVector = Icons.Filled.Cake, contentDescription = null)
                Spacer(modifier = Modifier.width(8.dp))
                Text(strings.birthdayCountdownTitle, fontWeight = FontWeight.SemiBold)
            }

            Spacer(modifier = Modifier.height(16.dp))

            AdBanner(adUnitId = bannerAdUnitId)
        }
    }
}
