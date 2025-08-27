package com.trackstudio.secured;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.atomic.AtomicReference;

import com.trackstudio.app.UdfValue;
import com.trackstudio.app.adapter.AdapterManager;
import com.trackstudio.app.filter.customizer.Customizer;
import com.trackstudio.containers.Link;
import com.trackstudio.exception.GranException;
import com.trackstudio.kernel.cache.TaskRelatedManager;
import com.trackstudio.kernel.cache.UserRelatedManager;
import com.trackstudio.kernel.manager.KernelManager;
import com.trackstudio.soap.bean.UdfValueBean;
import com.trackstudio.startup.Config;
import com.trackstudio.startup.I18n;
import com.trackstudio.tools.Null;
import com.trackstudio.tools.Pair;
import com.trackstudio.tools.PropertyContainer;
import com.trackstudio.tools.compare.FieldSort;
import com.trackstudio.tools.compare.IUdfSort;
import com.trackstudio.tools.compare.SortTask;
import com.trackstudio.tools.compare.SortUser;

import net.jcip.annotations.ThreadSafe;

import static com.trackstudio.tools.textfilter.MacrosUtil.buildLabelUdf;

/**
 * Bean which represents custom field value
 */
@ThreadSafe
public class SecuredUDFValueBean extends Secured implements IUdfSort {
    private final UdfValue udfValue;
    protected final Secured secured;
    protected final AtomicReference<SecuredUDFBean> udf = new AtomicReference<SecuredUDFBean>();
    private volatile boolean scriptException = false;
    private volatile CopyOnWriteArrayList participant = new CopyOnWriteArrayList();
    private static final UserRelatedManager USER_MANAGER = UserRelatedManager.getInstance();
    private static final TaskRelatedManager TASK_MANAGER = TaskRelatedManager.getInstance();
    public Secured getSecured() {
        return secured;
    }

    public boolean isScriptException() {
        return scriptException;
    }

    public void setScriptException(boolean scriptException) {
        this.scriptException = scriptException;
    }

    public SecuredUDFValueBean(UdfValue udfValue_, Secured secured_) {
        this.secured = secured_;
        this.sc = secured.getSecure();
        this.udfValue = udfValue_;
    }


    public boolean isTaskUdfEditable(String objectId) throws GranException {
        return isTaskUdfEditable(objectId, null);
    }

    public boolean isTaskUdfEditable(String objectId, String statusId) throws GranException {
        return KernelManager.getUdf().isTaskUdfEditable(objectId, getSecure().getUserId(), getUdfId(), statusId);
    }

    public boolean isNewTaskUdfEditable(String objectId, String statusId, String submiter, String handler) throws GranException {
        return KernelManager.getUdf().isNewTaskUdfEditable(objectId, getSecure().getUserId(), getUdfId(), statusId, submiter, handler);
    }

    public boolean isUserUdfEditable(String objectId) throws GranException {
        return KernelManager.getUdf().isUserUdfEditable(objectId, getSecure().getUserId(), getUdfId());
    }

    public String getUdfId() {
        return udfValue.getUdfId();
    }

    public Integer getUdfType() {
        return udfValue.getType();
    }

    /**
     * ����� ���������� �������� UDF
     * ���� UDF/ ������������ ����
     * 0 STRING 'string' String
     * 1 FLOAT 'float' Double
     * 2 DATE 'date' Calendar
     * 3 LIST 'list' Pair ����/��������. pair.key, pair.value
     * 4 INTEGER 'integer' Integer
     * 5 MEMO 'memo' String
     * 6 MULTILIST 'multilist' List<Pair> ������ ��� ����/��������
     * 7 TASK 'task' List<SecuredTaskBean> ������ SecuredTaskBean
     * 8 USER 'user' List<SecuredUserBean> ������ SecuredUsetBean
     * 9 URL 'url' Link
     *
     * @return �������� ����
     * @throws GranException ��� �������������
     */
    public Object getValue() throws GranException {
        Object result = udfValue.getValue(secured);
        if (result != null && getUdfType().equals(UdfValue.USER)) {
            List<String> res = (List<String>) result;
            if (!res.isEmpty()) {
                List<SecuredUserBean> users = new ArrayList<SecuredUserBean>();
                for (String userId : res) {
                    SecuredUserBean sub = AdapterManager.getInstance().getSecuredFindAdapterManager().findUserById(getSecure(), userId);
                    if (sub != null) users.add(sub);
                }
                Collections.sort(users, new SortUser(FieldSort.NAME));
                return users;
            }
            return null;
        } else if (result != null && getUdfType().equals(UdfValue.TASK)) {
            List<String> res = (List<String>) result;
            if (!res.isEmpty()) {
                List<SecuredTaskBean> tasks = new ArrayList<SecuredTaskBean>();
                for (String taskId : res) {
                    SecuredTaskBean sub = AdapterManager.getInstance().getSecuredFindAdapterManager().findTaskById(getSecure(), taskId);
                    if (sub != null) tasks.add(sub);
                }
                Collections.sort(tasks, new SortTask(FieldSort.NAME, true));
                return tasks;
            }
            return null;
        } else {
            return result;
        }
    }

