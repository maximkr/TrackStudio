package com.trackstudio.action.user;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.TreeSet;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import com.trackstudio.action.GeneralAction;
import com.trackstudio.action.TSDispatchAction;
import com.trackstudio.action.task.ChangeEvent;
import com.trackstudio.app.Preferences;
import com.trackstudio.app.Slider;
import com.trackstudio.app.adapter.AdapterManager;
import com.trackstudio.app.filter.FValue;
import com.trackstudio.app.filter.FilterSettings;
import com.trackstudio.app.filter.UserFValue;
import com.trackstudio.app.session.SessionContext;
import com.trackstudio.common.FieldMap;
import com.trackstudio.constants.UdfConstants;
import com.trackstudio.exception.UserException;
import com.trackstudio.form.UserListForm;
import com.trackstudio.kernel.cache.Action;
import com.trackstudio.kernel.cache.UserRelatedManager;
import com.trackstudio.secured.SecuredFilterBean;
import com.trackstudio.secured.SecuredUDFValueBean;
import com.trackstudio.secured.SecuredUserBean;
import com.trackstudio.tools.textfilter.HTMLEncoder;


public class UserListAction extends TSDispatchAction {
    private static final Log log = LogFactory.getLog(UserViewAction.class);

    public static class SortedLink {
        private volatile String sortBy;
        private volatile Boolean canView;
        private volatile int parts;

        public String getSortBy() {
            return sortBy;
        }

        public void setSortBy(String sortBy) {
            this.sortBy = sortBy;
        }

        public Boolean getCanView() {
            return canView;
        }

        public Boolean isCanView() {
            return canView;
        }

        public void setCanView(Boolean canView) {
            this.canView = canView;
        }

        public int getParts() {
            return parts;
        }

        public void setParts(int parts) {
            this.parts = parts;
        }

        public SortedLink(String sortBy, Boolean canView, int parts) {
            this.sortBy = sortBy;
            this.parts = parts;
            this.canView = canView;
        }
    }

    public static class SortedFactory {
        final FilterSettings settings;
        final List<String> curorder;
        volatile int parts;

        public SortedFactory(FilterSettings settings) {
            this.settings = settings;
            curorder = settings.getSortedBy();
            this.parts = 1;
        }

        public SortedLink getLink(String field, String filter, int size) {
            boolean value = settings.getSettings().getView().contains(filter);
            if (value)
                parts += size;
            return new SortedLink((curorder != null && curorder.contains('_' + field) ? "" : "_") + field, value, size);
        }

        public int getParts() {
            return parts;
        }
    }

