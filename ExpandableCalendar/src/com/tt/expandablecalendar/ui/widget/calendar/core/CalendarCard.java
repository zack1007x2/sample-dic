package com.tt.expandablecalendar.ui.widget.calendar.core;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.GestureDetector;
import android.view.GestureDetector.SimpleOnGestureListener;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.nineoldandroids.animation.ObjectAnimator;
import com.tt.expandablecalendar.R;
import com.tt.expandablecalendar.ui.widget.calendar.core.CalendarGridViewAdapter.OnDaySelectListener;
import com.tt.expandablecalendar.ui.widget.calendar.utils.TimeUtil;

/**
 * 單個月的日曆視圖 <功能簡述> <Br>
 * 可以定義樣式<功能詳細描述> <Br>
 * 
 * @author Kyson http://www.hikyson.cn/
 */
@SuppressLint("ClickableViewAccessibility")
public class CalendarCard extends LinearLayout {

    /**
     * 控件的日期改變 <功能簡述> <Br>
     * <功能詳細描述> <Br>
     * 
     * @author Kyson
     */
    public interface OnCalendarChangeListener {
        void onCalendarChange(Calendar cal);
    }

    private Context mContext;

    private GridWithoutScrollView mGv;

    private CalendarGridViewAdapter mGridViewAdapter;

    private Calendar mCurrentCal = Calendar.getInstance();

    private OnCalendarChangeListener mOnCalendarChangeListener;

    private OnDaySelectListener mOnDaySelectListener;

    public CalendarCard(Context context) {
        this(context, null);
    }

    public CalendarCard(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.mContext = context;
        init(attrs);
    }

    // 可以定義的屬性

    // 星期文字樣式
    private int mWeekTextStyle;
    // 今日文字樣式
    private int mTodayTextStyle;

    private int mNotCurrentTextStyle;
    // 普通日期文字樣式
    private int mDayTextStyle;
    // 選中文字背景
    private int mDaySelector;

    /**
     * 初始化日期以及view等控件
     */
    private void init(AttributeSet attrs) {
        // 獲取所有的資源檔案
        if (attrs != null) {

            TypedArray a = mContext.obtainStyledAttributes(attrs,
                    R.styleable.TlCalendar);
            mWeekTextStyle = a.getResourceId(
                    R.styleable.TlCalendar_weekTextStyle,
                    R.style.textView_sp13_grey_bg_bold);
            mTodayTextStyle = a.getResourceId(
                    R.styleable.TlCalendar_todayTextStyle,
                    R.style.textView_sp12_green);
            mNotCurrentTextStyle = a.getResourceId(
                    R.styleable.TlCalendar_notCurrentTextStyle,
                    R.style.textView_12_grey_light);
            mDayTextStyle = a.getResourceId(
                    R.styleable.TlCalendar_dayTextStyle,
                    R.style.textView_sp12_white);
            mDaySelector = a.getResourceId(R.styleable.TlCalendar_daySelector,
                    R.drawable.tl_widget_calendar_item_common_selector);

            a.recycle();
        } else {
            mWeekTextStyle = R.style.textView_sp13_grey_bg_bold;
            mTodayTextStyle = R.style.textView_sp12_green;
            mNotCurrentTextStyle = R.style.textView_12_grey_light;
            mDayTextStyle = R.style.textView_sp12_white;
            mDaySelector = R.drawable.tl_widget_calendar_item_common_selector;
        }

        View view = LayoutInflater.from(mContext).inflate(
                R.layout.widget_calendar_card, this, true);

        LinearLayout weekdaysLl = (LinearLayout) view
                .findViewById(R.id.widget_calendar_card_weekdays);

        // 設置星期文字樣式
        for (int i = 0; i < weekdaysLl.getChildCount(); i++) {
            View child = weekdaysLl.getChildAt(i);
            if (child instanceof TextView) {
                ((TextView) child).setTextAppearance(mContext, mWeekTextStyle);
            }
        }

        mGv = (GridWithoutScrollView) view
                .findViewById(R.id.widget_calendar_card_gv);
        mGridViewAdapter = new CalendarGridViewAdapter(mContext);
        mGv.setAdapter(mGridViewAdapter);

        mGridViewAdapter.initStyle(mTodayTextStyle, mNotCurrentTextStyle,
                mDayTextStyle, mDaySelector);

        mGridViewAdapter.setOnDaySelectListener(new OnDaySelectListener() {

            @Override
            public void onDaySelectListener(Calendar date) {
                mCurrentCal = (Calendar) date.clone();
                if (mOnDaySelectListener != null) {
                    mOnDaySelectListener.onDaySelectListener(date);
                }
            }
        });
    }

