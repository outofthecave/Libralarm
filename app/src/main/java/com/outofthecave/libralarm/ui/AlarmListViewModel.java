package com.outofthecave.libralarm.ui;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.outofthecave.libralarm.model.Alarm;
import com.outofthecave.libralarm.room.AlarmDao;
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

    public void add(final Alarm alarm) {
        Needle.onBackgroundThread().execute(new Runnable() {
            @Override
            public void run() {
                alarmDao.add(alarm);
            }
        });
    }

    public void delete(final Alarm alarm) {
        Needle.onBackgroundThread().execute(new Runnable() {
            @Override
            public void run() {
                alarmDao.delete(alarm);
            }
        });
    }

    /**
     * Replace one alarm with another. This is different from calling {@link #delete(Alarm)}
     * and {@link #add(Alarm)} in that it guarantees that the deletion is executed before the
     * addition.
     *
     * @param alarmToReplace The old alarm to delete.
     * @param alarmToAdd The new alarm to add.
     */
    public void replace(@Nullable final Alarm alarmToReplace, @Nullable final Alarm alarmToAdd) {
        Needle.onBackgroundThread().execute(new Runnable() {
            @Override
            public void run() {
                if (alarmToReplace != null) {
                    alarmDao.delete(alarmToReplace);
                }
                if (alarmToAdd != null) {
                    alarmDao.add(alarmToAdd);
                }
            }
        });
    }
}
