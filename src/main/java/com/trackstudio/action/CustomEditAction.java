package com.trackstudio.action;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import javax.servlet.http.HttpServletRequest;

import org.apache.struts.actions.ForwardAction;

import com.trackstudio.app.UdfValue;
import com.trackstudio.app.adapter.AdapterManager;
import com.trackstudio.app.session.SessionContext;
import com.trackstudio.constants.CategoryConstants;
import com.trackstudio.constants.UdfConstants;
import com.trackstudio.constants.WorkflowConstants;
import com.trackstudio.exception.GranException;
import com.trackstudio.form.CustomForm;
import com.trackstudio.kernel.manager.KernelManager;
import com.trackstudio.secured.SecuredMstatusBean;
import com.trackstudio.secured.SecuredPrstatusBean;
import com.trackstudio.secured.SecuredTaskBean;
import com.trackstudio.secured.SecuredUDFBean;
import com.trackstudio.secured.SecuredUserBean;
import com.trackstudio.secured.SecuredWorkflowUDFBean;
import com.trackstudio.startup.I18n;
import com.trackstudio.tools.Pair;
import com.trackstudio.tools.PropertyComparable;
import com.trackstudio.tools.PropertyContainer;

public class CustomEditAction extends ForwardAction {

    public static final String DEFAULT_LIST_VALUE = "default value";

    public static class UdfBeanListItem extends PropertyComparable {
        public String id;
        public String name;
        public String type;

        public Integer order;

        public boolean canUpdate;


        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public boolean getCanUpdate() {
            return canUpdate;
        }

        public boolean isCanUpdate() {
            return canUpdate;
        }

        public void setCanUpdate(boolean canUpdate) {
            this.canUpdate = canUpdate;
        }

        public String getType() {
            return type;
        }

        public void setType(String type) {
            this.type = type;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }


        public Integer getOrder() {
            return order;
        }

        public void setOrder(Integer order) {
            this.order = order;
        }

        protected PropertyContainer getContainer() {
            PropertyContainer pc = container.get();
            if (pc != null)
                return pc; // object in cache, return it

            PropertyContainer newPC = new PropertyContainer();
            newPC.put(order).put(name).put(id);

            if (container.compareAndSet(null, newPC)) // try to update
                return newPC; // we can update - return loaded value
            else
                return container.get(); // some other thread already updated it - use saved value
        }

        public UdfBeanListItem(String id, String name, String type, Integer order, boolean canUpdate) {
            this.id = id;
            this.canUpdate = canUpdate;
            this.type = type;
            this.name = name;
            this.order = order;
        }

    }

    /**
     * This method searchs user id or task id
     * @param type udf type
     * @param initStr key
     * @param sc Session
     * @return object id
     * @throws GranException for necessery
     */
    public static String getInitial(int type, String initStr, SessionContext sc) throws GranException {
        String initial = "1";
        if (type == UdfConstants.TASK && initStr != null && initStr.length() != 0) {
            initial = AdapterManager.getInstance().getSecuredTaskAdapterManager().findTaskIdByQuickGo(sc, initStr.replace('#', ' ').trim());
        } else if (type == UdfConstants.USER && initStr != null && initStr.length() != 0) {
            initial = (AdapterManager.getInstance().getSecuredUserAdapterManager().findByLogin(sc, initStr) != null) ? AdapterManager.getInstance().getSecuredUserAdapterManager().findByLogin(sc, initStr).getId() : null;
        }
        return initial;
    }

