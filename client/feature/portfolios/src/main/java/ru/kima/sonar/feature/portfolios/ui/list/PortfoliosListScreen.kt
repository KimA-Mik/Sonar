package ru.kima.sonar.feature.portfolios.ui.list

import android.content.res.Resources
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalResources
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf
import org.koin.androidx.compose.koinViewModel
import ru.kima.sonar.common.ui.components.AppBar
import ru.kima.sonar.common.ui.components.ConditionalPullToRefreshBox
import ru.kima.sonar.common.ui.event.SonarEvent
import ru.kima.sonar.common.ui.navigation.Navigator
import ru.kima.sonar.common.ui.preview.SonarPreview
import ru.kima.sonar.common.ui.util.LocalNavigator
import ru.kima.sonar.common.ui.util.LocalSnackbarHostState
import ru.kima.sonar.feature.portfolios.R
import ru.kima.sonar.feature.portfolios.navigtion.PortfoliosGraph
import ru.kima.sonar.feature.portfolios.ui.list.event.PortfolioListEvent
import ru.kima.sonar.feature.portfolios.ui.list.event.PortfolioListUiEvent
import ru.kima.sonar.feature.portfolios.ui.list.model.DisplayPortfolio
import ru.kima.sonar.feature.portfolios.ui.list.state.PortfolioListState


private const val TAG = "PortfoliosListScreen"

@Composable
internal fun PortfoliosListScreen(
    bottomBar: @Composable () -> Unit
) {
    val viewModel: PortfoliosListViewModel = koinViewModel()

    val state by viewModel.state.collectAsStateWithLifecycle()
    val uiEvent by viewModel.uiEvents.collectAsStateWithLifecycle()
    PortfoliosListScreenContent(
        state = state,
        uiEvent = uiEvent,
        onEvent = viewModel::onEvent,
        modifier = Modifier.fillMaxSize(),
        bottomBar = bottomBar
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun PortfoliosListScreenContent(
    state: PortfolioListState,
    uiEvent: SonarEvent<PortfolioListUiEvent>,
    onEvent: (PortfolioListEvent) -> Unit,
    modifier: Modifier = Modifier,
    bottomBar: @Composable () -> Unit = {}
) {
    val resources = LocalResources.current
    val navigator = LocalNavigator.current
    val snackbarHostState = LocalSnackbarHostState.current

    LaunchedEffect(uiEvent) {
        collectUiEvents(
            uiEvent = uiEvent,
            localResources = resources,
            navigator = navigator
        )
    }

    val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior()
    Scaffold(
        modifier = modifier,
        topBar = {
            AppBar(
                titleContent = {
                    Text(stringResource(R.string.graph_name_portfolios))
                },
                scrollBehavior = scrollBehavior
            )
        },
        bottomBar = bottomBar,
        snackbarHost = { SnackbarHost(snackbarHostState) },
        floatingActionButton = {
            FloatingActionButton(onClick = { onEvent(PortfolioListEvent.CreatePortfolioClicked) }) {
                Icon(
                    painter = painterResource(ru.kima.sonar.common.ui.R.drawable.add_24px),
                    contentDescription = stringResource(R.string.action_create_portfolio_content_description)
                )
            }
        }
    ) { paddingValues ->
        PortfoliosListScreenBody(
            portfolios = state.portfolios,
            isLoading = state.isLoading,
            onEvent = onEvent,
            modifier = Modifier
                .padding(paddingValues)
                .fillMaxSize()
                .nestedScroll(scrollBehavior.nestedScrollConnection)
        )
    }
}

private fun collectUiEvents(
    uiEvent: SonarEvent<PortfolioListUiEvent>,
    localResources: Resources,
    navigator: Navigator
) {
    uiEvent.consume { event ->
        when (event) {
            is PortfolioListUiEvent.OpenCreatePortfolioDialog -> navigator.navigate(PortfoliosGraph.List.CreatePortfolioDialog)
            PortfolioListUiEvent.DismissCreatePortfolioDialog -> navigator.goBack()
            is PortfolioListUiEvent.NavigateToPortfolioDetails -> navigator.navigate(
                PortfoliosGraph.List.Details(event.portfolioId)
            )
        }
    }
}

@Composable
private fun PortfoliosListScreenBody(
    portfolios: ImmutableList<DisplayPortfolio>,
    isLoading: Boolean,
    onEvent: (PortfolioListEvent) -> Unit,
    modifier: Modifier = Modifier
) {
    ConditionalPullToRefreshBox(
        isRefreshing = isLoading,
        onRefresh = { onEvent(PortfolioListEvent.Refresh) },
        modifier = modifier
    ) {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(start = 8.dp, end = 8.dp, bottom = 32.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            items(portfolios, key = { it.id }) {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    onClick = { onEvent(PortfolioListEvent.PortfolioClicked(it.id)) }
                ) {
                    Text(
                        it.name,
                        modifier = Modifier.padding(16.dp)
                    )
                }
            }
        }
    }
}


@Preview
@Composable
private fun PortfolioListPreview() = SonarPreview {
    PortfoliosListScreenContent(
        state = PortfolioListState.default(
            portfolios = persistentListOf(
                DisplayPortfolio(1, "Портфель 1"),
                DisplayPortfolio(2, "Портфель 2"),
                DisplayPortfolio(3, "Портфель 3")
            )
        ),
        uiEvent = SonarEvent(),
        onEvent = {},
        modifier = Modifier.fillMaxSize()
    )
}