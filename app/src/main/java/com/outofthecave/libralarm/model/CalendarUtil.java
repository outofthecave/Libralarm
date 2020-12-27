package com.outofthecave.libralarm.model;

import java.util.Calendar;

public class CalendarUtil {
    private CalendarUtil() {
    }

    public static int getOneBasedMonth(int calendarMonth) {
        return 1 + calendarMonth - Calendar.JANUARY;
    }

    public static int getOneBasedMonth(Calendar calendar) {
        return getOneBasedMonth(calendar.get(Calendar.MONTH));
    }

    public static int getMonthForCalendar(DateTime dateTime) {
        return dateTime.month - 1 + Calendar.JANUARY;
    }
}
