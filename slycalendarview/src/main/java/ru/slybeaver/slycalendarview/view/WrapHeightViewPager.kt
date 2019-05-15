package ru.slybeaver.slycalendarview.view

import android.content.Context
import android.support.v4.view.ViewPager
import android.util.AttributeSet
import android.view.View

class WrapHeightViewPager @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null
) : ViewPager(context, attrs) {

    override fun onMeasure(widthSpec: Int, heightSpec: Int) {
        var height = 0
        var heightMeasureSpec = 0
        for (i in 0 until childCount) {
            val child = getChildAt(i)
            child.measure(
                widthSpec,
                View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED)
            )
            val h = child.measuredHeight
            if (h > height) height = h
        }

        if (height != 0) {
            heightMeasureSpec =
                View.MeasureSpec.makeMeasureSpec(height, View.MeasureSpec.EXACTLY)
        }

        super.onMeasure(widthSpec, heightMeasureSpec)
    }
}