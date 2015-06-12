package com.tt.expandablecalendar.ui.widget.calendar.core;

import java.util.Calendar;

/**
 * 封裝日曆適配器的數據 <功能簡述> </Br> <功能詳細描述> </Br>
 * 
 * @author kysonX
 */
public class CalendarItem {
    // 本月
    public static final int MONTH_CURRENT = 0;
    // 上月
    public static final int MONTH_PRE = 1;
    // 下月
    public static final int MONTH_NEXT = 2;

    public Calendar calendar;
    // 是否為今天
    public boolean isToday = false;
    // 是否為當月（前後會有空）
    public int monthPos;
    // 是否選中
//    public boolean isChecked = false;
}
