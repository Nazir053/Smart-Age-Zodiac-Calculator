package com.example.presentation.components

import android.os.Build
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.animateIntAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Analytics
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Cake
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.ExpandLess
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.PlayCircleOutline
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.outlined.Analytics
import androidx.compose.material.icons.outlined.AutoAwesome
import androidx.compose.material.icons.outlined.History
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.SelectableDates
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.domain.model.AgeCalculationResult
import com.example.domain.model.AppLanguage
import com.example.domain.model.TableRowItem
import com.example.domain.model.ZodiacProfile
import com.example.presentation.AppDestination
import com.example.utils.LocalizedUiStrings
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneOffset
import kotlin.math.cos
import kotlin.math.sin

/**
 * Enlarged Animated Zodiac Emblem with continuous gentle floating pulse and shimmering starlight.
 * Uses GPU graphicsLayer + Canvas drawing for 60fps zero-recomposition performance.
 */
@Composable
fun AnimatedZodiacEmblem(
    symbol: String,
    animationsEnabled: Boolean = true,
    badgeSize: Dp = 76.dp,
    symbolFontSize: TextUnit = 38.sp,
    containerColor: Color = Color(0xFF1E1B4B),
    symbolColor: Color = Color(0xFFFBBF24),
    starlightColor: Color = Color(0xFFFDE68A),
    modifier: Modifier = Modifier
) {
    val isRobolectric = remember {
        Build.FINGERPRINT.equals("robolectric", ignoreCase = true)
    }
    val shouldAnimate = animationsEnabled && !isRobolectric

    val floatOffsetPx: Float
    val pulseScale: Float
    val shimmerPhase: Float

    if (shouldAnimate) {
        val infiniteTransition = rememberInfiniteTransition(label = "zodiac_starlight_pulse")
        val animFloat by infiniteTransition.animateFloat(
            initialValue = -6f,
            targetValue = 6f,
            animationSpec = infiniteRepeatable(
                animation = tween(durationMillis = 2200, easing = FastOutSlowInEasing),
                repeatMode = RepeatMode.Reverse
            ),
            label = "float_y"
        )
        val animScale by infiniteTransition.animateFloat(
            initialValue = 0.95f,
            targetValue = 1.07f,
            animationSpec = infiniteRepeatable(
                animation = tween(durationMillis = 1800, easing = FastOutSlowInEasing),
                repeatMode = RepeatMode.Reverse
            ),
            label = "scale_pulse"
        )
        val animShimmer by infiniteTransition.animateFloat(
            initialValue = 0f,
            targetValue = 6.2831855f, // 2 * PI
            animationSpec = infiniteRepeatable(
                animation = tween(durationMillis = 3600, easing = LinearEasing),
                repeatMode = RepeatMode.Restart
            ),
            label = "starlight_shimmer"
        )
        floatOffsetPx = animFloat
        pulseScale = animScale
        shimmerPhase = animShimmer
    } else {
        floatOffsetPx = 0f
        pulseScale = 1f
        shimmerPhase = 0.8f
    }

    Box(
        modifier = modifier
            .size(badgeSize)
            .graphicsLayer {
                translationY = floatOffsetPx
                scaleX = pulseScale
                scaleY = pulseScale
            },
        contentAlignment = Alignment.Center
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val center = Offset(size.width / 2f, size.height / 2f)
            val maxRadius = size.minDimension / 2f

            // Glowing radial starlight aura
            drawCircle(
                brush = Brush.radialGradient(
                    colors = listOf(
                        starlightColor.copy(alpha = 0.42f),
                        containerColor.copy(alpha = 0.92f),
                        containerColor
                    ),
                    center = center,
                    radius = maxRadius
                ),
                radius = maxRadius * 0.90f,
                center = center
            )

            // Outer celestial gold ring
            drawCircle(
                color = symbolColor.copy(alpha = 0.82f),
                radius = maxRadius * 0.88f,
                center = center,
                style = Stroke(width = 2.2.dp.toPx())
            )

            // Inner subtle orbital ring
            drawCircle(
                color = starlightColor.copy(alpha = 0.32f),
                radius = maxRadius * 0.72f,
                center = center,
                style = Stroke(width = 1.dp.toPx())
            )

            // 8 Shimmering Starlight particles orbiting the emblem
            val starCount = 8
            val orbitRadius = maxRadius * 0.88f
            for (i in 0 until starCount) {
                val baseAngle = (i * (Math.PI * 2.0 / starCount)).toFloat() + (shimmerPhase * 0.35f)
                val sx = center.x + cos(baseAngle) * orbitRadius
                val sy = center.y + sin(baseAngle) * orbitRadius
                val twinkle = ((sin(shimmerPhase + i * 0.9f) + 1f) / 2f).coerceIn(0.25f, 1f)
                val starRadius = (1.6.dp.toPx()) + (1.8.dp.toPx() * twinkle)

                drawCircle(
                    color = starlightColor.copy(alpha = twinkle),
                    radius = starRadius,
                    center = Offset(sx, sy)
                )
            }
        }

        Text(
            text = symbol,
            fontSize = symbolFontSize,
            fontWeight = FontWeight.Bold,
            color = symbolColor,
            textAlign = TextAlign.Center
        )
    }
}

