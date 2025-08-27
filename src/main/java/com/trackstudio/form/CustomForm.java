package com.trackstudio.form;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.struts.action.ActionMapping;


public class CustomForm extends BaseForm {
    private String udfId;

    private String addlist;
    private String require;
    private String htmlview;
    private String lookuponly;
    private String cachevalues;
    private String createNewUdf;
    protected String workflowId;
    protected String[] delete;
    protected String idUdf;
    protected String caption;
    protected String referencedbycaption;
    protected String order;
    protected Integer type;
    protected String def;
    protected String script;
    protected String list;
    protected String lscript;
    protected String initial;
    protected String calculen;
    protected String lookupen;
    protected String canview, cannotview, hiddencanview;
    protected String canedit, cannotedit, hiddencanedit;
    protected boolean required;


    protected Map<String, String> captions = new HashMap<String, String>();
    protected Map<String, String> orders = new HashMap<String, String>();
    protected Map<String, String> defaults = new HashMap<String, String>();
    protected Map<String, String> scripts = new HashMap<String, String>();
    protected Map<String, String> lscripts = new HashMap<String, String>();
    protected Map<String, String> addlists = new HashMap<String, String>();
    public Map<String, String> dellists = new HashMap<String, String>();
    public Map<String, String[]> lists = new HashMap<String, String[]>();
    public Map<String, String> requireds = new HashMap<String, String>();
    public Map<String, String> htmlviews = new HashMap<String, String>();

    public Object getCaptions(String key) {
        Object o = captions.get(key);
        return o != null ? o : "";
    }


    public String getReferencedbycaption() {
        return referencedbycaption;
    }

    public void setReferencedbycaption(String referencedbycaption) {
        this.referencedbycaption = referencedbycaption;
    }

    public void setCaptions(String key, String value) {
        captions.put(key, value);
    }

    public Object getOrders(String key) {
        Object o = orders.get(key);
        return o != null ? o : "";
    }

    public void setOrders(String key, String value) {
        orders.put(key, value);
    }

    public String getIdUdf() {
        return idUdf;
    }

    public void setIdUdf(String idUdf) {
        this.idUdf = idUdf;
    }

    public Object getDefaults(String key) {
        Object o = defaults.get(key);
        return o != null ? o : "";
    }

    public void setDefaults(String key, String value) {
        defaults.put(key, value);
    }

    public Object getScripts(String key) {
        Object o = scripts.get(key);
        return o != null ? o : "";
    }

    public void setScripts(String key, String value) {
        scripts.put(key, value);
    }

    public Object getLookupscripts(String key) {
        Object o = lscripts.get(key);
        return o != null ? o : "";
    }

    public void setLookupscripts(String key, String value) {
        lscripts.put(key, value);
    }

    public String getAddlists(String key) {
        return addlists.get(key);
    }

    public void setAddlists(String key, String value) {
        addlists.put(key, value);
    }

    public String getLists(String key) {
        String[] s = lists.get(key);
        return s != null && s.length > 0 ? s[0] : null;
    }

    public void setLists(String key, String value) {
        lists.put(key, new String[]{value});
    }

    public String getDellists(String key) {
        return dellists.get(key);
    }

    public void setDellists(String key, String value) {
        dellists.put(key, value);
    }

    public String getRequireds(String key) {
        String o = requireds.get(key);

        if (o == null || o.equals("off")) return null;
        else return "on";
    }

    public void setRequireds(String key, String value) {
        requireds.put(key, value);
    }

    public String getHtmlviews(String key) {
        String o = htmlviews.get(key);

        if (o == null || o.equals("off")) return null;
        else return "on";
    }

    public void setHtmlviews(String key, String value) {
        htmlviews.put(key, value);
    }

    public String getCaption() {
        return caption;
    }

    public void setCaption(String caption) {
        this.caption = caption;
    }

    public String getOrder() {
        return order;
    }

    public void setOrder(String order) {
        this.order = order;
    }

    public Integer getType() {
        return type;
    }

    public void setType(Integer type) {
        this.type = type;
    }

    public String getDef() {
        return def;
    }

    public void setDef(String def) {
        this.def = def;
    }

    public void setScript(String script) {
        this.script = script;
    }

    public String getScript() {
        return script;
    }

    public void setLscript(String lscript) {
        this.lscript = lscript;
    }

    public String getLscript() {
        return lscript;
    }

    public String getList() {
        return list;
    }

    public void setList(String list) {
        this.list = list;
    }

    public boolean isRequired() {
        return required;
    }

    public void setRequired(boolean required) {
        this.required = required;
    }

