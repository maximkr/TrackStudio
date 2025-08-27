package com.trackstudio.secured;

import com.trackstudio.app.session.SessionContext;
import com.trackstudio.exception.GranException;
import com.trackstudio.kernel.cache.TaskRelatedInfo;
import com.trackstudio.kernel.manager.KernelManager;
import com.trackstudio.model.Status;
import com.trackstudio.tools.formatter.HourFormatter;
import org.json.JSONObject;

import java.util.Calendar;
import java.util.List;
import java.util.Map;

public class SecuredTaskArchiveBean {

    private TaskRelatedInfo info;
    private Map<String, String> udfs;
    private List<SecuredTaskAttachmentBean> atts;
    private List<SecuredMessageArchiveBean> msgs;
    private SessionContext sc;
    private String path;
    private String statusName;
    private String statusId;
    private String statusColor;
    private boolean statusIsFinish;
    private boolean statusIsStart;
    private String categoryName;
    private String categoryIcon;
    private String categoryId;
    private String submitterId;
    private String submitterName;
    private String submitterLogin;
    private String submitterEmail;
    private boolean submitterActive;
    private String submitterTel;
    private String handlerUserId;
    private String handlerUserName;
    private String handlerUserLogin;
    private String handlerUserEmail;
    private boolean handlerUserActive;
    private String handlerUserTel;
    private String handlerGroupId;
    private String handlerGroupName;
    private String resolutionId;
    private String resolutionName;
    private String priorityId;
    private String priorityName;

    public SecuredTaskArchiveBean(TaskRelatedInfo task, SessionContext sc,
                                  Map<String, String> udfs, List<SecuredTaskAttachmentBean> atts,
                                  List<SecuredMessageArchiveBean> msgs, String path,
                                  JSONObject status, JSONObject category, JSONObject submitter, JSONObject handler, JSONObject handlerGroup, JSONObject resolution, JSONObject priority) throws GranException {
        this.info = task;
        this.udfs = udfs;
        this.atts = atts;
        this.msgs = msgs;
        this.sc = sc;
        this.path = path;
        this.statusName = status.optString("name");
        this.statusId = status.optString("id", null);
        this.statusColor = status.optString("color");
        this.statusIsFinish = status.optInt("isFinish") == 1;
        this.statusIsStart = status.optInt("isStart") == 1;
        this.categoryName = category.optString("name");
        this.categoryIcon = category.optString("icon");
        this.categoryId = category.optString("id", null);
        this.submitterId = submitter.optString("id");
        this.submitterName = submitter.optString("name");
        this.submitterLogin = submitter.optString("login");
        this.submitterEmail = submitter.optString("email");
        this.submitterActive = submitter.optInt("active") == 1;
        this.submitterTel = submitter.optString("tel");
        this.handlerUserId = handler.optString("id", null);
        this.handlerGroupId = handlerGroup.optString("groupid", null);
        this.handlerUserName = handler.optString("name");
        this.handlerGroupName = handlerGroup.optString("groupname");
        this.handlerUserLogin = handler.optString("login");
        this.handlerUserEmail = handler.optString("email");
        this.handlerUserActive = handler.optInt("active") == 1;
        this.handlerUserTel = handler.optString("tel");
        this.resolutionId = resolution.optString("id", null);
        this.resolutionName = resolution.optString("name");
        this.priorityId = priority.optString("id", null);
        this.priorityName = priority.optString("name");
    }

    public Map<String, String> getUdfs() {
        return udfs;
    }

    public List<SecuredTaskAttachmentBean> getAtts() {
        return atts;
    }

    public List<SecuredMessageArchiveBean> getMsgs() {
        return msgs;
    }

    public String getNumber() {
        return info.getNumber();
    }

    public String getDescription() throws GranException {
        return info.getDescription();
    }

    public String getId() {
        return this.info.getId();
    }

    public String getName() {
        return this.info.getName();
    }

    public SecuredUserBean getSubmitter() throws GranException {
        return new SecuredUserBean(this.info.getSubmitterId(), sc);
    }

    public String getSubmitterId() {
        return submitterId;
    }

    public String getShortname() {
        return this.info.getShortname();
    }

    public String getCategoryId() {
        return categoryId;
    }

