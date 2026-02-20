package ru.kima.sonar.feature.portfolios.navigtion

import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import androidx.navigation.navigation
import ru.kima.sonar.feature.portfolios.ui.list.PortfoliosListScreen

fun NavGraphBuilder.portfoliosNavGraph() = navigation<PortfoliosGraph>(
    startDestination = PortfoliosGraph.PortfoliosList
) {
    composable<PortfoliosGraph.PortfoliosList> {
        PortfoliosListScreen()
    }
}
