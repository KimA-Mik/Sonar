package ru.kima.sonar.feature.portfolios.ui.details

import android.icu.text.DecimalFormatSymbols
import android.util.Log
import androidx.compose.runtime.Stable
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import ru.kima.sonar.common.serverapi.util.NOTE_LENGTH
import ru.kima.sonar.common.ui.event.SonarEvent
import ru.kima.sonar.common.ui.util.DecimalFormatter
import ru.kima.sonar.common.util.combine
import ru.kima.sonar.common.util.isSuccess
import ru.kima.sonar.data.homeapi.datasource.HomeApiDataSource
import ru.kima.sonar.feature.portfolios.ui.details.event.EditEntryUiEvent
import ru.kima.sonar.feature.portfolios.ui.details.event.EditEntryUserEvent
import ru.kima.sonar.feature.portfolios.ui.details.state.EditEntryDialogState
import java.math.BigDecimal
import java.text.NumberFormat

private const val TAG = "EditEntryDialogViewModel"

@Stable
internal class EditEntryDialogViewModel(
    private val entryId: Long,
    private val homeApi: HomeApiDataSource,
    private val savedStateHandle: SavedStateHandle
) : ViewModel() {
    private val numberFormat = NumberFormat.getInstance()
    private val sonarFormatter = DecimalFormatter()

    private val isLoading = MutableStateFlow(false)

    private val _uiEvents = MutableStateFlow(SonarEvent<EditEntryUiEvent>())
    val uiEvents = _uiEvents.asStateFlow()

    init {
        if (!savedStateHandle.contains(ENTRY_ID_KEY)) {
            load()
        }
    }

    private fun load() = viewModelScope.launch {
        isLoading.value = true
        val result = homeApi.getPortfolioEntry(entryId)
        if (result.isSuccess()) {
            val entry = result.data
            savedStateHandle[ENTRY_ID_KEY] = entryId
            savedStateHandle[NAME_KEY] = entry.name
            savedStateHandle[PRICE_KEY] = entry.price
            savedStateHandle[LOW_PRICE_KEY] = numberFormat.format(entry.lowPrice)
            savedStateHandle[HIGH_PRICE_KEY] = numberFormat.format(entry.highPrice)
            savedStateHandle[NOTE_KEY] = entry.note
        } else {
            Log.d(TAG, "Failed to load entry with id $entryId: ${result.data}")
        }
        isLoading.value = false
    }

    val state = combine(
        isLoading,
        savedStateHandle.getStateFlow(NAME_KEY, ""),
        savedStateHandle.getStateFlow(PRICE_KEY, BigDecimal.ZERO),
        savedStateHandle.getStateFlow(LOW_PRICE_KEY, ""),
        savedStateHandle.getStateFlow(HIGH_PRICE_KEY, ""),
        savedStateHandle.getStateFlow(NOTE_KEY, "")
    ) { isLoading, name, price, lowPrice, highPrice, note ->
        EditEntryDialogState(
            isLoading = isLoading,
            name = name,
            price = price,
            lowPrice = lowPrice,
            highPrice = highPrice,
            note = note
        )
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), EditEntryDialogState.default())

    fun onEvent(event: EditEntryUserEvent) {
        when (event) {
            is EditEntryUserEvent.HighPriceUpdated -> onHighPriceUpdated(event.highPrice)
            is EditEntryUserEvent.LowPriceUpdated -> onLowPriceUpdated(event.lowPrice)
            is EditEntryUserEvent.NoteUpdated -> onNoteUpdated(event.note)
            EditEntryUserEvent.ApplyChangesClicked -> onApplyChangesClicked()
        }
    }

    private fun onHighPriceUpdated(highPrice: String) {
        savedStateHandle[HIGH_PRICE_KEY] = sonarFormatter.cleanup(highPrice)
    }

    private fun onLowPriceUpdated(lowPrice: String) {
        savedStateHandle[LOW_PRICE_KEY] = sonarFormatter.cleanup(lowPrice)
    }

    private fun onNoteUpdated(note: String) {
        savedStateHandle[NOTE_KEY] = if (note.length > NOTE_LENGTH) note.take(NOTE_LENGTH) else note
    }

    private fun onApplyChangesClicked() {
        val symbols: DecimalFormatSymbols = DecimalFormatSymbols.getInstance()
        val decimalSeparator = symbols.decimalSeparator
        val lowPrice = savedStateHandle.get<String>(LOW_PRICE_KEY) ?: ""
        val highPrice = savedStateHandle.get<String>(HIGH_PRICE_KEY) ?: ""
        val lowPriceBd = if (lowPrice.isBlank()) {
            BigDecimal.ZERO
        } else {
            //TODO: factor out instead of copypasting
            BigDecimal(
                lowPrice
                    .replace(decimalSeparator, '.')
                    .replace(" ", "")
            )
        }
        val highPriceBd = if (highPrice.isBlank()) {
            BigDecimal.ZERO
        } else {
            BigDecimal(
                highPrice
                    .replace(decimalSeparator, '.')
                    .replace(" ", "")
            )
        }

        viewModelScope.launch {
            val res = homeApi.updateEntry(
                entryId = entryId,
                name = savedStateHandle.get<String>(NAME_KEY) ?: "",
                lowPrice = lowPriceBd,
                highPrice = highPriceBd,
                note = savedStateHandle.get<String>(NOTE_KEY) ?: ""
            )

            if (res.isSuccess()) {
                _uiEvents.value = SonarEvent(EditEntryUiEvent.Success)
            } else {
                Log.d(TAG, "Failed to apply changes for entry with id $entryId: ${res.data}")
            }
        }
    }

    companion object {
        private const val ENTRY_ID_KEY = "entryId"
        private const val NAME_KEY = "name"
        private const val PRICE_KEY = "price"
        private const val LOW_PRICE_KEY = "lowPrice"
        private const val HIGH_PRICE_KEY = "highPrice"
        private const val NOTE_KEY = "note"
    }
}