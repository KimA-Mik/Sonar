package ru.kima.sonar.common.serverapi.routing

import io.ktor.resources.Resource

@Resource("/${AuthRoute.ROOT}")
class AuthRoute {
    @Resource("/login")
    class Login(val parent: AuthRoute = AuthRoute())

    companion object {
        const val ROOT = "auth"
    }
}