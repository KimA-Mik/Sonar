package ru.kima.sonar.common.serverapi.routing

const val WEBSOCKET = "ws"
object SecurityRoute {
    const val ROOT = "$WEBSOCKET/securities"

    object Shares {
        const val PATH = "$ROOT/shares"

        object Share {
            const val TICKER_KEY = "ticker"
            const val PATH = "${Shares.PATH}/{$TICKER_KEY}"
        }
    }

    object Futures {
        const val PATH = "$ROOT/futures"


        object Future {
            const val TICKER_KEY = "ticker"
            const val PATH = "${Futures.PATH}/{$TICKER_KEY}"
        }
    }
}