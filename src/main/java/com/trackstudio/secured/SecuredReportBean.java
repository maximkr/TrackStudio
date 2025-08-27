package com.trackstudio.secured;

import java.util.HashMap;
import java.util.List;
import java.util.StringTokenizer;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.trackstudio.app.adapter.AdapterManager;
import com.trackstudio.app.session.SessionContext;
import com.trackstudio.exception.GranException;
import com.trackstudio.kernel.cache.Action;
import com.trackstudio.model.Report;
import com.trackstudio.soap.bean.ReportBean;
import com.trackstudio.tools.PropertyContainer;

import net.jcip.annotations.Immutable;

/**
 * Bean which represents report
 */
@Immutable
public class SecuredReportBean extends Secured {

    private static final Log log = LogFactory.getLog(SecuredReportBean.class);

    private final String id;
    private final String name;
    private final boolean priv;
    private final String params;
    private final String rtype;

    private final String taskId;
    private final String filterId;
    private final String ownerId;

    public SecuredReportBean(Report report, SessionContext sc) throws GranException {
        log.trace("SecuredReportBean(" + report + ", " + sc + ')');
        this.id = report.getId();
        this.sc = sc;
        this.taskId = report.getTask() != null ? report.getTask().getId() : null;
        this.ownerId = report.getOwner() != null ? report.getOwner().getId() : null;
        this.params = report.getParams();
        boolean p = report.getPriv() == 1;
        if (sc.taskOnSight(taskId) && (!p || sc.getUserId().equals(ownerId))) {
            this.name = report.getName();
            this.priv = report.getPriv() == 1;
            this.rtype = report.getRtype();
            this.filterId = report.getFilter() != null ? report.getFilter().getId() : null;
        } else {
            this.name = null;
            this.priv = true;
            this.rtype = null;
            this.filterId = null;
        }
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }


    public boolean isPriv() {
        return priv;
    }

    public boolean getPriv() {
        return priv;
    }

    public String getParams() {
        return params;
    }

    public HashMap<String, String> getParamsHashMap() {
        HashMap<String, String> hashMap = new HashMap<String, String>();
        if (params != null && params.length() != 0) {
            StringTokenizer st = new StringTokenizer(params,";", false);
            while (st.hasMoreTokens()) {
                String s = st.nextToken();
                if (s != null && s.length() != 0) {
                    String key = s.substring(0,s.indexOf("="));
                    String value = s.substring(s.indexOf("=")+"=".length(), s.length());
                    hashMap.put(key, value);
                }
            }
        }
            return hashMap;
        }

    public String getRtype() throws GranException {
        return rtype;
    }

    public String getRtypeText() throws GranException {
        return com.trackstudio.app.report.birt.Report.getReportTypeFactory(sc, rtype);
    }

    public SecuredTaskBean getTask() throws GranException {
        return new SecuredTaskBean(taskId, sc);
    }

    public SecuredFilterBean getFilter() throws GranException {
        return AdapterManager.getInstance().getSecuredFindAdapterManager().findFilterById(sc, this.filterId);
    }

    public SecuredUserBean getOwner() throws GranException {
        return new SecuredUserBean(ownerId, sc);
    }

    protected PropertyContainer getContainer() {
        PropertyContainer pc = container.get();
        if (pc != null)
            return pc; // object in cache, return it

        PropertyContainer newPC = new PropertyContainer();
        newPC.put(getName()).put(getId());

        if (container.compareAndSet(null, newPC)) // try to update
            return newPC; // we can update - return loaded value
        else
            return container.get(); // some other thread already updated it - use saved value
    }

    public boolean isAllowedByACL() throws GranException {
        return getOwner().isAllowedByACL();
    }

    public boolean canManage() throws GranException {
        return isAllowedByACL() && sc.canAction(Action.managePrivateReports, getTaskId());
    }

    public boolean canView() throws GranException {
        return priv ? sc.getUserId().equals(getOwnerId()) : getFilter().canView() && getTask().canView();
    }

    public ReportBean getSOAP() throws GranException {
        ReportBean bean = new ReportBean();
        bean.setFilterId(filterId);
        bean.setId(id);
        bean.setName(name);
        bean.setOwnerId(ownerId);
        bean.setParams(params);
        bean.setPriv(priv);
        bean.setRtype(rtype);
        bean.setTaskId(taskId);
        return bean;
    }

    public String getTaskId() {
        return taskId;
    }

    public String getFilterId() {
        return filterId;
    }

    public String getOwnerId() {
        return ownerId;
    }

}
