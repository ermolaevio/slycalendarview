package ru.slybeaver.slycalendarview

import ru.slybeaver.slycalendarview.util.SlyCalendarUtil
import java.util.*

class SlyCalendarData {

    var selectedStartDate: Date? = null// first selected date
    var selectedEndDate: Date? = null // end selected date
    val currentDate: Date =
        SlyCalendarUtil.getCalendarWithoutTime(Date()).time // current showing date
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
    var isFutureDatesDisabled: Boolean = true // make future dates are not selectable
    val isDateState: Boolean
        get() {
            return when (currentState) {
                State.START_DATE,
                State.END_DATE,
                State.DEFAULT -> true
                else -> false
            }
        }

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

                        if (selectedDateTime == startDate.time
                            || selectedDateTime == endDate.time
                        ) {
                            selectedEndDate = null
                            selectedStartDate = selectedDate

                        } else if (selectedDateTime < endDate.time) {
                            selectedStartDate = selectedDate
                        } else if (selectedDateTime > endDate.time) {
                            selectedEndDate = selectedDate
                            currentState = State.END_DATE
                        }
                    }
                }
                State.END_DATE -> {
                    if (selectedDateTime == startDate.time
                        || selectedDateTime == endDate?.time
                    ) {
                        selectedEndDate = null
                        selectedStartDate = selectedDate
                        currentState = State.START_DATE

                    } else if (selectedDateTime < startDate.time) {
                        selectedStartDate = selectedDate
                        currentState = State.START_DATE
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
        var calendarStart: Calendar? =
            null // start calendar with selected year or current. It depends on the currentState
        if (selectedStartDate != null) {
            calendarStart = Calendar.getInstance()
            calendarStart.time = selectedStartDate
        }

        var calendarEnd: Calendar? =
            null // end calendar with selected year or current. It depends on the currentState
        val calendarToday = SlyCalendarUtil.getCalendarWithoutTime(currentDate)

        if (selectedEndDate != null) {
            calendarEnd = Calendar.getInstance()
            calendarEnd.time = selectedEndDate
        }

        val startYear = calendarStart?.get(Calendar.YEAR) ?: 0
        val endYear = calendarEnd?.get(Calendar.YEAR) ?: 0

        when (currentState) {
            State.START_YEAR -> {
                if (calendarStart == null) {
                    currentState = State.START_DATE
                    return
                }

                calendarStart.set(Calendar.YEAR, year)

                if (isFutureDatesDisabled && calendarStart.time > calendarToday.time) {
                    currentState = State.START_DATE
                    return
                } else if (calendarEnd == null) {
                    selectedStartDate = calendarStart.time
                } else if (year < endYear) {
                    selectedStartDate = calendarStart.time
                } else if (year > endYear) {
                    selectedStartDate = calendarEnd.time
                    selectedEndDate = calendarStart.time
                } else {
                    if (calendarStart.time == calendarEnd.time
                        || calendarStart.time == selectedStartDate
                    ) {
                        selectedStartDate = calendarStart.time
                        selectedEndDate = null
                    } else if (calendarStart.time > calendarEnd.time) {
                        selectedStartDate = calendarEnd.time
                        selectedEndDate = calendarStart.time
                    } else if (calendarStart.time < calendarEnd.time) {
                        selectedStartDate = calendarStart.time
                    }
                }
                currentState = State.START_DATE
            }
            State.END_YEAR -> {
                if (calendarStart == null || calendarEnd == null) {
                    currentState = State.END_DATE
                    return
                }
                calendarEnd.set(Calendar.YEAR, year)
                if (isFutureDatesDisabled && calendarEnd.time > calendarToday.time) {
                    currentState = State.END_DATE
                    return
                } else if (year < startYear) {
                    selectedStartDate = calendarEnd.time
                    selectedEndDate = calendarStart.time
                } else if (year > startYear) {
                    selectedEndDate = calendarEnd.time
                } else {
                    if (calendarEnd.time == selectedEndDate
                        || calendarEnd.time == calendarStart.time
                    ) {
                        selectedStartDate = calendarStart.time
                        selectedEndDate = null
                        currentState = State.START_DATE
                        return
                    } else if (calendarEnd.time < calendarStart.time) {
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

    fun cancelYearState() {
        currentState = when (currentState) {
            State.START_YEAR -> State.START_DATE
            State.END_YEAR -> State.END_DATE
            else -> currentState
        }
    }
}

/**
 * state to know what the user will select
 */
enum class State {
    DEFAULT, // like START DATE, used only at begin when start date not selected
    START_DATE, END_DATE,
    START_YEAR, END_YEAR
}
