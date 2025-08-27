package com.trackstudio.containers;

import com.trackstudio.secured.SecuredPrstatusBean;
import com.trackstudio.tools.PropertyComparable;
import com.trackstudio.tools.PropertyContainer;

import net.jcip.annotations.Immutable;

/**
 * Класс-контейнер для хранения данных о правилах доступа
 */
@Immutable
public class RuleListItem extends PropertyComparable {

    protected final SecuredPrstatusBean status;
    protected final String restriction; //seems like not used


    public RuleListItem(SecuredPrstatusBean status, String restrictions) {
        this.status = status;
        this.restriction = restrictions;
    }

    public SecuredPrstatusBean getStatus() {
        return status;
    }

    public String getRestriction() {
        return restriction;
    }

    protected PropertyContainer getContainer() {
        PropertyContainer pc = container.get();
        if (pc != null)
            return pc; // object in cache, return it

        PropertyContainer newPC = new PropertyContainer();
        newPC.put(status.getName()).put(restriction).put(status.getId());

        if (container.compareAndSet(null, newPC)) // try to update
            return newPC; // we can update - return loaded value
        else
            return container.get(); // some other thread already updated it - use saved value
    }
}
