package ru.kima.sonar.server.data.market.marketdata.remote

import ru.kima.sonar.common.serverapi.model.CandleInterval
import ru.kima.sonar.common.serverapi.model.schema.CandleSource
import ru.kima.sonar.common.serverapi.model.schema.InstrumentExchangeType
import ru.kima.sonar.common.serverapi.model.schema.InstrumentStatus
import ru.kima.sonar.common.serverapi.model.schema.LastPriceType
import ru.kima.sonar.common.serverapi.model.security.Future
import ru.kima.sonar.common.serverapi.model.security.Share
import ru.kima.sonar.server.data.market.marketdata.remote.service.InstrumentsService
import ru.kima.sonar.server.data.market.marketdata.remote.service.MarketDataService
import ru.kima.sonar.server.data.market.marketdata.remote.service.futures
import ru.kima.sonar.server.data.market.marketdata.remote.service.getCandles
import ru.kima.sonar.server.data.market.marketdata.remote.service.getLastPrices
import ru.kima.sonar.server.data.market.marketdata.remote.service.getOrderBook
import ru.kima.sonar.server.data.market.marketdata.remote.service.mappers.toFuture
import ru.kima.sonar.server.data.market.marketdata.remote.service.mappers.toHistoricalCandle
import ru.kima.sonar.server.data.market.marketdata.remote.service.mappers.toLastPrice
import ru.kima.sonar.server.data.market.marketdata.remote.service.mappers.toOrderBook
import ru.kima.sonar.server.data.market.marketdata.remote.service.mappers.toShare
import ru.kima.sonar.server.data.market.marketdata.remote.service.mappers.toTCandleInterval
import ru.kima.sonar.server.data.market.marketdata.remote.service.mappers.toTCandleSource
import ru.kima.sonar.server.data.market.marketdata.remote.service.mappers.toTInstrumentStatus
import ru.kima.sonar.server.data.market.marketdata.remote.service.mappers.toTPriceType
import ru.kima.sonar.server.data.market.marketdata.remote.service.shares
import ru.kima.sonar.server.data.market.marketdata.util.CachedValue
import ru.kima.sonar.server.data.market.marketdata.util.RateLimiter
import ru.tinkoff.piapi.contract.v1.InstrumentsServiceGrpc
import ru.tinkoff.piapi.contract.v1.MarketDataServiceGrpc
import ru.ttech.piapi.core.connector.ConnectorConfiguration
import ru.ttech.piapi.core.connector.ServiceStubFactory
import java.util.Properties
import kotlin.time.Duration.Companion.hours
import kotlin.time.Duration.Companion.minutes
import kotlin.time.Instant

//Make dynamic
private const val TINKOFF_UNARY_REQUEST_LIMIT = 50
private val TINKOFF_RATE_WINDOW = 1.minutes

class TinkoffDataSource(token: String) {
    private val marketDataService: MarketDataService
    private val instrumentsService: InstrumentsService
    private val rateLimiter = RateLimiter(
        limit = TINKOFF_UNARY_REQUEST_LIMIT,
        rateWindow = TINKOFF_RATE_WINDOW
    )

    init {
        val properties = Properties()
        properties.setProperty("token", token)
        val configuration = ConnectorConfiguration
            .loadFromProperties(properties)
        val unaryServiceFactory = ServiceStubFactory.create(configuration)

        marketDataService = unaryServiceFactory.newAsyncService(MarketDataServiceGrpc::newStub)
        instrumentsService = unaryServiceFactory.newAsyncService(InstrumentsServiceGrpc::newStub)
    }

    private val sharesCache =
        mutableMapOf<Pair<InstrumentStatus, InstrumentExchangeType>, CachedValue<List<Share>>>().withDefault {
            CachedValue(cacheLifetime = 12.hours) {
                rateLimiter.rateLimitedResult {
                    return@rateLimitedResult instrumentsService
                        .shares(it.first, it.second)
                        .await().instrumentsList.map { tinkoffShare -> tinkoffShare.toShare() }
                }
            }
        }

    suspend fun shares(
        instrumentStatus: InstrumentStatus,
        instrumentExchangeType: InstrumentExchangeType
    ) = accessSharesCache(instrumentStatus to instrumentExchangeType).getValue()

    private fun accessSharesCache(key: Pair<InstrumentStatus, InstrumentExchangeType>): CachedValue<List<Share>> {
        if (!sharesCache.contains(key)) {
            sharesCache[key] = sharesCache.getValue(key)
        }

        return sharesCache.getValue(key)
    }

    private val futuresCache =
        mutableMapOf<Pair<InstrumentStatus, InstrumentExchangeType>, CachedValue<List<Future>>>().withDefault {
            CachedValue(cacheLifetime = 12.hours) {
                rateLimiter.rateLimitedResult {
                    return@rateLimitedResult instrumentsService
                        .futures(it.first, it.second)
                        .await().instrumentsList.map { tinkoffFuture -> tinkoffFuture.toFuture() }
                }
            }
        }

    private fun accessFuturesCache(key: Pair<InstrumentStatus, InstrumentExchangeType>): CachedValue<List<Future>> {
        if (!futuresCache.contains(key)) {
            futuresCache[key] = futuresCache.getValue(key)
        }

        return futuresCache.getValue(key)
    }

    suspend fun futures(
        instrumentStatus: InstrumentStatus,
        instrumentExchangeType: InstrumentExchangeType
    ) = accessFuturesCache(instrumentStatus to instrumentExchangeType).getValue()

    suspend fun getCandles(
        uid: String,
        from: Instant,
        to: Instant,
        interval: CandleInterval,
        candleSource: CandleSource
    ) = rateLimiter.rateLimitedResult {
        runCatching {
            marketDataService.getCandles(
                uid = uid,
                from = from,
                to = to,
                interval = interval.toTCandleInterval(),
                candleSource = candleSource.toTCandleSource(),
            )
                .await()
                .candlesList
                .map { it.toHistoricalCandle() }
        }
    }

    suspend fun getOrderBook(uid: String, depth: Int) = rateLimiter.rateLimitedResult {
        runCatching {
            marketDataService.getOrderBook(
                uid = uid,
                depth = depth
            )
                .await()
                .toOrderBook()
        }
    }

//    suspend fun findSecurity(ticker: String): Result<Security> = runCatching {
//        val cleanedTicker = ticker.trim()
//        for (instrumentStatus in InstrumentStatus.entries) {
//            for (instrumentExchangeType in InstrumentExchangeType.entries) {
//                val pair = instrumentStatus to instrumentExchangeType
//                accessSharesCache(pair)
//                    .getValue().getOrNull()
//                    ?.find { it.ticker.equals(cleanedTicker, ignoreCase = true) }
//                    ?.let { return@runCatching it }
//
//                accessFuturesCache(pair)
//                    .getValue().getOrNull()
//                    ?.find { it.ticker.equals(cleanedTicker, ignoreCase = true) }
//                    ?.let { return@runCatching it }
//            }
//        }
//        throw ServerExceptions.SecurityNotFoundException(ticker)
//    }

    suspend fun getLastPrices(
        instrumentIds: List<String>,
        instrumentStatus: InstrumentStatus,
        lastPriceType: LastPriceType
    ) = runCatching {
        marketDataService.getLastPrices(
            instrumentIds = instrumentIds,
            instrumentStatus = instrumentStatus.toTInstrumentStatus(),
            lastPriceType = lastPriceType.toTPriceType()
        )
            .await()
            .lastPricesList
            .map { it.toLastPrice() }
    }
}
