package ru.kima.sonar.feature.portfolios.ui.rules.events

import ru.kima.sonar.common.serverapi.model.rules.RulesMode

sealed interface RulesScreenUserEvent {
    data class SetMode(val mode: RulesMode) : RulesScreenUserEvent
}