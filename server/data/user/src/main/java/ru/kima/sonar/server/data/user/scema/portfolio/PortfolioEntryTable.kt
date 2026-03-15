package ru.kima.sonar.server.data.user.scema.portfolio

import org.jetbrains.exposed.v1.core.ReferenceOption
import org.jetbrains.exposed.v1.core.dao.id.LongIdTable
import org.jetbrains.exposed.v1.datetime.timestamp
import ru.kima.sonar.common.serverapi.util.MONEY_PRECISION
import ru.kima.sonar.common.serverapi.util.MONEY_SCALE
import ru.kima.sonar.common.serverapi.util.NOTE_LENGTH
import ru.kima.sonar.common.serverapi.util.TITLE_LENGTH
import ru.kima.sonar.common.serverapi.util.UID_LENGTH
import java.math.BigDecimal
import kotlin.time.Instant

internal object PortfolioEntryTable : LongIdTable() {
    val portfolioId = long("portfolio_id").references(
        PortfolioTable.id,
        onDelete = ReferenceOption.CASCADE
    )
    val securityUid = varchar("security_uid", UID_LENGTH)
    val name = varchar("name", TITLE_LENGTH)
    val targetDeviation =
        decimal("target_deviation", MONEY_PRECISION, MONEY_SCALE).default(BigDecimal.ONE)
    val lowPrice = decimal("low_price", MONEY_PRECISION, MONEY_SCALE)
    val highPrice = decimal("high_price", MONEY_PRECISION, MONEY_SCALE)
    val note = varchar("note", NOTE_LENGTH)
    val enabled = bool("enabled").default(true)
    val shouldNotify = bool("should_notify").default(true)
    val lastUnboundUpdate = timestamp("last_unbound_update").default(Instant.DISTANT_PAST)
    val lastUnboundUpdatePrice = decimal("last_unbound_update_price", MONEY_PRECISION, MONEY_SCALE)
        .default(BigDecimal.ZERO)
}

