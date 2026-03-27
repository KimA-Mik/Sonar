package ru.kima.sonar.server

import com.github.ajalt.clikt.core.CliktCommand
import com.github.ajalt.clikt.core.main
import com.github.ajalt.clikt.parameters.options.default
import com.github.ajalt.clikt.parameters.options.help
import com.github.ajalt.clikt.parameters.options.multiple
import com.github.ajalt.clikt.parameters.options.option
import com.github.ajalt.clikt.parameters.options.required
import com.github.ajalt.clikt.parameters.types.int
import io.ktor.network.tls.certificates.buildKeyStore
import io.ktor.network.tls.certificates.saveToFile
import io.ktor.serialization.kotlinx.KotlinxWebsocketSerializationConverter
import io.ktor.serialization.kotlinx.json.json
import io.ktor.server.application.install
import io.ktor.server.auth.Authentication
import io.ktor.server.auth.bearer
import io.ktor.server.engine.ApplicationEngine
import io.ktor.server.engine.connector
import io.ktor.server.engine.embeddedServer
import io.ktor.server.engine.sslConnector
import io.ktor.server.netty.Netty
import io.ktor.server.plugins.calllogging.CallLogging
import io.ktor.server.plugins.contentnegotiation.ContentNegotiation
import io.ktor.server.resources.Resources
import io.ktor.server.websocket.WebSockets
import kotlinx.serialization.json.Json
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
import java.io.File

class Program : CliktCommand() {
    val port by option("-p", "--port").int().default(80)
    val sslPort by option("--ssl-port").int().default(443)
    val marketDbName by option("--market-db-name").default("marketdata.db")
    val usersDbName by option("--users-db-name").default("users.db")
    val tToken by option("--t-invest-token").required().help("T-Invest API token")
    val firebaseCredentialsPath by option("--firebase-credentials").required()
        .help("Firebase credentials path")
    val firebaseProjectId by option("--firebase-project-id").required().help("Firebase project ID")

    val domains by option("--domain").multiple()
    val keystorePath by option("--keystore")
    val certificateAlias by option("--certificate-alias")
    val certificatePassword by option("--certificate-password")
    val keystorePassword by option("--keystore-password")
    override fun run() {
        embeddedServer(Netty, configure = {
            configureEnv(
                keystorePath = keystorePath,
                domainsList = domains,
                certificateAlias = certificateAlias,
                certificatePassword = certificatePassword,
                keystorePassword = keystorePassword,
                httpPort = port,
                httpsPort = sslPort
            )
        }) {
            install(CallLogging)
            install(Resources)
            install(ContentNegotiation) { json() }
            install(WebSockets) {
                contentConverter = KotlinxWebsocketSerializationConverter(Json)
            }
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

fun ApplicationEngine.Configuration.configureEnv(
    keystorePath: String?,
    domainsList: List<String>,
    certificateAlias: String?,
    certificatePassword: String?,
    keystorePassword: String?,
    httpPort: Int,
    httpsPort: Int,
) {
    connector {
        port = httpPort
    }

    var error = false
    val errorSb = StringBuilder()
    if (keystorePath == null) {
        error = true
        errorSb.appendLine("Provide keystore path")
    }

    if (certificateAlias == null) {
        error = true
        errorSb.appendLine("Provide certificate alias")
    }

    if (certificatePassword == null) {
        error = true
        errorSb.appendLine("Provide certificate password")
    }

    if (keystorePassword == null) {
        error = true
        errorSb.appendLine("Provide keystore password")
    }

    if (domainsList.isEmpty()) {
        error = true
        errorSb.appendLine("Provide at least one domain")
    }

    if (error) {
        println("Unable to use ssl connection")
        println(errorSb.toString())
        return
    }

    val keyStoreFile = File(keystorePath!!)
    val keyStore = buildKeyStore {
        certificate(certificateAlias!!) {
            password = certificatePassword!!
            domains = domainsList
        }
    }
    keyStore.saveToFile(keyStoreFile, "123456")

    sslConnector(
        keyStore = keyStore,
        keyAlias = certificateAlias!!,
        keyStorePassword = { keystorePassword!!.toCharArray() },
        privateKeyPassword = { certificatePassword!!.toCharArray() }) {
        port = httpsPort
        keyStorePath = keyStoreFile
    }
}