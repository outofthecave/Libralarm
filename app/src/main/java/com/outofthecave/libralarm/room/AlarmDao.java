package com.outofthecave.libralarm.room;

import com.outofthecave.libralarm.model.Alarm;

import java.util.List;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

@Dao
public interface AlarmDao {
    @Query("SELECT * FROM Alarm")
    LiveData<List<Alarm>> getAllLive();

    @Query("SELECT * FROM Alarm")
    List<Alarm> getAll();

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void upsert(Alarm alarm);

    @Delete(entity = Alarm.class)
    void deleteByKey(AlarmKey key);

    @Update
    void updateAlarm(Alarm alarm);
}
