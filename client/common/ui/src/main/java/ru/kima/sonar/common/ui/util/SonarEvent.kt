package ru.kima.sonar.common.ui.util

data class SonarEvent<T>(
    val data: T? = null
) {
    private val timestamp: Long = System.currentTimeMillis()
    private var consumed = false
    fun consume(action: (T) -> Unit) {
        if (!consumed) {
            consumed = true
            data?.let { action(it) }
        }
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as SonarEvent<*>

        if (timestamp != other.timestamp) return false
        if (data != other.data) return false

        return true
    }

    override fun hashCode(): Int {
        var result = timestamp.hashCode()
        result = 31 * result + (data?.hashCode() ?: 0)
        return result
    }
}