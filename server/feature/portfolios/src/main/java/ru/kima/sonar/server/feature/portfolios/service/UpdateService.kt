package ru.kima.sonar.server.feature.portfolios.service

import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlinx.datetime.DayOfWeek
import kotlinx.datetime.toLocalDateTime
import org.slf4j.LoggerFactory
import ru.kima.sonar.common.serverapi.events.BoundPriceEvent
import ru.kima.sonar.common.serverapi.events.UnboundPriceEvent
import ru.kima.sonar.common.serverapi.model.LastPrice
import ru.kima.sonar.common.util.MathUtil
import ru.kima.sonar.common.util.valueOr
import ru.kima.sonar.server.common.util.time.DateUtil
import ru.kima.sonar.server.data.market.marketdata.MarketDataRepository
import ru.kima.sonar.server.data.user.datasource.UserDataSource
import ru.kima.sonar.server.data.user.datasource.portfolio.PortfolioDataSource
import ru.kima.sonar.server.data.user.model.UserAndSessions
import ru.kima.sonar.server.data.user.model.portfolio.Portfolio
import ru.kima.sonar.server.data.user.model.portfolio.PortfolioEntry
import ru.kima.sonar.server.data.user.model.portfolio.PortfolioWithEntries
import ru.kima.sonar.server.feature.portfolios.service.model.CacheEntry
import ru.kima.sonar.server.feature.portfolios.service.model.IndicatorsCache
import java.math.BigDecimal
import kotlin.time.Clock
import kotlin.time.Duration.Companion.minutes
import kotlin.time.Duration.Companion.seconds

