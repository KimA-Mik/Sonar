package ru.kima.sonar.data.finam.remote.datasource

import ru.kima.sonar.common.util.SonarResult
import ru.kima.sonar.data.finam.model.FinamError

internal interface RemoteFinamDataSource {
    suspend fun findTicker(ticker: String): SonarResult<String, FinamError>
}