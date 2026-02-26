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

        @Resource("add")
        class AddEntry(val parent: Portfolio)

        @Resource("delete")
        class Delete(val parent: Portfolio)
    }

    @Resource("entries/{id}")
    class Entry(val parent: PortfoliosRoute = PortfoliosRoute(), val id: Long) {
        @Resource("update")
        class Update(val parent: Entry)

        @Resource("delete")
        class Delete(val parent: Entry)
    }

    companion object {
        const val ROOT = "portfolios"
    }
}