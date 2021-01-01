package com.outofthecave.libralarm.logic;

import androidx.annotation.Nullable;
import androidx.annotation.VisibleForTesting;

import com.outofthecave.libralarm.model.Alarm;
import com.outofthecave.libralarm.model.AlarmNotification;
import com.outofthecave.libralarm.model.DateTime;
import com.outofthecave.libralarm.model.SnoozedAlarm;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AlarmListFilter {
    private static final long THRESHOLD_MILLIS = 2 * 60 * 1000;

    private AlarmListFilter() {
    }

    public static Map<Integer, SnoozedAlarm> toSnoozedAlarmMap(List<SnoozedAlarm> snoozedAlarms) {
        Map<Integer, SnoozedAlarm> idToSnoozedAlarm = new HashMap<>();
        for (SnoozedAlarm snoozedAlarm : snoozedAlarms) {
            idToSnoozedAlarm.put(snoozedAlarm.id, snoozedAlarm);
        }
        return idToSnoozedAlarm;
    }

    public static AlarmNotification getAlarmsComingUpNow(List<Alarm> allAlarms, Map<Integer, SnoozedAlarm> idToSnoozedAlarm, DateTime lastTriggered) {
        DateTime now = DateTime.now();
        return getAlarmsComingUpAtDateTime(allAlarms, idToSnoozedAlarm, lastTriggered, now);
    }

    @VisibleForTesting
    static AlarmNotification getAlarmsComingUpAtDateTime(List<Alarm> allAlarms, Map<Integer, SnoozedAlarm> idToSnoozedAlarm, DateTime lastTriggered, DateTime referenceDateTime) {
        AlarmNotification notification = new AlarmNotification();
        for (Alarm alarm : allAlarms) {
            if (alarm.enabled) {
                SnoozedAlarm snoozedAlarm = idToSnoozedAlarm.get(alarm.id);
                DateTime alarmNotifDateTime = getNextNotificationDateTime(alarm, snoozedAlarm);
                if (referenceDateTime.compareTo(alarmNotifDateTime) <= 0
                        && lastTriggered.compareTo(alarmNotifDateTime) < 0) {
                    if (notification.alarms.isEmpty()) {
                        notification.alarms.add(alarm);
                        notification.dateTime = alarmNotifDateTime;
                    } else {
                        int cmp = alarmNotifDateTime.compareTo(notification.dateTime);
                        if (cmp < 0) {
                            notification.alarms = new ArrayList<>(1);
                            notification.alarms.add(alarm);
                            notification.dateTime = alarmNotifDateTime;
                        } else if (cmp == 0) {
                            notification.alarms.add(alarm);
                        }
                    }
                }
            }
        }

        return notification;
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

    public static ArrayList<Alarm> getAlarmsToNotifyAboutNow(List<Alarm> allAlarms, Map<Integer, SnoozedAlarm> idToSnoozedAlarm) {
        DateTime now = DateTime.now();
        return getAlarmsToNotifyAboutAtDateTime(allAlarms, idToSnoozedAlarm, now);
    }

    @VisibleForTesting
    static ArrayList<Alarm> getAlarmsToNotifyAboutAtDateTime(List<Alarm> allAlarms, Map<Integer, SnoozedAlarm> idToSnoozedAlarm, DateTime referenceDateTime) {
        ArrayList<Alarm> alarms = new ArrayList<>();
        long referenceEpochMillis = referenceDateTime.toEpochMillis();
        long minDiffMillis = Long.MAX_VALUE;
        for (Alarm alarm : allAlarms) {
            if (alarm.enabled) {
                SnoozedAlarm snoozedAlarm = idToSnoozedAlarm.get(alarm.id);
                DateTime alarmNotifDateTime = getNextNotificationDateTime(alarm, snoozedAlarm);
                if (alarmNotifDateTime.compareTo(referenceDateTime) <= 0) {
                    long diffMillis = referenceEpochMillis - alarmNotifDateTime.toEpochMillis();
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
        }

        return alarms;
    }

    public static boolean isSnoozingPossibleForAny(List<Alarm> alarms, Map<Integer, SnoozedAlarm> idToSnoozedAlarm) {
        for (Alarm alarm : alarms) {
            SnoozedAlarm snoozedAlarm = idToSnoozedAlarm.get(alarm.id);
            if (isSnoozingPossible(alarm, snoozedAlarm)) {
                return true;
            }
        }

        return false;
    }

    public static boolean isSnoozingPossible(Alarm alarm, @Nullable SnoozedAlarm snoozedAlarm) {
        if (alarm.enabled) {
            if (snoozedAlarm != null) {
                return alarm.snooze.maximum == -1 || snoozedAlarm.snoozeCount < alarm.snooze.maximum;
            } else {
                return alarm.snooze.maximum != 0;
            }
        }

        return false;
    }
}
