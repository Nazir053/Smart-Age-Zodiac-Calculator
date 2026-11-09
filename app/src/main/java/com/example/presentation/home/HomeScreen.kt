package com.example.presentation.home

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Analytics
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.BookmarkAdd
import androidx.compose.material.icons.filled.Cake
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Calculate
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.DarkMode
import androidx.compose.material.icons.filled.EventAvailable
import androidx.compose.material.icons.filled.LightMode
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Translate
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.AssistChip
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.FilterChip
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.domain.calculator.AgeCalculationEngine
import com.example.domain.model.AgeCalculationResult
import com.example.domain.model.AppLanguage
import com.example.domain.model.AppSettings
import com.example.domain.model.AppThemeMode
import com.example.domain.model.SavedCalculationEntry
import com.example.presentation.AppDestination
import com.example.presentation.components.AdBanner
import com.example.presentation.components.AnimatedZodiacEmblem
import com.example.presentation.components.CustomDatePickerDialog
import com.example.presentation.components.ModernLanguageSwitcher
import com.example.presentation.components.ResponsiveScreenContainer
import com.example.utils.LocalizedUiStrings
import java.time.LocalDate
import java.time.Month
import java.time.Year
import java.time.YearMonth
import java.time.format.DateTimeFormatter
import java.util.Locale

