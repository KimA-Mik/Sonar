package ru.kima.sonar.feature.securities.ui.list

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import ru.kima.sonar.common.util.SonarResult
import ru.kima.sonar.data.homeapi.datasource.HomeApiDataSource
import ru.kima.sonar.feature.securities.ui.list.model.DisplayListItemShare
import ru.kima.sonar.feature.securities.ui.list.model.mappers.toDisplayListItemShare

private const val TAG = "SecuritiesListViewModel"

class SecuritiesListViewModel(
    private val homeApiDataSource: HomeApiDataSource
) : ViewModel() {
    private var sharesJob: Job? = null
    private val shares = MutableStateFlow(emptyList<DisplayListItemShare>())
    private val sharesListState =
        MutableStateFlow<SecuritiesListState.SecurityListState>(SecuritiesListState.SecurityListState.Nothing)

    val state = combine(
        shares,
        sharesListState
    ) { shares, sharesListState ->
        SecuritiesListState(
            shares = shares,
            sharesListState = sharesListState
        )
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), SecuritiesListState.default())

    fun onEvent(event: SecuritiesListEvent) {
        when (event) {
            SecuritiesListEvent.OnSharesListOpen -> onSharesListOpen()
            SecuritiesListEvent.OnSharesListDispose -> onSharesListDispose()
        }
    }

    private fun onSharesListOpen() {
        if (sharesJob != null) return
        sharesJob = viewModelScope.launch(Dispatchers.Default) {
            sharesListState.value = SecuritiesListState.SecurityListState.Loading
            homeApiDataSource.tradableShares().collect { result ->
                when (result) {
                    is SonarResult.Success -> {
                        shares.value = result.data.map { it.toDisplayListItemShare() }
                        sharesListState.value = SecuritiesListState.SecurityListState.Nothing
                    }

                    is SonarResult.Error -> sharesListState.value =
                        SecuritiesListState.SecurityListState.Error
                }
            }
        }
    }

    private fun onSharesListDispose() {
        sharesJob?.cancel()
        sharesJob = null
    }
}