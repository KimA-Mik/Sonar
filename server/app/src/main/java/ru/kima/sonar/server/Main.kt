package ru.kima.sonar.server

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

fun main() {
    embeddedServer(Netty, port = 69) {
        install(CallLogging)
        install(Resources)
        install(ContentNegotiation) { json() }
        install(WebSockets)
        install(Koin) {
            //TODO: implement command line args
            modules(dataModule("", ""), featureModule())
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
    }.start(wait = true)
}