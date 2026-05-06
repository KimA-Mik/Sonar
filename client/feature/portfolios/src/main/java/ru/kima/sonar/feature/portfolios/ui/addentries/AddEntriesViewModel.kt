package ru.kima.sonar.feature.portfolios.ui.addentries

import android.icu.text.NumberFormat
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
import ru.kima.sonar.common.serverapi.dto.portfolio.request.AddPortfolioEntryRequest
import ru.kima.sonar.common.serverapi.model.portfolio.PortfolioEntry
import ru.kima.sonar.common.serverapi.model.portfolio.StopLoss
import ru.kima.sonar.common.serverapi.model.portfolio.TakeProfit
import ru.kima.sonar.common.serverapi.util.NOTE_LENGTH
import ru.kima.sonar.common.ui.event.SonarEvent
import ru.kima.sonar.common.ui.util.DecimalFormatter
import ru.kima.sonar.common.util.SonarResult
import ru.kima.sonar.common.util.isError
import ru.kima.sonar.common.util.isSuccess
import ru.kima.sonar.common.util.map
import ru.kima.sonar.data.homeapi.datasource.HomeApiDataSource
import ru.kima.sonar.feature.portfolios.ui.addentries.event.AddEntriesSnackbarMessage
import ru.kima.sonar.feature.portfolios.ui.addentries.event.AddEntriesUiEvent
import ru.kima.sonar.feature.portfolios.ui.addentries.event.AddEntriesUserEvent
import ru.kima.sonar.feature.portfolios.ui.addentries.event.SelectSecuritiesDialogUserEvent
import ru.kima.sonar.feature.portfolios.ui.addentries.model.AddEntriesTabs
import ru.kima.sonar.feature.portfolios.ui.addentries.model.AddableSecurity
import ru.kima.sonar.feature.portfolios.ui.addentries.model.mapper.toAddableSecurity
import ru.kima.sonar.feature.portfolios.ui.addentries.state.AddEntriesScreenState
import ru.kima.sonar.feature.portfolios.ui.addentries.state.SelectSecuritiesDialogState
import ru.kima.sonar.feature.portfolios.ui.components.editentry.EditEntryComponent
import ru.kima.sonar.feature.portfolios.ui.components.editentry.toComponents
import ru.kima.sonar.feature.portfolios.ui.components.editentry.toPortfolioEntries
import java.math.BigDecimal
import kotlin.math.max

private const val TAG = "AddEntriesViewModel"

