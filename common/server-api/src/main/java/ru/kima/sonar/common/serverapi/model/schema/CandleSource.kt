package ru.kima.sonar.common.serverapi.model.schema

enum class CandleSource {
    /** Все свечи.*/
    UNSPECIFIED,

    /** Биржевые свечи (торговые сессии).*/
    EXCHANGE,

    /** Все свечи с учетом торговли по выходным.*/
    INCLUDE_WEEKEND,
    UNRECOGNIZED
}