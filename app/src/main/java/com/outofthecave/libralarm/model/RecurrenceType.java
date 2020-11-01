package com.outofthecave.libralarm.model;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.HashMap;
import java.util.Map;

import androidx.annotation.NonNull;

public enum RecurrenceType implements Parcelable {
    NONE(0),
    DAILY(1),
    WEEKLY(2),
    MONTHLY(3),
    YEARLY(4),
    ;

    private static final Map<Integer, RecurrenceType> VALUE_TO_INSTANCE = new HashMap<>();

    static {
        for (RecurrenceType recurrenceType : RecurrenceType.values()) {
            VALUE_TO_INSTANCE.put(recurrenceType.value, recurrenceType);
        }
    }

    private final int value;

    RecurrenceType(int value) {
        this.value = value;
    }

    public static final Creator<RecurrenceType> CREATOR = new Creator<RecurrenceType>() {
        @NonNull
        @Override
        public RecurrenceType createFromParcel(Parcel in) {
            int value = in.readInt();
            RecurrenceType recurrenceType = RecurrenceType.VALUE_TO_INSTANCE.get(value);
            if (recurrenceType == null) {
                recurrenceType = RecurrenceType.NONE;
            }
            return recurrenceType;
        }

        @Override
        public RecurrenceType[] newArray(int size) {
            return new RecurrenceType[size];
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
