package com.outofthecave.libralarm;

import android.app.KeyguardManager;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.outofthecave.libralarm.logic.AlarmListFilter;
import com.outofthecave.libralarm.logic.AlarmNameFormatter;
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

public class FullscreenAlarmActivity extends AppCompatActivity {
    @Nullable
    private ArrayList<Alarm> alarms = null;
    @Nullable
    private Map<Integer, SnoozedAlarm> idToSnoozedAlarm = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d("FullscreenAlarmActivity", "onCreate");
        setContentView(R.layout.activity_fullscreen_alarm);

        final AppDatabase database = AppDatabase.getInstance(this);
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
                Log.d("FullscreenAlarmActivity", String.format("Retrieved %s alarm(s).", alarmData.alarms.size()));

                Map<Integer, SnoozedAlarm> idToSnoozedAlarm = AlarmListFilter.toSnoozedAlarmMap(alarmData.snoozedAlarms);
                ArrayList<Alarm> alarms = AlarmListFilter.getAlarmsToNotifyAboutNow(alarmData.alarms, idToSnoozedAlarm);
                onAlarmListReceived(alarms, idToSnoozedAlarm);
            }
        });

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O_MR1) {
            setShowWhenLocked(true);
            setTurnScreenOn(true);
        } else {
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED
                    | WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD
                    | WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON
                    | WindowManager.LayoutParams.FLAG_FULLSCREEN);
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            // This code looks like it doesn't do anything, but without it, the screen won't turn
            // on if the screen is locked with a PIN.
            Log.d("FullscreenAlarmActivity", "Keyguard dismiss: requesting");
            KeyguardManager keyguardManager = (KeyguardManager) getSystemService(Context.KEYGUARD_SERVICE);
            keyguardManager.requestDismissKeyguard(this, new KeyguardManager.KeyguardDismissCallback() {
                @Override
                public void onDismissError() {
                    super.onDismissError();
                    Log.d("FullscreenAlarmActivity", "Keyguard dismiss: error");
                }

                @Override
                public void onDismissSucceeded() {
                    super.onDismissSucceeded();
                    Log.d("FullscreenAlarmActivity", "Keyguard dismiss: succeeded");
                }

                @Override
                public void onDismissCancelled() {
                    super.onDismissCancelled();
                    Log.d("FullscreenAlarmActivity", "Keyguard dismiss: cancelled");
                }
            });
        }
    }

    private void onAlarmListReceived(ArrayList<Alarm> alarms, Map<Integer, SnoozedAlarm> idToSnoozedAlarm) {
        this.alarms = alarms;
        this.idToSnoozedAlarm = idToSnoozedAlarm;

        String text = AlarmNameFormatter.joinAlarmNamesOnNewline(alarms);
        TextView alarmName = findViewById(R.id.alarmName);
        if (!text.isEmpty()) {
            alarmName.setText(text);
        }

        if (!AlarmListFilter.isSnoozingPossibleForAny(alarms, idToSnoozedAlarm)) {
            Button snoozeAlarmButton = findViewById(R.id.snoozeAlarmButton);
            snoozeAlarmButton.setVisibility(View.GONE);
        }
    }

    public void onSnoozeAlarmButtonClick(View view) {
        Log.d("FullscreenAlarmActivity", "onSnoozeAlarmButtonClick");

        if (alarms != null && idToSnoozedAlarm != null) {
            final List<SnoozedAlarm> snoozedAlarms = new ArrayList<>(alarms.size());
            for (Alarm alarm : alarms) {
                SnoozedAlarm snoozedAlarm = idToSnoozedAlarm.get(alarm.id);
                if (AlarmListFilter.isSnoozingPossible(alarm, snoozedAlarm)) {
                    if (snoozedAlarm == null) {
                        snoozedAlarm = new SnoozedAlarm(alarm.id);
                    }
                    snoozedAlarm.snoozeCount += 1;
                    snoozedAlarms.add(snoozedAlarm);
                }
            }

            final AppDatabase database = AppDatabase.getInstance(this);
            Needle.onBackgroundThread().execute(new Runnable() {
                @Override
                public void run() {
                    database.snoozedAlarmDao().upsert(snoozedAlarms);
                }
            });
        }

        finish();
    }

    public void onCancelAlarmButtonClick(View view) {
        Log.d("FullscreenAlarmActivity", "onCancelAlarmButtonClick");

        if (alarms != null) {
            List<AlarmKey> snoozedAlarmsToDelete = new ArrayList<>(alarms.size());
            for (Alarm alarm : alarms) {
                AlarmKey key = new AlarmKey();
                key.id = alarm.id;
                snoozedAlarmsToDelete.add(key);
            }

            final AppDatabase database = AppDatabase.getInstance(this);
            Needle.onBackgroundThread().execute(new Runnable() {
                @Override
                public void run() {
                    database.snoozedAlarmDao().deleteByKey(snoozedAlarmsToDelete);
                }
            });
        }

        finish();
    }
}
