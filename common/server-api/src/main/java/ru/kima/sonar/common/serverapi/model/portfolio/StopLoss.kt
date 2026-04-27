package ru.kima.sonar.common.serverapi.model.portfolio

import kotlinx.serialization.Serializable
import ru.kima.sonar.common.serverapi.util.BigDecimalJson

@Serializable
data class StopLoss(
    val id: Long,
    val entryId: Long,
    val price: BigDecimalJson?,
    val note: String
)