    public static void fillEditForm(SessionContext sc, CustomForm form, HttpServletRequest request) throws GranException {
        putUdfTypes(sc, request);
        if (form.getCreateNewUdf() == null && form.getUdfId() != null && form.getUdfId().length() != 0) {
            SecuredUDFBean udf = AdapterManager.getInstance().getSecuredFindAdapterManager().findUDFById(sc, form.getUdfId());
            form.setCaption(udf.getCaption());
            form.setReferencedbycaption(udf.getReferencedbycaption());
            form.setOrder(udf.getOrder() == null ? "0" : udf.getOrder().toString());
            form.setDef(udf.getDefaultUDF());
            form.setScript(udf.getScript());
            form.setRequire(udf.isRequired() ? "true" : null);
            form.setHtmlview(udf.isHtmlview() ? "true" : null);
            form.setLscript(udf.getLookupscript());
            form.setLookuponly(udf.isLookuponly() ? "true" : null);
            form.setCachevalues(udf.isCachevalues() ? "true" : null);
            form.setCalculen(udf.getScript() != null ? "true" : null);
            form.setLookupen(udf.getLookupscript() != null ? "true" : null);
            form.setType(udf.getType());
            if (udf.getType() == UdfConstants.TASK && udf.getInitial() != null) {
                SecuredTaskBean tid = AdapterManager.getInstance().getSecuredFindAdapterManager().findTaskById(sc, udf.getInitial());
                if (tid != null) form.setInitial('#' + tid.getNumber());
            } else if (udf.getType() == UdfConstants.USER && udf.getInitial() != null) {
                SecuredUserBean user = AdapterManager.getInstance().getSecuredFindAdapterManager().findUserById(sc, udf.getInitial());
                if (user != null) form.setInitial(user.getLogin());
            } else {
                form.setInitial(null);
            }
            Map map = udf.getUL();
            ArrayList<Pair> udflist = new ArrayList<Pair>();
            for (Object o : map.keySet()) {
                String key = (String) o;
                udflist.add(new Pair(key, (String) map.get(key)));
                form.setValue(key, null);
            }
            Collections.sort(udflist);
            sc.setRequestAttribute(request, "udflist", udflist);
            sc.setRequestAttribute(request, "type", udf.getType().toString());
            sc.setRequestAttribute(request, "udfId", form.getUdfId());
            sc.setRequestAttribute(request, "udf", udf);
            sc.setRequestAttribute(request, "lookupen", udf.getLookupscript() != null && udf.getLookupscript().length() != 0 ? "false" : "true");
            sc.setRequestAttribute(request, "calculen", udf.getScript() != null && udf.getScript().length() != 0 ? "false" : "true");
        } else {

            sc.setRequestAttribute(request, "createNewUdf", "true");
            sc.setRequestAttribute(request, "type", form.getType());
            sc.setRequestAttribute(request, "lookupen", "true");
            sc.setRequestAttribute(request, "calculen", "true");
            form.setLookuponly(null);
            form.setCachevalues("true");
            form.setOrder("100");
            /*form.setCalculen(null);
            form.setLookupen(null);*/
        }

        sc.setRequestAttribute(request, "pattern", sc.getUser().getDateFormatter().getPattern2());

    }

    public static void fillListValuesForm(SessionContext sc, String filter, CustomForm form, HttpServletRequest request) throws GranException {
        putUdfTypes(sc, request);
        SecuredUDFBean udf = AdapterManager.getInstance().getSecuredFindAdapterManager().findUDFById(sc, form.getUdfId());
        Map map = udf.getUL();
        ArrayList<Pair> udflist = new ArrayList<Pair>();
        for (Object o : map.keySet()) {
            String key = (String) o;
            udflist.add(new Pair(key, (String) map.get(key)));
            form.setValue(key, null);
        }
        Collections.sort(udflist);
        sc.setRequestAttribute(request, "udflist", udflist);
        sc.setRequestAttribute(request, "type", udf.getType().toString());
        sc.setRequestAttribute(request, "udfId", form.getUdfId());
        sc.setRequestAttribute(request, "udf", udf);
    }

