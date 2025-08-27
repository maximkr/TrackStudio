package com.trackstudio.tools.formatter;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.atomic.AtomicInteger;

import com.trackstudio.app.filter.FilterSettings;

import net.jcip.annotations.Immutable;

@Immutable
public class SortedFactory {
    private final FilterSettings settings;
    private final CopyOnWriteArrayList<String> curOrder = new CopyOnWriteArrayList<String>();
    private final AtomicInteger parts = new AtomicInteger(1);
    private final CopyOnWriteArrayList<String> sortFields = new CopyOnWriteArrayList<String>();

    public SortedFactory(FilterSettings settings, List<String> sortFields) {
        this.settings = settings;
        if (settings.getSortedBy()!=null)
            curOrder.addAll(settings.getSortedBy());
        if (sortFields != null)
            this.sortFields.addAll(sortFields);
    }

    public SortedLink getLink(String field, String filter, int size) {
        String currentSort = null;
        if (sortFields.contains(field) || sortFields.contains("_" + field)) {
            currentSort = sortFields.contains(field) ? "abs" : "desc";
        }
        boolean value = settings.getSettings().getView().contains(filter);
        if (value)
            parts.addAndGet(size);
        return new SortedLink((!curOrder.isEmpty() && ('_' + field).equals(curOrder.get(0)) ? "" : "_") + field, value, size, currentSort);
    }

    public int getParts() {
        return parts.get();
    }
}