class UpdateService(
    private val userDataSource: UserDataSource,
    private val portfolioDataSource: PortfolioDataSource,
    private val marketDataRepository: MarketDataRepository,
    private val updateHandler: UpdateServiceUpdateHandler,
) {
    private val logger = LoggerFactory.getLogger(this::class.java)
    private val job = SupervisorJob()
    private val exceptionHandler = CoroutineExceptionHandler { _, exception ->
        logger.error("Update service exception: $exception")
    }

    private val scope = CoroutineScope(job + exceptionHandler)
    private var activeJob: Job? = null

    fun run() {
        if (activeJob != null) return
        activeJob = scope.launch {
            while (isActive) {
                delay(10.seconds)
                checkForUpdates()
//                delay((15L + Random.nextLong(0L..15L)) * TimeUtil.SECOND_MILLIS)
                delayNonWorkingHours(8, 45, 23, 59)
            }
        }
    }

    private val weekends = setOf(DayOfWeek.SATURDAY, DayOfWeek.SUNDAY)

    private suspend fun delayNonWorkingHours(
        startHour: Int,
        startMinute: Int,
        endHour: Int,
        endMinute: Int
    ) {
        var now = Clock.System.now()
        var currentDatetime = now.toLocalDateTime(DateUtil.timezoneMoscow)

        if (currentDatetime.dayOfWeek in weekends) {
            val delay = DateUtil.msUntilStartOfDay(now, DayOfWeek.MONDAY)
            delay(delay)
            now = Clock.System.now()
            currentDatetime = now.toLocalDateTime(DateUtil.timezoneMoscow)
        }

        if (DateUtil.isHoursInRange(
                currentDatetime.hour, currentDatetime.minute,
                startHour, startMinute,
                endHour, endMinute
            )
        ) {
            return
        }

        val msUntilWork = DateUtil.msUntilTime(
            now,
            startHour, startMinute
        )

        delay(msUntilWork)
    }

    private suspend fun checkForUpdates() = coroutineScope {
        val usersDeferred = async { userDataSource.getUsersAndSessions() }
        val portfoliosDeferred = async { portfolioDataSource.getPortfolios() }

        val users = usersDeferred.await().valueOr { return@coroutineScope }
            .filter { user -> user.sessions.any { it.notificationProvider != null } }
            .associateBy { it.user.id }
        val portfolios = portfoliosDeferred.await().valueOr { return@coroutineScope }
            .filter { users.contains(it.portfolio.userId) }

        val uids = buildSet {
            for (portfolio in portfolios) {
                for (entry in portfolio.entries) {
                    add(entry.securityUid)
                }
            }
        }.toList()

        val lastPrices = marketDataRepository.getLastPrices(uids)
            .valueOr { return@coroutineScope }
            .associateBy { it.uid }
        val cache = IndicatorsCache(marketDataRepository)

        handlePortfolios(users, portfolios, lastPrices, cache)
    }

    private suspend fun handlePortfolios(
        users: Map<Long, UserAndSessions>,
        portfolios: List<PortfolioWithEntries>,
        lastPrices: Map<String, LastPrice>,
        cache: IndicatorsCache
    ) {
        val toUpdate = mutableListOf<PortfolioEntry>()
        for (portfolio in portfolios) {
            val user = users[portfolio.portfolio.userId]
            if (user == null) {
                logger.error("Np user for portfolio $portfolio")
                continue
            }
            handlePortfolio(user, portfolio, lastPrices, cache, toUpdate)
        }

        if (toUpdate.isNotEmpty()) {
            val iLoveRaceConditions = mutableListOf<PortfolioEntry>()
            val currentEntries = portfolioDataSource.getPortfolios()
                .valueOr { return }
                .flatMap { it.entries }
                .associateBy { it.id }

            for (update in toUpdate) {
                val current = currentEntries[update.id] ?: continue
                if (update.lowPrice.compareTo(current.lowPrice) != 0) continue
                if (update.highPrice.compareTo(current.highPrice) != 0) continue
                iLoveRaceConditions.add(update)
            }

            portfolioDataSource.updatePortfolioEntries(iLoveRaceConditions)
        }
    }

    private suspend fun handlePortfolio(
        user: UserAndSessions,
        portfolio: PortfolioWithEntries,
        lastPrices: Map<String, LastPrice>,
        cache: IndicatorsCache,
        updatedEntries: MutableList<PortfolioEntry>
    ) {
        for (entry in portfolio.entries) {
            val price = lastPrices[entry.securityUid] ?: continue
            val cacheEntry = cache[entry.securityUid] ?: continue

            val current = handlePrice(user, portfolio.portfolio, entry, price, cacheEntry)

            if (current != entry) {
                updatedEntries.add(current)
            }
        }
    }

    private suspend fun handlePrice(
        user: UserAndSessions,
        portfolio: Portfolio,
        entry: PortfolioEntry,
        lastPrice: LastPrice,
        indicators: CacheEntry,
    ): PortfolioEntry {
        if (!entry.enabled) return entry

        return if (lastPrice.price > entry.highPrice || lastPrice.price < entry.lowPrice) {
            handleUnboundPrice(user, portfolio, entry, indicators, lastPrice)
        } else {
            handleBoundPrice(user, portfolio, entry, indicators, lastPrice)
        }
    }

    private val unboundUpdateIntervalSec = 1.minutes
    private val unboundThreshold = BigDecimal("0.1")

    private suspend fun handleUnboundPrice(
        user: UserAndSessions,
        portfolio: Portfolio,
        entry: PortfolioEntry,
        indicators: CacheEntry,
        lastPrice: LastPrice,
    ): PortfolioEntry {
        if (entry.lastUnboundUpdatePrice.compareTo(lastPrice.price) == 0) {
            return entry
        }

        val now = Clock.System.now()
        if (now - entry.lastUnboundUpdate < unboundUpdateIntervalSec) {
            return entry
        }

        val highDeviation = MathUtil.absolutePercentageDifference(lastPrice.price, entry.highPrice)
        val lowDeviation = MathUtil.absolutePercentageDifference(lastPrice.price, entry.lowPrice)
        val priceType = when {
            lastPrice.price > entry.highPrice && highDeviation > unboundThreshold -> UnboundPriceEvent.PriceType.Above(
                entry.highPrice,
                deviation = highDeviation
            )

            lastPrice.price < entry.lowPrice && lowDeviation > unboundThreshold -> UnboundPriceEvent.PriceType.Below(
                targetPrice = entry.lowPrice,
                deviation = lowDeviation
            )

            else -> null
        }

        if (priceType != null) {
            val event = UpdateServiceEvent.UnboundPriceAlert(
                user = user,
                portfolio = portfolio,
                entry = entry,
                indicators = indicators,
                lastPrice = lastPrice,
                priceType = priceType
            )

            updateHandler.consume(event)
            return entry.copy(
                shouldNotify = true,
                lastUnboundUpdate = now,
                lastUnboundUpdatePrice = lastPrice.price
            )
        }

        return entry
    }

    private suspend fun handleBoundPrice(
        user: UserAndSessions,
        portfolio: Portfolio,
        entry: PortfolioEntry,
        indicators: CacheEntry,
        lastPrice: LastPrice,
    ): PortfolioEntry {
        val currentHighDeviation =
            MathUtil.absolutePercentageDifference(lastPrice.price, entry.highPrice)
        val currentLowDeviation =
            MathUtil.absolutePercentageDifference(lastPrice.price, entry.lowPrice)
        val shouldNotifyHigh = currentHighDeviation < entry.targetDeviation
        val shouldNotifyLow = currentLowDeviation < entry.targetDeviation
        if ((shouldNotifyLow || shouldNotifyHigh) && entry.shouldNotify) {
            val event = UpdateServiceEvent.PriceAlert(
                user = user,
                portfolio = portfolio,
                entry = entry,
                indicators = indicators,
                lastPrice = lastPrice,
                priceType = when {
                    shouldNotifyHigh && shouldNotifyLow -> BoundPriceEvent.PriceType.All(
                        lowTargetPrice = entry.lowPrice,
                        lowDeviation = currentLowDeviation,
                        highTargetPrice = entry.highPrice,
                        highDeviation = currentHighDeviation
                    )

                    shouldNotifyHigh -> BoundPriceEvent.PriceType.High(
                        targetPrice = entry.highPrice,
                        deviation = currentHighDeviation
                    )

                    else -> BoundPriceEvent.PriceType.Low(
                        targetPrice = entry.lowPrice,
                        deviation = currentLowDeviation
                    )
                }
            )

            updateHandler.consume(event)
            return entry.copy(shouldNotify = false)
        } else if (!shouldNotifyHigh && !shouldNotifyLow && !entry.shouldNotify) {
            return entry.copy(shouldNotify = true)
        }

        return entry
    }
}