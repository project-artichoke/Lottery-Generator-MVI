package com.aaltix.lotto.feature.settings.presentation

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.clickable
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.aaltix.lotto.core.ui.R
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.aaltix.lotto.core.domain.model.LotteryType
import com.aaltix.lotto.core.domain.model.LotteryTypes
import com.aaltix.lotto.core.ui.components.LoadingIndicator
import com.aaltix.lotto.core.ui.components.LottoCard
import com.aaltix.lotto.core.ui.theme.LottoTheme
import org.koin.androidx.compose.koinViewModel

@Composable
fun SettingsScreen(
    onNavigateToCustomTypes: () -> Unit,
    viewModel: SettingsViewModel = koinViewModel()
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    var selectedLotteryType by remember { mutableStateOf<LotteryType?>(null) }

    LaunchedEffect(Unit) {
        viewModel.effect.collect { effect ->
            when (effect) {
                is SettingsContract.Effect.ShowLotteryTypeInfo -> {
                    selectedLotteryType = effect.type
                }
                is SettingsContract.Effect.NavigateToCustomTypes -> {
                    onNavigateToCustomTypes()
                }
            }
        }
    }

    SettingsContent(
        state = state,
        onIntent = viewModel::processIntent
    )

    // Lottery type info dialog
    selectedLotteryType?.let { type ->
        LotteryTypeInfoDialog(
            type = type,
            onDismiss = { selectedLotteryType = null }
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun SettingsContent(
    state: SettingsContract.State,
    onIntent: (SettingsContract.Intent) -> Unit
) {
    val context = LocalContext.current

    Column(modifier = Modifier.fillMaxSize()) {
        TopAppBar(
            title = { Text(stringResource(R.string.settings)) },
            colors = TopAppBarDefaults.topAppBarColors(
                containerColor = MaterialTheme.colorScheme.primary,
                titleContentColor = MaterialTheme.colorScheme.onPrimary
            )
        )

        if (state.isLoading) {
            LoadingIndicator(modifier = Modifier.fillMaxSize())
        } else {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
            ) {
                // Custom Lottery Types Section
                SectionHeader(title = stringResource(R.string.custom_lottery_types))

                LottoCard(
                    modifier = Modifier.padding(horizontal = 16.dp)
                ) {
                    ListItem(
                        headlineContent = { Text(stringResource(R.string.manage_custom_types)) },
                        supportingContent = {
                            Text(
                                stringResource(
                                    R.string.custom_types_count,
                                    state.customTypesCount
                                )
                            )
                        },
                        trailingContent = {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        },
                        modifier = Modifier.clickable {
                            onIntent(SettingsContract.Intent.NavigateToCustomTypes)
                        }
                    )
                }

                Spacer(modifier = Modifier.height(24.dp))

                // Lottery Types Section
                SectionHeader(title = stringResource(R.string.available_lottery_types))

                LottoCard(
                    modifier = Modifier.padding(horizontal = 16.dp)
                ) {
                    state.lotteryTypes.forEachIndexed { index, type ->
                        LotteryTypeItem(
                            type = type,
                            onInfoClick = { onIntent(SettingsContract.Intent.ViewLotteryTypeInfo(type)) }
                        )
                        if (index < state.lotteryTypes.lastIndex) {
                            HorizontalDivider()
                        }
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                // About Section
                SectionHeader(title = stringResource(R.string.about))

                LottoCard(
                    modifier = Modifier.padding(horizontal = 16.dp)
                ) {
                    ListItem(
                        headlineContent = { Text(stringResource(R.string.version)) },
                        supportingContent = { Text(state.appVersion) }
                    )
                    HorizontalDivider()
                    ListItem(
                        headlineContent = { Text(stringResource(R.string.developer)) },
                        supportingContent = { Text(stringResource(R.string.developer_name)) }
                    )
                    HorizontalDivider()
                    ListItem(
                        headlineContent = { Text(stringResource(R.string.architecture)) },
                        supportingContent = { Text(stringResource(R.string.architecture_description)) }
                    )
                }

                Spacer(modifier = Modifier.height(24.dp))

                // Responsible Gaming Section
                SectionHeader(title = stringResource(R.string.responsible_gaming))

                LottoCard(
                    modifier = Modifier.padding(horizontal = 16.dp)
                ) {
                    ListItem(
                        headlineContent = { Text(stringResource(R.string.responsible_gaming_link)) },
                        supportingContent = {
                            Text(stringResource(R.string.disclaimer_responsible_gaming))
                        },
                        leadingContent = {
                            Icon(
                                imageVector = Icons.Default.Phone,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.primary
                            )
                        },
                        modifier = Modifier.clickable {
                            val intent = Intent(Intent.ACTION_DIAL).apply {
                                data = Uri.parse("tel:1-800-426-2537")
                            }
                            context.startActivity(intent)
                        }
                    )
                }

                Spacer(modifier = Modifier.height(32.dp))
            }
        }
    }
}

@Composable
private fun SectionHeader(title: String) {
    Text(
        text = title,
        style = MaterialTheme.typography.titleSmall,
        color = MaterialTheme.colorScheme.primary,
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp)
    )
}

@Composable
private fun LotteryTypeItem(
    type: LotteryType,
    onInfoClick: () -> Unit
) {
    ListItem(
        headlineContent = { Text(type.displayName) },
        supportingContent = { Text(type.description) },
        trailingContent = {
            IconButton(onClick = onInfoClick) {
                Icon(
                    imageVector = Icons.Default.Info,
                    contentDescription = stringResource(R.string.info),
                    tint = MaterialTheme.colorScheme.primary
                )
            }
        }
    )
}

@Composable
private fun LotteryTypeInfoDialog(
    type: LotteryType,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(type.displayName) },
        text = {
            Column {
                Text(
                    text = stringResource(R.string.main_numbers),
                    style = MaterialTheme.typography.titleSmall
                )
                Text(
                    text = stringResource(R.string.pick_main_numbers, type.mainNumberCount, type.mainNumberMax),
                    style = MaterialTheme.typography.bodyMedium
                )

                if (type.hasBonusNumbers) {
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        text = stringResource(R.string.bonus_number),
                        style = MaterialTheme.typography.titleSmall
                    )
                    Text(
                        text = stringResource(R.string.pick_bonus_number, type.bonusNumberCount, type.bonusNumberMax),
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            }
        },
        confirmButton = {
            TextButton(onClick = onDismiss) {
                Text(stringResource(R.string.close))
            }
        }
    )
}

@Preview(showBackground = true)
@Composable
private fun SettingsContentPreview() {
    LottoTheme {
        SettingsContent(
            state = SettingsContract.State(
                lotteryTypes = LotteryTypes.ALL
            ),
            onIntent = {}
        )
    }
}
