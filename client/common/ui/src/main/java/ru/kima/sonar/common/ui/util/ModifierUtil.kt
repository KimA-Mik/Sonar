package ru.kima.sonar.common.ui.util

import androidx.compose.ui.Modifier

inline fun Modifier.applyIf(condition: Boolean, modifier: Modifier.() -> Modifier): Modifier {
    return if (condition) {
        then(Modifier.modifier())
    } else {
        this
    }
}