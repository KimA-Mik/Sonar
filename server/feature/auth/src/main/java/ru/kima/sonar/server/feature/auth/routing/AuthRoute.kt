package ru.kima.sonar.server.feature.auth.routing

import io.ktor.http.HttpStatusCode
import io.ktor.server.application.Application
import io.ktor.server.request.receive
import io.ktor.server.resources.get
import io.ktor.server.response.respond
import io.ktor.server.routing.routing
import ru.kima.sonar.common.serverapi.clientrequests.AuthenticateClientRequest
import ru.kima.sonar.common.serverapi.routing.AuthRoute

fun Application.authRoute() = routing {
    get<AuthRoute> {
        runCatching { call.receive<AuthenticateClientRequest>() }
            .getOrElse {
                call.respond(HttpStatusCode.BadRequest)
                return@get
            }
    }
}

