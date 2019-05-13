package ru.slybeaver.slycalendarview

import java.util.*

internal class SlyCalendarData {

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

        val state = currentState
        if (isSingle) {
            selectedStartDate = selectedDate
        } else {
            val selectedDateTime = selectedDate.time

            when (state) {
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
}

enum class State {
    START, END
}
