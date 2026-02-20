package ru.kima.sonar.ui.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import ru.kima.sonar.feature.authentication.navigation.AuthGraph
import ru.kima.sonar.feature.authentication.navigation.authNavGraph
import ru.kima.sonar.feature.portfolios.navigtion.portfoliosNavGraph
import ru.kima.sonar.feature.securities.navigation.SecuritiesGraph
import ru.kima.sonar.feature.securities.navigation.securitiesNavGraph

@Composable
fun SonarNavHost(
    navController: NavHostController,
    authorised: Boolean,
) {
    NavHost(
        navController = navController,
        startDestination = if (authorised) SecuritiesGraph else AuthGraph,
    ) {
        authNavGraph()
        portfoliosNavGraph()
        securitiesNavGraph()
    }
}