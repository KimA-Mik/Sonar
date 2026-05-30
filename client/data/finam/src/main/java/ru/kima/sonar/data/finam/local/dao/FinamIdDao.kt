package ru.kima.sonar.data.finam.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import ru.kima.sonar.data.finam.local.entities.FinamIdEntity

@Dao
internal interface FinamIdDao {
    @Insert
    suspend fun insert(finamIdEntity: FinamIdEntity)

    @Query("SELECT * FROM ${FinamIdEntity.TABLE_NAME} WHERE ticker = :ticker")
    suspend fun findByTicker(ticker: String): FinamIdEntity?
}