package ru.kima.sonar.server.data.user.model.portfolio

data class PortfolioWithEntries(
    val portfolio: Portfolio,
    val entries: List<PortfolioEntry>,
)
