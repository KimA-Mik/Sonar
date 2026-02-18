package ru.kima.sonar.server.data.market.marketdata.remote.service.mappers

import ru.kima.sonar.common.serverapi.model.schema.InstrumentExchangeType

typealias TInstrumentExchangeType = ru.tinkoff.piapi.contract.v1.InstrumentExchangeType

fun InstrumentExchangeType.toTInstrumentExchangeType(): TInstrumentExchangeType =
    when (this) {
        InstrumentExchangeType.INSTRUMENT_EXCHANGE_UNSPECIFIED -> TInstrumentExchangeType.INSTRUMENT_EXCHANGE_UNSPECIFIED
        InstrumentExchangeType.INSTRUMENT_EXCHANGE_DEALER -> TInstrumentExchangeType.INSTRUMENT_EXCHANGE_DEALER
        InstrumentExchangeType.UNRECOGNIZED -> TInstrumentExchangeType.INSTRUMENT_EXCHANGE_UNSPECIFIED
    }
