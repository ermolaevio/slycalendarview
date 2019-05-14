package ru.slybeaver.slycalendarview

import java.util.*

class SlyCalendarData {

    var selectedStartDate: Date? = null// first selected date
    var selectedEndDate: Date? = null // ended selected date
    var showDate: Date? = Calendar.getInstance().time // current showing date

    var isFirstMonday = true
    var isSingle = false //

    var backgroundColor: Int? = null
    var headerColor: Int? = null
    var headerTextColor: Int? = null
    var textColor: Int? = null
    var selectedColor: Int? = null
    var selectedTextColor: Int? = null
    var timeTheme: Int? = null

    var currentState = State.DEFAULT // current date to select

    fun setNewSelectedDate(selectedDate: Date) {
        val startDate = selectedStartDate
        val endDate = selectedEndDate

        if (isSingle || startDate == null) {
            selectedStartDate = selectedDate
            currentState = State.END_DATE
        } else {
            val selectedDateTime = selectedDate.time

            when (currentState) {
                State.START_DATE -> {
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
                State.END_DATE -> {
                    if (selectedDateTime < startDate.time) {
                        selectedStartDate = selectedDate
                        selectedEndDate = startDate
                    } else if (selectedDateTime > startDate.time) {
                        selectedEndDate = selectedDate
                    }
                }
                else -> {
                    // ignored
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
            State.START_YEAR -> {
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
                currentState = State.START_DATE
            }
            State.END_YEAR -> {
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
                currentState = State.END_DATE
            }
            else -> {
                // ignored
            }
        }
    }
}

/**
 * state to know what the user will select
 */
enum class State {
    DEFAULT, // select START_DATE first time
    START_DATE, END_DATE,
    START_YEAR, END_YEAR

}
