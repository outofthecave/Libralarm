package com.outofthecave.libralarm.model;

import android.os.Build;
import android.os.Parcel;
import android.os.Parcelable;

import java.util.Objects;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.room.Embedded;
import androidx.room.Entity;
import androidx.room.PrimaryKey;
import androidx.room.TypeConverters;

@Entity
public final class Alarm implements Parcelable {
    @PrimaryKey(autoGenerate = true)
    public int id;

    @NonNull
    public String name = "";

    public boolean enabled = true;

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
        return id == alarm.id
                && Objects.equals(name, alarm.name)
                && enabled == alarm.enabled
                && Objects.equals(dateTime, alarm.dateTime)
                && Objects.equals(recurrence, alarm.recurrence)
                && Objects.equals(audioVisual, alarm.audioVisual)
                && Objects.equals(notificationType, alarm.notificationType)
                && Objects.equals(autoSnooze, alarm.autoSnooze)
                && Objects.equals(snooze, alarm.snooze);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name, enabled, dateTime, recurrence, audioVisual, notificationType, autoSnooze, snooze);
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("Alarm{");
        sb.append("id=");
        sb.append(id);
        sb.append(",");
        sb.append("enabled=");
        sb.append(enabled);
        sb.append(",");
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
            alarm.id = in.readInt();
            alarm.name = in.readString();
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                alarm.enabled = in.readBoolean();
            } else {
                alarm.enabled = in.readByte() == (byte) 1;
            }
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
        out.writeInt(id);
        out.writeString(name);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            out.writeBoolean(enabled);
        } else {
            out.writeByte(enabled ? (byte) 1 : (byte) 0);
        }
        out.writeParcelable(dateTime, flags);
        out.writeParcelable(recurrence, flags);
        out.writeParcelable(audioVisual, flags);
        out.writeParcelable(notificationType, flags);
        out.writeParcelable(autoSnooze, flags);
        out.writeParcelable(snooze, flags);
    }
}
