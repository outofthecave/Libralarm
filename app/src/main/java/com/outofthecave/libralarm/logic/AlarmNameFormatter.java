package com.outofthecave.libralarm.logic;

import android.content.Context;

import androidx.annotation.NonNull;

import com.outofthecave.libralarm.R;
import com.outofthecave.libralarm.model.Alarm;
import com.outofthecave.libralarm.model.SnoozedAlarm;

import java.util.List;
import java.util.Map;

public class AlarmNameFormatter {
    private AlarmNameFormatter() {
    }

    public static String joinAlarmNamesOnNewline(Context context, @NonNull List<Alarm> alarms, Map<Integer, SnoozedAlarm> idToSnoozedAlarm) {
        StringBuilder text = new StringBuilder();
        boolean isFirst = true;
        for (Alarm alarm : alarms) {
            if (!alarm.name.isEmpty()) {
                if (!isFirst) {
                    text.append("\n");
                }

                text.append(alarm.name);

                SnoozedAlarm snoozedAlarm = idToSnoozedAlarm.get(alarm.id);
                if (snoozedAlarm != null) {
                    if (snoozedAlarm.snoozeCount == 1) {
                        text.append(context.getString(R.string.alarm_name_suffix_snoozed_once));
                    } else if (snoozedAlarm.snoozeCount > 1) {
                        text.append(context.getString(R.string.alarm_name_suffix_snoozed_multiple_times, snoozedAlarm.snoozeCount));
                    }
                }

                isFirst = false;
            }
        }

        return text.toString();
    }
}
