package com.outofthecave.libralarm.logic;

import com.outofthecave.libralarm.model.Alarm;
import com.outofthecave.libralarm.model.DateTime;
import com.outofthecave.libralarm.model.SnoozedAlarm;

import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertSame;

public class AlarmListFilterTest {
    @Test
    public void testGetNextNotificationDateTimeMultiple() {
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

        Map<Integer, SnoozedAlarm> idToSnoozedAlarm = new HashMap<>();

        DateTime lastTriggered = new DateTime();
        lastTriggered.hour = 7;
        lastTriggered.minute = 30;

        DateTime referenceDateTime = new DateTime();
        referenceDateTime.hour = 7;
        referenceDateTime.minute = 45;

        DateTime expectedNotificationDateTime = new DateTime();
        expectedNotificationDateTime.hour = 8;
        expectedNotificationDateTime.minute = 0;

        DateTime notificationDateTime = AlarmListFilter.getNextNotificationDateTimeAfterDateTime(allAlarms, idToSnoozedAlarm, lastTriggered, referenceDateTime);
        assertEquals(expectedNotificationDateTime, notificationDateTime);
    }

    @Test
    public void testGetNextNotificationDateTimeLastTriggeredThisMinute() {
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

        Map<Integer, SnoozedAlarm> idToSnoozedAlarm = new HashMap<>();

        DateTime lastTriggered = new DateTime();
        lastTriggered.hour = 8;
        lastTriggered.minute = 0;

        DateTime referenceDateTime = new DateTime();
        referenceDateTime.hour = 8;
        referenceDateTime.minute = 0;

        DateTime expectedNotificationDateTime = new DateTime();
        expectedNotificationDateTime.hour = 8;
        expectedNotificationDateTime.minute = 30;

        DateTime notificationDateTime = AlarmListFilter.getNextNotificationDateTimeAfterDateTime(allAlarms, idToSnoozedAlarm, lastTriggered, referenceDateTime);
        assertEquals(expectedNotificationDateTime, notificationDateTime);
    }

    @Test
    public void testGetNextNotificationDateTimeIgnoreDisabledAlarms() {
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

        Map<Integer, SnoozedAlarm> idToSnoozedAlarm = new HashMap<>();

        DateTime lastTriggered = new DateTime();
        lastTriggered.hour = 7;
        lastTriggered.minute = 30;

        DateTime referenceDateTime = new DateTime();
        referenceDateTime.hour = 8;
        referenceDateTime.minute = 0;

        DateTime expectedNotificationDateTime = new DateTime();
        expectedNotificationDateTime.hour = 8;
        expectedNotificationDateTime.minute = 0;

        DateTime notificationDateTime = AlarmListFilter.getNextNotificationDateTimeAfterDateTime(allAlarms, idToSnoozedAlarm, lastTriggered, referenceDateTime);
        assertEquals(expectedNotificationDateTime, notificationDateTime);
    }

    @Test
    public void testGetNextNotificationDateTimeSnoozedAlarm() {
        Alarm alarm800Foo = new Alarm();
        alarm800Foo.id = 1;
        alarm800Foo.name = "Foo";
        alarm800Foo.dateTime.hour = 8;
        alarm800Foo.dateTime.minute = 0;
        alarm800Foo.snooze.maximum = -1;
        alarm800Foo.snooze.intervalMinutes = 5;

        Alarm alarm830Foo = new Alarm();
        alarm830Foo.id = 2;
        alarm830Foo.name = "Foo";
        alarm830Foo.dateTime.hour = 8;
        alarm830Foo.dateTime.minute = 30;

        List<Alarm> allAlarms = Arrays.asList(alarm800Foo, alarm830Foo);

        SnoozedAlarm snoozedAlarm800Foo = new SnoozedAlarm(alarm800Foo.id);
        snoozedAlarm800Foo.snoozeCount = 1;

        Map<Integer, SnoozedAlarm> idToSnoozedAlarm = new HashMap<>();
        idToSnoozedAlarm.put(snoozedAlarm800Foo.id, snoozedAlarm800Foo);

        DateTime lastTriggered = new DateTime();
        lastTriggered.hour = 8;
        lastTriggered.minute = 0;

        DateTime referenceDateTime = new DateTime();
        referenceDateTime.hour = 8;
        referenceDateTime.minute = 4;

        DateTime expectedNotificationDateTime = new DateTime();
        expectedNotificationDateTime.hour = 8;
        expectedNotificationDateTime.minute = 5;

        DateTime notificationDateTime = AlarmListFilter.getNextNotificationDateTimeAfterDateTime(allAlarms, idToSnoozedAlarm, lastTriggered, referenceDateTime);
        assertEquals(expectedNotificationDateTime, notificationDateTime);
    }

