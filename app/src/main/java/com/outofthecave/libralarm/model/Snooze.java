package com.outofthecave.libralarm.model;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.Objects;

import androidx.annotation.Nullable;

public final class Snooze implements Parcelable {
    /**
     * Maximum number of times the alarm can be snoozed. This does NOT include the first time when
     * the alarm initially goes off before any snoozing. {@code -1} means unlimited.
     */
    public int maximum = 0;
    /**
     * How long before the alarm goes off again after going off and being snoozed. (For example, if
     * the alarm first went off at 8:00 am and this is set to 5, it will go off again at 8:05 am,
     * even if it was only snoozed at 8:02 am.)
     */
    public int intervalMinutes = 5;
    /**
     * Factor that determines how the interval changes each time the alarm is snoozed. (For example,
     * if this is 0.5 and {@link #intervalMinutes} is 10, the actual intervals would be 10, 5, 2, 1,
     * etc. (The result of applying this factor is converted to an integer and cannot be less than 1
     * minute.) The alarm would therefore go off at 8:00 am, 8:10, 8:15, 8:17, 8:18, etc.)
     */
    public double intervalChangeFactor = 1d;

    @Override
    public boolean equals(@Nullable Object that) {
        if (this == that) {
            return true;
        }

        if (that == null || getClass() != that.getClass()) {
            return false;
        }

        Snooze snooze = (Snooze) that;
        return maximum == snooze.maximum
                && intervalMinutes == snooze.intervalMinutes
                && intervalChangeFactor == snooze.intervalChangeFactor;
    }

    @Override
    public int hashCode() {
        return Objects.hash(maximum, intervalMinutes, intervalChangeFactor);
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("Snooze{");
        sb.append("maximum=");
        sb.append(maximum);
        sb.append(",");
        sb.append("intervalMinutes=");
        sb.append(intervalMinutes);
        sb.append(",");
        sb.append("intervalChangeFactor=");
        sb.append(intervalChangeFactor);
        sb.append("}");
        return sb.toString();
    }

    public static final Creator<Snooze> CREATOR = new Creator<Snooze>() {
        @Override
        public Snooze createFromParcel(Parcel in) {
            Snooze snooze = new Snooze();
            snooze.maximum = in.readInt();
            snooze.intervalMinutes = in.readInt();
            snooze.intervalChangeFactor = in.readDouble();
            return snooze;
        }

        @Override
        public Snooze[] newArray(int size) {
            return new Snooze[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel out, int flags) {
        out.writeInt(maximum);
        out.writeInt(intervalMinutes);
        out.writeDouble(intervalChangeFactor);
    }
}
