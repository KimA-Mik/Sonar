package ru.kima.sonar.server

import io.ktor.serialization.kotlinx.json.json
import io.ktor.server.application.install
import io.ktor.server.auth.Authentication
import io.ktor.server.auth.UserIdPrincipal
import io.ktor.server.auth.apikey.apiKey
import io.ktor.server.auth.bearer
import io.ktor.server.auth.digest
import io.ktor.server.engine.embeddedServer
import io.ktor.server.netty.Netty
import io.ktor.server.plugins.contentnegotiation.ContentNegotiation
import kotlinx.serialization.json.Json

fun main() {
    embeddedServer(Netty, port = 69) {
        install(ContentNegotiation) {
            json(Json)
        }

        install(Authentication) {
            digest("user-default") {
                realm = "ud"
                digestProvider { _, _ ->
                    ByteArray(1)
                }
            }
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

            apiKey("default-api-key") {
                validate { keyFromHeader ->

                }
            }
        }
    }.start(wait = true)
}