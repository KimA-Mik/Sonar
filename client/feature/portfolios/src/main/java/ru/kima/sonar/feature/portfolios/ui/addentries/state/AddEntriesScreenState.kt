package ru.kima.sonar.feature.portfolios.ui.addentries.state

import androidx.compose.runtime.Immutable
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf
import ru.kima.sonar.feature.portfolios.ui.addentries.model.EditableEntry

@Immutable
internal data class AddEntriesScreenState(
    val isLoading: Boolean,
    val wasError: Boolean,
    val entries: ImmutableList<EditableEntry>
) {
    companion object {
        fun default(
            isLoading: Boolean = false,
            wasError: Boolean = false,
            entries: ImmutableList<EditableEntry> = persistentListOf()
        ) = AddEntriesScreenState(
            isLoading = isLoading,
            wasError = wasError,
            entries = entries
        )
    }
}
