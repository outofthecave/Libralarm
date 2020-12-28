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

import com.outofthecave.libralarm.logic.AlarmListFilter;
import com.outofthecave.libralarm.model.Alarm;
import com.outofthecave.libralarm.model.DateTime;
import com.outofthecave.libralarm.model.NotificationType;
import com.outofthecave.libralarm.room.AppDatabase;

import java.util.ArrayList;
import java.util.List;

import needle.Needle;
import needle.UiRelatedTask;

public class AlarmNotifier extends BroadcastReceiver {
    private static final String FULLSCREEN_NOTIFICATION_CHANNEL_ID = "alarm_fullscreen";
    private static final String HEADS_UP_NOTIFICATION_CHANNEL_ID = "alarm_heads_up";
    private static final String PLAIN_NOTIFICATION_CHANNEL_ID = "alarm_notification";

    public static final String EXTRA_ALARMS = "com.outofthecave.libralarm.EXTRA_ALARMS";

    @Override
    public void onReceive(final Context context, Intent intent) {
        ArrayList<Alarm> alarms = intent.getParcelableArrayListExtra(EXTRA_ALARMS);
        Log.d("AlarmNotifier", "Triggered a scheduled notification for " + (alarms == null ? null : alarms.size()) + " alarm(s).");

        if (alarms != null) {
            showNotification(context, alarms);
        } else {
            final AppDatabase database = AppDatabase.getInstance(context);
            Needle.onBackgroundThread().execute(new UiRelatedTask<List<Alarm>>() {
                @Override
                protected List<Alarm> doWork() {
                    return database.alarmDao().getAll();
                }

                @Override
                protected void thenDoUiRelatedWork(@NonNull List<Alarm> allAlarms) {
                    Log.d("AlarmNotifier", "Retrieved " + allAlarms.size() + " alarm(s).");

                    ArrayList<Alarm> alarms = AlarmListFilter.getAlarmsToNotifyAboutNow(allAlarms);
                    showNotification(context, alarms);
                }
            });
        }
    }

    private void showNotification(Context context, @NonNull ArrayList<Alarm> alarms) {
        Log.d("AlarmNotifier", "Supposed to show a notification for " + alarms.size() + " alarm(s).");
        if (!alarms.isEmpty()) {
            String text = joinNames(alarms, "and", true);

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
                fullscreenIntent.putParcelableArrayListExtra(FullscreenAlarmActivity.EXTRA_ALARMS_FOR_FULLSCREEN, alarms);
                PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, fullscreenIntent, 0);
                notificationBuilder.setFullScreenIntent(pendingIntent, true);
            } else {
                Intent notificationTapIntent = new Intent(context, AlarmListActivity.class);
                PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, notificationTapIntent, 0);
                notificationBuilder.setContentIntent(pendingIntent);
            }

            notificationBuilder.setSmallIcon(R.drawable.ic_baseline_alarm_24)
                    .setContentTitle(text)
                    .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
                    .setCategory(NotificationCompat.CATEGORY_ALARM);

            NotificationManagerCompat notificationManager = NotificationManagerCompat.from(context);
            notificationManager.notify(0, notificationBuilder.build());

            AlarmNotificationScheduler.setLastTriggered(DateTime.now());
        }

        AlarmNotificationScheduler.scheduleNextNotification(context);
    }

    @VisibleForTesting
    static String joinNames(List<Alarm> alarms, String conjunction, boolean useOxfordComma) {
        if (alarms.size() == 1) {
            return alarms.get(0).name;
        }

        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < alarms.size(); ++i) {
            if (i == alarms.size() - 1) {
                if (useOxfordComma && alarms.size() > 2) {
                    sb.append(",");
                }
                sb.append(" ");
                sb.append(conjunction);
                sb.append(" ");
            } else if (i != 0) {
                sb.append(", ");
            }
            sb.append(alarms.get(i).name);
        }
        return sb.toString();
    }

    public static void registerNotificationChannels(Context context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            String fullscreenName = "Fullscreen alarms";
            String fullscreenDesc = "Notifications for fullscreen alarms created in the app";
            NotificationChannel fullscreenChannel = new NotificationChannel(FULLSCREEN_NOTIFICATION_CHANNEL_ID, fullscreenName, NotificationManager.IMPORTANCE_HIGH);
            fullscreenChannel.setDescription(fullscreenDesc);

            String headsUpName = "Screen-top alarms";
            String headsUpDesc = "Notifications shown on top of whatever is on the screen for alarms created in the app";
            NotificationChannel headsUpChannel = new NotificationChannel(HEADS_UP_NOTIFICATION_CHANNEL_ID, headsUpName, NotificationManager.IMPORTANCE_HIGH);
            headsUpChannel.setDescription(headsUpDesc);

            String plainName = "Plain alarm notifications";
            String plainDesc = "Plain notifications for alarms created in the app";
            NotificationChannel plainChannel = new NotificationChannel(PLAIN_NOTIFICATION_CHANNEL_ID, plainName, NotificationManager.IMPORTANCE_DEFAULT);
            plainChannel.setDescription(plainDesc);

            NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
            notificationManager.createNotificationChannel(fullscreenChannel);
            notificationManager.createNotificationChannel(headsUpChannel);
            notificationManager.createNotificationChannel(plainChannel);
        }
    }
}
