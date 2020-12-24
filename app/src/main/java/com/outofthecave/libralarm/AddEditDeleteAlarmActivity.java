package com.outofthecave.libralarm;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.outofthecave.libralarm.model.Alarm;

public class AddEditDeleteAlarmActivity extends AppCompatActivity {
    public static final int REQUEST_CODE = 1;

    @Nullable
    private Alarm alarmToReplace = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_edit_delete_alarm);

        Intent intent = getIntent();
        this.alarmToReplace = intent.getParcelableExtra(AlarmListActivity.EXTRA_ALARM_TO_REPLACE);

        if (alarmToReplace != null) {
            EditText nameTextField = findViewById(R.id.nameTextField);
            nameTextField.setText(alarmToReplace.name);
        } else {
            Button deleteButton = findViewById(R.id.deleteAlarmButton);
            deleteButton.setVisibility(View.GONE);
        }
    }

    public void onSaveAlarmButtonClick(View view) {
        onButtonClickImpl(view, true);
    }

    public void onDeleteAlarmButtonClick(View view) {
        onButtonClickImpl(view, false);
    }

    private void onButtonClickImpl(View view, boolean doAddNewAlarm) {
        Intent intent = new Intent(this, AlarmListActivity.class);

        if (alarmToReplace != null) {
            intent.putExtra(AlarmListActivity.EXTRA_ALARM_TO_REPLACE, alarmToReplace);
        }

        if (doAddNewAlarm) {
            Alarm alarm = new Alarm();

            EditText nameTextField = findViewById(R.id.nameTextField);
            alarm.name = nameTextField.getText().toString().trim();

            intent.putExtra(AlarmListActivity.EXTRA_ALARM_TO_ADD, alarm);
        }

        setResult(RESULT_OK, intent);
        finish();
    }
}
