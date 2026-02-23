package ru.kima.sonar.feature.portfolios.navigtion

import androidx.navigation3.runtime.EntryProviderScope
import androidx.navigation3.runtime.NavKey
import ru.kima.sonar.feature.portfolios.ui.list.PortfoliosListScreen

fun EntryProviderScope<NavKey>.portfoliosNavGraph() {
    entry<PortfoliosGraph.PortfoliosList> { PortfoliosListScreen() }
}
