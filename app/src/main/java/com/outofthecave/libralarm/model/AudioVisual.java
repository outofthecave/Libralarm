package com.outofthecave.libralarm.model;

import android.net.Uri;
import android.os.Parcel;
import android.os.Parcelable;

import java.util.Arrays;
import java.util.Objects;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.room.TypeConverters;

public final class AudioVisual implements Parcelable {
    @TypeConverters(UriConverter.class)
    @Nullable
    public Uri sound = null;
    public int soundVolume = 1;  // TODO set reasonable default volume
    public double soundVolumeAugmentationFactor = 1d;

    @TypeConverters(LongArrayConverter.class)
    @NonNull
    public long[] vibrationPattern = new long[0];  // TODO how does this work?

    public int lightArgb;  // TODO what's this?
    public int lightOnMs;
    public int lightOffMs;

    @Override
    public boolean equals(@Nullable Object that) {
        if (this == that) {
            return true;
        }

        if (that == null || getClass() != that.getClass()) {
            return false;
        }

        AudioVisual audioVisual = (AudioVisual) that;
        return Objects.equals(sound, audioVisual.sound)
                && soundVolume == audioVisual.soundVolume
                && soundVolumeAugmentationFactor == audioVisual.soundVolumeAugmentationFactor
                && Arrays.equals(vibrationPattern, audioVisual.vibrationPattern)
                && lightArgb == audioVisual.lightArgb
                && lightOnMs == audioVisual.lightOnMs
                && lightOffMs == audioVisual.lightOffMs;
    }

    @Override
    public int hashCode() {
        return Objects.hash(sound, soundVolume, soundVolumeAugmentationFactor,
                vibrationPattern,
                lightArgb, lightOnMs, lightOffMs);
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("AudioVisual{");
        sb.append("sound=");
        sb.append(sound);
        sb.append(",");
        sb.append("soundVolume=");
        sb.append(soundVolume);
        sb.append(",");
        sb.append("soundVolumeAugmentationFactor=");
        sb.append(soundVolumeAugmentationFactor);
        sb.append(",");
        sb.append("vibrationPattern=");
        sb.append(Arrays.toString(vibrationPattern));
        sb.append(",");
        sb.append("lightArgb=");
        sb.append(lightArgb);
        sb.append(",");
        sb.append("lightOnMs=");
        sb.append(lightOnMs);
        sb.append(",");
        sb.append("lightOffMs=");
        sb.append(lightOffMs);
        sb.append("}");
        return sb.toString();
    }

    public static final Creator<AudioVisual> CREATOR = new Creator<AudioVisual>() {
        @Override
        public AudioVisual createFromParcel(Parcel in) {
            AudioVisual audioVisual = new AudioVisual();
            audioVisual.sound = in.readParcelable(getClass().getClassLoader());
            audioVisual.soundVolume = in.readInt();
            audioVisual.soundVolumeAugmentationFactor = in.readDouble();

            int length = in.readInt();
            audioVisual.vibrationPattern = new long[length];
            in.readLongArray(audioVisual.vibrationPattern);

            audioVisual.lightArgb = in.readInt();
            audioVisual.lightOnMs = in.readInt();
            audioVisual.lightOffMs = in.readInt();
            return audioVisual;
        }

        @Override
        public AudioVisual[] newArray(int size) {
            return new AudioVisual[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel out, int flags) {
        out.writeParcelable(sound, flags);
        out.writeInt(soundVolume);
        out.writeDouble(soundVolumeAugmentationFactor);

        out.writeInt(vibrationPattern.length);
        out.writeLongArray(vibrationPattern);

        out.writeInt(lightArgb);
        out.writeInt(lightOnMs);
        out.writeInt(lightOffMs);
    }
}
