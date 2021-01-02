package com.outofthecave.libralarm.logic;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import androidx.annotation.NonNull;

import com.outofthecave.libralarm.AlarmNotificationScheduler;
import com.outofthecave.libralarm.model.Alarm;
import com.outofthecave.libralarm.model.DateTime;
import com.outofthecave.libralarm.model.SnoozedAlarm;
import com.outofthecave.libralarm.room.AlarmData;
import com.outofthecave.libralarm.room.AppDatabase;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import needle.Needle;
import needle.UiRelatedTask;

public class AlarmCanceler extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        final AppDatabase database = AppDatabase.getInstance(context);
        Needle.onBackgroundThread().execute(new UiRelatedTask<AlarmData>() {
            @Override
            protected AlarmData doWork() {
                AlarmData alarmData = new AlarmData();
                alarmData.alarms = database.alarmDao().getAll();
                alarmData.snoozedAlarms = database.snoozedAlarmDao().getAll();
                return alarmData;
            }

            @Override
            protected void thenDoUiRelatedWork(@NonNull AlarmData alarmData) {
                Log.d("AlarmCanceler", String.format("Retrieved %s alarm(s).", alarmData.alarms.size()));

                Map<Integer, SnoozedAlarm> idToSnoozedAlarm = AlarmListFilter.toSnoozedAlarmMap(alarmData.snoozedAlarms);
                // INVARIANT: We assume there is at most one notification showing at any time.
                DateTime lastTriggered = AlarmNotificationScheduler.getLastTriggered();
                ArrayList<Alarm> alarms = AlarmListFilter.getAlarmsToNotifyAboutAtDateTime(alarmData.alarms, idToSnoozedAlarm, lastTriggered);
                TriggeringAlarmActionHandler.cancelAlarms(context, alarms);
            }
        });
    }
}
