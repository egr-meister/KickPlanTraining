package com.kickplan.training.util

import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

/**
 * Small, dependency-free date/time helpers.
 *
 * Uses java.util.Calendar + SimpleDateFormat so it works on minSdk 24 without
 * core-library desugaring. Every parse is guarded; invalid input never throws.
 */
object DateUtils {

    private val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.US).apply { isLenient = false }
    private val timeFormat = SimpleDateFormat("HH:mm", Locale.US).apply { isLenient = false }
    private val isoFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", Locale.US)
    private val prettyDateFormat = SimpleDateFormat("EEE, d MMM yyyy", Locale.US)
    private val weekdayFormat = SimpleDateFormat("EEEE", Locale.US)
    private val shortWeekdayFormat = SimpleDateFormat("EEE", Locale.US)

    /** Today as YYYY-MM-DD. */
    fun today(): String = dateFormat.format(Date())

    /** Current local time as HH:mm. */
    fun nowTime(): String = timeFormat.format(Date())

    /** Full ISO-ish timestamp used for createdAt / updatedAt. */
    fun nowTimestamp(): String =
        SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.US).format(Date())

    fun isValidDate(value: String?): Boolean {
        if (value.isNullOrBlank()) return false
        return try {
            dateFormat.parse(value) != null
        } catch (e: Exception) {
            false
        }
    }

    fun isValidTime(value: String?): Boolean {
        if (value.isNullOrBlank()) return false
        return try {
            timeFormat.parse(value) != null
        } catch (e: Exception) {
            false
        }
    }

    /** Parse a YYYY-MM-DD string to a Date, or null if invalid. */
    fun parseDate(value: String?): Date? {
        if (value.isNullOrBlank()) return null
        return try {
            dateFormat.parse(value)
        } catch (e: Exception) {
            null
        }
    }

    /** Pretty label for a YYYY-MM-DD date; returns the raw string if unparseable. */
    fun prettyDate(value: String?): String {
        val d = parseDate(value) ?: return value.orEmpty()
        return prettyDateFormat.format(d)
    }

    /** Weekday name (e.g. "Wednesday") for a YYYY-MM-DD date, or "" if invalid. */
    fun weekdayName(value: String?): String {
        val d = parseDate(value) ?: return ""
        return weekdayFormat.format(d)
    }

    fun shortWeekday(value: String?): String {
        val d = parseDate(value) ?: return ""
        return shortWeekdayFormat.format(d)
    }

    /**
     * Returns the list of the last 7 calendar dates (YYYY-MM-DD), oldest first,
     * ending today. Week is treated as the trailing 7-day window.
     */
    fun last7Days(): List<String> {
        val cal = Calendar.getInstance()
        val result = ArrayList<String>(7)
        // Go back 6 days, then move forward to today.
        cal.add(Calendar.DAY_OF_YEAR, -6)
        repeat(7) {
            result.add(dateFormat.format(cal.time))
            cal.add(Calendar.DAY_OF_YEAR, 1)
        }
        return result
    }

    /**
     * Dates of the current week starting Monday (Mon..Sun), YYYY-MM-DD.
     */
    fun currentWeekMondayFirst(): List<String> {
        val cal = Calendar.getInstance()
        cal.firstDayOfWeek = Calendar.MONDAY
        // Move back to Monday of the current week.
        while (cal.get(Calendar.DAY_OF_WEEK) != Calendar.MONDAY) {
            cal.add(Calendar.DAY_OF_YEAR, -1)
        }
        val result = ArrayList<String>(7)
        repeat(7) {
            result.add(dateFormat.format(cal.time))
            cal.add(Calendar.DAY_OF_YEAR, 1)
        }
        return result
    }

    /** Format an ISO UTC date (e.g. from the API) into local YYYY-MM-DD + HH:mm. */
    fun splitIsoUtc(utc: String?): Pair<String, String> {
        if (utc.isNullOrBlank()) return "" to ""
        return try {
            isoFormat.timeZone = java.util.TimeZone.getTimeZone("UTC")
            val parsed = isoFormat.parse(utc) ?: return "" to ""
            // Format back in the device's local time zone.
            val localDate = SimpleDateFormat("yyyy-MM-dd", Locale.US).format(parsed)
            val localTime = SimpleDateFormat("HH:mm", Locale.US).format(parsed)
            localDate to localTime
        } catch (e: Exception) {
            "" to ""
        }
    }
}
