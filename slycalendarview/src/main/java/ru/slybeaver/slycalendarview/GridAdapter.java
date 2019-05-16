package ru.slybeaver.slycalendarview;

import android.content.Context;
import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.LayerDrawable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import ru.slybeaver.slycalendarview.listeners.DateSelectListener;
import ru.slybeaver.slycalendarview.listeners.GridChangeListener;
import ru.slybeaver.slycalendarview.util.SlyCalendarUtil;

/**
 * Created by psinetron on 29/11/2018.
 * http://slybeaver.ru
 */
public class GridAdapter extends ArrayAdapter {

    private SlyCalendarData calendarData;
    private int shiftMonth;
    private ArrayList<Date> monthlyDates = new ArrayList<>();
    private DateSelectListener listener;
    private LayoutInflater inflater;
    private GridChangeListener gridListener;

    private final Calendar today = SlyCalendarUtil.INSTANCE.getCalendarWithoutTime(new Date());

    public GridAdapter(
            @NonNull Context context,
            @NonNull SlyCalendarData calendarData,
            int shiftMonth,
            @Nullable DateSelectListener listener,
            GridChangeListener gridListener
    ) {
        super(context, R.layout.slycalendar_single_cell);
        this.calendarData = calendarData;
        this.listener = listener;
        this.inflater = LayoutInflater.from(context);
        this.shiftMonth = shiftMonth;
        this.gridListener = gridListener;
        init();
    }

