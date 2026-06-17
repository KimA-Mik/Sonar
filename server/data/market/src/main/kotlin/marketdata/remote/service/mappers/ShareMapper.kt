package ru.kima.sonar.server.data.market.marketdata.remote.service.mappers

import ru.kima.sonar.common.serverapi.model.security.Share
import ru.kima.sonar.server.data.market.marketdata.util.toInstant

typealias TinkoffShare = ru.tinkoff.piapi.contract.v1.Share

fun TinkoffShare.toShare() = Share(
    uid = uid,
    ticker = ticker,
    name = name,
    lot = lot,
    firstTradeDate = first1MinCandleDate.toInstant()
)