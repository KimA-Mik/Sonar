package ru.kima.sonar.feature.portfolios.ui.details.model.mapper

import ru.kima.sonar.common.serverapi.model.portfolio.PortfolioEntry
import ru.kima.sonar.feature.portfolios.ui.details.model.DisplayItemEntry
import java.math.BigDecimal

private val zeroSequence = sequenceOf(BigDecimal.ZERO)
internal fun PortfolioEntry.toDisplayItemEntry(showNote: Boolean = true) = DisplayItemEntry(
    id = id,
    uid = uid,
    name = name,
    price = price,
    lowPrice = stopLosses
        .asSequence()
        .mapNotNull { it.price }
        .ifEmpty { zeroSequence }
        .minOf { it },
    highPrice = takeProfits
        .asSequence()
        .mapNotNull { it.price }
        .ifEmpty { zeroSequence }
        .maxOf { it },
    note = note,
    showNote = showNote
)