package com.outofthecave.libralarm.logic;

import androidx.annotation.VisibleForTesting;

import com.outofthecave.libralarm.model.Alarm;
import com.outofthecave.libralarm.model.DateTime;

import java.util.ArrayList;
import java.util.List;

public class AlarmListFilter {
    private static final long THRESHOLD_MILLIS = 2 * 60 * 1000;

    private AlarmListFilter() {
    }

    public static ArrayList<Alarm> getAlarmsComingUpNow(List<Alarm> allAlarms, DateTime lastTriggered) {
        DateTime now = DateTime.now();
        return getAlarmsComingUpAtDateTime(allAlarms, lastTriggered, now);
    }

    @VisibleForTesting
    static ArrayList<Alarm> getAlarmsComingUpAtDateTime(List<Alarm> allAlarms, DateTime lastTriggered, DateTime referenceDateTime) {
        ArrayList<Alarm> upcomingAlarms = new ArrayList<>(1);
        for (Alarm alarm : allAlarms) {
            if (alarm.enabled
                    && referenceDateTime.compareTo(alarm.dateTime) <= 0
                    && lastTriggered.compareTo(alarm.dateTime) < 0) {
                if (upcomingAlarms.isEmpty()) {
                    upcomingAlarms.add(alarm);
                } else {
                    int cmp = alarm.dateTime.compareTo(upcomingAlarms.get(0).dateTime);
                    if (cmp < 0) {
                        upcomingAlarms = new ArrayList<>(1);
                        upcomingAlarms.add(alarm);
                    } else if (cmp == 0) {
                        upcomingAlarms.add(alarm);
                    }
                }
            }
        }

        return upcomingAlarms;
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
