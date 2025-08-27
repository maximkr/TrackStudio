package com.trackstudio.secured;

import java.util.ArrayList;
import java.util.HashMap;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.trackstudio.app.CalculatedValue;
import com.trackstudio.app.session.SessionContext;
import com.trackstudio.constants.UdfConstants;
import com.trackstudio.exception.GranException;
import com.trackstudio.kernel.cache.UDFCacheItem;
import com.trackstudio.kernel.manager.KernelManager;
import com.trackstudio.soap.bean.UdfBean;
import com.trackstudio.tools.PropertyContainer;
import com.trackstudio.tools.compare.IUdfSort;

import net.jcip.annotations.ThreadSafe;

/**
 * Bean which represents custom field
 */
@ThreadSafe
public abstract class SecuredUDFBean extends Secured implements IUdfSort {

    //    private static NumberFormat nf = new DecimalFormat("####");
    protected static final Log log = LogFactory.getLog(CalculatedValue.class);

    protected final String udfId;
    protected final String caption;
    protected final String referencedbycaption;
    protected final String captionEx;
    protected final Integer order;
    protected final String def;
    protected final boolean required;
    protected final boolean htmlview;
    protected final Integer type;
    protected final String script;
    protected final String lookupscript;
    protected final boolean lookuponly;
    protected final boolean cachevalues;
    protected final String initial;
    protected volatile String taskId;
    protected volatile String userId;
    protected volatile String workflowId;
    protected volatile String udfSourceId;


    public SecuredUDFBean(UDFCacheItem u, SessionContext sc) {
        this.udfId = u.getId();
        this.sc = sc;
        this.def = u.getDef();
        this.captionEx = u.buildName();
        this.caption = u.getCaption();
        this.referencedbycaption = u.getReferencedbycaption();
        this.order = u.getOrder();
        this.required = u.getRequired() != null && u.getRequired() == 1;
        this.htmlview = u.getHtmlview() != null && u.getHtmlview() == 1;
        this.lookuponly = u.getLookuponly() != null && u.getLookuponly() == 1;
        this.cachevalues = u.getCachevalues() != null && u.getCachevalues() == 1;
        this.type = u.getType();

        this.script = u.getScript();
        this.lookupscript = u.getLookupscript();

        this.initial = u.getInitialtaskId() != null && u.getType() == UdfConstants.TASK ? u.getInitialtaskId() : u.getInitialuserId() != null && u.getType() == UdfConstants.USER ? u.getInitialuserId() : null;
        this.taskId = u.getTaskId();
        this.workflowId = u.getWorkflowId();
        this.userId = u.getUserId();
        this.udfSourceId = u.getUdfsourceId();
    }


    public String getTaskId() {
        return taskId;
    }

    public String getUserId() {
        return userId;
    }

    public String getWorkflowId() {
        return workflowId;
    }

    public String getReferencedbycaption() {
        return referencedbycaption;
    }

    public String getId() {
        return udfId;
    }

    public String getCaption() {
        return caption;
    }

    public String getCaptionEx() {
        return captionEx;
    }


    public Integer getOrder() {
        return order;
    }

    public String getDefaultUDF() throws GranException {
        return KernelManager.getUdf().getLocalizedDefaultValue(getId(), getSecure().getLocale(), getSecure().getTimezone());
    }

    public boolean isRequired() {
        return required;
    }

    public boolean isHtmlview() {
        return htmlview;
    }

    public Integer getType() {
        return type;
    }


    public String getUdfSourceId() {
        return udfSourceId;
    }

    public String getUdfId() {
        return udfId;
    }

    public abstract Object getLookupscriptCalc(Secured o) throws GranException;


    public String getScript() {
        return script;
    }


    public String getLookupscript() {
        return lookupscript;
    }

    protected PropertyContainer getContainer() {
        PropertyContainer pc = container.get();
        if (pc != null)
            return pc; // object in cache, return it

        PropertyContainer newPC = new PropertyContainer();
        newPC.put(getOrder()).put(getCaptionEx()).put(getId());

        if (container.compareAndSet(null, newPC)) // try to update
            return newPC; // we can update - return loaded value
        else
            return container.get(); // some other thread already updated it - use saved value
    }

    public HashMap<String, String> getUL() throws GranException {
        return KernelManager.getUdf().getUdflist(getId());
    }

    public ArrayList<String> getUdflist() throws GranException {
        HashMap<String, String> hm = getUL();
        ArrayList<String> list = new ArrayList<String>();
        if (hm == null)
            return list;
        for (String key : hm.keySet()) {
            list.add(hm.get(key) + '\n' + key);
        }
        return list;
    }

    public String getULString() throws GranException {
        String r = "";
        HashMap<String, String> ul = getUL();
        if (ul != null) {
            for (String o : ul.keySet()) {
                r += ul.get(o) + "<BR>";
            }
        }
        return r;
    }

    public UdfBean getSOAP() throws GranException {
        UdfBean bean = new UdfBean();
        bean.setCaption(caption);
        bean.setDef(def);
        bean.setOrder(order != null ? order : 0);
        bean.setRequired(required);
        bean.setHtmlview(htmlview);
        bean.setType(type != null ? type : 0);
        bean.setUdfId(udfId);
        bean.setUdfSourceId(udfSourceId);
        bean.setScript(script);
        bean.setLookupscript(lookupscript);
        bean.setLookuponly(lookuponly);
        bean.setInitial(initial);
        bean.setCachevalues(cachevalues);
        return bean;
    }


    public boolean isLookuponly() {
        return lookuponly;
    }

    public boolean isCachevalues() {
        return cachevalues;
    }

    public String getInitial() {
        return initial;
    }

    @Override
    public String toString() {
        return "SecuredUDFBean{" +
                "udfId='" + udfId + '\'' +
                ", caption='" + caption + '\'' +
                ", taskId='" + taskId + '\'' +
                ", userId='" + userId + '\'' +
                ", workflowId='" + workflowId + '\'' +
                '}';
    }
}
