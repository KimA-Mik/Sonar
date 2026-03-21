package ru.kima.sonar.common.serverapi.model

import kotlin.time.Duration
import kotlin.time.Duration.Companion.days
import kotlin.time.Duration.Companion.hours
import kotlin.time.Duration.Companion.minutes
import kotlin.time.Duration.Companion.seconds

/**
 *Интервал свечей. Максимальное значение интервала приведено ориентировочно, может отличаться в большую сторону в зависимости от параметров запроса.
 */
enum class CandleInterval(val duration: Duration, val limit: Int) {
    /**
     *Интервал не определен.
     * <code>CANDLE_INTERVAL_UNSPECIFIED = 0;</code>
     */
    CANDLE_INTERVAL_UNSPECIFIED(Duration.ZERO, 0),

    /**
     *От 5 секунд до 200 минут. Максимальное значение `limit` — 2500.
     */
    CANDLE_INTERVAL_5_SEC(5.seconds, 2500),

    /**
     *От 10 секунд до 200 минут. Максимальное значение `limit` — 1250.
     */
    CANDLE_INTERVAL_10_SEC(10.seconds, 1250),

    /**
     *От 30 секунд до 20 часов. Максимальное значение `limit` — 2500.
     */
    CANDLE_INTERVAL_30_SEC(30.seconds, 2500),

    /**
     *От 1 минуты до 1 дня. Максимальное значение `limit` — 2400.
     */
    CANDLE_INTERVAL_1_MIN(1.minutes, 2400),

    /**
     *От 2 минут до 1 дня. Максимальное значение `limit` — 1200.
     */
    CANDLE_INTERVAL_2_MIN(2.minutes, 1200),

    /**
     *От 3 минут до 1 дня. Максимальное значение `limit` — 750.
     */
    CANDLE_INTERVAL_3_MIN(3.minutes, 750),

    /**
     *От 5 минут до недели. Максимальное значение `limit` — 2400.
     */
    CANDLE_INTERVAL_5_MIN(5.minutes, 2400),

    /**
     *От 10 минут до недели. Максимальное значение `limit` — 1200.
     */
    CANDLE_INTERVAL_10_MIN(10.minutes, 1200),

    /**
     *От 15 минут до 3 недель. Максимальное значение `limit` — 2400.
     */
    CANDLE_INTERVAL_15_MIN(15.minutes, 2400),

    /**
     *От 30 минут до 3 недель. Максимальное значение `limit` — 1200.
     */
    CANDLE_INTERVAL_30_MIN(30.minutes, 1200),

    /**
     *От 1 часа до 3 месяцев. Максимальное значение `limit` — 2400.
     */
    CANDLE_INTERVAL_HOUR(1.hours, 2400),

    /**
     *От 2 часов до 3 месяцев. Максимальное значение `limit` — 2400.
     */
    CANDLE_INTERVAL_2_HOUR(2.hours, 2400),

    /**
     *От 4 часов до 3 месяцев. Максимальное значение `limit` — 700.
     */
    CANDLE_INTERVAL_4_HOUR(4.hours, 700),

    /**
     *От 1 дня до 6 лет. Максимальное значение `limit` — 2400.
     */
    CANDLE_INTERVAL_DAY(1.days, 2400),

    /**
     *От 1 недели до 5 лет. Максимальное значение `limit` — 300.
     */
    CANDLE_INTERVAL_WEEK(7.days, 300),

    /**
     *От 1 месяца до 10 лет. Максимальное значение `limit` — 120.
     */
    CANDLE_INTERVAL_MONTH(30.days, 120),
    UNRECOGNIZED(Duration.INFINITE, Int.MAX_VALUE),
}