    public static void fillViewForm(SessionContext sc, CustomForm form, HttpServletRequest request) throws GranException {
        putUdfTypes(sc, request);
        SecuredUDFBean udf = AdapterManager.getInstance().getSecuredFindAdapterManager().findUDFById(sc, form.getUdfId());
        Map<String, String> map = udf.getUL();
        ArrayList<Pair> udflist = new ArrayList<Pair>();
        for (Map.Entry e : map.entrySet()) {
            udflist.add(new Pair(e.getKey().toString(), e.getValue().toString()));
            form.setValue(e.getKey().toString(), null);
        }
        Collections.sort(udflist);
        ArrayList<PrstatusListItem> mstatusPermissions = new ArrayList<PrstatusListItem>();

        Set<SecuredPrstatusBean> prstatusSet = new TreeSet<SecuredPrstatusBean>(AdapterManager.getInstance().getSecuredPrstatusAdapterManager().getAvailablePrstatusList(sc, sc.getUserId()));
        boolean isWorkflowUdf = KernelManager.getFind().findUdfsource(udf.getUdfSourceId()).getWorkflow() != null;


        List<SecuredPrstatusBean> viewAll = new ArrayList<SecuredPrstatusBean>();
        List<SecuredPrstatusBean> viewSubmitter = new ArrayList<SecuredPrstatusBean>();
        List<SecuredPrstatusBean> viewHandler = new ArrayList<SecuredPrstatusBean>();
        List<SecuredPrstatusBean> viewSubAndHandler = new ArrayList<SecuredPrstatusBean>();

        List<SecuredPrstatusBean> editAll = new ArrayList<SecuredPrstatusBean>();
        List<SecuredPrstatusBean> editSubmitter = new ArrayList<SecuredPrstatusBean>();
        List<SecuredPrstatusBean> editHandler = new ArrayList<SecuredPrstatusBean>();
        List<SecuredPrstatusBean> editSubAndHandler = new ArrayList<SecuredPrstatusBean>();
        for (Object aPrstatusSet : prstatusSet) {
            SecuredPrstatusBean spb = (SecuredPrstatusBean) aPrstatusSet;
            List types = AdapterManager.getInstance().getSecuredUDFAdapterManager().getUDFRuleList(sc, spb.getId(), udf.getId());
            if (types != null) {
                if (types.contains(WorkflowConstants.VIEW_ALL))
                    viewAll.add(spb);
                if (types.contains(WorkflowConstants.VIEW_SUBMITTER))
                    viewSubmitter.add(spb);
                if (types.contains(WorkflowConstants.VIEW_HANDLER))
                    viewHandler.add(spb);
                if (types.contains(WorkflowConstants.VIEW_SUBMITTER_AND_HANDLER))
                    viewSubAndHandler.add(spb);

                if (types.contains(CategoryConstants.EDIT_ALL))
                    editAll.add(spb);
                if (types.contains(CategoryConstants.EDIT_SUBMITTER))
                    editSubmitter.add(spb);
                if (types.contains(CategoryConstants.EDIT_HANDLER))
                    editHandler.add(spb);
                if (types.contains(CategoryConstants.EDIT_SUBMITTER_AND_HANDLER))
                    editSubAndHandler.add(spb);

            }

            if (isWorkflowUdf) {
                PrstatusListItem mstatusP = new PrstatusListItem(spb.getId(), spb.getName());
                String viewMst = "";
                if (types.contains(UdfConstants.MSTATUS_VIEW_ALL))
                    viewMst = I18n.getString(sc, "ALL");
                String editMst = "";
                if (types.contains(UdfConstants.MSTATUS_EDIT_ALL))
                    editMst = I18n.getString(sc, "ALL");
                for (SecuredMstatusBean securedMstatusBean : AdapterManager.getInstance().getSecuredWorkflowAdapterManager().getMstatusList(sc, KernelManager.getFind().findUdfsource(udf.getUdfSourceId()).getWorkflow().getId())) {
                    if (types.contains(WorkflowConstants.MSTATUS_VIEW_PREFIX + securedMstatusBean.getId()))
                        viewMst += (viewMst.length() == 0 ? "" : "<br/>") + securedMstatusBean.getName();
                    if (types.contains(WorkflowConstants.MSTATUS_EDIT_PREFIX + securedMstatusBean.getId()))
                        editMst += (editMst.length() == 0 ? "" : "<br/>") + securedMstatusBean.getName();
                }
                if (viewMst.length() == 0)
                    viewMst = I18n.getString(sc, "NONE");
                if (editMst.length() == 0)
                    editMst = I18n.getString(sc, "NONE");
                mstatusP.setView(viewMst);
                mstatusP.setEdit(editMst);
                mstatusPermissions.add(mstatusP);
            }
        }
        if (isWorkflowUdf) {
            sc.setRequestAttribute(request, "isWorkflow", "true");
            sc.setRequestAttribute(request, "mstatusPermissions", mstatusPermissions);
        }
        boolean isValidatePermission = true;
        List<SecuredPrstatusBean> exceptionPermission = new ArrayList<SecuredPrstatusBean>();
        for (SecuredPrstatusBean spb : editAll) {
            if (!viewAll.contains(spb)) {
                isValidatePermission = false;
                exceptionPermission.add(spb);
            }
        }
        sc.setRequestAttribute(request, "isValidePermission", isValidatePermission);
        sc.setRequestAttribute(request, "exceptionPermission", exceptionPermission);
        sc.setRequestAttribute(request, "viewAll", viewAll);
        sc.setRequestAttribute(request, "editAll", editAll);
        sc.setRequestAttribute(request, "viewHandler", viewHandler);
        sc.setRequestAttribute(request, "editHandler", editHandler);
        sc.setRequestAttribute(request, "viewSubAndHandler", viewSubAndHandler);
        sc.setRequestAttribute(request, "editSubAndHandler", editSubAndHandler);
        sc.setRequestAttribute(request, "viewSubmitter", viewSubmitter);
        sc.setRequestAttribute(request, "editSubmitter", editSubmitter);

        if (udf.getType() == UdfValue.LIST && udf.getDefaultUDF() != null && udf.getDefaultUDF().length() != 0) {
            sc.setRequestAttribute(request, "listValue", AdapterManager.getInstance().getSecuredFindAdapterManager().findUdflistById(sc, udf.getDefaultUDF()).getVal());
        }
        sc.setRequestAttribute(request, "udflist", udflist);
        sc.setRequestAttribute(request, "type", udf.getType().toString());
        sc.setRequestAttribute(request, "udfId", form.getUdfId());
        sc.setRequestAttribute(request, "udf", udf);
    }

