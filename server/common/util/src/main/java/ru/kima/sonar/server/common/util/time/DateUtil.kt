package ru.kima.sonar.server.common.util.time

import kotlinx.datetime.DateTimeUnit
import kotlinx.datetime.DayOfWeek
import kotlinx.datetime.TimeZone
import kotlinx.datetime.atStartOfDayIn
import kotlinx.datetime.plus
import kotlinx.datetime.toLocalDateTime
import kotlinx.datetime.until
import kotlin.time.Instant

object DateUtil {
    val timezoneMoscow = TimeZone.of("Europe/Moscow")

    fun msUntilStartOfDay(
        instant: Instant,
        dayOfWeek: DayOfWeek,
        timezone: TimeZone = timezoneMoscow
    ): Long {
        var date = instant
            .toLocalDateTime(timezone)
            .date
            .plus(1, DateTimeUnit.DAY)

        while (date.dayOfWeek != dayOfWeek) {
            date = date.plus(1, DateTimeUnit.DAY)
        }

        return instant.until(date.atStartOfDayIn(timezone), DateTimeUnit.MILLISECOND)
    }

    fun isHoursInRange(
        hour: Int,
        minute: Int,
        startHour: Int,
        startMinute: Int,
        endHour: Int,
        endMinute: Int
    ): Boolean {
        if (hour in (startHour + 1)..<endHour
        ) {
            return true
        }

        if ((hour == startHour && minute >= startMinute) ||
            (hour == endHour && minute <= endMinute)
        ) {
            return true
        }

        return false
    }

    fun msUntilTime(
        instant: Instant,
        startHour: Int,
        startMinute: Int,
        timezone: TimeZone = timezoneMoscow
    ): Long {
        val dateTime = instant.toLocalDateTime(timezone)
        var dayOfWork = dateTime.date

        if (dateTime.hour in (startHour + 1)..23) {
            dayOfWork = dayOfWork.plus(1, DateTimeUnit.DAY)
        }

        val startOfWork = dayOfWork
            .atStartOfDayIn(timezone)
            .plus(startHour, DateTimeUnit.HOUR)
            .plus(startMinute, DateTimeUnit.MINUTE)

        return instant.until(startOfWork, DateTimeUnit.MILLISECOND)
    }
}