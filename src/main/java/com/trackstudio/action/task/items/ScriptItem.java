package com.trackstudio.action.task.items;

public class ScriptItem implements Comparable<ScriptItem>{
    private String name;
    private String type;
    private String connectedTo;
    private boolean exist;

    public ScriptItem(String name, String type, String connectedTo, boolean exist) {
        this.name = name;
        this.type = type;
        this.connectedTo = connectedTo;
        this.exist = exist;
    }

    public String getName() {
        return name;
    }

    public String getType() {
        return type;
    }

    public String getConnectedTo() {
        return connectedTo;
    }

    public boolean isExist() {
        return exist;
    }

    public int compareTo(ScriptItem o) {
        return this.getName().compareTo(o.getName());
    }
}
