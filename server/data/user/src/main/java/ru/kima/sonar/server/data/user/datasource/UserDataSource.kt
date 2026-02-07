package ru.kima.sonar.server.data.user.datasource

import ru.kima.sonar.common.util.SonarResult
import ru.kima.sonar.server.data.user.model.User
import ru.kima.sonar.server.data.user.model.UserDataError

interface UserDataSource {
    suspend fun updateUser(user: User): SonarResult<User, UserDataError>
    suspend fun getUserByEmail(email: String): SonarResult<User, UserDataError>
    suspend fun getUserById(id: Long): SonarResult<User, UserDataError>
}