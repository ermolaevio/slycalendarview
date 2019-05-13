package ru.slybeaver.truecalendar

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast

import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

import ru.slybeaver.slycalendarview.SlyCalendarDialog

class MainActivity : AppCompatActivity(), SlyCalendarDialog.Callback {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        findViewById<View>(R.id.btnShowCalendar).setOnClickListener {
            SlyCalendarDialog()
                .setSingle(false)
                .setCallback(this@MainActivity)
                .show(supportFragmentManager, "TAG_SLYCALENDAR")
        }
    }

    override fun onCancelled() {
        //Nothing
    }

    override fun onDataSelected(
        firstDate: Calendar?,
        secondDate: Calendar?
    ) {
        if (firstDate != null) {
            if (secondDate == null) {
                Toast.makeText(
                    this,
                    SimpleDateFormat(getString(R.string.timeFormat), Locale.getDefault()).format(
                        firstDate.time
                    ),
                    Toast.LENGTH_LONG

                ).show()
            } else {
                Toast.makeText(
                    this,
                    getString(
                        R.string.period,
                        SimpleDateFormat(
                            getString(R.string.dateFormat),
                            Locale.getDefault()
                        ).format(firstDate.time),
                        SimpleDateFormat(
                            getString(R.string.timeFormat),
                            Locale.getDefault()
                        ).format(secondDate.time)
                    ),
                    Toast.LENGTH_LONG

                ).show()
            }
        }
    }
}
