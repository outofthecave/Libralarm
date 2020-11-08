package com.outofthecave.libralarm.model;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.EnumSet;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

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

    private static final Map<Integer, Weekday> VALUE_TO_INSTANCE = getValueToInstance();

    private static Map<Integer, Weekday> getValueToInstance() {
        Map<Integer, Weekday> valueToInstance = new HashMap<>();
        for (Weekday weekday : Weekday.values()) {
            valueToInstance.put(weekday.value, weekday);
        }
        return valueToInstance;
    }

    private static final int MAX_VALUE = getMaxValue();

    private static int getMaxValue() {
        int maxValue = 0;
        for (Weekday weekday : Weekday.values()) {
            if (weekday.value > maxValue) {
                maxValue = weekday.value;
            }
        }
        return maxValue;
    }

    private final int value;

    Weekday(int value) {
        this.value = value;
    }

    @Override
    public String toString() {
        return name();
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

    public static int weekdaySetToInt(Set<Weekday> set) {
        int result = 0;
        for (Weekday weekday : set) {
            result += Math.round(Math.pow(2, weekday.value));
        }
        return result;
    }

    public static EnumSet<Weekday> intToWeekdaySet(int i) {
        EnumSet<Weekday> result = EnumSet.noneOf(Weekday.class);
        for (int value = Weekday.MAX_VALUE; value >= 0; --value) {
            long power = Math.round(Math.pow(2, value));
            if (power < i) {
                Weekday weekday = Weekday.VALUE_TO_INSTANCE.get(value);
                if (weekday != null) {
                    result.add(weekday);
                }
                i -= power;
            }
        }
        return result;
    }
}
