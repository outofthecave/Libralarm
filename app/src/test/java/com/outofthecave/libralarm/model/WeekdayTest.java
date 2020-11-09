package com.outofthecave.libralarm.model;

import org.junit.Test;

import java.util.EnumSet;

import static org.junit.Assert.*;

public class WeekdayTest {
    @Test
    public void testSetConverter() {
        runSetConverter(EnumSet.allOf(Weekday.class));
        runSetConverter(EnumSet.noneOf(Weekday.class));
        runSetConverter(EnumSet.of(Weekday.SUNDAY, Weekday.MONDAY, Weekday.FRIDAY));
        runSetConverter(EnumSet.of(Weekday.SATURDAY));
    }

    private void runSetConverter(EnumSet<Weekday> weekdays) {
        int i = Weekday.SetConverter.toInt(weekdays);
        EnumSet<Weekday> newWeekdays = Weekday.SetConverter.toWeekdaySet(i);
        assertEquals(weekdays, newWeekdays);
    }
}