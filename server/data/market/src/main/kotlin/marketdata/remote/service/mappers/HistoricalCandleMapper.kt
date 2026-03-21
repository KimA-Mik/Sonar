package ru.kima.sonar.server.data.market.marketdata.remote.service.mappers

import ru.kima.sonar.common.serverapi.model.HistoricCandle
import ru.kima.sonar.server.data.market.marketdata.util.toInstant
import ru.tinkoff.piapi.contract.v1.Quotation
import java.math.BigDecimal


typealias TinkoffHistoricCandle = ru.tinkoff.piapi.contract.v1.HistoricCandle

fun TinkoffHistoricCandle.toHistoricalCandle() = HistoricCandle(
    open = open.toDouble(),
    high = high.toDouble(),
    low = low.toDouble(),
    close = close.toDouble(),
    volume = volume,
    time = time.toInstant(),
    isComplete = isComplete
)

//TODO: Make sane converter
fun Quotation.toBigDecimal(): BigDecimal = BigDecimal(units).add(BigDecimal(nano).movePointLeft(9))
fun Quotation.toDouble(): Double = units + nano / 1_000_000_000.0
