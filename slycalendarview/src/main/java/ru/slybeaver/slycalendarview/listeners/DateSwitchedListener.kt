package ru.slybeaver.slycalendarview.listeners

import ru.slybeaver.slycalendarview.State

interface DateSwitchedListener {
    fun onDateSwitched(state: State)
    fun onYearClicked(state: State)
}