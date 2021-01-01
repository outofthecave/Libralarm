package com.outofthecave.libralarm.room;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.outofthecave.libralarm.model.Alarm;
import com.outofthecave.libralarm.model.SnoozedAlarm;

import java.util.Collections;
import java.util.List;
import java.util.Objects;

public final class AlarmData {
    @NonNull
    public List<Alarm> alarms = Collections.emptyList();

    @NonNull
    public List<SnoozedAlarm> snoozedAlarms = Collections.emptyList();

    @Override
    public boolean equals(@Nullable Object that) {
        if (this == that) {
            return true;
        }

        if (that == null || getClass() != that.getClass()) {
            return false;
        }

        AlarmData alarmData = (AlarmData) that;
        return Objects.equals(alarms, alarmData.alarms)
                && Objects.equals(snoozedAlarms, alarmData.snoozedAlarms);
    }

    @Override
    public int hashCode() {
        return Objects.hash(alarms, snoozedAlarms);
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("AlarmData{");
        sb.append("alarms=");
        sb.append(alarms);
        sb.append(",");
        sb.append("snoozedAlarms=");
        sb.append(snoozedAlarms);
        sb.append("}");
        return sb.toString();
    }
}
