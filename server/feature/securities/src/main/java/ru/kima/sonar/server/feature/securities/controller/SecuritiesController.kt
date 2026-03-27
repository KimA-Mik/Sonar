package ru.kima.sonar.server.feature.securities.controller

import io.ktor.http.HttpStatusCode
import io.ktor.server.response.respond
import io.ktor.server.routing.RoutingCall
import kotlinx.coroutines.flow.first
import ru.kima.sonar.server.data.market.marketdata.MarketDataRepository

internal class SecuritiesController(
    private val repository: MarketDataRepository
) {
    suspend fun sharesRoute(call: RoutingCall) {
        val shares = repository.tradableShares().first()
        call.respond(shares)
    }

    suspend fun shareRoute(ticker: String, call: RoutingCall) {
        call.respond(HttpStatusCode.NotFound)
    }

    suspend fun futuresRoute(call: RoutingCall) {
        val futures = repository.tradableFutures().first()
        call.respond(futures)
    }

    suspend fun futureRoute(ticker: String, call: RoutingCall) {
        call.respond(HttpStatusCode.NotFound)
    }
}