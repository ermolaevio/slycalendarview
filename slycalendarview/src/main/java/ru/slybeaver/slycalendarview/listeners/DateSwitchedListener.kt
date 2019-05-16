package ru.slybeaver.slycalendarview.listeners

import ru.slybeaver.slycalendarview.State

internal interface DateSwitchedListener {
    fun onDateSwitched(state: State)
    fun onYearClicked(state: State)
}