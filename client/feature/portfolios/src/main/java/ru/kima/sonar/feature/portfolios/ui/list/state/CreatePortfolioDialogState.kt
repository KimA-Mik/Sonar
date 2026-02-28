package ru.kima.sonar.feature.portfolios.ui.list.state

import androidx.compose.runtime.Immutable

@Immutable
internal data class CreatePortfolioDialogState(
    val newName: String,
    val error: DialogError
) {
    enum class DialogError {
        NONE, BLANK_NAME
    }
    companion object {
        fun default(
            newName: String = "",
            error: DialogError = DialogError.NONE
        ) = CreatePortfolioDialogState(
            newName = newName,
            error = error
        )
    }
}
