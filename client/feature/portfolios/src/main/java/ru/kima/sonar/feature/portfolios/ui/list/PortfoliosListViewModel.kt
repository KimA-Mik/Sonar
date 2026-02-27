package ru.kima.sonar.feature.portfolios.ui.list

import android.util.Log
import androidx.compose.runtime.Stable
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.collections.immutable.persistentListOf
import kotlinx.collections.immutable.toPersistentList
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import ru.kima.sonar.common.ui.util.SonarEvent
import ru.kima.sonar.common.util.isSuccess
import ru.kima.sonar.data.homeapi.datasource.HomeApiDataSource
import ru.kima.sonar.feature.portfolios.ui.list.event.PortfolioListEvent
import ru.kima.sonar.feature.portfolios.ui.list.event.PortfolioListUiEvent
import ru.kima.sonar.feature.portfolios.ui.list.model.DisplayPortfolio
import ru.kima.sonar.feature.portfolios.ui.list.state.CreatePortfolioDialogState
import ru.kima.sonar.feature.portfolios.ui.list.state.PortfolioListState

private const val TAG = "PortfoliosListViewModel"

@Stable
internal class PortfoliosListViewModel(
    private val homeApiDataSource: HomeApiDataSource,
) : ViewModel() {
    private val portfolios = MutableStateFlow(persistentListOf<DisplayPortfolio>())
    private val isLoading = MutableStateFlow(false)

    init {
        onRefresh()
    }

    val state = combine(
        portfolios,
        isLoading
    ) { portfolios, isLoading ->
        PortfolioListState(
            portfolios = portfolios,
            isLoading = isLoading
        )
    }.stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(5_000),
        PortfolioListState.default()
    )

    private val _uiEvents = MutableStateFlow(SonarEvent<PortfolioListUiEvent>())
    val uiEvents = _uiEvents.asStateFlow()

    private val createDialogValue = MutableStateFlow("")
    private val createDialogIsError = MutableStateFlow(false)
    val dialogState = combine(createDialogValue, createDialogIsError) { name, isError ->
        CreatePortfolioDialogState(
            newName = name,
            isError = isError
        )
    }.stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(5_000),
        CreatePortfolioDialogState.default()
    )

    fun onEvent(event: PortfolioListEvent) {
        when (event) {
            PortfolioListEvent.Refresh -> onRefresh()
            PortfolioListEvent.CreatePortfolioClicked -> onCreatePortfolioClicked()
            PortfolioListEvent.AcceptNewPortfolioDialog -> onAcceptNewPortfolioDialog()
            PortfolioListEvent.DismissNewPortfolioDialog -> onDismissNewPortfolioDialog()
            is PortfolioListEvent.UpdatePortfolioName -> onUpdatePortfolioName(event.name)
        }
    }

    private fun onRefresh() = launchLoading(Dispatchers.Default) {
        val res = homeApiDataSource.portfolios()
        if (res.isSuccess()) {
            portfolios.value = res.data.map {
                DisplayPortfolio(
                    id = it.id,
                    name = it.name
                )
            }.toPersistentList()
        } else {
            Log.d(TAG, "Failed to load portfolios: ${res.data}")
        }
    }


    private fun onCreatePortfolioClicked() {
        createDialogValue.value = ""
        _uiEvents.value = SonarEvent(PortfolioListUiEvent.OpenCreatePortfolioDialog)
    }

    private fun onAcceptNewPortfolioDialog() {
        if (createDialogValue.value.isBlank()) {
            createDialogIsError.value = true
            return
        }

        _uiEvents.value = SonarEvent(PortfolioListUiEvent.DismissCreatePortfolioDialog)
        launchLoading {
            val res = homeApiDataSource.createPortfolio(createDialogValue.value)
            if (res.isSuccess()) {
                onRefresh()
            } else {
                Log.d(TAG, "Failed to create portfolio: ${res.data}")
            }
        }
    }

    private fun onDismissNewPortfolioDialog() =
        _uiEvents.update { SonarEvent(PortfolioListUiEvent.DismissCreatePortfolioDialog) }

    private fun onUpdatePortfolioName(newName: String) {
        createDialogIsError.value = false
        createDialogValue.value = newName
    }

    private fun launchLoading(
        dispatcher: CoroutineDispatcher = Dispatchers.IO,
        action: suspend () -> Unit
    ) = viewModelScope.launch(dispatcher) {
        isLoading.value = true
        try {
            action()
        } finally {
            isLoading.value = false
        }
    }
}