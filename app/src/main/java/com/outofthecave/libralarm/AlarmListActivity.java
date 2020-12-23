package com.outofthecave.libralarm;

import android.content.Context;
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
            public void onChanged(List<Alarm> birthdays) {
                onAlarmListLoaded(context, birthdays);
            }
        });

        binding.fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "TODO Add new alarm", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        // TODO Remove example alarms
        Needle.onBackgroundThread().execute(new Runnable() {
            @Override
            public void run() {
                AppDatabase database = AppDatabase.getInstance(context);
                AlarmDao alarmDao = database.alarmDao();
                Alarm exampleAlarm1 = new Alarm();
                exampleAlarm1.name = "Travail";
                exampleAlarm1.dateTime.hour = 7;
                exampleAlarm1.dateTime.minute = 30;
                alarmDao.add(exampleAlarm1);
                Alarm exampleAlarm2 = new Alarm();
                exampleAlarm2.name = "Fin de semaine";
                exampleAlarm2.dateTime.hour = 8;
                exampleAlarm2.dateTime.minute = 30;
                alarmDao.add(exampleAlarm2);
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

    public void onAlarmListLoaded(Context context, List<Alarm> alarms) {
        recyclerViewAdapter.setAlarms(alarms);
    }
}