package ru.kima.sonar.feature.portfolios.ui.components.editentry

import androidx.compose.runtime.Immutable
import java.math.BigDecimal

@Immutable
internal sealed interface EditEntryComponent {
    val key: String

    @Immutable
    data class Title(
        override val key: String,
        val title: String,
        val price: BigDecimal,
        val targetDeviation: String,
        val id: Long,
    ) : EditEntryComponent {
        companion object {
            fun generateKey(uid: String): String {
                return "Title$KEY_SEPARATOR$uid"
            }
        }
    }

    @Immutable
    data class StopLoss(
        override val key: String,
        val index: Int,
        val price: String,
        val note: String,
        val id: Long = 0
    ) : EditEntryComponent {
        companion object {
            fun generateKey(uid: String, index: Int): String {
                return "StopLoss$KEY_SEPARATOR$uid$KEY_SEPARATOR$index"
            }
        }
    }

    @Immutable
    data class TakeProfit(
        override val key: String,
        val index: Int,
        val price: String,
        val note: String,
        val id: Long = 0
    ) : EditEntryComponent {
        companion object {
            fun generateKey(uid: String, index: Int): String {
                return "EditEntryComponent$KEY_SEPARATOR$uid$KEY_SEPARATOR$index"
            }
        }
    }

    @Immutable
    data class AddStopLoss(
        val uid: String,
    ) : EditEntryComponent {
        override val key: String = "AddStopLoss$KEY_SEPARATOR$uid"

        override fun equals(other: Any?): Boolean {
            if (this === other) return true
            if (javaClass != other?.javaClass) return false

            other as AddStopLoss
            return key == other.key
        }

        override fun hashCode() = key.hashCode()
    }

    @Immutable
    data class AddTakeProfit(
        val uid: String
    ) : EditEntryComponent {
        override val key = "AddTakeProfit$KEY_SEPARATOR$uid"
        override fun equals(other: Any?): Boolean {
            if (this === other) return true
            if (javaClass != other?.javaClass) return false

            other as AddTakeProfit
            return key == other.key
        }

        override fun hashCode() = key.hashCode()
    }

    @Immutable
    data class Padding(override val key: String) : EditEntryComponent {
        companion object {
            fun generateKey(uid: String, index: Int): String {
                return "Padding$KEY_SEPARATOR$uid$KEY_SEPARATOR$index"
            }
        }
    }

    companion object {
        const val KEY_SEPARATOR = ';'

        fun getUid(key: String): String? {
            return key.split(KEY_SEPARATOR).getOrNull(1)
        }

        fun getIndex(key: String): Int {
            return key.split(KEY_SEPARATOR).getOrNull(2)?.toIntOrNull() ?: -1
        }
    }
}