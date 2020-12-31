package com.outofthecave.libralarm;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.util.Log;

import androidx.annotation.Nullable;

import com.outofthecave.libralarm.logic.AlarmListFilter;
import com.outofthecave.libralarm.model.Alarm;
import com.outofthecave.libralarm.model.DateTime;
import com.outofthecave.libralarm.model.SnoozedAlarm;
import com.outofthecave.libralarm.room.AppDatabase;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import needle.Needle;
import needle.UiRelatedTask;

public class AlarmNotificationScheduler extends BroadcastReceiver {
    /** The time at which the last notification was shown to the user. */
    private static DateTime lastTriggered = new DateTime();

    public static void setLastTriggered(DateTime lastTriggered) {
        AlarmNotificationScheduler.lastTriggered = lastTriggered;
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        if ("android.intent.action.BOOT_COMPLETED".equals(intent.getAction())) {
            scheduleNextNotification(context);
        }
    }

    public static void scheduleNextNotification(final Context context) {
        final AppDatabase database = AppDatabase.getInstance(context);
        Needle.onBackgroundThread().execute(new UiRelatedTask<List<Alarm>>() {
            @Override
            protected List<Alarm> doWork() {
                return database.alarmDao().getAll();
            }

            @Override
            protected void thenDoUiRelatedWork(List<Alarm> alarms) {
                scheduleNextNotification(context, alarms, idToSnoozedAlarm);
            }
        });
    }

    public static void scheduleNextNotification(Context context, List<Alarm> alarms, Map<Integer, SnoozedAlarm> idToSnoozedAlarm) {
        if (!alarms.isEmpty()) {
            // Android only allows us to set one trigger for the AlarmNotifier, so we only schedule
            // a notification for the closest upcoming alarms (in case there are multiple alarms at
            // the same time).
            ArrayList<Alarm> upcomingAlarms = AlarmListFilter.getAlarmsComingUpNow(alarms, idToSnoozedAlarm, lastTriggered);

            if (!upcomingAlarms.isEmpty()) {
                long triggerTimestamp = upcomingAlarms.get(0).dateTime.toEpochMillis();
                scheduleNotification(context, triggerTimestamp, upcomingAlarms);

                setAutoSchedulingOnReboot(context, true);
                return;
            }
        }

        Log.d("AlarmNotifScheduler", "Canceling any scheduled notifications");
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        Intent notifierIntent = new Intent(context, AlarmNotifier.class);
        PendingIntent pendingNotifierIntent = getPendingNotifierIntent(context, notifierIntent);
        alarmManager.cancel(pendingNotifierIntent);
        setAutoSchedulingOnReboot(context, false);
    }

    private static PendingIntent getPendingNotifierIntent(Context context, Intent notifierIntent) {
        return PendingIntent.getBroadcast(context, 0, notifierIntent, 0);
    }

    /**
     * Enable or disable auto-scheduling the notifications again when the device is rebooted.
     *
     * @param context The current context.
     * @param enabled Whether to enable the automatic scheduling. If this is
     *                {@code false}, the automatic scheduling will be disabled.
     */
    private static void setAutoSchedulingOnReboot(Context context, boolean enabled) {
        ComponentName receiver = new ComponentName(context, AlarmNotificationScheduler.class);
        PackageManager packageManager = context.getPackageManager();
        int state;
        if (enabled) {
            state = PackageManager.COMPONENT_ENABLED_STATE_ENABLED;
        } else {
            state = PackageManager.COMPONENT_ENABLED_STATE_DISABLED;
        }
        packageManager.setComponentEnabledSetting(receiver, state, PackageManager.DONT_KILL_APP);
    }

    /**
     * Schedule a notification for a specific time.
     *
     * @param context The current context.
     * @param triggerTimestamp When to trigger the notification, in milliseconds since the Unix Epoch.
     * @param alarmsToNotifyAbout The alarms to mention in the notification.
     */
    public static void scheduleNotification(Context context, long triggerTimestamp, @Nullable ArrayList<Alarm> alarmsToNotifyAbout) {
        Log.d("AlarmNotifScheduler", "Scheduling an alarm notification for epoch time: " + triggerTimestamp);

        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        Intent notifierIntent = new Intent(context, AlarmNotifier.class);

        notifierIntent.putParcelableArrayListExtra(AlarmNotifier.EXTRA_ALARMS, alarmsToNotifyAbout);
        PendingIntent pendingNotifierIntent = getPendingNotifierIntent(context, notifierIntent);

        alarmManager.setExact(AlarmManager.RTC_WAKEUP, triggerTimestamp, pendingNotifierIntent);
    }
}
