package com.aaltix.lotto.feature.settings.presentation.addedit

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MenuAnchorType
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.aaltix.lotto.core.ui.R
import com.aaltix.lotto.core.ui.adaptive.CenteredContent
import com.aaltix.lotto.core.ui.adaptive.adaptiveHorizontalPadding
import com.aaltix.lotto.core.ui.adaptive.isExpandedTablet
import com.aaltix.lotto.core.ui.components.LoadingIndicator
import com.aaltix.lotto.core.ui.components.LottoCard
import org.koin.androidx.compose.koinViewModel

@Composable
fun AddEditLotteryTypeScreen(
    typeId: String?,
    onNavigateBack: () -> Unit,
    viewModel: AddEditLotteryTypeViewModel = koinViewModel()
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(typeId) {
        typeId?.let { viewModel.processIntent(AddEditLotteryTypeContract.Intent.LoadType(it)) }
    }

    LaunchedEffect(Unit) {
        viewModel.effect.collect { effect ->
            when (effect) {
                is AddEditLotteryTypeContract.Effect.NavigateBack -> onNavigateBack()
                is AddEditLotteryTypeContract.Effect.SaveSuccess -> onNavigateBack()
                is AddEditLotteryTypeContract.Effect.ShowError -> {
                    snackbarHostState.showSnackbar(effect.message)
                }
            }
        }
    }

    AddEditLotteryTypeContent(
        state = state,
        snackbarHostState = snackbarHostState,
        onIntent = viewModel::processIntent
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun AddEditLotteryTypeContent(
    state: AddEditLotteryTypeContract.State,
    snackbarHostState: SnackbarHostState,
    onIntent: (AddEditLotteryTypeContract.Intent) -> Unit
) {
    val title = if (state.isEditMode) {
        stringResource(R.string.edit_lottery_type)
    } else {
        stringResource(R.string.add_lottery_type)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(title) },
                navigationIcon = {
                    IconButton(onClick = { onIntent(AddEditLotteryTypeContract.Intent.NavigateBack) }) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = stringResource(R.string.back)
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    titleContentColor = MaterialTheme.colorScheme.onPrimary,
                    navigationIconContentColor = MaterialTheme.colorScheme.onPrimary
                )
            )
        },
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { paddingValues ->
        val horizontalPadding = adaptiveHorizontalPadding()
        val isExpanded = isExpandedTablet()

        if (state.isLoading) {
            LoadingIndicator(modifier = Modifier.padding(paddingValues))
        } else {
            CenteredContent(maxWidth = 700.dp) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues)
                        .verticalScroll(rememberScrollState())
                        .padding(horizontal = horizontalPadding)
                        .padding(top = 16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    // Name Input
                    OutlinedTextField(
                        value = state.name,
                        onValueChange = { onIntent(AddEditLotteryTypeContract.Intent.UpdateName(it)) },
                        label = { Text(stringResource(R.string.lottery_name)) },
                        isError = state.nameError != null,
                        supportingText = state.nameError?.let { { Text(it) } },
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(
                            capitalization = KeyboardCapitalization.Words
                        ),
                        modifier = Modifier.fillMaxWidth()
                    )

                    if (isExpanded) {
                        // Side-by-side layout for Main and Bonus numbers on expanded tablets
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(16.dp)
                        ) {
                            // Main Numbers Section
                            LottoCard(modifier = Modifier.weight(1f)) {
                                Column(modifier = Modifier.padding(16.dp)) {
                                    Text(
                                        text = stringResource(R.string.main_numbers),
                                        style = MaterialTheme.typography.titleMedium
                                    )
                                    Spacer(modifier = Modifier.height(12.dp))
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.spacedBy(16.dp)
                                    ) {
                                        NumberDropdown(
                                            label = stringResource(R.string.count),
                                            value = state.mainNumberCount,
                                            options = (1..10).toList(),
                                            onValueChange = { onIntent(AddEditLotteryTypeContract.Intent.UpdateMainNumberCount(it)) },
                                            modifier = Modifier.weight(1f)
                                        )
                                        NumberDropdown(
                                            label = stringResource(R.string.max_value),
                                            value = state.mainNumberMax,
                                            options = (state.mainNumberCount..99).toList(),
                                            onValueChange = { onIntent(AddEditLotteryTypeContract.Intent.UpdateMainNumberMax(it)) },
                                            modifier = Modifier.weight(1f)
                                        )
                                    }
                                }
                            }

                            // Bonus Numbers Section
                            LottoCard(modifier = Modifier.weight(1f)) {
                                Column(modifier = Modifier.padding(16.dp)) {
                                    Text(
                                        text = stringResource(R.string.bonus_number),
                                        style = MaterialTheme.typography.titleMedium
                                    )
                                    Spacer(modifier = Modifier.height(12.dp))
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.spacedBy(16.dp)
                                    ) {
                                        NumberDropdown(
                                            label = stringResource(R.string.count),
                                            value = state.bonusNumberCount,
                                            options = (0..3).toList(),
                                            onValueChange = { onIntent(AddEditLotteryTypeContract.Intent.UpdateBonusNumberCount(it)) },
                                            modifier = Modifier.weight(1f)
                                        )
                                        NumberDropdown(
                                            label = stringResource(R.string.max_value),
                                            value = state.bonusNumberMax,
                                            options = if (state.bonusNumberCount > 0) {
                                                (state.bonusNumberCount..99).toList()
                                            } else {
                                                listOf(0)
                                            },
                                            onValueChange = { onIntent(AddEditLotteryTypeContract.Intent.UpdateBonusNumberMax(it)) },
                                            enabled = state.bonusNumberCount > 0,
                                            modifier = Modifier.weight(1f)
                                        )
                                    }
                                }
                            }
                        }
                    } else {
                        // Stacked layout for phones and medium tablets
                        // Main Numbers Section
                        LottoCard {
                            Column(modifier = Modifier.padding(16.dp)) {
                                Text(
                                    text = stringResource(R.string.main_numbers),
                                    style = MaterialTheme.typography.titleMedium
                                )
                                Spacer(modifier = Modifier.height(12.dp))
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                                ) {
                                    NumberDropdown(
                                        label = stringResource(R.string.count),
                                        value = state.mainNumberCount,
                                        options = (1..10).toList(),
                                        onValueChange = { onIntent(AddEditLotteryTypeContract.Intent.UpdateMainNumberCount(it)) },
                                        modifier = Modifier.weight(1f)
                                    )
                                    NumberDropdown(
                                        label = stringResource(R.string.max_value),
                                        value = state.mainNumberMax,
                                        options = (state.mainNumberCount..99).toList(),
                                        onValueChange = { onIntent(AddEditLotteryTypeContract.Intent.UpdateMainNumberMax(it)) },
                                        modifier = Modifier.weight(1f)
                                    )
                                }
                            }
                        }

                        // Bonus Numbers Section
                        LottoCard {
                            Column(modifier = Modifier.padding(16.dp)) {
                                Text(
                                    text = stringResource(R.string.bonus_number),
                                    style = MaterialTheme.typography.titleMedium
                                )
                                Spacer(modifier = Modifier.height(12.dp))
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                                ) {
                                    NumberDropdown(
                                        label = stringResource(R.string.count),
                                        value = state.bonusNumberCount,
                                        options = (0..3).toList(),
                                        onValueChange = { onIntent(AddEditLotteryTypeContract.Intent.UpdateBonusNumberCount(it)) },
                                        modifier = Modifier.weight(1f)
                                    )
                                    NumberDropdown(
                                        label = stringResource(R.string.max_value),
                                        value = state.bonusNumberMax,
                                        options = if (state.bonusNumberCount > 0) {
                                            (state.bonusNumberCount..99).toList()
                                        } else {
                                            listOf(0)
                                        },
                                        onValueChange = { onIntent(AddEditLotteryTypeContract.Intent.UpdateBonusNumberMax(it)) },
                                        enabled = state.bonusNumberCount > 0,
                                        modifier = Modifier.weight(1f)
                                    )
                                }
                            }
                        }
                    }

                    // Preview Section
                    LottoCard {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text(
                                text = stringResource(R.string.preview),
                                style = MaterialTheme.typography.titleMedium
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = state.previewMainNumbers,
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            if (state.hasBonusNumbers) {
                                Text(
                                    text = "+ ${state.previewBonusNumbers}",
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            } else {
                                Text(
                                    text = state.previewBonusNumbers,
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    // Save Button
                    Button(
                        onClick = { onIntent(AddEditLotteryTypeContract.Intent.Save) },
                        enabled = state.isValid && !state.isSaving,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            text = if (state.isSaving) {
                                stringResource(R.string.saving)
                            } else {
                                stringResource(R.string.save)
                            }
                        )
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun NumberDropdown(
    label: String,
    value: Int,
    options: List<Int>,
    onValueChange: (Int) -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true
) {
    var expanded by remember { mutableStateOf(false) }

    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = { if (enabled) expanded = it },
        modifier = modifier
    ) {
        OutlinedTextField(
            value = value.toString(),
            onValueChange = {},
            readOnly = true,
            enabled = enabled,
            label = { Text(label) },
            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
            colors = ExposedDropdownMenuDefaults.outlinedTextFieldColors(),
            modifier = Modifier
                .menuAnchor(MenuAnchorType.PrimaryNotEditable)
                .fillMaxWidth()
        )
        ExposedDropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            options.forEach { option ->
                DropdownMenuItem(
                    text = { Text(option.toString()) },
                    onClick = {
                        onValueChange(option)
                        expanded = false
                    }
                )
            }
        }
    }
}