/**
 * Modern segmented pill switcher for the 3 supported languages:
 * English (Default), বাংলা (Bangla), and हिन्दी (Hindi).
 */
@Composable
fun ModernLanguageSwitcher(
    selectedLanguage: AppLanguage,
    onSelectLanguage: (AppLanguage) -> Unit,
    testTagForLanguage: (AppLanguage) -> String,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier
            .fillMaxWidth()
            .testTag("modern_language_switcher"),
        shape = RoundedCornerShape(18.dp),
        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.65f),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(4.dp),
            horizontalArrangement = Arrangement.spacedBy(6.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            AppLanguage.entries.forEach { langOption ->
                val isSelected = selectedLanguage == langOption
                val scriptBadge = when (langOption) {
                    AppLanguage.ENGLISH -> "EN"
                    AppLanguage.BENGALI -> "বাং"
                    AppLanguage.HINDI -> "हिं"
                }
                val displayLabel = when (langOption) {
                    AppLanguage.ENGLISH -> "English"
                    AppLanguage.BENGALI -> "বাংলা"
                    AppLanguage.HINDI -> "हिन्दी"
                }

                val pillModifier = Modifier
                    .weight(1f)
                    .clip(RoundedCornerShape(14.dp))
                    .then(
                        if (isSelected) {
                            Modifier.background(
                                Brush.horizontalGradient(
                                    colors = listOf(
                                        MaterialTheme.colorScheme.primary,
                                        MaterialTheme.colorScheme.secondary
                                    )
                                )
                            )
                        } else {
                            Modifier.background(Color.Transparent)
                        }
                    )
                    .clickable { onSelectLanguage(langOption) }
                    .padding(vertical = 8.dp, horizontal = 8.dp)
                    .testTag(testTagForLanguage(langOption))

                Row(
                    modifier = pillModifier,
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Surface(
                        shape = CircleShape,
                        color = if (isSelected) {
                            Color.White.copy(alpha = 0.22f)
                        } else {
                            MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.75f)
                        },
                        modifier = Modifier.size(24.dp)
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Text(
                                text = scriptBadge,
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = FontWeight.ExtraBold,
                                color = if (isSelected) {
                                    MaterialTheme.colorScheme.onPrimary
                                } else {
                                    MaterialTheme.colorScheme.primary
                                },
                                fontSize = 10.sp
                            )
                        }
                    }

                    Spacer(modifier = Modifier.width(6.dp))

                    Text(
                        text = displayLabel,
                        style = MaterialTheme.typography.labelLarge,
                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.SemiBold,
                        color = if (isSelected) {
                            MaterialTheme.colorScheme.onPrimary
                        } else {
                            MaterialTheme.colorScheme.onSurfaceVariant
                        },
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }
        }
    }
}

@Composable
fun AnimatedCounter(
    targetValue: Int,
    animationsEnabled: Boolean,
    style: TextStyle,
    color: Color = MaterialTheme.colorScheme.onSurface,
    modifier: Modifier = Modifier
) {
    val animatedValue by animateIntAsState(
        targetValue = targetValue,
        animationSpec = if (animationsEnabled) {
            tween(durationMillis = 280, easing = FastOutSlowInEasing)
        } else {
            tween(durationMillis = 0)
        },
        label = "counter_anim"
    )
    Text(
        text = animatedValue.toString(),
        style = style,
        color = color,
        modifier = modifier
    )
}

