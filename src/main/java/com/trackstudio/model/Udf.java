package com.trackstudio.model;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

import com.trackstudio.constants.UdfConstants;


/**
 * Describes custom field (User Defined Field, UDF)
 */
public class Udf implements Serializable, Comparable {

    public static int STRING = 0;
    public static int FLOAT = 1;
    public static int DATE = 2;
    public static int LIST = 3;
    public static int INTEGER = 4;
    public static int MEMO = 5;


    private String id; //identifier
    private String caption; //persistent
    private String referencedbycaption; //persistent
    private Integer order; //persistent
    private String def; //persistent
    private Task initialtask; //persistent
    private User initialuser; //persistent
    private Integer required; //persistent
    private Integer htmlview; //persistent
    private Integer lookuponly; //persistent
    private Integer cachevalues; //persistent
    private Integer type; //persistent
    private Set udflistSet = new HashSet(); //persistent
    private Set udfvalSet = new HashSet(); //persistent
    private Set uprstatusSet = new HashSet(); //persistent
    private Set umstatusSet = new HashSet(); //persistent
    private String script;
    private String lookupscript;


    public String getReferencedbycaption() {
        return referencedbycaption;
    }

    public void setReferencedbycaption(String referencedbycaption) {
        this.referencedbycaption = referencedbycaption;
    }

    public String getScript() {
        return script;
    }

    public void setScript(String formulaLongtext) {
        this.script = formulaLongtext;
    }

    public void setNullScript() {
        this.script = null;
    }


    public String getLookupscript() {
        return lookupscript;
    }

    public void setNullLookupScript() {
        this.lookupscript = null;
    }

    public void setLookupscript(String formulaLongtext) {
        this.lookupscript = formulaLongtext;
    }


    private Udfsource udfsource;

    public Udf(String id) {
        this.id = id;
    }

    public Udf(String caption, String referencedbycaption, Integer order, String def, boolean required, boolean htmlview, Integer type, String formulaLongtext, String lookupformulaLongtext, boolean lookuponly, boolean cachevalues, Task initialtask, User initialuser, Udfsource udfsource) {
        this.caption = caption;
        this.referencedbycaption = referencedbycaption;
        this.order = order;
        this.def = def;
        this.required = required ? 1 : 0;
        this.htmlview = htmlview ? 1 : 0;
        this.type = type;
        this.udfsource = udfsource;
        this.script = formulaLongtext;
        this.lookupscript = lookupformulaLongtext;
        this.lookuponly = lookuponly ? 1 : 0;
        this.cachevalues = cachevalues ? 1 : 0;
        this.initialtask = initialtask;
        this.initialuser = initialuser;
    }

    public Udf(String caption, String referencedbycaption, Integer order, String def, boolean required, boolean htmlview, Integer type, String formulaLongtext, String lookupformulaLongtext, boolean lookuponly, boolean cachevalues, String initial, String udfsourceId) {
        this(caption, referencedbycaption, order, def, required, htmlview, type, formulaLongtext, lookupformulaLongtext,
                lookuponly, cachevalues, type == UdfConstants.TASK && initial != null ? new Task(initial) : null, type == UdfConstants.USER && initial != null ? new User(initial) : null, udfsourceId != null ? new Udfsource(udfsourceId) : null);
    }

    public Udf() {
    }

    public Udf(Udfsource udfsource) {
        this.udfsource = udfsource;
    }

    public String getId() {
        return this.id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getCaption() {
        return this.caption;
    }

    public boolean hasScript() {

        return script != null;
    }

    public boolean hasLookupScript() {

        return lookupscript != null;
    }


    public void setCaption(String caption) {
        this.caption = caption;
    }

    public Integer getOrder() {
        return this.order;
    }

    public void setOrder(Integer order) {
        this.order = order;
    }

    public String getDef() {
        return this.def;
    }

    public void setDef(String def) {
        this.def = def;
    }

    public Integer getRequired() {
        return this.required;
    }

    public void setRequired(Integer required) {
        this.required = required;
    }

    public Integer getHtmlview() {
        return htmlview;
    }

    public void setHtmlview(Integer htmlview) {
        this.htmlview = htmlview;
    }

    public Integer getType() {
        return this.type;
    }

    public void setType(Integer type) {
        this.type = type;
    }

    public Udfsource getUdfsource() {
        return this.udfsource;
    }

    public void setUdfsource(Udfsource udfsource) {
        this.udfsource = udfsource;
    }

    public Set getUdflistSet() {
        return this.udflistSet;
    }

    public void setUdflistSet(Set udflistSet) {
        this.udflistSet = udflistSet;
    }

    public Set getUdfvalSet() {
        return this.udfvalSet;
    }

    public void setUdfvalSet(Set udfvalSet) {
        this.udfvalSet = udfvalSet;
    }

    public int compareTo(Object it) {

        if (order == null) {
            return 1;
        }
        Integer other = ((Udf) it).getOrder();
        if (other == null) {
            return -1;
        }
        int i = this.order.compareTo(((Udf) it).getOrder());
        if (i == 0)
            return (order + caption + id).compareTo(((Udf) it).getOrder() + ((Udf) it).getCaption() + ((Udf) it).getId());
        else
            return i;

    }

    public boolean equals(Object obj) {
        return obj instanceof Udf && ((Udf) obj).getId().equals(this.id);
    }

    public Set getUprstatusSet() {
        return uprstatusSet;
    }

    public void setUprstatusSet(Set uprstatusSet) {
        this.uprstatusSet = uprstatusSet;
    }

    public Integer getLookuponly() {
        return lookuponly;
    }

    public void setLookuponly(Integer lookuponly) {
        this.lookuponly = lookuponly;
    }

    public Integer getCachevalues() {
        return cachevalues;
    }

    public void setCachevalues(Integer cachevalues) {
        this.cachevalues = cachevalues;
    }

    public Task getInitialtask() {
        return initialtask;
    }

    public void setInitialtask(Task initialtask) {
        this.initialtask = initialtask;
    }

    public User getInitialuser() {
        return initialuser;
    }

    public void setInitialuser(User initialuser) {
        this.initialuser = initialuser;
    }

    public String toString() {
        return caption;

    }


    public Set getUmstatusSet() {
        return umstatusSet;
    }

    public void setUmstatusSet(Set umstatusSet) {
        this.umstatusSet = umstatusSet;
    }

    @Override
    public int hashCode() {
        return id != null ? id.hashCode() : 0;
    }
}
