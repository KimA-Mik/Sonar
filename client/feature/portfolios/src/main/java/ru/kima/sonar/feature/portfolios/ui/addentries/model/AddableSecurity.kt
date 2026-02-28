package ru.kima.sonar.feature.portfolios.ui.addentries.model

import androidx.compose.runtime.Immutable
import java.math.BigDecimal

@Immutable
data class AddableSecurity(
    val uid: String,
    val ticker: String,
    val name: String,
    val price: BigDecimal,
    val selected: Boolean,
    val basicAsset: String
)