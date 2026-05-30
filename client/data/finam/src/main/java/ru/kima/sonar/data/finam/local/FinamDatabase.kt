package ru.kima.sonar.data.finam.local

import androidx.room.Database
import androidx.room.RoomDatabase
import ru.kima.sonar.data.finam.local.dao.FinamIdDao
import ru.kima.sonar.data.finam.local.entities.FinamIdEntity

@Database(
    entities = [FinamIdEntity::class],
    version = 1
)
internal abstract class FinamDatabase : RoomDatabase() {
    abstract fun finamIdDao(): FinamIdDao
}