package ru.kima.sonar.data.applicationconfig.updateconfig.datasource

import kotlinx.coroutines.flow.Flow
import ru.kima.sonar.data.applicationconfig.updateconfig.model.ProviderUpdate
import ru.kima.sonar.data.applicationconfig.updateconfig.model.UpdateConfig

interface UpdateConfigDataSource {
    fun configFlow(): Flow<UpdateConfig>
    suspend fun setProviderUpdate(providerUpdate: ProviderUpdate?)
}