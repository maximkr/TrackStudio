package com.trackstudio.tools;

import com.trackstudio.kernel.cache.PluginType;

import net.jcip.annotations.Immutable;

@Immutable
public class PluginPair implements Comparable<PluginPair> {
    private final String key;
    private final PluginType type;
    private final Long time;

    public PluginPair(String key, PluginType type, Long time) {
        this.key = key;
        this.type = type;
        this.time = time;
    }

    public String getKey() {
        return key;
    }

    public Long getTime() {
        return time;
    }

    public PluginType getType() {
        return type;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof PluginPair)) return false;

        PluginPair that = (PluginPair) o;

        if (key != null ? !key.equals(that.key) : that.key != null) return false;
        if (time != null ? !time.equals(that.time) : that.time != null) return false;
        if (type != null ? !type.equals(that.type) : that.type != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = key != null ? key.hashCode() : 0;
        result = 31 * result + (time != null ? time.hashCode() : 0);
        return result;
    }

    @Override
    public int compareTo(PluginPair o) {
        return this.key.compareTo(o.key);
    }
}
