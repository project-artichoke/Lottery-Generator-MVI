package com.aaltix.lotto.feature.generator.presentation

import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LifecycleResumeEffect
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Casino
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.outlined.Casino
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.ListItem
import androidx.compose.material3.ListItemDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.aaltix.lotto.core.ui.R
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.aaltix.lotto.core.domain.model.GeneratedNumbers
import com.aaltix.lotto.core.domain.model.LotteryType
import com.aaltix.lotto.core.domain.model.LotteryTypes
import com.aaltix.lotto.core.ui.animation.ConfettiEffect
import com.aaltix.lotto.core.ui.components.LottoBall
import com.aaltix.lotto.core.ui.components.LottoButton
import com.aaltix.lotto.core.ui.components.LottoCard
import com.aaltix.lotto.core.ui.theme.LottoTheme
import kotlinx.coroutines.launch
import org.koin.androidx.compose.koinViewModel

@Composable
fun GeneratorScreen(
    viewModel: GeneratorViewModel = koinViewModel(),
    onNavigateToHistory: () -> Unit = {}
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val context = LocalContext.current

    // Reload lottery types when screen resumes (to pick up new custom types)
    LifecycleResumeEffect(Unit) {
        viewModel.processIntent(GeneratorContract.Intent.LoadLotteryTypes)
        onPauseOrDispose { }
    }

    LaunchedEffect(Unit) {
        viewModel.effect.collect { effect ->
            when (effect) {
                is GeneratorContract.Effect.ShowToast -> {
                    Toast.makeText(context, effect.message, Toast.LENGTH_SHORT).show()
                }
                is GeneratorContract.Effect.NavigateToHistory -> {
                    onNavigateToHistory()
                }
                is GeneratorContract.Effect.TriggerHapticFeedback -> {
                    // Haptic feedback could be triggered here
                }
            }
        }
    }

    GeneratorContent(
        state = state,
        onIntent = viewModel::processIntent
    )
}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
private fun GeneratorContent(
    state: GeneratorContract.State,
    onIntent: (GeneratorContract.Intent) -> Unit
) {
    var showBottomSheet by remember { mutableStateOf(false) }
    val sheetState = rememberModalBottomSheetState()
    val scope = rememberCoroutineScope()

    Box(modifier = Modifier.fillMaxSize()) {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = { Text(stringResource(R.string.lottery_generator)) },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = MaterialTheme.colorScheme.primary,
                        titleContentColor = MaterialTheme.colorScheme.onPrimary
                    )
                )
            }
        ) { paddingValues ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .padding(horizontal = 16.dp)
                    .padding(top = 16.dp)
                    .verticalScroll(rememberScrollState()),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Lottery Type Selector Card
                LotteryTypeSelectorCard(
                    selectedType = state.selectedLotteryType,
                    onClick = { showBottomSheet = true }
                )

                Spacer(modifier = Modifier.height(24.dp))

                // Generated Numbers Display
                LottoCard {
                    Text(
                        text = state.selectedLotteryType.displayName,
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.primary
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = state.selectedLotteryType.description,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    Spacer(modifier = Modifier.height(24.dp))

                    // Main Numbers
                    if (state.generatedNumbers != null) {
                        NumbersDisplay(
                            numbers = state.generatedNumbers,
                            animate = state.isAnimating,
                            onAnimationComplete = { onIntent(GeneratorContract.Intent.AnimationComplete) }
                        )
                    } else {
                        EmptyNumbersPlaceholder()
                    }
                }

                Spacer(modifier = Modifier.height(32.dp))

                // Generate Button
                LottoButton(
                    text = stringResource(R.string.generate_numbers),
                    onClick = { onIntent(GeneratorContract.Intent.GenerateNumbers) },
                    isLoading = state.isLoading,
                    icon = Icons.Default.Casino
                )

                // Error display
                state.error?.let { error ->
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = error,
                        color = MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            }
        }

        // Confetti overlay
        ConfettiEffect(
            isActive = state.showConfetti,
            onAnimationEnd = { onIntent(GeneratorContract.Intent.ConfettiComplete) }
        )

        // Bottom Sheet for Lottery Type Selection
        if (showBottomSheet) {
            ModalBottomSheet(
                onDismissRequest = { showBottomSheet = false },
                sheetState = sheetState
            ) {
                LotteryTypeBottomSheetContent(
                    availableTypes = state.availableLotteryTypes,
                    selectedType = state.selectedLotteryType,
                    onTypeSelected = { type ->
                        onIntent(GeneratorContract.Intent.SelectLotteryType(type))
                        scope.launch {
                            sheetState.hide()
                            showBottomSheet = false
                        }
                    }
                )
            }
        }
    }
}

