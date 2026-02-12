package ru.kima.sonar.ui.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import ru.kima.sonar.feature.authentication.navigation.AuthGraph
import ru.kima.sonar.feature.authentication.navigation.authNavGraph

@Composable
fun SonarNavHost(
    navController: NavHostController
) {
    NavHost(
        navController = navController,
        startDestination = AuthGraph,
    ) {
        authNavGraph()
    }
}