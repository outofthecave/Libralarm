package com.outofthecave.libralarm.model;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.Objects;

import androidx.annotation.Nullable;
import androidx.room.Entity;

public final class DateTime implements Parcelable {
    public int year = 1970;
    public int month = 1;
    public int day = 1;
    public int hour = 0;
    public int minute = 0;

    @Override
    public boolean equals(@Nullable Object that) {
        if (this == that) {
            return true;
        }

        if (that == null || getClass() != that.getClass()) {
            return false;
        }

        DateTime dateTime = (DateTime) that;
        return year == dateTime.year
                && month == dateTime.month
                && day == dateTime.day
                && hour == dateTime.hour
                && minute == dateTime.minute;
    }

    @Override
    public int hashCode() {
        return Objects.hash(year, month, day, hour, minute);
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("DateTime{");
        sb.append("year=");
        sb.append(year);
        sb.append(",");
        sb.append("month=");
        sb.append(month);
        sb.append(",");
        sb.append("day=");
        sb.append(day);
        sb.append(",");
        sb.append("hour=");
        sb.append(hour);
        sb.append(",");
        sb.append("minute=");
        sb.append(minute);
        sb.append("}");
        return sb.toString();
    }

    public static final Creator<DateTime> CREATOR = new Creator<DateTime>() {
        @Override
        public DateTime createFromParcel(Parcel in) {
            DateTime dateTime = new DateTime();
            dateTime.year = in.readInt();
            dateTime.month = in.readInt();
            dateTime.day = in.readInt();
            dateTime.hour = in.readInt();
            dateTime.minute = in.readInt();
            return dateTime;
        }

        @Override
        public DateTime[] newArray(int size) {
            return new DateTime[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel out, int flags) {
        out.writeInt(year);
        out.writeInt(month);
        out.writeInt(day);
        out.writeInt(hour);
        out.writeInt(minute);
    }
}
