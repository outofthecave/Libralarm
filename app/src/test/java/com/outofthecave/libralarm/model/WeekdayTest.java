package com.outofthecave.libralarm.model;

import org.junit.Test;

import java.util.EnumSet;

import static org.junit.Assert.*;

public class WeekdayTest {
    @Test
    public void weekdaySetToInt_intToWeekdaySet() {
        run_weekdaySetToInt_intToWeekdaySet(EnumSet.noneOf(Weekday.class));
        run_weekdaySetToInt_intToWeekdaySet(EnumSet.allOf(Weekday.class));
        run_weekdaySetToInt_intToWeekdaySet(EnumSet.of(Weekday.SUNDAY, Weekday.MONDAY, Weekday.FRIDAY));
        run_weekdaySetToInt_intToWeekdaySet(EnumSet.of(Weekday.SATURDAY));
    }

    private void run_weekdaySetToInt_intToWeekdaySet(EnumSet<Weekday> weekdays) {
        int i = Weekday.weekdaySetToInt(weekdays);
        EnumSet<Weekday> newWeekdays = Weekday.intToWeekdaySet(i);
        assertEquals(weekdays, newWeekdays);
    }
}