package com.outofthecave.libralarm.logic;

import androidx.annotation.NonNull;

import com.outofthecave.libralarm.model.Alarm;

import java.util.List;

public class AlarmNameFormatter {
    private AlarmNameFormatter() {
    }

    public static String joinAlarmNamesOnNewline(@NonNull List<Alarm> alarms) {
        StringBuilder text = new StringBuilder();
        boolean isFirst = true;
        for (Alarm alarm : alarms) {
            if (!alarm.name.isEmpty()) {
                if (!isFirst) {
                    text.append("\n");
                }
                text.append(alarm.name);
                isFirst = false;
            }
        }
        return text.toString();
    }
}
