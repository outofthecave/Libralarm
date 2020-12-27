package com.outofthecave.libralarm.ui;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.outofthecave.libralarm.model.Alarm;
import com.outofthecave.libralarm.room.AlarmDao;
import com.outofthecave.libralarm.room.AlarmKey;
import com.outofthecave.libralarm.room.AppDatabase;

import java.util.List;

import needle.Needle;

public class AlarmListViewModel extends AndroidViewModel {
    private final AlarmDao alarmDao;
    private final LiveData<List<Alarm>> alarms;

    public AlarmListViewModel(@NonNull Application application) {
        super(application);

        AppDatabase database = AppDatabase.getInstance(application);
        alarmDao = database.alarmDao();
        alarms = alarmDao.getAllLive();
    }

    public LiveData<List<Alarm>> getAlarms() {
        return alarms;
    }

    public void upsert(final Alarm alarm) {
        Needle.onBackgroundThread().execute(new Runnable() {
            @Override
            public void run() {
                alarmDao.upsert(alarm);
            }
        });
    }

    public void deleteById(int id) {
        final AlarmKey key = new AlarmKey();
        key.id = id;
        Needle.onBackgroundThread().execute(new Runnable() {
            @Override
            public void run() {
                alarmDao.deleteByKey(key);
            }
        });
    }

    public void updateAlarm(final Alarm alarm) {
        Needle.onBackgroundThread().execute(new Runnable() {
            @Override
            public void run() {
                alarmDao.updateAlarm(alarm);
            }
        });
    }
}
