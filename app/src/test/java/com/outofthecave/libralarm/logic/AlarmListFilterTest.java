package com.outofthecave.libralarm.logic;

import com.outofthecave.libralarm.model.Alarm;
import com.outofthecave.libralarm.model.DateTime;
import com.outofthecave.libralarm.model.Weekday;

import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.EnumSet;
import java.util.List;

import static org.junit.Assert.assertEquals;

public class AlarmListFilterTest {
    @Test
    public void testGetAlarmsComingUpMultiple() {
        Alarm alarm800Foo = new Alarm();
        alarm800Foo.id = 1;
        alarm800Foo.name = "Foo";
        alarm800Foo.dateTime.hour = 8;
        alarm800Foo.dateTime.minute = 0;

        Alarm alarm830Foo = new Alarm();
        alarm830Foo.id = 2;
        alarm830Foo.name = "Foo";
        alarm830Foo.dateTime.hour = 8;
        alarm830Foo.dateTime.minute = 30;

        Alarm alarm800Bar = new Alarm();
        alarm800Bar.id = 3;
        alarm800Bar.name = "Bar";
        alarm800Bar.dateTime.hour = 8;
        alarm800Bar.dateTime.minute = 0;

        List<Alarm> allAlarms = Arrays.asList(alarm800Foo, alarm830Foo, alarm800Bar);

        DateTime lastTriggered = new DateTime();
        lastTriggered.hour = 7;
        lastTriggered.minute = 30;

        DateTime referenceDateTime = new DateTime();
        referenceDateTime.hour = 7;
        referenceDateTime.minute = 45;

        List<Alarm> expectedUpcomingAlarms = Arrays.asList(alarm800Foo, alarm800Bar);

        List<Alarm> upcomingAlarms = AlarmListFilter.getAlarmsComingUpAtDateTime(allAlarms, lastTriggered, referenceDateTime);
        assertEquals(expectedUpcomingAlarms, upcomingAlarms);
    }

    @Test
    public void testGetAlarmsComingUpLastTriggeredThisMinute() {
        Alarm alarm800Foo = new Alarm();
        alarm800Foo.id = 1;
        alarm800Foo.name = "Foo";
        alarm800Foo.dateTime.hour = 8;
        alarm800Foo.dateTime.minute = 0;

        Alarm alarm830Foo = new Alarm();
        alarm830Foo.id = 2;
        alarm830Foo.name = "Foo";
        alarm830Foo.dateTime.hour = 8;
        alarm830Foo.dateTime.minute = 30;

        Alarm alarm800Bar = new Alarm();
        alarm800Bar.id = 3;
        alarm800Bar.name = "Bar";
        alarm800Bar.dateTime.hour = 8;
        alarm800Bar.dateTime.minute = 0;

        List<Alarm> allAlarms = Arrays.asList(alarm800Foo, alarm830Foo, alarm800Bar);

        DateTime lastTriggered = new DateTime();
        lastTriggered.hour = 8;
        lastTriggered.minute = 0;

        DateTime referenceDateTime = new DateTime();
        referenceDateTime.hour = 8;
        referenceDateTime.minute = 0;

        List<Alarm> expectedUpcomingAlarms = Arrays.asList(alarm830Foo);

        List<Alarm> upcomingAlarms = AlarmListFilter.getAlarmsComingUpAtDateTime(allAlarms, lastTriggered, referenceDateTime);
        assertEquals(expectedUpcomingAlarms, upcomingAlarms);
    }

    @Test
    public void testGetAlarmsComingUpIgnoreDisabledAlarms() {
        Alarm alarm800Foo = new Alarm();
        alarm800Foo.id = 1;
        alarm800Foo.name = "Foo";
        alarm800Foo.enabled = false;
        alarm800Foo.dateTime.hour = 8;
        alarm800Foo.dateTime.minute = 0;

        Alarm alarm830Foo = new Alarm();
        alarm830Foo.id = 2;
        alarm830Foo.name = "Foo";
        alarm830Foo.dateTime.hour = 8;
        alarm830Foo.dateTime.minute = 30;

        Alarm alarm800Bar = new Alarm();
        alarm800Bar.id = 3;
        alarm800Bar.name = "Bar";
        alarm800Bar.dateTime.hour = 8;
        alarm800Bar.dateTime.minute = 0;

        List<Alarm> allAlarms = Arrays.asList(alarm800Foo, alarm830Foo, alarm800Bar);

        DateTime lastTriggered = new DateTime();
        lastTriggered.hour = 7;
        lastTriggered.minute = 30;

        DateTime referenceDateTime = new DateTime();
        referenceDateTime.hour = 8;
        referenceDateTime.minute = 0;

        List<Alarm> expectedUpcomingAlarms = Arrays.asList(alarm800Bar);

        List<Alarm> upcomingAlarms = AlarmListFilter.getAlarmsComingUpAtDateTime(allAlarms, lastTriggered, referenceDateTime);
        assertEquals(expectedUpcomingAlarms, upcomingAlarms);
    }

