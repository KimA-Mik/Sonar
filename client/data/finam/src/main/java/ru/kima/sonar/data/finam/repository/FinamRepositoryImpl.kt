package ru.kima.sonar.data.finam.repository

import ru.kima.sonar.data.finam.remote.datasource.RemoteFinamDataSource

internal class FinamRepositoryImpl(
    private val dataSource: RemoteFinamDataSource
) : FinamRepository