package ru.kima.sonar.feature.portfolios.ui.addentries

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
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
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import org.koin.androidx.compose.koinViewModel
import org.koin.core.parameter.parametersOf
import ru.kima.sonar.common.ui.components.AppBar
import ru.kima.sonar.common.ui.util.CommonDrawables
import ru.kima.sonar.common.ui.util.LocalNavigator
import ru.kima.sonar.common.ui.util.LocalSnackbarHostState
import ru.kima.sonar.common.ui.util.SonarEvent
import ru.kima.sonar.feature.portfolios.navigtion.PortfoliosGraph
import ru.kima.sonar.feature.portfolios.ui.addentries.event.AddEntriesUiEvent
import ru.kima.sonar.feature.portfolios.ui.addentries.event.AddEntriesUserEvent
import ru.kima.sonar.feature.portfolios.ui.addentries.state.AddEntriesScreenState

@Composable
internal fun AddEntriesScreen(
    portfolioId: Long,
    modifier: Modifier = Modifier
) {
    val viewModel: AddEntriesViewModel = koinViewModel {
        parametersOf(portfolioId)
    }

    val state by viewModel.state.collectAsStateWithLifecycle()
    val uiEvent by viewModel.uiEvents.collectAsStateWithLifecycle()

    AddEntriesScreenContent(
        state = state,
        onEvent = viewModel::onEvent,
        uiEvent = uiEvent,
        modifier = modifier.fillMaxSize()
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun AddEntriesScreenContent(
    state: AddEntriesScreenState,
    onEvent: (AddEntriesUserEvent) -> Unit,
    uiEvent: SonarEvent<AddEntriesUiEvent>,
    modifier: Modifier = Modifier
) {
    val navigator = LocalNavigator.current
    val snackbarHostState = LocalSnackbarHostState.current
    LaunchedEffect(uiEvent) {
        uiEvent.consume { event ->
            when (event) {
                AddEntriesUiEvent.OpenSelectSecuritiesDialog ->
                    navigator.navigate(PortfoliosGraph.List.Details.AddEntries.SelectSecuritiesDialog)
            }
        }
    }

    val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior()
    Scaffold(
        modifier = modifier,
        topBar = {
            AppBar(
                titleContent = {},
                navigateUp = { navigator.goBack() },
                scrollBehavior = scrollBehavior
            )
        },
        snackbarHost = { SnackbarHost(snackbarHostState) },
        floatingActionButton = {
            FloatingActionButton(onClick = { onEvent(AddEntriesUserEvent.OpenSelectSecuritiesDialogClicked) }) {
                Icon(
                    painterResource(CommonDrawables.add_24px),
                    contentDescription = null
                )
            }
        }
    ) { paddingValues ->
        AddEntriesScreenBody(
            state = state,
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .nestedScroll(scrollBehavior.nestedScrollConnection)
        )
    }
}

@Composable
private fun AddEntriesScreenBody(
    state: AddEntriesScreenState,
    modifier: Modifier = Modifier
) {
    LazyColumn(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        items(state.entries, key = { it.uid }) {
            Text(it.ticker)
        }
    }
}