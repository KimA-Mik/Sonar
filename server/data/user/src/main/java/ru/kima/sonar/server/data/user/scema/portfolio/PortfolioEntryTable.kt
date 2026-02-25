package ru.kima.sonar.server.data.user.scema.portfolio

import org.jetbrains.exposed.v1.core.dao.id.LongIdTable
import ru.kima.sonar.common.serverapi.util.MONEY_PRECISION
import ru.kima.sonar.common.serverapi.util.MONEY_SCALE
import ru.kima.sonar.common.serverapi.util.NOTE_LENGTH
import ru.kima.sonar.common.serverapi.util.TITLE_LENGTH
import ru.kima.sonar.common.serverapi.util.UID_LENGTH

internal object PortfolioEntryTable : LongIdTable() {
    val portfolioId = long("portfolio_id").references(PortfolioTable.id)
    val securityUid = varchar("security_uid", UID_LENGTH)
    val name = varchar("name", TITLE_LENGTH)
    val lowPrice = decimal("low_price", MONEY_PRECISION, MONEY_SCALE)
    val highPrice = decimal("high_price", MONEY_PRECISION, MONEY_SCALE)
    val note = varchar("note", NOTE_LENGTH)
}

