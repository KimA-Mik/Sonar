package ru.kima.sonar.data.applicationconfig.local.util

import android.content.Context
import androidx.datastore.dataStore

val Context.localConfigDataStore by dataStore(
    fileName = "application_config.pb",
    serializer = LocalConfigSerializer
)