    @Test
    public void testGetNextNotificationDateTimeUnlimitedSnoozeWithChangeFactor() {
        Alarm alarm = new Alarm();
        alarm.id = 1;
        alarm.dateTime.hour = 8;
        alarm.dateTime.minute = 0;
        alarm.snooze.maximum = -1;
        alarm.snooze.intervalMinutes = 10;
        alarm.snooze.intervalChangeFactor = 0.5;

        SnoozedAlarm snoozedAlarm = new SnoozedAlarm(alarm.id);

        DateTime expectedDateTime = new DateTime();
        expectedDateTime.hour = 8;
        expectedDateTime.minute = 0;

        DateTime dateTime = AlarmListFilter.getNextNotificationDateTime(alarm, snoozedAlarm);
        assertEquals(expectedDateTime, dateTime);

        snoozedAlarm.snoozeCount = 1;
        expectedDateTime.minute = 10;
        dateTime = AlarmListFilter.getNextNotificationDateTime(alarm, snoozedAlarm);
        assertEquals(expectedDateTime, dateTime);

        snoozedAlarm.snoozeCount = 2;
        expectedDateTime.minute = 15;
        dateTime = AlarmListFilter.getNextNotificationDateTime(alarm, snoozedAlarm);
        assertEquals(expectedDateTime, dateTime);

        snoozedAlarm.snoozeCount = 3;
        expectedDateTime.minute = 17;
        dateTime = AlarmListFilter.getNextNotificationDateTime(alarm, snoozedAlarm);
        assertEquals(expectedDateTime, dateTime);

        snoozedAlarm.snoozeCount = 4;
        expectedDateTime.minute = 18;
        dateTime = AlarmListFilter.getNextNotificationDateTime(alarm, snoozedAlarm);
        assertEquals(expectedDateTime, dateTime);

        snoozedAlarm.snoozeCount = 5;
        expectedDateTime.minute = 19;
        dateTime = AlarmListFilter.getNextNotificationDateTime(alarm, snoozedAlarm);
        assertEquals(expectedDateTime, dateTime);
    }

    @Test
    public void testGetNextNotificationDateTimeNullSnoozedAlarm() {
        Alarm alarm = new Alarm();
        alarm.id = 1;
        alarm.dateTime.hour = 8;
        alarm.dateTime.minute = 0;
        alarm.snooze.maximum = -1;
        alarm.snooze.intervalMinutes = 10;
        alarm.snooze.intervalChangeFactor = 0.5;

        SnoozedAlarm snoozedAlarm = null;

        DateTime expectedDateTime = new DateTime();
        expectedDateTime.hour = 8;
        expectedDateTime.minute = 0;

        DateTime dateTime = AlarmListFilter.getNextNotificationDateTime(alarm, snoozedAlarm);
        assertEquals(expectedDateTime, dateTime);
    }

    @Test
    public void testGetNextNotificationDateTimeLimitedSnooze() {
        Alarm alarm = new Alarm();
        alarm.id = 1;
        alarm.dateTime.hour = 8;
        alarm.dateTime.minute = 0;
        alarm.snooze.maximum = 2;
        alarm.snooze.intervalMinutes = 5;

        SnoozedAlarm snoozedAlarm = new SnoozedAlarm(alarm.id);

        DateTime expectedDateTime = new DateTime();
        expectedDateTime.hour = 8;
        expectedDateTime.minute = 0;

        DateTime dateTime = AlarmListFilter.getNextNotificationDateTime(alarm, snoozedAlarm);
        assertEquals(expectedDateTime, dateTime);

        snoozedAlarm.snoozeCount = 1;
        expectedDateTime.minute = 5;
        dateTime = AlarmListFilter.getNextNotificationDateTime(alarm, snoozedAlarm);
        assertEquals(expectedDateTime, dateTime);

        snoozedAlarm.snoozeCount = 2;
        expectedDateTime.minute = 10;
        dateTime = AlarmListFilter.getNextNotificationDateTime(alarm, snoozedAlarm);
        assertEquals(expectedDateTime, dateTime);

        // This shouldn't happen because the snooze button wouldn't even be shown any more.
        snoozedAlarm.snoozeCount = 3;
        expectedDateTime.minute = 0;
        dateTime = AlarmListFilter.getNextNotificationDateTime(alarm, snoozedAlarm);
        assertEquals(expectedDateTime, dateTime);
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

        Map<Integer, SnoozedAlarm> idToSnoozedAlarm = new HashMap<>();

        DateTime referenceDateTime = new DateTime();
        referenceDateTime.hour = 8;
        referenceDateTime.minute = 0;

        List<Alarm> expectedAlarms = Arrays.asList(alarm800Foo, alarm800Bar);

        List<Alarm> alarms = AlarmListFilter.getAlarmsToNotifyAboutAtDateTime(allAlarms, idToSnoozedAlarm, referenceDateTime);
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

        Map<Integer, SnoozedAlarm> idToSnoozedAlarm = new HashMap<>();

        DateTime referenceDateTime = new DateTime();
        referenceDateTime.hour = 8;
        referenceDateTime.minute = 1;

        List<Alarm> expectedAlarms = Arrays.asList(alarm800);

        List<Alarm> alarms = AlarmListFilter.getAlarmsToNotifyAboutAtDateTime(allAlarms, idToSnoozedAlarm, referenceDateTime);
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

        Map<Integer, SnoozedAlarm> idToSnoozedAlarm = new HashMap<>();

        DateTime referenceDateTime = new DateTime();
        referenceDateTime.hour = 8;
        referenceDateTime.minute = 0;

        List<Alarm> expectedAlarms = Arrays.asList(alarm800Bar);

        List<Alarm> alarms = AlarmListFilter.getAlarmsToNotifyAboutAtDateTime(allAlarms, idToSnoozedAlarm, referenceDateTime);
        assertEquals(expectedAlarms, alarms);
    }

