package ru.slybeaver.slycalendarview.view

import android.content.Context
import android.support.v4.view.ViewPager
import android.util.AttributeSet
import android.view.MotionEvent
import ru.slybeaver.slycalendarview.listeners.CurrentMonthListener
import ru.slybeaver.slycalendarview.util.MonthChangedListener

internal class SlyMonthViewPager @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null
) : ViewPager(context, attrs) {

    private var disableSwipeToRight = false
    private var startXValue = 0f

    // set height of bigger child
    override fun onMeasure(widthSpec: Int, heightSpec: Int) {
        var height = 0
        var heightMeasureSpec = 0
        for (i in 0 until childCount) {
            val child = getChildAt(i)
            child.measure(
                widthSpec,
                MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED)
            )
            val h = child.measuredHeight
            if (h > height) height = h
        }

        if (height != 0) {
            heightMeasureSpec =
                MeasureSpec.makeMeasureSpec(height, MeasureSpec.EXACTLY)
        }
        super.onMeasure(widthSpec, heightMeasureSpec)
    }

    override fun onInterceptTouchEvent(event: MotionEvent): Boolean {
        if (isSwipeToRightAllowed(event)) {
            return super.onInterceptTouchEvent(event)
        }
        return false
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        if (isSwipeToRightAllowed(event)) {
            return super.onTouchEvent(event)
        }
        return false
    }

    fun disableFutureMonths(positionToDisable: Int, listener: CurrentMonthListener) {

        addOnPageChangeListener(object : MonthChangedListener() {
            override fun onPageSelected(position: Int) {
                disableSwipeToRight =
                    if (positionToDisable == position) {
                        listener.onCurrentMonthSelected(true)
                        true
                    } else {
                        listener.onCurrentMonthSelected(false)
                        false
                    }
            }
        })
    }

    private fun isSwipeToRightAllowed(event: MotionEvent): Boolean {
        if (disableSwipeToRight.not()) return true

        if (event.action == MotionEvent.ACTION_DOWN) {
            startXValue = event.x
            return true
        }

        if (event.action == MotionEvent.ACTION_MOVE) {
            val diff = event.x - startXValue
            // swipe from right to left detected
            if (diff < 0) return false
        }
        return true
    }
}

