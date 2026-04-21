package ru.kima.sonar.server.common.util.ktor

import io.ktor.http.HttpStatusCode
import io.ktor.server.request.receive
import io.ktor.server.response.respond
import io.ktor.server.routing.RoutingCall

suspend inline fun <reified T> RoutingCall.receiveOrBadRequest(onError: (Exception) -> Nothing): T {
    return try {
        receive()
    } catch (e: Exception) {
        respond(HttpStatusCode.BadRequest)
        onError(e)
    }
}