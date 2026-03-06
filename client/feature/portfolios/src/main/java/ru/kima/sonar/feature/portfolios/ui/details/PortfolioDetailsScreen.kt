package ru.kima.sonar.feature.portfolios.ui.details

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.DropdownMenuGroup
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.DropdownMenuPopup
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MenuDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf
import org.koin.androidx.compose.koinViewModel
import org.koin.core.parameter.parametersOf
import ru.kima.sonar.common.ui.components.AppBar
import ru.kima.sonar.common.ui.components.ConditionalPullToRefreshBox
import ru.kima.sonar.common.ui.event.ResultEffect
import ru.kima.sonar.common.ui.event.SonarEvent
import ru.kima.sonar.common.ui.navigation.Navigator
import ru.kima.sonar.common.ui.preview.SonarPreview
import ru.kima.sonar.common.ui.util.CommonDrawables
import ru.kima.sonar.common.ui.util.CommonStrings
import ru.kima.sonar.common.ui.util.LocalNavigator
import ru.kima.sonar.common.ui.util.LocalNumberFormat
import ru.kima.sonar.common.ui.util.LocalSnackbarHostState
import ru.kima.sonar.common.ui.util.formatBigDecimal
import ru.kima.sonar.feature.portfolios.R
import ru.kima.sonar.feature.portfolios.navigtion.PortfoliosGraph
import ru.kima.sonar.feature.portfolios.ui.addentries.event.AddEntriesResultEvent
import ru.kima.sonar.feature.portfolios.ui.details.event.PortfolioDetailsResultEvent
import ru.kima.sonar.feature.portfolios.ui.details.event.PortfolioDetailsUiEvent
import ru.kima.sonar.feature.portfolios.ui.details.event.PortfolioDetailsUserEvent
import ru.kima.sonar.feature.portfolios.ui.details.model.DisplayItemEntry
import ru.kima.sonar.feature.portfolios.ui.details.state.PortfolioDetailsState
import java.math.BigDecimal

@Composable
internal fun PortfolioDetailsScreen(
    portfolioId: Long,
    modifier: Modifier = Modifier
) {
    val viewModel: PortfolioDetailsViewModel = koinViewModel {
        parametersOf(portfolioId)
    }

    val state by viewModel.state.collectAsStateWithLifecycle()
    val uiEvent by viewModel.uiEvents.collectAsStateWithLifecycle()

    PortfolioDetailsScreenContent(
        state = state,
        onEvent = viewModel::onEvent,
        uiEvent = uiEvent,
        modifier = modifier.fillMaxSize()
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun PortfolioDetailsScreenContent(
    state: PortfolioDetailsState,
    onEvent: (PortfolioDetailsUserEvent) -> Unit,
    uiEvent: SonarEvent<PortfolioDetailsUiEvent>,
    modifier: Modifier = Modifier
) {
    val snackbarHostState = LocalSnackbarHostState.current
    val navigator = LocalNavigator.current

    LaunchedEffect(uiEvent) { consumeEvent(uiEvent, navigator) }
    ResultEffect<PortfolioDetailsResultEvent> { consumeResultEvent(it, onEvent) }
    ResultEffect<AddEntriesResultEvent> { consumeAddEntriesResultEvent(it, onEvent) }

    val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior()
    Scaffold(
        modifier = modifier,
        topBar = {
            AppBar(
                titleContent = {
                    Text(
                        text = state.name,
                        overflow = TextOverflow.Ellipsis,
                        maxLines = 1
                    )
                },
                navigateUp = { navigator.goBack() },
                scrollBehavior = scrollBehavior
            )
        },
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) },
        floatingActionButton = {
            AnimatedVisibility(!state.wasError) {
                FloatingActionButton(onClick = { onEvent(PortfolioDetailsUserEvent.AddEntriesButtonClicked) }) {
                    Icon(
                        painter = painterResource(ru.kima.sonar.common.ui.R.drawable.add_24px),
                        contentDescription = stringResource(R.string.content_description_add_securities)
                    )
                }
            }
        }
    ) { paddingValues ->
        PortfolioDetailsScreenBody(
            state = state,
            onEvent = onEvent,
            modifier = Modifier
                .padding(paddingValues)
                .nestedScroll(scrollBehavior.nestedScrollConnection)
        )
    }
}

