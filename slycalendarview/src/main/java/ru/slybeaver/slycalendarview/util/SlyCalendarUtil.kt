package ru.slybeaver.slycalendarview.util

import java.util.*

internal object SlyCalendarUtil {

    fun getCalendarWithMonthShift(date: Date, pos: Int, count: Int): Calendar {
        val instance = Calendar.getInstance()
        instance.time = date
        instance.add(Calendar.MONTH, getMonthShift(pos, count))
        return instance
    }

    fun getMonthShift(pos: Int, count: Int): Int {
        return pos - (startPosition(count))
    }

    fun startPosition(count: Int) = count / 2

    fun getCalendarWithoutTime(date: Date): Calendar {
        return Calendar.getInstance().apply {
            time = date
            set(Calendar.HOUR, 0)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }
    }
}