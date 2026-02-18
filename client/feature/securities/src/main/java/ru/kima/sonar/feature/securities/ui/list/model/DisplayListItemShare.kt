package ru.kima.sonar.feature.securities.ui.list.model

import androidx.compose.runtime.Immutable
import java.math.BigDecimal

@Immutable
class DisplayListItemShare(
    val uid: String,
    val name: String,
    val ticker: String,
    val price: BigDecimal
)