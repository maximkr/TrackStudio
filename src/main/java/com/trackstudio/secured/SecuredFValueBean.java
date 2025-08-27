package com.trackstudio.secured;

import java.awt.*;
import java.util.ArrayList;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import com.trackstudio.app.filter.FValue;
import com.trackstudio.app.session.SessionContext;
import com.trackstudio.exception.GranException;
import com.trackstudio.tools.PropertyContainer;

import net.jcip.annotations.Immutable;

/**
 * Bean which represents filtering criteria
 */
@Immutable
public abstract class SecuredFValueBean extends Secured {
    protected final ConcurrentMap map = new ConcurrentHashMap();

    public String getId() {
        return null;
    }

     protected PropertyContainer getContainer() {
         PropertyContainer pc = container.get();
         if (pc != null)
             return pc; // object in cache, return it

         PropertyContainer newPC = new PropertyContainer();
         newPC.put(getId());

         if (container.compareAndSet(null, newPC)) // try to update
             return newPC; // we can update - return loaded value
         else
             return container.get(); // some other thread already updated it - use saved value
    }

    public boolean isAllowedByACL() throws GranException {
        return true;
    }

    public boolean canView() throws GranException {
        return true;
    }

    public SecuredFValueBean(FValue f, SessionContext sc) {
        map.putAll(f);
        this.sc = sc;
    }

    protected String getStirngValueFromMap(String key){
        Object o = map.get(key);
        if (o == null)
            return null;
        if (o instanceof List){
            return ((List)o).getItem(0);
        }
        if (o instanceof ArrayList){
            return String.valueOf(((ArrayList)o).get(0));
        }
        return String.valueOf(o);
    }


    protected String[] getArrayValueFromMap(String key){
        Object o = map.get(key);
        if (o == null)
            return null;
        if (o instanceof List){
            return ((List) o).getItems();
        }
        return null;
    }
}
