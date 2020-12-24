package com.outofthecave.libralarm.model;

import java.util.Calendar;

public class CalendarUtil {
    private CalendarUtil() {
    }

    public static int getOneBasedMonth(Calendar calendar) {
        return 1 + calendar.get(Calendar.MONTH) - Calendar.JANUARY;
    }

    public static int getMonthForCalendar(DateTime dateTime) {
        return dateTime.month - 1 + Calendar.JANUARY;
    }
}
