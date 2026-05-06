package ru.kima.sonar.feature.portfolios.ui.components.editentry

import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.toImmutableList
import ru.kima.sonar.common.serverapi.model.portfolio.PortfolioEntry
import ru.kima.sonar.common.serverapi.model.portfolio.StopLoss
import ru.kima.sonar.common.serverapi.model.portfolio.TakeProfit
import ru.kima.sonar.common.ui.util.DecimalFormatter
import ru.kima.sonar.common.ui.util.formatLocalized
import java.math.BigDecimal
import kotlin.math.max

internal fun List<PortfolioEntry>.toComponents(): ImmutableList<EditEntryComponent> {
    val components = mutableListOf<EditEntryComponent>()
    for (entry in this) {
        components.add(
            EditEntryComponent.Title(
                key = EditEntryComponent.Title.generateKey(entry.uid),
                uid = entry.uid,
                title = entry.name,
                price = entry.price,
                targetDeviation = entry.targetDeviation.toString(),
                id = entry.id
            )
        )

        val height = max(entry.stopLosses.size, entry.takeProfits.size)
        val slSize = entry.stopLosses.size
        val tpSize = entry.takeProfits.size
        var paddingCount = 0
        for (row in 0..height) {
            val slComponent = when {
                row < slSize -> {
                    val stopLoss = entry.stopLosses[row]
                    EditEntryComponent.StopLoss(
                        key = EditEntryComponent.StopLoss.generateKey(entry.uid, row),
                        uid = entry.uid,
                        index = row + 1,
                        price = stopLoss.price?.formatLocalized(3) ?: "",
                        note = stopLoss.note
                    )
                }

                row == slSize -> EditEntryComponent.AddStopLoss(entry.uid)
                else -> EditEntryComponent.Padding(
                    key = EditEntryComponent.Padding.generateKey(entry.uid, paddingCount++),
                    uid = entry.uid
                )
            }

            val tpComponent = when {
                row < tpSize -> {
                    val takeProfit = entry.takeProfits[row]
                    EditEntryComponent.TakeProfit(
                        key = EditEntryComponent.TakeProfit.generateKey(entry.uid, row),
                        uid = entry.uid,
                        index = row + 1,
                        price = takeProfit.price?.formatLocalized(3) ?: "",
                        note = takeProfit.note
                    )
                }

                row == tpSize -> EditEntryComponent.AddTakeProfit(entry.uid)
                else -> EditEntryComponent.Padding(
                    key = EditEntryComponent.Padding.generateKey(entry.uid, paddingCount++),
                    uid = entry.uid
                )
            }

            components.add(slComponent)
            components.add(tpComponent)
        }
    }

    return components.toImmutableList()
}

internal fun List<EditEntryComponent>.toPortfolioEntries() = buildList<PortfolioEntry> {
    for (i in 0 until lastIndex) {
        val entry = getPortfolioEntry(i) ?: continue
        add(entry)
    }
}

private fun List<EditEntryComponent>.getPortfolioEntry(start: Int): PortfolioEntry? {
    val title = getOrNull(start)
    if (title !is EditEntryComponent.Title) return null
    val stopLosses = mutableListOf<StopLoss>()
    val takeProfits = mutableListOf<TakeProfit>()

    val df = DecimalFormatter()
    for (i in start + 1 until size) {
        when (val component = get(i)) {
            is EditEntryComponent.StopLoss -> stopLosses.add(
                StopLoss(
                    id = component.id,
                    entryId = title.id,
                    price = if (component.price.isBlank()) null else df.parseToBigDecimal(
                        component.price
                    ),
                    note = component.note
                )
            )

            is EditEntryComponent.TakeProfit -> takeProfits.add(
                TakeProfit(
                    id = component.id,
                    entryId = title.id,
                    price = if (component.price.isBlank()) null else df.parseToBigDecimal(
                        component.price
                    ),
                    note = component.note
                )
            )

            is EditEntryComponent.Title -> break
            else -> continue
        }
    }

    return PortfolioEntry(
        id = title.id,
        uid = title.uid,
        name = title.title,
        targetDeviation = df.parseToBigDecimal(title.targetDeviation),
        price = title.price,
        lowPrice = BigDecimal.ZERO,
        highPrice = BigDecimal.ZERO,
        note = "",
        stopLosses = stopLosses,
        takeProfits = takeProfits
    )
}