package ru.kima.sonar.server.data.market.marketdata.remote.service.mappers

import ru.kima.sonar.common.serverapi.model.schema.InstrumentStatus

typealias TInstrumentStatus = ru.tinkoff.piapi.contract.v1.InstrumentStatus

fun InstrumentStatus.toTInstrumentStatus(): TInstrumentStatus = when (this) {
    InstrumentStatus.INSTRUMENT_STATUS_UNSPECIFIED -> TInstrumentStatus.INSTRUMENT_STATUS_UNSPECIFIED
    InstrumentStatus.INSTRUMENT_STATUS_BASE -> TInstrumentStatus.INSTRUMENT_STATUS_BASE
    InstrumentStatus.INSTRUMENT_STATUS_ALL -> TInstrumentStatus.INSTRUMENT_STATUS_ALL
    InstrumentStatus.UNRECOGNIZED -> TInstrumentStatus.UNRECOGNIZED
}