@Composable
fun AgeSummaryCard(
    result: AgeCalculationResult,
    language: AppLanguage,
    strings: LocalizedUiStrings,
    animationsEnabled: Boolean,
    onOpenBirthdayCountdown: () -> Unit,
    onOpenZodiac: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .testTag("age_summary_hero_card"),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    Brush.linearGradient(
                        colors = listOf(
                            MaterialTheme.colorScheme.primaryContainer,
                            MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.75f)
                        )
                    )
                )
                .padding(20.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = strings.exactAgeBannerTitle,
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.8f),
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = "${result.formattedBirthDate()} (${result.birthWeekday(language)})",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                }
                Surface(
                    shape = RoundedCornerShape(50),
                    color = MaterialTheme.colorScheme.surface.copy(alpha = 0.90f),
                    modifier = Modifier
                        .clip(RoundedCornerShape(50))
                        .clickable { onOpenZodiac() }
                        .testTag("summary_zodiac_pill")
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        AnimatedZodiacEmblem(
                            symbol = result.zodiacProfile.symbol,
                            animationsEnabled = animationsEnabled,
                            badgeSize = 38.dp,
                            symbolFontSize = 20.sp
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Column {
                            Text(
                                text = result.zodiacProfile.localizedPrimaryName(language),
                                style = MaterialTheme.typography.labelLarge,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Text(
                                text = result.zodiacProfile.element(language),
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.primary
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                AgeUnitBox(
                    value = result.exactYears,
                    label = strings.yearsLabel,
                    animationsEnabled = animationsEnabled,
                    modifier = Modifier.weight(1f)
                )
                AgeUnitBox(
                    value = result.exactMonths,
                    label = strings.monthsLabel,
                    animationsEnabled = animationsEnabled,
                    modifier = Modifier.weight(1f)
                )
                AgeUnitBox(
                    value = result.exactDays,
                    label = strings.daysLabel,
                    animationsEnabled = animationsEnabled,
                    modifier = Modifier.weight(1f)
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            Surface(
                shape = RoundedCornerShape(14.dp),
                color = MaterialTheme.colorScheme.surface.copy(alpha = 0.82f),
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(14.dp))
                    .clickable { onOpenBirthdayCountdown() }
                    .testTag("summary_next_birthday_strip")
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 14.dp, vertical = 10.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Filled.Cake,
                            contentDescription = "Next Birthday",
                            tint = MaterialTheme.colorScheme.tertiary,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = if (result.isBirthdayToday) {
                                when (language) {
                                    AppLanguage.ENGLISH -> "Happy Birthday Today!"
                                    AppLanguage.BENGALI -> "আজ আপনার শুভ জন্মদিন!"
                                    AppLanguage.HINDI -> "आज आपको जन्मदिन की शुभकामनाएँ!"
                                }
                            } else {
                                "${strings.nextBirthdayCountdownLabel}: ${result.daysUntilNextBirthday} ${strings.daysLabel}"
                            },
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.SemiBold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }
                    Text(
                        text = result.formattedNextBirthday(),
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.primary,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}

@Composable
private fun AgeUnitBox(
    value: Int,
    label: String,
    animationsEnabled: Boolean,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(16.dp),
        color = MaterialTheme.colorScheme.surface.copy(alpha = 0.92f)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 14.dp, horizontal = 8.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            AnimatedCounter(
                targetValue = value,
                animationsEnabled = animationsEnabled,
                style = MaterialTheme.typography.headlineLarge,
                color = MaterialTheme.colorScheme.primary
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = label,
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
fun InformationTable(
    tableId: String,
    title: String,
    subtitle: String? = null,
    headerLeft: String,
    headerRight: String,
    rows: List<TableRowItem>,
    initiallyExpanded: Boolean = true,
    animationsEnabled: Boolean = true,
    onCopyRow: (TableRowItem) -> Unit,
    modifier: Modifier = Modifier
) {
    var expanded by remember { mutableStateOf(initiallyExpanded) }

    Card(
        modifier = modifier
            .fillMaxWidth()
            .testTag("info_table_$tableId"),
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)
    ) {
        Column(modifier = Modifier.fillMaxWidth()) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { expanded = !expanded }
                    .padding(horizontal = 16.dp, vertical = 14.dp)
                    .testTag("toggle_table_$tableId"),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = title,
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.onSurface,
                        fontWeight = FontWeight.Bold
                    )
                    if (!subtitle.isNullOrBlank()) {
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = subtitle,
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
                IconButton(onClick = { expanded = !expanded }) {
                    Icon(
                        imageVector = if (expanded) Icons.Filled.ExpandLess else Icons.Filled.ExpandMore,
                        contentDescription = if (expanded) "Collapse table" else "Expand table",
                        tint = MaterialTheme.colorScheme.primary
                    )
                }
            }

            AnimatedVisibility(
                visible = expanded,
                enter = if (animationsEnabled) expandVertically(animationSpec = spring()) + fadeIn() else fadeIn(tween(0)),
                exit = if (animationsEnabled) shrinkVertically(animationSpec = spring()) + fadeOut() else fadeOut(tween(0))
            ) {
                Column(modifier = Modifier.fillMaxWidth()) {
                    HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.65f))
                            .padding(horizontal = 16.dp, vertical = 10.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = headerLeft,
                            style = MaterialTheme.typography.labelMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.weight(0.44f)
                        )
                        Text(
                            text = headerRight,
                            style = MaterialTheme.typography.labelMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.weight(0.46f)
                        )
                        Spacer(modifier = Modifier.width(36.dp))
                    }

                    rows.forEachIndexed { index, row ->
                        val rowBg = if (index % 2 == 0) {
                            Color.Transparent
                        } else {
                            MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.28f)
                        }
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(rowBg)
                                .padding(horizontal = 16.dp, vertical = 11.dp)
                                .testTag("table_row_${row.key}"),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(modifier = Modifier.weight(0.44f)) {
                                Text(
                                    text = row.label,
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                    fontWeight = FontWeight.Medium
                                )
                                if (row.badgeText != null) {
                                    Spacer(modifier = Modifier.height(3.dp))
                                    Surface(
                                        shape = RoundedCornerShape(6.dp),
                                        color = if (row.isApproximate) {
                                            MaterialTheme.colorScheme.tertiaryContainer.copy(alpha = 0.65f)
                                        } else {
                                            MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.65f)
                                        }
                                    ) {
                                        Text(
                                            text = row.badgeText,
                                            style = MaterialTheme.typography.labelSmall,
                                            color = if (row.isApproximate) {
                                                MaterialTheme.colorScheme.onTertiaryContainer
                                            } else {
                                                MaterialTheme.colorScheme.onPrimaryContainer
                                            },
                                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                        )
                                    }
                                }
                            }

                            Text(
                                text = row.value,
                                style = MaterialTheme.typography.bodyMedium,
                                fontWeight = FontWeight.SemiBold,
                                color = MaterialTheme.colorScheme.onSurface,
                                modifier = Modifier
                                    .weight(0.46f)
                                    .padding(horizontal = 6.dp)
                            )

                            IconButton(
                                onClick = { onCopyRow(row) },
                                modifier = Modifier
                                    .size(36.dp)
                                    .testTag("copy_row_${row.key}")
                            ) {
                                Icon(
                                    imageVector = Icons.Filled.ContentCopy,
                                    contentDescription = "Copy ${row.label}",
                                    tint = MaterialTheme.colorScheme.primary.copy(alpha = 0.8f),
                                    modifier = Modifier.size(17.dp)
                                )
                            }
                        }
                        if (index < rows.lastIndex) {
                            HorizontalDivider(
                                color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f)
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun ZodiacCard(
    profile: ZodiacProfile,
    language: AppLanguage,
    isSelected: Boolean,
    animationsEnabled: Boolean = true,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .clip(RoundedCornerShape(20.dp))
            .clickable { onClick() }
            .testTag("zodiac_card_${profile.englishName.lowercase()}"),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (isSelected) {
                MaterialTheme.colorScheme.primaryContainer
            } else {
                MaterialTheme.colorScheme.surface
            }
        ),
        border = BorderStroke(
            width = if (isSelected) 2.dp else 1.dp,
            color = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outlineVariant
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 14.dp, horizontal = 10.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            AnimatedZodiacEmblem(
                symbol = profile.symbol,
                animationsEnabled = animationsEnabled,
                badgeSize = 68.dp,
                symbolFontSize = 34.sp
            )
            Spacer(modifier = Modifier.height(10.dp))
            Text(
                text = profile.localizedPrimaryName(language),
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            Text(
                text = when (language) {
                    AppLanguage.ENGLISH -> "${profile.bengaliName} • ${profile.hindiName}"
                    AppLanguage.BENGALI -> profile.englishName
                    AppLanguage.HINDI -> profile.englishName
                },
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.primary,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = profile.dateRange(language),
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}

@Composable
fun BirthdayCountdownCard(
    result: AgeCalculationResult,
    language: AppLanguage,
    animationsEnabled: Boolean,
    modifier: Modifier = Modifier
) {
    val animatedProgress by animateFloatAsState(
        targetValue = result.birthdayCycleProgress,
        animationSpec = if (animationsEnabled) tween(600, easing = FastOutSlowInEasing) else tween(0),
        label = "birthday_progress"
    )
    val primaryColor = MaterialTheme.colorScheme.primary
    val trackColor = MaterialTheme.colorScheme.surfaceVariant
    val accentColor = MaterialTheme.colorScheme.tertiary

    Card(
        modifier = modifier
            .fillMaxWidth()
            .testTag("birthday_countdown_card"),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(22.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier.size(200.dp)
            ) {
                Canvas(modifier = Modifier.size(180.dp)) {
                    val strokeWidth = 16.dp.toPx()
                    drawArc(
                        color = trackColor,
                        startAngle = -90f,
                        sweepAngle = 360f,
                        useCenter = false,
                        style = Stroke(width = strokeWidth, cap = StrokeCap.Round)
                    )
                    drawArc(
                        brush = Brush.sweepGradient(
                            listOf(primaryColor, accentColor, primaryColor)
                        ),
                        startAngle = -90f,
                        sweepAngle = 360f * animatedProgress,
                        useCenter = false,
                        style = Stroke(width = strokeWidth, cap = StrokeCap.Round)
                    )
                }

                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    AnimatedCounter(
                        targetValue = result.daysUntilNextBirthday.toInt(),
                        animationsEnabled = animationsEnabled,
                        style = MaterialTheme.typography.displayMedium,
                        color = MaterialTheme.colorScheme.primary
                    )
                    Text(
                        text = when (language) {
                            AppLanguage.ENGLISH -> "Days Remaining"
                            AppLanguage.BENGALI -> "দিন বাকি"
                            AppLanguage.HINDI -> "दिन शेष"
                        },
                        style = MaterialTheme.typography.labelLarge,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Surface(
                        shape = RoundedCornerShape(50),
                        color = MaterialTheme.colorScheme.tertiaryContainer
                    ) {
                        Text(
                            text = "${(animatedProgress * 100).toInt()}% ${
                                when (language) {
                                    AppLanguage.ENGLISH -> "of year"
                                    AppLanguage.BENGALI -> "সম্পন্ন"
                                    AppLanguage.HINDI -> "पूर्ण"
                                }
                            }",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onTertiaryContainer,
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 3.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(18.dp))

            Text(
                text = when (language) {
                    AppLanguage.ENGLISH ->
                        "${result.nextBirthdayRemainingMonths} Months & ${result.nextBirthdayRemainingDays} Days Remaining"
                    AppLanguage.BENGALI ->
                        "${result.nextBirthdayRemainingMonths} মাস এবং ${result.nextBirthdayRemainingDays} দিন বাকি"
                    AppLanguage.HINDI ->
                        "${result.nextBirthdayRemainingMonths} महीने और ${result.nextBirthdayRemainingDays} दिन शेष"
                },
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )

            Spacer(modifier = Modifier.height(6.dp))

            Text(
                text = when (language) {
                    AppLanguage.ENGLISH ->
                        "Next Birthday: ${result.formattedNextBirthday()} (${result.nextBirthdayWeekdayEn}) • Turning ${result.ageOnNextBirthday}"
                    AppLanguage.BENGALI ->
                        "পরবর্তী জন্মদিন: ${result.formattedNextBirthday()} (${result.nextBirthdayWeekdayBn}) • বয়স হবে ${result.ageOnNextBirthday}"
                    AppLanguage.HINDI ->
                        "अगला जन्मदिन: ${result.formattedNextBirthday()} (${result.nextBirthdayWeekdayHi}) • आयु होगी ${result.ageOnNextBirthday}"
                },
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CustomDatePickerDialog(
    initialDate: LocalDate,
    onDateSelected: (LocalDate) -> Unit,
    onDismiss: () -> Unit
) {
    val todayMillis = remember {
        LocalDate.now().atStartOfDay(ZoneOffset.UTC).toInstant().toEpochMilli()
    }
    val initialMillis = remember(initialDate) {
        initialDate.atStartOfDay(ZoneOffset.UTC).toInstant().toEpochMilli()
    }

    val datePickerState = rememberDatePickerState(
        initialSelectedDateMillis = initialMillis,
        selectableDates = object : SelectableDates {
            override fun isSelectableDate(utcTimeMillis: Long): Boolean {
                return utcTimeMillis <= todayMillis
            }

            override fun isSelectableYear(year: Int): Boolean {
                return year in 1900..LocalDate.now().year
            }
        }
    )

    DatePickerDialog(
        onDismissRequest = onDismiss,
        confirmButton = {
            Button(
                onClick = {
                    val millis = datePickerState.selectedDateMillis
                    if (millis != null) {
                        val pickedDate = Instant.ofEpochMilli(millis)
                            .atZone(ZoneOffset.UTC)
                            .toLocalDate()
                        onDateSelected(pickedDate)
                    }
                    onDismiss()
                },
                modifier = Modifier.testTag("confirm_date_picker_btn")
            ) {
                Text("Select Date")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    ) {
        DatePicker(state = datePickerState)
    }
}

@Composable
fun AdBanner(
    adUnitId: String,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp)
            .testTag("admob_banner_container"),
        shape = RoundedCornerShape(12.dp),
        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.55f),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(52.dp)
                .padding(horizontal = 12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Surface(
                    shape = RoundedCornerShape(4.dp),
                    color = MaterialTheme.colorScheme.primaryContainer
                ) {
                    Text(
                        text = "Ad",
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onPrimaryContainer,
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                    )
                }
                Spacer(modifier = Modifier.width(10.dp))
                Column {
                    Text(
                        text = "Google AdMob Test Banner",
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = "Unit: ${adUnitId.takeLast(14)}",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
            Text(
                text = "Test Mode",
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.primary
            )
        }
    }
}

@Composable
fun AdOverlays(
    showInterstitial: Boolean,
    showRewarded: Boolean,
    interstitialUnitId: String,
    rewardedUnitId: String,
    onDismissInterstitial: () -> Unit,
    onCompleteRewarded: () -> Unit,
    onDismissRewarded: () -> Unit
) {
    if (showInterstitial) {
        AlertDialog(
            onDismissRequest = onDismissInterstitial,
            title = { Text("Google AdMob Test Interstitial") },
            text = {
                Column {
                    Text("Shown at a natural navigation transition with strict frequency capping.")
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = "Test Ad Unit ID: $interstitialUnitId",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = onDismissInterstitial,
                    modifier = Modifier.testTag("close_interstitial_ad_btn")
                ) {
                    Text("Continue to App")
                }
            }
        )
    }

    if (showRewarded) {
        AlertDialog(
            onDismissRequest = onDismissRewarded,
            icon = {
                Icon(
                    imageVector = Icons.Filled.PlayCircleOutline,
                    contentDescription = "Rewarded Ad",
                    tint = MaterialTheme.colorScheme.primary
                )
            },
            title = { Text("Unlock Bonus Celestial Themes") },
            text = {
                Column {
                    Text(
                        "Watch this optional Google AdMob test rewarded ad to permanently unlock the Celestial Gold and Cosmic Violet accent themes."
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "Test Rewarded Unit: $rewardedUnitId",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = onCompleteRewarded,
                    modifier = Modifier.testTag("claim_reward_ad_btn")
                ) {
                    Text("Claim Theme Reward")
                }
            },
            dismissButton = {
                OutlinedButton(onClick = onDismissRewarded) {
                    Text("Not Now")
                }
            }
        )
    }
}

private data class BottomNavTab(
    val destination: AppDestination,
    val labelSelector: (LocalizedUiStrings) -> String,
    val selectedIcon: ImageVector,
    val unselectedIcon: ImageVector,
    val testTag: String
)

@Composable
fun CustomBottomNavigation(
    currentDestination: AppDestination,
    strings: LocalizedUiStrings,
    onNavigate: (AppDestination) -> Unit
) {
    val tabs = remember {
        listOf(
            BottomNavTab(
                destination = AppDestination.HOME,
                labelSelector = { it.navHome },
                selectedIcon = Icons.Filled.Home,
                unselectedIcon = Icons.Outlined.Home,
                testTag = "nav_tab_home"
            ),
            BottomNavTab(
                destination = AppDestination.AGE_DETAILS,
                labelSelector = { it.navAgeDetails },
                selectedIcon = Icons.Filled.Analytics,
                unselectedIcon = Icons.Outlined.Analytics,
                testTag = "nav_tab_age_details"
            ),
            BottomNavTab(
                destination = AppDestination.ZODIAC,
                labelSelector = { it.navZodiac },
                selectedIcon = Icons.Filled.AutoAwesome,
                unselectedIcon = Icons.Outlined.AutoAwesome,
                testTag = "nav_tab_zodiac"
            ),
            BottomNavTab(
                destination = AppDestination.HISTORY,
                labelSelector = { it.navHistory },
                selectedIcon = Icons.Filled.History,
                unselectedIcon = Icons.Outlined.History,
                testTag = "nav_tab_history"
            ),
            BottomNavTab(
                destination = AppDestination.SETTINGS,
                labelSelector = { it.navSettings },
                selectedIcon = Icons.Filled.Settings,
                unselectedIcon = Icons.Outlined.Settings,
                testTag = "nav_tab_settings"
            )
        )
    }

    val effectiveSelected = if (currentDestination == AppDestination.BIRTHDAY_COUNTDOWN) {
        AppDestination.AGE_DETAILS
    } else {
        currentDestination
    }

    NavigationBar(
        modifier = Modifier
            .fillMaxWidth()
            .windowInsetsPadding(WindowInsets.navigationBars)
            .testTag("bottom_navigation_bar"),
        containerColor = MaterialTheme.colorScheme.surface,
        tonalElevation = 4.dp
    ) {
        tabs.forEach { tab ->
            val selected = effectiveSelected == tab.destination
            NavigationBarItem(
                selected = selected,
                onClick = { onNavigate(tab.destination) },
                icon = {
                    Icon(
                        imageVector = if (selected) tab.selectedIcon else tab.unselectedIcon,
                        contentDescription = tab.labelSelector(strings)
                    )
                },
                label = {
                    Text(
                        text = tab.labelSelector(strings),
                        style = MaterialTheme.typography.labelSmall,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                },
                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor = MaterialTheme.colorScheme.onPrimaryContainer,
                    selectedTextColor = MaterialTheme.colorScheme.primary,
                    indicatorColor = MaterialTheme.colorScheme.primaryContainer,
                    unselectedIconColor = MaterialTheme.colorScheme.onSurfaceVariant,
                    unselectedTextColor = MaterialTheme.colorScheme.onSurfaceVariant
                ),
                modifier = Modifier.testTag(tab.testTag)
            )
        }
    }
}

@Composable
fun EmptyState(
    icon: ImageVector,
    title: String,
    subtitle: String,
    actionLabel: String? = null,
    onActionClick: (() -> Unit)? = null,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 12.dp),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.45f)
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(28.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Surface(
                shape = CircleShape,
                color = MaterialTheme.colorScheme.primaryContainer,
                modifier = Modifier.size(64.dp)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(
                        imageVector = icon,
                        contentDescription = title,
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(30.dp)
                    )
                }
            }
            Spacer(modifier = Modifier.height(14.dp))
            Text(
                text = title,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface,
                textAlign = TextAlign.Center
            )
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = subtitle,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center
            )
            if (actionLabel != null && onActionClick != null) {
                Spacer(modifier = Modifier.height(16.dp))
                Button(onClick = onActionClick) {
                    Text(actionLabel)
                }
            }
        }
    }
}

@Composable
fun ResponsiveScreenContainer(
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit
) {
    Box(
        modifier = modifier.fillMaxWidth(),
        contentAlignment = Alignment.TopCenter
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .widthIn(max = 680.dp)
        ) {
            content()
        }
    }
}
