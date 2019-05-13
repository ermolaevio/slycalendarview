package ru.slybeaver.slycalendarview;

import android.content.Context;
import android.content.res.TypedArray;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import ru.slybeaver.slycalendarview.adapter.YearListAdapter;
import ru.slybeaver.slycalendarview.listeners.DateSelectListener;
import ru.slybeaver.slycalendarview.listeners.DialogCompleteListener;
import ru.slybeaver.slycalendarview.listeners.YearSelectedListener;

/**
 * Created by psinetron on 29/11/2018.
 * http://slybeaver.ru
 */
public class SlyCalendarView extends FrameLayout implements DateSelectListener {

    private SlyCalendarData slyCalendarData;

    private SlyCalendarDialog.Callback callback = null;

    private DialogCompleteListener completeListener = null;

    // todo добавить сюда vpager как перем

    private AttributeSet attrs = null;
    private int defStyleAttr = 0;


    public SlyCalendarView(Context context) {
        super(context);
    }

    public SlyCalendarView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        this.attrs = attrs;
    }

    public SlyCalendarView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.attrs = attrs;
        this.defStyleAttr = defStyleAttr;
    }

    public void setCallback(@Nullable SlyCalendarDialog.Callback callback) {
        this.callback = callback;
    }

    public void setCompleteListener(@Nullable DialogCompleteListener completeListener) {
        this.completeListener = completeListener;
    }

    public void setSlyCalendarData(SlyCalendarData slyCalendarData) {
        this.slyCalendarData = slyCalendarData;

        TypedArray typedArray = getContext().obtainStyledAttributes(attrs, R.styleable.SlyCalendarView, defStyleAttr, 0);

        if (slyCalendarData.getBackgroundColor() == null) {
            slyCalendarData.setBackgroundColor(typedArray.getColor(R.styleable.SlyCalendarView_backgroundColor, ContextCompat.getColor(getContext(), R.color.slycalendar_defBackgroundColor)));
        }
        if (slyCalendarData.getHeaderColor() == null) {
            slyCalendarData.setHeaderColor(typedArray.getColor(R.styleable.SlyCalendarView_headerColor, ContextCompat.getColor(getContext(), R.color.slycalendar_defHeaderColor)));
        }
        if (slyCalendarData.getHeaderTextColor() == null) {
            slyCalendarData.setHeaderTextColor(typedArray.getColor(R.styleable.SlyCalendarView_headerTextColor, ContextCompat.getColor(getContext(), R.color.slycalendar_defHeaderTextColor)));
        }
        if (slyCalendarData.getTextColor() == null) {
            slyCalendarData.setTextColor(typedArray.getColor(R.styleable.SlyCalendarView_textColor, ContextCompat.getColor(getContext(), R.color.slycalendar_defTextColor)));
        }
        if (slyCalendarData.getSelectedColor() == null) {
            slyCalendarData.setSelectedColor(typedArray.getColor(R.styleable.SlyCalendarView_selectedColor, ContextCompat.getColor(getContext(), R.color.slycalendar_defSelectedColor)));
        }
        if (slyCalendarData.getSelectedTextColor() == null) {
            slyCalendarData.setSelectedTextColor(typedArray.getColor(R.styleable.SlyCalendarView_selectedTextColor, ContextCompat.getColor(getContext(), R.color.slycalendar_defSelectedTextColor)));
        }
        typedArray.recycle();
        inflate(getContext(), R.layout.slycalendar_frame, this);

        init();
        initCalendar();
        showHeader();
    }

    private void init() {
        final ViewPager vpager = findViewById(R.id.content);
        final MonthPagerAdapter vadapter = new MonthPagerAdapter(slyCalendarData, this, vpager);
        vpager.setAdapter(vadapter);
        vpager.setCurrentItem(vadapter.getCount() / 2);

        findViewById(R.id.txtYear).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(final View v) {
                final View arrows = findViewById(R.id.arrows_container);
                arrows.setVisibility(View.GONE);
                vpager.setVisibility(View.INVISIBLE);
                v.setClickable(false);

                final RecyclerView list = findViewById(R.id.years);
                list.setVisibility(View.VISIBLE);
                list.setLayoutManager(new LinearLayoutManager(getContext()));

                final Calendar calendar = Calendar.getInstance();
                calendar.setTime(slyCalendarData.getShowDate());
                int shiftMonth = vpager.getCurrentItem() - (vadapter.getCount() / 2);
                calendar.add(Calendar.MONTH, shiftMonth);
                final int currentYear = calendar.get(Calendar.YEAR);
                list.setAdapter(new YearListAdapter(currentYear, new YearSelectedListener() {
                    @Override
                    public void onYearSelected(int year) {
                        arrows.setVisibility(View.VISIBLE);
                        vpager.setVisibility(View.VISIBLE);
                        list.setVisibility(View.GONE);
                        list.setAdapter(null);
                        v.setClickable(true);

                        int shiftMonth = (year - currentYear) * 12;
                        vpager.setCurrentItem(vpager.getCurrentItem() + shiftMonth, false);
                        ((TextView) findViewById(R.id.txtYear)).setText(String.valueOf(year));
                    }
                }));
                // todo move from here to year adapter
                int position = currentYear - 1970;
                if (position > 2) position -= 3;
                list.scrollToPosition(position);
            }
        });
    }

    // drawing header
    private void showHeader() {
        Calendar calendarStart = Calendar.getInstance();
        Calendar calendarEnd = null;
        if (slyCalendarData.getSelectedStartDate() != null) {
            calendarStart.setTime(slyCalendarData.getSelectedStartDate());
        } else {
            calendarStart.setTime(slyCalendarData.getShowDate());
        }

        if (slyCalendarData.getSelectedEndDate() != null) {
            calendarEnd = Calendar.getInstance();
            calendarEnd.setTime(slyCalendarData.getSelectedEndDate());
        }

        ((TextView) findViewById(R.id.txtYear)).setText(String.valueOf(calendarStart.get(Calendar.YEAR)));


        if (calendarEnd == null) {
            ((TextView) findViewById(R.id.txtSelectedPeriod)).setText(
                    new SimpleDateFormat("EE, dd MMMM", Locale.getDefault()).format(calendarStart.getTime())
            );
        } else {
            SimpleDateFormat dateFormatMonth = new SimpleDateFormat("EE, dd MMM", Locale.getDefault());
            SimpleDateFormat dateFormat = new SimpleDateFormat("EE, dd", Locale.getDefault());

            if (calendarStart.get(Calendar.MONTH) == calendarEnd.get(Calendar.MONTH)) {
                ((TextView) findViewById(R.id.txtSelectedPeriod)).setText(
                        // todo to upper case first letter
                        getContext().getString(R.string.slycalendar_dates_period, dateFormat.format(calendarStart.getTime()), dateFormatMonth.format(calendarEnd.getTime()))
                );
            } else {
                ((TextView) findViewById(R.id.txtSelectedPeriod)).setText(
                        getContext().getString(R.string.slycalendar_dates_period, dateFormatMonth.format(calendarStart.getTime()), dateFormatMonth.format(calendarEnd.getTime()))
                );
            }
        }
    }

    @Override
    public void dateSelect(Date selectedDate) {
        if (slyCalendarData.getSelectedStartDate() == null || slyCalendarData.isSingle()) {
            slyCalendarData.setSelectedStartDate(selectedDate);
        } else if (slyCalendarData.getSelectedEndDate() == null) {
            if (selectedDate.getTime() < slyCalendarData.getSelectedStartDate().getTime()) {
                slyCalendarData.setSelectedEndDate(slyCalendarData.getSelectedStartDate());
                slyCalendarData.setSelectedStartDate(selectedDate);
            } else if (selectedDate.getTime() == slyCalendarData.getSelectedStartDate().getTime()) {
                slyCalendarData.setSelectedEndDate(null);
                slyCalendarData.setSelectedStartDate(selectedDate);
            } else if (selectedDate.getTime() > slyCalendarData.getSelectedStartDate().getTime()) {
                slyCalendarData.setSelectedEndDate(selectedDate);
            }
        } else {
            slyCalendarData.setSelectedEndDate(null);
            slyCalendarData.setSelectedStartDate(selectedDate);
        }
        showHeader();
    }

    @Override
    public void dateLongSelect(Date selectedDate) {
        slyCalendarData.setSelectedEndDate(null);
        slyCalendarData.setSelectedStartDate(selectedDate);
        showHeader();
    }

    private void initCalendar() {
        paintCalendar();
        initListenersForButtons();
    }

    private void initListenersForButtons() {
        findViewById(R.id.txtCancel).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (callback != null) {
                    callback.onCancelled();
                }
                if (completeListener != null) {
                    completeListener.complete();
                }
            }
        });

        findViewById(R.id.txtSave).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (callback != null) {
                    Calendar start = null;
                    Calendar end = null;
                    if (slyCalendarData.getSelectedStartDate() != null) {
                        start = Calendar.getInstance();
                        start.setTime(slyCalendarData.getSelectedStartDate());
                    }
                    if (slyCalendarData.getSelectedEndDate() != null) {
                        end = Calendar.getInstance();
                        end.setTime(slyCalendarData.getSelectedEndDate());
                    }
                    callback.onDataSelected(start, end);
                }
                if (completeListener != null) {
                    completeListener.complete();
                }
            }
        });

        findViewById(R.id.btnMonthPrev).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                ViewPager vpager = findViewById(R.id.content);
                vpager.setCurrentItem(vpager.getCurrentItem() - 1);
            }
        });

        findViewById(R.id.btnMonthNext).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                ViewPager vpager = findViewById(R.id.content);
                vpager.setCurrentItem(vpager.getCurrentItem() + 1);
            }
        });
    }

    private void paintCalendar() {
        findViewById(R.id.mainFrame).setBackgroundColor(slyCalendarData.getBackgroundColor());
        findViewById(R.id.headerView).setBackgroundColor(slyCalendarData.getHeaderColor());
        ((TextView) findViewById(R.id.txtYear)).setTextColor(slyCalendarData.getHeaderTextColor());
        ((TextView) findViewById(R.id.txtSelectedPeriod)).setTextColor(slyCalendarData.getHeaderTextColor());
    }
}
