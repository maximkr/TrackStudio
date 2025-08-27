package com.trackstudio.containers;

import com.trackstudio.tools.PropertyContainer;

import net.jcip.annotations.ThreadSafe;

/**
 * Класс-контейнер для хранения данных о подписках
 */
@ThreadSafe
public class SubscriptionListItem extends NotificationListItem {
    public volatile String start;
    public volatile String stop;
    public volatile String nextrun;
    public volatile String interval;

    public String getStart() {
        return start;
    }

    public String getStop() {
        return stop;
    }

    public String getNextrun() {
        return nextrun;
    }

    public String getInterval() {
        return interval;
    }

    public void setStart(String start) {
        this.start = start;
    }

    public void setStop(String stop) {
        this.stop = stop;
    }

    public void setNextrun(String nextrun) {
        this.nextrun = nextrun;
    }

    public void setInterval(String interval) {
        this.interval = interval;
    }

    protected PropertyContainer getContainer() {
        PropertyContainer pc = container.get();
        if (pc != null)
            return pc; // object in cache, return it

        PropertyContainer newPC = new PropertyContainer();
        newPC.put(name).put(task.getName()).put(filter).put(template).put(start).put(stop).put(interval).put(id);

        if (container.compareAndSet(null, newPC)) // try to update
            return newPC; // we can update - return loaded value
        else
            return container.get(); // some other thread already updated it - use saved value
    }

    public SubscriptionListItem(String id, String name) {
        super(id, name);
    }


}
