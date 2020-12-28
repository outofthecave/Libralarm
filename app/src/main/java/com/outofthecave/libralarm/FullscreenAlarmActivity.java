package com.outofthecave.libralarm;

import android.app.KeyguardManager;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.WindowManager;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.outofthecave.libralarm.model.Alarm;

import java.util.ArrayList;

public class FullscreenAlarmActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d("FullscreenAlarmActivity", "onCreate");
        setContentView(R.layout.activity_fullscreen_alarm);

        Intent intent = getIntent();
        ArrayList<Alarm> alarms = intent.getParcelableArrayListExtra(AlarmNotifier.EXTRA_ALARMS);
        if (!alarms.isEmpty()) {
            // TODO Design actual UI
            TextView alarmName = findViewById(R.id.alarmName);
            alarmName.setText(alarms.get(0).name);
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
}
