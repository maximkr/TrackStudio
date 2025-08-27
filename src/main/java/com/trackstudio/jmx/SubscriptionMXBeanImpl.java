package com.trackstudio.jmx;

import java.util.concurrent.atomic.AtomicLong;

import com.trackstudio.jmx.beans.AbstractNotification;
import com.trackstudio.jmx.beans.ISubscriptionMXBean;
import com.trackstudio.model.Subscription;

public class SubscriptionMXBeanImpl extends AbstractNotification implements ISubscriptionMXBean {
    private final AtomicLong sequenceNumber = new AtomicLong(0);

    private static final SubscriptionMXBeanImpl instance = new SubscriptionMXBeanImpl();

    private SubscriptionMXBeanImpl() {}

    public static SubscriptionMXBeanImpl getInstance() {
        return instance;
    }

    public void sendUser(Subscription subscription, String prstatusId, String userId) {
        StringBuilder sb = new StringBuilder();
        sb.append("prstatusId=").append(prstatusId).append(";");
        sb.append("userId=").append(userId).append(";");
        sb.append("subscriptionId=").append(subscription.getId()).append(";");
        sb.append("subscriptionName=").append(subscription.getName()).append(";");
        sb.append("taskId=").append(subscription.getTask().getId()).append(";");
        sb.append("taskName=").append(subscription.getTask().getName()).append(";");
        sb.append("filterId=").append(subscription.getFilter().getId()).append(";");

        super.send(this, sequenceNumber.incrementAndGet(), "Subscription Mail", "Subscription Mail : " + sb.toString());
    }
}
