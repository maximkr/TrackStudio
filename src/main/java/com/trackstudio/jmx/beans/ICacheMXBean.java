package com.trackstudio.jmx.beans;

import com.trackstudio.exception.GranException;

public interface ICacheMXBean {
    long getTotalTasks() throws GranException;
    long getTotalUsers() throws GranException;
    String getMemorySizeTasks() throws GranException, IllegalAccessException;
    String getMemorySizeUsers() throws GranException, IllegalAccessException;
}
