package ru.kima.sonar.server.data.market.marketdata.remote.service.mappers

import ru.kima.sonar.common.serverapi.model.CandleInterval

typealias TCandleInterval = ru.tinkoff.piapi.contract.v1.CandleInterval

fun CandleInterval.toTCandleInterval(): TCandleInterval = when (this) {
    CandleInterval.CANDLE_INTERVAL_UNSPECIFIED -> TCandleInterval.CANDLE_INTERVAL_UNSPECIFIED
    CandleInterval.CANDLE_INTERVAL_5_SEC -> TCandleInterval.CANDLE_INTERVAL_5_SEC
    CandleInterval.CANDLE_INTERVAL_10_SEC -> TCandleInterval.CANDLE_INTERVAL_10_SEC
    CandleInterval.CANDLE_INTERVAL_30_SEC -> TCandleInterval.CANDLE_INTERVAL_30_SEC
    CandleInterval.CANDLE_INTERVAL_1_MIN -> TCandleInterval.CANDLE_INTERVAL_1_MIN
    CandleInterval.CANDLE_INTERVAL_5_MIN -> TCandleInterval.CANDLE_INTERVAL_5_MIN
    CandleInterval.CANDLE_INTERVAL_15_MIN -> TCandleInterval.CANDLE_INTERVAL_15_MIN
    CandleInterval.CANDLE_INTERVAL_HOUR -> TCandleInterval.CANDLE_INTERVAL_HOUR
    CandleInterval.CANDLE_INTERVAL_DAY -> TCandleInterval.CANDLE_INTERVAL_DAY
    CandleInterval.CANDLE_INTERVAL_2_MIN -> TCandleInterval.CANDLE_INTERVAL_2_MIN
    CandleInterval.CANDLE_INTERVAL_3_MIN -> TCandleInterval.CANDLE_INTERVAL_3_MIN
    CandleInterval.CANDLE_INTERVAL_10_MIN -> TCandleInterval.CANDLE_INTERVAL_10_MIN
    CandleInterval.CANDLE_INTERVAL_30_MIN -> TCandleInterval.CANDLE_INTERVAL_30_MIN
    CandleInterval.CANDLE_INTERVAL_2_HOUR -> TCandleInterval.CANDLE_INTERVAL_2_HOUR
    CandleInterval.CANDLE_INTERVAL_4_HOUR -> TCandleInterval.CANDLE_INTERVAL_4_HOUR
    CandleInterval.CANDLE_INTERVAL_WEEK -> TCandleInterval.CANDLE_INTERVAL_WEEK
    CandleInterval.CANDLE_INTERVAL_MONTH -> TCandleInterval.CANDLE_INTERVAL_MONTH
    CandleInterval.UNRECOGNIZED -> TCandleInterval.UNRECOGNIZED
}