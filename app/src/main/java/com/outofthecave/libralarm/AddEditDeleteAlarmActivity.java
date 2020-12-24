package com.outofthecave.libralarm;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TimePicker;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.outofthecave.libralarm.model.Alarm;
import com.outofthecave.libralarm.model.CalendarUtil;

import java.util.Calendar;

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
        } else {
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

        TimePicker timePicker = findViewById(R.id.timePicker);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            alarmBeingEdited.dateTime.hour = timePicker.getHour();
            alarmBeingEdited.dateTime.minute = timePicker.getMinute();
        } else {
            alarmBeingEdited.dateTime.hour = timePicker.getCurrentHour();
            alarmBeingEdited.dateTime.minute = timePicker.getCurrentMinute();
        }

        // TODO Remove hardcoded date
        Calendar now = Calendar.getInstance();
        alarmBeingEdited.dateTime.year = now.get(Calendar.YEAR);
        alarmBeingEdited.dateTime.month = CalendarUtil.getOneBasedMonth(now);
        alarmBeingEdited.dateTime.day = now.get(Calendar.DAY_OF_MONTH);

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
