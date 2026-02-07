package ru.kima.sonar.data.homeapi.datasource

import ru.kima.sonar.common.util.SonarResult

interface HomeApiDataSource {
    suspend fun login(login: String, password: String): SonarResult<Unit, Unit>
}