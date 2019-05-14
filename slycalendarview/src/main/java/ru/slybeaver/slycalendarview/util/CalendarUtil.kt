package ru.slybeaver.slycalendarview.util

import java.util.*

object CalendarUtil {

    fun getCalendarWithMonthShift(date: Date, pos: Int, count: Int): Calendar {
        val instance = Calendar.getInstance()
        instance.time = date
        instance.add(Calendar.MONTH, getMonthShift(pos, count))
        return instance
    }

    fun getMonthShift(pos: Int, count: Int): Int {
        return pos - (count / 2)
    }
}