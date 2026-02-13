package ru.kima.sonar.server.feature.securities.routing

import io.ktor.server.application.Application
import io.ktor.server.auth.authenticate
import io.ktor.server.routing.routing
import io.ktor.server.websocket.webSocket
import io.ktor.websocket.close
import ru.kima.sonar.common.serverapi.routing.SecurityRoute
import ru.kima.sonar.server.feature.auth.MAIN_BEARER_NAME

fun Application.securitiesRoute() = routing {
    authenticate(MAIN_BEARER_NAME) {
        webSocket(SecurityRoute.Shares.PATH) {

        }

        webSocket(SecurityRoute.Shares.Share.PATH) {
            val ticker = call.parameters[SecurityRoute.Shares.Share.TICKER_KEY]
            if (ticker == null) {
                close()
                return@webSocket
            }
        }

        webSocket(SecurityRoute.Futures.PATH) {

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