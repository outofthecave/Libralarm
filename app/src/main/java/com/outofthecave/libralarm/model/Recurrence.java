package com.outofthecave.libralarm.model;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.EnumSet;
import java.util.Objects;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.room.TypeConverters;

public final class Recurrence implements Parcelable {
    @TypeConverters(RecurrenceType.Converter.class)
    @NonNull
    public RecurrenceType type = RecurrenceType.NONE;

    /**
     * Number of days/weeks/months/years between two occurrences. The unit is defined by
     * {@link #type}.
     */
    public int step = 1;

    /** Which weekdays the alarm should trigger on. */
    @TypeConverters(Weekday.SetConverter.class)
    @NonNull
    public EnumSet<Weekday> weekdays = EnumSet.noneOf(Weekday.class);

    /**
     * Ordinal of the weekday in the month when the alarm should trigger. For example, {@code 3} for
     * "every third Sunday/Monday/etc. of the month". Negative numbers are allowed: {@code -1} means
     * the last weekday of the month, {@code -2} means the second to last weekday, etc. To be used
     * in combination with {@link #weekdays}.
     */
    public int weekdayOrdinal = 1;

    /**
     * Which day of the month to trigger the alarm on. Only used if {@link #type} is
     * {@link RecurrenceType#MONTHLY} or {@link RecurrenceType#YEARLY}. This is different from
     * {@link DateTime#day} to allow for negative numbers: {@code -1} means the last day of the
     * month, {@code -2} means the second to last day, etc.
     */
    public int day = 1;

    @Override
    public boolean equals(@Nullable Object that) {
        if (this == that) {
            return true;
        }

        if (that == null || getClass() != that.getClass()) {
            return false;
        }

        Recurrence recurrence = (Recurrence) that;
        return Objects.equals(type, recurrence.type)
                && step == recurrence.step
                && Objects.equals(weekdays, recurrence.weekdays)
                && weekdayOrdinal == recurrence.weekdayOrdinal
                && day == recurrence.day;
    }

    @Override
    public int hashCode() {
        return Objects.hash(type, step, weekdays, weekdayOrdinal, day);
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("Recurrence{");
        sb.append("type=");
        sb.append(type);
        sb.append(",");
        sb.append("step=");
        sb.append(step);
        sb.append(",");
        sb.append("weekdays=");
        sb.append(weekdays);
        sb.append(",");
        sb.append("weekdayOrdinal=");
        sb.append(weekdayOrdinal);
        sb.append(",");
        sb.append("day=");
        sb.append(day);
        sb.append("}");
        return sb.toString();
    }

    public static final Creator<Recurrence> CREATOR = new Creator<Recurrence>() {
        @Override
        public Recurrence createFromParcel(Parcel in) {
            Recurrence recurrence = new Recurrence();
            recurrence.type = in.readParcelable(getClass().getClassLoader());
            recurrence.step = in.readInt();
            recurrence.weekdays = Weekday.SetConverter.toWeekdaySet(in.readInt());
            recurrence.weekdayOrdinal = in.readInt();
            recurrence.day = in.readInt();
            return recurrence;
        }

        @Override
        public Recurrence[] newArray(int size) {
            return new Recurrence[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel out, int flags) {
        out.writeParcelable(type, flags);
        out.writeInt(step);
        out.writeInt(Weekday.SetConverter.toInt(weekdays));
        out.writeInt(weekdayOrdinal);
        out.writeInt(day);
    }
}
