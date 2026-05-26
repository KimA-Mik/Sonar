package ru.kima.sonar.feature.portfolios.ui.details

import android.util.Log
import androidx.compose.runtime.Stable
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf
import kotlinx.collections.immutable.toPersistentList
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import ru.kima.sonar.common.ui.event.SonarEvent
import ru.kima.sonar.common.ui.util.DecimalFormatter
import ru.kima.sonar.common.util.isSuccess
import ru.kima.sonar.common.util.valueOr
import ru.kima.sonar.data.homeapi.datasource.HomeApiDataSource
import ru.kima.sonar.feature.portfolios.ui.components.editentry.EditEntryComponent
import ru.kima.sonar.feature.portfolios.ui.components.editentry.addStopLoss
import ru.kima.sonar.feature.portfolios.ui.components.editentry.addTakeProfit
import ru.kima.sonar.feature.portfolios.ui.components.editentry.deleteStopLoss
import ru.kima.sonar.feature.portfolios.ui.components.editentry.deleteTakeProfit
import ru.kima.sonar.feature.portfolios.ui.components.editentry.toComponents
import ru.kima.sonar.feature.portfolios.ui.components.editentry.toPortfolioEntries
import ru.kima.sonar.feature.portfolios.ui.components.editentry.updateStopLossNote
import ru.kima.sonar.feature.portfolios.ui.components.editentry.updateStopLossPrice
import ru.kima.sonar.feature.portfolios.ui.components.editentry.updateTakeProfitNote
import ru.kima.sonar.feature.portfolios.ui.components.editentry.updateTakeProfitPrice
import ru.kima.sonar.feature.portfolios.ui.components.editentry.updateTargetDeviation
import ru.kima.sonar.feature.portfolios.ui.details.event.EditEntryUiEvent
import ru.kima.sonar.feature.portfolios.ui.details.event.EditEntryUserEvent
import ru.kima.sonar.feature.portfolios.ui.details.state.EditEntryDialogState
import java.text.NumberFormat

private const val TAG = "EditEntryDialogViewModel"

@Stable
internal class EditEntryDialogViewModel(
    private val entryId: Long,
    private val homeApi: HomeApiDataSource,
) : ViewModel() {
    private val numberFormat = NumberFormat.getInstance()
    private val decimalFormatter = DecimalFormatter()
    private val sonarFormatter = DecimalFormatter()

    private val isLoading = MutableStateFlow(false)
    private val name = MutableStateFlow("")
    private val components = MutableStateFlow<ImmutableList<EditEntryComponent>>(persistentListOf())

    private val _uiEvents = MutableStateFlow(SonarEvent<EditEntryUiEvent>())
    val uiEvents = _uiEvents.asStateFlow()

    init {
        load()
    }

    private var uid = ""
    private fun load() = viewModelScope.launch {
        isLoading.value = true
        val result = homeApi.getPortfolioEntry(entryId)
        if (result.isSuccess()) {
            name.value = result.data.name
            uid = result.data.uid
            components.value = listOf(result.data).toComponents()
        } else {
            Log.d(TAG, "Failed to load entry with id $entryId: ${result.data}")
        }
        isLoading.value = false
    }

    val state = combine(
        isLoading, name, components
    ) { isLoading, name, components ->
        EditEntryDialogState(
            isLoading = isLoading,
            name = name,
            components = components
        )
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), EditEntryDialogState.default())

    fun onEvent(event: EditEntryUserEvent) {
        when (event) {
            EditEntryUserEvent.ApplyChangesClicked -> onApplyChangesClicked()
            EditEntryUserEvent.AddStopLoss -> onAddStopLoss()
            EditEntryUserEvent.AddTakeProfit -> onAddTakeProfit()
            is EditEntryUserEvent.DeleteStopLoss -> onDeleteStopLoss(event.key)
            is EditEntryUserEvent.DeleteTakeProfit -> onDeleteTakeProfit(event.key)
            is EditEntryUserEvent.UpdateTargetDeviation ->
                onUpdateTargetDeviation(event.key, event.deviation)

            is EditEntryUserEvent.StopLossNoteChange ->
                onStopLossNoteChange(event.key, event.note)

            is EditEntryUserEvent.StopLossPriceChange ->
                onStopLossPriceChange(event.key, event.price)

            is EditEntryUserEvent.TakeProfitNoteChange ->
                onTakeProfitNoteChange(event.key, event.note)

            is EditEntryUserEvent.TakeProfitPriceChange ->
                onTakeProfitPriceChange(event.key, event.price)
        }
    }

    private fun onAddStopLoss() {
        val temp = components.value.toMutableList()
        temp.addStopLoss(uid)
        components.value = temp.toPersistentList()
    }

    private fun onAddTakeProfit() {
        val temp = components.value.toMutableList()
        temp.addTakeProfit(uid)
        components.value = temp.toPersistentList()
    }

    private fun onStopLossNoteChange(key: String, note: String) {
        val temp = components.value.toMutableList()
        temp.updateStopLossNote(key, note)
        components.value = temp.toPersistentList()
    }

    private fun onStopLossPriceChange(key: String, price: String) {
        val temp = components.value.toMutableList()
        temp.updateStopLossPrice(key, price, decimalFormatter)
        components.value = temp.toPersistentList()
    }

    private fun onDeleteStopLoss(key: String) {
        val temp = components.value.toMutableList()
        temp.deleteStopLoss(key)
        components.value = temp.toPersistentList()
    }

    private fun onTakeProfitNoteChange(key: String, note: String) {
        val temp = components.value.toMutableList()
        temp.updateTakeProfitNote(key, note)
        components.value = temp.toPersistentList()
    }

    private fun onTakeProfitPriceChange(key: String, price: String) {
        val temp = components.value.toMutableList()
        temp.updateTakeProfitPrice(key, price, decimalFormatter)
        components.value = temp.toPersistentList()
    }

    private fun onDeleteTakeProfit(key: String) {
        val temp = components.value.toMutableList()
        temp.deleteTakeProfit(key)
        components.value = temp.toPersistentList()
    }

    private fun onUpdateTargetDeviation(key: String, deviation: String) {
        val temp = components.value.toMutableList()
        temp.updateTargetDeviation(key, deviation, sonarFormatter)
        components.value = temp.toPersistentList()
    }

    private fun onApplyChangesClicked() {
        val entry = components.value.toPortfolioEntries()
            .valueOr { return }.firstOrNull() ?: return

        viewModelScope.launch {
            val res = homeApi.updateEntry(
                entryId = entryId,
                name = entry.name,
                targetDeviation = entry.targetDeviation,
                stopLosses = entry.stopLosses,
                takeProfits = entry.takeProfits
            )

            if (res.isSuccess()) {
                _uiEvents.value = SonarEvent(EditEntryUiEvent.Success)
            } else {
                Log.d(TAG, "Failed to apply changes for entry with id $entryId: ${res.data}")
            }
        }
    }
}