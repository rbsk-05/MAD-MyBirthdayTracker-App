package com.example.mybirthdaytracker.utils

import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale
import kotlin.math.ceil
import kotlin.math.floor

object DateUtils {

    private val format = SimpleDateFormat("MMM d yyyy", Locale.US)
    private val months = arrayOf("Jan", "Feb", "Mar", "Apr", "May", "Jun", "Jul", "Aug", "Sep", "Oct", "Nov", "Dec")
    private val days = arrayOf("Sun", "Mon", "Tue", "Wed", "Thu", "Fri", "Sat")

    private fun parseDob(dob: String): Date? {
        return try {
            format.parse(dob)
        } catch (e: Exception) {
            null
        }
    }

    fun getBirthDate(dob: String): Int {
        val date = parseDob(dob) ?: return 1
        val cal = Calendar.getInstance().apply { time = date }
        return cal.get(Calendar.DAY_OF_MONTH)
    }

    fun getBirthMonth(dob: String): String {
        val date = parseDob(dob) ?: return "Jan"
        val cal = Calendar.getInstance().apply { time = date }
        return months[cal.get(Calendar.MONTH)]
    }

    fun getAge(dob: String): Int {
        val birthDate = parseDob(dob) ?: return 0
        val birthCal = Calendar.getInstance().apply { time = birthDate }
        val todayCal = Calendar.getInstance()

        var age = todayCal.get(Calendar.YEAR) - birthCal.get(Calendar.YEAR)

        if (birthCal.get(Calendar.MONTH) > todayCal.get(Calendar.MONTH) ||
            (birthCal.get(Calendar.MONTH) == todayCal.get(Calendar.MONTH) &&
             birthCal.get(Calendar.DAY_OF_MONTH) > todayCal.get(Calendar.DAY_OF_MONTH))) {
            age--
        }
        return age
    }

    fun getDaysLeft(dob: String): Int {
        val birthDate = parseDob(dob) ?: return 0
        val birthCal = Calendar.getInstance().apply { time = birthDate }
        val todayCal = Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, 0)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }

        val nextBirthdateCal = Calendar.getInstance().apply {
            set(Calendar.YEAR, todayCal.get(Calendar.YEAR))
            set(Calendar.MONTH, birthCal.get(Calendar.MONTH))
            set(Calendar.DAY_OF_MONTH, birthCal.get(Calendar.DAY_OF_MONTH))
            set(Calendar.HOUR_OF_DAY, 0)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }

        if (nextBirthdateCal.before(todayCal)) {
            nextBirthdateCal.add(Calendar.YEAR, 1)
        }

        val daysLeftMs = nextBirthdateCal.timeInMillis - todayCal.timeInMillis
        return ceil(daysLeftMs.toDouble() / (1000 * 60 * 60 * 24)).toInt()
    }

    fun getDay(dob: String): String {
        val birthDate = parseDob(dob) ?: return "Sun"
        val birthCal = Calendar.getInstance().apply { time = birthDate }
        val todayCal = Calendar.getInstance()

        val tempCal = Calendar.getInstance().apply {
            set(Calendar.YEAR, todayCal.get(Calendar.YEAR))
            set(Calendar.MONTH, birthCal.get(Calendar.MONTH))
            set(Calendar.DAY_OF_MONTH, birthCal.get(Calendar.DAY_OF_MONTH))
        }

        return days[tempCal.get(Calendar.DAY_OF_WEEK) - 1] // Calendar.SUNDAY is 1
    }

    fun getDaysAgo(dob: String): Int {
        val birthDate = parseDob(dob) ?: return 0
        val birthCal = Calendar.getInstance().apply { time = birthDate }
        val todayCal = Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, 0)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }

        val lastBirthDayCal = Calendar.getInstance().apply {
            set(Calendar.YEAR, todayCal.get(Calendar.YEAR))
            set(Calendar.MONTH, birthCal.get(Calendar.MONTH))
            set(Calendar.DAY_OF_MONTH, birthCal.get(Calendar.DAY_OF_MONTH))
            set(Calendar.HOUR_OF_DAY, 0)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }

        if (lastBirthDayCal.after(todayCal)) {
            lastBirthDayCal.add(Calendar.YEAR, -1)
        }

        val daysAgoMs = todayCal.timeInMillis - lastBirthDayCal.timeInMillis
        return floor(daysAgoMs.toDouble() / (1000 * 60 * 60 * 24)).toInt()
    }
}