    public ActionForward page(ActionMapping mapping, ActionForm form, HttpServletRequest request,
                              HttpServletResponse response) throws Exception {
        boolean w = lockManager.acquireConnection();
        try {
            log.trace("##########");
            UserListForm bf = (UserListForm) form;
            SessionContext sc = (SessionContext) request.getAttribute("sc");
            String id = GeneralAction.getInstance().userHeader(null, sc, request);
            SecuredUserBean currentUser = new SecuredUserBean(id, sc);
            sc.setCurrentSpace("UserListAction", request);
            sc.setRequestAttribute(request, "currentUser", currentUser);
            sc.setRequestAttribute(request, "id", currentUser.getId());
            sc.setRequestAttribute(request, "userId", currentUser.getId());
            String contextPath = request.getContextPath();

            String filterid = AdapterManager.getInstance().getSecuredFilterAdapterManager().getCurrentUserFilterId(sc, id);
            FilterSettings flthm = null;
            Object filterObject = sc.getAttribute("userfilter");
            if (filterObject != null) {
                flthm = (FilterSettings) filterObject;
                if (!flthm.getFilterId().equals(filterid) || !flthm.getObjectId().equals(id)) {
                    flthm = null;
                }
            }

            UserFValue val = AdapterManager.getInstance().getSecuredFilterAdapterManager().getUserFValue(sc, filterid).getFValue();
            FilterSettings originFilterSettings = new FilterSettings(val, id, filterid);
            if (flthm == null) {
                flthm = originFilterSettings;
                sc.setAttribute("userfilter", originFilterSettings);
            }
            SecuredFilterBean fltr = AdapterManager.getInstance().getSecuredFindAdapterManager().findFilterById(sc, filterid);
            sc.setRequestAttribute(request, "filter", fltr);
            sc.setRequestAttribute(request, "filterId", fltr.getId());
            sc.setRequestAttribute(request, "filterName", fltr.getName());
            sc.setRequestAttribute(request, "filterDescription", fltr.getDescription());
            HTMLEncoder ec = new HTMLEncoder(fltr.getName());
            ec.replace("'", "\\'");
            ec.replace("\"", "\\\"");
            ec.replace("\r\n", " ");
            ec.replace("\n", " ");
            String title = request.getAttribute("title") != null ? (String) request.getAttribute("title") : "";
            sc.setRequestAttribute(request, "title", title + " - " + ec.toString());

            boolean userUDFView = flthm.getSettings().needFilterUDF() && !currentUser.getUDFValuesList().isEmpty();

            if (bf.getSliderPage() != null && bf.getSliderPage().length() != 0) {
                flthm.setCurrentPage(Integer.parseInt(bf.getSliderPage()));
                originFilterSettings.setCurrentPage(Integer.parseInt(bf.getSliderPage()));
            }

            if (bf.getSliderOrder() != null && bf.getSliderOrder().length() != 0) {
                flthm.setSortedBy(FValue.parseFilterValue(bf.getSliderOrder()));
                originFilterSettings.setSortedBy(FValue.parseFilterValue(bf.getSliderOrder()));
            }

            Slider<SecuredUserBean> usl = AdapterManager.getInstance().getSecuredUserAdapterManager().getUserList(sc, id, (UserFValue) flthm.getSettings(), flthm.getCurrentPage(), userUDFView, flthm.getSortedBy());
            usl.setAll(bf.isAll(), sc.getLocale());
            sc.setRequestAttribute(request, "headerSlider", usl.drawSlider(contextPath + "/UserListAction.do?method=page&amp;id=" + id, "span", "floatlink"));
            sc.setRequestAttribute(request, "slider", usl.drawSlider(contextPath + "/UserListAction.do?method=page&amp;id=" + id, "div", "slider"));
            sc.setRequestAttribute(request, "sliderSize", usl.getColSize());

            List<SecuredUserBean> userList = usl.getCol();


            //UserListView tlvw = new UserListView(currentUser, filterid, userList, contextPath);

            SortedFactory fact = new SortedFactory(originFilterSettings);

            ArrayList<SecuredFilterBean> list = new ArrayList<SecuredFilterBean>(new TreeSet<SecuredFilterBean>(AdapterManager.getInstance().getSecuredFilterAdapterManager().getUserFilterList(sc, id)));
            ArrayList<SecuredFilterBean> filters = new ArrayList<SecuredFilterBean>();
            ArrayList<SecuredFilterBean> additionalFilters = new ArrayList<SecuredFilterBean>();
            for (SecuredFilterBean f : list) {
                if (Preferences.showInToolbar(f.getPreferences()))
                    filters.add(f);
                else
                    additionalFilters.add(f);
            }
            sc.setRequestAttribute(request, "filters", filters);
            sc.setRequestAttribute(request, "additionalFilters", additionalFilters);

            sc.setRequestAttribute(request, "headerLogin", fact.getLink(FieldMap.USER_LOGIN.getFieldKey(), FieldMap.USER_LOGIN.getFilterKey(), 2));

            sc.setRequestAttribute(request, "headerCompany", fact.getLink(FieldMap.USER_COMPANY.getFieldKey(), FieldMap.USER_COMPANY.getFilterKey(), 2));
            sc.setRequestAttribute(request, "headerStatus", fact.getLink(FieldMap.USER_STATUS.getFieldKey(), FieldMap.USER_STATUS.getFilterKey(), 2));
            sc.setRequestAttribute(request, "headerEmail", fact.getLink(FieldMap.USER_EMAIL.getFieldKey(), FieldMap.USER_EMAIL.getFilterKey(), 2));
            sc.setRequestAttribute(request, "headerTel", fact.getLink(FieldMap.USER_TEL.getFieldKey(), FieldMap.USER_TEL.getFilterKey(), 2));
            sc.setRequestAttribute(request, "headerExpireDate", fact.getLink(FieldMap.USER_EXPIREDATE.getFieldKey(), FieldMap.USER_EXPIREDATE.getFilterKey(), 2));
            sc.setRequestAttribute(request, "headerLocale", fact.getLink(FieldMap.USER_LOCALE.getFieldKey(), FieldMap.USER_LOCALE.getFilterKey(), 2));
            sc.setRequestAttribute(request, "headerTimezone", fact.getLink(FieldMap.USER_TIMEZONE.getFieldKey(), FieldMap.USER_TIMEZONE.getFilterKey(), 2));
            sc.setRequestAttribute(request, "headerActive", fact.getLink(FieldMap.USER_ACTIVE.getFieldKey(), FieldMap.USER_ACTIVE.getFilterKey(), 2));
            sc.setRequestAttribute(request, "headerChildrenCount", fact.getLink(FieldMap.USER_CHILDCOUNT.getFieldKey(), FieldMap.USER_CHILDCOUNT.getFilterKey(), 1));
            sc.setRequestAttribute(request, "headerChildrenAllowed", fact.getLink(FieldMap.USER_CHILDALLOWED.getFieldKey(), FieldMap.USER_CHILDALLOWED.getFilterKey(), 1));
        sc.setRequestAttribute(request, "headerParent", fact.getLink(FieldMap.USER_PARENT.getFieldKey(), FieldMap.USER_PARENT.getFilterKey(), 2));
        sc.setRequestAttribute(request, "headerTemplate", fact.getLink(FieldMap.USER_TEMPLATE.getFieldKey(), FieldMap.USER_TEMPLATE.getFilterKey(), 2));


            SortedLink userNameLnk = fact.getLink(FieldMap.USER_NAME.getFieldKey(), FieldMap.USER_NAME.getFilterKey(), 8);
            SortedLink fullPathLnk = fact.getLink(FieldMap.FULLPATH.getFieldKey(), FieldMap.FULLPATH.getFilterKey(), 8);


            HashMap<String, SortedLink> udfHeaderLink = new HashMap<String, SortedLink>();
            HashMap<String, String> udfHeaderCaption = new HashMap<String, String>();
            List<String> udfs = new ArrayList<String>();

            if (userUDFView) {
                for (SecuredUDFValueBean udf : currentUser.getUDFValuesList()) {
                    if (originFilterSettings.getSettings().getView().contains(FValue.UDF + udf.getId())) {
                        udfHeaderLink.put(udf.getId(), fact.getLink(FValue.UDF_SORT + udf.getId(), FValue.UDF + udf.getId(), 2));
                        udfHeaderCaption.put(udf.getId(), udf.getCaption());
                        udfs.add(udf.getId());
                    }
                }
            }
            sc.setRequestAttribute(request, "udfHeaderLink", udfHeaderLink);
            sc.setRequestAttribute(request, "udfHeaderCaption", udfHeaderCaption);
            sc.setRequestAttribute(request, "udfs", udfs);

            Cookie[] cook = request.getCookies();
            String selectedIds = "";
            if (cook != null) {

                for (Cookie c : cook) {
                    log.debug(c.getName() + c.getValue());
                    if (c.getName().equals("_selectedId") && c.getValue() != null && c.getValue().length() > 0) {
                        selectedIds = c.getValue();
                    }
                }
            }
            ArrayList<SecuredUserBean> selected = new ArrayList<SecuredUserBean>();
            for (String s : selectedIds.split(UdfConstants.SPLIT_SEPARATOR)) {
                SecuredUserBean t = AdapterManager.getInstance().getSecuredFindAdapterManager().findUserById(sc, s);
                if (t != null) selected.add(t);

            }
            sc.setRequestAttribute(request, "selectedIds", selected);
            sc.setRequestAttribute(request, "userLines", userList);
            int sizeOfPart = 100 / fact.getParts();
            int freePercents = 100 - fact.getParts() * sizeOfPart;
            if (fullPathLnk.canView && userNameLnk.canView) {
                int type = freePercents >> 1;
                fullPathLnk.setParts(8 * sizeOfPart + type);
                userNameLnk.setParts(8 * sizeOfPart + (freePercents - type));
                sc.setRequestAttribute(request, "headerFullPath", fullPathLnk);
                sc.setRequestAttribute(request, "headerName", userNameLnk);
            } else {
                fullPathLnk.setParts(8 * sizeOfPart + freePercents);
                userNameLnk.setParts(8 * sizeOfPart + freePercents);
                sc.setRequestAttribute(request, "headerFullPath", fullPathLnk);
                sc.setRequestAttribute(request, "headerName", userNameLnk);
            }
            sc.setRequestAttribute(request, "sizeOfPart", sizeOfPart);
            if (sc.allowedByUser(id)) {
                if (sc.canAction(Action.createUser, id)) {

                    sc.setRequestAttribute(request, "canCreateUser", Boolean.TRUE);
                } else
                    sc.setRequestAttribute(request, "canCreateUser", Boolean.FALSE);
            } else {
                sc.setRequestAttribute(request, "canCreateUser", Boolean.FALSE);
            }

            sc.setRequestAttribute(request, "totalChildrenCount", usl.getTotalChildrenCount());
            sc.setRequestAttribute(request, "canDeleteUsers", sc.canAction(Action.deleteUser, id));

            sc.setRequestAttribute(request, "canCutUser", sc.canAction(Action.cutPasteUser, id));
            sc.setRequestAttribute(request, "canPasteUser", sc.getAttribute("USERS") != null && sc.getAttribute("USERS").toString().length() > 0 && currentUser.canManage() && sc.canAction(Action.createUser, id));
            sc.setRequestAttribute(request, "canManageUserPrivateFilters", fltr.canView() && sc.canAction(Action.manageUserPrivateFilters, id));
            sc.setRequestAttribute(request, "filterName", fltr.getName());
            sc.setAttribute("userfilter", flthm);
            selectUserTab(sc, id, "tabUserList", request);
            return mapping.findForward("userListJSP");
        } finally {
            if (w) lockManager.releaseConnection();
        }

    }

