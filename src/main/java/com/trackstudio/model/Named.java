package com.trackstudio.model;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class Named {
    private static Log log = LogFactory.getLog(Named.class);
    protected String name;
    protected String id;

    public String getName() {
        return this.name;
    }

    public String getCodeName() {
        return this.getName() + this.getId();
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getId() {
        return id;
    }

    public void setId(String s) {
        this.id = s;
    }

    public int hashCode() {
        return (getId() + getName()).hashCode();
    }

    public boolean equals(Object obj) {
        return obj instanceof Named && ((Named) obj).getId().equals(this.id) && ((Named) obj).getId().equals(this.name);
    }


}
