package ru.kima.sonar.data.applicationconfig.updateconfig.datasource

import android.content.Context
import kotlinx.coroutines.flow.Flow
import ru.kima.sonar.data.applicationconfig.updateconfig.model.ProviderUpdate
import ru.kima.sonar.data.applicationconfig.updateconfig.model.UpdateConfig
import ru.kima.sonar.data.applicationconfig.updateconfig.util.updateConfigDataStore

class ProtoDataStoreUpdateConfigDataSourceImpl(
    context: Context
) : UpdateConfigDataSource {
    private val dataStore = context.updateConfigDataStore

    override fun configFlow(): Flow<UpdateConfig> = dataStore.data
    override suspend fun setProviderUpdate(providerUpdate: ProviderUpdate?) {
        dataStore.updateData {
            it.copy(
                providerUpdate = providerUpdate
            )
        }
    }
}