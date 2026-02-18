package ru.kima.sonar.server.data.market.marketdata.remote.service.mappers

import ru.kima.sonar.common.serverapi.model.security.Future
import ru.kima.sonar.server.data.market.marketdata.util.toInstant

typealias TinkoffFuture = ru.tinkoff.piapi.contract.v1.Future

fun TinkoffFuture.toFuture() = Future(
    uid = uid,
    ticker = ticker,
    name = name,
    lot = lot,
    expirationDate = expirationDate.toInstant(),
    basicAsset = basicAsset,
)
