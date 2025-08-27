package com.trackstudio.app.adapter.macros;

import java.util.Calendar;

import net.jcip.annotations.Immutable;

@Immutable
public class DayPeriods implements Periods {
    public String getPattern() {
        return "dd.MM.yy";
    }

    public Calendar reset(Calendar a) {
        Calendar one = (Calendar) a.clone();
        one.set(Calendar.HOUR_OF_DAY, 0);
        one.set(Calendar.MINUTE, 0);
        one.set(Calendar.SECOND, 1);
        one.set(Calendar.MILLISECOND, 0);

        return one;
    }

    public int getShift() {
        return Calendar.DAY_OF_YEAR;
    }

    public int since(Calendar a, Calendar b) {
        Calendar one = reset(a);
        Calendar two = reset(b);
        long l = one.getTimeInMillis() - two.getTimeInMillis();

        return (int) (l / (24L * 3600000L));
    }
}