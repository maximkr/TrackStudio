package com.trackstudio.app.adapter.macros;

import java.util.Calendar;

import net.jcip.annotations.Immutable;

@Immutable
public interface Periods{
    public int since(Calendar a, Calendar b);
    public String getPattern();
    public int getShift();
    public Calendar reset(Calendar a);
}