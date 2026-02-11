package ru.kima.sonar.feature.authentication.uscase

import kotlinx.coroutines.flow.firstOrNull
import ru.kima.sonar.common.util.isSuccess
import ru.kima.sonar.data.applicationconfig.local.datasource.LocalConfigDataSource
import ru.kima.sonar.data.homeapi.datasource.HomeApiDataSource
import ru.kima.sonar.data.homeapi.error.HomeApiError

class LogInUseCase(
    private val localConfigDataSource: LocalConfigDataSource,
    private val homeApiDataSource: HomeApiDataSource
) {
    suspend operator fun invoke(login: String, password: String): Result {
        val localConfig = localConfigDataSource
            .localConfig()
            .firstOrNull() ?: return Result.UnknownError

        val clientId = localConfig.notificationProviderClientId
        val response = homeApiDataSource.login(
            login, password,
            localConfig.notificationProvider,
            clientId
        )

        return if (response.isSuccess()) {
            localConfigDataSource.updateApiAccessToken(response.data)
            localConfigDataSource.updateLoginAndPassword(login, password)
            Result.Success
        } else {
            when (response.data) {
                is HomeApiError.Unauthorized -> Result.IncorrectCredentials
                is HomeApiError.NetworkError -> Result.NetworkError
                is HomeApiError.UnknownApiError -> Result.ServerError
                is HomeApiError.UnknownError -> Result.UnknownError
            }
        }
    }

    sealed interface Result {
        data object Success : Result
        data object IncorrectCredentials : Result
        data object NetworkError : Result
        data object ServerError : Result
        data object UnknownError : Result
    }
}