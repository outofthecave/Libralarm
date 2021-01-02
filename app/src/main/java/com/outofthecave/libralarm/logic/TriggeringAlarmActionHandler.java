package com.outofthecave.libralarm.logic;

import android.content.Context;

import com.outofthecave.libralarm.AlarmNotificationScheduler;
import com.outofthecave.libralarm.model.Alarm;
import com.outofthecave.libralarm.model.SnoozedAlarm;
import com.outofthecave.libralarm.room.AlarmData;
import com.outofthecave.libralarm.room.AlarmKey;
import com.outofthecave.libralarm.room.AppDatabase;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import needle.Needle;
import needle.UiRelatedTask;

/**
 * Handler for actions (such as snooze and cancel) on alarms that are currently triggering.
 */
public class TriggeringAlarmActionHandler {
    private TriggeringAlarmActionHandler() {
    }

    public static void snoozeAlarms(final Context context, List<Alarm> alarmsToSnooze, Map<Integer, SnoozedAlarm> idToSnoozedAlarm) {
        final List<SnoozedAlarm> snoozedAlarms = new ArrayList<>(alarmsToSnooze.size());
        for (Alarm alarm : alarmsToSnooze) {
            SnoozedAlarm snoozedAlarm = idToSnoozedAlarm.get(alarm.id);
            if (AlarmListFilter.isSnoozingPossible(alarm, snoozedAlarm)) {
                if (snoozedAlarm == null) {
                    snoozedAlarm = new SnoozedAlarm(alarm.id);
                }
                snoozedAlarm.snoozeCount += 1;
                snoozedAlarms.add(snoozedAlarm);
            }
        }

        final AppDatabase database = AppDatabase.getInstance(context);
        Needle.onBackgroundThread().execute(new UiRelatedTask<AlarmData>() {
            @Override
            protected AlarmData doWork() {
                database.snoozedAlarmDao().upsert(snoozedAlarms);

                AlarmData alarmData = new AlarmData();
                alarmData.alarms = database.alarmDao().getAll();
                alarmData.snoozedAlarms = database.snoozedAlarmDao().getAll();
                return alarmData;
            }

            @Override
            protected void thenDoUiRelatedWork(AlarmData alarmData) {
                AlarmNotificationScheduler.scheduleNextNotification(context, alarmData);
            }
        });
    }

    public static void cancelAlarms(Context context, List<Alarm> alarmsToCancel) {
        List<AlarmKey> snoozedAlarmsToDelete = new ArrayList<>(alarmsToCancel.size());
        for (Alarm alarm : alarmsToCancel) {
            AlarmKey key = new AlarmKey();
            key.id = alarm.id;
            snoozedAlarmsToDelete.add(key);
        }

        final AppDatabase database = AppDatabase.getInstance(context);
        Needle.onBackgroundThread().execute(new UiRelatedTask<AlarmData>() {
            @Override
            protected AlarmData doWork() {
                database.snoozedAlarmDao().deleteByKey(snoozedAlarmsToDelete);

                AlarmData alarmData = new AlarmData();
                alarmData.alarms = database.alarmDao().getAll();
                alarmData.snoozedAlarms = database.snoozedAlarmDao().getAll();
                return alarmData;
            }

            @Override
            protected void thenDoUiRelatedWork(AlarmData alarmData) {
                AlarmNotificationScheduler.scheduleNextNotification(context, alarmData);
            }
        });
    }
}
