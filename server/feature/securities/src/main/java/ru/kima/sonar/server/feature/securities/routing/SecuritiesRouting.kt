package ru.kima.sonar.server.feature.securities.routing

import io.ktor.server.application.Application
import io.ktor.server.auth.authenticate
import io.ktor.server.resources.get
import io.ktor.server.routing.routing
import org.koin.ktor.ext.inject
import ru.kima.sonar.common.serverapi.routing.SecurityRoute
import ru.kima.sonar.server.feature.auth.MAIN_BEARER_NAME
import ru.kima.sonar.server.feature.securities.controller.SecuritiesController

fun Application.securitiesRoute() = routing {
    val controller by inject<SecuritiesController>()
    authenticate(MAIN_BEARER_NAME) {
        get<SecurityRoute.Shares> { controller.sharesRoute(call) }

        get<SecurityRoute.Shares.Share> { share ->
            controller.shareRoute(share.ticker, call)
        }

        get<SecurityRoute.Futures> { controller.futuresRoute(call) }

        get<SecurityRoute.Futures.Future> { future ->
            controller.futureRoute(future.ticker, call)
        }
    }
}