package ru.kima.sonar.common.serverapi.routing


import io.ktor.resources.Resource

@Resource("/${SecurityRoute.ROOT}")
class SecurityRoute {
    @Resource("shares")
    data class Shares(val parent: SecurityRoute = SecurityRoute()) {
        @Resource("{ticker}")
        data class Share(val parent: Shares = Shares(), val ticker: String)
    }

    @Resource("futures")
    data class Futures(val parent: SecurityRoute = SecurityRoute()) {
        @Resource("{ticker}")
        data class Future(val parent: Futures = Futures(), val ticker: String)
    }

    companion object {
        const val ROOT = "securities"
    }
}