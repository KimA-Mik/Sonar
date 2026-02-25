package ru.kima.sonar.common.serverapi.routing

import io.ktor.resources.Resource

@Resource("/${PortfoliosRoute.ROOT}")
class PortfoliosRoute {

    companion object {
        const val ROOT = "portfolios"
    }
}