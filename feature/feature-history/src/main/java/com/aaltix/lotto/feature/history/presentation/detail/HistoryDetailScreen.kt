package com.aaltix.lotto.feature.history.presentation.detail

import android.widget.Toast
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.aaltix.lotto.core.ui.R
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.aaltix.lotto.core.domain.model.GeneratedNumbers
import com.aaltix.lotto.core.domain.model.LotteryTypes
import com.aaltix.lotto.core.ui.components.LoadingIndicator
import com.aaltix.lotto.core.ui.components.LottoBall
import com.aaltix.lotto.core.ui.components.LottoCard
import com.aaltix.lotto.core.ui.theme.LottoTheme
import org.koin.androidx.compose.koinViewModel

@Composable
fun HistoryDetailScreen(
    entryId: String,
    viewModel: HistoryDetailViewModel = koinViewModel(),
    onNavigateBack: () -> Unit = {}
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val context = LocalContext.current

    LaunchedEffect(entryId) {
        viewModel.processIntent(HistoryDetailContract.Intent.LoadEntry(entryId))
    }

    LaunchedEffect(Unit) {
        viewModel.effect.collect { effect ->
            when (effect) {
                is HistoryDetailContract.Effect.NavigateBack -> {
                    onNavigateBack()
                }
                is HistoryDetailContract.Effect.ShowToast -> {
                    val message = when (effect.message) {
                        HistoryDetailContract.ToastMessage.EntryDeleted ->
                            context.getString(R.string.history_entry_deleted)
                        HistoryDetailContract.ToastMessage.DeleteFailed ->
                            context.getString(R.string.delete)
                    }
                    Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
                }
                is HistoryDetailContract.Effect.DeleteSuccessAndNavigateBack -> {
                    // Show toast first, then navigate back
                    val message = when (effect.message) {
                        HistoryDetailContract.ToastMessage.EntryDeleted ->
                            context.getString(R.string.history_entry_deleted)
                        HistoryDetailContract.ToastMessage.DeleteFailed ->
                            context.getString(R.string.delete)
                    }
                    Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
                    onNavigateBack()
                }
            }
        }
    }

    HistoryDetailContent(
        state = state,
        onIntent = viewModel::processIntent
    )
}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
private fun HistoryDetailContent(
    state: HistoryDetailContract.State,
    onIntent: (HistoryDetailContract.Intent) -> Unit
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.details)) },
                navigationIcon = {
                    IconButton(onClick = { onIntent(HistoryDetailContract.Intent.NavigateBack) }) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = stringResource(R.string.back),
                            tint = MaterialTheme.colorScheme.onPrimary
                        )
                    }
                },
                actions = {
                    if (state.entry != null) {
                        IconButton(onClick = { onIntent(HistoryDetailContract.Intent.DeleteEntry) }) {
                            Icon(
                                imageVector = Icons.Default.Delete,
                                contentDescription = stringResource(R.string.delete),
                                tint = MaterialTheme.colorScheme.onPrimary
                            )
                        }
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    titleContentColor = MaterialTheme.colorScheme.onPrimary
                )
            )
        }
    ) { paddingValues ->
        when {
            state.isLoading -> {
                LoadingIndicator(modifier = Modifier.padding(paddingValues))
            }
            state.error != null -> {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues)
                        .padding(horizontal = 16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Text(
                        text = state.error,
                        color = MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.bodyLarge
                    )
                }
            }
            state.entry != null -> {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues)
                        .padding(horizontal = 16.dp)
                        .padding(top = 16.dp)
                ) {
                    LottoCard {
                        Text(
                            text = state.entry.lotteryType.displayName,
                            style = MaterialTheme.typography.headlineSmall,
                            color = MaterialTheme.colorScheme.primary
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        Text(
                            text = state.entry.formattedDate,
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )

                        Spacer(modifier = Modifier.height(24.dp))

                        // Main Numbers
                        Text(
                            text = stringResource(R.string.main_numbers),
                            style = MaterialTheme.typography.titleMedium
                        )

                        Spacer(modifier = Modifier.height(12.dp))

                        FlowRow(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            state.entry.mainNumbers.forEachIndexed { index, number ->
                                LottoBall(
                                    number = number,
                                    index = index,
                                    animate = false,
                                    size = 56.dp
                                )
                            }
                        }

                        // Bonus Number
                        if (state.entry.hasBonusNumbers) {
                            Spacer(modifier = Modifier.height(24.dp))

                            Text(
                                text = stringResource(R.string.bonus_number),
                                style = MaterialTheme.typography.titleMedium
                            )

                            Spacer(modifier = Modifier.height(12.dp))

                            Row(
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                state.entry.bonusNumbers.forEachIndexed { index, number ->
                                    LottoBall(
                                        number = number,
                                        isBonus = true,
                                        index = index,
                                        animate = false,
                                        size = 56.dp
                                    )
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // Lottery Type Info Card
                    LottoCard {
                        Text(
                            text = stringResource(R.string.lottery_type_info),
                            style = MaterialTheme.typography.titleMedium
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        Text(
                            text = state.entry.lotteryType.description,
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun HistoryDetailContentPreview() {
    LottoTheme {
        HistoryDetailContent(
            state = HistoryDetailContract.State(
                entry = GeneratedNumbers(
                    id = "1",
                    lotteryType = LotteryTypes.POWERBALL,
                    mainNumbers = listOf(5, 12, 23, 44, 67),
                    bonusNumbers = listOf(15),
                    timestamp = System.currentTimeMillis()
                )
            ),
            onIntent = {}
        )
    }
}
