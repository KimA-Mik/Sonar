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
import kotlinx.coroutines.awaitAll
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
import ru.kima.sonar.common.serverapi.util.NOTE_LENGTH
import ru.kima.sonar.common.ui.event.SonarEvent
import ru.kima.sonar.common.ui.util.DecimalFormatter
import ru.kima.sonar.common.util.BigDecimalUtil
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
    private val decimalFormatter = DecimalFormatter()
    private val selectedEntries = MutableStateFlow(persistentListOf<EditableEntry>())
    private val isLoading = MutableStateFlow(false)
    private val networkError = MutableStateFlow(false)
    private val inputError =
        selectedEntries.map { entries -> entries.any { it.highPriceError || it.lowPriceError } }
    private val wasError = combine(networkError, inputError) { networkError, inputError ->
        networkError || inputError
    }

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
            is AddEntriesUserEvent.ExpandClicked -> onExpandClicked(event.uid)
            is AddEntriesUserEvent.UpdateHighPrice -> onUpdateHighPrice(event.uid, event.price)
            is AddEntriesUserEvent.UpdateLowPrice -> onUpdateLowPrice(event.uid, event.price)
            is AddEntriesUserEvent.UpdateTargetDeviation -> onUpdateTargetDeviation(
                event.uid,
                event.deviation
            )

            is AddEntriesUserEvent.NoteUpdated -> onUpdateNote(event.uid, event.note)
            is AddEntriesUserEvent.RemoveEntryClicked -> onRemoveEntry(event.uid)
            AddEntriesUserEvent.SaveChangesClicked -> onSaveChangesClicked()
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
            selectDialogIsLoading.value = true
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
        _uiEvents.value = SonarEvent(AddEntriesUiEvent.OpenSelectSecuritiesDialog)
    }

    private fun onExpandClicked(uid: String) {
        updateEntry(uid) {
            it.copy(expanded = !it.expanded)
        }
    }

    private fun onUpdateHighPrice(uid: String, price: String) {
        val cleanedPrice = decimalFormatter.cleanup(price)
        updateEntry(uid) {
            it.copy(
                highPrice = cleanedPrice,
//                highPriceError = validBigDecimal(cleanedPrice)
            )
        }
    }

    private fun onUpdateLowPrice(uid: String, price: String) {
        val cleanedPrice = decimalFormatter.cleanup(price)
        updateEntry(uid) {
            it.copy(
                lowPrice = cleanedPrice,
//                lowPriceError = validBigDecimal(cleanedPrice)
            )
        }
    }

    private fun onUpdateTargetDeviation(
        uid: String,
        deviation: String
    ) {
        val cleanedPrice = decimalFormatter.cleanup(deviation)
        updateEntry(uid) {
            it.copy(
                targetDeviation = cleanedPrice,
//                lowPriceError = validBigDecimal(cleanedPrice)
            )
        }
    }

    private fun onUpdateNote(uid: String, note: String) {
        updateEntry(uid) {
            it.copy(
                note = if (note.length > NOTE_LENGTH) note.take(NOTE_LENGTH) else note
            )
        }
    }

    private fun onRemoveEntry(uid: String) {
        val securities = selectedEntries.value.toMutableList()
        val index = securities.indexOfFirst { it.uid == uid }
        if (index < 0) return

        securities.removeAt(index)
        selectedEntries.value = securities.toPersistentList()
    }

    private fun onSaveChangesClicked() = viewModelScope.launch {
        if (state.value.wasError) return@launch
        coroutineScope {
            val securities = selectedEntries.value
            val deferred = securities.map { security ->
                async(Dispatchers.IO) {
                    val lowPrice = if (security.lowPrice.isBlank()) BigDecimal.ZERO
                    else decimalFormatter.parseToBigDecimal(security.lowPrice)

                    val highPrice = if (security.highPrice.isBlank()) BigDecimal.ZERO
                    else decimalFormatter.parseToBigDecimal(security.highPrice)

                    val targetDeviation = if (security.targetDeviation.isBlank()) BigDecimal.ONE
                    else decimalFormatter.parseToBigDecimal(security.targetDeviation)

                    homeApiDataSource.addEntry(
                        portfolioId = portfolioId,
                        name = security.ticker,
                        targetDeviation = targetDeviation,
                        securityUid = security.uid,
                        lowPrice = lowPrice,
                        highPrice = highPrice,
                        note = security.note,
                    )
                }
            }

            try {
                deferred.awaitAll()
            } catch (e: Exception) {
                Log.d(TAG, "Unable to upload securities because of $e")
            } finally {
                //TODO: make batch create. It's stupid this way
                _uiEvents.value = SonarEvent(AddEntriesUiEvent.PopBackSuccess)
            }
        }
    }

    private fun updateEntry(uid: String, action: (EditableEntry) -> EditableEntry): Boolean {
        val securities = selectedEntries.value.toMutableList()
        val index = securities.indexOfFirst { it.uid == uid }
        if (index < 0) return false

        securities[index] = action(securities[index])
        selectedEntries.value = securities.toPersistentList()
        return true
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

        val percent = 1f
        val bdPercent = BigDecimal(percent.toString())
        for ((_, security) in selectDialogAdditions) {
            val priceDeviation = security.price * bdPercent / BigDecimalUtil.HUNDRED
            val newEntry = EditableEntry(
                uid = security.uid,
                ticker = security.ticker,
                price = security.price,
                //TODO: Factor out default target deviation somewhere
                targetDeviation = nf.format(bdPercent),
                lowPrice = nf.format(security.price - priceDeviation),
                lowPriceError = false,
                highPrice = nf.format(security.price + priceDeviation),
                highPriceError = false,
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
