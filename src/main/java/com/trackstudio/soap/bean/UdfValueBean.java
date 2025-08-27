package com.trackstudio.soap.bean;


public class UdfValueBean {
    protected String udfId;
    /**
     * Can be one of these : 'string', 'float', 'integer', 'date', 'url', 'task', 'user', 'list', 'multilist'
     */
    protected String type;

    /**
     * A String presentation on UDF value. For 'date' its TimeInMillis, 'list' is a list ID, 'multilist' is a string like 'id1=val1\nid2=val2'
     * Also for 'task' and 'user'
     */
    protected String value;

    private String name;

    public String getUdfId() {
        return udfId;
    }

    public void setUdfId(String udfId) {
        this.udfId = udfId;
    }


    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
