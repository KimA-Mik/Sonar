package ru.kima.sonar.feature.portfolios.ui.list.dialog

import androidx.compose.runtime.Stable
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import ru.kima.sonar.common.ui.event.SonarEvent
import ru.kima.sonar.common.util.isSuccess
import ru.kima.sonar.data.homeapi.datasource.HomeApiDataSource
import ru.kima.sonar.feature.portfolios.ui.list.event.RemovePortfolioDialogUiEvent
import ru.kima.sonar.feature.portfolios.ui.list.event.RemovePortfolioDialogUserEvent
import ru.kima.sonar.feature.portfolios.ui.list.state.RemovePortfolioDialogState

@Stable
internal class RemovePortfolioDialogViewModel(
    private val portfolioId: Long,
    private val homeApi: HomeApiDataSource
) : ViewModel() {
    private val name = MutableStateFlow("")
    private val isLoading = MutableStateFlow(false)
    private val waError = MutableStateFlow(false)
    val state = combine(
        name,
        isLoading,
        waError
    ) { name, isLoading, waError ->
        RemovePortfolioDialogState(
            name = name,
            isLoading = isLoading,
            waError = waError
        )
    }.stateIn(
        viewModelScope, SharingStarted.WhileSubscribed(5_000), RemovePortfolioDialogState.default()
    )

    private fun loadPortfolio() = viewModelScope.launch {
        isLoading.value = true
        val res = homeApi.getPortfolio(portfolioId)
        if (res.isSuccess()) {
            name.value = res.data.name
        } else {
            waError.value = true
        }
        isLoading.value = false
    }

    init {
        loadPortfolio()
    }

    private val _uiEvents = MutableStateFlow(SonarEvent<RemovePortfolioDialogUiEvent>())
    val uiEvents = _uiEvents.asStateFlow()
    fun onEvent(event: RemovePortfolioDialogUserEvent) {
        when (event) {
            RemovePortfolioDialogUserEvent.AcceptClicked -> onAcceptClicked()
            RemovePortfolioDialogUserEvent.DismissClicked -> onDismissClicked()
            RemovePortfolioDialogUserEvent.RefreshClicked -> onRefreshClicked()
        }
    }

    private fun onAcceptClicked() = viewModelScope.launch {
        val res = homeApi.deletePortfolio(portfolioId)
        if (res.isSuccess()) {
            _uiEvents.value = SonarEvent(RemovePortfolioDialogUiEvent.PopBackWithRefresh)
        } else {
            waError.value = true
        }
    }

    private fun onDismissClicked() {
        _uiEvents.value = SonarEvent(RemovePortfolioDialogUiEvent.PopBack)
    }

    private fun onRefreshClicked() {
        loadPortfolio()
    }
}