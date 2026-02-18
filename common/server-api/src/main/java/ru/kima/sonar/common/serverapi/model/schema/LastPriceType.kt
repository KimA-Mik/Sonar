package ru.kima.sonar.common.serverapi.model.schema

/**
 * Тип последней цены
 * @see <a href="https://developer.tbank.ru/invest/services/quotes/marketdata#lastpricetype">Reference</a>
 */
enum class LastPriceType {
    /** Не определен */
    LAST_PRICE_UNSPECIFIED,

    /** Цена биржи. */
    LAST_PRICE_EXCHANGE,

    /** Цена дилера. */
    LAST_PRICE_DEALER,

    UNRECOGNIZED
}