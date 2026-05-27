package ru.kima.sonar.feature.portfolios.ui.addentries.model

import androidx.compose.runtime.Immutable
import ru.kima.sonar.common.serverapi.model.portfolio.SecurityType
import java.math.BigDecimal

@Immutable
data class AddableSecurity(
    val uid: String,
    val ticker: String,
    val securityType: SecurityType,
    val name: String,
    val price: BigDecimal,
    val selected: Boolean,
    val basicAsset: String
)