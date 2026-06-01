package ru.kima.sonar.data.finam.local.cookiesdatastore

import android.content.Context
import androidx.datastore.dataStore

internal val Context.cookiesDataStore by dataStore(
    fileName = "local_cookies.pb",
    serializer = LocalCookiesSerializer
)
