package ru.kima.sonar.server.data.market.marketdata.remote

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import ru.kima.sonar.common.serverapi.model.CandleInterval
import ru.kima.sonar.common.serverapi.model.LastPrice
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
import ru.kima.sonar.server.data.market.marketdata.util.RateLimiter
import ru.tinkoff.piapi.contract.v1.InstrumentsServiceGrpc
import ru.tinkoff.piapi.contract.v1.MarketDataServiceGrpc
import ru.ttech.piapi.core.connector.ConnectorConfiguration
import ru.ttech.piapi.core.connector.ServiceStubFactory
import ru.ttech.piapi.core.connector.streaming.StreamManagerFactory
import ru.ttech.piapi.core.connector.streaming.StreamServiceStubFactory
import ru.ttech.piapi.core.connector.streaming.listeners.OnNextListener
import ru.ttech.piapi.core.impl.marketdata.MarketDataStreamManager
import ru.ttech.piapi.core.impl.marketdata.subscription.Instrument
import ru.ttech.piapi.core.impl.marketdata.wrapper.LastPriceWrapper
import java.util.Properties
import java.util.concurrent.Executors
import kotlin.coroutines.CoroutineContext
import kotlin.time.Duration
import kotlin.time.Duration.Companion.days
import kotlin.time.Duration.Companion.minutes
import kotlin.time.Duration.Companion.seconds
import kotlin.time.Instant

//Make dynamic
private const val TINKOFF_UNARY_REQUEST_LIMIT = 50
private val TINKOFF_RATE_WINDOW = 1.minutes

class TinkoffDataSource(token: String) {
    private val marketDataService: MarketDataService
    private val instrumentsService: InstrumentsService
    private val marketDataStreamManager: MarketDataStreamManager
    private val rateLimiter = RateLimiter(
        limit = TINKOFF_UNARY_REQUEST_LIMIT,
        rateWindow = TINKOFF_RATE_WINDOW
    )

    private val rootJob = SupervisorJob()
    private val coroutineScope = CoroutineScope(rootJob)

    private var sharesMap = mapOf<String, Share>()
    private var futuresMap = mapOf<String, Future>()
    private var _sharesLastPrices = mutableMapOf<String, LastPrice>()
    private var _futuresLastPrices = mutableMapOf<String, LastPrice>()

    //Waiting for Explicit Backing Fields to become stable to remove this boilerplate
    private val sharesStateFlow = MutableStateFlow<List<Share>>(emptyList())
    val shares = sharesStateFlow.asStateFlow()
    private val futuresStateFlow = MutableStateFlow<List<Future>>(emptyList())
    val futures = futuresStateFlow.asStateFlow()
    private val sharesLastPricesStateFlow = MutableStateFlow<List<LastPrice>>(emptyList())
    val sharesLastPrices = sharesLastPricesStateFlow.asStateFlow()
    private val futuresLastPricesStateFlow = MutableStateFlow<List<LastPrice>>(emptyList())
    val futuresLastPrices = futuresLastPricesStateFlow.asStateFlow()

    init {
        val properties = Properties().apply { setProperty("token", token) }
        val configuration = ConnectorConfiguration.loadFromProperties(properties)
        val unaryServiceFactory = ServiceStubFactory.create(configuration)

        marketDataService = unaryServiceFactory.newAsyncService(MarketDataServiceGrpc::newStub)
        instrumentsService = unaryServiceFactory.newAsyncService(InstrumentsServiceGrpc::newStub)

        val streamServiceFactory = StreamServiceStubFactory.create(unaryServiceFactory)
        val streamManagerFactory = StreamManagerFactory.create(streamServiceFactory)
        marketDataStreamManager = streamManagerFactory.newMarketDataStreamManager(
            Executors.newCachedThreadPool(),
            Executors.newSingleThreadScheduledExecutor()
        )

        runUpdates()
    }

    private fun periodicUpdate(
        delay: Duration,
        context: CoroutineContext = Dispatchers.IO,
        action: suspend () -> Unit
    ) {
        coroutineScope.launch(context) {
            while (isActive) {
                action()
                delay(delay)
            }
        }
    }

