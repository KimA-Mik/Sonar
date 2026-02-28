package ru.kima.sonar.feature.portfolios.ui.details.state

import androidx.compose.runtime.Immutable
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf
import ru.kima.sonar.feature.portfolios.ui.details.model.DisplayItemEntry

@Immutable
internal data class PortfolioDetailsState(
    val name: String,
    val entries: ImmutableList<DisplayItemEntry>,
    val isLoading: Boolean,
    val wasError: Boolean
) {
    companion object {
        fun default(
            name: String = "",
            entries: ImmutableList<DisplayItemEntry> = persistentListOf(),
            isLoading: Boolean = false,
            wasError: Boolean = false
        ) = PortfolioDetailsState(
            name = name,
            entries = entries,
            isLoading = isLoading,
            wasError = wasError
        )
    }
}
