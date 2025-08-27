package com.trackstudio.jmx.beans;

import com.trackstudio.exception.GranException;

public interface IUserSessionsMXBean {
    String[] getUserSessions() throws GranException;
}
