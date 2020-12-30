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
    void upsert(SnoozedAlarm alarm);

    @Delete(entity = SnoozedAlarm.class)
    void deleteByKey(AlarmKey key);

    @Update(onConflict = OnConflictStrategy.IGNORE)
    void updateSnoozedAlarms(List<SnoozedAlarm> alarms);
}
