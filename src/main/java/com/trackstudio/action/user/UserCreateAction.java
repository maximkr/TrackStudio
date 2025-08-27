package com.trackstudio.action.user;

import java.util.ArrayList;
import java.util.Collections;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import com.trackstudio.action.TSDispatchAction;
import com.trackstudio.app.PageNaming;
import com.trackstudio.app.adapter.AdapterManager;
import com.trackstudio.app.session.SessionContext;
import com.trackstudio.containers.PrstatusListItem;
import com.trackstudio.kernel.cache.Action;
import com.trackstudio.secured.SecuredPrstatusBean;

public class UserCreateAction extends TSDispatchAction {

    public ActionForward page(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        boolean w = lockManager.acquireConnection();
        try {
            log.trace("##########");
            SessionContext sc = (SessionContext) request.getAttribute("sc");
            response.addCookie(createCookie(sc.getCurrentSpace(), "UserCreateAction"));
            String id = request.getParameter("id");
            response.addCookie(createCookie(sc.getCurrentSpace(), "UserCreateAction"));

            sc.setRequestAttribute(request, "id", id);
            if (sc.allowedByUser(id)) {
                if (sc.canAction(Action.createUser, id)) {
                    //todo id?

                    ArrayList<SecuredPrstatusBean> availablePrstatusList = AdapterManager.getInstance().getSecuredPrstatusAdapterManager().getCreatablePrstatusList(sc, id);
                    ArrayList<PrstatusListItem> listPr = new ArrayList<PrstatusListItem>();
                    for (SecuredPrstatusBean n : availablePrstatusList) {
                        listPr.add(new PrstatusListItem(n.getId(), n.getName()));
                    }
                    Collections.sort(listPr);
                    sc.setRequestAttribute(request, "prstatus", listPr);
                    sc.setRequestAttribute(request, "canCreateUser", true);
                } else
                    sc.setRequestAttribute(request, "canCreateUser", false);
            } else {
                sc.setRequestAttribute(request, "canCreateUser", false);
            }
            sc.setRequestAttribute(request, "currentTab", PageNaming.TAB_USER_CUSTOM_FIELD_PROPERTIES);
            sc.setRequestAttribute(request, "tileId", "createUser");
            return mapping.findForward("createUserTileJSP");
        } finally {
            if (w) lockManager.releaseConnection();
        }

    }


    public ActionForward create(ActionMapping mapping, ActionForm form, HttpServletRequest request,
                                HttpServletResponse response) throws Exception {
        log.trace("##########");
        return mapping.findForward("userEditPage");
    }
}
