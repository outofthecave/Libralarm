package com.outofthecave.libralarm.model;

import android.net.Uri;
import android.os.Parcel;
import android.os.Parcelable;

import java.util.EnumSet;
import java.util.Objects;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.room.Entity;

@Entity
public final class Alarm implements Parcelable {
    @NonNull
    public String name = "";

    public int year = 1970;
    public int month = 1;
    public int day = 1;
    public int hour = 0;
    public int minute = 0;

    @NonNull
    public RecurrenceType recurrenceType = RecurrenceType.NONE;
    /**
     * Number of days/weeks/months/years between two occurrences. The unit is defined by
     * {@link #recurrenceType}.
     */
    public int recurrenceStep = 1;
    /** Which weekdays the alarm should trigger on. */
    @NonNull
    public EnumSet<Weekday> recurrenceWeekdays = EnumSet.noneOf(Weekday.class);
    /**
     * Ordinal of the weekday in the month when the alarm should trigger. For example, {@code 3} for
     * "every third Sunday/Monday/etc. of the month". Negative numbers are allowed: {@code -1} means
     * the last weekday of the month, {@code -2} means the second to last weekday, etc. To be used
     * in combination with {@link #recurrenceWeekdays}.
     */
    public int recurrenceWeekdayOrdinal = 1;
    /**
     * Which day of the month to trigger the alarm on. Only used if {@link #recurrenceType} is
     * {@link RecurrenceType#MONTHLY} or {@link RecurrenceType#YEARLY}. This is different from
     * {@link #day} to allow for negative numbers: {@code -1} means the last day of the month,
     * {@code -2} means the second to last day, etc.
     */
    public int recurrenceDay = 1;

    @Nullable
    public Uri sound = null;
    public int soundVolume = 1;  // TODO set reasonable default volume
    public double soundVolumeAugmentationFactor = 1d;

    public long[] vibrationPattern = new long[0];  // TODO how does this work?

    public int lightArgb;  // TODO what's this?
    public int lightOnMs;
    public int lightOffMs;

    public NotificationType notificationType = NotificationType.FULLSCREEN;

    /**
     * Automatically snooze the alarm after it's been trying to get the user's attention without
     * success for this many minutes. (Getting the user's attention includes sounds, vibrations, and
     * lights.)
     *
     * If multiple auto-snooze conditions are set, the condition that is met first will snooze the
     * alarm and the other conditions will have no effect.
     */
    public int autoSnoozeAfterMinutes = 1;
    /**
     * Automatically snooze the alarm after the sound has been played this many times. {@code -1}
     * means unlimited.
     *
     * If multiple auto-snooze conditions are set, the condition that is met first will snooze the
     * alarm and the other conditions will have no effect.
     */
    public int autoSnoozeAfterSoundLoops = -1;
    /**
     * Automatically snooze the alarm after the vibration pattern has been repeated this many times.
     * {@code -1} means unlimited.
     *
     * If multiple auto-snooze conditions are set, the condition that is met first will snooze the
     * alarm and the other conditions will have no effect.
     */
    public int autoSnoozeAfterVibrationLoops = -1;
    /**
     * Automatically snooze the alarm after the light pattern has been repeated this many times.
     * {@code -1} means unlimited.
     *
     * If multiple auto-snooze conditions are set, the condition that is met first will snooze the
     * alarm and the other conditions will have no effect.
     */
    public int autoSnoozeAfterLightLoops = -1;

    /**
     * Maximum number of times the alarm will trigger if snoozed repeatedly. This includes the first
     * time when the alarm initially goes off before any snoozing. {@code -1} means unlimited.
     */
    public int maxRepetitions = 1;
    // TODO documentation
    public int repetitionIntervalMinutes = 1;
    // TODO or should we allow the user to set custom repetition instances?
    public double repetitionIntervalChangeFactor = 1d;

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
                && day == alarm.day
                && recurrenceType == alarm.recurrenceType
                && Objects.equals(year, alarm.year);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, day, recurrenceType, year);
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("Alarm{");
        sb.append("name=\"");
        sb.append(name);
        sb.append("\",");
        sb.append("day=");
        sb.append(day);
        sb.append(",");
        sb.append("month=");
        sb.append(recurrenceType);
        if (year != null) {
            sb.append(",");
            sb.append("year=");
            sb.append(year);
        }
        sb.append("}");
        return sb.toString();
    }

    public static final Creator<Alarm> CREATOR = new Creator<Alarm>() {
        @Override
        public Alarm createFromParcel(Parcel in) {
            Alarm alarm = new Alarm();
            alarm.name = in.readString();
            alarm.day = in.readInt();
            alarm.recurrenceType = in.readInt();
            boolean hasYear = in.readByte() != 0;
            int year = in.readInt();
            if (hasYear) {
                alarm.year = year;
            }
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
        out.writeInt(day);
        out.writeInt(recurrenceType);
        boolean hasYear = year != null;
        // Write a boolean (as a byte) to indicate whether the year is present.
        out.writeByte((byte) (hasYear ? 1 : 0));
        out.writeInt(hasYear ? year : 0);
    }
}
