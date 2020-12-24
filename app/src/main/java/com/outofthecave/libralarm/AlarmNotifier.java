package com.outofthecave.libralarm;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.VisibleForTesting;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.outofthecave.libralarm.model.Alarm;
import com.outofthecave.libralarm.model.CalendarUtil;
import com.outofthecave.libralarm.model.DateTime;
import com.outofthecave.libralarm.room.AppDatabase;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import needle.Needle;
import needle.UiRelatedTask;

public class AlarmNotifier extends BroadcastReceiver {
    private static final String NOTIFICATION_CHANNEL_ID = "alarm";
    private static final long THRESHOLD_MILLIS = 2 * 60 * 1000;

    public static final String EXTRA_ALARMS = "com.outofthecave.libralarm.EXTRA_ALARMS";

    @Override
    public void onReceive(final Context context, Intent intent) {
        List<Alarm> alarms = intent.getParcelableArrayListExtra(EXTRA_ALARMS);
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

                    List<Alarm> alarms = new ArrayList<>();
                    DateTime now = DateTime.now();
                    long nowEpochMillis = now.toEpochMillis();
                    long minDiffMillis = Long.MAX_VALUE;
                    for (Alarm alarm : allAlarms) {
                        if (alarm.dateTime.compareTo(now) <= 0) {
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

                    showNotification(context, alarms);
                }
            });
        }
    }

    private void showNotification(Context context, @NonNull List<Alarm> alarms) {
        Log.d("AlarmNotifier", "Supposed to show a notification for " + alarms.size() + " alarm(s).");
        if (!alarms.isEmpty()) {
            String text = joinNames(alarms, "and", true);

            Intent notificationTapIntent = new Intent(context, AlarmListActivity.class);
            NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(context, NOTIFICATION_CHANNEL_ID)
                    .setSmallIcon(R.drawable.ic_baseline_alarm_24)
                    .setContentTitle(text)
                    .setContentIntent(PendingIntent.getActivity(context, 0, notificationTapIntent, 0))
                    .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
                    .setCategory(NotificationCompat.CATEGORY_ALARM)
                    .setPriority(NotificationCompat.PRIORITY_DEFAULT);

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

    public static void registerNotificationChannel(Context context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            String name = "Alarm";
            String description = "Notifications for alarms created in the app";
            int importance = NotificationManager.IMPORTANCE_HIGH;
            NotificationChannel channel = new NotificationChannel(NOTIFICATION_CHANNEL_ID, name, importance);
            channel.setDescription(description);
            NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
            notificationManager.createNotificationChannel(channel);
        }
    }
}
