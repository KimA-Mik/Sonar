package ru.kima.sonar.data.finam.repository

import ru.kima.sonar.common.util.SonarResult
import ru.kima.sonar.data.finam.model.FinamRepositoryError

interface FinamRepository {
    suspend fun findFinamId(ticker: String): SonarResult<String, FinamRepositoryError>
}