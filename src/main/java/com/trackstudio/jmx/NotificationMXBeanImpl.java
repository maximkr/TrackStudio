package com.trackstudio.jmx;

import java.util.concurrent.atomic.AtomicLong;

import com.trackstudio.jmx.beans.AbstractNotification;
import com.trackstudio.jmx.beans.INotificationMXBean;

import net.jcip.annotations.Immutable;

@Immutable
public class NotificationMXBeanImpl extends AbstractNotification implements INotificationMXBean {
    private final AtomicLong sequenceNumber = new AtomicLong(0);
    private static final NotificationMXBeanImpl instance = new NotificationMXBeanImpl();

    private NotificationMXBeanImpl() {}

    public static NotificationMXBeanImpl getInstance() {
        return instance;
    }

    public void sendUser(com.trackstudio.simple.Notification simpleNotification, String login, String code, String taskId, String msgId) {
        StringBuilder sb = new StringBuilder();
        sb.append("login=").append(login).append(";");
        sb.append("nofiticationId=").append(simpleNotification.getId()).append(";");
        sb.append("nofiticationName=").append(simpleNotification.getName()).append(";");
        sb.append("code=").append(code).append(";");
        sb.append("taskId=").append(taskId).append(";");
        sb.append("msgId=").append(msgId).append(";");
        super.send(this, sequenceNumber.incrementAndGet(), "Import Mail", "Import Mail create msg : " + sb.toString());
    }
}
