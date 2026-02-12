package ru.kima.sonar.server.feature.auth.routing

import io.ktor.http.HttpStatusCode
import io.ktor.server.application.Application
import io.ktor.server.request.receive
import io.ktor.server.resources.post
import io.ktor.server.response.respond
import io.ktor.server.routing.routing
import ru.kima.sonar.common.serverapi.clientrequests.AuthenticateClientRequest
import ru.kima.sonar.common.serverapi.routing.AuthRoute

fun Application.authRoute() = routing {
    post<AuthRoute.Login> {
        val asd = runCatching { call.receive<AuthenticateClientRequest>() }
            .getOrElse {
                println(it)
                call.respond(HttpStatusCode.BadRequest)
                return@post
            }
        println(asd)
        call.respond(HttpStatusCode.OK)
    }
}

