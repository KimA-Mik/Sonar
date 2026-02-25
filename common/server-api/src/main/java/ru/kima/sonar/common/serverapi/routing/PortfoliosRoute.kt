package ru.kima.sonar.common.serverapi.routing

import io.ktor.resources.Resource

@Resource("/${PortfoliosRoute.ROOT}")
class PortfoliosRoute {
    @Resource("create")
    class CreatePortfolio(val parent: PortfoliosRoute = PortfoliosRoute())

    @Resource("{id}")
    class Portfolio(val parent: PortfoliosRoute = PortfoliosRoute(), val id: Long) {
        @Resource("update")
        class Update(val parent: Portfolio)
    }

    companion object {
        const val ROOT = "portfolios"
    }
}