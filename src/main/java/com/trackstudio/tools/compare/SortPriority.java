package com.trackstudio.tools.compare;

import java.util.Comparator;

import com.trackstudio.secured.SecuredPriorityBean;

import net.jcip.annotations.Immutable;

@Immutable
public class SortPriority implements Comparator<SecuredPriorityBean> {
    private final FieldSort key;
    private final boolean order;

    public SortPriority(FieldSort key, boolean order) {
        this.key = key;
        this.order = order;
    }

    @Override
    public int compare(SecuredPriorityBean o1, SecuredPriorityBean o2) {
        switch (key) {
            case ORDER:
                int result = o1.getOrder().compareTo(o2.getOrder());
                return order ? result : -result;
            default:
                return 0;
        }
    }
}
