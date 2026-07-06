package ru.kima.sonar.data.applicationconfig.updateconfig.util

import android.content.Context
import androidx.datastore.dataStore

val Context.updateConfigDataStore by dataStore(
    fileName = "update_config.pb",
    serializer = UpdateConfigSerializer
)