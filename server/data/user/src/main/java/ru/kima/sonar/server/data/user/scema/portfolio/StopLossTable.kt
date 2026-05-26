package ru.kima.sonar.server.data.user.scema.portfolio

import org.jetbrains.exposed.v1.core.ReferenceOption
import org.jetbrains.exposed.v1.core.dao.id.LongIdTable
import ru.kima.sonar.common.serverapi.util.MONEY_PRECISION
import ru.kima.sonar.common.serverapi.util.MONEY_SCALE
import ru.kima.sonar.common.serverapi.util.NOTE_LENGTH
import java.math.BigDecimal
import kotlin.time.Instant

internal object StopLossTable : LongIdTable() {
    val entryId = reference(
        name = "entry_id",
        refColumn = PortfolioEntryTable.id,
        onDelete = ReferenceOption.CASCADE
    )

    val price = decimal("price", MONEY_PRECISION, MONEY_SCALE).nullable()
    val note = varchar("note", NOTE_LENGTH)
    val shouldNotify = bool("should_notify").default(true)
    val lastUnboundUpdate = long("last_unbound_update")
        .default(Instant.DISTANT_PAST.toEpochMilliseconds())
    val lastUnboundUpdatePrice = decimal("last_unbound_update_price", MONEY_PRECISION, MONEY_SCALE)
        .default(BigDecimal.ZERO)
}