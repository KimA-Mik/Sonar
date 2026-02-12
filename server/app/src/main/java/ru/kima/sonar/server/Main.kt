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
import org.koin.ktor.plugin.Koin
import ru.kima.sonar.server.di.dataModule
import ru.kima.sonar.server.di.featureModule
import ru.kima.sonar.server.feature.auth.routing.authRoute

fun main() {
    embeddedServer(Netty, port = 69) {
        install(CallLogging)
        install(Resources)
        install(ContentNegotiation) { json() }
        install(Koin) {
            modules(dataModule(), featureModule())
        }

        install(Authentication) {
            bearer("auth-bearer") {
                realm = "Access to the '/' path"
                authenticate { tokenCredential ->
                    if (tokenCredential.token == "abc123") {
                        UserIdPrincipal("jetbrains")
                    } else {
                        null
                    }
                }
            }
        }

        authRoute()
    }.start(wait = true)
}