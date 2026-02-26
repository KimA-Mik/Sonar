package ru.kima.sonar.server.data.user.model.portfolio

import java.math.BigDecimal

data class PortfolioEntry(
    val id: Long,
    val portfolioId: Long,
    val securityUid: String,
    val name: String,
    val lowPrice: BigDecimal,
    val highPrice: BigDecimal,
    val note: String,
) {
    companion object {
        fun default(
            id: Long = 0,
            portfolioId: Long,
            securityUid: String,
            name: String,
            lowPrice: BigDecimal,
            highPrice: BigDecimal,
            note: String
        ) = PortfolioEntry(
            id = id,
            portfolioId = portfolioId,
            securityUid = securityUid,
            name = name,
            lowPrice = lowPrice,
            highPrice = highPrice,
            note = note
        )
    }
}