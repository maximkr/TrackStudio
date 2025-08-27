package com.trackstudio.action.user;

import java.util.ArrayList;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.trackstudio.app.adapter.AdapterManager;
import com.trackstudio.app.session.SessionContext;
import com.trackstudio.kernel.lock.LockManager;
import com.trackstudio.secured.SecuredTaskAclBean;
import com.trackstudio.secured.SecuredUserAclBean;

public class StatusViewACLAction {
    private static Log log = LogFactory.getLog(StatusViewACLAction.class);
    private static final LockManager lockManager = LockManager.getInstance();


    public void viewACL(String prstatusId, HttpServletRequest request) throws Exception {
        boolean w = lockManager.acquireConnection();
        try {
            log.trace("##########");
            SessionContext sc = (SessionContext) request.getAttribute("sc");
            ArrayList<SecuredTaskAclBean> taskAcls = AdapterManager.getInstance().getSecuredAclAdapterManager().getGroupTaskAclList(sc, prstatusId);
            ArrayList<SecuredUserAclBean> userAcls = AdapterManager.getInstance().getSecuredAclAdapterManager().getGroupUserAclList(sc, prstatusId);
            sc.setRequestAttribute(request, "acls", taskAcls);
            sc.setRequestAttribute(request, "userAcls", userAcls);
        } finally {
            if (w) lockManager.releaseConnection();
        }

    }
}
