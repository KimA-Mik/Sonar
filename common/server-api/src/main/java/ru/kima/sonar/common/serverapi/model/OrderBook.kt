package ru.kima.sonar.common.serverapi.model

import java.math.BigDecimal
import kotlin.time.Instant

/**
 * Информация о стакане.
 * @see <a href="https://developer.tbank.ru/invest/services/quotes/marketdata#getorderbookresponse">Reference</a>
 * */
data class OrderBook(
    /** UID инструмента.*/
    val uid: String,
    /**Глубина стакана*/
    val depth: Int,
    /**Массив предложений.*/
    val bids: List<Order>,
    /**Массив спроса.*/
    val asks: List<Order>,
    /**Цена последней сделки за 1 инструмент*/
    val lastPrice: BigDecimal,
    /**Цена закрытия за 1 инструмент*/
    val closePrice: BigDecimal,
    /**Верхний лимит цены за 1 инструмент*/
    val limitUp: BigDecimal,
    /**Нижний лимит цены за 1 инструмент*/
    val limitDown: BigDecimal,
    /**Время получения цены последней сделки.*/
    val lastPriceTs: Instant,
    /**Время получения цены закрытия.*/
    val closePriceTs: Instant,
    /**Время формирования стакана на бирже.*/
    val orderBookTs: Instant
)