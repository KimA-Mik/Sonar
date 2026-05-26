package ru.kima.sonar.server.data.user.model.portfolio

import java.math.BigDecimal
import kotlin.time.Instant

data class TakeProfit(
    val id: Long,
    val entryId: Long,
    val price: BigDecimal?,
    val note: String,
    val shouldNotify: Boolean,
    val lastUnboundUpdate: Instant,
    val lastUnboundUpdatePrice: BigDecimal,
) {
    companion object {
        fun default(
            entryId: Long,
            id: Long = 0L,
            price: BigDecimal? = null,
            note: String = "",
            shouldNotify: Boolean = true,
            lastUnboundUpdate: Instant = Instant.DISTANT_PAST,
            lastUnboundUpdatePrice: BigDecimal = BigDecimal.ZERO,
        ) = TakeProfit(
            id = id,
            entryId = entryId,
            price = price,
            note = note,
            shouldNotify = shouldNotify,
            lastUnboundUpdate = lastUnboundUpdate,
            lastUnboundUpdatePrice = lastUnboundUpdatePrice
        )
    }
}
