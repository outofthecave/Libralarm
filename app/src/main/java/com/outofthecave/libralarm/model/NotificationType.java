package com.outofthecave.libralarm.model;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.HashMap;
import java.util.Map;

import androidx.annotation.NonNull;
import androidx.room.TypeConverter;

public enum NotificationType implements Parcelable {
    FULLSCREEN(0),
    HEADS_UP(1),
    NOTIFICATION(2),
    ;

    private static final Map<Integer, NotificationType> VALUE_TO_INSTANCE = new HashMap<>();

    static {
        for (NotificationType notificationType : NotificationType.values()) {
            VALUE_TO_INSTANCE.put(notificationType.value, notificationType);
        }
    }

    private final int value;

    NotificationType(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }

    @Override
    public String toString() {
        return name();
    }

    @NonNull
    public static NotificationType fromValue(int value) {
        NotificationType notificationType = NotificationType.VALUE_TO_INSTANCE.get(value);
        if (notificationType == null) {
            notificationType = NotificationType.FULLSCREEN;
        }
        return notificationType;
    }

    public static final class Converter {
        @TypeConverter
        public static int toInt(NotificationType notificationType) {
            return notificationType.value;
        }

        @NonNull
        @TypeConverter
        public static NotificationType toNotificationType(int value) {
            return NotificationType.fromValue(value);
        }
    }

    public static final Creator<NotificationType> CREATOR = new Creator<NotificationType>() {
        @NonNull
        @Override
        public NotificationType createFromParcel(Parcel in) {
            int value = in.readInt();
            return NotificationType.fromValue(value);
        }

        @Override
        public NotificationType[] newArray(int size) {
            return new NotificationType[size];
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
