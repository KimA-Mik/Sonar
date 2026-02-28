package ru.kima.sonar.feature.portfolios.ui.details

import android.util.Log
import androidx.compose.runtime.Stable
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.collections.immutable.persistentListOf
import kotlinx.collections.immutable.toPersistentList
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import ru.kima.sonar.common.ui.util.SonarEvent
import ru.kima.sonar.common.util.isSuccess
import ru.kima.sonar.data.homeapi.datasource.HomeApiDataSource
import ru.kima.sonar.feature.portfolios.ui.details.event.PortfolioDetailsUiEvent
import ru.kima.sonar.feature.portfolios.ui.details.event.PortfolioDetailsUserEvent
import ru.kima.sonar.feature.portfolios.ui.details.model.DisplayItemEntry
import ru.kima.sonar.feature.portfolios.ui.details.model.mapper.toDisplayItemEntry
import ru.kima.sonar.feature.portfolios.ui.details.state.PortfolioDetailsState

private const val TAG = "PortfolioDetailsViewModel"

@Stable
internal class PortfolioDetailsViewModel(
    private val portfolioId: Long,
    private val homeApiDataSource: HomeApiDataSource
) : ViewModel() {
    private val name = MutableStateFlow("")
    private val entries = MutableStateFlow(persistentListOf<DisplayItemEntry>())
    private val isLoading = MutableStateFlow(false)
    private val wasError = MutableStateFlow(false)

    init {
        refresh()
    }

    val state = combine(
        name,
        entries,
        isLoading,
        wasError,
    ) { name, entries, isLoading, wasError ->
        PortfolioDetailsState(
            name = name,
            entries = entries,
            isLoading = isLoading,
            wasError = wasError
        )
    }.stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(5_000),
        PortfolioDetailsState.default()
    )

    private val _uiEvents = MutableStateFlow(SonarEvent<PortfolioDetailsUiEvent>())
    val uiEvents = _uiEvents.asStateFlow()

    fun onEvent(event: PortfolioDetailsUserEvent) {
        when (event) {
            PortfolioDetailsUserEvent.AddEntriesButtonClicked -> onAddEntriesButtonClicked()
        }
    }

    private fun refresh() {
        viewModelScope.launch {
            isLoading.value = true
            val res = homeApiDataSource.getPortfolio(portfolioId)
            if (res.isSuccess()) {
                val portfolio = res.data
                name.value = portfolio.name
                entries.value = portfolio.entries.map { it.toDisplayItemEntry() }.toPersistentList()
                wasError.value = false
            } else {
                wasError.value = true
                Log.e(TAG, "Failed to load portfolio details: ${res.data}")
            }
            isLoading.value = false
        }
    }

    private fun onAddEntriesButtonClicked() {
        if (!wasError.value) {
            _uiEvents.value = SonarEvent(PortfolioDetailsUiEvent.OpenAddEntriesScreen(portfolioId))
        }
    }


}