    public String getHtmlview() {
        return htmlview;
    }

    public void setHtmlview(String htmlview) {
        this.htmlview = htmlview;
    }


    public void reset(ActionMapping actionMapping, HttpServletRequest httpServletRequest) {
        super.reset(actionMapping, httpServletRequest);
        caption = null;
        required = false;
        order = null;
        type = null;
        def = null;
        script = null;
        lscript = null;
        list = null;
        idUdf = null;
        lookuponly = "false";
        cachevalues = "true";
        initial = null;
        addlists = new HashMap<String, String>();
        dellists = new HashMap<String, String>();

    }

    public String getWorkflowId() {
        return workflowId;
    }

    public void setWorkflowId(String workflowId) {
        this.workflowId = workflowId;
    }

    public String[] getDelete() {
        return delete;
    }

    public void setDelete(String[] delete) {
        this.delete = delete;
    }

    public String getUdfId() {
        return udfId;
    }

    public void setUdfId(String udfId) {
        if (isMutable())
            this.udfId = udfId;
    }

    public String getRequire() {
        return require;
    }

    public void setRequire(String require) {
        this.require = require;
    }

    public String getCreateNewUdf() {
        return createNewUdf;
    }

    public void setCreateNewUdf(String createNewUdf) {
        if (isMutable())
            this.createNewUdf = createNewUdf;
    }

    public String getAddlist() {
        return addlist;
    }

    public void setAddlist(String addlist) {
        this.addlist = addlist;
    }

    public String getCachevalues() {
        return cachevalues;
    }

    public void setCachevalues(String cachevalues) {
        this.cachevalues = cachevalues;
    }

    public String getInitial() {
        return initial;
    }

    public void setInitial(String initial) {
        this.initial = initial;
    }

    public String getCalculen() {
        return calculen;
    }

    public void setCalculen(String calculen) {
        this.calculen = calculen;
    }

    public String getLookupen() {
        return lookupen;
    }

    public void setLookupen(String lookupen) {
        this.lookupen = lookupen;
    }


    public String getLookuponly() {
        return lookuponly;
    }

    public void setLookuponly(String lookuponly) {
        this.lookuponly = lookuponly;
    }

    public String getCanview() {
        return canview;
    }

    public void setCanview(String canview) {
        this.canview = canview;
    }

    public String getCannotview() {
        return cannotview;
    }

    public void setCannotview(String cannotview) {
        this.cannotview = cannotview;
    }

    public String getHiddencanview() {
        return hiddencanview;
    }

    public void setHiddencanview(String hiddencanview) {
        this.hiddencanview = hiddencanview;
    }

    public String getCanedit() {
        return canedit;
    }

    public void setCanedit(String canedit) {
        this.canedit = canedit;
    }

    public String getCannotedit() {
        return cannotedit;
    }

    public void setCannotedit(String cannotedit) {
        this.cannotedit = cannotedit;
    }

    public String getHiddencanedit() {
        return hiddencanedit;
    }

    public void setHiddencanedit(String hiddencanedit) {
        this.hiddencanedit = hiddencanedit;
    }

    public Map getCaptions() {
        return captions;
    }

    public void setCaptions(Map<String, String> captions) {
        this.captions = captions;
    }

    public Map getOrders() {
        return orders;
    }

    public void setOrders(Map<String, String> orders) {
        this.orders = orders;
    }

    public Map getDefaults() {
        return defaults;
    }

    public void setDefaults(Map<String, String> defaults) {
        this.defaults = defaults;
    }

    public Map getScripts() {
        return scripts;
    }

    public void setScripts(Map<String, String> scripts) {
        this.scripts = scripts;
    }

    public Map getLscripts() {
        return lscripts;
    }

    public void setLscripts(Map<String, String> lscripts) {
        this.lscripts = lscripts;
    }

    public Map getAddlists() {
        return addlists;
    }

    public void setAddlists(Map<String, String> addlists) {
        this.addlists = addlists;
    }

    public Map getDellists() {
        return dellists;
    }

    public void setDellists(Map<String, String> dellists) {
        this.dellists = dellists;
    }

    public Map getLists() {
        return lists;
    }

    public void setLists(Map<String, String[]> lists) {
        this.lists = lists;
    }

    public Map getRequireds() {
        return requireds;
    }

    public void setRequireds(Map<String, String> requireds) {
        this.requireds = requireds;
    }

    public Map getHtmlviews() {
        return htmlviews;
    }

    public void setHtmlviews(Map<String, String> htmlviews) {
        this.htmlviews = htmlviews;
    }

    public boolean validate() {
        return !order.equals("");
    }
}
