package ru.kima.sonar.server.feature.portfolios.service.model

import java.math.BigDecimal


data class SecurityPrice(
    val price: BigDecimal,
    val lot: Int
)