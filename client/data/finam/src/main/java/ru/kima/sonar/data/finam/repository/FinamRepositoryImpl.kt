package ru.kima.sonar.data.finam.repository

import ru.kima.sonar.common.util.SonarResult
import ru.kima.sonar.common.util.isError
import ru.kima.sonar.data.finam.local.dao.FinamIdDao
import ru.kima.sonar.data.finam.local.entities.FinamIdEntity
import ru.kima.sonar.data.finam.model.FinamError
import ru.kima.sonar.data.finam.model.FinamRepositoryError
import ru.kima.sonar.data.finam.remote.datasource.RemoteFinamDataSource

internal class FinamRepositoryImpl(
    private val localDataSource: FinamIdDao,
    private val remoteDataSource: RemoteFinamDataSource
) : FinamRepository {
    private val finamRegex = "https://www.finam.ru/quote/moex/[a-zA-Z0-9]*/".toRegex()
    override suspend fun findFinamId(ticker: String): SonarResult<String, FinamRepositoryError> {
        localDataSource.findByTicker(ticker)
            ?.let { return SonarResult.Success(it.remoteIdentifier) }
        val result = remoteDataSource.findTicker(ticker)
        if (result.isError()) {
            return when (result.data) {
                is FinamError.RequestFailed -> SonarResult.Error(FinamRepositoryError.NoConnection)
                is FinamError.RequestError -> SonarResult.Error(
                    FinamRepositoryError.Unknown(Exception("Finam request error: code=${(result.data as FinamError.RequestError).code}, message=${(result.data as FinamError.RequestError).message}"))
                )

                is FinamError.Unknown -> SonarResult.Error(FinamRepositoryError.Unknown((result.data as FinamError.Unknown).e))
            }
        }


        val matchResul = finamRegex.findAll(result.data).firstOrNull()
        val url = matchResul?.value
            ?: return SonarResult.Error(FinamRepositoryError.NotFound(ticker))

        val splitted = url.split('/')
        if (splitted.size < 2) {
            return SonarResult.Error(FinamRepositoryError.NotFound(ticker))
        }
        val finamId = splitted[splitted.lastIndex - 1]
        localDataSource.insert(
            FinamIdEntity(
                ticker = ticker,
                remoteIdentifier = finamId
            )
        )

        return SonarResult.Success(finamId)
    }
}