package ru.kima.sonar.feature.portfolios.ui.list.state

import androidx.compose.runtime.Immutable

@Immutable
data class CreatePortfolioDialogState(
    val newName: String,
    val isError: Boolean
) {
    companion object {
        fun default(
            newName: String = "",
            isError: Boolean = false
        ) = CreatePortfolioDialogState(
            newName = newName,
            isError = isError
        )
    }
}
