package ru.kima.sonar.server.data.market.marketdata.remote.service.mappers

import ru.kima.sonar.common.serverapi.model.LastPrice
import ru.kima.sonar.server.data.market.marketdata.util.toInstant


typealias TinkoffLastPrice = ru.tinkoff.piapi.contract.v1.LastPrice

fun TinkoffLastPrice.toLastPrice() = LastPrice(
    uid = instrumentUid,
    price = price.toBigDecimal(),
    time = time.toInstant(),
    lastPriceType = lastPriceType.toLastPriceType()
)