    public ActionForward deleteUsers(ActionMapping mapping, ActionForm form, HttpServletRequest request,
                                     HttpServletResponse response) throws Exception {
        boolean w = lockManager.acquireConnection();
        try {
            UserListForm bf = (UserListForm) form;
            SessionContext sc = (SessionContext) request.getAttribute("sc");
            String id = GeneralAction.getInstance().userHeader(null, sc, request);
            List<String> usersToClearOperation = new ArrayList<String>();
            if (sc.getAttribute("USERS") != null && sc.getAttribute("USER_OPERATION") != null) {
                String ids = (String) sc.getAttribute("USERS");
                for (String sid : ids.split(UdfConstants.SPLIT_SEPARATOR)) {
                    SecuredUserBean sub = AdapterManager.getInstance().getSecuredFindAdapterManager().findUserById(sc, sid);
                    if (sub != null) usersToClearOperation.add(sub.getLogin());
                }
            }

            String gotoUser = null;
            String ids = bf.getCollector();
            if (ids != null) {


                UserException uexception = null;

                List<String> usersToDelete = new ArrayList<String>();
                for (String anUserId : ids.split(UdfConstants.SPLIT_SEPARATOR)) {
                    try {
                        SecuredUserBean bean = AdapterManager.getInstance().getSecuredFindAdapterManager().findUserById(sc, anUserId);
                        if (bean != null && bean.isDelete()) {
                            if (anUserId.equals(id)) gotoUser = bean.getManagerId();
                            if (!anUserId.equals(sc.getUserId()) && sc.canAction(Action.deleteUser, anUserId)) {
                                String userLogin = bean.getLogin();
                                AdapterManager.getInstance().getSecuredUserAdapterManager().deleteUser(sc, anUserId);
                                usersToDelete.add(userLogin);
                            }
                        }
                    } catch (UserException ue) {
                        if (uexception == null) {
                            uexception = ue;
                        } else {
                            uexception.addActionMessages(ue.getActionMessages());
                        }
                    }
                }
                sc.setAttribute("jsEvent", new ChangeEvent(ChangeEvent.EVENT_USER_DELETED,
                        new SecuredUserBean(id, sc).getLogin(), new String[]{}, new String[]{},
                        usersToDelete.toArray(new String[]{}), new String[]{}, new String[]{},
                        new String[]{}, usersToClearOperation.toArray(new String[]{}), new String[]{}, ""));

                if (uexception != null) throw uexception;
            }
            if (gotoUser != null) {
                bf.setId(gotoUser);
                bf.setMutable(false);
                sc.setRequestAttribute(request, "id", gotoUser);
            }
        } catch (UserException ue) {
            if (ue.getActionMessages() != null) {
                saveMessages(request, ue.getActionMessages());
                return mapping.getInputForward();
            } else
                throw ue;
        } finally {
            if (w) lockManager.releaseConnection();
        }

        return mapping.findForward("userListPage");
    }

