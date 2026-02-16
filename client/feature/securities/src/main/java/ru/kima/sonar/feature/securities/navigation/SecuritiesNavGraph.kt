package ru.kima.sonar.feature.securities.navigation

import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import androidx.navigation.navigation
import ru.kima.sonar.feature.securities.ui.list.SecuritiesLstScreen

fun NavGraphBuilder.securitiesNavGraph() = navigation<SecuritiesGraph>(
    startDestination = SecuritiesGraph.SecuritiesList
) {
    composable<SecuritiesGraph.SecuritiesList> {
        SecuritiesLstScreen()
    }
}