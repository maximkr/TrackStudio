package com.trackstudio.app.filter.list;

import java.util.Set;
import java.util.TreeSet;

import net.jcip.annotations.NotThreadSafe;

/**
 * Contains per-thread list of tasks, not required to be thread-safe
 */

@NotThreadSafe
public class ItemQueue {
    private final Set<String> set = new TreeSet<String>();

    public ItemQueue() {
    }

    public Set<String> getSet() {
        return set;
    }
}
