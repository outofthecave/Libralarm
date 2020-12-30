package com.outofthecave.libralarm;

import android.app.KeyguardManager;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.outofthecave.libralarm.logic.AlarmListFilter;
import com.outofthecave.libralarm.logic.AlarmNameFormatter;
import com.outofthecave.libralarm.model.Alarm;
import com.outofthecave.libralarm.model.SnoozedAlarm;
import com.outofthecave.libralarm.room.AppDatabase;

import java.util.ArrayList;
import java.util.List;

import needle.Needle;
import needle.UiRelatedTask;

public class FullscreenAlarmActivity extends AppCompatActivity {
    public static final String EXTRA_ALARMS_FOR_FULLSCREEN = "com.outofthecave.libralarm.ALARMS_FOR_FULLSCREEN";

    @Nullable
    private ArrayList<Alarm> alarms = null;
    @Nullable
    private ArrayList<SnoozedAlarm> snoozedAlarms = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d("FullscreenAlarmActivity", "onCreate");
        setContentView(R.layout.activity_fullscreen_alarm);

        Intent intent = getIntent();
        ArrayList<Alarm> alarms = intent.getParcelableArrayListExtra(EXTRA_ALARMS_FOR_FULLSCREEN);
        Log.d("FullscreenAlarmActivity", String.format("Received %s alarm(s).", (alarms == null ? null : alarms.size())));

        if (alarms != null) {
            onAlarmListReceived(alarms);
        } else {
            final AppDatabase database = AppDatabase.getInstance(this);
            Needle.onBackgroundThread().execute(new UiRelatedTask<List<Alarm>>() {
                @Override
                protected List<Alarm> doWork() {
                    return database.alarmDao().getAll();
                }

                @Override
                protected void thenDoUiRelatedWork(@NonNull List<Alarm> allAlarms) {
                    Log.d("FullscreenAlarmActivity", String.format("Retrieved %s alarm(s).", allAlarms.size()));

                    ArrayList<Alarm> alarms = AlarmListFilter.getAlarmsToNotifyAboutNow(allAlarms);
                    onAlarmListReceived(alarms);
                }
            });
        }

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

    private void onAlarmListReceived(ArrayList<Alarm> alarms) {
        this.alarms = alarms;
        this.snoozedAlarms = new ArrayList<>(alarms.size());
        for (Alarm alarm : alarms) {
            SnoozedAlarm snoozedAlarm = new SnoozedAlarm(alarm.id);
            snoozedAlarms.add(snoozedAlarm);
        }

        String text = AlarmNameFormatter.joinAlarmNamesOnNewline(alarms);
        TextView alarmName = findViewById(R.id.alarmName);
        if (!text.isEmpty()) {
            alarmName.setText(text);
        }
    }

    public void onSnoozeAlarmButtonClick(View view) {
        Log.d("FullscreenAlarmActivity", "onSnoozeAlarmButtonClick");

        if (snoozedAlarms != null) {
            for (SnoozedAlarm snoozedAlarm : snoozedAlarms) {
                snoozedAlarm.snoozeCount += 1;
            }

            final AppDatabase database = AppDatabase.getInstance(this);
            Needle.onBackgroundThread().execute(new Runnable() {
                @Override
                public void run() {
                    database.snoozedAlarmDao().updateSnoozedAlarms(snoozedAlarms);
                }
            });
        }

        finish();
    }

    public void onCancelAlarmButtonClick(View view) {
        Log.d("FullscreenAlarmActivity", "onCancelAlarmButtonClick");
        finish();
    }
}
