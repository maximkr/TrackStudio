package com.trackstudio.secured;

import com.trackstudio.tools.PropertyContainer;

import net.jcip.annotations.Immutable;

@Immutable
public class SecuredSearchUserItem extends SecuredSearchItem {
    private final SecuredUserBean user;

    public SecuredSearchUserItem(int pos, SecuredUserBean user, String surroundText, String word) {
        super(surroundText, user.getSecure(), word);
        this.user = user;
    }

    protected PropertyContainer getContainer() {
        PropertyContainer pc = container.get();
        if (pc != null)
            return pc; // object in cache, return it

        PropertyContainer newPC = new PropertyContainer();
        if (user != null) newPC.put(user.getName()).put(user.getId());

        if (container.compareAndSet(null, newPC)) // try to update
            return newPC; // we can update - return loaded value
        else
            return container.get(); // some other thread already updated it - use saved value
    }

    public SecuredUserBean getUser() {
        return user;
    }

    public String getName() {
        return user.getName();
    }

    public String getHighlightName() {
        return getHighlightText(user.getName());
    }

    public String getId() {
        return user.getId();
    }


}
