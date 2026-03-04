package ru.kima.sonar.feature.portfolios.navigtion

import androidx.compose.runtime.Composable
import androidx.compose.ui.window.DialogProperties
import androidx.navigation3.runtime.EntryProviderScope
import androidx.navigation3.runtime.NavKey
import androidx.navigation3.scene.DialogSceneStrategy
import ru.kima.sonar.common.ui.navigation.SharedViewModelStoreNavEntryDecorator
import ru.kima.sonar.common.ui.navigation.toContentKey
import ru.kima.sonar.feature.portfolios.ui.addentries.AddEntriesScreen
import ru.kima.sonar.feature.portfolios.ui.addentries.SelectSecuritiesDialog
import ru.kima.sonar.feature.portfolios.ui.details.DeleteEntryDialog
import ru.kima.sonar.feature.portfolios.ui.details.PortfolioDetailsScreen
import ru.kima.sonar.feature.portfolios.ui.list.CreatePortfolioDialog
import ru.kima.sonar.feature.portfolios.ui.list.PortfoliosListScreen

fun EntryProviderScope<NavKey>.portfoliosNavGraph(
    bottomBar: @Composable () -> Unit
) {
    entry<PortfoliosGraph.List>(
        clazzContentKey = { key -> key.toContentKey() }
    ) { PortfoliosListScreen(bottomBar = bottomBar) }

    entry<PortfoliosGraph.List.CreatePortfolioDialog>(
        metadata = SharedViewModelStoreNavEntryDecorator.parent(PortfoliosGraph.List.toContentKey())
                + DialogSceneStrategy.dialog(DialogProperties(dismissOnClickOutside = false))
    ) { CreatePortfolioDialog() }

    entry<PortfoliosGraph.List.Details>(
        clazzContentKey = { key -> key.toContentKey() }
    ) { key ->
        PortfolioDetailsScreen(key.portfolioId)
    }

    entry<PortfoliosGraph.List.Details.DeleteEntryDialog>(
        metadata = DialogSceneStrategy.dialog()
                + SharedViewModelStoreNavEntryDecorator.parent(
            PortfoliosGraph.List.Details(0).toContentKey()
        )
    ) {
        DeleteEntryDialog()
    }

    entry<PortfoliosGraph.List.Details.AddEntries>(
        clazzContentKey = { key -> key.toContentKey() }
    ) { key ->
        AddEntriesScreen(key.portfolioId)
    }

    entry<PortfoliosGraph.List.Details.AddEntries.SelectSecuritiesDialog>(
        metadata = DialogSceneStrategy.dialog(DialogProperties(dismissOnClickOutside = false))
                + SharedViewModelStoreNavEntryDecorator.parent(
            PortfoliosGraph.List.Details.AddEntries(0).toContentKey()
        )
    ) {
        SelectSecuritiesDialog()
    }
}
