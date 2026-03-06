package ru.kima.sonar.feature.portfolios.ui.list

import android.util.Log
import androidx.compose.runtime.Stable
import androidx.lifecycle.SavedStateHandle
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
import ru.kima.sonar.common.ui.event.SonarEvent
import ru.kima.sonar.common.util.isSuccess
import ru.kima.sonar.data.homeapi.datasource.HomeApiDataSource
import ru.kima.sonar.feature.portfolios.ui.list.event.PortfolioListEvent
import ru.kima.sonar.feature.portfolios.ui.list.event.PortfolioListUiEvent
import ru.kima.sonar.feature.portfolios.ui.list.model.DisplayPortfolio
import ru.kima.sonar.feature.portfolios.ui.list.state.PortfolioListState
import ru.kima.sonar.feature.portfolios.ui.list.state.PortfolioNameDialogState

private const val TAG = "PortfoliosListViewModel"

@Stable
internal class PortfoliosListViewModel(
    private val homeApiDataSource: HomeApiDataSource,
    private val savedStateHandle: SavedStateHandle
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

    private val selectedPortfolioId =
        savedStateHandle.getMutableStateFlow(KEY_PORTFOLIO_ID, EMPTY_ID)
    private val createDialogValue = savedStateHandle.getMutableStateFlow(KEY_PORTFOLIO_NAME, "")
    private val createDialogError = savedStateHandle.getMutableStateFlow(
        KEY_PORTFOLIO_DIALOG_ERROR,
        PortfolioNameDialogState.DialogError.NONE
    )
    val dialogState = combine(createDialogValue, createDialogError) { name, error ->
        PortfolioNameDialogState(
            newName = name,
            error = error
        )
    }.stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(5_000),
        PortfolioNameDialogState.default()
    )

    fun onEvent(event: PortfolioListEvent) {
        when (event) {
            PortfolioListEvent.Refresh -> onRefresh()
            PortfolioListEvent.CreatePortfolioClicked -> onCreatePortfolioClicked()
            PortfolioListEvent.AcceptNewPortfolioDialog -> onAcceptNewPortfolioDialog()
            PortfolioListEvent.DismissNewPortfolioDialog -> onDismissNewPortfolioDialog()
            is PortfolioListEvent.RenamePortfolioClicked -> onRenamePortfolioClicked(event.portfolioId)
            PortfolioListEvent.AcceptRenamePortfolioDialog -> onAcceptRenamePortfolioDialog()
            PortfolioListEvent.DismissRenamePortfolioDialog -> onDismissRenamePortfolioDialog()
            is PortfolioListEvent.UpdatePortfolioName -> onUpdatePortfolioName(event.name)
            is PortfolioListEvent.PortfolioClicked -> onPortfolioClicked(event.portfolioId)
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
        createDialogError.value = PortfolioNameDialogState.DialogError.NONE
        _uiEvents.value = SonarEvent(PortfolioListUiEvent.OpenCreatePortfolioDialog)
    }

    private fun onAcceptNewPortfolioDialog() {
        if (createDialogValue.value.isBlank()) {
            createDialogError.value = PortfolioNameDialogState.DialogError.BLANK_NAME
            return
        }

        _uiEvents.value = SonarEvent(PortfolioListUiEvent.DismissDialog)
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
        _uiEvents.update { SonarEvent(PortfolioListUiEvent.DismissDialog) }

    private fun onRenamePortfolioClicked(portfolioId: Long) {
        val portfolio = portfolios.value.firstOrNull { it.id == portfolioId } ?: return
        selectedPortfolioId.value = portfolioId
        createDialogValue.value = portfolio.name
        createDialogError.value = PortfolioNameDialogState.DialogError.NONE
        _uiEvents.value = SonarEvent(PortfolioListUiEvent.OpenRenamePortfolioDialog)
    }

    private fun onAcceptRenamePortfolioDialog() = viewModelScope.launch {
        val portfolioId = selectedPortfolioId.value
        if (portfolioId == EMPTY_ID) return@launch
        if (createDialogValue.value.isBlank()) {
            createDialogError.value = PortfolioNameDialogState.DialogError.BLANK_NAME
            return@launch
        }

        val res = homeApiDataSource.updatePortfolio(portfolioId, createDialogValue.value)
        if (res.isSuccess()) {
            onRefresh()
            _uiEvents.update { SonarEvent(PortfolioListUiEvent.DismissDialog) }
        } else {
            Log.d(TAG, "Failed to rename portfolio with id $portfolioId: ${res.data}")
        }
    }

    private fun onDismissRenamePortfolioDialog() =
        _uiEvents.update { SonarEvent(PortfolioListUiEvent.DismissDialog) }

    private fun onUpdatePortfolioName(newName: String) {
        createDialogError.value = PortfolioNameDialogState.DialogError.NONE
        createDialogValue.value = newName
    }

    private fun onPortfolioClicked(portfolioId: Long) = _uiEvents.update {
        SonarEvent(PortfolioListUiEvent.NavigateToPortfolioDetails(portfolioId))
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

    companion object {
        private const val KEY_PORTFOLIO_ID = "portfolioId"
        private const val KEY_PORTFOLIO_NAME = "portfolioName"
        private const val KEY_PORTFOLIO_DIALOG_ERROR = "portfolioDialogError"
        private const val EMPTY_ID = 0L
    }
}