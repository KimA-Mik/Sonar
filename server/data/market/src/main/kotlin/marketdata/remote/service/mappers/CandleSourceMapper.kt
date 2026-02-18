package ru.kima.sonar.server.data.market.marketdata.remote.service.mappers

import ru.kima.sonar.common.serverapi.model.schema.CandleSource

private typealias TCandleSource = ru.tinkoff.piapi.contract.v1.GetCandlesRequest.CandleSource

fun CandleSource.toTCandleSource(): TCandleSource = when (this) {
    CandleSource.UNSPECIFIED -> TCandleSource.CANDLE_SOURCE_UNSPECIFIED
    CandleSource.UNRECOGNIZED -> TCandleSource.UNRECOGNIZED
    CandleSource.EXCHANGE -> TCandleSource.CANDLE_SOURCE_EXCHANGE
    CandleSource.INCLUDE_WEEKEND -> TCandleSource.CANDLE_SOURCE_INCLUDE_WEEKEND
}