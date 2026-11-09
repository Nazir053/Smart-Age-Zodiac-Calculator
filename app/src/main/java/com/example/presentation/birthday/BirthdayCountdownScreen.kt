package com.example.presentation.birthday

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Cake
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.domain.model.AgeCalculationResult
import com.example.domain.model.AppSettings
import com.example.presentation.components.AdBanner
import com.example.presentation.components.BirthdayCountdownCard
import com.example.presentation.components.EmptyState
import com.example.presentation.components.InformationTable
import com.example.presentation.components.ResponsiveScreenContainer
import com.example.utils.LocalizedUiStrings
import com.example.utils.ShareAndExportHelper

@Composable
fun BirthdayCountdownScreen(
    result: AgeCalculationResult?,
    settings: AppSettings,
    strings: LocalizedUiStrings,
    bannerAdUnitId: String,
    onBack: () -> Unit,
    onNavigateHome: () -> Unit,
    onShowSnackbar: (String) -> Unit
) {
    BackHandler { onBack() }
    val context = LocalContext.current

    ResponsiveScreenContainer {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 16.dp, vertical = 12.dp)
                .testTag("birthday_countdown_screen")
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(
                    onClick = onBack,
                    modifier = Modifier.testTag("birthday_back_button")
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Back"
                    )
                }
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = strings.birthdayCountdownTitle,
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            if (result == null) {
                EmptyState(
                    icon = Icons.Filled.Cake,
                    title = strings.birthdayCountdownTitle,
                    subtitle = strings.noRecentCalculations,
                    actionLabel = strings.navHome,
                    onActionClick = onNavigateHome
                )
                return@Column
            }

            BirthdayCountdownCard(
                result = result,
                language = settings.language,
                animationsEnabled = settings.animationsEnabled
            )

            Spacer(modifier = Modifier.height(18.dp))

            InformationTable(
                tableId = "countdown_birthday_table",
                title = strings.table2Title,
                subtitle = strings.progressToNextBirthday,
                headerLeft = strings.tableHeaderInformation,
                headerRight = strings.tableHeaderResult,
                rows = result.getBirthdayTable(settings.language),
                initiallyExpanded = true,
                animationsEnabled = settings.animationsEnabled,
                onCopyRow = { row ->
                    ShareAndExportHelper.copyToClipboard(context, row.label, "${row.label}: ${row.value}")
                    onShowSnackbar("${row.label} — ${strings.copiedToast}")
                }
            )

            Spacer(modifier = Modifier.height(16.dp))

            AdBanner(adUnitId = bannerAdUnitId)
        }
    }
}
