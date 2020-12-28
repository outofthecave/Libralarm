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
import androidx.appcompat.app.AppCompatActivity;

import com.outofthecave.libralarm.logic.AlarmListFilter;
import com.outofthecave.libralarm.model.Alarm;
import com.outofthecave.libralarm.room.AppDatabase;

import java.util.ArrayList;
import java.util.List;

import needle.Needle;
import needle.UiRelatedTask;

public class FullscreenAlarmActivity extends AppCompatActivity {
    public static final String EXTRA_ALARMS_FOR_FULLSCREEN = "com.outofthecave.libralarm.ALARMS_FOR_FULLSCREEN";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d("FullscreenAlarmActivity", "onCreate");
        setContentView(R.layout.activity_fullscreen_alarm);

        Intent intent = getIntent();
        ArrayList<Alarm> alarms = intent.getParcelableArrayListExtra(EXTRA_ALARMS_FOR_FULLSCREEN);
        Log.d("FullscreenAlarmActivity", String.format("Received %s alarm(s).", (alarms == null ? null : alarms.size())));

        if (alarms != null) {
            fillTextViewWithAlarmNames(alarms);
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
                    fillTextViewWithAlarmNames(alarms);
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

    private void fillTextViewWithAlarmNames(List<Alarm> alarms) {
        String text = joinAlarmNames(alarms);
        TextView alarmName = findViewById(R.id.alarmName);
        if (!text.isEmpty()) {
            alarmName.setText(text);
        }
    }

    private String joinAlarmNames(List<Alarm> alarms) {
        StringBuilder text = new StringBuilder();
        boolean isFirst = true;
        for (Alarm alarm : alarms) {
            if (!alarm.name.isEmpty()) {
                if (!isFirst) {
                    text.append("\n");
                }
                text.append(alarm.name);
                isFirst = false;
            }
        }
        return text.toString();
    }

    public void onCancelAlarmButtonClick(View view) {
        Log.d("FullscreenAlarmActivity", "onCancelAlarmButtonClick");
        finish();
    }
}
