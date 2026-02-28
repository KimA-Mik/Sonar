package ru.kima.sonar.feature.portfolios.navigtion

import androidx.compose.ui.window.DialogProperties
import androidx.navigation3.runtime.EntryProviderScope
import androidx.navigation3.runtime.NavKey
import androidx.navigation3.scene.DialogSceneStrategy
import ru.kima.sonar.common.ui.navigation.SharedViewModelStoreNavEntryDecorator
import ru.kima.sonar.common.ui.navigation.toContentKey
import ru.kima.sonar.feature.portfolios.ui.details.PortfolioDetailsScreen
import ru.kima.sonar.feature.portfolios.ui.list.CreatePortfolioDialog
import ru.kima.sonar.feature.portfolios.ui.list.PortfoliosListScreen

fun EntryProviderScope<NavKey>.portfoliosNavGraph() {
    entry<PortfoliosGraph.List>(
        clazzContentKey = { key -> key.toContentKey() }
    ) { PortfoliosListScreen() }

    entry<PortfoliosGraph.List.CreatePortfolioDialog>(
        metadata = SharedViewModelStoreNavEntryDecorator.parent(PortfoliosGraph.List.toContentKey())
                + DialogSceneStrategy.dialog(DialogProperties(dismissOnClickOutside = false))
    ) { CreatePortfolioDialog() }

    entry<PortfoliosGraph.List.Details>(
        clazzContentKey = { key -> key.toContentKey() }
    ) { key ->
        PortfolioDetailsScreen(key.portfolioId)
    }
}
