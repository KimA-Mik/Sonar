package ru.kima.sonar.feature.authentication.navigation

import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import androidx.navigation.navigation
import ru.kima.sonar.feature.authentication.ui.authscreen.AuthScreen

fun NavGraphBuilder.authNavGraph() = navigation<AuthGraph>(
    startDestination = AuthGraph.Login
) {
    composable<AuthGraph.Login> { AuthScreen() }
}