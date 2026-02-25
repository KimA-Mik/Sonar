package ru.kima.sonar.server.feature.portfolios.routing

import io.ktor.server.application.Application
import io.ktor.server.auth.authenticate
import io.ktor.server.resources.get
import io.ktor.server.resources.post
import io.ktor.server.resources.put
import io.ktor.server.routing.routing
import org.koin.ktor.ext.inject
import ru.kima.sonar.common.serverapi.routing.PortfoliosRoute
import ru.kima.sonar.server.feature.auth.MAIN_BEARER_NAME
import ru.kima.sonar.server.feature.portfolios.controller.PortfoliosController

fun Application.portfoliosRoute() = routing {
    authenticate(MAIN_BEARER_NAME) {
        val controller: PortfoliosController by inject()
        get<PortfoliosRoute> { controller.portfoliosRoute(call) }
        post<PortfoliosRoute.CreatePortfolio> { controller.createPortfolio(call) }
        get<PortfoliosRoute.Portfolio> { controller.getPortfolio(call, it.id) }
        put<PortfoliosRoute.Portfolio.Update> { controller.updatePortfolio(call, it.parent.id) }
    }
}