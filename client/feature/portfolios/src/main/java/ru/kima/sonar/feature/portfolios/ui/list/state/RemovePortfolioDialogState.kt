package ru.kima.sonar.feature.portfolios.ui.list.state

import androidx.compose.runtime.Immutable

@Immutable
internal data class RemovePortfolioDialogState(
    val name: String,
    val isLoading: Boolean,
    val waError: Boolean
) {
    companion object {
        fun default(
            name: String = "",
            isLoading: Boolean = false,
            waError: Boolean = false
        ) = RemovePortfolioDialogState(
            name = name,
            isLoading = isLoading,
            waError = waError
        )
    }
}
