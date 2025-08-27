package com.trackstudio.secured;

import com.trackstudio.exception.GranException;
import com.trackstudio.tools.PropertyContainer;

import net.jcip.annotations.Immutable;

@Immutable
public class SecuredSearchTaskItem extends SecuredSearchItem {
    private final SecuredTaskBean task;

    public SecuredSearchTaskItem(SecuredTaskBean task, String surroundText, String word) {
        super(surroundText, task.getSecure(), word);
        this.task = task;
    }

    protected PropertyContainer getContainer() {
        PropertyContainer pc = container.get();
        if (pc != null)
            return pc; // object in cache, return it

        PropertyContainer newPC = new PropertyContainer(true);
        if (task != null)
            try {
            newPC.put(task.getUpdatedate()).put(task.getName());
        } catch (GranException e) {
            e.printStackTrace();
        }

        if (container.compareAndSet(null, newPC)) // try to update
            return newPC; // we can update - return loaded value
        else
            return container.get(); // some other thread already updated it - use saved value
    }

    public SecuredTaskBean getTask() {
        return task;
    }

    public String getName() {
        return task.getName();
    }

    public String getHighlightName() {
        return getHighlightText(task.getName());
    }

    public String getId() {
        return task.getId();
    }


}
