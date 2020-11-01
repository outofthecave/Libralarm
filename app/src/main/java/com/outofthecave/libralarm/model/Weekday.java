package com.outofthecave.libralarm.model;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.HashMap;
import java.util.Map;

import androidx.annotation.Nullable;

public enum Weekday implements Parcelable {
    SUNDAY(0),
    MONDAY(1),
    TUESDAY(2),
    WEDNESDAY(3),
    THURSDAY(4),
    FRIDAY(5),
    SATURDAY(6),
    ;

    private static final Map<Integer, Weekday> VALUE_TO_INSTANCE = new HashMap<>();

    static {
        for (Weekday weekday : Weekday.values()) {
            VALUE_TO_INSTANCE.put(weekday.value, weekday);
        }
    }

    private final int value;

    Weekday(int value) {
        this.value = value;
    }

    public static final Creator<Weekday> CREATOR = new Creator<Weekday>() {
        @Nullable
        @Override
        public Weekday createFromParcel(Parcel in) {
            int value = in.readInt();
            return Weekday.VALUE_TO_INSTANCE.get(value);
        }

        @Override
        public Weekday[] newArray(int size) {
            return new Weekday[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel out, int flags) {
        out.writeInt(value);
    }
}
