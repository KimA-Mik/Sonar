package ru.kima.sonar.feature.portfolios.ui.details.state

import androidx.compose.runtime.Immutable
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf
import ru.kima.sonar.feature.portfolios.ui.components.editentry.EditEntryComponent

@Immutable
internal data class EditEntryDialogState(
    val isLoading: Boolean,
    val name: String,
    val components: ImmutableList<EditEntryComponent>
) {
    companion object {
        fun default(
            isLoading: Boolean = false,
            name: String = "",
            components: ImmutableList<EditEntryComponent> = persistentListOf()
        ) = EditEntryDialogState(
            isLoading = isLoading,
            name = name,
            components = components
        )
    }
}