    @NonNull
    @Override
    public View getView(final int position, @Nullable View convertView, @Nullable ViewGroup parent) {
        Calendar dateCal = Calendar.getInstance();
        dateCal.setTime(monthlyDates.get(position));

        Calendar calendarStart = null;
        if (calendarData.getSelectedStartDate() != null) {
            calendarStart = SlyCalendarUtil.INSTANCE.getCalendarWithoutTime(calendarData.getSelectedStartDate());
        }

        Calendar calendarEnd = null;
        if (calendarData.getSelectedEndDate() != null) {
            calendarEnd = SlyCalendarUtil.INSTANCE.getCalendarWithoutTime(calendarData.getSelectedEndDate());
        }

        View view = convertView;
        if (view == null) {
            view = inflater.inflate(R.layout.slycalendar_single_cell, parent, false);
        }

        ((TextView) view.findViewById(R.id.txtDate)).setText(String.valueOf(dateCal.get(Calendar.DAY_OF_MONTH)));
        view.findViewById(R.id.cellView).setBackgroundResource(R.color.slycalendar_defBackgroundColor);


        view.findViewById(R.id.cellView).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Calendar selectedDate = SlyCalendarUtil.INSTANCE.getCalendarWithoutTime(monthlyDates.get(position));
                if (checkDateIsEnabled(selectedDate)) {
                    if (listener != null) listener.dateSelect(selectedDate.getTime());
                    gridListener.gridChanged();
                }
            }
        });

        view.findViewById(R.id.cellView).setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                Calendar selectedDate = SlyCalendarUtil.INSTANCE.getCalendarWithoutTime(monthlyDates.get(position));
                if (checkDateIsEnabled(selectedDate)) {
                    if (listener != null) listener.dateLongSelect(selectedDate.getTime());
                    gridListener.gridChanged();
                    return true;
                }
                return false;
            }
        });

        view.findViewById(R.id.cellView).setBackgroundColor(calendarData.getBackgroundColor());

        // todo clean here
        if (calendarStart != null && calendarEnd != null) {
            if (dateCal.get(Calendar.DAY_OF_YEAR) == calendarStart.get(Calendar.DAY_OF_YEAR) && dateCal.get(Calendar.YEAR) == calendarStart.get(Calendar.YEAR)) {
                LayerDrawable shape = (LayerDrawable) ContextCompat.getDrawable(getContext(), R.drawable.slycalendar_start_day);
                assert shape != null;
                ((GradientDrawable) shape.findDrawableByLayerId(R.id.dateShapeItem)).setColor(calendarData.getSelectedColor());
                (shape.findDrawableByLayerId(R.id.dateShapeItem)).setAlpha(20);
                view.findViewById(R.id.cellView).setBackground(shape);
            } else if (dateCal.getTimeInMillis() > calendarStart.getTimeInMillis() && dateCal.getTimeInMillis() < calendarEnd.getTimeInMillis()) {
                LayerDrawable shape = (LayerDrawable) ContextCompat.getDrawable(getContext(), R.drawable.slycalendar_middle_day);
                assert shape != null;
                ((GradientDrawable) shape.findDrawableByLayerId(R.id.dateShapeItem)).setColor(calendarData.getSelectedColor());
                (shape.findDrawableByLayerId(R.id.dateShapeItem)).setAlpha(20);
                view.findViewById(R.id.cellView).setBackground(shape);
                if (position % 7 == 0) {
                    shape = (LayerDrawable) ContextCompat.getDrawable(getContext(), R.drawable.slycalendar_start_day_month);
                    assert shape != null;
                    ((GradientDrawable) shape.findDrawableByLayerId(R.id.dateShapeItem)).setColor(calendarData.getSelectedColor());
                    (shape.findDrawableByLayerId(R.id.dateShapeItem)).setAlpha(20);
                    view.findViewById(R.id.cellView).setBackground(shape);
                }
                if ((position + 1) % 7 == 0) {
                    shape = (LayerDrawable) ContextCompat.getDrawable(getContext(), R.drawable.slycalendar_end_day_month);
                    assert shape != null;
                    ((GradientDrawable) shape.findDrawableByLayerId(R.id.dateShapeItem)).setColor(calendarData.getSelectedColor());
                    (shape.findDrawableByLayerId(R.id.dateShapeItem)).setAlpha(20);
                    view.findViewById(R.id.cellView).setBackground(shape);
                }

            } else if (dateCal.get(Calendar.DAY_OF_YEAR) == calendarEnd.get(Calendar.DAY_OF_YEAR) && dateCal.get(Calendar.YEAR) == calendarEnd.get(Calendar.YEAR)) {
                LayerDrawable shape = (LayerDrawable) ContextCompat.getDrawable(getContext(), R.drawable.slycalendar_end_day);
                assert shape != null;
                ((GradientDrawable) shape.findDrawableByLayerId(R.id.dateShapeItem)).setColor(calendarData.getSelectedColor());
                (shape.findDrawableByLayerId(R.id.dateShapeItem)).setAlpha(20);
                view.findViewById(R.id.cellView).setBackground(shape);
            }
        }

        Calendar currentDate = Calendar.getInstance();
        currentDate.setTime(calendarData.getCurrentDate());
        currentDate.add(Calendar.MONTH, shiftMonth);

        view.findViewById(R.id.frameSelected).setBackgroundResource(0);
        ((TextView) view.findViewById(R.id.txtDate)).setTextColor(calendarData.getTextColor());
        if (calendarStart != null && dateCal.get(Calendar.DAY_OF_YEAR) == calendarStart.get(Calendar.DAY_OF_YEAR) && currentDate.get(Calendar.MONTH) == dateCal.get(Calendar.MONTH) && dateCal.get(Calendar.YEAR) == calendarStart.get(Calendar.YEAR)) {
            LayerDrawable shape = (LayerDrawable) ContextCompat.getDrawable(getContext(), R.drawable.slycalendar_selected_day);
            assert shape != null;
            ((GradientDrawable) shape.findDrawableByLayerId(R.id.selectedDateShapeItem)).setColor(calendarData.getSelectedColor());
            view.findViewById(R.id.frameSelected).setBackground(shape);
            ((TextView) view.findViewById(R.id.txtDate)).setTextColor(calendarData.getSelectedTextColor());
        }

        if (calendarEnd != null && dateCal.get(Calendar.DAY_OF_YEAR) == calendarEnd.get(Calendar.DAY_OF_YEAR) && currentDate.get(Calendar.MONTH) == dateCal.get(Calendar.MONTH) && dateCal.get(Calendar.YEAR) == calendarEnd.get(Calendar.YEAR)) {
            LayerDrawable shape = (LayerDrawable) ContextCompat.getDrawable(getContext(), R.drawable.slycalendar_selected_day);
            assert shape != null;
            ((GradientDrawable) shape.findDrawableByLayerId(R.id.selectedDateShapeItem)).setColor(calendarData.getSelectedColor());
            view.findViewById(R.id.frameSelected).setBackground(shape);
            ((TextView) view.findViewById(R.id.txtDate)).setTextColor(calendarData.getSelectedTextColor());
        }

        if (checkDateIsEnabled(dateCal)) {
            view.findViewById(R.id.cellView).setEnabled(true);
            (view.findViewById(R.id.txtDate)).setAlpha(1);
            // highlight day of current month
            if (currentDate.get(Calendar.MONTH) == dateCal.get(Calendar.MONTH)) {
                // todo change color or alpha
                (view.findViewById(R.id.txtDate)).setAlpha(1);
            } else {
                (view.findViewById(R.id.txtDate)).setAlpha(.4f);
            }
        } else {
            view.findViewById(R.id.cellView).setEnabled(false);
            (view.findViewById(R.id.txtDate)).setAlpha(.1f);
        }

        return view;
    }

    @Override
    public int getCount() {
        return monthlyDates.size();
    }

    @Nullable
    @Override
    public Date getItem(int position) {
        return monthlyDates.get(position);
    }

    public void update(int shiftMonth) {
        this.shiftMonth = shiftMonth;
        init();
        notifyDataSetChanged();
    }

    private void init() {
        monthlyDates = new ArrayList<>();
        Calendar calendar = Calendar.getInstance(Locale.getDefault());
        calendar.setTime(calendarData.getCurrentDate());
        calendar.add(Calendar.MONTH, shiftMonth);
        calendar.set(Calendar.DAY_OF_MONTH, 1);

        int dOfWeek = calendar.get(Calendar.DAY_OF_WEEK);
        if (calendarData.isFirstMonday() && dOfWeek == Calendar.SUNDAY) dOfWeek = 8;
        int firstDayOfTheMonth = calendarData.isFirstMonday() ? dOfWeek - 2 : dOfWeek - 1;
        calendar.add(Calendar.DAY_OF_MONTH, -firstDayOfTheMonth);
        int MAX_CALENDAR_COLUMN = 42;

        while (monthlyDates.size() < MAX_CALENDAR_COLUMN) {
            monthlyDates.add(calendar.getTime());
            calendar.add(Calendar.DAY_OF_MONTH, 1);
        }
    }

    private boolean checkDateIsEnabled(Calendar date) {
        if (!calendarData.isFutureDatesDisabled()) return true;

        return date.getTimeInMillis() <= today.getTimeInMillis();
    }
}
