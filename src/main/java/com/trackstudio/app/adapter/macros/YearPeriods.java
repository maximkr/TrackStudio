package com.trackstudio.app.adapter.macros;

import java.util.Calendar;

import net.jcip.annotations.Immutable;

@Immutable
public class YearPeriods implements Periods {
    public Calendar reset(Calendar a) {
        Calendar one = (Calendar) a.clone();
        one.set(Calendar.DAY_OF_YEAR, 1);
        one.set(Calendar.HOUR_OF_DAY, 0);
        one.set(Calendar.MINUTE, 0);
        one.set(Calendar.SECOND, 1);
        one.set(Calendar.MILLISECOND, 0);
        return one;
    }

    @Override
    public int since(Calendar a, Calendar b) {
        Calendar one = reset(a);
        Calendar two = reset(b);
        int res = 0;
        while (two.before(one)) {
            res++;
            two.add(getShift(), 1);
        }
        return res;
    }

    @Override
    public String getPattern() {
        return "yyyy";
    }

    @Override
    public int getShift() {
        return Calendar.YEAR;
    }

}
