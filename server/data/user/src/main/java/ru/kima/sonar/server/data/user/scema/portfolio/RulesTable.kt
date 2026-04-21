package ru.kima.sonar.server.data.user.scema.portfolio

import kotlinx.serialization.json.Json
import org.jetbrains.exposed.v1.core.ReferenceOption
import org.jetbrains.exposed.v1.core.dao.id.LongIdTable
import org.jetbrains.exposed.v1.json.json
import ru.kima.sonar.common.serverapi.model.rules.Rule
import ru.kima.sonar.common.serverapi.model.rules.RulesMode

internal object RulesTable : LongIdTable() {
    val portfolioId = long("portfolio_id").index().references(
        PortfolioTable.id,
        onDelete = ReferenceOption.RESTRICT
    )
    val rulesMode = enumeration<RulesMode>("rules_mode")
    val rule = json<Rule>("rule", Json)
}