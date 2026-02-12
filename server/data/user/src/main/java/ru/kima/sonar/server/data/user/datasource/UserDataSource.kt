package ru.kima.sonar.server.data.user.datasource

import ru.kima.sonar.common.util.SonarResult
import ru.kima.sonar.server.data.user.model.Session
import ru.kima.sonar.server.data.user.model.User
import ru.kima.sonar.server.data.user.model.UserAndSession
import ru.kima.sonar.server.data.user.model.UserDataError

interface UserDataSource {
    suspend fun insertUser(user: User): SonarResult<User, UserDataError>
    suspend fun updateUser(user: User): SonarResult<User, UserDataError>
    suspend fun getUserByEmail(email: String): SonarResult<User, UserDataError>
    suspend fun getUserById(id: Long): SonarResult<User, UserDataError>

    suspend fun insertSession(session: Session): SonarResult<Session, UserDataError>
    suspend fun updateSession(session: Session): SonarResult<Session, UserDataError>
    suspend fun getSessionByToken(token: String): SonarResult<Session, UserDataError>
    suspend fun getSessionsByUserId(userId: Long): SonarResult<List<Session>, UserDataError>
    suspend fun deleteSessionByToken(token: String): SonarResult<Unit, UserDataError>
    suspend fun getUserAndSessionsByToken(token: String): SonarResult<UserAndSession, UserDataError>
}