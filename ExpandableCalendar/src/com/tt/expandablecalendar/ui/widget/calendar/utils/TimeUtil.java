package com.tt.expandablecalendar.ui.widget.calendar.utils;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class TimeUtil {

    /**
     * 取得系統時間 <功能簡述>
     * 
     * @return
     */
    public static long getSystemTime() {
        return System.currentTimeMillis();
    }

    /**
     * 獲取時間的每個域 格式：年月日時分秒 <功能簡述>
     * 
     * @param time
     * @return
     */
    public static int[] getTimeFields(long time) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(time);
        int[] timeFields = new int[6];
        timeFields[0] = calendar.get(Calendar.YEAR);
        timeFields[1] = calendar.get(Calendar.MONTH);
        timeFields[2] = calendar.get(Calendar.DAY_OF_MONTH);
        timeFields[3] = calendar.get(Calendar.HOUR_OF_DAY);
        timeFields[4] = calendar.get(Calendar.MINUTE);
        timeFields[5] = calendar.get(Calendar.SECOND);
        return timeFields;
    }

    /**
     * 獲取格式化的時間 <功能簡述>
     * 
     * @param formatter
     * @param time
     * @return
     */
    public static String getFormatTime(String formatter, long time) {
        SimpleDateFormat format = new SimpleDateFormat(formatter,
                Locale.getDefault());
        return format.format(new Date(time));
    }

    /**
     * 判斷時間是否為今天 <功能簡述>
     * 
     * @param time
     * @return
     */
    public static boolean isToday(long time) {
        long now = getSystemTime();
        int[] nowFields = getTimeFields(now);
        int[] timeFields = getTimeFields(time);
        return nowFields[0] == timeFields[0] && nowFields[1] == timeFields[1]
                && nowFields[2] == timeFields[2];
    }

    /**
     * 比較兩個日期是否為同一天 <功能簡述>
     * 
     * @param fromCalendar
     * @param toCalendar
     * @return
     */
    public static boolean isSameDay(Calendar fromCalendar, Calendar toCalendar) {
        if (fromCalendar == null || toCalendar == null) {
            return false;
        }
        // 年月日都一樣，則為同一天
        return fromCalendar.get(Calendar.YEAR) == toCalendar.get(Calendar.YEAR)
                && fromCalendar.get(Calendar.MONTH) == toCalendar
                        .get(Calendar.MONTH)
                && fromCalendar.get(Calendar.DAY_OF_MONTH) == toCalendar
                        .get(Calendar.DAY_OF_MONTH);
    }

}
