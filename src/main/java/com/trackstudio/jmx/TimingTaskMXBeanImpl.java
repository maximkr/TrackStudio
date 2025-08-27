package com.trackstudio.jmx;

import java.util.Calendar;
import java.util.concurrent.atomic.AtomicLong;

import javax.management.AttributeChangeNotification;
import javax.management.Notification;

import com.trackstudio.jmx.beans.AbstractNotification;
import com.trackstudio.jmx.beans.ITimingTaskMXBean;

public class TimingTaskMXBeanImpl extends AbstractNotification implements ITimingTaskMXBean {
    private final AtomicLong sequenceNumber = new AtomicLong(0);
    private volatile Calendar startCreate = null;
    private volatile Calendar startUpdate = null;
    private volatile Calendar startPage = null;
    private static final TimingTaskMXBeanImpl instance = new TimingTaskMXBeanImpl();

    private TimingTaskMXBeanImpl() {}

    public static TimingTaskMXBeanImpl getInstance() {
        return instance;
    }

    public void setStartCreateTask(Calendar start) {
        this.startCreate = (Calendar)start.clone();
    }

    public void setStartPageTask(Calendar start) {
        this.startPage = (Calendar)start.clone();
    }

    public void setFinishPageTask(Calendar end) {
        Notification n = new AttributeChangeNotification(this,
                sequenceNumber.incrementAndGet(),
                System.currentTimeMillis(),
                "Page task time : " + (end.getTimeInMillis() - startPage.getTimeInMillis()) + " ms",
                "Page task",
                "String",
                startPage,
                end);

        sendNotification(n);
    }

    public void setFinishCreateTask(Calendar end) {
        Notification n = new AttributeChangeNotification(this,
                        sequenceNumber.incrementAndGet(),
                        System.currentTimeMillis(),
                        "Create task time : " + (end.getTimeInMillis() - startCreate.getTimeInMillis()) + " ms",
                        "Create task",
                        "String",
                        startCreate,
                        end);

        sendNotification(n);
    }

    public void setStartUpdateTask(Calendar start) {
        this.startUpdate = (Calendar)start.clone();
    }
    public void setFinishUpdateTask(Calendar end) {
        Notification n = new AttributeChangeNotification(this,
                sequenceNumber.incrementAndGet(),
                System.currentTimeMillis(),
                "Update task time : " + (end.getTimeInMillis() - startUpdate.getTimeInMillis()) + " ms",
                "Update task",
                "String",
                startUpdate,
                end);

        sendNotification(n);
    }
}
