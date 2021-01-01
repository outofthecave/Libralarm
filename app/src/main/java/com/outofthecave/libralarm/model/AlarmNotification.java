package com.outofthecave.libralarm.model;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.ArrayList;
import java.util.Objects;

public final class AlarmNotification {
    @NonNull
    public ArrayList<Alarm> alarms = new ArrayList<>(1);

    @NonNull
    public DateTime dateTime = new DateTime();

    @Override
    public boolean equals(@Nullable Object that) {
        if (this == that) {
            return true;
        }

        if (that == null || getClass() != that.getClass()) {
            return false;
        }

        AlarmNotification alarmNotification = (AlarmNotification) that;
        return Objects.equals(alarms, alarmNotification.alarms)
                && Objects.equals(dateTime, alarmNotification.dateTime);
    }

    @Override
    public int hashCode() {
        return Objects.hash(alarms, dateTime);
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("AlarmNotification{");
        sb.append("alarms=");
        sb.append(alarms);
        sb.append(",");
        sb.append("dateTime=");
        sb.append(dateTime);
        sb.append("}");
        return sb.toString();
    }
}
