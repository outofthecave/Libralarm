package com.outofthecave.libralarm.model;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.Nullable;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.PrimaryKey;

import java.util.Objects;

@Entity
public final class SnoozedAlarm implements Parcelable {
    @ForeignKey(entity = Alarm.class, parentColumns = {"id"}, childColumns = {"id"})
    @PrimaryKey
    public int id;

    /**
     * Number of times the alarm has been snoozed already.
     */
    // TODO Move this field to the Alarm class and delete the SnoozedAlarm class. We don't want to
    // rely on the presence/absence of an entry in a database to track the state of alarms because
    // we can't guarantee that they'll be deleted at the right time. Instead, we should also add a
    // field to Alarm to keep track of the last timestamp when the alarm triggered.
    public int snoozeCount = 0;

    /**
     * @deprecated For use by generated Room code only.
     */
    @Deprecated
    public SnoozedAlarm() {
    }

    public SnoozedAlarm(int id) {
        this.id = id;
    }

    @Override
    public boolean equals(@Nullable Object that) {
        if (this == that) {
            return true;
        }

        if (that == null || getClass() != that.getClass()) {
            return false;
        }

        SnoozedAlarm alarm = (SnoozedAlarm) that;
        return id == alarm.id
                && snoozeCount == alarm.snoozeCount;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, snoozeCount);
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("SnoozedAlarm{");
        sb.append("id=");
        sb.append(id);
        sb.append(",");
        sb.append("snoozeCount=");
        sb.append(snoozeCount);
        sb.append("}");
        return sb.toString();
    }

    public static final Creator<SnoozedAlarm> CREATOR = new Creator<SnoozedAlarm>() {
        @Override
        public SnoozedAlarm createFromParcel(Parcel in) {
            SnoozedAlarm alarm = new SnoozedAlarm();
            alarm.id = in.readInt();
            alarm.snoozeCount = in.readInt();
            return alarm;
        }

        @Override
        public SnoozedAlarm[] newArray(int size) {
            return new SnoozedAlarm[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel out, int flags) {
        out.writeInt(id);
        out.writeInt(snoozeCount);
    }
}
