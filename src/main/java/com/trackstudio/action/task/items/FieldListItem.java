package com.trackstudio.action.task.items;

import com.trackstudio.tools.PropertyComparable;
import com.trackstudio.tools.PropertyContainer;

/**
 * Created by IntelliJ IDEA.
 * User: User
 * Date: 24.04.2007
 * Time: 11:59:27
 * To change this template use File | Settings | File Templates.
 */
public class FieldListItem extends PropertyComparable {
    int order;
    String fieldKey;
    String display;
    String sortBy;


    public int getOrder() {
        return order;
    }

    public void setOrder(int order) {
        this.order = order;
    }

    public String getFieldKey() {
        return fieldKey;
    }

    public void setFieldKey(String fieldKey) {
        this.fieldKey = fieldKey;
    }

    public String getDisplay() {
        return display;
    }

    public void setDisplay(String display) {
        this.display = display;
    }

    public String getSortBy() {
        return sortBy;
    }

    public void setSortBy(String sortBy) {
        this.sortBy = sortBy;
    }


    public FieldListItem(int order, String filterdKey, String sortBy, String text) {
        this.order = order;
        this.fieldKey = filterdKey;
        this.display = text;
        this.sortBy = sortBy;
    }

    protected PropertyContainer getContainer() {
        PropertyContainer pc = container.get();
        if (pc != null)
            return pc; // object in cache, return it

        PropertyContainer newPC = new PropertyContainer();
        newPC.put(order).put(sortBy);

        if (container.compareAndSet(null, newPC)) // try to update
            return newPC; // we can update - return loaded value
        else
            return container.get(); // some other thread already updated it - use saved value
    }
}
