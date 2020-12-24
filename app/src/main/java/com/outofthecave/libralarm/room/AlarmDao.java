package com.outofthecave.libralarm.room;

import com.outofthecave.libralarm.model.Alarm;

import java.util.List;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

@Dao
public interface AlarmDao {
    @Query("SELECT * FROM Alarm")
    LiveData<List<Alarm>> getAllLive();

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void upsert(Alarm alarm);

    @Delete(entity = Alarm.class)
    void deleteByKey(AlarmKey key);
}