    public ActionForward paste(ActionMapping mapping, ActionForm form, HttpServletRequest request,
                               HttpServletResponse response) throws Exception {
        boolean w = lockManager.acquireConnection();
        try {
            log.trace("##########");
            UserListForm ulf = (UserListForm) form;
            SessionContext sc = (SessionContext) request.getAttribute("sc");
            String userid = GeneralAction.getInstance().userHeader(null, sc, request);
            if (sc.getAttribute("USERS") != null && sc.getAttribute("USER_OPERATION") != null) {
                String ids = (String) sc.getAttribute("USERS");
                TreeSet<String> root = new TreeSet<String>();
                for (String id : ids.split(UdfConstants.SPLIT_SEPARATOR)) {
                    if (root.isEmpty()) root.add(id);
                    else {
                        boolean clear = true;
                        UserRelatedManager userRelatedManager = UserRelatedManager.getInstance();
                        for (String rt : root) {
                            if (userRelatedManager.hasPath(id, rt)) {
                                clear = false;
                                if (userRelatedManager.getUserChain(id, rt) != null) {
                                    // id is parent of rt, need to replace rt
                                    root.remove(rt);
                                    clear = true;
                                    break;
                                } else {
                                    // rt is parent of id, it's ok
                                    break;
                                }
                            }
                        }
                        if (clear) root.add(id);
                    }
                }


                AdapterManager.getInstance().getSecuredUserAdapterManager().pasteUsers(sc, ulf.getId(), root.toArray(new String[]{}));

                List<String> usersToClearOperation = new ArrayList<String>();
                for (String sid : ids.split(UdfConstants.SPLIT_SEPARATOR)) {
                    SecuredUserBean sub = AdapterManager.getInstance().getSecuredFindAdapterManager().findUserById(sc, sid);
                    if (sub != null) usersToClearOperation.add(sub.getLogin());
                }

                List<String> userLoginsToPaste = new ArrayList<String>();
                List<String> userNamesToPaste = new ArrayList<String>();
                List<String> iconsToPaste = new ArrayList<String>();
                List<String> actionsToPaste = new ArrayList<String>();
                List<String> userIdsToPaste = new ArrayList<String>();
                String context = request.getContextPath();
                for (String sid : root) {
                    SecuredUserBean sub = AdapterManager.getInstance().getSecuredFindAdapterManager().findUserById(sc, sid);
                    String imageServlet = "/ImageServlet/" + GeneralAction.SERVLET_KEY;
                    if (sub != null) {
                        userLoginsToPaste.add(sub.getLogin());
                        userNamesToPaste.add(sub.getName());
                        iconsToPaste.add(context + imageServlet + "/html/xtree/images/userNode.gif");
                        actionsToPaste.add("javascript:{self.top.frames[1].location = '" + context + "/UserAction.do?method=page&id=" + sub.getId() + "&thisframe=true';; active='_" + sub.isActive() + "';}");
                        userIdsToPaste.add(sid);
                    }
                }
                String firstId = root.first();
                SecuredUserBean parent = AdapterManager.getInstance().getSecuredFindAdapterManager().findUserById(sc, firstId).getParent();
                String parentHint = parent == null ? "" : parent.getLogin();
                sc.setAttribute("jsEvent", new ChangeEvent(ChangeEvent.EVENT_USER_PASTED_CUT, parentHint,
                        iconsToPaste.toArray(new String[]{}), new String[]{},
                        userLoginsToPaste.toArray(new String[]{}), userNamesToPaste.toArray(new String[]{}),
                        actionsToPaste.toArray(new String[]{}), userIdsToPaste.toArray(new String[]{}), usersToClearOperation.toArray(new String[]{}), new String[]{}, ""));

                sc.removeAttribute("USERS");
            }
            return mapping.findForward("userListPage");
        } finally {
            if (w) lockManager.releaseConnection();
        }

    }

