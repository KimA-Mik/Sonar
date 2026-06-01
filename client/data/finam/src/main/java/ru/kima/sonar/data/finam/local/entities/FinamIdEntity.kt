package ru.kima.sonar.data.finam.local.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = FinamIdEntity.TABLE_NAME)
internal data class FinamIdEntity(
    @PrimaryKey val ticker: String,
    @ColumnInfo(name = "remote_identifier") val remoteIdentifier: String
) {
    companion object {
        const val TABLE_NAME = "finam_id"
    }
}
