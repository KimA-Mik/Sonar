package ru.kima.sonar.common.ui.util

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import java.math.BigDecimal
import java.text.DecimalFormatSymbols


class DecimalFormatter(
    symbols: DecimalFormatSymbols = DecimalFormatSymbols.getInstance()
) {

    val thousandsSeparator = symbols.groupingSeparator
    val decimalSeparator = symbols.decimalSeparator

    fun cleanup(input: String): String {

        if (input.matches("\\D".toRegex())) return ""
        if (input.matches("0+".toRegex())) return "0"

        val sb = StringBuilder()

        var hasDecimalSep = false

        for (char in input) {
            if (char.isDigit()) {
                sb.append(char)
                continue
            }
            if (char == decimalSeparator && !hasDecimalSep && sb.isNotEmpty()) {
                sb.append(char)
                hasDecimalSep = true
            }
        }

        return sb.toString()
    }

    fun formatForVisual(input: String): String {

        val split = input.split(decimalSeparator)

        val intPart = split[0]
            .reversed()
            .chunked(3)
            .joinToString(separator = thousandsSeparator.toString())
            .reversed()

        val fractionPart = split.getOrNull(1)

        return if (fractionPart == null) intPart else intPart + decimalSeparator + fractionPart
    }
}

@Composable
fun formatBigDecimal(bigDecimal: BigDecimal): String {
    val nf = LocalNumberFormat.current
    return remember(bigDecimal) { nf.format(bigDecimal) }
}