package ru.kima.sonar.feature.securities.ui.list

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.PagerState
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.LoadingIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.PrimaryTabRow
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Tab
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import kotlinx.coroutines.launch
import org.koin.androidx.compose.koinViewModel
import ru.kima.sonar.common.ui.preview.SonarPreview
import ru.kima.sonar.common.ui.util.LocalNumberFormat
import ru.kima.sonar.feature.securities.R
import ru.kima.sonar.feature.securities.ui.list.model.DisplayListItemShare
import java.math.BigDecimal

@Composable
fun SecuritiesLstScreen() {
    val viewModel: SecuritiesListViewModel = koinViewModel()
    val state by viewModel.state.collectAsStateWithLifecycle()
    val onEvent = remember { { event: SecuritiesListEvent -> viewModel.onEvent(event) } }
    SecuritiesListScreenContent(
        state,
        onEvent
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun SecuritiesListScreenContent(
    state: SecuritiesListState,
    onEvent: (SecuritiesListEvent) -> Unit
) {
//    DisposableEffect(Unit) {
//        onDispose {
//
//        }
//    }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.top_bar_title_securities_list)) }
            )
        },
    ) { paddingValues ->
        SecuritiesListScreenBody(
            state = state,
            modifier = Modifier.padding(paddingValues),
            onEvent = onEvent
        )
//        Box(
//            modifier = Modifier
//                .fillMaxSize()
//                .padding(paddingValues),
//            contentAlignment = Alignment.Center
//        ) {
//            LazyColumn(
//                modifier = Modifier.fillMaxSize(),
//                verticalArrangement = Arrangement.spacedBy(8.dp)
//            ) {
//                items(state.shares, key = { it.uid }) {
//                    ListItemShare(
//                        ticker = it.ticker,
//                        name = it.name,
//                        price = it.price,
//                        modifier = Modifier
//                            .fillMaxWidth()
//                            .padding(horizontal = 8.dp)
//                    )
//                }
//            }
//        }
    }
}

private val tabs = listOf(
    R.string.tab_name_shares,
    R.string.tab_name_futures,
)

@Composable
private fun SecuritiesListScreenBody(
    state: SecuritiesListState,
    modifier: Modifier = Modifier,
    onEvent: (SecuritiesListEvent) -> Unit
) = Column(modifier = modifier) {
    var selectedIndex by rememberSaveable { mutableIntStateOf(0) }
    val pagerState = rememberPagerState(selectedIndex) { tabs.size }
    val coroutineScope = rememberCoroutineScope()
    Tabs(
        pagerState,
        onTabClick = { index ->
            selectedIndex = index
            coroutineScope.launch { pagerState.animateScrollToPage(index) }
        }
    )

    TabsBody(
        pagerState = pagerState,
        state = state,
        onEvent = onEvent
    )
}

@Composable
private fun Tabs(
    pagerState: PagerState,
    modifier: Modifier = Modifier,
    onTabClick: (Int) -> Unit
) {
    PrimaryTabRow(selectedTabIndex = pagerState.currentPage, modifier = modifier) {
        tabs.forEachIndexed { index, stringId ->
            Tab(selected = pagerState.currentPage == index, onClick = { onTabClick(index) }) {
                Text(
                    stringResource(stringId),
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
fun TabsBody(
    pagerState: PagerState,
    state: SecuritiesListState,
    modifier: Modifier = Modifier,
    onEvent: (SecuritiesListEvent) -> Unit
) = Box(modifier = modifier) {
    HorizontalPager(state = pagerState) {
        when (it) {
            0 -> SharesPage(
                shares = state.shares,
                modifier = Modifier.fillMaxSize(),
                onEvent = onEvent
            )

            1 -> Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Text(stringResource(R.string.tab_name_futures))
            }
        }
    }

    AnimatedVisibility(
        visible = state.sharesListState == SecuritiesListState.SecurityListState.Loading,
        modifier = Modifier.align(Alignment.TopCenter),
        enter = slideInVertically(initialOffsetY = { -it }),
        exit = slideOutVertically(targetOffsetY = { -it })
    ) {
        LoadingIndicator(modifier = Modifier.padding(16.dp))
    }
}

@Composable
fun SharesPage(
    shares: List<DisplayListItemShare>,
    modifier: Modifier = Modifier,
    onEvent: (SecuritiesListEvent) -> Unit
) {
    DisposableEffect(Unit) {
        onEvent(SecuritiesListEvent.OnSharesListOpen)
        onDispose { onEvent(SecuritiesListEvent.OnSharesListDispose) }
    }

    LazyColumn(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        items(shares, key = { it.uid }) {
            ListItemShare(
                ticker = it.ticker,
                name = it.name,
                price = it.price,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 8.dp)
                    .animateItem()
            )
        }
    }
}


@Composable
fun ListItemShare(
    ticker: String,
    name: String,
    price: BigDecimal,
    modifier: Modifier = Modifier
) = Row(
    modifier = modifier,
    horizontalArrangement = Arrangement.SpaceBetween,
    verticalAlignment = Alignment.CenterVertically
) {
    Column {
        Text(ticker, style = MaterialTheme.typography.headlineSmall)
        Text(name, style = MaterialTheme.typography.bodyMedium)
    }

    PriceFormat(price)
}

@Composable
fun PriceFormat(price: BigDecimal, modifier: Modifier = Modifier) {
    val numberFormat = LocalNumberFormat.current
    Text(
        "${numberFormat.format(price)} RUB",
        modifier = modifier,
        style = MaterialTheme.typography.bodyLarge
    )
}

@Preview(name = "Preview Default")
@Preview(name = "Preview Russian", locale = "ru")
@Composable
private fun SecuritiesListScreenPreview() = SonarPreview {
    SecuritiesListScreenContent(
        state = SecuritiesListState.default(
            shares = listOf(
                DisplayListItemShare(
                    uid = "1",
                    ticker = "AAPL",
                    name = "Apple Inc.",
                    price = BigDecimal("150.0"),
                ),
                DisplayListItemShare(
                    uid = "2",
                    ticker = "GOOGL",
                    name = "Alphabet Inc.",
                    price = BigDecimal("2800.0"),
                )
            )
        ),
        onEvent = {}
    )
}