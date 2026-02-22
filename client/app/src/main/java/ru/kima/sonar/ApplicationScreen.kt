package ru.kima.sonar

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.navigation3.runtime.NavKey
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.ui.NavDisplay
import ru.kima.sonar.common.ui.navigation.Navigator
import ru.kima.sonar.common.ui.navigation.rememberNavigationState
import ru.kima.sonar.common.ui.util.LocalNavigator
import ru.kima.sonar.common.ui.util.LocalSnackbarHostState
import ru.kima.sonar.feature.authentication.navigation.AuthGraph
import ru.kima.sonar.feature.authentication.navigation.authNavGraph
import ru.kima.sonar.feature.portfolios.navigtion.PortfoliosGraph
import ru.kima.sonar.feature.portfolios.navigtion.portfoliosNavGraph
import ru.kima.sonar.feature.securities.navigation.SecuritiesGraph
import ru.kima.sonar.feature.securities.navigation.securitiesNavGraph
import ru.kima.sonar.ui.navigation.NavBarItem


private val TOP_LEVEL_ROUTES = mapOf<NavKey, NavBarItem>(
    SecuritiesGraph.SecuritiesList to NavBarItem(
        icon = R.drawable.security_24px,
        selectedIcon = R.drawable.security_filled_24px,
        description = R.string.root_mame_securities
    ),
    PortfoliosGraph.PortfoliosList to NavBarItem(
        icon = R.drawable.cases_24px,
        selectedIcon = R.drawable.cases_filled_24px,
        description = R.string.root_mame_portfolios
    )
)

@Composable
fun ApplicationScreen(authorised: Boolean) {
    val navigationState = rememberNavigationState(
        startRoute = if (authorised) SecuritiesGraph.SecuritiesList else AuthGraph.Login,
        topLevelRoutes = if (authorised) TOP_LEVEL_ROUTES.keys else remember { setOf(AuthGraph.Login) }
    )
    val snackbarHostState = remember { SnackbarHostState() }
    val navigator = remember(navigationState) { Navigator(navigationState) }
    val entryProvider = entryProvider {
        authNavGraph()
        portfoliosNavGraph()
        securitiesNavGraph()
    }

    CompositionLocalProvider(
        LocalNavigator provides navigator,
        LocalSnackbarHostState provides snackbarHostState
    ) {
        Scaffold(
            bottomBar = {
                if (authorised) {
                    BottomAppBar {
                        TOP_LEVEL_ROUTES.forEach { (route, item) ->
                            val selected = route == navigationState.topLevelRoute
                            NavigationBarItem(
                                icon = {
                                    Icon(
                                        painterResource(if (selected) item.selectedIcon else item.icon),
                                        contentDescription = null
                                    )
                                },
                                label = { Text(stringResource(item.description)) },
                                selected = selected,
                                onClick = { navigator.navigate(route) }
                            )
                        }
                    }
                }
            },
            snackbarHost = { SnackbarHost(snackbarHostState) }
        ) { paddingValues ->
            println(navigator.state.backStacks.keys)
            NavDisplay(
                entries = navigationState.toDecoratedEntries(entryProvider),
                onBack = { navigator.goBack() },
                modifier = Modifier.padding(paddingValues)
            )
        }
    }
}