    public SecuredCategoryBean getCategory() throws GranException {
        return new SecuredCategoryBean(KernelManager.getFind().findCategory(this.info.getCategoryId()), sc);
    }

    public String getStatusId() {
        return statusId;
    }

    public String getResolutionName() {
        return resolutionName;
    }

    public String getPriorityName() {
        return priorityName;
    }

    public String getStatusName() {
        return statusName;
    }

    public String getStatusColor() {
        return statusColor;
    }

    public String getCategoryName() {
        return categoryName;
    }

    public String getCategoryIcon() {
        return categoryIcon;
    }

    public String getStatusImage() {
        StringBuffer sb = new StringBuffer();
        String imageState = statusIsStart && statusIsFinish ? "finish" : statusIsStart ? "start" : statusIsFinish ? "finish" : "";
        sb.append("/cssimages/").append(imageState).append("state.png");
        return sb.toString();
    }

    public String getSubmitterName() {
        return submitterName;
    }

    public String getSubmitterLogin() {
        return submitterLogin;
    }

    public String getSubmitterEmail() {
        return submitterEmail;
    }

    public boolean isSubmitterActive() {
        return submitterActive;
    }

    public String getSubmitterTel() {
        return submitterTel;
    }

    public String getHandlerUserName() {
        return handlerUserName;
    }

    public String getHandlerUserLogin() {
        return handlerUserLogin;
    }

    public String getHandlerUserEmail() {
        return handlerUserEmail;
    }

    public boolean isHandlerUserActive() {
        return handlerUserActive;
    }

    public String getHandlerUserTel() {
        return handlerUserTel;
    }

    public String getHandlerGroupName() {
        return handlerGroupName;
    }

    public SecuredStatusBean getStatus() throws GranException {
        return new SecuredStatusBean(KernelManager.getFind().findStatus(this.info.getStatusId()), sc);
    }

    public String getResolutionId() {
        return resolutionId;
    }

    public SecuredResolutionBean getResolution() throws GranException {
        return new SecuredResolutionBean(KernelManager.getFind().findResolution(this.info.getStatusId()), sc);
    }

    public Calendar getUpdatedate() {
        return this.info.getUpdatedate();
    }

    public Calendar getClosedate() {
        return this.info.getClosedate();
    }

    public Calendar getSubmitdate() {
        return this.info.getSubmitdate();
    }

    public String getPriorityId() {
        return priorityId;
    }

    public SecuredPriorityBean getPriority() throws GranException {
        return new SecuredPriorityBean(KernelManager.getFind().findPriority(this.info.getPriorityId()), sc);
    }

    public Calendar getDeadline() {
        return info.getDeadline();
    }

    public String getBudgetAsString() throws GranException {
        Long budget_ = info.getBudget();
        if (budget_ != null && budget_ > 0) {
            HourFormatter hf = new HourFormatter(info.getBudget(), this.getBudgetFormat(), sc.getLocale());
            return hf.getString();
        } else {
            return "";
        }
    }

    public String getBudgetFormat() throws GranException {
        String b = getCategory().getBudget();
        return b != null ? b : "";
    }

    public String getHandlerUserId() {
        return handlerUserId;
    }

    public String getHandlerId() {
        return this.info.getHandlerId();
    }

    public String getHandlerGroupId() {
        return handlerGroupId;
    }

    public SecuredUserBean getHandlerUser() throws GranException {
        return new SecuredUserBean(this.info.getHandlerUserId(), sc);
    }

    public SecuredPrstatusBean getHandlerGroup() throws GranException {
        return new SecuredPrstatusBean(
                KernelManager.getFind().findPrstatus(this.info.getHandlerGroupId()), sc
        );
    }

    public String getActualBudgetAsString() throws GranException {
        Long actualBudget_ = info.getActualBudget();
        if (actualBudget_ != null && actualBudget_ > 0) {
            HourFormatter hf = new HourFormatter(actualBudget_, getBudgetFormat(), sc.getLocale());
            return hf.getString();
        } else {
            return "";
        }
    }

    public long getChildrenCount() throws GranException {
        return 0;
    }

    public String getParentId() {
        return this.info.getParentId();
    }

    public boolean isArchived() {
        return true;
    }

    public String getPath() {
        return path;
    }
}