@Stable
internal class AddEntriesViewModel(
    private val portfolioId: Long,
    private val homeApiDataSource: HomeApiDataSource
) : ViewModel() {
    private val decimalFormatter = DecimalFormatter()

    //    private val selectedEntries = MutableStateFlow(persistentListOf<EditableEntry>())
    private val isLoading = MutableStateFlow(false)
    private val networkError = MutableStateFlow(false)
    private val components = MutableStateFlow(persistentListOf<EditEntryComponent>())

    val state = combine(
        isLoading,
        components
    ) { isLoading, components ->
        AddEntriesScreenState(
            isLoading = isLoading,
            components = components
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
            AddEntriesUserEvent.SaveChangesClicked -> onSaveChangesClicked()
            is AddEntriesUserEvent.DeleteEntry -> onDeleteEntry(event.uid)
            is AddEntriesUserEvent.AddStopLoss -> onAddStopLoss(event.uid)
            is AddEntriesUserEvent.AddTakeProfit -> onAddTakeProfit(event.uid)
            is AddEntriesUserEvent.UpdateStopLossNote -> onUpdateStopLossNote(event.key, event.note)
            is AddEntriesUserEvent.UpdateStopLossPrice -> onUpdateStopLossPrice(
                event.key, event.price
            )

            is AddEntriesUserEvent.DeleteStopLoss -> onDeleteStopLoss(event.key)

            is AddEntriesUserEvent.UpdateTakeProfitNote -> onUpdateTakeProfitNote(
                event.key, event.note
            )

            is AddEntriesUserEvent.UpdateTakeProfitPrice -> onUpdateTakeProfitPrice(
                event.key, event.price
            )

            is AddEntriesUserEvent.DeleteTakeProfit -> onDeleteTakeProfit(event.key)
        }
    }

    fun onSelectDialogEvent(event: SelectSecuritiesDialogUserEvent) {
        when (event) {
            SelectSecuritiesDialogUserEvent.AcceptClicked -> onAcceptClicked()
            is SelectSecuritiesDialogUserEvent.EntryChecked -> onEntryChecked(event.uid)
            is SelectSecuritiesDialogUserEvent.QueryUpdated -> onQueryUpdated(event.query)
            SelectSecuritiesDialogUserEvent.RefreshRequest -> onRefreshRequest()
            SelectSecuritiesDialogUserEvent.ClearQueryClicked -> onClearQueryClicked()
            is SelectSecuritiesDialogUserEvent.BulkQueryUpdated -> onBulkQueryUpdated(event.query)
            is SelectSecuritiesDialogUserEvent.TabSelected -> onTabSelected(event.index)
        }
    }

    private var nf: NumberFormat = NumberFormat.getInstance()
    fun setNumberFormatter(nf: NumberFormat) {
        this.nf = nf
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
                networkError.value = false
            } else {
                networkError.value = true
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
    private val selectDialogTab = MutableStateFlow(0)
    private val selectDialogBulkQuery = MutableStateFlow("")
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
        selectDialogIsLoading,
        selectDialogTab,
        selectDialogBulkQuery
    ) { query, securities, isLoading, selectedTab, bulkQuery ->
        SelectSecuritiesDialogState(
            query = query,
            entries = securities,
            isLoading = isLoading,
            selectedTabIndex = selectedTab,
            bulkQuery = bulkQuery
        )
    }.stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(5000),
        SelectSecuritiesDialogState.default()
    )

    private suspend fun loadSelectDialog() = coroutineScope {
        try {
            selectDialogIsLoading.value = true
            val sharesRes = async(Dispatchers.Default) {
                homeApiDataSource.tradableShares()
                    .first()
                    .map { shares ->
                        shares
                            .sortedBy { it.ticker }
                            .map { share ->
                                val selected = components.value.any { it.uid == share.uid }
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
                                val selected = components.value.any { it.uid == future.uid }
                                future.toAddableSecurity(
                                    selected = if (selected) !selectDialogRemovals.contains(future.uid)
                                    else selectDialogAdditions.contains(future.uid)
                                )
                            }
                    }
            }

            val shares = sharesRes.await()
            if (shares.isError()) {
                networkError.value = true
                Log.e(TAG, "Unable to load shares: ${shares.data}")
                return@coroutineScope
            }

            val futures = futuresRes.await()
            if (futures.isError()) {
                networkError.value = true
                Log.e(TAG, "Unable to load futures: ${futures.data}")
                return@coroutineScope
            }

            selectDialogSecurities.value = (shares.data + futures.data)
                .filter { !currentEntries.contains(it.uid) }
                .sortedBy { it.ticker }.toPersistentList()
        } catch (e: Exception) {
            Log.e(TAG, "Unable to load select dialog: ${e.message}")
        } finally {
            selectDialogIsLoading.value = false
        }
    }

    //Main actions
    private fun onOpenSelectSecuritiesDialogClicked() = viewModelScope.launch {
        loadSelectDialog()
        selectDialogQuery.value = ""
        selectDialogAdditions.clear()
        selectDialogRemovals.clear()
        selectDialogBulkQuery.value = ""
        selectDialogTab.value = 0
        _uiEvents.value = SonarEvent(AddEntriesUiEvent.OpenSelectSecuritiesDialog)
    }

    private fun onSaveChangesClicked() = viewModelScope.launch {
        val entries = components.value.toPortfolioEntries()
        if (entries.isEmpty()) return@launch

        val forRequest = entries.map {
            AddPortfolioEntryRequest.Entry(
                securityUid = it.uid,
                name = it.name,
                targetDeviation = it.targetDeviation,
                stopLosses = it.stopLosses,
                takeProfits = it.takeProfits,
            )
        }


        when (val res = homeApiDataSource.addEntry(portfolioId, forRequest)) {
            is SonarResult.Success -> _uiEvents.value =
                SonarEvent(AddEntriesUiEvent.PopBackSuccess)

            is SonarResult.Error -> _uiEvents.value =
                SonarEvent(AddEntriesUiEvent.ShowSnackbar(AddEntriesSnackbarMessage.ApiError(res.data)))
        }
    }

    private fun onDeleteEntry(uid: String) {
        val mutableComponents = components.value.toMutableList()
        mutableComponents.removeAll { it.uid == uid }
        components.value = mutableComponents.toPersistentList()
    }

    private fun onAddStopLoss(uid: String) {
        val newComponents = components.value.toMutableList()
        var maxIndex = 0
        for (component in newComponents) {
            if (component is EditEntryComponent.StopLoss && component.uid == uid) {
                maxIndex = max(maxIndex, EditEntryComponent.getIndex(component.key))
            }
        }

        newComponents.add(
            EditEntryComponent.StopLoss(
                EditEntryComponent.StopLoss.generateKey(uid, maxIndex + 1),
                uid = uid,
                index = maxIndex + 1,
                price = "",
                note = "",
                id = 0,
            )
        )

        newComponents.balanceEntry(uid)
        components.value = newComponents.toPersistentList()
    }

    private fun onAddTakeProfit(uid: String) {
        val newComponents = components.value.toMutableList()
        var maxIndex = 0
        for (component in newComponents) {
            if (component is EditEntryComponent.TakeProfit && component.uid == uid) {
                maxIndex = max(maxIndex, EditEntryComponent.getIndex(component.key))
            }
        }

        newComponents.add(
            EditEntryComponent.TakeProfit(
                EditEntryComponent.TakeProfit.generateKey(uid, maxIndex + 1),
                uid = uid,
                index = maxIndex + 1,
                price = "",
                note = "",
                id = 0,
            )
        )

        newComponents.balanceEntry(uid)
        components.value = newComponents.toPersistentList()
    }

    private fun onUpdateStopLossNote(key: String, note: String) {
        val mutableComponents = components.value.toMutableList()
        val index = mutableComponents.indexOfFirst { it.key == key }
        if (index < 0) return
        val stopLoss = mutableComponents[index]
        if (stopLoss !is EditEntryComponent.StopLoss) return

        mutableComponents[index] = stopLoss.copy(
            note = if (note.length <= NOTE_LENGTH) note else note.take(NOTE_LENGTH)
        )
        components.value = mutableComponents.toPersistentList()
    }

    private fun onUpdateStopLossPrice(key: String, price: String) {
        val mutableComponents = components.value.toMutableList()
        val index = mutableComponents.indexOfFirst { it.key == key }
        if (index < 0) return
        val stopLoss = mutableComponents[index]
        if (stopLoss !is EditEntryComponent.StopLoss) return

        mutableComponents[index] = stopLoss.copy(price = decimalFormatter.cleanup(price))
        components.value = mutableComponents.toPersistentList()
    }

    private fun onDeleteStopLoss(key: String) {
        val newComponents = components.value.toMutableList()
        val i = newComponents.indexOfFirst { it.key == key }
        if (i < 0) return
        val stopLoss = newComponents[i]
        if (stopLoss !is EditEntryComponent.StopLoss) return
        newComponents.removeAt(i)
        newComponents.balanceEntry(stopLoss.uid)
        components.value = newComponents.toPersistentList()
    }

    private fun onUpdateTakeProfitNote(key: String, note: String) {
        val mutableComponents = components.value.toMutableList()
        val index = mutableComponents.indexOfFirst { it.key == key }
        if (index < 0) return
        val takeProfit = mutableComponents[index]
        if (takeProfit !is EditEntryComponent.TakeProfit) return

        mutableComponents[index] = takeProfit.copy(
            note = if (note.length <= NOTE_LENGTH) note else note.take(NOTE_LENGTH)
        )
        components.value = mutableComponents.toPersistentList()
    }

    private fun onUpdateTakeProfitPrice(key: String, price: String) {
        val mutableComponents = components.value.toMutableList()
        val index = mutableComponents.indexOfFirst { it.key == key }
        if (index < 0) return
        val stopLoss = mutableComponents[index] as? EditEntryComponent.TakeProfit ?: return

        mutableComponents[index] = stopLoss.copy(price = decimalFormatter.cleanup(price))
        components.value = mutableComponents.toPersistentList()
    }

    private fun onDeleteTakeProfit(key: String) {
        val newComponents = components.value.toMutableList()
        val i = newComponents.indexOfFirst { it.key == key }
        if (i < 0) return
        val takeProfit = newComponents[i]
        if (takeProfit !is EditEntryComponent.TakeProfit) return
        newComponents.removeAt(i)
        newComponents.balanceEntry(takeProfit.uid)
        components.value = newComponents.toPersistentList()
    }

    //Dialog Actions
    private fun onAcceptClicked() {
        val selectedTabIndex = selectDialogTab.value
        val tab = try {
            AddEntriesTabs.entries[selectedTabIndex]
        } catch (e: Exception) {
            Log.e(TAG, "Unable to get selected tab for index $selectedTabIndex: ${e.message}")
            return
        }

        when (tab) {
            AddEntriesTabs.Selector -> acceptSelector()
            AddEntriesTabs.Bulk -> acceptBulk()
        }
    }

    private fun acceptSelector() {
        if (selectDialogAdditions.isEmpty() && selectDialogRemovals.isEmpty()) {
            return
        }

        val editableComponents = components.value.toMutableList()
        if (selectDialogRemovals.isNotEmpty()) {
            editableComponents.removeAll { selectDialogRemovals.contains(it.uid) }
        }

        val percent = BigDecimal(0.01)
        val newComponents = selectDialogAdditions.values.toComponents(percent)

        components.value = (editableComponents + newComponents).toPersistentList()
    }

    private val regex = """\W+""".toRegex()
    private fun acceptBulk() {
        viewModelScope.launch(Dispatchers.Default) {
            val query = selectDialogBulkQuery.value
            if (query.isBlank()) return@launch

            val words = query.split(regex)
            val securities = selectDialogSecurities.value
            val recognisedSecurities = words.asSequence()
                .mapNotNull { word ->
                    securities.find { it.ticker.contentEquals(word, ignoreCase = true) }
                }
                .filter { !currentEntries.contains(it.uid) }
                .filter { !components.value.any { entry -> entry.uid == it.uid } }
                .toList()

            if (recognisedSecurities.isEmpty()) {
                _uiEvents.value =
                    SonarEvent(AddEntriesUiEvent.ShowSnackbar(AddEntriesSnackbarMessage.NoSecuritiesFound))
                return@launch
            }

            val editableComponents = components.value.toMutableList()
            val newComponents = recognisedSecurities.toComponents(0.01.toBigDecimal())

            _uiEvents.value = SonarEvent(
                AddEntriesUiEvent.ShowSnackbar(
                    AddEntriesSnackbarMessage.AddedBulkSecurities(recognisedSecurities.size)
                )
            )

            components.value = (editableComponents + newComponents).toPersistentList()
        }
    }

    private fun Collection<AddableSecurity>.toComponents(percent: BigDecimal) = map { security ->
        val priceDeviation = security.price * percent
        PortfolioEntry(
            id = 0,
            uid = security.uid,
            name = security.name,
            targetDeviation = percent,
            price = security.price,
            lowPrice = percent,
            highPrice = percent,
            note = "",
            stopLosses = listOf(
                StopLoss(
                    id = 0,
                    entryId = 0,
                    price = security.price - priceDeviation,
                    note = ""
                )
            ),
            takeProfits = listOf(
                TakeProfit(
                    id = 0,
                    entryId = 0,
                    price = security.price + priceDeviation,
                    note = ""
                )
            ),
        )
    }.toComponents()

    private fun onEntryChecked(uid: String) {
        val securities = selectDialogSecurities.value.toMutableList()
        val i = securities.indexOfFirst { it.uid == uid }
        if (i < 0) {
            Log.e(TAG, "Unable to check security $uid")
            return
        }

        val security = securities[i]
        val selected = components.value.any { it.uid == security.uid }
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
    private fun onBulkQueryUpdated(query: String) = selectDialogBulkQuery.update { query }
    private fun onTabSelected(index: Int) = selectDialogTab.update { index }

    private fun List<EditEntryComponent>.findTitle(uid: String) = indexOfFirst {
        it is EditEntryComponent.Title && it.uid == uid
    }

    private fun MutableList<EditEntryComponent>.balanceEntry(uid: String) {
        var titleIndex = findTitle(uid)
        if (titleIndex < 0) return

        val stopLosses = filter { it is EditEntryComponent.StopLoss && it.uid == uid }
        val takeProfits = filter { it is EditEntryComponent.TakeProfit && it.uid == uid }
        removeAll { it !is EditEntryComponent.Title && it.uid == uid }
        val height = max(stopLosses.size, takeProfits.size)
        val balanced = mutableListOf<EditEntryComponent>()

        var paddingCount = 0
        for (row in 0..height) {
            val slComponent = when {
                row < stopLosses.size -> {
                    val stopLoss = stopLosses[row] as EditEntryComponent.StopLoss
                    stopLoss.copy(index = row + 1)
                }

                row == stopLosses.size -> EditEntryComponent.AddStopLoss(uid)
                else -> EditEntryComponent.Padding(
                    key = EditEntryComponent.Padding.generateKey(uid, paddingCount++),
                    uid = uid
                )
            }

            val tpComponent = when {
                row < takeProfits.size -> {
                    val takeProfit = takeProfits[row] as EditEntryComponent.TakeProfit
                    takeProfit.copy(index = row + 1)
                }

                row == takeProfits.size -> EditEntryComponent.AddTakeProfit(uid)
                else -> EditEntryComponent.Padding(
                    key = EditEntryComponent.Padding.generateKey(uid, paddingCount++),
                    uid = uid
                )
            }

            balanced.add(slComponent)
            balanced.add(tpComponent)
        }

        titleIndex = findTitle(uid)
        addAll(titleIndex + 1, balanced)
    }
}