    private fun runUpdates() {
        periodicUpdate(1.days) {
            updateShares()
            sharesStateFlow.value = sharesMap.values.toList()
        }
        periodicUpdate(1.days) {
            updateFutures()
            futuresStateFlow.value = futuresMap.values.toList()
        }
        //TODO: make it sleep at night, when the market is closed
        periodicUpdate(10.seconds) {
            sharesLastPricesStateFlow.value = _sharesLastPrices.values.toList()
            futuresLastPricesStateFlow.value = _futuresLastPrices.values.toList()
        }
    }

    private val lastPriceListener = OnNextListener<LastPriceWrapper> {
        if (sharesMap.contains(it.instrumentUid)) {
            _sharesLastPrices[it.instrumentUid] = it.toLastPrice()
        } else if (futuresMap.contains(it.instrumentUid)) {
            _futuresLastPrices[it.instrumentUid] = it.toLastPrice()
        }
    }

    private suspend fun updateShares() {
        val sharesDeferred = instrumentsService.shares(
            InstrumentStatus.INSTRUMENT_STATUS_BASE,
            InstrumentExchangeType.INSTRUMENT_EXCHANGE_UNSPECIFIED
        )

        val newShares = runCatching { sharesDeferred.await() }
            .getOrNull()?.instrumentsList?.map { it.toShare() } ?: return

        val oldSharesMap = sharesMap.toMutableMap()
        val newSharesExtra = mutableMapOf<String, Share>()

        for (share in newShares) {
            if (oldSharesMap.contains(share.uid)) {
                oldSharesMap.remove(share.uid)
            } else {
                newSharesExtra[share.uid] = share
            }
        }

        var updated = false
        if (oldSharesMap.isNotEmpty()) {
            val toUnsubscribe = buildSet {
                oldSharesMap.keys.forEach { add(Instrument(it)) }
            }

            marketDataStreamManager.unsubscribeLastPrices(toUnsubscribe)
            updated = true
        }

        if (newSharesExtra.isNotEmpty()) {
            val toSubscribe = buildSet {
                newSharesExtra.keys.forEach { add(Instrument(it)) }
            }

            getLastPrices(
                newSharesExtra.keys.toList(),
                InstrumentStatus.INSTRUMENT_STATUS_BASE,
                LastPriceType.LAST_PRICE_EXCHANGE
            ).getOrNull()?.forEach { _sharesLastPrices[it.uid] = it }
            marketDataStreamManager.subscribeLastPrices(toSubscribe, lastPriceListener)
            updated = true
        }

        if (updated) {
            sharesMap = newShares.associateBy { it.uid }
        }
    }

    private suspend fun updateFutures() {
        val futuresDeferred = instrumentsService.futures(
            InstrumentStatus.INSTRUMENT_STATUS_BASE,
            InstrumentExchangeType.INSTRUMENT_EXCHANGE_UNSPECIFIED
        )

        val newFutures = runCatching { futuresDeferred.await() }
            .getOrNull()?.instrumentsList?.map { it.toFuture() } ?: return

        val oldFuturesMap = futuresMap.toMutableMap()
        val newFuturesExtra = mutableMapOf<String, Future>()

        for (future in newFutures) {
            if (oldFuturesMap.contains(future.uid)) {
                oldFuturesMap.remove(future.uid)
            } else {
                newFuturesExtra[future.uid] = future
            }
        }

        var updated = false
        if (oldFuturesMap.isNotEmpty()) {
            val toUnsubscribe = buildSet {
                oldFuturesMap.keys.forEach { add(Instrument(it)) }
            }

            marketDataStreamManager.unsubscribeLastPrices(toUnsubscribe)
            updated = true
        }

        if (newFuturesExtra.isNotEmpty()) {
            val toSubscribe = buildSet {
                newFuturesExtra.keys.forEach { add(Instrument(it)) }
            }

            getLastPrices(
                newFuturesExtra.keys.toList(),
                InstrumentStatus.INSTRUMENT_STATUS_BASE,
                LastPriceType.LAST_PRICE_EXCHANGE
            ).getOrNull()?.forEach { _futuresLastPrices[it.uid] = it }
            marketDataStreamManager.subscribeLastPrices(toSubscribe, lastPriceListener)
            updated = true
        }

        if (updated) {
            futuresMap = newFutures.associateBy { it.uid }
        }
    }


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

    fun shutdown() {
        marketDataStreamManager.shutdown()
        rootJob.cancel()
    }
}
