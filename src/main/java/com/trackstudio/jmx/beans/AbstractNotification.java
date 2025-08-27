package com.trackstudio.jmx.beans;

import javax.management.AttributeChangeNotification;
import javax.management.MBeanNotificationInfo;
import javax.management.Notification;
import javax.management.NotificationBroadcasterSupport;

public abstract class AbstractNotification extends NotificationBroadcasterSupport {
    @Override
    public MBeanNotificationInfo[] getNotificationInfo() {
        String[] types = new String[] {
                AttributeChangeNotification.ATTRIBUTE_CHANGE
        };
        String name = AttributeChangeNotification.class.getName();
        String description = "An attribute of this MBean has changed";
        MBeanNotificationInfo info =
                new MBeanNotificationInfo(types, name, description);
        return new MBeanNotificationInfo[] {info};
    }
    
    public void send(Object source, long sequenceNumber, String name, String text) {
        Notification n = new AttributeChangeNotification(
                source,
                sequenceNumber,
                System.currentTimeMillis(),
                text,
                name,
                "String",
                null,
                null);
        sendNotification(n);
    }
}
