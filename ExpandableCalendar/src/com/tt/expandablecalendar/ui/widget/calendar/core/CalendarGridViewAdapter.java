package com.tt.expandablecalendar.ui.widget.calendar.core;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.tt.expandablecalendar.R;
import com.tt.expandablecalendar.ui.widget.calendar.utils.TimeUtil;

/**
 * 日曆的適配器 <功能簡述> <Br>
 * <功能詳細描述> <Br>
 * 
 * @author kysonX
 */
public class CalendarGridViewAdapter extends BaseAdapter {
    /**
     * 自定義監聽介面
     * 
     * @author Administrator
     * 
     */
    public interface OnDaySelectListener {
        void onDaySelectListener(Calendar date);
    }

    private OnDaySelectListener mOnDaySelectListener;

    private List<CalendarItem> mDatas = new ArrayList<CalendarItem>();
    // 當前選中的日期
    private Calendar mSelectedCal;

    private LayoutInflater mInflater;

    private Context mContext;

    public CalendarGridViewAdapter(Context context) {
        this.mContext = context;
        mInflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    private int mTodayTextStyle = R.style.textView_sp12_green;

    private int mNotCurrentTextStyle = R.style.textView_12_grey_light;

    private int mDayTextStyle = R.style.textView_sp12_white;

    private int mDaySelector = R.drawable.tl_widget_calendar_item_common_selector;

    public void initStyle(int todayTextStyle, int notCurrentTextStyle,
            int dayTextStyle, int daySelector) {

        this.mTodayTextStyle = todayTextStyle;

        this.mNotCurrentTextStyle = notCurrentTextStyle;

        this.mDayTextStyle = dayTextStyle;

        this.mDaySelector = daySelector;
    }

    @Override
    public int getCount() {
        return mDatas.size();
    }

    @Override
    public CalendarItem getItem(int position) {
        return mDatas.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        GridViewHolder holder;
        if (convertView == null) {
            holder = new GridViewHolder();
            convertView = mInflater.inflate(
                    R.layout.item_widget_common_calendar_gridview, parent,
                    false);
            holder.tvDay = (TextView) convertView
                    .findViewById(R.id.widget_common_calendar_gridview_item_date);
            convertView.setTag(holder);
        } else {
            holder = (GridViewHolder) convertView.getTag();
        }
        final CalendarItem calendarItem = getItem(position);

        TextView tvDay = holder.tvDay;

        tvDay.setText(String.valueOf(calendarItem.calendar
                .get(Calendar.DAY_OF_MONTH)));
        tvDay.setTextAppearance(mContext, getTextStyle(calendarItem));
        tvDay.setBackgroundResource(mDaySelector);
        tvDay.setSelected(TimeUtil.isSameDay(mSelectedCal,
                calendarItem.calendar));
        tvDay.setEnabled(calendarItem.monthPos == CalendarItem.MONTH_CURRENT);
        tvDay.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                checkItem(calendarItem.calendar);
            }
        });
        return convertView;
    }

    // check one item
    private void checkItem(Calendar cal) {
        // 點擊的和當前選中的是同一天
        if (TimeUtil.isSameDay(cal, mSelectedCal)) {
            return;
        }
        mSelectedCal = (Calendar) cal.clone();
        notifyDataSetChanged();
        if (mOnDaySelectListener != null) {
            mOnDaySelectListener.onDaySelectListener(mSelectedCal);
        }
    }

    /**
     * get textview's color <功能簡述>
     * 
     * @param calendarItem
     * @return
     */
    private int getTextStyle(CalendarItem calendarItem) {
        int style;
        if (calendarItem.monthPos == CalendarItem.MONTH_CURRENT) {
            // current month
            if (calendarItem.isToday) {
                style = mTodayTextStyle;
            } else {
                style = mDayTextStyle;
            }
        } else {
            // 非本月
            style = mNotCurrentTextStyle;
        }
        return style;
    }

    public static class GridViewHolder {
        public TextView tvDay;
    }

    public void setOnDaySelectListener(OnDaySelectListener onDaySelectListener) {
        this.mOnDaySelectListener = onDaySelectListener;
    }

    public List<CalendarItem> getDatas() {
        return mDatas;
    }

    public void setDatas(List<CalendarItem> datas) {
        this.mDatas = datas;
    }

    public void setSelectedDate(Calendar cal) {
        checkItem(cal);
    }

    public Calendar getSelecterDate() {
        return mSelectedCal;
    }

}
