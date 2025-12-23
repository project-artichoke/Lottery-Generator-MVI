package com.aaltix.lotto.feature.history.presentation.list

import android.widget.Toast
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.DeleteSweep
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.aaltix.lotto.core.domain.model.GeneratedNumbers
import com.aaltix.lotto.core.domain.model.LotteryType
import com.aaltix.lotto.core.domain.model.LotteryTypes
import com.aaltix.lotto.core.ui.components.LoadingIndicator
import com.aaltix.lotto.core.ui.components.LottoBall
import com.aaltix.lotto.core.ui.components.LottoCard
import com.aaltix.lotto.core.ui.theme.LottoTheme
import com.aaltix.lotto.core.ui.theme.SkyBlue
import com.aaltix.lotto.core.ui.R
import org.koin.androidx.compose.koinViewModel

@Composable
fun HistoryListScreen(
    viewModel: HistoryListViewModel = koinViewModel(),
    onNavigateToDetail: (String) -> Unit = {}
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val context = LocalContext.current

    LaunchedEffect(Unit) {
        viewModel.effect.collect { effect ->
            when (effect) {
                is HistoryListContract.Effect.NavigateToDetail -> {
                    onNavigateToDetail(effect.entryId)
                }
                is HistoryListContract.Effect.ShowToast -> {
                    val messageRes = when (effect.message) {
                        HistoryListContract.ToastMessage.EntryDeleted -> R.string.history_entry_deleted
                        HistoryListContract.ToastMessage.HistoryCleared -> R.string.history_cleared
                    }
                    Toast.makeText(context, context.getString(messageRes), Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    HistoryListContent(
        state = state,
        onIntent = viewModel::processIntent
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun HistoryListContent(
    state: HistoryListContract.State,
    onIntent: (HistoryListContract.Intent) -> Unit
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.history)) },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    titleContentColor = MaterialTheme.colorScheme.onPrimary
                ),
                actions = {
                    if (state.historyEntries.isNotEmpty()) {
                        IconButton(
                            onClick = { onIntent(HistoryListContract.Intent.ShowClearAllDialog) }
                        ) {
                            Icon(
                                imageVector = Icons.Default.DeleteSweep,
                                contentDescription = stringResource(R.string.history_clear_all),
                                tint = MaterialTheme.colorScheme.onPrimary
                            )
                        }
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            // Filter chips
            if (state.availableFilters.isNotEmpty()) {
                val filterChipColors = FilterChipDefaults.filterChipColors(
                    selectedContainerColor = SkyBlue,
                    selectedLabelColor = Color.White,
                    selectedLeadingIconColor = Color.White
                )
                LazyRow(
                    modifier = Modifier.fillMaxWidth(),
                    contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    item {
                        FilterChip(
                            selected = state.selectedFilter == null,
                            onClick = { onIntent(HistoryListContract.Intent.FilterByType(null)) },
                            label = { Text(stringResource(R.string.history_filter_all)) },
                            colors = filterChipColors
                        )
                    }
                    items(
                        count = state.availableFilters.size,
                        key = { state.availableFilters[it].id }
                    ) { index ->
                        val type = state.availableFilters[index]
                        FilterChip(
                            selected = state.selectedFilter?.id == type.id,
                            onClick = { onIntent(HistoryListContract.Intent.FilterByType(type)) },
                            label = { Text(type.displayName) },
                            colors = filterChipColors
                        )
                    }
                }
            }

            when {
                state.isLoading -> {
                    LoadingIndicator()
                }
                state.isEmpty -> {
                    EmptyHistoryMessage()
                }
                else -> {
                    LazyColumn(
                        contentPadding = PaddingValues(16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        items(
                            items = state.historyEntries,
                            key = { it.id }
                        ) { entry ->
                            HistoryItem(
                                entry = entry,
                                onClick = { onIntent(HistoryListContract.Intent.ViewDetail(entry.id)) },
                                onDelete = { onIntent(HistoryListContract.Intent.DeleteEntry(entry.id)) }
                            )
                        }
                    }
                }
            }
        }
    }

    // Clear all confirmation dialog
    if (state.showClearConfirmDialog) {
            AlertDialog(
                onDismissRequest = { onIntent(HistoryListContract.Intent.DismissClearAllDialog) },
                title = { Text(stringResource(R.string.history_clear_title)) },
                text = { Text(stringResource(R.string.history_clear_message)) },
                confirmButton = {
                    TextButton(
                        onClick = { onIntent(HistoryListContract.Intent.ConfirmClearAll) }
                    ) {
                        Text(stringResource(R.string.history_clear_confirm))
                    }
                },
                dismissButton = {
                    TextButton(
                        onClick = { onIntent(HistoryListContract.Intent.DismissClearAllDialog) }
                    ) {
                        Text(stringResource(R.string.history_clear_cancel))
                    }
                }
            )
        }
    }

@Composable
private fun HistoryItem(
    entry: GeneratedNumbers,
    onClick: () -> Unit,
    onDelete: () -> Unit
) {
    LottoCard(onClick = onClick) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.Top
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = entry.lotteryType.displayName,
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.primary
                )

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = entry.formattedDate,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                Spacer(modifier = Modifier.height(12.dp))

                // Numbers display
                Row(
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    entry.mainNumbers.forEachIndexed { index, number ->
                        LottoBall(
                            number = number,
                            index = index,
                            animate = false,
                            size = 36.dp
                        )
                    }

                    if (entry.hasBonusNumbers) {
                        Spacer(modifier = Modifier.width(8.dp))
                        entry.bonusNumbers.forEach { number ->
                            LottoBall(
                                number = number,
                                isBonus = true,
                                animate = false,
                                size = 36.dp
                            )
                        }
                    }
                }
            }

            IconButton(onClick = onDelete) {
                Icon(
                    imageVector = Icons.Default.Delete,
                    contentDescription = stringResource(R.string.delete),
                    tint = MaterialTheme.colorScheme.error
                )
            }
        }
    }
}

@Composable
private fun EmptyHistoryMessage() {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.padding(32.dp)
        ) {
            Text(
                text = stringResource(R.string.history_empty_title),
                style = MaterialTheme.typography.titleLarge,
                color = MaterialTheme.colorScheme.onSurface
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = stringResource(R.string.history_empty_message),
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun HistoryListContentPreview() {
    LottoTheme {
        HistoryListContent(
            state = HistoryListContract.State(
                historyEntries = listOf(
                    GeneratedNumbers(
                        id = "1",
                        lotteryType = LotteryTypes.POWERBALL,
                        mainNumbers = listOf(5, 12, 23, 44, 67),
                        bonusNumbers = listOf(15),
                        timestamp = System.currentTimeMillis()
                    ),
                    GeneratedNumbers(
                        id = "2",
                        lotteryType = LotteryTypes.MEGA_MILLIONS,
                        mainNumbers = listOf(3, 17, 28, 45, 61),
                        bonusNumbers = listOf(8),
                        timestamp = System.currentTimeMillis() - 3600000
                    )
                ),
                availableFilters = LotteryTypes.ALL
            ),
            onIntent = {}
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun HistoryListEmptyPreview() {
    LottoTheme {
        HistoryListContent(
            state = HistoryListContract.State(
                isEmpty = true,
                availableFilters = LotteryTypes.ALL
            ),
            onIntent = {}
        )
    }
}
