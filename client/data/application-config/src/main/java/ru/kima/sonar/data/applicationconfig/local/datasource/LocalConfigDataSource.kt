package ru.kima.sonar.data.applicationconfig.local.datasource

import kotlinx.coroutines.flow.Flow
import ru.kima.sonar.data.applicationconfig.local.model.LocalConfig

interface LocalConfigDataSource {
    fun localConfig(): Flow<LocalConfig>
}