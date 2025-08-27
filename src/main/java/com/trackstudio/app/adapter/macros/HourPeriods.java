package com.trackstudio.app.adapter.macros;

import java.util.Calendar;

import net.jcip.annotations.Immutable;

@Immutable
public class HourPeriods implements Periods {
    public Calendar reset(Calendar a) {
        Calendar one = (Calendar) a.clone();
        one.set(Calendar.MINUTE, 0);
        one.set(Calendar.SECOND, 1);
        one.set(Calendar.MILLISECOND, 0);
        return one;
    }

    public String getPattern() {
        return "hh:00 dd.MM.yy";
    }

    public int getShift() {
        return Calendar.HOUR;
    }

    public int since(Calendar a, Calendar b) {
        Calendar one = reset(a);
        Calendar two = reset(b);
        long l = one.getTimeInMillis() - two.getTimeInMillis();

        return (int) (l / 3600000L);
    }

}
