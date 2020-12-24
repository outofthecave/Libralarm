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
    private Alarm alarmBeingEdited = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_edit_delete_alarm);

        Intent intent = getIntent();
        this.alarmBeingEdited = intent.getParcelableExtra(AlarmListActivity.EXTRA_ALARM_TO_EDIT);

        if (alarmBeingEdited != null) {
            EditText nameTextField = findViewById(R.id.nameTextField);
            nameTextField.setText(alarmBeingEdited.name);
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
