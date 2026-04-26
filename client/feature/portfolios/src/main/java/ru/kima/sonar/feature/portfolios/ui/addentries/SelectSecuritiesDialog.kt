package ru.kima.sonar.feature.portfolios.ui.addentries

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.PagerState
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material3.Checkbox
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.PrimaryTabRow
import androidx.compose.material3.Tab
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import kotlinx.collections.immutable.toPersistentList
import kotlinx.coroutines.launch
import org.koin.androidx.compose.koinViewModel
import ru.kima.sonar.common.ui.components.ConditionalPullToRefreshBox
import ru.kima.sonar.common.ui.components.SonarAlertDialog
import ru.kima.sonar.common.ui.components.SonarDialogDefaults
import ru.kima.sonar.common.ui.preview.SonarPreview
import ru.kima.sonar.common.ui.util.CommonDrawables
import ru.kima.sonar.common.ui.util.CommonStrings
import ru.kima.sonar.common.ui.util.LocalNavigator
import ru.kima.sonar.common.ui.util.LocalNumberFormat
import ru.kima.sonar.common.ui.util.clearFocusOnSoftKeyboardHide
import ru.kima.sonar.feature.portfolios.R
import ru.kima.sonar.feature.portfolios.ui.addentries.event.SelectSecuritiesDialogUserEvent
import ru.kima.sonar.feature.portfolios.ui.addentries.model.AddEntriesTabs
import ru.kima.sonar.feature.portfolios.ui.addentries.model.AddableSecurity
import ru.kima.sonar.feature.portfolios.ui.addentries.state.SelectSecuritiesDialogState
import java.math.BigDecimal


@Composable
internal fun SelectSecuritiesDialog(modifier: Modifier = Modifier) {
    val viewModel: AddEntriesViewModel = koinViewModel()
    val state by viewModel.selectDialogState.collectAsStateWithLifecycle()

    SelectSecuritiesDialogContent(
        state = state,
        onEvent = viewModel::onSelectDialogEvent,
        modifier = modifier
    )
}

@Composable
private fun SelectSecuritiesDialogContent(
    state: SelectSecuritiesDialogState,
    onEvent: (SelectSecuritiesDialogUserEvent) -> Unit,
    modifier: Modifier = Modifier
) {
    val navigator = LocalNavigator.current
    SonarAlertDialog(
        confirmButton = {
            TextButton(onClick = {
                onEvent(SelectSecuritiesDialogUserEvent.AcceptClicked)
                navigator.goBack()
            }) {
                Text(stringResource(CommonStrings.action_confirm))
            }
        },
        modifier = modifier,
        dismissButton = {
            TextButton(
                onClick = { navigator.goBack() }
            ) {
                Text(stringResource(CommonStrings.action_cancel))
            }
        },
        text = {
            Column {
                val pagerState =
                    rememberPagerState(state.selectedTabIndex) { AddEntriesTabs.entries.size }
                val coroutineScope = rememberCoroutineScope()

                LaunchedEffect(pagerState) {
                    snapshotFlow { pagerState.currentPage }.collect { page ->
                        onEvent(SelectSecuritiesDialogUserEvent.TabSelected(page))
                    }
                }

                Tabs(
                    pagerState = pagerState,
                    onTabClick = { coroutineScope.launch { pagerState.animateScrollToPage(it) } }
                )

                TabsBody(
                    pagerState = pagerState,
                    state = state,
                    onEvent = onEvent,
                    contentPadding = PaddingValues(top = 8.dp)
                )
            }
        }
    )
}