@Composable
fun HomeScreen(
    selectedBirthDate: LocalDate?,
    profileNameInput: String,
    pinProfileChecked: Boolean,
    validationError: String?,
    calculationResult: AgeCalculationResult?,
    pinnedProfiles: List<SavedCalculationEntry>,
    recentCalculations: List<SavedCalculationEntry>,
    settings: AppSettings,
    strings: LocalizedUiStrings,
    bannerAdUnitId: String,
    showBirthDayFinderSheet: Boolean,
    onSelectBirthDate: (LocalDate) -> Unit,
    onProfileNameChange: (String) -> Unit,
    onPinProfileChange: (Boolean) -> Unit,
    onCalculateClick: () -> Unit,
    onClearClick: () -> Unit,
    onToggleTheme: () -> Unit,
    onSelectLanguage: (AppLanguage) -> Unit,
    onNavigate: (AppDestination) -> Unit,
    onOpenBirthDayFinder: (Boolean) -> Unit,
    onLoadSavedEntry: (SavedCalculationEntry) -> Unit
) {
    var showDatePicker by remember { mutableStateOf(false) }
    val today = remember { LocalDate.now() }
    val dateFormatter = remember { DateTimeFormatter.ofPattern("dd MMM yyyy", Locale.ENGLISH) }
    val lang = settings.language

    if (showDatePicker) {
        CustomDatePickerDialog(
            initialDate = selectedBirthDate ?: today.minusYears(25),
            onDateSelected = onSelectBirthDate,
            onDismiss = { showDatePicker = false }
        )
    }

    if (showBirthDayFinderSheet) {
        BirthDayFinderDialog(
            initialDate = selectedBirthDate ?: today.minusYears(25),
            language = lang,
            onSelectForMainCalc = { date ->
                onSelectBirthDate(date)
                onOpenBirthDayFinder(false)
            },
            onDismiss = { onOpenBirthDayFinder(false) }
        )
    }

    ResponsiveScreenContainer {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 16.dp, vertical = 10.dp)
                .testTag("home_screen_container")
        ) {
            // A. Header + Trilingual Quick Switcher Bar
            HomeHeader(
                strings = strings,
                settings = settings,
                onToggleTheme = onToggleTheme,
                onSelectLanguage = onSelectLanguage,
                onOpenSettings = { onNavigate(AppDestination.SETTINGS) }
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Saved DOB Profiles Quick-Switch Bar (if any)
            if (pinnedProfiles.isNotEmpty()) {
                Text(
                    text = strings.savedProfilesTitle,
                    style = MaterialTheme.typography.labelLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(bottom = 6.dp)
                )
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    pinnedProfiles.forEach { profile ->
                        FilterChip(
                            selected = selectedBirthDate?.toString() == profile.birthDateIso,
                            onClick = { onLoadSavedEntry(profile) },
                            label = {
                                Text("${profile.zodiacSymbol} ${profile.profileName} (${profile.birthDateIso})")
                            },
                            leadingIcon = {
                                Icon(
                                    imageVector = Icons.Filled.Person,
                                    contentDescription = null,
                                    modifier = Modifier.size(16.dp)
                                )
                            }
                        )
                    }
                }
                Spacer(modifier = Modifier.height(12.dp))
            }

            // B. Main Age Calculator Card with Inline Day / Month / Year Dropdowns + Calendar Picker + Live Preview
            MainAgeCalculatorCard(
                selectedBirthDate = selectedBirthDate ?: today.minusYears(25),
                calculationResult = calculationResult,
                profileNameInput = profileNameInput,
                pinProfileChecked = pinProfileChecked,
                validationError = validationError,
                strings = strings,
                language = lang,
                animationsEnabled = settings.animationsEnabled,
                onOpenCalendarPicker = { showDatePicker = true },
                onSelectBirthDate = onSelectBirthDate,
                onProfileNameChange = onProfileNameChange,
                onPinProfileChange = onPinProfileChange,
                onCalculateClick = onCalculateClick,
                onClearClick = onClearClick,
                onOpenZodiacScreen = { onNavigate(AppDestination.ZODIAC) }
            )

            Spacer(modifier = Modifier.height(16.dp))

            // C. Quick Information Card
            QuickInformationCard(
                today = today,
                calculationResult = calculationResult,
                language = lang,
                strings = strings,
                dateFormatter = dateFormatter,
                onOpenCountdown = { onNavigate(AppDestination.BIRTHDAY_COUNTDOWN) }
            )

            Spacer(modifier = Modifier.height(16.dp))

            // D. Quick Tools Grid
            Text(
                text = strings.quickToolsTitle,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )
            Spacer(modifier = Modifier.height(8.dp))
            QuickToolsGrid(
                strings = strings,
                language = lang,
                onOpenAgeDetails = { onNavigate(AppDestination.AGE_DETAILS) },
                onOpenZodiac = { onNavigate(AppDestination.ZODIAC) },
                onOpenBirthdayCountdown = { onNavigate(AppDestination.BIRTHDAY_COUNTDOWN) },
                onOpenBirthDayFinder = { onOpenBirthDayFinder(true) }
            )

            Spacer(modifier = Modifier.height(18.dp))

            // E. Recent Calculations
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = strings.recentCalculationsTitle,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                if (recentCalculations.isNotEmpty()) {
                    TextButton(onClick = { onNavigate(AppDestination.HISTORY) }) {
                        Text(
                            when (lang) {
                                AppLanguage.ENGLISH -> "See All"
                                AppLanguage.BENGALI -> "সব দেখুন"
                                AppLanguage.HINDI -> "सभी देखें"
                            }
                        )
                    }
                }
            }
            Spacer(modifier = Modifier.height(6.dp))

            if (recentCalculations.isEmpty()) {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.45f)
                    )
                ) {
                    Text(
                        text = strings.noRecentCalculations,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.padding(16.dp)
                    )
                }
            } else {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    recentCalculations.take(4).forEach { entry ->
                        RecentCalculationItemCard(
                            entry = entry,
                            strings = strings,
                            animationsEnabled = settings.animationsEnabled,
                            onViewDetails = { onLoadSavedEntry(entry) }
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            AdBanner(adUnitId = bannerAdUnitId)

            Spacer(modifier = Modifier.height(10.dp))
        }
    }
}

