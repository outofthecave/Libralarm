package com.outofthecave.libralarm.logic;

import android.content.Context;

import com.outofthecave.libralarm.R;
import com.outofthecave.libralarm.model.Alarm;
import com.outofthecave.libralarm.model.SnoozedAlarm;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.runners.MockitoJUnitRunner;
import org.mockito.stubbing.Answer;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class AlarmNameFormatterTest {
    @Mock
    Context mockContext;

    @Test
    public void testJoinAlarmNamesOnNewline() {
        Alarm alarmFoo = new Alarm();
        alarmFoo.id = 0;
        alarmFoo.name = "Foo";

        Alarm alarmBar = new Alarm();
        alarmBar.id = 1;
        alarmBar.name = "Bar";
        SnoozedAlarm snoozedAlarmBar = new SnoozedAlarm(alarmBar.id);
        snoozedAlarmBar.snoozeCount = 1;

        Alarm alarmQux = new Alarm();
        alarmQux.id = 2;
        alarmQux.name = "Qux";
        SnoozedAlarm snoozedAlarmQux = new SnoozedAlarm(alarmQux.id);
        snoozedAlarmQux.snoozeCount = 2;

        List<Alarm> alarms = Arrays.asList(alarmFoo, alarmBar, alarmQux);

        Map<Integer, SnoozedAlarm> idToSnoozedAlarm = new HashMap<>();
        idToSnoozedAlarm.put(alarmBar.id, snoozedAlarmBar);
        idToSnoozedAlarm.put(alarmQux.id, snoozedAlarmQux);

        when(mockContext.getString(R.string.alarm_name_suffix_snoozed_once))
                .thenReturn(" (snoozed once)");

        when(mockContext.getString(eq(R.string.alarm_name_suffix_snoozed_multiple_times), any()))
                .thenAnswer(new Answer<String>() {
                    @Override
                    public String answer(InvocationOnMock invocation) throws Throwable {
                        return String.format(" (snoozed %1$d times)", invocation.getArgumentAt(1, Integer.class));
                    }
                });

        String text = AlarmNameFormatter.joinAlarmNamesOnNewline(mockContext, alarms, idToSnoozedAlarm);

        String expectedText = "Foo\nBar (snoozed once)\nQux (snoozed 2 times)";

        assertEquals(expectedText, text);
    }
}