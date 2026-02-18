package ru.kima.sonar.server.data.market.marketdata.remote.service.mappers

import ru.kima.sonar.common.serverapi.model.Order
import ru.kima.sonar.common.serverapi.model.OrderBook
import ru.kima.sonar.server.data.market.marketdata.util.toInstant
import ru.tinkoff.piapi.contract.v1.GetOrderBookResponse

typealias TinkoffOrder = ru.tinkoff.piapi.contract.v1.Order

fun GetOrderBookResponse.toOrderBook() = OrderBook(
    uid = instrumentUid,
    depth = depth,
    bids = bidsList.map { it.toOrder() },
    asks = asksList.map { it.toOrder() },
    lastPrice = lastPrice.toBigDecimal(),
    closePrice = closePrice.toBigDecimal(),
    limitUp = limitUp.toBigDecimal(),
    limitDown = limitDown.toBigDecimal(),
    lastPriceTs = lastPriceTs.toInstant(),
    closePriceTs = closePriceTs.toInstant(),
    orderBookTs = orderbookTs.toInstant(),
)

fun TinkoffOrder.toOrder() = Order(
    price = price.toBigDecimal(),
    quantity = quantity
)