    private static void putUdfTypes(SessionContext sc, HttpServletRequest request) {
        sc.setRequestAttribute(request, "string", Integer.toString(UdfValue.STRING));
        sc.setRequestAttribute(request, "date", Integer.toString(UdfValue.DATE));
        sc.setRequestAttribute(request, "float", Integer.toString(UdfValue.FLOAT));
        sc.setRequestAttribute(request, "integer", Integer.toString(UdfValue.INTEGER));
        sc.setRequestAttribute(request, "list", Integer.toString(UdfValue.LIST));
        sc.setRequestAttribute(request, "memo", Integer.toString(UdfValue.MEMO));
        sc.setRequestAttribute(request, "multilist", Integer.toString(UdfValue.MULTILIST));
        sc.setRequestAttribute(request, "task", Integer.toString(UdfValue.TASK));
        sc.setRequestAttribute(request, "url", Integer.toString(UdfValue.URL));
        sc.setRequestAttribute(request, "user", Integer.toString(UdfValue.USER));
    }

    //todo нужно все же переделать на нормальный ListItem паттерн
    public void fillForm(String idUDF, SessionContext sc, List<SecuredWorkflowUDFBean> udfs, HttpServletRequest request) throws GranException {
        putUdfTypes(sc, request);
        sc.setRequestAttribute(request, "idUdf", idUDF);
        sc.setRequestAttribute(request, "pattern", sc.getUser().getDateFormatter().getPattern2());
        Collections.sort(udfs);
        boolean canCreate = (Boolean) request.getAttribute("_can_create");
        sc.setRequestAttribute(request, "canCreateObject", canCreate);
        HashMap<Integer, String> type = new HashMap<Integer, String>();

        type.put(UdfValue.STRING, I18n.getString(sc.getLocale(), "UDF_STRING"));
        type.put(UdfValue.MEMO, I18n.getString(sc.getLocale(), "UDF_MEMO"));
        type.put(UdfValue.FLOAT, I18n.getString(sc.getLocale(), "UDF_FLOAT"));
        type.put(UdfValue.INTEGER, I18n.getString(sc.getLocale(), "UDF_INTEGER"));
        type.put(UdfValue.DATE, I18n.getString(sc.getLocale(), "UDF_DATE"));
        type.put(UdfValue.LIST, I18n.getString(sc.getLocale(), "UDF_LIST"));
        type.put(UdfValue.MULTILIST, I18n.getString(sc.getLocale(), "UDF_MULTILIST"));
        type.put(UdfValue.TASK, I18n.getString(sc.getLocale(), "UDF_TASK"));
        type.put(UdfValue.USER, I18n.getString(sc.getLocale(), "UDF_USER"));
        type.put(UdfValue.URL, I18n.getString(sc.getLocale(), "UDF_URL"));
        if (canCreate) {
            List<Pair> types = new ArrayList<Pair>();
            types.add(new Pair(Integer.toString(UdfValue.STRING), I18n.getString(sc.getLocale(), "UDF_STRING")));
            types.add(new Pair(Integer.toString(UdfValue.MEMO), I18n.getString(sc.getLocale(), "UDF_MEMO")));
            types.add(new Pair(Integer.toString(UdfValue.FLOAT), I18n.getString(sc.getLocale(), "UDF_FLOAT")));
            types.add(new Pair(Integer.toString(UdfValue.INTEGER), I18n.getString(sc.getLocale(), "UDF_INTEGER")));
            types.add(new Pair(Integer.toString(UdfValue.DATE), I18n.getString(sc.getLocale(), "UDF_DATE")));
            types.add(new Pair(Integer.toString(UdfValue.LIST), I18n.getString(sc.getLocale(), "UDF_LIST")));
            types.add(new Pair(Integer.toString(UdfValue.MULTILIST), I18n.getString(sc.getLocale(), "UDF_MULTILIST")));
            types.add(new Pair(Integer.toString(UdfValue.TASK), I18n.getString(sc.getLocale(), "UDF_TASK")));
            types.add(new Pair(Integer.toString(UdfValue.USER), I18n.getString(sc.getLocale(), "UDF_USER")));
            types.add(new Pair(Integer.toString(UdfValue.URL), I18n.getString(sc.getLocale(), "UDF_URL")));

            sc.setRequestAttribute(request, "isTwoParams", "true");
            sc.setRequestAttribute(request, "secondParamCollection", types);
            sc.setRequestAttribute(request, "msgHowCreateObject", I18n.getString(sc.getLocale(), "CUSTOM_FIELD_ADD"));
            sc.setRequestAttribute(request, "secondParamMsg", I18n.getString(sc.getLocale(), "TYPE"));
            sc.setRequestAttribute(request, "secondParamName", "type");
            sc.setRequestAttribute(request, "firstParamMsg", I18n.getString(sc.getLocale(), "CAPTION"));
            sc.setRequestAttribute(request, "firstParamName", "caption");
            sc.setRequestAttribute(request, "msgAddObject", I18n.getString(sc.getLocale(), "CUSTOM_FIELD_ADD"));
        }

        ArrayList<UdfBeanListItem> udfList = new ArrayList<UdfBeanListItem>();
        for (SecuredWorkflowUDFBean ri : udfs) {
            UdfBeanListItem fli = new UdfBeanListItem(ri.getId(), ri.getCaption(), type.get(ri.getType()), ri.getOrder(), ri.canManage());
            udfList.add(fli);
        }

        sc.setRequestAttribute(request, "udfList", udfList);
    }

