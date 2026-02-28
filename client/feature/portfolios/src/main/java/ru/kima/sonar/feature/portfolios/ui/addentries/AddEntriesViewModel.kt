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
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import ru.kima.sonar.common.util.isError
import ru.kima.sonar.common.util.isSuccess
import ru.kima.sonar.common.util.map
import ru.kima.sonar.data.homeapi.datasource.HomeApiDataSource
import ru.kima.sonar.feature.portfolios.ui.addentries.event.AddEntriesUserEvent
import ru.kima.sonar.feature.portfolios.ui.addentries.event.SelectSecuritiesDialogUserEvent
import ru.kima.sonar.feature.portfolios.ui.addentries.model.AddableSecurity
import ru.kima.sonar.feature.portfolios.ui.addentries.model.EditableEntry
import ru.kima.sonar.feature.portfolios.ui.addentries.model.mapper.toAddableSecurity
import ru.kima.sonar.feature.portfolios.ui.addentries.state.SelectSecuritiesDialogState

private const val TAG = "AddEntriesViewModel"

@Stable
internal class AddEntriesViewModel(
    private val portfolioId: Long,
    private val homeApiDataSource: HomeApiDataSource
) : ViewModel() {
    private val isLoading = MutableStateFlow(false)
    private val wasError = MutableStateFlow(false)
    private val selectedEntries = MutableStateFlow(persistentListOf<EditableEntry>())

    private val currentEntries = mutableSetOf<String>()

    init {
        refresh()
    }

    fun onEvent(event: AddEntriesUserEvent) {

    }

    fun onSelectDialogEvent(event: SelectSecuritiesDialogUserEvent) {

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

    //Select dialog state
    private val selectDialogQuery = MutableStateFlow("")
    private val selectDialogSecurities = MutableStateFlow(persistentListOf<AddableSecurity>())
    private val selectDialogIsLoading = MutableStateFlow(false)
    val selectDialogState = combine(
        selectDialogQuery,
        selectDialogSecurities,
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
                                share.toAddableSecurity(
                                    selected = selectedEntries.value.any { it.uid == share.uid }
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
                                future.toAddableSecurity(
                                    selected = selectedEntries.value.any { it.uid == future.uid }
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
}