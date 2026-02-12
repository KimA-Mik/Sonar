package ru.kima.sonar.server.data.user.datasource

import org.jetbrains.exposed.v1.core.eq
import org.jetbrains.exposed.v1.jdbc.SchemaUtils
import org.jetbrains.exposed.v1.jdbc.transactions.transaction
import org.jetbrains.exposed.v1.migration.jdbc.MigrationUtils
import org.slf4j.LoggerFactory
import ru.kima.sonar.common.util.SonarResult
import ru.kima.sonar.server.data.user.database.DatabaseConnector
import ru.kima.sonar.server.data.user.model.Session
import ru.kima.sonar.server.data.user.model.User
import ru.kima.sonar.server.data.user.model.UserDataError
import ru.kima.sonar.server.data.user.scema.SessionEntity
import ru.kima.sonar.server.data.user.scema.SessionTable
import ru.kima.sonar.server.data.user.scema.UserEntity
import ru.kima.sonar.server.data.user.scema.UserTable

class ExposedUserDataSource(
    private val databaseConnector: DatabaseConnector
) : UserDataSource {
    private val logger = LoggerFactory.getLogger(this::class.java)

    init {
        transaction {
            val tables = arrayOf(UserTable, SessionTable)
            SchemaUtils.create(*tables)
            val missingColumnsStatements =
                MigrationUtils.statementsRequiredForDatabaseMigration(*tables)
            missingColumnsStatements.forEach {
                logger.info("Executing statement: $it")
                try {
                    connection.prepareStatement(it, true).executeUpdate()
                } catch (e: Exception) {
                    logger.error(e.message)
                }
            }
        }
    }

    // Transformation methods
    private fun UserEntity.toDomainModel(): User = User(
        id = id.value,
        email = email,
        passwordHash = passwordHash
    )

    private fun SessionEntity.toDomainModel(): Session = Session(
        id = id.value,
        userId = userId,
        token = token,
        notificationProvider = notificationProvider,
        notificationProviderId = notificationProviderId,
        createdAt = createdAt,
        lastAccessed = lastAccessed,
        device = device
    )

    private fun UserEntity.putInside(domainObject: User) {
        email = domainObject.email
        passwordHash = domainObject.passwordHash
    }

    private fun SessionEntity.putInside(domainObject: Session) {
        userId = domainObject.userId
        token = domainObject.token
        notificationProvider = domainObject.notificationProvider
        notificationProviderId = domainObject.notificationProviderId
        createdAt = domainObject.createdAt
        lastAccessed = domainObject.lastAccessed
        device = domainObject.device
    }

    override suspend fun insertUser(user: User): SonarResult<User, UserDataError> {
        return try {
            val result = databaseConnector.transaction {
                val userEntity = UserEntity.new {
                    email = user.email
                    passwordHash = user.passwordHash
                }
                userEntity.toDomainModel()
            }
            SonarResult.Success(result)
        } catch (e: Exception) {
            logger.error("Error inserting user", e)
            SonarResult.Error(UserDataError.UnknownError(e))
        }
    }

    override suspend fun updateUser(user: User): SonarResult<User, UserDataError> {
        return try {
            val result = databaseConnector.transaction {
                // Use extension findByIdAndUpdate to update atomically
                UserEntity.findByIdAndUpdate(user.id) {
                    it.putInside(user)
                }?.toDomainModel()
            }
            if (result != null) {
                SonarResult.Success(result)
            } else {
                SonarResult.Error(UserDataError.UserNotFound)
            }
        } catch (e: Exception) {
            logger.error("Error updating user", e)
            SonarResult.Error(UserDataError.UnknownError(e))
        }
    }

    override suspend fun getUserByEmail(email: String): SonarResult<User, UserDataError> {
        return try {
            val userEntity = databaseConnector.transaction {
                UserEntity.find { UserTable.email eq email }.firstOrNull()
            }
            if (userEntity != null) {
                SonarResult.Success(userEntity.toDomainModel())
            } else {
                SonarResult.Error(UserDataError.UserNotFound)
            }
        } catch (e: Exception) {
            logger.error("Error getting user by email", e)
            SonarResult.Error(UserDataError.UnknownError(e))
        }
    }

    override suspend fun getUserById(id: Long): SonarResult<User, UserDataError> {
        return try {
            val userEntity = databaseConnector.transaction {
                UserEntity.findById(id)
            }
            if (userEntity != null) {
                SonarResult.Success(userEntity.toDomainModel())
            } else {
                SonarResult.Error(UserDataError.UserNotFound)
            }
        } catch (e: Exception) {
            logger.error("Error getting user by id", e)
            SonarResult.Error(UserDataError.UnknownError(e))
        }
    }

    override suspend fun insertSession(session: Session): SonarResult<Session, UserDataError> {
        return try {
            val result = databaseConnector.transaction {
                val sessionEntity = SessionEntity.new {
                    putInside(session)
                }
                sessionEntity.toDomainModel()
            }
            SonarResult.Success(result)
        } catch (e: Exception) {
            logger.error("Error inserting session", e)
            SonarResult.Error(UserDataError.UnknownError(e))
        }
    }

    override suspend fun updateSession(session: Session): SonarResult<Session, UserDataError> {
        return try {
            val result = databaseConnector.transaction {
                SessionEntity.findByIdAndUpdate(session.id) {
                    it.putInside(session)
                }?.toDomainModel()
            }
            if (result != null) {
                SonarResult.Success(result)
            } else {
                SonarResult.Error(UserDataError.UserNotFound)
            }
        } catch (e: Exception) {
            logger.error("Error updating session", e)
            SonarResult.Error(UserDataError.UnknownError(e))
        }
    }

    override suspend fun getSessionByToken(token: String): SonarResult<Session, UserDataError> {
        return try {
            val sessionEntity = databaseConnector.transaction {
                SessionEntity.find { SessionTable.token eq token }.firstOrNull()
            }
            if (sessionEntity != null) {
                SonarResult.Success(sessionEntity.toDomainModel())
            } else {
                SonarResult.Error(UserDataError.UserNotFound)
            }
        } catch (e: Exception) {
            logger.error("Error getting session by token", e)
            SonarResult.Error(UserDataError.UnknownError(e))
        }
    }

    override suspend fun getSessionsByUserId(userId: Long): SonarResult<List<Session>, UserDataError> {
        return try {
            val sessions = databaseConnector.transaction {
                SessionEntity.find { SessionTable.userId eq userId }.map { it.toDomainModel() }
            }
            SonarResult.Success(sessions)
        } catch (e: Exception) {
            logger.error("Error getting sessions by user id", e)
            SonarResult.Error(UserDataError.UnknownError(e))
        }
    }

    override suspend fun deleteSessionByToken(token: String): SonarResult<Unit, UserDataError> {
        return try {
            databaseConnector.transaction {
                SessionEntity.find { SessionTable.token eq token }.forEach { it.delete() }
            }
            SonarResult.Success(Unit)
        } catch (e: Exception) {
            logger.error("Error deleting session by token", e)
            SonarResult.Error(UserDataError.UnknownError(e))
        }
    }
}