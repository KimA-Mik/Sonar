package ru.kima.sonar.server.data.user.model.portfolio

import java.math.BigInteger

data class PortfolioEntry(
    val id: Long,
    val portfolioId: Long,
    val securityUid: String,
    val name: String,
    val lowPrice: BigInteger,
    val highPrice: BigInteger,
    val note: String,
)