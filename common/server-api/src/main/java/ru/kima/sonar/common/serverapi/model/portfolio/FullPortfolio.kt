package ru.kima.sonar.common.serverapi.model.portfolio

import kotlinx.serialization.Serializable

@Serializable
data class FullPortfolio(
    val portfolio: SonarPortfolio,
    val rule: SonarRule?
)
