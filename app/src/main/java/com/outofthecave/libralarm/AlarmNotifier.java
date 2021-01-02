package com.outofthecave.libralarm;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.VisibleForTesting;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.core.content.ContextCompat;

import com.outofthecave.libralarm.logic.AlarmCanceler;
import com.outofthecave.libralarm.logic.AlarmListFilter;
import com.outofthecave.libralarm.logic.AlarmNameFormatter;
import com.outofthecave.libralarm.logic.AlarmSnoozer;
import com.outofthecave.libralarm.model.Alarm;
import com.outofthecave.libralarm.model.DateTime;
import com.outofthecave.libralarm.model.NotificationType;
import com.outofthecave.libralarm.model.SnoozedAlarm;
import com.outofthecave.libralarm.room.AlarmData;
import com.outofthecave.libralarm.room.AppDatabase;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import needle.Needle;
import needle.UiRelatedTask;

public class AlarmNotifier extends BroadcastReceiver {
    private static final String FULLSCREEN_NOTIFICATION_CHANNEL_ID = "alarm_fullscreen";
    private static final String HEADS_UP_NOTIFICATION_CHANNEL_ID = "alarm_heads_up";
    private static final String PLAIN_NOTIFICATION_CHANNEL_ID = "alarm_notification";

    @Override
    public void onReceive(final Context context, Intent intent) {
        Log.d("AlarmNotifier", "Triggered a scheduled notification");

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
                Log.d("AlarmNotifier", "Retrieved " + alarmData.alarms.size() + " alarm(s).");

                Map<Integer, SnoozedAlarm> idToSnoozedAlarm = AlarmListFilter.toSnoozedAlarmMap(alarmData.snoozedAlarms);
                ArrayList<Alarm> alarms = AlarmListFilter.getAlarmsToNotifyAboutNow(alarmData.alarms, idToSnoozedAlarm);
                showNotification(context, alarms, idToSnoozedAlarm);
            }
        });
    }

    private void showNotification(Context context, @NonNull ArrayList<Alarm> alarms, Map<Integer, SnoozedAlarm> idToSnoozedAlarm) {
        Log.d("AlarmNotifier", "Supposed to show a notification for " + alarms.size() + " alarm(s).");
        if (!alarms.isEmpty()) {
            String text = AlarmNameFormatter.joinAlarmNamesOnNewline(alarms);

            NotificationType notificationType = NotificationType.NOTIFICATION;
            for (Alarm alarm : alarms) {
                if (alarm.notificationType.getValue() < notificationType.getValue()) {
                    notificationType = alarm.notificationType;
                }
            }
            Log.d("AlarmNotifier", "Effective notification type: " + notificationType);

            String channelId;
            if (notificationType == NotificationType.FULLSCREEN) {
                channelId = FULLSCREEN_NOTIFICATION_CHANNEL_ID;
            } else if (notificationType == NotificationType.HEADS_UP) {
                channelId = HEADS_UP_NOTIFICATION_CHANNEL_ID;
            } else {
                channelId = PLAIN_NOTIFICATION_CHANNEL_ID;
            }
            NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(context, channelId);

            if (notificationType == NotificationType.FULLSCREEN
                    || notificationType == NotificationType.HEADS_UP) {
                notificationBuilder.setPriority(NotificationCompat.PRIORITY_HIGH);
            } else {
                notificationBuilder.setPriority(NotificationCompat.PRIORITY_DEFAULT);
            }

            if (notificationType == NotificationType.FULLSCREEN
                    && ContextCompat.checkSelfPermission(context,
                    AddEditDeleteAlarmActivity.FULLSCREEN_PERMISSION)
                    == PackageManager.PERMISSION_GRANTED) {
                Log.d("AlarmNotifier", "Creating a fullscreen intent");
                Intent fullscreenIntent = new Intent(context, FullscreenAlarmActivity.class);
                fullscreenIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK
                        | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, fullscreenIntent, 0);
                notificationBuilder.setFullScreenIntent(pendingIntent, true);

            } else {
                Intent notificationTapIntent = new Intent(context, AlarmListActivity.class);
                PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, notificationTapIntent, 0);
                notificationBuilder.setContentIntent(pendingIntent);

                if (AlarmListFilter.isSnoozingPossibleForAny(alarms, idToSnoozedAlarm)) {
                    Intent snoozeIntent = new Intent(context, AlarmSnoozer.class);
                    PendingIntent snoozePendingIntent = PendingIntent.getActivity(context, 0, snoozeIntent, 0);
                    notificationBuilder.addAction(R.drawable.ic_baseline_snooze_24,
                            context.getString(R.string.notification_action_snooze_alarm),
                            snoozePendingIntent);
                }

                Intent cancelIntent = new Intent(context, AlarmCanceler.class);
                PendingIntent cancelPendingIntent = PendingIntent.getActivity(context, 0, cancelIntent, 0);
                notificationBuilder.addAction(R.drawable.ic_baseline_alarm_off_24,
                        context.getString(R.string.notification_action_cancel_alarm),
                        cancelPendingIntent);
            }

            notificationBuilder.setSmallIcon(R.drawable.ic_baseline_alarm_24)
                    .setContentText(text)
                    .setStyle(new NotificationCompat.BigTextStyle()
                            .bigText(text))
                    .setAllowSystemGeneratedContextualActions(false)
                    .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
                    .setCategory(NotificationCompat.CATEGORY_ALARM);

            NotificationManagerCompat notificationManager = NotificationManagerCompat.from(context);
            notificationManager.notify(0, notificationBuilder.build());

            AlarmNotificationScheduler.setLastTriggered(DateTime.now());
        }

        AlarmNotificationScheduler.scheduleNextNotification(context);
    }

    public static void registerNotificationChannels(Context context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel fullscreenChannel = new NotificationChannel(
                    FULLSCREEN_NOTIFICATION_CHANNEL_ID,
                    context.getString(R.string.notification_channel_fullscreen_name),
                    NotificationManager.IMPORTANCE_HIGH);
            fullscreenChannel.setDescription(context.getString(R.string.notification_channel_fullscreen_desc));

            NotificationChannel headsUpChannel = new NotificationChannel(
                    HEADS_UP_NOTIFICATION_CHANNEL_ID,
                    context.getString(R.string.notification_channel_heads_up_name),
                    NotificationManager.IMPORTANCE_HIGH);
            headsUpChannel.setDescription(context.getString(R.string.notification_channel_heads_up_desc));

            NotificationChannel plainChannel = new NotificationChannel(
                    PLAIN_NOTIFICATION_CHANNEL_ID,
                    context.getString(R.string.notification_channel_plain_name),
                    NotificationManager.IMPORTANCE_DEFAULT);
            plainChannel.setDescription(context.getString(R.string.notification_channel_plain_desc));

            NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
            notificationManager.createNotificationChannel(fullscreenChannel);
            notificationManager.createNotificationChannel(headsUpChannel);
            notificationManager.createNotificationChannel(plainChannel);
        }
    }
}
