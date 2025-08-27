package com.trackstudio.tools.compare;

import java.util.Comparator;

import com.trackstudio.secured.SecuredUserBean;

import net.jcip.annotations.Immutable;

@Immutable
public class SortUser implements Comparator<SecuredUserBean> {
    private final FieldSort key;

    public SortUser(FieldSort key) {
        this.key = key;
    }

    @Override
    public int compare(SecuredUserBean o1, SecuredUserBean o2) {
        switch (key) {
            case REVERSE_NAME :
                return -o1.getName().compareTo(o2.getName());
            case NAME :
                return o1.getName().compareTo(o2.getName());
            case LOGIN :
                return o1.getLogin().compareTo(o2.getLogin());
            default:
                return 0;
        }
    }
}
