package ru.kima.sonar.server.data.market.marketdata

interface MarketDataRepository {
    fun startPolling()
    fun stopPolling()
}