package com.outofthecave.libralarm;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.google.android.material.snackbar.Snackbar;
import com.outofthecave.libralarm.databinding.ActivityAlarmListBinding;
import com.outofthecave.libralarm.model.Alarm;
import com.outofthecave.libralarm.room.AlarmDao;
import com.outofthecave.libralarm.room.AppDatabase;
import com.outofthecave.libralarm.ui.AlarmListRecyclerViewAdapter;
import com.outofthecave.libralarm.ui.AlarmListViewModel;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import needle.Needle;

public class AlarmListActivity extends AppCompatActivity {
    public static final String EXTRA_ALARM_TO_ADD = "com.outofthecave.geburtstagskalender.ALARM_TO_ADD";
    public static final String EXTRA_ALARM_TO_REPLACE = "com.outofthecave.geburtstagskalender.ALARM_TO_REPLACE";

    private ActivityAlarmListBinding binding;
    private AlarmListRecyclerViewAdapter recyclerViewAdapter;
    private AlarmListViewModel alarmListViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        final Context context = this;

        binding = ActivityAlarmListBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        setSupportActionBar(binding.toolbar);

        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.alarmRecycler);
        // Improve performance because changes in content do not change the layout size of the RecyclerView.
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        this.recyclerViewAdapter = new AlarmListRecyclerViewAdapter(this);
        recyclerView.setAdapter(recyclerViewAdapter);

        this.alarmListViewModel = new ViewModelProvider(this).get(AlarmListViewModel.class);
        alarmListViewModel.getAlarms().observe(this, new Observer<List<Alarm>>() {
            @Override
            public void onChanged(List<Alarm> alarms) {
                onAlarmListLoaded(context, alarms);
            }
        });

        binding.fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, AddEditDeleteAlarmActivity.class);
                startActivityForResult(intent, AddEditDeleteAlarmActivity.REQUEST_CODE);
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
        super.onActivityResult(requestCode, resultCode, intent);

        if (requestCode != AddEditDeleteAlarmActivity.REQUEST_CODE || resultCode != RESULT_OK) {
            return;
        }

        final Alarm alarmToReplace = intent.getParcelableExtra(EXTRA_ALARM_TO_REPLACE);
        final Alarm alarmToAdd = intent.getParcelableExtra(EXTRA_ALARM_TO_ADD);
        if (alarmToReplace == null && alarmToAdd == null) {
            return;
        }

        alarmListViewModel.replace(alarmToReplace, alarmToAdd);
    }

    public void onAlarmListLoaded(Context context, List<Alarm> alarms) {
        recyclerViewAdapter.setAlarms(alarms);
    }
}