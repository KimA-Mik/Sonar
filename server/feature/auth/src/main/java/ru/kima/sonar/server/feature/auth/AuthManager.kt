package ru.kima.sonar.server.feature.auth

import org.slf4j.LoggerFactory
import org.springframework.security.crypto.argon2.Argon2PasswordEncoder
import ru.kima.sonar.common.serverapi.model.NotificationProvider
import ru.kima.sonar.common.util.isSuccess
import ru.kima.sonar.common.util.valueOr
import ru.kima.sonar.server.data.user.datasource.UserDataSource
import ru.kima.sonar.server.data.user.model.Session
import ru.kima.sonar.server.data.user.model.User
import ru.kima.sonar.server.feature.auth.impl.generateToken
import kotlin.time.Clock

class AuthManager(private val userDataSource: UserDataSource) {
    private val logger = LoggerFactory.getLogger(this::class.java)
    private val argon2 = Argon2PasswordEncoder.defaultsForSpringSecurity_v5_8()

//    init {
//        CoroutineScope(Dispatchers.Default).launch {
//            val login = "123"
//            val password = "456"
//
//            userDataSource.insertUser(
//                User(
//                    id = 0,
//                    email = login,
//                    passwordHash = argon2.encode(password)!!
//                )
//            )
//        }
//    }

    suspend fun logInUser(
        email: String,
        password: String,
        device: String,
        notificationProvider: NotificationProvider?,
        notificationProviderClientId: String?
    ): String? {
        val user = userDataSource.getUserByEmail(email).valueOr { return null }
        if (!argon2.matches(password, user.passwordHash)) return null

        var token: String
        do {
            token = generateToken(email)
        } while (userDataSource.getSessionByToken(token).isSuccess())
        val now = Clock.System.now()
        userDataSource.insertSession(
            Session(
                id = 0,
                userId = user.id,
                token = token,
                notificationProvider = notificationProvider,
                notificationProviderId = notificationProviderClientId,
                createdAt = now,
                lastAccessed = now,
                device = device
            )
        ).valueOr {
            logger.error("Failed to create session for user ${user.id}: $it")
            return null
        }

        return token
    }

    fun hashPassword(userId: Long, password: String): String {
        return argon2.encode(password)!!
    }

    fun getUserForToken(token: String): User? {
        TODO()
    }
}