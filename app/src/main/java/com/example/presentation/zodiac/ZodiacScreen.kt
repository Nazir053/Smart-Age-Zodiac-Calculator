package com.example.presentation.zodiac

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
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
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.WarningAmber
import androidx.compose.material3.AssistChip
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SuggestionChip
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.domain.calculator.ZodiacCalculator
import com.example.domain.model.AgeCalculationResult
import com.example.domain.model.AppLanguage
import com.example.domain.model.AppSettings
import com.example.domain.model.ZodiacProfile
import com.example.presentation.components.AdBanner
import com.example.presentation.components.AnimatedZodiacEmblem
import com.example.presentation.components.InformationTable
import com.example.presentation.components.ResponsiveScreenContainer
import com.example.presentation.components.ZodiacCard
import com.example.utils.LocalizedUiStrings
import com.example.utils.ShareAndExportHelper

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun ZodiacScreen(
    selectedZodiac: ZodiacProfile,
    calculationResult: AgeCalculationResult?,
    settings: AppSettings,
    strings: LocalizedUiStrings,
    bannerAdUnitId: String,
    onSelectSign: (ZodiacProfile) -> Unit,
    onShowSnackbar: (String) -> Unit
) {
    val context = LocalContext.current
    val lang = settings.language
    val birthDateZodiac = calculationResult?.zodiacProfile

    ResponsiveScreenContainer {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 16.dp, vertical = 12.dp)
                .testTag("zodiac_screen")
        ) {
            Text(
                text = strings.zodiacScreenTitle,
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )
            if (birthDateZodiac != null) {
                Spacer(modifier = Modifier.height(4.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = "${strings.selectedZodiacLabel}: ${birthDateZodiac.symbol} ${birthDateZodiac.name(lang)}",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.primary,
                        fontWeight = FontWeight.SemiBold,
                        modifier = Modifier.weight(1f)
                    )
                    if (selectedZodiac.id != birthDateZodiac.id) {
                        Spacer(modifier = Modifier.width(8.dp))
                        AssistChip(
                            onClick = { onSelectSign(birthDateZodiac) },
                            label = {
                                Text(
                                    when (lang) {
                                        AppLanguage.ENGLISH -> "My DOB Sign"
                                        AppLanguage.BENGALI -> "আমার রাশিতে ফিরুন"
                                        AppLanguage.HINDI -> "मेरी राशि पर लौटें"
                                    }
                                )
                            }
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Animated Selected Zodiac Hero Card with Enlarged 92.dp Floating Starlight Emblem
            AnimatedContent(
                targetState = selectedZodiac,
                transitionSpec = {
                    if (settings.animationsEnabled) {
                        fadeIn(tween(220)) togetherWith fadeOut(tween(150))
                    } else {
                        fadeIn(tween(0)) togetherWith fadeOut(tween(0))
                    }
                },
                label = "zodiac_hero_reveal"
            ) { sign ->
                SelectedZodiacHeroCard(
                    profile = sign,
                    language = lang,
                    strings = strings,
                    animationsEnabled = settings.animationsEnabled
                )
            }

            // Boundary Date Advisory (if the user's calculated DOB falls near a zodiac boundary)
            if (birthDateZodiac != null && birthDateZodiac.id == selectedZodiac.id && birthDateZodiac.isNearCusp) {
                Spacer(modifier = Modifier.height(12.dp))
                Surface(
                    shape = RoundedCornerShape(14.dp),
                    color = MaterialTheme.colorScheme.tertiaryContainer,
                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.tertiary),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("zodiac_boundary_note")
                ) {
                    Row(
                        modifier = Modifier.padding(14.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Filled.WarningAmber,
                            contentDescription = "Cusp Boundary Note",
                            tint = MaterialTheme.colorScheme.onTertiaryContainer
                        )
                        Spacer(modifier = Modifier.width(10.dp))
                        Text(
                            text = ZodiacCalculator.getBoundaryDisclaimer(lang),
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onTertiaryContainer
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Memoized Selected Zodiac Information Table (11 Rows)
            val detailRows = remember(selectedZodiac, lang) {
                ZodiacCalculator.getZodiacDetailsTable(selectedZodiac, lang)
            }
            InformationTable(
                tableId = "zodiac_details",
                title = "${strings.zodiacDetailsTableTitle} — ${selectedZodiac.localizedPrimaryName(lang)}",
                subtitle = "${selectedZodiac.englishName} • ${selectedZodiac.bengaliName} • ${selectedZodiac.hindiName} (${selectedZodiac.symbol})",
                headerLeft = strings.tableHeaderInformation,
                headerRight = when (lang) {
                    AppLanguage.ENGLISH -> "Details"
                    AppLanguage.BENGALI -> "বিস্তারিত"
                    AppLanguage.HINDI -> "विवरण"
                },
                rows = detailRows,
                initiallyExpanded = true,
                animationsEnabled = settings.animationsEnabled,
                onCopyRow = { row ->
                    ShareAndExportHelper.copyToClipboard(context, row.label, "${row.label}: ${row.value}")
                    onShowSnackbar("${row.label} — ${strings.copiedToast}")
                }
            )

            Spacer(modifier = Modifier.height(18.dp))

            // 12-Sign Comparison Table
            ZodiacComparisonTableCard(
                allSigns = ZodiacCalculator.allSigns,
                selectedSignId = selectedZodiac.id,
                language = lang,
                title = strings.zodiacComparisonTableTitle,
                onSelectSign = onSelectSign
            )

            Spacer(modifier = Modifier.height(20.dp))

            // All 12 Zodiac Cards Grid with Enlarged Animated Emblems
            Text(
                text = strings.allZodiacSignsTitle,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )
            Spacer(modifier = Modifier.height(10.dp))

            val chunkedSigns = remember { ZodiacCalculator.allSigns.chunked(3) }
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                chunkedSigns.forEach { rowSigns ->
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        rowSigns.forEach { sign ->
                            ZodiacCard(
                                profile = sign,
                                language = lang,
                                isSelected = sign.id == selectedZodiac.id,
                                animationsEnabled = settings.animationsEnabled,
                                onClick = { onSelectSign(sign) },
                                modifier = Modifier.weight(1f)
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(18.dp))

            // Traditional Astrological Disclaimer
            Surface(
                shape = RoundedCornerShape(14.dp),
                color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.55f),
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("zodiac_scientific_disclaimer")
            ) {
                Row(
                    modifier = Modifier.padding(14.dp),
                    verticalAlignment = Alignment.Top
                ) {
                    Icon(
                        imageVector = Icons.Filled.Info,
                        contentDescription = "Astrological Disclaimer",
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(10.dp))
                    Text(
                        text = ZodiacCalculator.getScientificDisclaimer(lang),
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            AdBanner(adUnitId = bannerAdUnitId)
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun SelectedZodiacHeroCard(
    profile: ZodiacProfile,
    language: AppLanguage,
    strings: LocalizedUiStrings,
    animationsEnabled: Boolean
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .testTag("selected_zodiac_hero_card"),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
        elevation = CardDefaults.cardElevation(defaultElevation = 3.dp)
    ) {
        Column(modifier = Modifier.fillMaxWidth()) {
            // Illustrated Banner Header with Enlarged 92.dp Animated Zodiac Emblem
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(148.dp)
            ) {
                Image(
                    painter = painterResource(id = R.drawable.img_celestial_hero_1794219631278),
                    contentDescription = profile.englishName,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize()
                )
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(
                            Brush.verticalGradient(
                                listOf(
                                    Color.Black.copy(alpha = 0.25f),
                                    Color.Black.copy(alpha = 0.82f)
                                )
                            )
                        )
                        .padding(18.dp),
                    contentAlignment = Alignment.CenterStart
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        AnimatedZodiacEmblem(
                            symbol = profile.symbol,
                            animationsEnabled = animationsEnabled,
                            badgeSize = 92.dp,
                            symbolFontSize = 46.sp
                        )
                        Spacer(modifier = Modifier.width(16.dp))
                        Column {
                            Text(
                                text = profile.localizedPrimaryName(language),
                                style = MaterialTheme.typography.headlineMedium,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                            Text(
                                text = profile.localizedSecondaryName(language),
                                style = MaterialTheme.typography.titleSmall,
                                color = Color(0xFFFBBF24),
                                fontWeight = FontWeight.SemiBold
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "${profile.dateRange(language)}  •  ${profile.element(language)}",
                                style = MaterialTheme.typography.bodySmall,
                                color = Color(0xFFE2E8F0)
                            )
                        }
                    }
                }
            }

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(18.dp)
            ) {
                Text(
                    text = profile.description(language),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurface
                )

                Spacer(modifier = Modifier.height(14.dp))

                Text(
                    text = strings.strengthsTitle,
                    style = MaterialTheme.typography.labelLarge,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )
                Spacer(modifier = Modifier.height(6.dp))
                FlowRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    profile.strengths(language).forEach { item ->
                        SuggestionChip(
                            onClick = {},
                            label = { Text(item, style = MaterialTheme.typography.labelSmall) }
                        )
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                Text(
                    text = strings.associatedTraitsTitle,
                    style = MaterialTheme.typography.labelLarge,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.secondary
                )
                Spacer(modifier = Modifier.height(6.dp))
                FlowRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    profile.associatedTraits(language).forEach { trait ->
                        SuggestionChip(
                            onClick = {},
                            label = { Text(trait, style = MaterialTheme.typography.labelSmall) }
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun ZodiacComparisonTableCard(
    allSigns: List<ZodiacProfile>,
    selectedSignId: com.example.domain.model.ZodiacSignId,
    language: AppLanguage,
    title: String,
    onSelectSign: (ZodiacProfile) -> Unit
) {
    val horizontalScroll = rememberScrollState()

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .testTag("zodiac_12_sign_comparison_table"),
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)
    ) {
        Column(modifier = Modifier.fillMaxWidth()) {
            Column(
                modifier = Modifier
                    .padding(16.dp)
                    .testTag("zodiac_comparison_header")
            ) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = when (language) {
                        AppLanguage.ENGLISH ->
                            "Tap any row to inspect that sign in detail (horizontally scrollable)"
                        AppLanguage.BENGALI ->
                            "যেকোনো সারিতে ট্যাপ করে সেই রাশির সম্পূর্ণ তথ্য দেখুন (ডানে স্ক্রলযোগ্য)"
                        AppLanguage.HINDI ->
                            "किसी भी राशि का पूरा विवरण देखने के लिए उस पंक्ति पर टैप करें"
                    },
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(horizontalScroll)
            ) {
                Row(
                    modifier = Modifier
                        .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.7f))
                        .padding(horizontal = 14.dp, vertical = 10.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    ComparisonHeaderCell(
                        when (language) {
                            AppLanguage.ENGLISH -> "Sign (EN)"
                            AppLanguage.BENGALI -> "রাশি (EN)"
                            AppLanguage.HINDI -> "राशि (EN)"
                        },
                        115.dp
                    )
                    ComparisonHeaderCell(
                        when (language) {
                            AppLanguage.ENGLISH -> "Bangla / Hindi"
                            AppLanguage.BENGALI -> "বাংলা নাম"
                            AppLanguage.HINDI -> "हिंदी नाम"
                        },
                        130.dp
                    )
                    ComparisonHeaderCell(
                        when (language) {
                            AppLanguage.ENGLISH -> "Element"
                            AppLanguage.BENGALI -> "উপাদান"
                            AppLanguage.HINDI -> "तत्व"
                        },
                        110.dp
                    )
                    ComparisonHeaderCell(
                        when (language) {
                            AppLanguage.ENGLISH -> "Ruling Planet"
                            AppLanguage.BENGALI -> "শাসক গ্রহ"
                            AppLanguage.HINDI -> "स्वामी ग्रह"
                        },
                        150.dp
                    )
                    ComparisonHeaderCell(
                        when (language) {
                            AppLanguage.ENGLISH -> "Date Range"
                            AppLanguage.BENGALI -> "তারিখ সীমা"
                            AppLanguage.HINDI -> "तिथि सीमा"
                        },
                        165.dp
                    )
                }

                allSigns.forEachIndexed { index, sign ->
                    val isSelected = sign.id == selectedSignId
                    val rowBg = when {
                        isSelected -> MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.55f)
                        index % 2 == 1 -> MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.25f)
                        else -> Color.Transparent
                    }

                    Row(
                        modifier = Modifier
                            .background(rowBg)
                            .clickable { onSelectSign(sign) }
                            .padding(horizontal = 14.dp, vertical = 10.dp)
                            .testTag("comparison_row_${sign.englishName.lowercase()}"),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        ComparisonBodyCell("${sign.symbol} ${sign.englishName}", 115.dp, bold = true)
                        ComparisonBodyCell(
                            when (language) {
                                AppLanguage.ENGLISH -> "${sign.bengaliName} / ${sign.hindiName}"
                                AppLanguage.BENGALI -> sign.bengaliName
                                AppLanguage.HINDI -> sign.hindiName
                            },
                            130.dp
                        )
                        ComparisonBodyCell(sign.element(language), 110.dp)
                        ComparisonBodyCell(sign.rulingPlanet(language), 150.dp)
                        ComparisonBodyCell(sign.dateRange(language), 165.dp)
                    }
                    if (index < allSigns.lastIndex) {
                        HorizontalDivider(
                            color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.45f)
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun ComparisonHeaderCell(text: String, width: androidx.compose.ui.unit.Dp) {
    Text(
        text = text,
        style = MaterialTheme.typography.labelMedium,
        fontWeight = FontWeight.Bold,
        color = MaterialTheme.colorScheme.onSurfaceVariant,
        modifier = Modifier
            .width(width)
            .padding(end = 8.dp)
    )
}

@Composable
private fun ComparisonBodyCell(
    text: String,
    width: androidx.compose.ui.unit.Dp,
    bold: Boolean = false
) {
    Text(
        text = text,
        style = MaterialTheme.typography.bodySmall,
        fontWeight = if (bold) FontWeight.Bold else FontWeight.Normal,
        color = MaterialTheme.colorScheme.onSurface,
        modifier = Modifier
            .width(width)
            .padding(end = 8.dp)
    )
}
