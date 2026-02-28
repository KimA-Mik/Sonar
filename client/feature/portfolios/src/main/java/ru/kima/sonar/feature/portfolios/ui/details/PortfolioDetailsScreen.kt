package ru.kima.sonar.feature.portfolios.ui.details

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import org.koin.androidx.compose.koinViewModel
import org.koin.core.parameter.parametersOf
import ru.kima.sonar.common.ui.components.AppBar
import ru.kima.sonar.common.ui.util.LocalNavigator
import ru.kima.sonar.common.ui.util.LocalSnackbarHostState
import ru.kima.sonar.common.ui.util.SonarEvent
import ru.kima.sonar.feature.portfolios.R
import ru.kima.sonar.feature.portfolios.navigtion.PortfoliosGraph
import ru.kima.sonar.feature.portfolios.ui.details.event.PortfolioDetailsUiEvent
import ru.kima.sonar.feature.portfolios.ui.details.event.PortfolioDetailsUserEvent
import ru.kima.sonar.feature.portfolios.ui.details.state.PortfolioDetailsState

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

    LaunchedEffect(uiEvent) {
        uiEvent.consume { event ->
            when (event) {
                is PortfolioDetailsUiEvent.OpenAddEntriesScreen ->
                    navigator.navigate(PortfoliosGraph.List.Details.AddEntries(event.portfolioId))
            }
        }
    }

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
                navigateUp = { navigator.goBack() }
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
        Box(
            modifier = Modifier.padding(paddingValues),
            contentAlignment = Alignment.Center
        ) {
            Text("Portfolio details")
        }
    }
}