    public String getValString() throws GranException {
        Object o = getValue();
        return o != null ? o.toString() : "";
    }

    /**
     * ����� ���������� �������� UDF
     * ���� UDF/ ������������ ����
     * 0 STRING 'string' String
     * 1 FLOAT 'float' Double
     * 2 DATE 'date' Calendar
     * 3 LIST 'list' String ��������, � �� ����. ���� ����� ������������� ���������
     * 4 INTEGER 'integer' Integer
     * 5 MEMO 'memo' String
     * 6 MULTILIST 'multilist' Set<String> ������ ��������
     * 7 TASK 'task' List<String> ������ taskId
     * 8 USER 'user' List<String> ������ userId
     * 9 URL 'url' String url\ndescription
     *
     * @param sec ������ ������������
     * @return �������� ����
     * @throws GranException ��� �������������
     * @deprecated ��� legacy-�����, ����������� ��� ������������� � ��� ����������� ��� 3.5 ���������
     */
    public Object getValue(Secured sec) throws GranException {
        Object result = udfValue.getValue(sec);
        // �������� ��������������. ����� ������������� List, Multilist � URL

        if (result != null && getUdfType().equals(UdfValue.LIST)) {
            Pair res = (Pair) result;
            return res.getValue();
        } else if (result != null && getUdfType().equals(UdfValue.MULTILIST)) {
            List<String> res = new ArrayList<String>();
            List<Pair> list = (List<Pair>) result;
            for (Pair pair : list) {
                res.add(pair.getValue());
            }
            Collections.sort(res);
            return res;
        } else if (result != null && getUdfType().equals(UdfValue.URL)) {
            Link link = (Link) result;
            return link.getLink() + "\n" + link.getDescription();
        } else
            return result;
    }

    private List<SecuredUserBean> buildLookupDataForUserField(List list, int limit) throws GranException {
        List<String> used = (List) udfValue.getValue(secured);
        if (used == null) used = new ArrayList<String>();
        List<SecuredUserBean> users = new ArrayList<SecuredUserBean>(list.size());
        int index = 0;
        for (Object o : list) {
            if (index >= limit && limit != -1) {
                break;
            }
            if (o instanceof SecuredUserBean) {
                SecuredUserBean user = (SecuredUserBean) o;
                if (!used.contains(user.getId())) {
                    users.add(user);
                    index++;
                }
            } else {
                String userId = KernelManager.getUser().findByLogin((String) o);
                if (userId != null && !used.contains(userId)) {
                    users.add(new SecuredUserBean(userId, secured.getSecure()));
                    index++;
                }
            }
        }
        Collections.sort(users, new SortUser(FieldSort.LOGIN));
        return users;
    }

    private List<SecuredTaskBean> buildLookupDataForTaskField(List list) throws GranException {
        TreeSet<SecuredTaskBean> tasks = new TreeSet<SecuredTaskBean>();
        for (Object o : list) {
            String taskId = KernelManager.getTask().findByNumber(o.toString());
            if (taskId != null) {
                if (getSecure().allowedByACL(taskId)) {
                    tasks.add(new SecuredTaskBean(taskId, getSecure()));
                }
            }
        }
        return new ArrayList<SecuredTaskBean>(tasks);
    }

    public List getList() throws GranException {
        return getLimitList(Config.getInstance().limit);
    }

