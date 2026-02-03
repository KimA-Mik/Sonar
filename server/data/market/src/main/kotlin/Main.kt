package ru.kima.sonar.server.data.market

import ru.tinkoff.piapi.contract.v1.SubscriptionInterval
import ru.ttech.piapi.core.connector.ServiceStubFactory
import ru.ttech.piapi.core.connector.streaming.StreamManagerFactory
import ru.ttech.piapi.core.connector.streaming.StreamServiceStubFactory
import ru.ttech.piapi.core.impl.marketdata.subscription.CandleSubscriptionSpec
import ru.ttech.piapi.core.impl.marketdata.subscription.Instrument
import ru.ttech.piapi.core.impl.marketdata.wrapper.CandleWrapper
import ru.ttech.piapi.storage.jdbc.config.JdbcConfiguration
import ru.ttech.piapi.storage.jdbc.repository.CandlesJdbcRepository
import java.lang.module.Configuration
import java.util.concurrent.Executors
import javax.sql.DataSource


//TIP To <b>Run</b> code, press <shortcut actionId="Run"/> or
// click the <icon src="AllIcons.Actions.Execute"/> icon in the gutter.
fun main() {
    val connectorConfiguration = Configuration.empty()
    val unaryServiceFactory = ServiceStubFactory.create(connectorConfiguration)
    val streamServiceFactory = StreamServiceStubFactory.create(unaryServiceFactory)
    val streamManagerFactory = StreamManagerFactory.create(streamServiceFactory)
    val executorService = Executors.newCachedThreadPool()
    val scheduledExecutorService = Executors.newSingleThreadScheduledExecutor()
    val jdbcConfiguration = JdbcConfiguration(createDataSource(), "trading", "candles")
    val candlesRepository = CandlesJdbcRepository(jdbcConfiguration)
    val marketDataStreamManager =
        streamManagerFactory.newMarketDataStreamManager(executorService, scheduledExecutorService)
    marketDataStreamManager.subscribeCandles(
        mutableSetOf(
            Instrument(
                "87db07bc-0e02-4e29-90bb-05e8ef791d7b",
                SubscriptionInterval.SUBSCRIPTION_INTERVAL_ONE_MINUTE
            )
        ),
        CandleSubscriptionSpec()
    ) { candle: CandleWrapper? -> candlesRepository.save(candle!!.original) }
    marketDataStreamManager.start()
}

private fun createDataSource(): DataSource {
    TODO("Not yet implemented")
}