package ru.kima.sonar.server.data.market.marketdata.remote.service

import kotlinx.coroutines.Deferred
import kotlinx.coroutines.future.asDeferred
import ru.kima.sonar.common.serverapi.model.schema.InstrumentExchangeType
import ru.kima.sonar.common.serverapi.model.schema.InstrumentStatus
import ru.kima.sonar.server.data.market.marketdata.remote.service.mappers.toTInstrumentExchangeType
import ru.kima.sonar.server.data.market.marketdata.remote.service.mappers.toTInstrumentStatus
import ru.tinkoff.piapi.contract.v1.FuturesResponse
import ru.tinkoff.piapi.contract.v1.InstrumentsRequest
import ru.tinkoff.piapi.contract.v1.InstrumentsServiceGrpc
import ru.tinkoff.piapi.contract.v1.SharesResponse
import ru.ttech.piapi.core.connector.AsyncStubWrapper

typealias InstrumentsService = AsyncStubWrapper<InstrumentsServiceGrpc.InstrumentsServiceStub>

/**
 * Список акций.
 * @see <a href="https://developer.tbank.ru/invest/services/instruments/methods#shares">Reference</a>
 */
fun InstrumentsService.shares(
    instrumentStatus: InstrumentStatus,
    instrumentExchange: InstrumentExchangeType
): Deferred<SharesResponse> = callAsyncMethod { stub, observer ->
    val builder = InstrumentsRequest.newBuilder()
        .setInstrumentStatus(instrumentStatus.toTInstrumentStatus())
        .setInstrumentExchange(instrumentExchange.toTInstrumentExchangeType())

    stub.shares(builder.build(), observer)
}.asDeferred()

/**
 * Список фьючерсов.
 * @see <a href="https://developer.tbank.ru/invest/services/instruments/methods#shares">Reference</a>
 */
fun InstrumentsService.futures(
    instrumentStatus: InstrumentStatus,
    instrumentExchange: InstrumentExchangeType
): Deferred<FuturesResponse> = callAsyncMethod { stub, observer ->
    val builder = InstrumentsRequest.newBuilder()
        .setInstrumentStatus(instrumentStatus.toTInstrumentStatus())
        .setInstrumentExchange(instrumentExchange.toTInstrumentExchangeType())

    stub.futures(builder.build(), observer)
}.asDeferred()
