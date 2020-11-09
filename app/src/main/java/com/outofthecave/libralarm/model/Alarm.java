package com.outofthecave.libralarm.model;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.Objects;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.room.Embedded;
import androidx.room.Entity;
import androidx.room.TypeConverters;

@Entity(primaryKeys = {"name", "dateTime_year", "dateTime_month", "dateTime_day", "dateTime_hour", "dateTime_minute"})
public final class Alarm implements Parcelable {
    @NonNull
    public String name = "";

    @Embedded(prefix = "dateTime_")
    @NonNull
    public DateTime dateTime = new DateTime();

    @Embedded(prefix = "recurrence_")
    @NonNull
    public Recurrence recurrence = new Recurrence();

    @Embedded(prefix = "audioVisual_")
    @NonNull
    public AudioVisual audioVisual = new AudioVisual();

    @TypeConverters(NotificationType.Converter.class)
    @NonNull
    public NotificationType notificationType = NotificationType.FULLSCREEN;

    @Embedded(prefix = "autoSnooze_")
    @NonNull
    public AutoSnooze autoSnooze = new AutoSnooze();

    @Embedded(prefix = "snooze_")
    @NonNull
    public Snooze snooze = new Snooze();

    @Override
    public boolean equals(@Nullable Object that) {
        if (this == that) {
            return true;
        }

        if (that == null || getClass() != that.getClass()) {
            return false;
        }

        Alarm alarm = (Alarm) that;
        return Objects.equals(name, alarm.name)
                && Objects.equals(dateTime, alarm.dateTime)
                && Objects.equals(recurrence, alarm.recurrence)
                && Objects.equals(audioVisual, alarm.audioVisual)
                && Objects.equals(notificationType, alarm.notificationType)
                && Objects.equals(autoSnooze, alarm.autoSnooze)
                && Objects.equals(snooze, alarm.snooze);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, dateTime, recurrence, audioVisual, notificationType, autoSnooze, snooze);
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("Alarm{");
        sb.append("name=\"");
        sb.append(name);
        sb.append("\",");
        sb.append("dateTime=");
        sb.append(dateTime);
        sb.append(",");
        sb.append("recurrence=");
        sb.append(recurrence);
        sb.append(",");
        sb.append("audioVisual=");
        sb.append(audioVisual);
        sb.append(",");
        sb.append("notificationType=");
        sb.append(notificationType);
        sb.append(",");
        sb.append("autoSnooze=");
        sb.append(autoSnooze);
        sb.append(",");
        sb.append("snooze=");
        sb.append(snooze);
        sb.append("}");
        return sb.toString();
    }

    public static final Creator<Alarm> CREATOR = new Creator<Alarm>() {
        @Override
        public Alarm createFromParcel(Parcel in) {
            Alarm alarm = new Alarm();
            alarm.name = in.readString();
            ClassLoader classLoader = getClass().getClassLoader();
            alarm.dateTime = in.readParcelable(classLoader);
            alarm.recurrence = in.readParcelable(classLoader);
            alarm.audioVisual = in.readParcelable(classLoader);
            alarm.notificationType = in.readParcelable(classLoader);
            alarm.autoSnooze = in.readParcelable(classLoader);
            alarm.snooze = in.readParcelable(classLoader);
            return alarm;
        }

        @Override
        public Alarm[] newArray(int size) {
            return new Alarm[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel out, int flags) {
        out.writeString(name);
        out.writeParcelable(dateTime, flags);
        out.writeParcelable(recurrence, flags);
        out.writeParcelable(audioVisual, flags);
        out.writeParcelable(notificationType, flags);
        out.writeParcelable(autoSnooze, flags);
        out.writeParcelable(snooze, flags);
    }
}
