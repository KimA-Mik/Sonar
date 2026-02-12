package ru.kima.sonar.server.feature.auth.routing

import io.ktor.http.HttpStatusCode
import io.ktor.server.application.Application
import io.ktor.server.request.receive
import io.ktor.server.resources.post
import io.ktor.server.response.respond
import io.ktor.server.routing.routing
import org.koin.ktor.ext.inject
import ru.kima.sonar.common.serverapi.clientrequests.AuthenticateClientRequest
import ru.kima.sonar.common.serverapi.routing.AuthRoute
import ru.kima.sonar.common.serverapi.serverresponse.AuthorizationResult
import ru.kima.sonar.server.feature.auth.AuthController

fun Application.authRoute() = routing {
    val authController by inject<AuthController>()

    post<AuthRoute.Login> {
        val request = runCatching { call.receive<AuthenticateClientRequest>() }
            .getOrElse {
                call.respond(HttpStatusCode.BadRequest)
                return@post
            }

        authController.login(request)?.let { token ->
            call.respond(AuthorizationResult(token))
        } ?: call.respond(HttpStatusCode.Unauthorized)
    }
}

