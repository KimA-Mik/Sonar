package ru.kima.sonar.server.data.market.marketdata.remote.service

import kotlinx.coroutines.future.asDeferred
import ru.kima.sonar.server.data.market.marketdata.util.toTimestamp
import ru.tinkoff.piapi.contract.v1.CandleInterval
import ru.tinkoff.piapi.contract.v1.GetCandlesRequest
import ru.tinkoff.piapi.contract.v1.GetLastPricesRequest
import ru.tinkoff.piapi.contract.v1.GetOrderBookRequest.newBuilder
import ru.tinkoff.piapi.contract.v1.InstrumentStatus
import ru.tinkoff.piapi.contract.v1.LastPriceType
import ru.tinkoff.piapi.contract.v1.MarketDataServiceGrpc
import ru.ttech.piapi.core.connector.AsyncStubWrapper
import kotlin.time.Instant

typealias MarketDataService = AsyncStubWrapper<MarketDataServiceGrpc.MarketDataServiceStub>


/**
 * Цены последних сделок по инструментам.
 * @see <a href="https://developer.tbank.ru/invest/services/quotes/marketdata#getlastprices">Reference</a>
 */
fun MarketDataService.getLastPrices(
    instrumentIds: List<String>,
    instrumentStatus: InstrumentStatus,
    lastPriceType: LastPriceType,
) = callAsyncMethod { stub, observer ->
    val builder = GetLastPricesRequest.newBuilder()
    instrumentIds.forEach { builder.addInstrumentId(it) }
    builder.setInstrumentStatus(instrumentStatus)
    builder.setLastPriceType(lastPriceType)

    stub.getLastPrices(builder.build(), observer)
}.asDeferred()

/**
 * Исторические свечи по инструменту.
 * @see <a href="https://developer.tbank.ru/invest/services/quotes/marketdata#getcandles">Reference></a>
 */
fun MarketDataService.getCandles(
    uid: String,
    from: Instant,
    to: Instant,
    interval: CandleInterval,
    candleSource: GetCandlesRequest.CandleSource,
) = callAsyncMethod { stub, observer ->
    val builder = GetCandlesRequest.newBuilder()
        .setInstrumentId(uid)
        .setFrom(from.toTimestamp())
        .setTo(to.toTimestamp())
        .setInterval(interval)
        .setCandleSourceType(candleSource)

    stub.getCandles(builder.build(), observer)
}.asDeferred()

const val ORDER_BOOK_DEPTH = 20

/**
 * Стакан по инструменту.
 * @see <a href="https://developer.tbank.ru/invest/services/quotes/marketdata#getorderbook">Reference</a>
 */
fun MarketDataService.getOrderBook(
    uid: String,
    depth: Int = ORDER_BOOK_DEPTH
) = callAsyncMethod { stub, observer ->
    val builder = newBuilder()
        .setInstrumentId(uid)
        .setDepth(depth)

    stub.getOrderBook(builder.build(), observer)
}.asDeferred()
