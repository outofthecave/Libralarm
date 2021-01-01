package com.outofthecave.libralarm.room;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import com.outofthecave.libralarm.model.SnoozedAlarm;

import java.util.List;

@Dao
public interface SnoozedAlarmDao {
    @Query("SELECT * FROM SnoozedAlarm")
    List<SnoozedAlarm> getAll();

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void upsert(List<SnoozedAlarm> snoozedAlarms);

    @Delete(entity = SnoozedAlarm.class)
    void deleteByKey(List<AlarmKey> keys);
}
