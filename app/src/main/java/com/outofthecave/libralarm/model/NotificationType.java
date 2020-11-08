package com.outofthecave.libralarm.model;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.HashMap;
import java.util.Map;

import androidx.annotation.NonNull;

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

    @Override
    public String toString() {
        return name();
    }

    public static final Creator<NotificationType> CREATOR = new Creator<NotificationType>() {
        @NonNull
        @Override
        public NotificationType createFromParcel(Parcel in) {
            int value = in.readInt();
            NotificationType notificationType = NotificationType.VALUE_TO_INSTANCE.get(value);
            if (notificationType == null) {
                notificationType = NotificationType.FULLSCREEN;
            }
            return notificationType;
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
