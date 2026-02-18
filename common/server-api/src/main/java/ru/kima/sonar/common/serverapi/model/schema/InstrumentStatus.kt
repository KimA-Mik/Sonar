package ru.kima.sonar.common.serverapi.model.schema

/**
 * Тип площадки торговли
 * @see <a href="https://developer.tbank.ru/invest/services/instruments/methods#instrumentstatus">Reference</a>
 */
enum class InstrumentStatus {
    /**
     * По умолчанию — базовый список инструментов, которыми можно торговать через T-Invest API. Сейчас списки доступных бумаг в API и других интерфейсах совпадают — кроме внебиржевых бумаг, но в будущем списки могут различаться.
     */
    INSTRUMENT_STATUS_BASE,

    /**
     * Список всех инструментов
     * */
    INSTRUMENT_STATUS_ALL,

    /**
     * Значение не определено
     */
    INSTRUMENT_STATUS_UNSPECIFIED,
    UNRECOGNIZED
}
