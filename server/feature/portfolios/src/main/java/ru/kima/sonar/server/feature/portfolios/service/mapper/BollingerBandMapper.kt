package ru.kima.sonar.server.feature.portfolios.service.mapper

import ru.kima.sonar.common.serverapi.events.model.BollingerBandsData
import ru.kima.sonar.server.feature.portfolios.techanalysis.BollingerBands

fun BollingerBands.BollingerBandsData.toEventData() = BollingerBandsData(
    lower = lower,
    middle = middle,
    upper = upper
)