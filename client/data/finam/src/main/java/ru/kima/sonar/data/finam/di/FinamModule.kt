package ru.kima.sonar.data.finam.di

import androidx.room.Room
import org.koin.android.ext.koin.androidContext
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.bind
import org.koin.dsl.module
import ru.kima.sonar.data.finam.local.FinamDatabase
import ru.kima.sonar.data.finam.remote.datasource.KtorRemoteFinamDataSource
import ru.kima.sonar.data.finam.remote.datasource.RemoteFinamDataSource
import ru.kima.sonar.data.finam.repository.FinamRepository
import ru.kima.sonar.data.finam.repository.FinamRepositoryImpl
import java.io.File


fun finamModule() = module {
    single {
        val context = androidContext()
        Room.databaseBuilder(
            context,
            FinamDatabase::class.java,
            File(context.cacheDir, "finam.db").absolutePath
        )
            .fallbackToDestructiveMigration(false)
            .build()
    }

    single {
        val database: FinamDatabase = get()
        database.finamIdDao()
    }

    singleOf(::KtorRemoteFinamDataSource) bind RemoteFinamDataSource::class
    singleOf(::FinamRepositoryImpl) bind FinamRepository::class
}