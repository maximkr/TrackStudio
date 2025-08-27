package com.trackstudio.tools.formatter;

import java.util.concurrent.atomic.AtomicInteger;

import net.jcip.annotations.Immutable;

@Immutable
public class SortedLink {
    private final String sortBy;
    private final Boolean canView;
    private final AtomicInteger parts;
    private final String currentSort;

    public String getCurrentSort() {
        return currentSort;
    }

    public String getSortBy() {
        return sortBy;
    }


    public Boolean getCanView() {
        return canView;
    }

    public Boolean isCanView() {
        return canView;
    }

    public int getParts() {
        return parts.get();
    }

    public void setParts(int parts) {
        this.parts.set(parts);
    }

    public SortedLink(String sortBy, Boolean canView, int parts, String currentSort) {
        this.sortBy = sortBy;
        this.parts = new AtomicInteger(parts);
        this.canView = canView;
        this.currentSort = currentSort;
    }
}