@Composable
private fun HomeHeader(
    strings: LocalizedUiStrings,
    settings: AppSettings,
    onToggleTheme: () -> Unit,
    onSelectLanguage: (AppLanguage) -> Unit,
    onOpenSettings: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .testTag("home_header")
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.weight(1f)
            ) {
                Surface(
                    shape = RoundedCornerShape(16.dp),
                    color = MaterialTheme.colorScheme.primaryContainer,
                    border = BorderStroke(
                        width = 1.5.dp,
                        color = MaterialTheme.colorScheme.primary.copy(alpha = 0.65f)
                    ),
                    shadowElevation = 4.dp,
                    modifier = Modifier
                        .size(56.dp)
                        .testTag("home_app_logo_badge")
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.img_launcher_icon_fg_1794219630344),
                        contentDescription = strings.appTitle,
                        contentScale = ContentScale.Crop,
                        modifier = Modifier
                            .fillMaxSize()
                            .graphicsLayer(
                                scaleX = 1.18f,
                                scaleY = 1.18f
                            )
                    )
                }
                Spacer(modifier = Modifier.width(12.dp))
                Column {
                    Text(
                        text = strings.appTitle,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    Text(
                        text = strings.appSubtitle,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }

            Row(verticalAlignment = Alignment.CenterVertically) {
                IconButton(
                    onClick = {
                        val next = when (settings.language) {
                            AppLanguage.ENGLISH -> AppLanguage.BENGALI
                            AppLanguage.BENGALI -> AppLanguage.HINDI
                            AppLanguage.HINDI -> AppLanguage.ENGLISH
                        }
                        onSelectLanguage(next)
                    },
                    modifier = Modifier.testTag("header_language_toggle")
                ) {
                    Icon(
                        imageVector = Icons.Filled.Translate,
                        contentDescription = "Switch Language",
                        tint = MaterialTheme.colorScheme.primary
                    )
                }
                IconButton(
                    onClick = onToggleTheme,
                    modifier = Modifier.testTag("header_theme_toggle")
                ) {
                    Icon(
                        imageVector = if (settings.themeMode == AppThemeMode.DARK) {
                            Icons.Filled.LightMode
                        } else {
                            Icons.Filled.DarkMode
                        },
                        contentDescription = "Toggle Theme",
                        tint = MaterialTheme.colorScheme.primary
                    )
                }
                IconButton(
                    onClick = onOpenSettings,
                    modifier = Modifier.testTag("header_settings_shortcut")
                ) {
                    Icon(
                        imageVector = Icons.Filled.Settings,
                        contentDescription = "Open Settings",
                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        // Modern Segmented 3-Language Switcher: English (Default) | বাংলা | हिन्दी
        ModernLanguageSwitcher(
            selectedLanguage = settings.language,
            onSelectLanguage = onSelectLanguage,
            testTagForLanguage = { langOption -> "home_lang_chip_${langOption.code}" }
        )
    }
}

@Composable
private fun MainAgeCalculatorCard(
    selectedBirthDate: LocalDate,
    calculationResult: AgeCalculationResult?,
    profileNameInput: String,
    pinProfileChecked: Boolean,
    validationError: String?,
    strings: LocalizedUiStrings,
    language: AppLanguage,
    animationsEnabled: Boolean,
    onOpenCalendarPicker: () -> Unit,
    onSelectBirthDate: (LocalDate) -> Unit,
    onProfileNameChange: (String) -> Unit,
    onPinProfileChange: (Boolean) -> Unit,
    onCalculateClick: () -> Unit,
    onClearClick: () -> Unit,
    onOpenZodiacScreen: () -> Unit
) {
    val currentYear = remember { LocalDate.now().year }
    val yearOptions = remember(currentYear) { (currentYear downTo 1900).toList() }
    val maxDayInMonth = remember(selectedBirthDate.year, selectedBirthDate.monthValue) {
        YearMonth.of(selectedBirthDate.year, selectedBirthDate.monthValue).lengthOfMonth()
    }
    val dayOptions = remember(maxDayInMonth) { (1..maxDayInMonth).toList() }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .testTag("main_age_calculator_card"),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 3.dp),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)
    ) {
        Column(modifier = Modifier.fillMaxWidth()) {
            // Top Decorative Celestial Banner Strip with Calendar Picker Button
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(82.dp)
            ) {
                Image(
                    painter = painterResource(id = R.drawable.img_celestial_hero_1794219631278),
                    contentDescription = null,
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
                                    Color.Black.copy(alpha = 0.76f)
                                )
                            )
                        )
                        .padding(horizontal = 18.dp, vertical = 12.dp),
                    contentAlignment = Alignment.BottomStart
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = strings.selectDobLabel,
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                        Surface(
                            shape = RoundedCornerShape(50),
                            color = Color.White.copy(alpha = 0.22f),
                            border = BorderStroke(1.dp, Color.White.copy(alpha = 0.45f)),
                            modifier = Modifier
                                .clip(RoundedCornerShape(50))
                                .clickable { onOpenCalendarPicker() }
                                .testTag("dob_selector_box")
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    imageVector = Icons.Filled.CalendarMonth,
                                    contentDescription = strings.calendarPickerBtn,
                                    tint = Color.White,
                                    modifier = Modifier.size(16.dp)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = strings.calendarPickerBtn,
                                    style = MaterialTheme.typography.labelMedium,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White
                                )
                            }
                        }
                    }
                }
            }

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(18.dp)
            ) {
                // Inline Day, Month, and Year Dropdowns for super easy, 1-tap date input
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    // DAY DROPDOWN
                    InlineDateDropdownBox(
                        label = strings.dayDropdownLabel,
                        selectedText = String.format(Locale.US, "%02d", selectedBirthDate.dayOfMonth),
                        options = dayOptions,
                        optionLabel = { d -> String.format(Locale.US, "%02d", d) },
                        onOptionSelected = { newDay ->
                            val clampedDay = newDay.coerceIn(1, maxDayInMonth)
                            onSelectBirthDate(
                                LocalDate.of(selectedBirthDate.year, selectedBirthDate.monthValue, clampedDay)
                            )
                        },
                        testTag = "dob_day_dropdown",
                        modifier = Modifier.weight(0.27f)
                    )

                    // MONTH DROPDOWN
                    InlineDateDropdownBox(
                        label = strings.monthDropdownLabel,
                        selectedText = AgeCalculationEngine.monthLabel(selectedBirthDate.month).get(language),
                        options = Month.entries,
                        optionLabel = { m ->
                            "${m.value}. ${AgeCalculationEngine.monthLabel(m).get(language)}"
                        },
                        onOptionSelected = { newMonth ->
                            val maxDays = YearMonth.of(selectedBirthDate.year, newMonth.value).lengthOfMonth()
                            val safeDay = selectedBirthDate.dayOfMonth.coerceAtMost(maxDays)
                            onSelectBirthDate(
                                LocalDate.of(selectedBirthDate.year, newMonth.value, safeDay)
                            )
                        },
                        testTag = "dob_month_dropdown",
                        modifier = Modifier.weight(0.41f)
                    )

                    // YEAR DROPDOWN
                    InlineDateDropdownBox(
                        label = strings.yearDropdownLabel,
                        selectedText = selectedBirthDate.year.toString(),
                        options = yearOptions,
                        optionLabel = { y -> y.toString() },
                        onOptionSelected = { newYear ->
                            val maxDays = YearMonth.of(newYear, selectedBirthDate.monthValue).lengthOfMonth()
                            val safeDay = selectedBirthDate.dayOfMonth.coerceAtMost(maxDays)
                            onSelectBirthDate(
                                LocalDate.of(newYear, selectedBirthDate.monthValue, safeDay)
                            )
                        },
                        testTag = "dob_year_dropdown",
                        modifier = Modifier.weight(0.32f)
                    )
                }

                // Quick Preset Chips for instant 1-tap testing
                Spacer(modifier = Modifier.height(10.dp))
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    AssistChip(
                        onClick = { onSelectBirthDate(LocalDate.of(2000, 2, 29)) },
                        label = {
                            Text(
                                when (language) {
                                    AppLanguage.ENGLISH -> "29 Feb 2000 (Leap Day)"
                                    AppLanguage.BENGALI -> "২৯ ফেব্রুয়ারি ২০০০ (লিপ ডে)"
                                    AppLanguage.HINDI -> "29 फ़रवरी 2000 (लीप दिवस)"
                                }
                            )
                        },
                        modifier = Modifier.testTag("preset_leap_day_chip")
                    )
                    AssistChip(
                        onClick = { onSelectBirthDate(LocalDate.of(1998, 3, 21)) },
                        label = { Text("21 Mar 1998") },
                        modifier = Modifier.testTag("preset_1998_chip")
                    )
                    AssistChip(
                        onClick = { onSelectBirthDate(LocalDate.of(1990, 11, 9)) },
                        label = { Text("09 Nov 1990") }
                    )
                }

                // Instant Live Age & Enlarged Animated Zodiac Preview Strip
                if (calculationResult != null) {
                    Spacer(modifier = Modifier.height(12.dp))
                    Surface(
                        shape = RoundedCornerShape(18.dp),
                        color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.55f),
                        border = BorderStroke(1.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.35f)),
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(18.dp))
                            .clickable { onOpenZodiacScreen() }
                            .testTag("instant_live_preview_strip")
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 14.dp, vertical = 12.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = strings.livePreviewLabel,
                                    style = MaterialTheme.typography.labelSmall,
                                    color = MaterialTheme.colorScheme.primary,
                                    fontWeight = FontWeight.Bold
                                )
                                Spacer(modifier = Modifier.height(2.dp))
                                Text(
                                    text = calculationResult.exactAgeSummary(language),
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                                Text(
                                    text = "${calculationResult.birthWeekday(language)}  •  ${calculationResult.zodiacProfile.localizedPrimaryName(language)}",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }

                            AnimatedZodiacEmblem(
                                symbol = calculationResult.zodiacProfile.symbol,
                                animationsEnabled = animationsEnabled,
                                badgeSize = 58.dp,
                                symbolFontSize = 30.sp
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Optional Profile Name Field & Pin Checkbox
                OutlinedTextField(
                    value = profileNameInput,
                    onValueChange = onProfileNameChange,
                    label = { Text(strings.profileNameLabel) },
                    placeholder = { Text(strings.profileNamePlaceholder) },
                    singleLine = true,
                    shape = RoundedCornerShape(14.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("profile_name_input")
                )

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { onPinProfileChange(!pinProfileChecked) }
                        .padding(vertical = 2.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Checkbox(
                        checked = pinProfileChecked,
                        onCheckedChange = onPinProfileChange,
                        modifier = Modifier.testTag("pin_profile_checkbox")
                    )
                    Icon(
                        imageVector = Icons.Filled.BookmarkAdd,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = strings.saveAsProfileLabel,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }

                if (!validationError.isNullOrBlank()) {
                    Spacer(modifier = Modifier.height(8.dp))
                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = MaterialTheme.colorScheme.errorContainer,
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("validation_error_banner")
                    ) {
                        Text(
                            text = validationError,
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onErrorContainer,
                            modifier = Modifier.padding(12.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Calculate & Reset Action Buttons
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    OutlinedButton(
                        onClick = onClearClick,
                        shape = RoundedCornerShape(14.dp),
                        modifier = Modifier
                            .weight(0.34f)
                            .height(50.dp)
                            .testTag("clear_dob_button")
                    ) {
                        Icon(
                            imageVector = Icons.Filled.Clear,
                            contentDescription = strings.clearBtn,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(strings.clearBtn, maxLines = 1)
                    }

                    Button(
                        onClick = onCalculateClick,
                        shape = RoundedCornerShape(14.dp),
                        modifier = Modifier
                            .weight(0.66f)
                            .height(50.dp)
                            .testTag("calculate_age_button")
                    ) {
                        Icon(
                            imageVector = Icons.Filled.Calculate,
                            contentDescription = strings.calculateAgeBtn,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = strings.calculateAgeBtn,
                            fontWeight = FontWeight.Bold,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun <T> InlineDateDropdownBox(
    label: String,
    selectedText: String,
    options: List<T>,
    optionLabel: (T) -> String,
    onOptionSelected: (T) -> Unit,
    testTag: String,
    modifier: Modifier = Modifier
) {
    var expanded by remember { mutableStateOf(false) }

    Box(modifier = modifier) {
        Surface(
            shape = RoundedCornerShape(14.dp),
            color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.65f),
            border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(14.dp))
                .clickable { expanded = true }
                .testTag(testTag)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 12.dp, vertical = 10.dp)
            ) {
                Text(
                    text = label,
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.primary,
                    fontWeight = FontWeight.SemiBold
                )
                Spacer(modifier = Modifier.height(2.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = selectedText,
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier.weight(1f)
                    )
                    Icon(
                        imageVector = Icons.Filled.ArrowDropDown,
                        contentDescription = label,
                        tint = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }
        }

        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false },
            modifier = Modifier.heightIn(max = 320.dp)
        ) {
            options.forEach { item ->
                DropdownMenuItem(
                    text = {
                        Text(
                            text = optionLabel(item),
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.Medium
                        )
                    },
                    onClick = {
                        expanded = false
                        onOptionSelected(item)
                    }
                )
            }
        }
    }
}

@Composable
private fun QuickInformationCard(
    today: LocalDate,
    calculationResult: AgeCalculationResult?,
    language: AppLanguage,
    strings: LocalizedUiStrings,
    dateFormatter: DateTimeFormatter,
    onOpenCountdown: () -> Unit
) {
    val todayWeekday = remember(today) { AgeCalculationEngine.weekdayLabel(today.dayOfWeek) }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .testTag("quick_information_card"),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.45f)
        ),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Text(
                text = strings.quickInfoTitle,
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )
            Spacer(modifier = Modifier.height(12.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                QuickInfoMetricTile(
                    label = strings.currentDateLabel,
                    value = today.format(dateFormatter),
                    modifier = Modifier.weight(1f)
                )
                QuickInfoMetricTile(
                    label = strings.currentDayLabel,
                    value = todayWeekday.get(language),
                    modifier = Modifier.weight(1f)
                )
            }
            Spacer(modifier = Modifier.height(10.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                QuickInfoMetricTile(
                    label = strings.currentYearLabel,
                    value = "${today.year} (${if (Year.isLeap(today.year.toLong())) "Leap" else "Standard"})",
                    modifier = Modifier.weight(1f)
                )
                QuickInfoMetricTile(
                    label = strings.nextBirthdayCountdownLabel,
                    value = if (calculationResult != null) {
                        "${calculationResult.daysUntilNextBirthday} ${strings.daysLabel}"
                    } else {
                        "—"
                    },
                    onClick = onOpenCountdown,
                    modifier = Modifier.weight(1f)
                )
            }
        }
    }
}

@Composable
private fun QuickInfoMetricTile(
    label: String,
    value: String,
    onClick: (() -> Unit)? = null,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier
            .clip(RoundedCornerShape(14.dp))
            .then(if (onClick != null) Modifier.clickable { onClick() } else Modifier),
        shape = RoundedCornerShape(14.dp),
        color = MaterialTheme.colorScheme.surface
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp, vertical = 10.dp)
        ) {
            Text(
                text = label,
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(modifier = Modifier.height(3.dp))
            Text(
                text = value,
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}

@Composable
private fun QuickToolsGrid(
    strings: LocalizedUiStrings,
    language: AppLanguage,
    onOpenAgeDetails: () -> Unit,
    onOpenZodiac: () -> Unit,
    onOpenBirthdayCountdown: () -> Unit,
    onOpenBirthDayFinder: () -> Unit
) {
    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            QuickToolCard(
                title = strings.toolAgeCalculator,
                subtitle = when (language) {
                    AppLanguage.ENGLISH -> "4 Extensive Tables"
                    AppLanguage.BENGALI -> "৪টি বিস্তারিত টেবিল"
                    AppLanguage.HINDI -> "4 विस्तृत तालिकाएँ"
                },
                icon = Icons.Filled.Analytics,
                testTag = "quick_tool_age_calculator",
                onClick = onOpenAgeDetails,
                modifier = Modifier.weight(1f)
            )
            QuickToolCard(
                title = strings.toolZodiacFinder,
                subtitle = when (language) {
                    AppLanguage.ENGLISH -> "12 Signs & Table"
                    AppLanguage.BENGALI -> "১২টি রাশি ও তুলনা"
                    AppLanguage.HINDI -> "12 राशियाँ और तालिका"
                },
                icon = Icons.Filled.AutoAwesome,
                testTag = "quick_tool_zodiac_finder",
                onClick = onOpenZodiac,
                modifier = Modifier.weight(1f)
            )
        }
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            QuickToolCard(
                title = strings.toolBirthdayCountdown,
                subtitle = when (language) {
                    AppLanguage.ENGLISH -> "Live Progress Ring"
                    AppLanguage.BENGALI -> "লাইভ প্রগ্রেস রিং"
                    AppLanguage.HINDI -> "लाइव प्रोग्रेस रिंग"
                },
                icon = Icons.Filled.Cake,
                testTag = "quick_tool_birthday_countdown",
                onClick = onOpenBirthdayCountdown,
                modifier = Modifier.weight(1f)
            )
            QuickToolCard(
                title = strings.toolBirthDayFinder,
                subtitle = when (language) {
                    AppLanguage.ENGLISH -> "Weekday of Any Date"
                    AppLanguage.BENGALI -> "যেকোনো তারিখের বার"
                    AppLanguage.HINDI -> "किसी भी तिथि का वार"
                },
                icon = Icons.Filled.EventAvailable,
                testTag = "quick_tool_birthday_finder",
                onClick = onOpenBirthDayFinder,
                modifier = Modifier.weight(1f)
            )
        }
    }
}

@Composable
private fun QuickToolCard(
    title: String,
    subtitle: String,
    icon: ImageVector,
    testTag: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    ElevatedCard(
        modifier = modifier
            .clip(RoundedCornerShape(18.dp))
            .clickable { onClick() }
            .testTag(testTag),
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.elevatedCardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Surface(
                shape = RoundedCornerShape(12.dp),
                color = MaterialTheme.colorScheme.primaryContainer,
                modifier = Modifier.size(44.dp)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(
                        imageVector = icon,
                        contentDescription = title,
                        tint = MaterialTheme.colorScheme.primary
                    )
                }
            }
            Spacer(modifier = Modifier.width(12.dp))
            Column {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    text = subtitle,
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }
    }
}

@Composable
private fun RecentCalculationItemCard(
    entry: SavedCalculationEntry,
    strings: LocalizedUiStrings,
    animationsEnabled: Boolean,
    onViewDetails: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .clickable { onViewDetails() }
            .testTag("recent_calc_item_${entry.id}"),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.weight(1f)
            ) {
                AnimatedZodiacEmblem(
                    symbol = entry.zodiacSymbol.ifBlank { "✦" },
                    animationsEnabled = animationsEnabled,
                    badgeSize = 46.dp,
                    symbolFontSize = 22.sp
                )
                Spacer(modifier = Modifier.width(12.dp))
                Column {
                    Text(
                        text = "${entry.profileName} • ${entry.birthDateIso}",
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = "${entry.summaryAgeText} • ${entry.zodiacNameEn}",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
            TextButton(onClick = onViewDetails) {
                Text(strings.viewDetailsBtn)
            }
        }
    }
}

@Composable
private fun BirthDayFinderDialog(
    initialDate: LocalDate,
    language: AppLanguage,
    onSelectForMainCalc: (LocalDate) -> Unit,
    onDismiss: () -> Unit
) {
    var year by remember { mutableIntStateOf(initialDate.year) }
    var month by remember { mutableIntStateOf(initialDate.monthValue) }
    val maxDayInMonth = remember(year, month) { YearMonth.of(year, month).lengthOfMonth() }
    var day by remember { mutableIntStateOf(initialDate.dayOfMonth.coerceAtMost(maxDayInMonth)) }

    val safeDay = day.coerceIn(1, maxDayInMonth)
    val inspectedDate = remember(year, month, safeDay) { LocalDate.of(year, month, safeDay) }
    val weekday = remember(inspectedDate) { AgeCalculationEngine.weekdayLabel(inspectedDate.dayOfWeek) }
    val season = remember(inspectedDate) { AgeCalculationEngine.seasonLabel(inspectedDate.month) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                when (language) {
                    AppLanguage.ENGLISH -> "Birth Day & Calendar Finder"
                    AppLanguage.BENGALI -> "জন্মবার ও ক্যালেন্ডার ফাইন্ডার"
                    AppLanguage.HINDI -> "जन्म वार और कैलेंडर फाइंडर"
                }
            )
        },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Text(
                    text = when (language) {
                        AppLanguage.ENGLISH ->
                            "Instantly find the exact weekday, day of year, and leap status for any date."
                        AppLanguage.BENGALI ->
                            "যেকোনো তারিখের সঠিক বার, বছরের তম দিন এবং লিপ ইয়ার স্ট্যাটাস দেখুন।"
                        AppLanguage.HINDI ->
                            "किसी भी तिथि का सटीक वार, वर्ष का दिन और लीप वर्ष स्थिति तुरंत देखें।"
                    },
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    TextButton(onClick = { year = (year - 1).coerceAtLeast(1900) }) { Text("-1Y") }
                    Text("$year", fontWeight = FontWeight.Bold)
                    TextButton(onClick = { year = (year + 1).coerceAtMost(LocalDate.now().year) }) { Text("+1Y") }

                    TextButton(onClick = { month = if (month == 1) 12 else month - 1 }) { Text("-1M") }
                    Text("M$month", fontWeight = FontWeight.Bold)
                    TextButton(onClick = { month = if (month == 12) 1 else month + 1 }) { Text("+1M") }

                    TextButton(onClick = { day = if (safeDay == 1) maxDayInMonth else safeDay - 1 }) { Text("-1D") }
                    Text("D$safeDay", fontWeight = FontWeight.Bold)
                    TextButton(onClick = { day = if (safeDay == maxDayInMonth) 1 else safeDay + 1 }) { Text("+1D") }
                }

                HorizontalDivider()

                Surface(
                    shape = RoundedCornerShape(14.dp),
                    color = MaterialTheme.colorScheme.primaryContainer,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier.padding(14.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = weekday.get(language),
                            style = MaterialTheme.typography.headlineMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                        Text(
                            text = "$inspectedDate • ${season.get(language)} • Day ${inspectedDate.dayOfYear}",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = { onSelectForMainCalc(inspectedDate) },
                enabled = !inspectedDate.isAfter(LocalDate.now())
            ) {
                Text(
                    when (language) {
                        AppLanguage.ENGLISH -> "Use This Date"
                        AppLanguage.BENGALI -> "বয়স হিসাব করুন"
                        AppLanguage.HINDI -> "इस तिथि को चुनें"
                    }
                )
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(
                    when (language) {
                        AppLanguage.ENGLISH -> "Close"
                        AppLanguage.BENGALI -> "বন্ধ করুন"
                        AppLanguage.HINDI -> "बंद करें"
                    }
                )
            }
        }
    )
}