@Composable
private fun Tabs(
    pagerState: PagerState,
    modifier: Modifier = Modifier,
    onTabClick: (Int) -> Unit
) {
    PrimaryTabRow(
        selectedTabIndex = pagerState.currentPage,
        modifier = modifier,
        containerColor = SonarDialogDefaults.color
    ) {
        AddEntriesTabs.entries.forEachIndexed { index, tab ->
            Tab(
                selected = pagerState.currentPage == index,
                onClick = { onTabClick(index) },
            ) {
                Text(
                    stringResource(tab.titleId),
                    modifier = Modifier.padding(8.dp),
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
private fun TabsBody(
    pagerState: PagerState,
    state: SelectSecuritiesDialogState,
    modifier: Modifier = Modifier,
    onEvent: (SelectSecuritiesDialogUserEvent) -> Unit,
    contentPadding: PaddingValues = PaddingValues.Zero
) = HorizontalPager(
    state = pagerState,
    modifier = modifier
) {
    when (it) {
        0 -> SelectorBody(
            state = state,
            modifier = Modifier
                .fillMaxSize()
                .padding(contentPadding),
            onEvent = onEvent
        )

        1 -> BulkBody(
            state = state,
            modifier = Modifier
                .fillMaxWidth()
                .padding(contentPadding),
            onEvent = onEvent
        )
    }
}

@Composable
private fun SelectorBody(
    state: SelectSecuritiesDialogState,
    onEvent: (SelectSecuritiesDialogUserEvent) -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        OutlinedTextField(
            value = state.query,
            onValueChange = { onEvent(SelectSecuritiesDialogUserEvent.QueryUpdated(it)) },
            modifier = Modifier
                .fillMaxWidth()
                .clearFocusOnSoftKeyboardHide(),
            label = { Text(stringResource(R.string.label_ticker)) },
            trailingIcon = {
                AnimatedVisibility(state.query.isNotEmpty()) {
                    IconButton(onClick = { onEvent(SelectSecuritiesDialogUserEvent.ClearQueryClicked) }) {
                        Icon(
                            painter = painterResource(CommonDrawables.close_small_24px),
                            contentDescription = null
                        )
                    }
                }
            },
            singleLine = true
        )

        ConditionalPullToRefreshBox(
            isRefreshing = state.isLoading,
            onRefresh = { onEvent(SelectSecuritiesDialogUserEvent.RefreshRequest) }
        ) {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.spacedBy(16.dp),
                contentPadding = PaddingValues(top = 16.dp)
            ) {
                items(state.entries, key = { it.uid }) {
                    AddableEntry(
                        it,
                        onEvent = onEvent,
                        modifier = Modifier
                            .fillMaxWidth()
                            .animateItem()
                    )
                }
            }
        }
    }
}

@Composable
private fun BulkBody(
    state: SelectSecuritiesDialogState,
    modifier: Modifier = Modifier,
    onEvent: (SelectSecuritiesDialogUserEvent) -> Unit
) {
    OutlinedTextField(
        value = state.bulkQuery,
        onValueChange = { onEvent(SelectSecuritiesDialogUserEvent.BulkQueryUpdated(it)) },
        modifier = modifier.clearFocusOnSoftKeyboardHide(),
        label = { Text(stringResource(R.string.label_bulk_input)) },
//    placeholder = { Text(stringResource(R.string.label_bulk_input_placeholder)) },
    )
}

@Composable
private fun AddableEntry(
    security: AddableSecurity,
    onEvent: (SelectSecuritiesDialogUserEvent) -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier.height(IntrinsicSize.Min),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Column(
            modifier = Modifier
                .fillMaxHeight()
                .weight(1f),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = security.ticker,
                style = MaterialTheme.typography.headlineSmall
            )
            Text(
                text = security.name,
                style = MaterialTheme.typography.bodyMedium,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }

        Column(
            modifier = Modifier.fillMaxHeight(),
            horizontalAlignment = Alignment.End
        ) {
            Checkbox(
                checked = security.selected,
                onCheckedChange = { onEvent(SelectSecuritiesDialogUserEvent.EntryChecked(security.uid)) }
            )

            Price(security.price)
        }
    }
}

@Composable
private fun Price(
    price: BigDecimal,
    modifier: Modifier = Modifier
) {
    val numberFormat = LocalNumberFormat.current
    Text(
        text = stringResource(
            ru.kima.sonar.common.ui.R.string.price_format,
            numberFormat.format(price)
        ),
        modifier = modifier,
        style = MaterialTheme.typography.labelMedium
    )
}

@Preview
@Composable
private fun SelectSecuritiesDialogPreview() = SonarPreview {
    val entries = buildList {
        add(
            AddableSecurity(
                uid = "SBER",
                ticker = "SBER",
                name = "Сбербанк",
                price = BigDecimal("123.456"),
                selected = false,
                basicAsset = "SBER"
            )
        )
        add(
            AddableSecurity(
                uid = "MOEX",
                ticker = "MOEX",
                name = "Индекс мосбиржи с очень длинным названием",
                price = BigDecimal("3456.789"),
                selected = true,
                basicAsset = "MOEX"
            )
        )
        repeat(15) {
            add(
                AddableSecurity(
                    uid = it.toString(),
                    ticker = "MOEX",
                    name = "Индекс мосбиржи с очень длинным названием",
                    price = BigDecimal("3456.$it"),
                    selected = it % 2 == 1,
                    basicAsset = "MOEX"
                )
            )
        }
    }
    SelectSecuritiesDialogContent(
        state = SelectSecuritiesDialogState.default(
            query = "MOEX",
            entries = entries.toPersistentList(),
        ),
        onEvent = {}
    )
}

@Preview
@Composable
private fun BulkTabPreview() = SonarPreview {
    SelectSecuritiesDialogContent(
        state = SelectSecuritiesDialogState.default(
            selectedTabIndex = AddEntriesTabs.Bulk.ordinal,
            bulkQuery = "MOEX\nSBER\nAAPL\nGOOGL"
        ),
        onEvent = {}
    )
}