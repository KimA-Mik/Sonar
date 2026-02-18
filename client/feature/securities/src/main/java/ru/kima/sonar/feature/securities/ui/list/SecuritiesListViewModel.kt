package ru.kima.sonar.feature.securities.ui.list

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import ru.kima.sonar.common.util.SonarResult
import ru.kima.sonar.data.homeapi.datasource.HomeApiDataSource

private const val TAG = "SecuritiesListViewModel"

class SecuritiesListViewModel(homeApiDataSource: HomeApiDataSource) : ViewModel() {
    init {
        viewModelScope.launch {
            homeApiDataSource.tradableShares().collect {
                when (it) {
                    is SonarResult.Success -> _state.value = SecuritiesListState(shares = it.data)
                    is SonarResult.Error -> _state.value = SecuritiesListState(shares = emptyList())
                }
            }

        }
    }

    private val _state = MutableStateFlow(SecuritiesListState(shares = emptyList()))
    val state = _state.asStateFlow()
}