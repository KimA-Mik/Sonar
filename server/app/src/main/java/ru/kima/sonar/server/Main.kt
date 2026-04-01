package ru.kima.sonar.server

import ch.qos.logback.classic.Level
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
import io.ktor.server.auth.bearer
import io.ktor.server.engine.embeddedServer
import io.ktor.server.netty.Netty
import io.ktor.server.plugins.calllogging.CallLogging
import io.ktor.server.plugins.contentnegotiation.ContentNegotiation
import io.ktor.server.resources.Resources
import org.koin.ktor.ext.inject
import org.koin.ktor.plugin.Koin
import ru.kima.sonar.server.di.dataModule
import ru.kima.sonar.server.di.featureModule
import ru.kima.sonar.server.di.rootModule
import ru.kima.sonar.server.feature.auth.AuthManager
import ru.kima.sonar.server.feature.auth.MAIN_BEARER_NAME
import ru.kima.sonar.server.feature.auth.routing.authRoute
import ru.kima.sonar.server.feature.portfolios.routing.portfoliosRoute
import ru.kima.sonar.server.feature.portfolios.service.core.initializeFirebase
import ru.kima.sonar.server.feature.portfolios.service.runUpdateService
import ru.kima.sonar.server.feature.securities.routing.securitiesRoute
import ru.kima.sonar.server.lifecycle.shutdownHook
import ru.kima.sonar.server.util.setLogbackLevel

class Program : CliktCommand() {
    val port by option("-p", "--port").int().default(1337)
    val marketDbName by option("--market-db-name").default("marketdata.db")
    val usersDbName by option("--users-db-name").default("users.db")
    val tToken by option("--t-invest-token").required().help("T-Invest API token")
    val firebaseCredentialsPath by option("--firebase-credentials").required()
        .help("Firebase credentials path")
    val firebaseProjectId by option("--firebase-project-id").required().help("Firebase project ID")
    override fun run() {
        setLogbackLevel(Level.INFO)
        embeddedServer(Netty, port = port) {
            install(CallLogging)
            install(Resources)
            install(ContentNegotiation) { json() }
            install(Koin) {
                modules(
                    dataModule(
                        usersDbName = usersDbName,
                        marketDataDbName = marketDbName,
                        tToken = tToken
                    ),
                    featureModule(),
                    rootModule()
                )
            }

            val authManager by inject<AuthManager>()
            install(Authentication) {
                bearer(MAIN_BEARER_NAME) {
                    authenticate { tokenCredential ->
                        authManager.getUserForToken(tokenCredential.token)?.user
                    }
                }
            }

            initializeFirebase(firebaseProjectId, firebaseCredentialsPath)
            authRoute()
            securitiesRoute()
            portfoliosRoute()
            runUpdateService()
            shutdownHook()
        }.start(wait = true)
    }
}

fun main(args: Array<String>) = Program().main(args)