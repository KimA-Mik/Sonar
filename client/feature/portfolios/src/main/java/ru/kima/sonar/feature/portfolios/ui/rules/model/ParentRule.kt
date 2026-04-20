package ru.kima.sonar.feature.portfolios.ui.rules.model

//TODO: Do something
sealed interface ParentRule {
    fun findKey(): Long {
        return when (this) {
            is DisplayRule.Group -> key
            is DisplayRule.Indicator -> key
        }
    }
}