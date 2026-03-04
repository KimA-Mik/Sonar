package ru.kima.sonar.feature.portfolios.navigtion

import androidx.navigation3.runtime.NavKey
import kotlinx.serialization.Serializable

object PortfoliosGraph {
    @Serializable
    object List : NavKey {
        @Serializable
        object CreatePortfolioDialog : NavKey

        @Serializable
        data class Details(val portfolioId: Long) : NavKey {
            @Serializable
            data object DeleteEntryDialog : NavKey

            @Serializable
            data class EditEntryDialog(val entryId: Long) : NavKey

            @Serializable
            data class AddEntries(val portfolioId: Long) : NavKey {
                @Serializable
                data object SelectSecuritiesDialog : NavKey
            }
        }
    }
}