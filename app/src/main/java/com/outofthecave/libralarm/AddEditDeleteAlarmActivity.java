package com.outofthecave.libralarm;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.TimePicker;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.outofthecave.libralarm.model.Alarm;
import com.outofthecave.libralarm.model.CalendarUtil;
import com.outofthecave.libralarm.model.NotificationType;

public class AddEditDeleteAlarmActivity extends AppCompatActivity {
    public static final int REQUEST_CODE = 1;

    @Nullable
    private Alarm alarmBeingEdited = null;

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

    public void onSaveAlarmButtonClick(View view) {
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
            alarmBeingEdited.notificationType = NotificationType.FULLSCREEN;
        } else if (notificationTypeHeadsUp.isChecked()) {
            alarmBeingEdited.notificationType = NotificationType.HEADS_UP;
        } else if (notificationTypeNotification.isChecked()) {
            alarmBeingEdited.notificationType = NotificationType.NOTIFICATION;
        }

        intent.putExtra(AlarmListActivity.EXTRA_ALARM_TO_UPSERT, alarmBeingEdited);
        finishWithResult(intent);
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
