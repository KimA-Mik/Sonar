package ru.kima.sonar.feature.portfolios.ui.addentries.state

import androidx.compose.runtime.Immutable
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf
import ru.kima.sonar.feature.portfolios.ui.components.editentry.EditEntryComponent

@Immutable
internal data class AddEntriesScreenState(
    val isLoading: Boolean,
    val components: ImmutableList<EditEntryComponent>
) {
    companion object {
        fun default(
            isLoading: Boolean = false,
            components: ImmutableList<EditEntryComponent> = persistentListOf()
        ) = AddEntriesScreenState(
            isLoading = isLoading,
            components = components
        )
    }
}
