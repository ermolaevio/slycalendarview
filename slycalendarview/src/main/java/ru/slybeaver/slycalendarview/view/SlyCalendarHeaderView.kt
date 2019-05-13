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

    var listener: DateSwitchedListener? = null
    private var currentState: State = State.END
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
            if (currentState != State.START) {
                currentState = State.START
                listener?.onDateSwitched(currentState)
                switchDate()
            }
        }
        endDate.setOnClickListener {
            if (currentState != State.END) {
                currentState = State.END
                listener?.onDateSwitched(currentState)
                switchDate()
            }
        }
        // todo select year
    }

    fun updateHeader(start: Date, end: Date?) {
        val calendarStart = Calendar.getInstance()
        calendarStart.time = start

        val calendarEnd = end?.let {
            val instance = Calendar.getInstance()
            instance.time = end
            instance
        }
        val sf = SimpleDateFormat("dd MMM", Locale.getDefault())

        startYear.text = calendarStart.get(Calendar.YEAR).toString()
        startDate.text = sf.format(calendarStart.time)

        if (calendarEnd != null) {
            endYear.text = calendarEnd.get(Calendar.YEAR).toString()
            endDate.text = sf.format(calendarEnd.time)
        } else {
            endYear.text = null
            endDate.text = "–"
        }
    }

    private fun switchDate() {
        // todo to constants
        when (currentState) {
            State.START -> {
                startYear.alpha = 1f
                startDate.alpha = 1f
                endYear.alpha = 0.5f
                endDate.alpha = 0.5f
            }
            State.END -> {
                startYear.alpha = 0.5f
                startDate.alpha = 0.5f
                endYear.alpha = 1f
                endDate.alpha = 1f
            }
        }
    }
}