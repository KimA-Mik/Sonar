package ru.kima.sonar.server.data.market.marketdata.remote.service.mappers

import ru.kima.sonar.common.serverapi.model.LastPrice
import ru.kima.sonar.server.data.market.marketdata.util.toInstant
import ru.ttech.piapi.core.impl.marketdata.wrapper.LastPriceWrapper
import java.time.ZoneOffset
import kotlin.time.toKotlinInstant


typealias TinkoffLastPrice = ru.tinkoff.piapi.contract.v1.LastPrice

fun TinkoffLastPrice.toLastPrice() = LastPrice(
    uid = instrumentUid,
    price = price.toBigDecimal(),
    time = time.toInstant(),
    lastPriceType = lastPriceType.toLastPriceType()
)

fun LastPriceWrapper.toLastPrice() = LastPrice(
    uid = instrumentUid,
    price = price,
    time = time.toInstant(ZoneOffset.UTC).toKotlinInstant(),
    lastPriceType = lastPriceType.toLastPriceType()
)