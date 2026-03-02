package ru.kima.sonar.feature.portfolios.ui.addentries

import android.util.Log
import androidx.compose.runtime.Stable
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.collections.immutable.persistentListOf
import kotlinx.collections.immutable.toPersistentList
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import ru.kima.sonar.common.ui.util.SonarEvent
import ru.kima.sonar.common.util.isError
import ru.kima.sonar.common.util.isSuccess
import ru.kima.sonar.common.util.map
import ru.kima.sonar.data.homeapi.datasource.HomeApiDataSource
import ru.kima.sonar.feature.portfolios.ui.addentries.event.AddEntriesUiEvent
import ru.kima.sonar.feature.portfolios.ui.addentries.event.AddEntriesUserEvent
import ru.kima.sonar.feature.portfolios.ui.addentries.event.SelectSecuritiesDialogUserEvent
import ru.kima.sonar.feature.portfolios.ui.addentries.model.AddableSecurity
import ru.kima.sonar.feature.portfolios.ui.addentries.model.EditableEntry
import ru.kima.sonar.feature.portfolios.ui.addentries.model.mapper.toAddableSecurity
import ru.kima.sonar.feature.portfolios.ui.addentries.state.AddEntriesScreenState
import ru.kima.sonar.feature.portfolios.ui.addentries.state.SelectSecuritiesDialogState
import java.math.BigDecimal

private const val TAG = "AddEntriesViewModel"

