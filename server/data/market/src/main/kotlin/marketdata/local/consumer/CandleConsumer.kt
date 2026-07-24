package ru.kima.sonar.server.data.market.marketdata.local.consumer

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.launch
import ru.kima.sonar.server.data.market.marketdata.local.LocalDataSource

internal class CandleConsumer(
    private val dataSource: LocalDataSource,
    private val coroutineScope: CoroutineScope,
) {
    private val eventQueue = MutableSharedFlow<ConsumerEvent>(extraBufferCapacity = 1024)
    suspend fun consume(event: ConsumerEvent) {
        eventQueue.emit(event)
    }

    init {
        coroutineScope.launch {
            eventQueue.collect { event ->
                processEvent(event)
            }
        }
    }

    private suspend fun processEvent(event: ConsumerEvent) {
        dataSource.onTick(event.ticker, event.price.toDouble(), event.time)
    }
}