    public List getLimitList(int limit) throws GranException {
        if (isLookup() && !scriptException) {
            Object c = getUdf().getLookupscriptCalc(secured);
            List<Pair> p = new ArrayList<Pair>();
            if (c != null && c instanceof List) {
                if (getUdfType().equals(UdfValue.USER)) {
                    return buildLookupDataForUserField((List) c, limit);
                } else if (getUdfType().equals(UdfValue.TASK)) {
                    List<SecuredTaskBean> tasks = buildLookupDataForTaskField((List<SecuredTaskBean>) c);//
                    List<SecuredTaskBean> list = (List<SecuredTaskBean>) getValue();
                    if (list != null) {
                        for (SecuredTaskBean task : list) {
                            if (!tasks.contains(task)) {
                                tasks.add(task);
                            }
                        }
                    }
                    return tasks;
                } else {
                    if (getUdfType().equals(UdfValue.LIST) || getUdfType().equals(UdfValue.MULTILIST)) {
                        Map<String, String> map = getUdflist();
                        for (Object o : (List) c) {
                            for (Map.Entry<String, String> entry : map.entrySet()) {
                                if (entry.getValue().equals(o.toString())) {
                                    p.add(new Pair(entry.getKey(), entry.getValue()));
                                }
                            }
                        }
                    } else {
                        for (Object o : (List) c) {
                            p.add(new Pair(o.toString(), o.toString()));
                        }
                    }
                    return p;
                }
            } else {
                return p;
            }
        } else {
            if (getUdfType().equals(UdfValue.USER)) {
                return buildUdfUserList(limit);
            } else if (getUdfType().equals(UdfValue.TASK)) {
                Object val = getValue();
                if (val != null)
                    return (List<SecuredTaskBean>) val;
                else return null;
            } else {
                List<Pair> p = udfValue.getList();
                Collections.sort(p);
                return p;
            }
        }
    }

    private List<SecuredUserBean> buildUdfUserList(int limit) throws GranException {
        Object val = getValue();
        List<SecuredUserBean> users = new ArrayList<SecuredUserBean>();
        int index = 0;
        if (secured instanceof SecuredTaskBean) {  //TODO remove copy\paste
            String taskId = ((SecuredTaskBean) secured).getTask().getId();
            for (SecuredUserBean user : AdapterManager.getInstance().getSecuredAclAdapterManager().getUserList(getSecure(), taskId)) {
                if (index >= limit && limit != -1) {
                    break;
                }
                if (TASK_MANAGER.hasAccess(user.getId(), taskId, user.getPrstatusId()) && user.isActive()
                        && KernelManager.getCategory().isCategoryViewable(secured.getId(), user.getId(), ((SecuredTaskBean) secured).getCategoryId(), user.getPrstatusId())) {
                    users.add(user);
                    index++;
                }
            }
        } else {
            for (SecuredUserBean user : AdapterManager.getInstance().getSecuredUserAdapterManager().getUserAndChildrenList(getSecure(), getSecure().getUserId())) {
                if (index >= limit && limit != -1) {
                    break;
                }
                if (USER_MANAGER.hasAccess(user.getId(), secured.getId(), user.getPrstatusId()) && user.isActive()) {
                    users.add(user);
                }
                index++;
            }
        }

        if (val != null) {
            for (SecuredUserBean user : (List<SecuredUserBean>) val) {
                if (users.contains(user)) {
                    users.remove(user);
                } else {
                    if (user.isActive()) {
                        users.add(user);
                    }
                }
            }
        }

        return users;
    }

    /**
     * ���������� ������ ��������
     *
     * @param o ������ ������������
     * @return ������ ��������
     * @throws GranException ��� �������������
     * @deprecated ��� legacy-�����, ����������� ��� ������������� � ��� ����������� ��� 3.5 ���������
     */
    public Object getList(Secured o) throws GranException {
        Object result = udfValue.getValue(o);
        // �������� ��������������. ����� ������������� List, Multilist � URL

        if (result != null && getUdfType().equals(UdfValue.LIST)) {
            Pair res = (Pair) result;
            return res.getKey();
        } else if (result != null && getUdfType().equals(UdfValue.MULTILIST)) {
            Set<String> res = new TreeSet<String>();
            List<Pair> list = (List<Pair>) result;
            for (Pair pair : list) {
                res.add(pair.getKey());
            }

            return res;
        } else return null;
    }


    @Override
    public Integer getOrder() {
        return udfValue.getOrder();
    }

    public String getCaption() {
        if (udfValue.getUdfId() != null)
            return udfValue.getCaption();
        else
            return null;
    }

    public String getReferencedByCaption() {
        if (udfValue.getUdfId() != null)
            return udfValue.getCaptionReference() == null ? getCaption() : udfValue.getCaptionReference();
        else
            return null;
    }

    public String getCaptionEx() throws GranException {
        if (udfValue.getUdfId() != null)
            return udfValue.getCaptionWorkflow();
        else
            return null;
    }

    public boolean isCalculated() {
        return udfValue.getUdfScript() != null;
    }

    public boolean isLookup() {
        return udfValue.getUdfLookupScript() != null;
    }

    public boolean isLookupOnly() {
        return udfValue.isLookupOnly();
    }

