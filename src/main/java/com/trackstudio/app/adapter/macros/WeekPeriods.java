package com.trackstudio.app.adapter.macros;

import java.util.Calendar;

import net.jcip.annotations.Immutable;

@Immutable
public class WeekPeriods implements Periods {
    public String getPattern() {
        return "dd.MM.yy";
    }

    public int getShift() {
        return Calendar.WEEK_OF_YEAR;
    }

    public Calendar reset(Calendar a) {
        Calendar two = (Calendar) a.clone();
        two.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY);
        two.set(Calendar.HOUR_OF_DAY, 0);
        two.set(Calendar.MINUTE, 0);
        two.set(Calendar.SECOND, 1);
        two.set(Calendar.MILLISECOND, 0);
        return two;
    }

    public int since(Calendar a, Calendar b) {
        Calendar one = reset(a);
        Calendar two = reset(b);
        long l = one.getTimeInMillis() - two.getTimeInMillis();

        return (int) (l / (24L * 3600000L * 7L));
    }
}