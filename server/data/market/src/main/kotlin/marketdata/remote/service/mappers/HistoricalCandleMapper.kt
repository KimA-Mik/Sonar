package ru.kima.sonar.server.data.market.marketdata.remote.service.mappers

import ru.kima.sonar.common.serverapi.model.HistoricCandle
import ru.kima.sonar.server.data.market.marketdata.util.toInstant
import ru.tinkoff.piapi.contract.v1.Quotation
import java.math.BigDecimal


typealias TinkoffHistoricCandle = ru.tinkoff.piapi.contract.v1.HistoricCandle

fun TinkoffHistoricCandle.toHistoricalCandle() = HistoricCandle(
    open = open.toBigDecimal(),
    high = high.toBigDecimal(),
    low = low.toBigDecimal(),
    close = close.toBigDecimal(),
    volume = volume,
    time = time.toInstant(),
    isComplete = isComplete
)

//TODO: Make sane converter
fun Quotation.toBigDecimal(): BigDecimal = BigDecimal(units).add(BigDecimal(nano).movePointLeft(9))
