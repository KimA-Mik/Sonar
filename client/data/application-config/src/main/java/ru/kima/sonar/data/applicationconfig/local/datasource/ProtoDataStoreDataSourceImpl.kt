package ru.kima.sonar.data.applicationconfig.local.datasource

import android.content.Context
import ru.kima.sonar.data.applicationconfig.local.util.localConfigDataStore

class ProtoDataStoreDataSourceImpl(context: Context) : LocalConfigDataSource {
    private val dataStore = context.localConfigDataStore
    override fun localConfig() = dataStore.data
}