    /**
     * 設置控件的選中日期 <功能簡述>
     * 
     * @param calendar
     */
    public void selectCurrentCalendar(Calendar calendar) {
        mCurrentCal = (Calendar) calendar.clone();
        // printCal();
        mGridViewAdapter.setSelectedDate(calendar);
        notifyDataChanged();
    }

    /**
     * jump to pre month <功能簡述>
     */
    public void toBeforeMonth() {
        mCurrentCal.add(Calendar.MONTH, -1);
        notifyDataChanged();
    }

    /**
     * jump to next month <功能簡述>
     */
    public void toAfterMonth() {
        mCurrentCal.add(Calendar.MONTH, 1);
        notifyDataChanged();
    }

    public void selectBeforeDay() {
        mCurrentCal.add(Calendar.DAY_OF_MONTH, -1);
        mGridViewAdapter.setSelectedDate(mCurrentCal);
        notifyDataChanged();
    }

    public void selectAfterDay() {
        mCurrentCal.add(Calendar.DAY_OF_MONTH, 1);
        mGridViewAdapter.setSelectedDate(mCurrentCal);
        notifyDataChanged();
    }

    private GestureDetector mDetector = new GestureDetector(mContext,
            new SimpleOnGestureListener() {

                @Override
                public boolean onDown(MotionEvent e) {
                    return true;
                }

                public boolean onFling(MotionEvent e1, MotionEvent e2,
                        float velocityX, float velocityY) {
                    // L.i("onFling");
                    if (Math.abs(velocityX) > Math.abs(velocityY) * 0.8) { // 降噪處理，Y方向的動作要更大一點才會認為是垂直滾動
                        if (velocityX > 0) {
                            // 向右邊
                            toBeforeMonth();
                        } else {
                            // 向左邊
                            toAfterMonth();
                        }
                        return true;
                    } else {
                        return false;
                    }
                };
            });

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        return mDetector.onTouchEvent(event);
    }

    // 初始點擊點
    private float mDx;
    // 用於判斷是移動還是點擊
    private static final int MIN_MOVE_X = 20;

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        switch (ev.getAction()) {
        case MotionEvent.ACTION_DOWN:
            mDx = ev.getX();
            break;
        case MotionEvent.ACTION_MOVE:
            float my = ev.getX();
            if (Math.abs(my - mDx) > MIN_MOVE_X) {// 如果X方向滑動的距離大於指定距離則攔截手勢動作，執行本View的OnTouch事件
                return true;
            }
            break;
        default:
            break;
        }
        return super.onInterceptTouchEvent(ev);
    }

    /**
     * 給gridview設置,設置數據 <功能簡述>
     * 
     * @param cal
     */
    private List<CalendarItem> getGvDataByYearAndMonth() {
        // 前面的空格數
        int firstDaySpaceCount = getFirstDayOfSpaceCount(mCurrentCal);
        // 後面的空格數
        int lastDaySpaceCount = getLastDayOfSpaceCount(mCurrentCal);
        // 獲取當前月有多少天
        int dayCount = getDayNumInMonth(mCurrentCal);

        return getGvListData(firstDaySpaceCount, lastDaySpaceCount, dayCount);
    }

    /**
     * 控件數據變化，通知改變樣式 <功能簡述>
     */
    private void notifyDataChanged() {
        performAnim();
        mGridViewAdapter.setDatas(getGvDataByYearAndMonth());
        mGridViewAdapter.notifyDataSetChanged();
        if (mOnCalendarChangeListener != null) {
            mOnCalendarChangeListener.onCalendarChange(mCurrentCal);
        }
    }

    /**
     * perform this view's anim <功能簡述>
     */
    private void performAnim() {
        ObjectAnimator.ofFloat(mGv, "alpha", 0f, 1f).setDuration(150).start();
    }

    /**
     * 為gridview中添加需要展示的數據
     * 
     * @param tempSum
     * @param dayNumInMonth
     */
    private List<CalendarItem> getGvListData(int first, int last, int dayCount) {
        List<CalendarItem> list = new ArrayList<CalendarItem>();
        // 當前選中的月份對應的calendar
        Calendar currentCalendar = (Calendar) mCurrentCal.clone();
        currentCalendar.set(Calendar.DAY_OF_MONTH, 1);

        // 前面的空格，填充
        for (int i = 0; i < first; i++) {
            CalendarItem calendarItem = new CalendarItem();
            Calendar calendar = (Calendar) currentCalendar.clone();
            calendar.add(Calendar.MONTH, -1);
            calendar.set(Calendar.DAY_OF_MONTH, getDayNumInMonth(calendar)
                    - first + i + 1);
            calendarItem.calendar = calendar;
            calendarItem.isToday = isToday(calendar);
            calendarItem.monthPos = CalendarItem.MONTH_PRE;
            list.add(calendarItem);
        }
        // 當前月的日期
        for (int j = 0; j < dayCount; j++) {
            CalendarItem calendarItem = new CalendarItem();
            Calendar calendar = (Calendar) currentCalendar.clone();
            calendar.set(Calendar.DAY_OF_MONTH, j + 1);
            calendarItem.calendar = calendar;
            calendarItem.isToday = isToday(calendar);
            calendarItem.monthPos = CalendarItem.MONTH_CURRENT;
            list.add(calendarItem);
        }

        // 後面的空格填充
        for (int k = 0; k < last; k++) {
            CalendarItem calendarItem = new CalendarItem();
            Calendar calendar = (Calendar) currentCalendar.clone();
            calendar.add(Calendar.MONTH, 1);
            calendar.set(Calendar.DAY_OF_MONTH, k + 1);
            calendarItem.calendar = calendar;
            calendarItem.isToday = isToday(calendar);
            calendarItem.monthPos = CalendarItem.MONTH_NEXT;
            list.add(calendarItem);
        }
        return list;
    }

    public void setOnCalendarChangeListener(
            OnCalendarChangeListener onCalendarChangeListener) {
        this.mOnCalendarChangeListener = onCalendarChangeListener;
    }

    public Calendar getSelectedDate() {
        return mGridViewAdapter.getSelecterDate();
    }

    /**
     * 獲取月份第一天前面的空格 <功能簡述>
     * 
     * @param cal
     * @return
     */
    private static int getFirstDayOfSpaceCount(Calendar cal) {
        Calendar calTemp = (Calendar) cal.clone();
        calTemp.set(Calendar.DAY_OF_MONTH, 1);
        int firstDayInWeek = calTemp.get(Calendar.DAY_OF_WEEK);
        // 換算為空格數
        return weekToSpaceCount(firstDayInWeek);
    }

    /**
     * 獲取月份最後一天後面的空格數 <功能簡述>
     * 
     * @param cal
     * @return
     */
    private static int getLastDayOfSpaceCount(Calendar cal) {
        Calendar calTemp = (Calendar) cal.clone();
        calTemp.set(Calendar.DAY_OF_MONTH, getDayNumInMonth(cal));
        int lastDayInWeek = calTemp.get(Calendar.DAY_OF_WEEK);
        return 6 - weekToSpaceCount(lastDayInWeek);
    }

    /**
     * 將周幾換算為空格數 <功能簡述>
     * 
     * @param week
     * @return
     */
    private static int weekToSpaceCount(int week) {
        int space = (7 + (week - 2)) % 7;
        return space;
    }

    /**
     * 獲取當前月的總共天數
     * 
     * @param cal
     * @return
     */
    private static int getDayNumInMonth(Calendar cal) {
        return cal.getActualMaximum(Calendar.DATE);
    }

    /**
     * 是否為今天 <功能簡述>
     * 
     * @return
     */
    private static boolean isToday(Calendar cal) {
        return TimeUtil.isToday(cal.getTimeInMillis());
    }

    public void setOnDaySelectListener(OnDaySelectListener onDaySelectListener) {
        this.mOnDaySelectListener = onDaySelectListener;
    }
}