@Composable
private fun LotteryTypeSelectorCard(
    selectedType: LotteryType,
    onClick: () -> Unit
) {
    OutlinedCard(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Icon
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.primaryContainer),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = if (selectedType.isCustom) Icons.Default.Star else Icons.Outlined.Casino,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onPrimaryContainer
                )
            }

            Spacer(modifier = Modifier.width(16.dp))

            // Text content
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = stringResource(R.string.lottery_type),
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Text(
                    text = selectedType.displayName,
                    style = MaterialTheme.typography.titleMedium
                )
                Text(
                    text = selectedType.description,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            // Arrow
            Icon(
                imageVector = Icons.Default.KeyboardArrowDown,
                contentDescription = stringResource(R.string.select_lottery_type),
                tint = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
private fun LotteryTypeBottomSheetContent(
    availableTypes: List<LotteryType>,
    selectedType: LotteryType,
    onTypeSelected: (LotteryType) -> Unit
) {
    val predefinedTypes = availableTypes.filter { !it.isCustom }
    val customTypes = availableTypes.filter { it.isCustom }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 32.dp)
    ) {
        // Header
        Text(
            text = stringResource(R.string.select_lottery_type),
            style = MaterialTheme.typography.titleLarge,
            modifier = Modifier.padding(horizontal = 24.dp, vertical = 16.dp)
        )

        HorizontalDivider()

        // Predefined Types Section
        if (predefinedTypes.isNotEmpty()) {
            SectionHeader(title = stringResource(R.string.official_lotteries))

            predefinedTypes.forEach { type ->
                LotteryTypeItem(
                    type = type,
                    isSelected = type.id == selectedType.id,
                    onClick = { onTypeSelected(type) }
                )
            }
        }

        // Custom Types Section
        if (customTypes.isNotEmpty()) {
            Spacer(modifier = Modifier.height(8.dp))
            SectionHeader(title = stringResource(R.string.my_custom_lotteries))

            customTypes.forEach { type ->
                LotteryTypeItem(
                    type = type,
                    isSelected = type.id == selectedType.id,
                    isCustom = true,
                    onClick = { onTypeSelected(type) }
                )
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
            .padding(horizontal = 24.dp, vertical = 12.dp)
    )
}

@Composable
private fun LotteryTypeItem(
    type: LotteryType,
    isSelected: Boolean,
    isCustom: Boolean = false,
    onClick: () -> Unit
) {
    val containerColor = if (isSelected) {
        MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.3f)
    } else {
        MaterialTheme.colorScheme.surface
    }

    ListItem(
        headlineContent = {
            Text(
                text = type.displayName,
                style = MaterialTheme.typography.bodyLarge
            )
        },
        supportingContent = {
            Text(
                text = type.description,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        },
        leadingContent = {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .background(
                        if (isCustom) MaterialTheme.colorScheme.tertiaryContainer
                        else MaterialTheme.colorScheme.secondaryContainer
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = if (isCustom) Icons.Default.Star else Icons.Outlined.Casino,
                    contentDescription = null,
                    tint = if (isCustom) MaterialTheme.colorScheme.onTertiaryContainer
                    else MaterialTheme.colorScheme.onSecondaryContainer,
                    modifier = Modifier.size(20.dp)
                )
            }
        },
        trailingContent = {
            if (isSelected) {
                Icon(
                    imageVector = Icons.Default.Check,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary
                )
            }
        },
        colors = ListItemDefaults.colors(containerColor = containerColor),
        modifier = Modifier.clickable(onClick = onClick)
    )
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun NumbersDisplay(
    numbers: GeneratedNumbers,
    animate: Boolean,
    onAnimationComplete: () -> Unit
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.fillMaxWidth()
    ) {
        Text(
            text = stringResource(R.string.main_numbers),
            style = MaterialTheme.typography.labelMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        Spacer(modifier = Modifier.height(12.dp))

        FlowRow(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center,
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            numbers.mainNumbers.forEachIndexed { index, number ->
                LottoBall(
                    number = number,
                    index = index,
                    animate = animate,
                    modifier = Modifier.padding(horizontal = 4.dp)
                )
            }
        }

        if (numbers.hasBonusNumbers) {
            Spacer(modifier = Modifier.height(24.dp))

            Text(
                text = stringResource(R.string.bonus_number),
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Spacer(modifier = Modifier.height(12.dp))

            Row(
                horizontalArrangement = Arrangement.Center,
                modifier = Modifier.fillMaxWidth()
            ) {
                numbers.bonusNumbers.forEachIndexed { index, number ->
                    LottoBall(
                        number = number,
                        isBonus = true,
                        index = numbers.mainNumbers.size + index,
                        animate = animate,
                        modifier = Modifier.padding(horizontal = 4.dp)
                    )
                }
            }
        }

        // Trigger animation complete after last ball
        LaunchedEffect(animate) {
            if (animate) {
                val totalBalls = numbers.mainNumbers.size + numbers.bonusNumbers.size
                kotlinx.coroutines.delay((totalBalls * 100L) + 500L)
                onAnimationComplete()
            }
        }
    }
}

@Composable
private fun EmptyNumbersPlaceholder() {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .fillMaxWidth()
            .padding(32.dp)
    ) {
        Text(
            text = stringResource(R.string.tap_generate_prompt),
            style = MaterialTheme.typography.bodyLarge,
            textAlign = TextAlign.Center,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun GeneratorContentPreview() {
    LottoTheme {
        GeneratorContent(
            state = GeneratorContract.State(
                selectedLotteryType = LotteryTypes.POWERBALL,
                availableLotteryTypes = LotteryTypes.ALL
            ),
            onIntent = {}
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun GeneratorContentWithNumbersPreview() {
    LottoTheme {
        GeneratorContent(
            state = GeneratorContract.State(
                selectedLotteryType = LotteryTypes.POWERBALL,
                availableLotteryTypes = LotteryTypes.ALL,
                generatedNumbers = GeneratedNumbers(
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
