package ru.kima.sonar.feature.portfolios.ui.list.state

import androidx.compose.runtime.Immutable
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf
import ru.kima.sonar.feature.portfolios.ui.list.model.DisplayPortfolio

@Immutable
internal data class PortfolioListState(
    val portfolios: ImmutableList<DisplayPortfolio>,
    val isLoading: Boolean
) {
    companion object {
        fun default(
            portfolios: ImmutableList<DisplayPortfolio> = persistentListOf(),
            isLoading: Boolean = false
        ) = PortfolioListState(
            portfolios = portfolios,
            isLoading = isLoading
        )
    }
}