    @Test
    public void testGetAlarmsToNotifyAboutMultiple() {
        Alarm alarm800Foo = new Alarm();
        alarm800Foo.id = 1;
        alarm800Foo.name = "Foo";
        alarm800Foo.dateTime.hour = 8;
        alarm800Foo.dateTime.minute = 0;

        Alarm alarm830Foo = new Alarm();
        alarm830Foo.id = 2;
        alarm830Foo.name = "Foo";
        alarm830Foo.dateTime.hour = 8;
        alarm830Foo.dateTime.minute = 30;

        Alarm alarm800Bar = new Alarm();
        alarm800Bar.id = 3;
        alarm800Bar.name = "Bar";
        alarm800Bar.dateTime.hour = 8;
        alarm800Bar.dateTime.minute = 0;

        List<Alarm> allAlarms = Arrays.asList(alarm800Foo, alarm830Foo, alarm800Bar);

        DateTime referenceDateTime = new DateTime();
        referenceDateTime.hour = 8;
        referenceDateTime.minute = 0;

        List<Alarm> expectedAlarms = Arrays.asList(alarm800Foo, alarm800Bar);

        List<Alarm> alarms = AlarmListFilter.getAlarmsToNotifyAboutAtDateTime(allAlarms, referenceDateTime);
        assertEquals(expectedAlarms, alarms);
    }

    @Test
    public void testGetAlarmsToNotifyAboutThreshold() {
        Alarm alarm800 = new Alarm();
        alarm800.id = 1;
        alarm800.dateTime.hour = 8;
        alarm800.dateTime.minute = 0;

        Alarm alarm759 = new Alarm();
        alarm759.id = 2;
        alarm759.dateTime.hour = 7;
        alarm759.dateTime.minute = 59;

        Alarm alarm802 = new Alarm();
        alarm802.id = 3;
        alarm802.dateTime.hour = 8;
        alarm802.dateTime.minute = 2;

        List<Alarm> allAlarms = Arrays.asList(alarm800, alarm759, alarm802);

        DateTime referenceDateTime = new DateTime();
        referenceDateTime.hour = 8;
        referenceDateTime.minute = 1;

        List<Alarm> expectedAlarms = Arrays.asList(alarm800);

        List<Alarm> alarms = AlarmListFilter.getAlarmsToNotifyAboutAtDateTime(allAlarms, referenceDateTime);
        assertEquals(expectedAlarms, alarms);
    }

    @Test
    public void testGetAlarmsToNotifyAboutIgnoreDisabledAlarms() {
        Alarm alarm800Foo = new Alarm();
        alarm800Foo.id = 1;
        alarm800Foo.name = "Foo";
        alarm800Foo.enabled = false;
        alarm800Foo.dateTime.hour = 8;
        alarm800Foo.dateTime.minute = 0;

        Alarm alarm830Foo = new Alarm();
        alarm830Foo.id = 2;
        alarm830Foo.name = "Foo";
        alarm830Foo.dateTime.hour = 8;
        alarm830Foo.dateTime.minute = 30;

        Alarm alarm800Bar = new Alarm();
        alarm800Bar.id = 3;
        alarm800Bar.name = "Bar";
        alarm800Bar.dateTime.hour = 8;
        alarm800Bar.dateTime.minute = 0;

        List<Alarm> allAlarms = Arrays.asList(alarm800Foo, alarm830Foo, alarm800Bar);

        DateTime referenceDateTime = new DateTime();
        referenceDateTime.hour = 8;
        referenceDateTime.minute = 0;

        List<Alarm> expectedAlarms = Arrays.asList(alarm800Bar);

        List<Alarm> alarms = AlarmListFilter.getAlarmsToNotifyAboutAtDateTime(allAlarms, referenceDateTime);
        assertEquals(expectedAlarms, alarms);
    }
}