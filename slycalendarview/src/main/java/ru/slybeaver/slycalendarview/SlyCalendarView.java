package ru.slybeaver.slycalendarview;

import android.content.Context;
import android.content.res.TypedArray;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.view.View;
import android.widget.FrameLayout;

import org.jetbrains.annotations.NotNull;

import java.util.Calendar;
import java.util.Date;

import ru.slybeaver.slycalendarview.adapter.YearListAdapter;
import ru.slybeaver.slycalendarview.listeners.CurrentMonthListener;
import ru.slybeaver.slycalendarview.listeners.DateSelectListener;
import ru.slybeaver.slycalendarview.listeners.DateSwitchedListener;
import ru.slybeaver.slycalendarview.listeners.DialogCompleteListener;
import ru.slybeaver.slycalendarview.listeners.YearSelectedListener;
import ru.slybeaver.slycalendarview.util.SlyCalendarUtil;
import ru.slybeaver.slycalendarview.view.SlyCalendarHeaderView;
import ru.slybeaver.slycalendarview.view.SlyMonthViewPager;

/**
 * Created by psinetron on 29/11/2018.
 * http://slybeaver.ru
 */
public class SlyCalendarView extends FrameLayout
        implements DateSelectListener, DateSwitchedListener, YearSelectedListener {

    private SlyCalendarData slyCalendarData;
    private SlyCalendarDialog.Callback callback = null;
    private DialogCompleteListener completeListener = null;

    private AttributeSet attrs = null;
    private int defStyleAttr = 0;
    private SlyCalendarHeaderView headerView;
    private View arrows;
    private SlyMonthViewPager viewPager;
    private RecyclerView yearsList;
    private View nextArrow;

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
        updateHeader();
    }

    private void init() {
        arrows = findViewById(R.id.arrows_container);
        nextArrow = findViewById(R.id.btnMonthNext);
        headerView = findViewById(R.id.headerView);
        viewPager = findViewById(R.id.content);
        yearsList = findViewById(R.id.years);

        final MonthPagerAdapter vadapter = new MonthPagerAdapter(slyCalendarData, this, viewPager);
        viewPager.setAdapter(vadapter);
        int startPosition = SlyCalendarUtil.INSTANCE.startPosition(vadapter.getCount()); // current date
        if (slyCalendarData.isFutureDatesDisabled()) {
            viewPager.disableFutureMonths(startPosition, new CurrentMonthListener() {
                @Override
                public void onCurrentMonthSelected(boolean selected) {
                    nextArrow.setEnabled(!selected);
                    nextArrow.setVisibility(selected ? View.INVISIBLE : View.VISIBLE);
                }
            });
        }
        viewPager.setCurrentItem(startPosition);

        yearsList.setLayoutManager(new LinearLayoutManager(getContext()));

        headerView.setListener(this);
    }

    @Override
    public void dateSelect(Date selectedDate) {
        slyCalendarData.setNewSelectedDate(selectedDate);
        updateHeader();
    }

    private void updateHeader() {
        headerView.updateHeader(
                slyCalendarData.getSelectedStartDate(),
                slyCalendarData.getSelectedEndDate()
        );
        headerView.setCurrentState(slyCalendarData.getCurrentState());
    }

    @Override
    public void dateLongSelect(Date selectedDate) {
        slyCalendarData.setSelectedEndDate(null);
        slyCalendarData.setSelectedStartDate(selectedDate);
        updateHeader();
    }

    @Override
    public void onDateSwitched(@NotNull State state) {
        slyCalendarData.setCurrentState(state);
    }

    @Override
    public void onYearClicked(@NotNull State state) {
        slyCalendarData.setCurrentState(state);
        switchToYearOrMonth(true);

        Calendar calendar = Calendar.getInstance();
        calendar.setTime(slyCalendarData.getCurrentDate());
        PagerAdapter adapter = viewPager.getAdapter();
        if (adapter == null) return;

        if (state == State.START_YEAR && slyCalendarData.getSelectedStartDate() != null) {
            calendar.setTime(slyCalendarData.getSelectedStartDate());
        } else if (state == State.END_YEAR && slyCalendarData.getSelectedEndDate() != null) {
            calendar.setTime(slyCalendarData.getSelectedEndDate());
        }
        final int currentRangeYear = calendar.get(Calendar.YEAR);

        YearListAdapter yearAdapter = new YearListAdapter(
                currentRangeYear,
                this,
                slyCalendarData.isFutureDatesDisabled()
        );
        yearsList.setAdapter(yearAdapter);
        yearsList.scrollToPosition(yearAdapter.getPositionToScroll());
    }

    @Override
    public void onYearSelected(int year) {
        switchToYearOrMonth(false);

        MonthPagerAdapter adapter = (MonthPagerAdapter) viewPager.getAdapter();
        if (adapter == null) return;

        slyCalendarData.setNewSelectedYear(year);
        updateHeader();

        int curPosition = viewPager.getCurrentItem();
        Calendar calendar = SlyCalendarUtil.INSTANCE.getCalendarWithMonthShift(
                slyCalendarData.getCurrentDate(),
                curPosition,
                adapter.getCount()
        );
        int currentYear = calendar.get(Calendar.YEAR);
        int shiftMonth = (year - currentYear) * 12;
        if (shiftMonth != 0) {
            // content isn't updated when set current position for viewPager
            viewPager.setCurrentItem(curPosition + shiftMonth, false);
        } else {
            // so we update view manually
            adapter.update(curPosition);
        }
    }

    private void initCalendar() {
        paintCalendar();
        initListenersForButtons();
    }

    private void initListenersForButtons() {
        findViewById(R.id.txtCancel).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (slyCalendarData.isDateState()) {
                    if (callback != null) {
                        callback.onCancelled();
                    }
                    if (completeListener != null) {
                        completeListener.complete();
                    }
                } else {
                    switchToYearOrMonth(false);
                    slyCalendarData.cancelYearState();
                    updateHeader();
                }
            }
        });

        findViewById(R.id.txtSave).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (callback != null) {
                    Calendar start = null;
                    if (slyCalendarData.getSelectedStartDate() != null) {
                        start = Calendar.getInstance();
                        start.setTime(slyCalendarData.getSelectedStartDate());
                    }

                    Calendar end = null;
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

        nextArrow.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                ViewPager vpager = findViewById(R.id.content);
                vpager.setCurrentItem(vpager.getCurrentItem() + 1);
            }
        });
    }

    private void switchToYearOrMonth(boolean switchToYear) {
        arrows.setVisibility(switchToYear ? View.GONE : View.VISIBLE);
        viewPager.setVisibility(switchToYear ? View.INVISIBLE : View.VISIBLE);
        yearsList.setVisibility(switchToYear ? View.VISIBLE : View.GONE);
        if (!switchToYear) {
            yearsList.setAdapter(null);
        }
    }

    private void paintCalendar() {
        // todo move to header view
        /*findViewById(R.id.mainFrame).setBackgroundColor(slyCalendarData.getBackgroundColor());
        findViewById(R.id.headerView).setBackgroundColor(slyCalendarData.getHeaderColor());
        ((TextView) findViewById(R.id.txtYear)).setTextColor(slyCalendarData.getHeaderTextColor());
        ((TextView) findViewById(R.id.txtSelectedPeriod)).setTextColor(slyCalendarData.getHeaderTextColor());*/
    }
}
