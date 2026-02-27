package ru.kima.sonar.feature.portfolios.navigtion

import androidx.compose.ui.window.DialogProperties
import androidx.navigation3.runtime.EntryProviderScope
import androidx.navigation3.runtime.NavKey
import androidx.navigation3.scene.DialogSceneStrategy
import ru.kima.sonar.common.ui.navigation.SharedViewModelStoreNavEntryDecorator
import ru.kima.sonar.common.ui.navigation.toContentKey
import ru.kima.sonar.feature.portfolios.ui.list.CreatePortfolioDialog
import ru.kima.sonar.feature.portfolios.ui.list.PortfoliosListScreen

fun EntryProviderScope<NavKey>.portfoliosNavGraph() {
    entry<PortfoliosGraph.PortfoliosList>(
        clazzContentKey = { key -> key.toContentKey() }
    ) { PortfoliosListScreen() }

    entry<PortfoliosGraph.PortfoliosList.CreatePortfolioDialog>(
        metadata = SharedViewModelStoreNavEntryDecorator.parent(PortfoliosGraph.PortfoliosList.toContentKey())
                + DialogSceneStrategy.dialog(DialogProperties(dismissOnClickOutside = false))
    ) { CreatePortfolioDialog() }
}
