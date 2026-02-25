package ru.kima.sonar.server.feature.portfolios.routing

import io.ktor.server.application.Application
import io.ktor.server.auth.authenticate
import io.ktor.server.resources.get
import io.ktor.server.routing.routing
import ru.kima.sonar.common.serverapi.routing.PortfoliosRoute
import ru.kima.sonar.server.feature.auth.MAIN_BEARER_NAME

fun Application.portfoliosRoute() = routing {
    authenticate(MAIN_BEARER_NAME) {
        get<PortfoliosRoute> {

        }
    }
}