package com.example.presentation.history

import androidx.compose.foundation.BorderStroke
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
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.DeleteSweep
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.PushPin
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.domain.model.AppLanguage
import com.example.domain.model.AppSettings
import com.example.domain.model.HistorySortOrder
import com.example.domain.model.SavedCalculationEntry
import com.example.presentation.components.AdBanner
import com.example.presentation.components.AnimatedZodiacEmblem
import com.example.presentation.components.EmptyState
import com.example.presentation.components.ResponsiveScreenContainer
import com.example.utils.LocalizedUiStrings
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun HistoryScreen(
    entries: List<SavedCalculationEntry>,
    searchQuery: String,
    sortOrder: HistorySortOrder,
    settings: AppSettings,
    strings: LocalizedUiStrings,
    bannerAdUnitId: String,
    onSearchQueryChange: (String) -> Unit,
    onSortOrderChange: (HistorySortOrder) -> Unit,
    onOpenEntry: (SavedCalculationEntry) -> Unit,
    onDeleteEntry: (String) -> Unit,
    onClearAllHistory: () -> Unit,
    onNavigateHome: () -> Unit
) {
    var showClearConfirmDialog by remember { mutableStateOf(false) }
    val lang = settings.language
    val timestampFormat = remember { SimpleDateFormat("dd MMM yyyy, HH:mm", Locale.ENGLISH) }

    if (showClearConfirmDialog) {
        AlertDialog(
            onDismissRequest = { showClearConfirmDialog = false },
            title = { Text(strings.clearAllHistoryBtn) },
            text = {
                Text(
                    when (lang) {
                        AppLanguage.ENGLISH ->
                            "Are you sure you want to permanently delete all locally stored calculation history and saved profiles?"
                        AppLanguage.BENGALI ->
                            "আপনি কি নিশ্চিত যে আপনি স্থানীয়ভাবে সংরক্ষিত সমস্ত হিসাব এবং প্রোফাইল মুছে ফেলতে চান?"
                        AppLanguage.HINDI ->
                            "क्या आप वाकई स्थानीय रूप से सहेजे गए सभी गणना इतिहास और प्रोफ़ाइल को स्थायी रूप से हटाना चाहते हैं?"
                    }
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        onClearAllHistory()
                        showClearConfirmDialog = false
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error),
                    modifier = Modifier.testTag("confirm_clear_history_btn")
                ) {
                    Text(
                        when (lang) {
                            AppLanguage.ENGLISH -> "Delete All"
                            AppLanguage.BENGALI -> "সব মুছুন"
                            AppLanguage.HINDI -> "सभी हटाएँ"
                        }
                    )
                }
            },
            dismissButton = {
                TextButton(onClick = { showClearConfirmDialog = false }) {
                    Text(
                        when (lang) {
                            AppLanguage.ENGLISH -> "Cancel"
                            AppLanguage.BENGALI -> "বাতিল"
                            AppLanguage.HINDI -> "रद्द करें"
                        }
                    )
                }
            }
        )
    }

    ResponsiveScreenContainer {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 16.dp, vertical = 12.dp)
                .testTag("history_screen")
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = strings.historyTitle,
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface,
                    modifier = Modifier.weight(1f)
                )
                if (entries.isNotEmpty()) {
                    OutlinedButton(
                        onClick = { showClearConfirmDialog = true },
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.testTag("clear_all_history_button")
                    ) {
                        Icon(
                            imageVector = Icons.Filled.DeleteSweep,
                            contentDescription = strings.clearAllHistoryBtn,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = when (lang) {
                                AppLanguage.ENGLISH -> "Clear All"
                                AppLanguage.BENGALI -> "সব মুছুন"
                                AppLanguage.HINDI -> "सभी मिटाएँ"
                            },
                            style = MaterialTheme.typography.labelMedium
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            OutlinedTextField(
                value = searchQuery,
                onValueChange = onSearchQueryChange,
                placeholder = { Text(strings.searchHistoryPlaceholder) },
                leadingIcon = {
                    Icon(imageVector = Icons.Filled.Search, contentDescription = "Search History")
                },
                trailingIcon = {
                    if (searchQuery.isNotBlank()) {
                        IconButton(onClick = { onSearchQueryChange("") }) {
                            Icon(imageVector = Icons.Filled.Clear, contentDescription = "Clear Search")
                        }
                    }
                },
                singleLine = true,
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("history_search_input")
            )

            Spacer(modifier = Modifier.height(10.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                FilterChip(
                    selected = sortOrder == HistorySortOrder.NEWEST_FIRST,
                    onClick = { onSortOrderChange(HistorySortOrder.NEWEST_FIRST) },
                    label = { Text(strings.sortNewest) },
                    modifier = Modifier.testTag("sort_newest_chip")
                )
                FilterChip(
                    selected = sortOrder == HistorySortOrder.OLDEST_FIRST,
                    onClick = { onSortOrderChange(HistorySortOrder.OLDEST_FIRST) },
                    label = { Text(strings.sortOldest) },
                    modifier = Modifier.testTag("sort_oldest_chip")
                )
            }

            Spacer(modifier = Modifier.height(14.dp))

            if (entries.isEmpty()) {
                EmptyState(
                    icon = Icons.Filled.History,
                    title = strings.emptyHistoryTitle,
                    subtitle = strings.emptyHistorySubtitle,
                    actionLabel = strings.navHome,
                    onActionClick = onNavigateHome
                )
            } else {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    entries.forEach { item ->
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(18.dp))
                                .clickable { onOpenEntry(item) }
                                .testTag("history_card_${item.id}"),
                            shape = RoundedCornerShape(18.dp),
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                            border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(16.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    modifier = Modifier.weight(1f)
                                ) {
                                    AnimatedZodiacEmblem(
                                        symbol = item.zodiacSymbol.ifBlank { "✦" },
                                        animationsEnabled = settings.animationsEnabled,
                                        badgeSize = 52.dp,
                                        symbolFontSize = 26.sp
                                    )
                                    Spacer(modifier = Modifier.width(12.dp))
                                    Column {
                                        Row(verticalAlignment = Alignment.CenterVertically) {
                                            Text(
                                                text = item.profileName,
                                                style = MaterialTheme.typography.titleMedium,
                                                fontWeight = FontWeight.Bold,
                                                color = MaterialTheme.colorScheme.onSurface
                                            )
                                            if (item.isPinnedProfile) {
                                                Spacer(modifier = Modifier.width(6.dp))
                                                Icon(
                                                    imageVector = Icons.Filled.PushPin,
                                                    contentDescription = "Pinned Profile",
                                                    tint = MaterialTheme.colorScheme.primary,
                                                    modifier = Modifier.size(14.dp)
                                                )
                                            }
                                        }
                                        Text(
                                            text = "DOB: ${item.birthDateIso}  •  ${item.zodiacNameEn}",
                                            style = MaterialTheme.typography.bodySmall,
                                            fontWeight = FontWeight.SemiBold,
                                            color = MaterialTheme.colorScheme.primary
                                        )
                                        Text(
                                            text = item.summaryAgeText,
                                            style = MaterialTheme.typography.bodySmall,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant
                                        )
                                        if (item.calculatedAtMillis > 0L) {
                                            Text(
                                                text = timestampFormat.format(Date(item.calculatedAtMillis)),
                                                style = MaterialTheme.typography.labelSmall,
                                                color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f)
                                            )
                                        }
                                    }
                                }

                                IconButton(
                                    onClick = { onDeleteEntry(item.id) },
                                    modifier = Modifier.testTag("delete_history_${item.id}")
                                ) {
                                    Icon(
                                        imageVector = Icons.Filled.Delete,
                                        contentDescription = "Delete entry",
                                        tint = MaterialTheme.colorScheme.error
                                    )
                                }
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            AdBanner(adUnitId = bannerAdUnitId)
        }
    }
}