    @Test
    public void testGetAlarmsToNotifyAboutSnoozedAlarm() {
        Alarm alarm800Foo = new Alarm();
        alarm800Foo.id = 1;
        alarm800Foo.name = "Foo";
        alarm800Foo.dateTime.hour = 8;
        alarm800Foo.dateTime.minute = 0;
        alarm800Foo.snooze.maximum = -1;
        alarm800Foo.snooze.intervalMinutes = 5;

        Alarm alarm830Foo = new Alarm();
        alarm830Foo.id = 2;
        alarm830Foo.name = "Foo";
        alarm830Foo.dateTime.hour = 8;
        alarm830Foo.dateTime.minute = 30;

        List<Alarm> allAlarms = Arrays.asList(alarm800Foo, alarm830Foo);

        SnoozedAlarm snoozedAlarm800Foo = new SnoozedAlarm(alarm800Foo.id);
        snoozedAlarm800Foo.snoozeCount = 1;

        Map<Integer, SnoozedAlarm> idToSnoozedAlarm = new HashMap<>();
        idToSnoozedAlarm.put(snoozedAlarm800Foo.id, snoozedAlarm800Foo);

        DateTime referenceDateTime = new DateTime();
        referenceDateTime.hour = 8;
        referenceDateTime.minute = 5;

        List<Alarm> expectedAlarms = Arrays.asList(alarm800Foo);

        List<Alarm> alarms = AlarmListFilter.getAlarmsToNotifyAboutAtDateTime(allAlarms, idToSnoozedAlarm, referenceDateTime);
        assertEquals(expectedAlarms, alarms);
    }

    @Test
    public void testIsSnoozingPossibleForAnyUnlimitedAndZero() {
        Alarm alarm800Foo = new Alarm();
        alarm800Foo.id = 1;
        alarm800Foo.snooze.maximum = -1;

        Alarm alarm830Foo = new Alarm();
        alarm830Foo.id = 2;

        List<Alarm> alarms = Arrays.asList(alarm800Foo, alarm830Foo);

        SnoozedAlarm snoozedAlarm800Foo = new SnoozedAlarm(alarm800Foo.id);
        snoozedAlarm800Foo.snoozeCount = 1;

        Map<Integer, SnoozedAlarm> idToSnoozedAlarm = new HashMap<>();
        idToSnoozedAlarm.put(snoozedAlarm800Foo.id, snoozedAlarm800Foo);

        assertSame(true, AlarmListFilter.isSnoozingPossible(alarm800Foo, snoozedAlarm800Foo));
        assertSame(false, AlarmListFilter.isSnoozingPossible(alarm830Foo, null));
        assertSame(true, AlarmListFilter.isSnoozingPossibleForAny(alarms, idToSnoozedAlarm));
    }

    @Test
    public void testIsSnoozingPossibleForAnyMaxAndMaxMinusOne() {
        Alarm alarm800Foo = new Alarm();
        alarm800Foo.id = 1;
        alarm800Foo.snooze.maximum = 3;

        Alarm alarm830Foo = new Alarm();
        alarm830Foo.id = 2;
        alarm830Foo.snooze.maximum = 3;

        List<Alarm> alarms = Arrays.asList(alarm800Foo, alarm830Foo);

        SnoozedAlarm snoozedAlarm800Foo = new SnoozedAlarm(alarm800Foo.id);
        snoozedAlarm800Foo.snoozeCount = 3;

        SnoozedAlarm snoozedAlarm830Foo = new SnoozedAlarm(alarm830Foo.id);
        snoozedAlarm830Foo.snoozeCount = 2;

        Map<Integer, SnoozedAlarm> idToSnoozedAlarm = new HashMap<>();
        idToSnoozedAlarm.put(snoozedAlarm800Foo.id, snoozedAlarm800Foo);
        idToSnoozedAlarm.put(snoozedAlarm830Foo.id, snoozedAlarm830Foo);

        assertSame(false, AlarmListFilter.isSnoozingPossible(alarm800Foo, snoozedAlarm800Foo));
        assertSame(true, AlarmListFilter.isSnoozingPossible(alarm830Foo, snoozedAlarm830Foo));
        assertSame(true, AlarmListFilter.isSnoozingPossibleForAny(alarms, idToSnoozedAlarm));
        assertSame(false, AlarmListFilter.isSnoozingPossibleForAny(Arrays.asList(alarm800Foo), idToSnoozedAlarm));
    }

    private static ArrayList<Alarm> toAlarmArrayList(Alarm... alarms) {
        return new ArrayList<>(Arrays.asList(alarms));
    }
}