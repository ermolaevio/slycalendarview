package ru.slybeaver.slycalendarview.view

import android.content.Context
import android.support.constraint.ConstraintLayout
import android.util.AttributeSet
import android.widget.TextView
import ru.slybeaver.slycalendarview.R
import ru.slybeaver.slycalendarview.State
import ru.slybeaver.slycalendarview.listeners.DateSwitchedListener
import java.text.SimpleDateFormat
import java.util.*

internal class SlyCalendarHeaderView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : ConstraintLayout(context, attrs, defStyleAttr) {

    companion object {
        private const val NOT_SELECTED = "–"
        private const val ALPHA_100 = 1f
        private const val ALPHA_50 = 0.5f
        private const val DATE_FORMAT = "dd MMM"
    }

    var listener: DateSwitchedListener? = null
    var currentState: State = State.DEFAULT
        set(value) {
            if (field != value) {
                field = value
                switchDate()
            }
        }
    private val startYear: TextView
    private val endYear: TextView
    private val startDate: TextView
    private val endDate: TextView

    init {
        inflate(context, R.layout.slycalendar_header, this)
        val padding = context.resources.getDimensionPixelSize(R.dimen.slycalendar_header_padding)
        setPadding(padding, padding, padding, padding)
        setBackgroundResource(R.color.slycalendar_defHeaderColor)
        startYear = findViewById(R.id.txtStartYear)
        startDate = findViewById(R.id.txtStartDate)
        endYear = findViewById(R.id.txtEndYear)
        endDate = findViewById(R.id.txtEndDate)

        startDate.setOnClickListener {
            when (currentState) {
                State.END_DATE -> switchDateState(State.START_DATE)
                State.END_YEAR -> switchYear(State.START_YEAR)
                else -> {
                    //ignore
                }
            }
        }
        endDate.setOnClickListener {
            when (currentState) {
                State.START_DATE -> switchDateState(State.END_DATE)
                State.START_YEAR -> switchYear(State.END_YEAR)
                else -> {
                    //ignore
                }
            }
        }
        startYear.setOnClickListener {
            when (currentState) {
                State.END_YEAR, State.END_DATE, State.START_DATE -> switchYear(State.START_YEAR)
                else -> {
                    //ignore
                }
            }
        }
        endYear.setOnClickListener {
            when (currentState) {
                State.START_YEAR, State.START_DATE, State.END_DATE -> switchYear(State.END_YEAR)
                else -> {
                    //ignore
                }
            }
        }
    }

    private fun switchDateState(state: State) {
        currentState = state
        listener?.onDateSwitched(currentState)
    }

    private fun switchYear(state: State) {
        currentState = state
        listener?.onYearClicked(currentState)
    }

    fun updateHeader(start: Date?, end: Date?) {
        val calendarStart = start?.let {
            val instance = Calendar.getInstance()
            instance.time = it
            instance
        }

        val calendarEnd = end?.let {
            val instance = Calendar.getInstance()
            instance.time = it
            instance
        }
        val sf = SimpleDateFormat(DATE_FORMAT, Locale.getDefault())

        val current = Calendar.getInstance()
        val currentYear = current.get(Calendar.YEAR).toString()

        if (calendarStart != null) {
            startYear.text = calendarStart.get(Calendar.YEAR).toString()
            startDate.text = sf.format(calendarStart.time)
        } else {
            startYear.text = currentYear
            startDate.text = NOT_SELECTED
        }

        if (calendarEnd != null) {
            endYear.text = calendarEnd.get(Calendar.YEAR).toString()
            endDate.text = sf.format(calendarEnd.time)
        } else {
            endYear.text = currentYear
            endDate.text = NOT_SELECTED
        }
    }

    private fun switchDate() {
        when (currentState) {
            State.START_DATE, State.DEFAULT, State.START_YEAR -> {
                startYear.alpha = ALPHA_100
                startDate.alpha = ALPHA_100
                endYear.alpha = ALPHA_50
                endDate.alpha = ALPHA_50
            }
            State.END_DATE, State.END_YEAR -> {
                startYear.alpha = ALPHA_50
                startDate.alpha = ALPHA_50
                endYear.alpha = ALPHA_100
                endDate.alpha = ALPHA_100
            }
        }
    }
}