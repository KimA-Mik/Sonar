package ru.kima.sonar

import android.Manifest
import android.os.Build
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.adaptive.ExperimentalMaterial3AdaptiveApi
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalResources
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.core.app.ActivityCompat
import androidx.navigation3.runtime.NavKey
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.scene.DialogSceneStrategy
import androidx.navigation3.ui.NavDisplay
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberPermissionState
import ru.kima.sonar.common.ui.event.LocalResultEventBus
import ru.kima.sonar.common.ui.navigation.NavigationState
import ru.kima.sonar.common.ui.navigation.Navigator
import ru.kima.sonar.common.ui.navigation.rememberNavigationState
import ru.kima.sonar.common.ui.util.LocalNavigator
import ru.kima.sonar.common.ui.util.LocalSnackbarHostState
import ru.kima.sonar.common.ui.util.getActivity
import ru.kima.sonar.feature.authentication.navigation.AuthGraph
import ru.kima.sonar.feature.authentication.navigation.authNavGraph
import ru.kima.sonar.feature.notifications.navigation.NotificationsGraph
import ru.kima.sonar.feature.notifications.navigation.notificationsNavGraph
import ru.kima.sonar.feature.notifications.ui.requestpermissiondialog.showPermissionSnackbar
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
    PortfoliosGraph.List to NavBarItem(
        icon = R.drawable.cases_24px,
        selectedIcon = R.drawable.cases_filled_24px,
        description = R.string.root_mame_portfolios
    )
)

@OptIn(ExperimentalMaterial3AdaptiveApi::class)
@Composable
fun ApplicationScreen(authorised: Boolean) {
    val navigationState = rememberNavigationState(
        startRoute = if (authorised) SecuritiesGraph.SecuritiesList else AuthGraph.Login,
        topLevelRoutes = if (authorised) TOP_LEVEL_ROUTES.keys else remember { setOf(AuthGraph.Login) }
    )
    val snackbarHostState = remember { SnackbarHostState() }
    val navigator = remember(navigationState) { Navigator(navigationState) }
    val bottomBar = @Composable {
        SonarBottomBar(navigationState, navigator)
    }
    val entryProvider = entryProvider {
        authNavGraph()
        portfoliosNavGraph(bottomBar = bottomBar)
        securitiesNavGraph(bottomBar = bottomBar)
        notificationsNavGraph()
    }

    CompositionLocalProvider(
        LocalNavigator provides navigator,
        LocalSnackbarHostState provides snackbarHostState,
        LocalResultEventBus provides SonarApplication.resultEventBus
    ) {
        AskNotificationPermission(authorised)
        val sceneStrategy = remember { DialogSceneStrategy<NavKey>() }
        NavDisplay(
            entries = navigationState.toDecoratedEntries(entryProvider),
            onBack = { navigator.goBack() },
            sceneStrategy = sceneStrategy
        )
    }
}

@Composable
fun SonarBottomBar(
    navigationState: NavigationState,
    navigator: Navigator,
    modifier: Modifier = Modifier
) {
    BottomAppBar(
        modifier = modifier
    ) {
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


@OptIn(ExperimentalPermissionsApi::class)
@Composable
private fun AskNotificationPermission(authorised: Boolean) {
    if (!authorised) return
    if (Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU) return
    val coroutineScope = rememberCoroutineScope()
    val snackbarHostState = LocalSnackbarHostState.current
    val resources = LocalResources.current
    val permissionState = rememberPermissionState(Manifest.permission.POST_NOTIFICATIONS) {
        snackbarHostState.showPermissionSnackbar(coroutineScope, it, resources)
    }

    if (permissionState.status.isGranted) return

    val context = LocalContext.current
    val navigator = LocalNavigator.current
    LaunchedEffect(Unit) {
        val activity = context.getActivity()
        if (activity != null && ActivityCompat.shouldShowRequestPermissionRationale(
                activity, Manifest.permission.POST_NOTIFICATIONS
            )
        ) {
            navigator.navigate(NotificationsGraph.RequestNotificationsPermissionDialog)
        } else {
            permissionState.launchPermissionRequest()
        }
    }
}