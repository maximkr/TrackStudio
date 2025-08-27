package com.trackstudio.tools.compare;

import java.util.Comparator;

import net.jcip.annotations.Immutable;

@Immutable
public class SortUdf implements Comparator<IUdfSort> {
    private final FieldSort key;

    public SortUdf(FieldSort key) {
        this.key = key;
    }

    @Override
    public int compare(IUdfSort o1, IUdfSort o2) {
        switch (key) {
            case NAME:
                return o1.getCaption().compareTo(o2.getCaption());
            case ORDER:
                if (o1.getOrder() != null && o2.getOrder() != null) {
                    int result = o1.getOrder().compareTo(o2.getOrder());
                    if (result == 0) {
                        if (o1.equals(o2)) {
                            return 0;
                        } else {
                            return o1.getCaption().compareTo(o2.getCaption());
                        }
                    }
                    return result;
                } else if (o1.getOrder() != null) {
                    return -1;
                } else if (o2.getOrder() != null) {
                    return 1;
                } else {
                    return 0;
                }
            default:
                return 0;
        }
    }
}