    public static void fillPermissionForm(SessionContext sc, CustomForm form, HttpServletRequest request) throws GranException {
        String udfId = form.getUdfId();
        sc.setRequestAttribute(request, "udfId", udfId);
        SecuredUDFBean udf = AdapterManager.getInstance().getSecuredFindAdapterManager().findUDFById(sc, form.getUdfId());
        sc.setRequestAttribute(request, "udf", udf);

        Set<SecuredPrstatusBean> prstatusSet = new TreeSet<SecuredPrstatusBean>(AdapterManager.getInstance().getSecuredPrstatusAdapterManager().getAvailablePrstatusList(sc, sc.getUserId()));
        sc.setRequestAttribute(request, "prstatusSet", prstatusSet);
        HashMap<String, String> vmap = new HashMap<String, String>();
        HashMap<String, String> vmaph = new HashMap<String, String>();
        HashMap<String, String> vmaps = new HashMap<String, String>();
        HashMap<String, String> emap = new HashMap<String, String>();
        HashMap<String, String> emaph = new HashMap<String, String>();
        HashMap<String, String> emaps = new HashMap<String, String>();
        for (Object aPrstatusSet : prstatusSet) {
            SecuredPrstatusBean spb = (SecuredPrstatusBean) aPrstatusSet;
            List types = AdapterManager.getInstance().getSecuredUDFAdapterManager().getUDFRuleList(sc, spb.getId(), udfId);
            if (!(types.contains(CategoryConstants.VIEW_ALL) || types.contains(CategoryConstants.VIEW_SUBMITTER) || types.contains(WorkflowConstants.VIEW_HANDLER) || types.contains(WorkflowConstants.VIEW_SUBMITTER_AND_HANDLER)))
                form.setValue("view-" + spb.getId(), CategoryConstants.NONE);
            if (types.contains(CategoryConstants.VIEW_ALL)) {
                form.setValue("view-" + spb.getId(), CategoryConstants.VIEW_ALL);
                vmap.put(spb.getId(), "on");
            }
            if (types.contains(CategoryConstants.VIEW_SUBMITTER)) {
                form.setValue("view-" + spb.getId(), CategoryConstants.VIEW_SUBMITTER);
                vmap.put(spb.getId(), "on");
                vmaps.put(spb.getId(), "on");
            }
            if (types.contains(WorkflowConstants.VIEW_HANDLER)) {
                form.setValue("view-" + spb.getId(), WorkflowConstants.VIEW_HANDLER);
                vmap.put(spb.getId(), "on");
                vmaph.put(spb.getId(), "on");
            }
            if (types.contains(WorkflowConstants.VIEW_SUBMITTER_AND_HANDLER)) {
                form.setValue("view-" + spb.getId(), WorkflowConstants.VIEW_SUBMITTER_AND_HANDLER);
                vmap.put(spb.getId(), "on");
                vmaps.put(spb.getId(), "on");
                vmaph.put(spb.getId(), "on");
            }

            if (!(types.contains(CategoryConstants.EDIT_ALL) || types.contains(CategoryConstants.EDIT_HANDLER) || types.contains(CategoryConstants.EDIT_SUBMITTER) || types.contains(CategoryConstants.EDIT_SUBMITTER_AND_HANDLER)))
                form.setValue("edit-" + spb.getId(), CategoryConstants.NONE);
            if (types.contains(CategoryConstants.EDIT_ALL)) {
                form.setValue("edit-" + spb.getId(), CategoryConstants.EDIT_ALL);
                emap.put(spb.getId(), "on");
            }
            if (types.contains(CategoryConstants.EDIT_SUBMITTER)) {
                form.setValue("edit-" + spb.getId(), CategoryConstants.EDIT_SUBMITTER);
                emap.put(spb.getId(), "on");
                emaps.put(spb.getId(), "on");
            }
            if (types.contains(CategoryConstants.EDIT_HANDLER)) {
                form.setValue("edit-" + spb.getId(), CategoryConstants.EDIT_HANDLER);
                emap.put(spb.getId(), "on");
                emaph.put(spb.getId(), "on");
            }
            if (types.contains(CategoryConstants.EDIT_SUBMITTER_AND_HANDLER)) {
                form.setValue("edit-" + spb.getId(), CategoryConstants.EDIT_SUBMITTER_AND_HANDLER);
                emap.put(spb.getId(), "on");
                emaps.put(spb.getId(), "on");
                emaph.put(spb.getId(), "on");
            }
        }
        sc.setRequestAttribute(request, "emap", emap);
        sc.setRequestAttribute(request, "emaps", emaps);
        sc.setRequestAttribute(request, "emaph", emaph);
        sc.setRequestAttribute(request, "vmap", vmap);
        sc.setRequestAttribute(request, "vmaps", vmaps);
        sc.setRequestAttribute(request, "vmaph", vmaph);

        boolean isUser = request.getAttribute("isUser") != null;
        ArrayList<Pair> someView = new ArrayList<Pair>();
        someView.add(new Pair(CategoryConstants.NONE, "-------" + I18n.getString(sc, "NONE") + "-------"));
        someView.add(new Pair(CategoryConstants.VIEW_ALL, "-------" + I18n.getString(sc, "ALL") + "-------"));
        if (!isUser) {
            someView.add(new Pair(CategoryConstants.VIEW_SUBMITTER, I18n.getString(sc, "SUBMITTER")));
            someView.add(new Pair(WorkflowConstants.VIEW_HANDLER, I18n.getString(sc, "HANDLER")));
            someView.add(new Pair(WorkflowConstants.VIEW_SUBMITTER_AND_HANDLER, I18n.getString(sc, "SUBMITTER_OR_HANDLER")));
        }
        sc.setRequestAttribute(request, "someView", someView);
        ArrayList<Pair> someEdit = new ArrayList<Pair>();
        someEdit.add(new Pair(CategoryConstants.NONE, "-------" + I18n.getString(sc, "NONE") + "-------"));
        someEdit.add(new Pair(CategoryConstants.EDIT_ALL, "-------" + I18n.getString(sc, "ALL") + "-------"));
        if (!isUser) {
            someEdit.add(new Pair(CategoryConstants.EDIT_SUBMITTER, I18n.getString(sc, "SUBMITTER")));
            someEdit.add(new Pair(CategoryConstants.EDIT_HANDLER, I18n.getString(sc, "HANDLER")));
            someEdit.add(new Pair(CategoryConstants.EDIT_SUBMITTER_AND_HANDLER, I18n.getString(sc, "SUBMITTER_OR_HANDLER")));
        }
        sc.setRequestAttribute(request, "someEdit", someEdit);


        ArrayList<Pair> some2 = new ArrayList<Pair>();
        some2.add(new Pair(CategoryConstants.NONE, "-------" + I18n.getString(sc, "NONE") + "-------"));
        some2.add(new Pair(CategoryConstants.EDIT_ALL, "-------" + I18n.getString(sc, "ALL") + "-------"));
        if (!isUser) {
            some2.add(new Pair(CategoryConstants.EDIT_SUBMITTER, I18n.getString(sc, "SUBMITTER")));
            some2.add(new Pair(CategoryConstants.EDIT_HANDLER, I18n.getString(sc, "HANDLER")));
            some2.add(new Pair(CategoryConstants.EDIT_SUBMITTER_AND_HANDLER, I18n.getString(sc, "SUBMITTER_OR_HANDLER")));
        }
        sc.setRequestAttribute(request, "some2", some2);
    }


    public static class PrstatusListItem extends PropertyComparable {

        String id;
        String name;
        String view;
        String edit;
        String viewStatuses;
        String editStatuses;

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public String getViewStatuses() {
            return viewStatuses;
        }

        public void setViewStatuses(String viewStatuses) {
            this.viewStatuses = viewStatuses;
        }

        public String getEditStatuses() {
            return editStatuses;
        }

        public void setEditStatuses(String editStatuses) {
            this.editStatuses = editStatuses;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getView() {
            return view;
        }

        public void setView(String view) {
            this.view = view;
        }

        public String getEdit() {
            return edit;
        }

        public void setEdit(String edit) {
            this.edit = edit;
        }

        protected PropertyContainer getContainer() {
            PropertyContainer pc = container.get();
            if (pc != null)
                return pc; // object in cache, return it

            PropertyContainer newPC = new PropertyContainer();
            newPC.put(name).put(id);

            if (container.compareAndSet(null, newPC)) // try to update
                return newPC; // we can update - return loaded value
            else
                return container.get(); // some other thread already updated it - use saved value
        }

        public PrstatusListItem(String id, String name) {
            this.id = id;
            this.name = name;
        }
    }

}
