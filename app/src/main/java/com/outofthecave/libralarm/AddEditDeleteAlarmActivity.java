package com.outofthecave.libralarm;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.TimePicker;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.outofthecave.libralarm.model.Alarm;
import com.outofthecave.libralarm.model.CalendarUtil;
import com.outofthecave.libralarm.model.NotificationType;

public class AddEditDeleteAlarmActivity extends AppCompatActivity {
    public static final int REQUEST_CODE = 1;

    private static final String FULLSCREEN_PERMISSION;
    static {
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.Q) {
            FULLSCREEN_PERMISSION = Manifest.permission.USE_FULL_SCREEN_INTENT;
        } else {
            FULLSCREEN_PERMISSION = "android.permission.USE_FULL_SCREEN_INTENT";
        }
    }

    private final ActivityResultLauncher<String> requestPermissionLauncher =
            registerForActivityResult(new ActivityResultContracts.RequestPermission(), isGranted -> {
                if (isGranted) {
                    onSaveAlarmButtonClick(null);
                } else {
                    afterFullscreenPermissionDenied();
                }
            });

    @Nullable
    private Alarm alarmBeingEdited = null;

    private final boolean[] shouldCancelSaving = new boolean[1];

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_edit_delete_alarm);

        Intent intent = getIntent();
        this.alarmBeingEdited = intent.getParcelableExtra(AlarmListActivity.EXTRA_ALARM_TO_EDIT);

        TimePicker timePicker = findViewById(R.id.timePicker);
        timePicker.setIs24HourView(true);

        if (alarmBeingEdited != null) {
            EditText nameTextField = findViewById(R.id.nameTextField);
            nameTextField.setText(alarmBeingEdited.name);

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                timePicker.setHour(alarmBeingEdited.dateTime.hour);
                timePicker.setMinute(alarmBeingEdited.dateTime.minute);
            } else {
                timePicker.setCurrentHour(alarmBeingEdited.dateTime.hour);
                timePicker.setCurrentMinute(alarmBeingEdited.dateTime.minute);
            }

            DatePicker datePicker = findViewById(R.id.datePicker);
            datePicker.updateDate(alarmBeingEdited.dateTime.year,
                    CalendarUtil.getMonthForCalendar(alarmBeingEdited.dateTime),
                    alarmBeingEdited.dateTime.day);

            if (alarmBeingEdited.notificationType == NotificationType.FULLSCREEN) {
                RadioButton notificationTypeFullscreen = findViewById(R.id.notificationTypeFullscreen);
                if (!notificationTypeFullscreen.isChecked()) {
                    notificationTypeFullscreen.toggle();
                }
            } else if (alarmBeingEdited.notificationType == NotificationType.HEADS_UP) {
                RadioButton notificationTypeHeadsUp = findViewById(R.id.notificationTypeHeadsUp);
                if (!notificationTypeHeadsUp.isChecked()) {
                    notificationTypeHeadsUp.toggle();
                }
            } else if (alarmBeingEdited.notificationType == NotificationType.NOTIFICATION) {
                RadioButton notificationTypeNotification = findViewById(R.id.notificationTypeNotification);
                if (!notificationTypeNotification.isChecked()) {
                    notificationTypeNotification.toggle();
                }
            }

        } else {
            // We're not editing an existing alarm, but creating a new one.
            Button deleteButton = findViewById(R.id.deleteAlarmButton);
            deleteButton.setVisibility(View.GONE);
        }
    }

    public void onSaveAlarmButtonClick(@Nullable View view) {
        synchronized (shouldCancelSaving) {
            if (shouldCancelSaving[0]) {
                shouldCancelSaving[0] = false;
                return;
            }
        }

        Intent intent = new Intent(this, AlarmListActivity.class);

        if (alarmBeingEdited == null) {
            alarmBeingEdited = new Alarm();
        }

        EditText nameTextField = findViewById(R.id.nameTextField);
        alarmBeingEdited.name = nameTextField.getText().toString().trim();

        alarmBeingEdited.enabled = true;

        TimePicker timePicker = findViewById(R.id.timePicker);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            alarmBeingEdited.dateTime.hour = timePicker.getHour();
            alarmBeingEdited.dateTime.minute = timePicker.getMinute();
        } else {
            alarmBeingEdited.dateTime.hour = timePicker.getCurrentHour();
            alarmBeingEdited.dateTime.minute = timePicker.getCurrentMinute();
        }

        DatePicker datePicker = findViewById(R.id.datePicker);
        alarmBeingEdited.dateTime.year = datePicker.getYear();
        alarmBeingEdited.dateTime.month = CalendarUtil.getOneBasedMonth(datePicker.getMonth());
        alarmBeingEdited.dateTime.day = datePicker.getDayOfMonth();

        RadioButton notificationTypeFullscreen = findViewById(R.id.notificationTypeFullscreen);
        RadioButton notificationTypeHeadsUp = findViewById(R.id.notificationTypeHeadsUp);
        RadioButton notificationTypeNotification = findViewById(R.id.notificationTypeNotification);
        if (notificationTypeFullscreen.isChecked()) {
            if (ContextCompat.checkSelfPermission(this, FULLSCREEN_PERMISSION)
                    == PackageManager.PERMISSION_GRANTED) {
                Log.d("AddEditDeleteAlarmActiv", "Fullscreen permission granted");
                alarmBeingEdited.notificationType = NotificationType.FULLSCREEN;
            } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M
                    && shouldShowRequestPermissionRationale(FULLSCREEN_PERMISSION)) {
                Log.d("AddEditDeleteAlarmActiv", "Should show request permission rationale");
                showRequestPermissionRationale();
                return;
            } else {
                requestFullscreenPermission();
                return;
            }
        } else if (notificationTypeHeadsUp.isChecked()) {
            alarmBeingEdited.notificationType = NotificationType.HEADS_UP;
        } else if (notificationTypeNotification.isChecked()) {
            alarmBeingEdited.notificationType = NotificationType.NOTIFICATION;
        } else {
            remindUserToSelectNotificationType();
            return;
        }

        intent.putExtra(AlarmListActivity.EXTRA_ALARM_TO_UPSERT, alarmBeingEdited);
        finishWithResult(intent);
    }

    private void showRequestPermissionRationale() {
        new AlertDialog.Builder(this)
                .setMessage("This app needs permission to show a fullscreen notification when the alarm triggers.")
                .setPositiveButton("Request permission", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        requestFullscreenPermission();
                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        afterFullscreenPermissionDenied();
                    }
                })
                .create()
                .show();
    }

    private void requestFullscreenPermission() {
        Log.d("AddEditDeleteAlarmActiv", "Requesting fullscreen permission");
        // The registered ActivityResultCallback gets the result of this request.
        requestPermissionLauncher.launch(FULLSCREEN_PERMISSION);

        Button saveAlarmButton = findViewById(R.id.saveAlarmButton);
        saveAlarmButton.setEnabled(false);
        Button cancelSavingButton = findViewById(R.id.cancelSavingButton);
        cancelSavingButton.setVisibility(View.VISIBLE);
    }

    private void afterFullscreenPermissionDenied() {
        Log.d("AddEditDeleteAlarmActiv", "Fullscreen permission denied");

        RadioGroup notificationTypeGroup = findViewById(R.id.notificationTypeGroup);
        notificationTypeGroup.clearCheck();

        RadioButton notificationTypeFullscreen = findViewById(R.id.notificationTypeFullscreen);
        notificationTypeFullscreen.setEnabled(false);

        remindUserToSelectNotificationType();
    }

    private void remindUserToSelectNotificationType() {
        TextView notificationTypeText = findViewById(R.id.notificationTypeText);
        RadioGroup notificationTypeGroup = findViewById(R.id.notificationTypeGroup);

        ScrollView scrollView = findViewById(R.id.scrollView);
        scrollView.scrollTo(0, notificationTypeText.getTop());
        notificationTypeText.requestFocus();

        // Flash the UI for selecting the notification type to direct the user's attention to it.
        Animation flashingAnimation = new AlphaAnimation(1, 0);
        flashingAnimation.setDuration(500);
        flashingAnimation.setRepeatCount(1);
        flashingAnimation.setRepeatMode(Animation.REVERSE);
        notificationTypeText.startAnimation(flashingAnimation);
        notificationTypeGroup.startAnimation(flashingAnimation);
    }

    public void onCancelSavingButtonClick(View view) {
        synchronized (shouldCancelSaving) {
            shouldCancelSaving[0] = true;
        }

        Button saveAlarmButton = findViewById(R.id.saveAlarmButton);
        saveAlarmButton.setEnabled(true);
        Button cancelSavingButton = findViewById(R.id.cancelSavingButton);
        cancelSavingButton.setVisibility(View.GONE);
    }

    public void onDeleteAlarmButtonClick(View view) {
        Intent intent = new Intent(this, AlarmListActivity.class);
        if (alarmBeingEdited != null) {
            intent.putExtra(AlarmListActivity.EXTRA_ALARM_ID_TO_DELETE, alarmBeingEdited.id);
        }
        finishWithResult(intent);
    }

    private void finishWithResult(Intent intent) {
        setResult(RESULT_OK, intent);
        finish();
    }
}
