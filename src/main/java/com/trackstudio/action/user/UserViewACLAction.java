package com.trackstudio.action.user;

import java.util.ArrayList;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.trackstudio.app.adapter.AdapterManager;
import com.trackstudio.app.session.SessionContext;
import com.trackstudio.kernel.lock.LockManager;
import com.trackstudio.secured.SecuredPrstatusBean;
import com.trackstudio.secured.SecuredUserBean;
import com.trackstudio.tools.EggBasket;

public class UserViewACLAction  {
    private static Log log = LogFactory.getLog(UserViewACLAction.class);
    private static final LockManager lockManager = LockManager.getInstance();

    public void viewAcl(SessionContext sc, String id, HttpServletRequest request) throws Exception {
        boolean w = lockManager.acquireConnection();
        try {
            log.trace("##########");
            sc.setRequestAttribute(request, "acls", SecuredUserBean.getPrstatusForTaskId(sc, id));
            ArrayList<SecuredUserBean> users = AdapterManager.getInstance().getSecuredAclAdapterManager().getAssignedUserList(sc, id);
            EggBasket<SecuredUserBean, SecuredPrstatusBean> userAcls = new EggBasket<SecuredUserBean, SecuredPrstatusBean>();
            for (SecuredUserBean ub : users) {
                if (ub.isOnSight()) {
                    for (SecuredPrstatusBean ps : AdapterManager.getInstance().getSecuredAclAdapterManager().getUserAllowedPrstatusList(sc, ub.getId(), id)) {
                        userAcls.putItem(ub, ps);
                    }
                }
            }
            sc.setRequestAttribute(request, "userAcls", userAcls);
        } finally {
            if (w) lockManager.releaseConnection();
        }

    }
}
