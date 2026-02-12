package ru.kima.sonar.server.feature.auth

import org.springframework.security.crypto.argon2.Argon2PasswordEncoder
import ru.kima.sonar.common.util.valueOr
import ru.kima.sonar.server.data.user.datasource.UserDataSource
import ru.kima.sonar.server.data.user.model.User

class AuthManager(private val userDataSource: UserDataSource) {
    private val argon2 = Argon2PasswordEncoder.defaultsForSpringSecurity_v5_8()

    suspend fun logInUser(login: String, password: String): String? {
        val user = userDataSource.getUserByEmail(login).valueOr { return null }
        if (!argon2.matches(password, user.passwordHash)) return null


        return null //TODO: generate token
    }

    fun hashPassword(userId: Long, password: String): String {
        return argon2.encode(password)!!
    }

    fun getUserForToken(token: String): User? {
        TODO()
    }
}