    public boolean getLookupOnly() {
        return udfValue.getLookupOnly();
    }

    public String getId() {
        return udfValue.getUdfId();
    }

    public boolean isAllowedByACL() throws GranException {
        return true;
    }

    public boolean canManage() throws GranException {
        return true;
    }

    public boolean canView() throws GranException {
        return true;
    }

    public Customizer getCustomizer(String sort, boolean dis) throws GranException {
        return udfValue.getCustomizer(getSecure().getTimezone(), getSecure().getLocale(), sort, dis);
    }

    public HashMap getUdflist() throws GranException {
        return KernelManager.getUdf().getUdflist(getId());
    }

    public boolean isRequired() throws GranException {
        return udfValue.isRequired();
    }

    public boolean isEmptyDefault() throws GranException {
        return getUdf().getDefaultUDF().isEmpty();
    }

    public boolean isEmptyValue() throws GranException {
        return udfValue.getValue(secured) == null;
    }

    public boolean isEmptyList() throws GranException {
        List l = getList();
        if (l == null) return true;
        else return (l.isEmpty());
    }

    public boolean isHtmlview() throws GranException {
        return udfValue.isHtmlview();
    }

    public boolean equals(Object o) {
        return o instanceof SecuredUDFValueBean && udfValue.equals(((SecuredUDFValueBean) o).udfValue);
    }


    protected PropertyContainer getContainer() {
        PropertyContainer pc = container.get();
        if (pc != null)
            return pc; // object in cache, return it

        PropertyContainer newPC = new PropertyContainer();
        newPC.put(udfValue.getOrder()).put(getCaption()).put(getId());

        if (container.compareAndSet(null, newPC)) // try to update
            return newPC; // we can update - return loaded value
        else
            return container.get(); // some other thread already updated it - use saved value
    }

    public String getType() {

        switch (udfValue.getType()) {
            case UdfValue.MULTILIST:
                return "multilist";

            case UdfValue.LIST:
                return "list";

            case UdfValue.USER:
                return "user";

            case UdfValue.TASK:
                return "task";

            case UdfValue.FLOAT:
                return "float";

            case UdfValue.INTEGER:
                return "integer";

            case UdfValue.URL:
                return "url";

            case UdfValue.MEMO:
                return "memo";

            case UdfValue.STRING:
                return "string";

            case UdfValue.DATE:
                return "date";

        }
        return "";
    }

    public SecuredUDFBean getUdf() throws GranException {
        SecuredUDFBean u = udf.get();
        if (u!=null)
            return u;

        u = AdapterManager.getInstance().getSecuredFindAdapterManager().findUDFById(getSecure(), getUdfId());
        if (udf.compareAndSet(null, u))
            return u;
        else
            return udf.get();
    }


    public UdfValueBean getSOAP()
            throws GranException {
        UdfValueBean bean = new UdfValueBean();

        bean.setUdfId(getUdfId());
        bean.setType(getType());
        bean.setName(getCaption());
        bean.setValue(udfValue.getValueContainer().getValueAsString(udfValue.getValue(secured)));
        return bean;
    }

    @Override
    public String toString() {
        return "SecuredUDFValueBean{" +
                "udfValue=" + udfValue +
                ", udf=" + udf.get() +
                '}';
    }

    public List getParticipant() {
        return participant;
    }

    public void setParticipant(List participant) {
        Collections.sort(participant);
        this.participant = new CopyOnWriteArrayList(Null.removeNullElementsFromList(participant));
    }

    @Override
    public void setId(String id) {
    }

    /**
     * This method builds html text for div html
     * @return html text
     */
    public String getBuildDivBox() throws GranException {
        StringBuilder sb = new StringBuilder();
        sb.append("<label id='participants_").append(this.getId()).append("'>").append(I18n.getString(this.getSecure(), "PARTICIPANTS")).append("</label>");
        int index = 0;
        List<SecuredUserBean> participantList = (List<SecuredUserBean>) this.getParticipant();
        for (SecuredUserBean user : participantList) {
            buildLabelUdf(sb, this.getId(), user, index, true);
            ++index;
        }
        sb.append("<label id='users_list_").append(this.getId()).append("'>").append(I18n.getString(this.getSecure(), "USERS_LIST")).append("</label>");
        List<SecuredUserBean> otherList =  (List<SecuredUserBean>) this.getLimitList(Config.getInstance().limit);
        Collections.sort(otherList);
        for (SecuredUserBean user : otherList) {
            buildLabelUdf(sb, this.getId(), user, index, false);
            ++index;
        }
        return sb.toString();
    }
}