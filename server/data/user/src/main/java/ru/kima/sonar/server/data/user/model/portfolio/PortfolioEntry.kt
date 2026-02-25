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
)