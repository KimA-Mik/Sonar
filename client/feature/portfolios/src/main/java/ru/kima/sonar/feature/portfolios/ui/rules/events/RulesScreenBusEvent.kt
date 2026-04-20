package ru.kima.sonar.feature.portfolios.ui.rules.events

internal sealed interface RulesScreenBusEvent {
    data class ConfirmClearGroup(val key: Long) : RulesScreenBusEvent
    data class ConfirmDeleteRule(val key: Long) : RulesScreenBusEvent
}