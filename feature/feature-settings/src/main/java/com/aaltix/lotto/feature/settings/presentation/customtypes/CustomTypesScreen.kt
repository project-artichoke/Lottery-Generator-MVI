package com.aaltix.lotto.feature.settings.presentation.customtypes

import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SwipeToDismissBox
import androidx.compose.material3.SwipeToDismissBoxValue
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberSwipeToDismissBoxState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.aaltix.lotto.core.domain.model.LotteryType
import com.aaltix.lotto.core.ui.R
import com.aaltix.lotto.core.ui.adaptive.CenteredContent
import com.aaltix.lotto.core.ui.adaptive.adaptiveHorizontalPadding
import com.aaltix.lotto.core.ui.components.LoadingIndicator
import com.aaltix.lotto.core.ui.components.LottoCard
import org.koin.androidx.compose.koinViewModel

@Composable
fun CustomTypesScreen(
    onNavigateBack: () -> Unit,
    onNavigateToAdd: () -> Unit,
    onNavigateToEdit: (String) -> Unit,
    viewModel: CustomTypesViewModel = koinViewModel()
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(Unit) {
        viewModel.effect.collect { effect ->
            when (effect) {
                is CustomTypesContract.Effect.NavigateToAddType -> onNavigateToAdd()
                is CustomTypesContract.Effect.NavigateToEditType -> onNavigateToEdit(effect.typeId)
                is CustomTypesContract.Effect.NavigateBack -> onNavigateBack()
                is CustomTypesContract.Effect.ShowDeleteSuccess -> {
                    snackbarHostState.showSnackbar("${effect.typeName} deleted")
                }
                is CustomTypesContract.Effect.ShowError -> {
                    snackbarHostState.showSnackbar(effect.message)
                }
            }
        }
    }

    CustomTypesContent(
        state = state,
        snackbarHostState = snackbarHostState,
        onIntent = viewModel::processIntent
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun CustomTypesContent(
    state: CustomTypesContract.State,
    snackbarHostState: SnackbarHostState,
    onIntent: (CustomTypesContract.Intent) -> Unit
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.custom_lottery_types)) },
                navigationIcon = {
                    IconButton(onClick = { onIntent(CustomTypesContract.Intent.NavigateBack) }) {
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
        floatingActionButton = {
            FloatingActionButton(
                onClick = { onIntent(CustomTypesContract.Intent.AddCustomType) },
                containerColor = MaterialTheme.colorScheme.primary
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = stringResource(R.string.add_lottery_type)
                )
            }
        },
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { paddingValues ->
        val horizontalPadding = adaptiveHorizontalPadding()

        CenteredContent(maxWidth = 600.dp) {
            if (state.isLoading) {
                LoadingIndicator(modifier = Modifier.padding(paddingValues))
            } else if (state.customTypes.isEmpty()) {
                EmptyState(modifier = Modifier.padding(paddingValues))
            } else {
                CustomTypesList(
                    types = state.customTypes,
                    onEdit = { onIntent(CustomTypesContract.Intent.EditCustomType(it)) },
                    onDelete = { onIntent(CustomTypesContract.Intent.DeleteCustomType(it)) },
                    modifier = Modifier.padding(paddingValues),
                    horizontalPadding = horizontalPadding
                )
            }
        }
    }
}

@Composable
private fun EmptyState(modifier: Modifier = Modifier) {
    Box(
        modifier = modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.padding(32.dp)
        ) {
            Text(
                text = stringResource(R.string.no_custom_types),
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Text(
                text = stringResource(R.string.tap_plus_to_add),
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(top = 8.dp)
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun CustomTypesList(
    types: List<LotteryType>,
    onEdit: (String) -> Unit,
    onDelete: (String) -> Unit,
    modifier: Modifier = Modifier,
    horizontalPadding: androidx.compose.ui.unit.Dp = 16.dp
) {
    LazyColumn(modifier = modifier.fillMaxSize()) {
        item {
            LottoCard(
                modifier = Modifier.padding(horizontal = horizontalPadding, vertical = 16.dp)
            ) {
                Column {
                    types.forEachIndexed { index, type ->
                        SwipeableCustomTypeItem(
                            type = type,
                            onEdit = { onEdit(type.id) },
                            onDelete = { onDelete(type.id) }
                        )
                        if (index < types.lastIndex) {
                            HorizontalDivider()
                        }
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun SwipeableCustomTypeItem(
    type: LotteryType,
    onEdit: () -> Unit,
    onDelete: () -> Unit
) {
    val dismissState = rememberSwipeToDismissBoxState(
        confirmValueChange = { dismissValue ->
            if (dismissValue == SwipeToDismissBoxValue.EndToStart) {
                onDelete()
                true
            } else {
                false
            }
        }
    )

    SwipeToDismissBox(
        state = dismissState,
        backgroundContent = {
            val color by animateColorAsState(
                when (dismissState.targetValue) {
                    SwipeToDismissBoxValue.EndToStart -> MaterialTheme.colorScheme.errorContainer
                    else -> Color.Transparent
                },
                label = "swipe_background"
            )
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(color)
                    .padding(horizontal = 20.dp),
                contentAlignment = Alignment.CenterEnd
            ) {
                Icon(
                    imageVector = Icons.Default.Delete,
                    contentDescription = stringResource(R.string.delete),
                    tint = MaterialTheme.colorScheme.onErrorContainer
                )
            }
        },
        content = {
            ListItem(
                headlineContent = { Text(type.displayName) },
                supportingContent = { Text(type.description) },
                trailingContent = {
                    IconButton(onClick = onEdit) {
                        Icon(
                            imageVector = Icons.Default.Edit,
                            contentDescription = stringResource(R.string.edit),
                            tint = MaterialTheme.colorScheme.primary
                        )
                    }
                },
                modifier = Modifier.background(MaterialTheme.colorScheme.surface)
            )
        },
        enableDismissFromStartToEnd = false,
        enableDismissFromEndToStart = true
    )
}
