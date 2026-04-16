package ru.kima.sonar.feature.portfolios.ui.rules.model

import androidx.compose.runtime.Immutable

sealed interface DisplayRule {
    val key: Long
    val depth: Int
    val parent: ParentRule?

    @Immutable
    data class Group(
        override val key: Long,
        val threshold: Int,
        override val depth: Int,
        override val parent: ParentRule?
    ) : DisplayRule, ParentRule

    sealed interface Indicator : DisplayRule {
        val low: Float
        val high: Float
        val threshold: Int

        @Immutable
        data class Rsi(
            override val key: Long,
            override val depth: Int,
            override val low: Float,
            override val high: Float,
            override val threshold: Int,
            override val parent: ParentRule?
        ) : Indicator

        @Immutable
        data class Srsi(
            override val key: Long,
            override val depth: Int,
            override val low: Float,
            override val high: Float,
            override val threshold: Int,
            override val parent: ParentRule?
        ) : Indicator

        @Immutable
        data class Mfi(
            override val key: Long,
            override val depth: Int,
            override val low: Float,
            override val high: Float,
            override val threshold: Int,
            override val parent: ParentRule?
        ) : Indicator

        @Immutable
        data class Bb(
            override val key: Long,
            override val depth: Int,
            override val low: Float,
            override val high: Float,
            override val threshold: Int,
            override val parent: ParentRule?
        ) : Indicator
    }
}