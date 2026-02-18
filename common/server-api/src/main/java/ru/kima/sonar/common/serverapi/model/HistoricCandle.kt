package ru.kima.sonar.common.serverapi.model

import java.math.BigDecimal
import kotlin.time.Instant

data class HistoricCandle(
    /**
     * Цена открытия за 1 инструмент. Чтобы получить стоимость лота, нужно умножить на лотность инструмента
     */
    val open: BigDecimal,
    /**
     * Максимальная цена за 1 инструмент
     */
    val high: BigDecimal,
    /**
     * Минимальная цена за 1 инструмент
     */
    val low: BigDecimal,
    /**
     * Цена закрытия за 1 инструмент
     */
    val close: BigDecimal,
    /**
     * Объем торгов в лотах.
     */
    val volume: Long,
    /**
     * Время свечи в часовом поясе UTC.
     */
    val time: Instant,
    /**
     * Признак завершенности свечи. `false` — свеча за текущие интервал еще сформирована не полностью.
     */
    val isComplete: Boolean
    /**
     * Тип источника свечи.
     */
    //val candleSourceType
)
