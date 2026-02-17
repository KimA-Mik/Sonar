package ru.kima.sonar.server

import com.github.ajalt.clikt.core.CliktCommand
import com.github.ajalt.clikt.core.main
import com.github.ajalt.clikt.parameters.options.default
import com.github.ajalt.clikt.parameters.options.help
import com.github.ajalt.clikt.parameters.options.option
import com.github.ajalt.clikt.parameters.options.required
import com.github.ajalt.clikt.parameters.types.int
import io.ktor.serialization.kotlinx.json.json
import io.ktor.server.application.install
import io.ktor.server.auth.Authentication
import io.ktor.server.auth.UserIdPrincipal
import io.ktor.server.auth.bearer
import io.ktor.server.engine.embeddedServer
import io.ktor.server.netty.Netty
import io.ktor.server.plugins.calllogging.CallLogging
import io.ktor.server.plugins.contentnegotiation.ContentNegotiation
import io.ktor.server.resources.Resources
import io.ktor.server.websocket.WebSockets
import org.koin.ktor.ext.inject
import org.koin.ktor.plugin.Koin
import ru.kima.sonar.server.di.dataModule
import ru.kima.sonar.server.di.featureModule
import ru.kima.sonar.server.feature.auth.AuthManager
import ru.kima.sonar.server.feature.auth.MAIN_BEARER_NAME
import ru.kima.sonar.server.feature.auth.routing.authRoute
import ru.kima.sonar.server.feature.securities.routing.securitiesRoute
import ru.kima.sonar.server.lifecycle.shutdownHook

class Program : CliktCommand() {
    val port by option("-p", "--port").int().default(69)
    val marketDbName by option("--market-db-name").default("marketdata.db")
    val tToken by option("--t-invest-token").required().help("T-Invest API token")
    override fun run() {
        embeddedServer(Netty, port = port) {
            install(CallLogging)
            install(Resources)
            install(ContentNegotiation) { json() }
            install(WebSockets)
            install(Koin) {
                modules(dataModule(marketDbName, tToken), featureModule())
            }

            val authManager by inject<AuthManager>()
            install(Authentication) {
                bearer(MAIN_BEARER_NAME) {
                    authenticate { tokenCredential ->
                        authManager.getUserForToken(tokenCredential.token)?.let {
                            UserIdPrincipal(it.user.email)
                        }
                    }
                }
            }

            authRoute()
            securitiesRoute()
            shutdownHook()
        }.start(wait = true)
    }
}

fun main(args: Array<String>) = Program().main(args)