package ru.kima.sonar.feature.securities.ui.list

import androidx.compose.runtime.Immutable
import ru.kima.sonar.feature.securities.ui.list.model.DisplayListItemFuture
import ru.kima.sonar.feature.securities.ui.list.model.DisplayListItemShare

@Immutable
data class SecuritiesListState(
    val shares: List<DisplayListItemShare>,
    val sharesListState: SecurityListState,
    val futures: List<DisplayListItemFuture>,
    val futuresListState: SecurityListState
) {
    sealed interface SecurityListState {
        data object Nothing : SecurityListState
        data object Loading : SecurityListState
        data object Error : SecurityListState
    }

    companion object {
        fun default(
            shares: List<DisplayListItemShare> = emptyList(),
            sharesListState: SecurityListState = SecurityListState.Nothing,
            futures: List<DisplayListItemFuture> = emptyList(),
            futuresListState: SecurityListState = SecurityListState.Nothing
        ) = SecuritiesListState(
            shares = shares,
            sharesListState = sharesListState,
            futures = futures,
            futuresListState = futuresListState
        )
    }
}