@Composable
internal fun PortfolioDetailsScreenBody(
    state: PortfolioDetailsState,
    onEvent: (PortfolioDetailsUserEvent) -> Unit,
    modifier: Modifier = Modifier
) = ConditionalPullToRefreshBox(
    isRefreshing = state.isLoading,
    modifier = modifier.fillMaxSize(),
    onRefresh = { onEvent(PortfolioDetailsUserEvent.Refresh) }
) {
    val dropdownMenuItems = remember(onEvent) {
        persistentListOf(
            DropdownMenuIte(
                text = CommonStrings.action_edit,
                onClick = { onEvent(PortfolioDetailsUserEvent.EditEntryButtonClicked(it)) }
            ),
            DropdownMenuIte(
                text = CommonStrings.action_delete,
                onClick = { onEvent(PortfolioDetailsUserEvent.DeleteEntryButtonClicked(it)) }
            )
        )
    }

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        items(state.entries, key = { it.uid }) { entry ->
            EntryItem(
                entry = entry,
                dropdownMenuItems = dropdownMenuItems,
                modifier = Modifier
                    .fillMaxWidth()
                    .animateItem()
            )
        }
    }
}

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
private fun EntryItem(
    entry: DisplayItemEntry,
    dropdownMenuItems: ImmutableList<DropdownMenuIte>,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.animateContentSize(),
        shape = RoundedCornerShape(8.dp)
    ) {
        val numberFormat = LocalNumberFormat.current
        Row(
            modifier = Modifier
                .padding(12.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = entry.name,
                    style = MaterialTheme.typography.titleMedium,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )

                Spacer(modifier = Modifier.width(4.dp))

                Row {
                    Text(
                        text = numberFormat.format(entry.price),
                        style = MaterialTheme.typography.bodyLarge
                    )

                    Spacer(modifier = Modifier.width(8.dp))

                    Text(
                        text = stringResource(
                            R.string.label_entry_low_price,
                            formatBigDecimal(entry.lowPrice)
                        ),
                        style = MaterialTheme.typography.bodySmall
                    )

                    Spacer(modifier = Modifier.width(8.dp))

                    Text(
                        text = stringResource(
                            R.string.label_entry_high_price,
                            formatBigDecimal(entry.highPrice)
                        ),
                        style = MaterialTheme.typography.bodySmall
                    )
                }

                if (entry.note.isNotBlank()) {
                    Text(
                        text = entry.note,
                        modifier = Modifier.clickable {},
                        style = MaterialTheme.typography.bodySmall,
                        maxLines = if (entry.showNote) Int.MAX_VALUE else 2,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }

            // Right column for optional actions / summary
            Box {
                DropdownMenu(
                    entryUid = entry.uid,
                    items = dropdownMenuItems
                )
            }
        }
    }
}

private fun consumeEvent(
    uiEvent: SonarEvent<PortfolioDetailsUiEvent>,
    navigator: Navigator,
) {
    uiEvent.consume { event ->
        when (event) {
            is PortfolioDetailsUiEvent.OpenAddEntriesScreen ->
                navigator.navigate(PortfoliosGraph.List.Details.AddEntries(event.portfolioId))

            PortfolioDetailsUiEvent.OpenDeleteEntryDialog ->
                navigator.navigate(PortfoliosGraph.List.Details.DeleteEntryDialog)

            is PortfolioDetailsUiEvent.OpenEditEntryDialog ->
                navigator.navigate(PortfoliosGraph.List.Details.EditEntryDialog(event.entryId))
        }
    }
}

private fun consumeResultEvent(
    result: PortfolioDetailsResultEvent,
    onEvent: (PortfolioDetailsUserEvent) -> Unit
) {
    when (result) {
        PortfolioDetailsResultEvent.EntryUpdated -> onEvent(PortfolioDetailsUserEvent.Refresh)
    }
}

private fun consumeAddEntriesResultEvent(
    result: AddEntriesResultEvent,
    onEvent: (PortfolioDetailsUserEvent) -> Unit
) {
    when (result) {
        AddEntriesResultEvent.Success -> onEvent(PortfolioDetailsUserEvent.Refresh)
    }
}

@Immutable
private data class DropdownMenuIte(
    val text: Int,
    val onClick: (String) -> Unit
)

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
private fun DropdownMenu(
    entryUid: String,
    items: ImmutableList<DropdownMenuIte>,
    modifier: Modifier = Modifier
) = Box(
    modifier = modifier
) {
    var expanded by remember { mutableStateOf(false) }
    IconButton(onClick = { expanded = true }) {
        Icon(
            painter = painterResource(CommonDrawables.more_vert_24px),
            contentDescription = null
        )
    }

    DropdownMenuPopup(
        expanded = expanded,
        onDismissRequest = { expanded = false }
    ) {
        DropdownMenuGroup(
            shapes = MenuDefaults.groupShape(1, items.size)
        ) {
            items.forEach { item ->
                DropdownMenuItem(
                    text = {
                        Text(stringResource(item.text))
                    },
                    onClick = { item.onClick(entryUid) },
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun EntryItemPreview() = SonarPreview {
    val sample = DisplayItemEntry(
        id = 1L,
        uid = "uid-1",
        name = "Sample Security Name That Might Be Long",
        price = BigDecimal("12345.56"),
        lowPrice = BigDecimal("12000.00"),
        highPrice = BigDecimal("13000.00"),
        note = "This is a sample note for the entry. It may contain small details.",
        showNote = true
    )

    EntryItem(
        entry = sample, modifier = Modifier
            .padding(16.dp)
            .fillMaxWidth(),
        dropdownMenuItems = persistentListOf()
    )
}