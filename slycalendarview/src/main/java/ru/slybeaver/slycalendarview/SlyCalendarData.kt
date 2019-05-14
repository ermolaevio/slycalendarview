package ru.slybeaver.slycalendarview

import java.util.*

class SlyCalendarData {

    var selectedStartDate: Date = Calendar.getInstance().time // first selected date
    var selectedEndDate: Date? = null // ended selected date
    var showDate: Date? = selectedStartDate // current showing date

    var isFirstMonday = true
    var isSingle = false //

    var backgroundColor: Int? = null
    var headerColor: Int? = null
    var headerTextColor: Int? = null
    var textColor: Int? = null
    var selectedColor: Int? = null
    var selectedTextColor: Int? = null
    var timeTheme: Int? = null

    var currentState = State.END // current date to select

    fun setNewSelectedDate(selectedDate: Date) {
        val startDate = selectedStartDate
        val endDate = selectedEndDate

        if (isSingle) {
            selectedStartDate = selectedDate
        } else {
            val selectedDateTime = selectedDate.time

            when (currentState) {
                State.START -> {
                    if (endDate == null) {
                        selectedStartDate = selectedDate
                    } else {
                        if (selectedDateTime < endDate.time) {
                            selectedStartDate = selectedDate
                        } else if (selectedDateTime > endDate.time) {
                            selectedStartDate = endDate
                            selectedEndDate = selectedDate
                        }
                    }
                }
                State.END -> {
                    if (selectedDateTime < startDate.time) {
                        selectedStartDate = selectedDate
                        selectedEndDate = startDate
                    } else if (selectedDateTime > startDate.time) {
                        selectedEndDate = selectedDate
                    }
                }
            }
        }
    }

    fun setNewSelectedYear(year: Int) {
        val calendarStart = Calendar.getInstance()
        calendarStart.time = selectedStartDate

        var calendarEnd: Calendar? = null

        if (selectedEndDate != null) {
            calendarEnd = Calendar.getInstance()
            calendarEnd.time = selectedEndDate
        }

        val startYear = calendarStart.get(Calendar.YEAR)
        val endYear = calendarEnd?.get(Calendar.YEAR)

        when (currentState) {
            State.START -> {
                calendarStart.set(Calendar.YEAR, year)
                if (endYear == null || calendarEnd == null) {
                    selectedStartDate = calendarStart.time
                } else if (year < endYear) {
                    selectedStartDate = calendarStart.time
                } else if (year > endYear) {
                    selectedStartDate = calendarEnd.time
                    selectedEndDate = calendarStart.time
                } else {
                    if (calendarStart.time > calendarEnd.time) {
                        selectedStartDate = calendarEnd.time
                        selectedEndDate = calendarStart.time
                    } else if (calendarStart.time < calendarEnd.time) {
                        selectedStartDate = calendarStart.time
                    }
                }
            }
            State.END -> {
                calendarEnd ?: return
                calendarEnd.set(Calendar.YEAR, year)
                if (year < startYear) {
                    selectedStartDate = calendarEnd.time
                    selectedEndDate = calendarStart.time
                } else if (year > startYear) {
                    selectedEndDate = calendarEnd.time
                } else {
                    if (calendarEnd.time < calendarStart.time) {
                        selectedStartDate = calendarEnd.time
                        selectedEndDate = calendarStart.time
                    } else if (calendarEnd.time > calendarStart.time) {
                        selectedEndDate = calendarEnd.time
                    }
                }
            }
        }
    }
}

/**
 * date to select
 */
enum class State {
    START, END
}
