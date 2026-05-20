package com.wordnote.app.util

import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.TimeUnit

object DateUtils {
    private val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())

    fun formatDate(timestamp: Long): String {
        return dateFormat.format(Date(timestamp))
    }

    fun daysBetween(timestamp1: Long, timestamp2: Long): Long {
        val diff = timestamp2 - timestamp1
        return TimeUnit.MILLISECONDS.toDays(diff)
    }

    fun daysAgo(timestamp: Long): Long {
        return daysBetween(timestamp, System.currentTimeMillis())
    }

    fun getDaysAgoText(timestamp: Long): String {
        val days = daysAgo(timestamp)
        return when {
            days == 0L -> "今天添加"
            days == 1L -> "1天前添加"
            else -> "${days}天前添加"
        }
    }

    fun getStartOfDay(timestamp: Long): Long {
        val calendar = Calendar.getInstance()
        calendar.timeInMillis = timestamp
        calendar.set(Calendar.HOUR_OF_DAY, 0)
        calendar.set(Calendar.MINUTE, 0)
        calendar.set(Calendar.SECOND, 0)
        calendar.set(Calendar.MILLISECOND, 0)
        return calendar.timeInMillis
    }

    fun isSameDay(timestamp1: Long, timestamp2: Long): Boolean {
        return getStartOfDay(timestamp1) == getStartOfDay(timestamp2)
    }
}
