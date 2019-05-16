package ru.slybeaver.slycalendarview.adapter

import android.graphics.Color
import android.support.v4.content.ContextCompat
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import ru.slybeaver.slycalendarview.R
import ru.slybeaver.slycalendarview.listeners.YearSelectedListener
import ru.slybeaver.slycalendarview.util.SlyCalendarUtil
import java.util.*

internal class YearListAdapter(
    private val current: Int,
    private val listener: YearSelectedListener,
    private val isFutureDisabled: Boolean
) : RecyclerView.Adapter<YearListAdapter.YearHolder>() {

    private val firstYear = 1970

    override fun getItemCount() = 130 // 1970 - 2099

    private val currentYear = SlyCalendarUtil.getCalendarWithoutTime(Date()).get(Calendar.YEAR)

    override fun onCreateViewHolder(parent: ViewGroup, type: Int): YearHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.slycalendar_year_cell, parent, false)
        return YearHolder(view)
    }

    override fun onBindViewHolder(h: YearHolder, pos: Int) {
        val year = firstYear + pos
        h.year.text = year.toString()
        if (current == year) {
            h.year.textSize = 25f
            h.year.setTextColor(ContextCompat.getColor(h.year.context, R.color.slycalendar_defSelectedColor))
        } else {
            h.year.textSize = 16f
            h.year.setTextColor(Color.BLACK)
        }

        if (year <= currentYear || !isFutureDisabled) {
            h.year.alpha = 1f
            h.year.isEnabled = true
        } else {
            h.year.alpha = 0.1f
            h.year.isEnabled = false
        }
        h.year.setOnClickListener { listener.onYearSelected(year) }
    }

    fun getPositionToScroll(): Int {
        var position = current - firstYear
        if (position > 2) position -= 3
        return position
    }

    class YearHolder(view: View) : RecyclerView.ViewHolder(view) {
        val year: TextView = view.findViewById(R.id.year)
    }
}