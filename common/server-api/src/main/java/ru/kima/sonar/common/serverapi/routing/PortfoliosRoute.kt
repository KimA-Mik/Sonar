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

        @Resource("rules")
        class Rules(val parent: Portfolio) {
            @Resource("update")
            class Update(val parent: Rules)
        }
    }

    @Resource("entries/{id}")
    class Entry(val parent: PortfoliosRoute = PortfoliosRoute(), val id: Long) {
        @Resource("update")
        class Update(val parent: Entry)

        @Resource("add_take_profit")
        class AddTakeProfit(val parent: Entry)

        @Resource("add_stop_loss")
        class AddStopLoss(val parent: Entry)

        @Resource("delete")
        class Delete(val parent: Entry)
    }

    @Resource("delete_take_profit/{id}")
    class DeleteTakeProfit(val parent: PortfoliosRoute = PortfoliosRoute(), val id: Long)

    @Resource("delete_stop_loss/{id}")
    class DeleteStopLoss(val parent: PortfoliosRoute = PortfoliosRoute(), val id: Long)

    companion object {
        const val ROOT = "portfolios"
    }
}