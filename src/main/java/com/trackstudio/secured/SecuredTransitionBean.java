package com.trackstudio.secured;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.trackstudio.app.adapter.AdapterManager;
import com.trackstudio.app.session.SessionContext;
import com.trackstudio.exception.GranException;
import com.trackstudio.model.Transition;
import com.trackstudio.soap.bean.TransitionBean;
import com.trackstudio.tools.PropertyContainer;

import net.jcip.annotations.Immutable;

/**
 * Bean which represents transition
 */
@Immutable
public class SecuredTransitionBean extends Secured {

    private static final Log log = LogFactory.getLog(SecuredTransitionBean.class);

    private final String id;
    private final String startId;
    private final String finishId;
    private final String mstatusId;

    public SecuredTransitionBean(Transition transition, SessionContext secure) throws GranException {
        this.id = transition.getId();
        this.sc = secure;
        this.mstatusId = transition.getMstatus() != null ? transition.getMstatus().getId() : null;
        this.startId = transition.getStart().getId();
        this.finishId = transition.getFinish().getId();
    }

    public String getId() {
        return id;
    }

    public SecuredStatusBean getStart() throws GranException {
        return AdapterManager.getInstance().getSecuredFindAdapterManager().findStatusById(sc, this.startId);
    }

    public SecuredStatusBean getFinish() throws GranException {
        return AdapterManager.getInstance().getSecuredFindAdapterManager().findStatusById(sc, this.finishId);
    }


    public SecuredMstatusBean getMstatus() throws GranException {
        return AdapterManager.getInstance().getSecuredFindAdapterManager().findMstatusById(sc, this.mstatusId);
    }

    public boolean isAllowedByACL() throws GranException {
        return getMstatus().isAllowedByACL();
    }

    public boolean canManage() throws GranException {
        return getMstatus().canManage();
    }

    public boolean canView() throws GranException {
        return getMstatus().canView();
    }

    public TransitionBean getSOAP()
            throws GranException {
        TransitionBean bean = new TransitionBean();
        bean.setId(id);
        bean.setFinishId(finishId);
        bean.setMstatusId(mstatusId);
        bean.setStartId(startId);
        return bean;
    }

     protected PropertyContainer getContainer() {
         PropertyContainer pc = container.get();
         if (pc != null)
             return pc; // object in cache, return it

         PropertyContainer newPC = new PropertyContainer();
         try{
             newPC.put(getStart().getName()).put(getFinish().getName()).put(getId());
         } catch (GranException e){
             newPC.put(getId());
         }

         if (container.compareAndSet(null, newPC)) // try to update
             return newPC; // we can update - return loaded value
         else
             return container.get(); // some other thread already updated it - use saved value
    }


    public String getStartId() {
        return startId;
    }

    public String getFinishId() {
        return finishId;
    }

    public String getMstatusId() {
        return mstatusId;
    }
}
