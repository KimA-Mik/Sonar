package ru.kima.sonar.feature.portfolios.ui.addentries.state

import androidx.compose.runtime.Immutable
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf
import ru.kima.sonar.feature.portfolios.ui.addentries.model.AddableSecurity

@Immutable
data class SelectSecuritiesDialogState(
    val query: String,
    val entries: ImmutableList<AddableSecurity>,
    val isLoading: Boolean
) {
    companion object {
        fun default(
            query: String = "",
            entries: ImmutableList<AddableSecurity> = persistentListOf(),
            isLoading: Boolean = false
        ) = SelectSecuritiesDialogState(
            query = query,
            entries = entries,
            isLoading
        )
    }
}
