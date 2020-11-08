package com.outofthecave.libralarm.model;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.Objects;

import androidx.annotation.Nullable;

/**
 * Auto-snooze settings for an alarm.
 *
 * If multiple auto-snooze conditions are set, the condition that is met first will snooze the
 * alarm and the other conditions will have no effect.
 */
public final class AutoSnooze implements Parcelable {
    /**
     * Automatically snooze the alarm after it's been trying to get the user's attention without
     * success for this many minutes. (Getting the user's attention includes any audio-visual.)
     */
    public int afterMinutes = 1;
    /**
     * Automatically snooze the alarm after the sound has been played this many times. {@code -1}
     * means unlimited.
     */
    public int afterSoundLoops = -1;
    /**
     * Automatically snooze the alarm after the vibration pattern has been repeated this many times.
     * {@code -1} means unlimited.
     */
    public int afterVibrationLoops = -1;
    /**
     * Automatically snooze the alarm after the light pattern has been repeated this many times.
     * {@code -1} means unlimited.
     */
    public int afterLightLoops = -1;

    @Override
    public boolean equals(@Nullable Object that) {
        if (this == that) {
            return true;
        }

        if (that == null || getClass() != that.getClass()) {
            return false;
        }

        AutoSnooze autoSnooze = (AutoSnooze) that;
        return afterMinutes == autoSnooze.afterMinutes
                && afterSoundLoops == autoSnooze.afterSoundLoops
                && afterVibrationLoops == autoSnooze.afterVibrationLoops
                && afterLightLoops == autoSnooze.afterLightLoops;
    }

    @Override
    public int hashCode() {
        return Objects.hash(afterMinutes, afterSoundLoops, afterVibrationLoops, afterLightLoops);
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("AutoSnooze{");
        sb.append("afterMinutes=");
        sb.append(afterMinutes);
        sb.append(",");
        sb.append("afterSoundLoops=");
        sb.append(afterSoundLoops);
        sb.append(",");
        sb.append("afterVibrationLoops=");
        sb.append(afterVibrationLoops);
        sb.append(",");
        sb.append("afterLightLoops=");
        sb.append(afterLightLoops);
        sb.append("}");
        return sb.toString();
    }

    public static final Creator<AutoSnooze> CREATOR = new Creator<AutoSnooze>() {
        @Override
        public AutoSnooze createFromParcel(Parcel in) {
            AutoSnooze autoSnooze = new AutoSnooze();
            autoSnooze.afterMinutes = in.readInt();
            autoSnooze.afterSoundLoops = in.readInt();
            autoSnooze.afterVibrationLoops = in.readInt();
            autoSnooze.afterLightLoops = in.readInt();
            return autoSnooze;
        }

        @Override
        public AutoSnooze[] newArray(int size) {
            return new AutoSnooze[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel out, int flags) {
        out.writeInt(afterMinutes);
        out.writeInt(afterSoundLoops);
        out.writeInt(afterVibrationLoops);
        out.writeInt(afterLightLoops);
    }
}
