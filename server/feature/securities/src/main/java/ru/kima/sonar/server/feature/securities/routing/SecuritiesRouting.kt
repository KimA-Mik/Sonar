package ru.kima.sonar.server.feature.securities.routing

import io.ktor.server.application.Application
import io.ktor.server.auth.authenticate
import io.ktor.server.routing.routing
import io.ktor.server.websocket.sendSerialized
import io.ktor.server.websocket.webSocket
import io.ktor.websocket.close
import org.koin.ktor.ext.inject
import ru.kima.sonar.common.serverapi.routing.SecurityRoute
import ru.kima.sonar.server.feature.auth.MAIN_BEARER_NAME
import ru.kima.sonar.server.feature.securities.controller.SecuritiesController

fun Application.securitiesRoute() = routing {
    val controller by inject<SecuritiesController>()
    authenticate(MAIN_BEARER_NAME) {
        webSocket(SecurityRoute.Shares.PATH) {
            controller.tradableShares().collect { shares ->
                sendSerialized(shares)
            }
        }

        webSocket(SecurityRoute.Shares.Share.PATH) {
            val ticker = call.parameters[SecurityRoute.Shares.Share.TICKER_KEY]
            if (ticker == null) {
                close()
                return@webSocket
            }
        }

        webSocket(SecurityRoute.Futures.PATH) {
            controller.tradableFutures().collect { futures ->
                sendSerialized(futures)
            }
        }

        webSocket(SecurityRoute.Futures.Future.PATH) {
            val ticker = call.parameters[SecurityRoute.Futures.Future.TICKER_KEY]
            if (ticker == null) {
                close()
                return@webSocket
            }
        }
    }
}