package com.outofthecave.libralarm.logic;

import com.outofthecave.libralarm.model.Alarm;
import com.outofthecave.libralarm.model.DateTime;

import java.util.ArrayList;
import java.util.List;

public class AlarmListFilter {
    private static final long THRESHOLD_MILLIS = 2 * 60 * 1000;

    private AlarmListFilter() {
    }

    public static ArrayList<Alarm> getAlarmsToNotifyAboutNow(List<Alarm> allAlarms) {
        ArrayList<Alarm> alarms = new ArrayList<>();
        DateTime now = DateTime.now();
        long nowEpochMillis = now.toEpochMillis();
        long minDiffMillis = Long.MAX_VALUE;
        for (Alarm alarm : allAlarms) {
            if (alarm.enabled && alarm.dateTime.compareTo(now) <= 0) {
                long diffMillis = nowEpochMillis - alarm.dateTime.toEpochMillis();
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