    public ActionForward cut(ActionMapping mapping, ActionForm form, HttpServletRequest request,
                             HttpServletResponse response) throws Exception {
        boolean w = lockManager.acquireConnection();
        try {
            log.trace("##########");

            UserListForm sf = (UserListForm) form;
            SessionContext sc = (SessionContext) request.getAttribute("sc");

            List<String> usersToClearOperation = new ArrayList<String>();
            if (sc.getAttribute("USERS") != null && sc.getAttribute("USER_OPERATION") != null) {
                String ids = (String) sc.getAttribute("USERS");
                for (String id : ids.split(UdfConstants.SPLIT_SEPARATOR)) {
                    SecuredUserBean sub = AdapterManager.getInstance().getSecuredFindAdapterManager().findUserById(sc, id);
                    if (sub != null) usersToClearOperation.add(sub.getLogin());
                }
            }

            String ids = sf.getCollector();
            if (ids != null) {
                sc.setAttribute("USERS", ids);
                sc.setAttribute("USER_OPERATION", "CUT");

                List<String> usersToCut = new ArrayList<String>();
                for (String id : ids.split(UdfConstants.SPLIT_SEPARATOR)) {
                    SecuredUserBean sub = AdapterManager.getInstance().getSecuredFindAdapterManager().findUserById(sc, id);
                    if (sub != null) usersToCut.add(sub.getLogin());
                }

                sc.setAttribute("jsEvent", new ChangeEvent(ChangeEvent.EVENT_USER_CUT,
                        "", new String[]{}, new String[]{},
                        usersToCut.toArray(new String[]{}), new String[]{},
                        new String[]{}, new String[]{}, usersToClearOperation.toArray(new String[]{}), new String[]{}, ""));
            }
            if (sf.getId() != null && sf.getId().length() > 0 && new SecuredUserBean(sf.getId(), sc).hasChildren())
                return mapping.findForward("userListPage");

            return mapping.findForward("userListPage");

        } finally {
            if (w) lockManager.releaseConnection();
        }
    }
}