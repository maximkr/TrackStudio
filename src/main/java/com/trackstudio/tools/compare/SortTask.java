package com.trackstudio.tools.compare;

import java.util.Comparator;

import com.trackstudio.secured.SecuredTaskBean;

import net.jcip.annotations.Immutable;

@Immutable
public class SortTask implements Comparator<SecuredTaskBean> {
    private final FieldSort key;
    private final boolean asc;

    public SortTask(FieldSort key, boolean asc) {
        this.key = key;
        this.asc = asc;
    }

    @Override
    public int compare(SecuredTaskBean o1, SecuredTaskBean o2) {
        int sortType = asc ? 1 : -1;
        switch (key) {
            case NAME:
                return sortType * o1.getName().compareTo(o2.getName());
            case NUMBER:
                return sortType * o1.getNumber().compareTo(o2.getNumber());
            default:
                return 0;
        }
    }
}
