package ru.kima.sonar.feature.portfolios.ui.components.editentry

import ru.kima.sonar.common.serverapi.util.NOTE_LENGTH
import ru.kima.sonar.common.ui.util.DecimalFormatter
import kotlin.math.max

internal fun List<EditEntryComponent>.findTitle(uid: String) = indexOfFirst {
    it is EditEntryComponent.Title && it.uid == uid
}

internal fun MutableList<EditEntryComponent>.balanceEntry(uid: String) {
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

internal fun MutableList<EditEntryComponent>.deleteEntry(uid: String) {
    removeAll { it.uid == uid }
}

internal fun MutableList<EditEntryComponent>.addStopLoss(uid: String) {
    var maxIndex = 0
    for (component in this) {
        if (component is EditEntryComponent.StopLoss && component.uid == uid) {
            maxIndex = max(maxIndex, EditEntryComponent.getIndex(component.key))
        }
    }

    add(
        EditEntryComponent.StopLoss(
            EditEntryComponent.StopLoss.generateKey(uid, maxIndex + 1),
            uid = uid,
            index = maxIndex + 1,
            price = "",
            note = "",
            id = 0,
        )
    )

    balanceEntry(uid)
}

internal fun MutableList<EditEntryComponent>.addTakeProfit(uid: String) {
    var maxIndex = 0
    for (component in this) {
        if (component is EditEntryComponent.TakeProfit && component.uid == uid) {
            maxIndex = max(maxIndex, EditEntryComponent.getIndex(component.key))
        }
    }

    add(
        EditEntryComponent.TakeProfit(
            EditEntryComponent.TakeProfit.generateKey(uid, maxIndex + 1),
            uid = uid,
            index = maxIndex + 1,
            price = "",
            note = "",
            id = 0,
        )
    )

    balanceEntry(uid)
}

internal fun MutableList<EditEntryComponent>.updateStopLossNote(key: String, note: String) {
    val index = indexOfFirst { it.key == key }
    if (index < 0) return
    val stopLoss = get(index)
    if (stopLoss !is EditEntryComponent.StopLoss) return

    this[index] = stopLoss.copy(
        note = if (note.length <= NOTE_LENGTH) note else note.take(NOTE_LENGTH)
    )
}

internal fun MutableList<EditEntryComponent>.updateStopLossPrice(
    key: String,
    price: String,
    decimalFormatter: DecimalFormatter = DecimalFormatter()
) {
    val index = indexOfFirst { it.key == key }
    if (index < 0) return
    val stopLoss = get(index)
    if (stopLoss !is EditEntryComponent.StopLoss) return

    this[index] = stopLoss.copy(price = decimalFormatter.cleanup(price))
}

internal fun MutableList<EditEntryComponent>.deleteStopLoss(key: String) {
    val i = indexOfFirst { it.key == key }
    if (i < 0) return
    val stopLoss = get(i)
    if (stopLoss !is EditEntryComponent.StopLoss) return
    removeAt(i)
    balanceEntry(stopLoss.uid)
}

internal fun MutableList<EditEntryComponent>.updateTakeProfitNote(key: String, note: String) {
    val index = indexOfFirst { it.key == key }
    if (index < 0) return
    val takeProfit = get(index)
    if (takeProfit !is EditEntryComponent.TakeProfit) return

    this[index] = takeProfit.copy(
        note = if (note.length <= NOTE_LENGTH) note else note.take(NOTE_LENGTH)
    )
}

internal fun MutableList<EditEntryComponent>.updateTakeProfitPrice(
    key: String,
    price: String,
    decimalFormatter: DecimalFormatter = DecimalFormatter()
) {
    val index = indexOfFirst { it.key == key }
    if (index < 0) return
    val stopLoss = get(index) as? EditEntryComponent.TakeProfit ?: return

    this[index] = stopLoss.copy(price = decimalFormatter.cleanup(price))
}

internal fun MutableList<EditEntryComponent>.deleteTakeProfit(key: String) {
    val i = indexOfFirst { it.key == key }
    if (i < 0) return
    val takeProfit = get(i)
    if (takeProfit !is EditEntryComponent.TakeProfit) return
    removeAt(i)
    balanceEntry(takeProfit.uid)
}

internal fun MutableList<EditEntryComponent>.updateTargetDeviation(
    key: String,
    targetDeviation: String,
    decimalFormatter: DecimalFormatter = DecimalFormatter()
) {
    val index = indexOfFirst { it.key == key }
    if (index < 0) return
    val title = get(index) as? EditEntryComponent.Title ?: return

    this[index] = title.copy(targetDeviation = decimalFormatter.cleanup(targetDeviation))
}