@Stable
internal class AddEntriesViewModel(
    private val portfolioId: Long,
    private val homeApiDataSource: HomeApiDataSource
) : ViewModel() {
    private val isLoading = MutableStateFlow(false)
    private val wasError = MutableStateFlow(false)
    private val selectedEntries = MutableStateFlow(persistentListOf<EditableEntry>())
    val state = combine(
        isLoading,
        wasError,
        selectedEntries
    ) { isLoading, wasError, entries ->
        AddEntriesScreenState(
            isLoading = isLoading,
            wasError = wasError,
            entries = entries
        )
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), AddEntriesScreenState.default())

    private val currentEntries = mutableSetOf<String>()

    init {
        refresh()
    }

    private val _uiEvents = MutableStateFlow(SonarEvent<AddEntriesUiEvent>())
    val uiEvents = _uiEvents.asStateFlow()

    fun onEvent(event: AddEntriesUserEvent) {
        when (event) {
            AddEntriesUserEvent.OpenSelectSecuritiesDialogClicked -> onOpenSelectSecuritiesDialogClicked()
        }
    }

    fun onSelectDialogEvent(event: SelectSecuritiesDialogUserEvent) {
        when (event) {
            SelectSecuritiesDialogUserEvent.AcceptClicked -> onAcceptClicked()
            is SelectSecuritiesDialogUserEvent.EntryChecked -> onEntryChecked(event.uid)
            is SelectSecuritiesDialogUserEvent.QueryUpdated -> onQueryUpdated(event.query)
            SelectSecuritiesDialogUserEvent.RefreshRequest -> onRefreshRequest()
            SelectSecuritiesDialogUserEvent.ClearQueryClicked -> onClearQueryClicked()
        }
    }

    private fun refresh() {
        viewModelScope.launch {
            isLoading.value = true
            currentEntries.clear()
            val res = homeApiDataSource.getPortfolio(portfolioId)
            if (res.isSuccess()) {
                res.data.entries.forEach {
                    currentEntries.add(it.uid)
                }
                wasError.value = false
            } else {
                wasError.value = true
                Log.e(TAG, "Failed to load portfolio details: ${res.data}")
            }
            isLoading.value = false
        }
    }

    private val selectDialogAdditions = mutableMapOf<String, AddableSecurity>()
    private val selectDialogRemovals = mutableSetOf<String>()
    //Select dialog state
    private val selectDialogQuery = MutableStateFlow("")
    private val selectDialogSecurities = MutableStateFlow(persistentListOf<AddableSecurity>())
    private val selectDialogIsLoading = MutableStateFlow(false)
    private val filteredSecurities = combine(
        selectDialogQuery, selectDialogSecurities
    ) { query, securities ->
        securities.filter { it.basicAsset.contains(query, ignoreCase = true) }
    }
        .map { it.toPersistentList() }
        .flowOn(Dispatchers.Default)
    val selectDialogState = combine(
        selectDialogQuery,
        filteredSecurities,
        selectDialogIsLoading
    ) { query, securities, isLoading ->
        SelectSecuritiesDialogState(
            query = query,
            entries = securities,
            isLoading = isLoading
        )
    }.stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(5000),
        SelectSecuritiesDialogState.default()
    )
    private suspend fun loadSelectDialog() = coroutineScope {
        try {
            val sharesRes = async(Dispatchers.Default) {
                homeApiDataSource.tradableShares()
                    .first()
                    .map { shares ->
                        shares
                            .sortedBy { it.ticker }
                            .map { share ->
                                val selected = selectedEntries.value.any { it.uid == share.uid }
                                share.toAddableSecurity(
                                    selected = if (selected) !selectDialogRemovals.contains(share.uid)
                                    else selectDialogAdditions.contains(share.uid)
                                )
                            }
                    }
            }
            val futuresRes = async(Dispatchers.Default) {
                homeApiDataSource.tradableFutures()
                    .first()
                    .map { futures ->
                        futures
                            .sortedBy { it.expirationDate }
                            .map { future ->
                                val selected = selectedEntries.value.any { it.uid == future.uid }
                                future.toAddableSecurity(
                                    selected = if (selected) !selectDialogRemovals.contains(future.uid)
                                    else selectDialogAdditions.contains(future.uid)
                                )
                            }
                    }
            }

            val shares = sharesRes.await()
            if (shares.isError()) {
                wasError.value = true
                Log.e(TAG, "Unable to load shares: ${shares.data}")
                return@coroutineScope
            }

            val futures = futuresRes.await()
            if (futures.isError()) {
                wasError.value = true
                Log.e(TAG, "Unable to load futures: ${futures.data}")
                return@coroutineScope
            }

            selectDialogSecurities.value =
                (shares.data + futures.data).sortedBy { it.ticker }.toPersistentList()
        } catch (e: Exception) {
            Log.e(TAG, "Unable to load select dialog: ${e.message}")
        } finally {
            selectDialogIsLoading.value = false
        }
    }

    private fun onOpenSelectSecuritiesDialogClicked() = viewModelScope.launch {
        loadSelectDialog()
        selectDialogQuery.value = ""
        selectDialogAdditions.clear()
        selectDialogRemovals.clear()
        _uiEvents.value = SonarEvent(AddEntriesUiEvent.OpenSelectSecuritiesDialog)
    }

    //Dialog Actions
    private fun onAcceptClicked() {
        if (selectDialogAdditions.isEmpty() && selectDialogRemovals.isEmpty()) {
            return
        }

        val editableEntries = selectedEntries.value.toMutableList()
        if (selectDialogRemovals.isNotEmpty()) {
            editableEntries.removeIf { selectDialogRemovals.contains(it.uid) }
        }

        val percent = 0.1f
        val bdPercent = BigDecimal(percent.toString())
        for ((_, security) in selectDialogAdditions) {
            val priceDeviation = security.price * bdPercent
            val newEntry = EditableEntry(
                uid = security.uid,
                ticker = security.ticker,
                price = security.price,
                lowPrice = security.price - priceDeviation,
                highPrice = security.price + priceDeviation,
                expanded = true,
                note = ""
            )
            editableEntries.add(newEntry)
        }

        selectedEntries.value = editableEntries.toPersistentList()
    }

    private fun onEntryChecked(uid: String) {
        val securities = selectDialogSecurities.value.toMutableList()
        val i = securities.indexOfFirst { it.uid == uid }
        if (i < 0) {
            Log.e(TAG, "Unable to check security $uid")
            return
        }

        val security = securities[i]
        val selected = selectedEntries.value.any { it.uid == security.uid }
        val newSecurity: AddableSecurity
        if (selected) {
            if (selectDialogRemovals.contains(security.uid)) {
                selectDialogRemovals.remove(security.uid)
                newSecurity = security.copy(selected = true)
            } else {
                selectDialogRemovals.add(security.uid)
                newSecurity = security.copy(selected = false)
            }
        } else {
            if (selectDialogAdditions.contains(security.uid)) {
                selectDialogAdditions.remove(security.uid)
                newSecurity = security.copy(selected = false)
            } else {
                selectDialogAdditions[security.uid] = security
                newSecurity = security.copy(selected = true)
            }
        }

        securities[i] = newSecurity
        selectDialogSecurities.value = securities.toPersistentList()
    }

    private fun onQueryUpdated(query: String) = selectDialogQuery.update { query }

    private fun onRefreshRequest() = viewModelScope.launch {
        loadSelectDialog()
    }

    private fun onClearQueryClicked() = selectDialogQuery.update { "" }
}
