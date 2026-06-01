package ru.kima.sonar.feature.notifications.notifications

import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import androidx.core.app.NotificationCompat
import androidx.core.net.toUri
import ru.kima.sonar.common.serverapi.model.portfolio.SecurityType
import ru.kima.sonar.common.util.valueOr
import ru.kima.sonar.data.finam.repository.FinamRepository
import ru.kima.sonar.feature.notifications.R

fun getTInvestAction(
    context: Context,
    ticker: String,
    securityType: SecurityType
): NotificationCompat.Action {
    val url = when (securityType) {
        SecurityType.SHARE -> "https://www.tbank.ru/invest/stocks/$ticker/"
        SecurityType.FUTURE -> "https://www.tbank.ru/invest/futures/$ticker/"
    }
    val browserIntent = Intent(Intent.ACTION_VIEW, url.toUri())
    val pendingIntentFlags =
        PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT

    val intent = PendingIntent.getActivity(
        context,
        0, // Unique request code
        browserIntent,
        pendingIntentFlags
    )

    return NotificationCompat.Action(
        R.drawable.tinvest_logo,
        context.getString(R.string.action_tinvest),
        intent
    )
}

suspend fun getFinamAction(
    context: Context,
    ticker: String,
    securityType: SecurityType,
    finamRepository: FinamRepository
): NotificationCompat.Action? {
    val securityId = when (securityType) {
        SecurityType.SHARE -> ticker.lowercase()
        SecurityType.FUTURE -> finamRepository.findFinamId(ticker).valueOr { return null }
    }
    val url = "https://www.finam.ru/quote/moex/$securityId/"

    val browserIntent = Intent(Intent.ACTION_VIEW, url.toUri())
    val pendingIntentFlags =
        PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
    val intent = PendingIntent.getActivity(
        context,
        0, // Unique request code
        browserIntent,
        pendingIntentFlags
    )
    return NotificationCompat.Action(
        R.drawable.finam_logo,
        context.getString(R.string.action_finam),
        intent
    )
}