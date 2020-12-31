package com.outofthecave.libralarm.logic;

import androidx.annotation.Nullable;
import androidx.annotation.VisibleForTesting;

import com.outofthecave.libralarm.model.Alarm;
import com.outofthecave.libralarm.model.DateTime;
import com.outofthecave.libralarm.model.SnoozedAlarm;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Map;

public class AlarmListFilter {
    private static final long THRESHOLD_MILLIS = 2 * 60 * 1000;

    private AlarmListFilter() {
    }

    public static ArrayList<Alarm> getAlarmsComingUpNow(List<Alarm> allAlarms, Map<Integer, SnoozedAlarm> idToSnoozedAlarm, DateTime lastTriggered) {
        DateTime now = DateTime.now();
        return getAlarmsComingUpAtDateTime(allAlarms, idToSnoozedAlarm, lastTriggered, now);
    }

    @VisibleForTesting
    static ArrayList<Alarm> getAlarmsComingUpAtDateTime(List<Alarm> allAlarms, Map<Integer, SnoozedAlarm> idToSnoozedAlarm, DateTime lastTriggered, DateTime referenceDateTime) {
        ArrayList<Alarm> upcomingAlarms = new ArrayList<>(1);
        DateTime upcomingAlarmsNotifDateTime = null;
        for (Alarm alarm : allAlarms) {
            if (alarm.enabled) {
                SnoozedAlarm snoozedAlarm = idToSnoozedAlarm.get(alarm.id);
                DateTime alarmNotifDateTime = getNextNotificationDateTime(alarm, snoozedAlarm);
                if (referenceDateTime.compareTo(alarmNotifDateTime) <= 0
                        && lastTriggered.compareTo(alarmNotifDateTime) < 0) {
                    if (upcomingAlarms.isEmpty()) {
                        upcomingAlarms.add(alarm);
                        upcomingAlarmsNotifDateTime = alarmNotifDateTime;
                    } else {
                        int cmp = alarmNotifDateTime.compareTo(upcomingAlarmsNotifDateTime);
                        if (cmp < 0) {
                            upcomingAlarms = new ArrayList<>(1);
                            upcomingAlarms.add(alarm);
                            upcomingAlarmsNotifDateTime = alarmNotifDateTime;
                        } else if (cmp == 0) {
                            upcomingAlarms.add(alarm);
                        }
                    }
                }
            }
        }

        return upcomingAlarms;
    }

    @VisibleForTesting
    static DateTime getNextNotificationDateTime(Alarm alarm, @Nullable SnoozedAlarm snoozedAlarm) {
        if (snoozedAlarm != null
                && snoozedAlarm.snoozeCount > 0
                && (alarm.snooze.maximum == -1
                || snoozedAlarm.snoozeCount <= alarm.snooze.maximum)) {

            int minutes = 0;
            for (int i = 0; i < snoozedAlarm.snoozeCount; ++i) {
                int interval = (int) (alarm.snooze.intervalMinutes * Math.pow(alarm.snooze.intervalChangeFactor, i));
                if (interval < 1) {
                    interval = 1;
                }
                minutes += interval;
            }

            Calendar calendar = alarm.dateTime.toCalendar();
            calendar.add(Calendar.MINUTE, minutes);
            return DateTime.fromCalendar(calendar);
        }

        return alarm.dateTime;
    }

    public static ArrayList<Alarm> getAlarmsToNotifyAboutNow(List<Alarm> allAlarms) {
        DateTime now = DateTime.now();
        return getAlarmsToNotifyAboutAtDateTime(allAlarms, now);
    }

    @VisibleForTesting
    static ArrayList<Alarm> getAlarmsToNotifyAboutAtDateTime(List<Alarm> allAlarms, DateTime referenceDateTime) {
        ArrayList<Alarm> alarms = new ArrayList<>();
        long referenceEpochMillis = referenceDateTime.toEpochMillis();
        long minDiffMillis = Long.MAX_VALUE;
        for (Alarm alarm : allAlarms) {
            if (alarm.enabled && alarm.dateTime.compareTo(referenceDateTime) <= 0) {
                long diffMillis = referenceEpochMillis - alarm.dateTime.toEpochMillis();
                if (diffMillis < THRESHOLD_MILLIS) {
                    if (diffMillis < minDiffMillis) {
                        alarms = new ArrayList<>();
                        alarms.add(alarm);
                        minDiffMillis = diffMillis;
                    } else if (diffMillis == minDiffMillis) {
                        alarms.add(alarm);
                    }
                }
            }
        }

        return alarms;
    }
}
