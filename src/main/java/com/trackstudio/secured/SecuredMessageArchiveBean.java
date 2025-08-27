package com.trackstudio.secured;

import com.trackstudio.exception.GranException;
import com.trackstudio.kernel.cache.MessageCacheItem;
import com.trackstudio.startup.Config;
import com.trackstudio.tools.formatter.HourFormatter;
import org.json.JSONObject;

import java.util.Calendar;
import java.util.List;

public class SecuredMessageArchiveBean {
    private final MessageCacheItem item;

    private final JSONObject mstatus;
    private final JSONObject resolution;
    private final JSONObject submitter;
    private final JSONObject handler;
    private final JSONObject handlerGroup;
    private final JSONObject priority;
    List<SecuredTaskAttachmentBean> attachments;

    public SecuredMessageArchiveBean(MessageCacheItem item, JSONObject mstatus, JSONObject resolution, JSONObject priority,
                                     JSONObject submitter, JSONObject handler, JSONObject handlerGroup, List<SecuredTaskAttachmentBean> attachments) {
        this.item = item;
        this.mstatus = mstatus;
        this.resolution = resolution;
        this.submitter = submitter;
        this.handler = handler;
        this.handlerGroup = handlerGroup;
        this.priority = priority;
        this.attachments = attachments;
    }

    public String getId() {
        return this.item.getId();
    }

    public List<SecuredTaskAttachmentBean> getAttachments() {
        return attachments;
    }

    public Calendar getTime() {
        return this.item.getTime();
    }

    public Calendar getDeadline() {
        return this.item.getDeadline();
    }

    public String getActualBudgetAsString() throws GranException {
        if (item.getHrs() != null && item.getHrs() > 0) {
            try {
                HourFormatter hf = new HourFormatter(item.getHrs(), "h", Config.getInstance().getDefaultLocale());
                return hf.getString();
            } catch (Exception e) {
                throw new GranException(e);
            }
        } else return "";
    }

    public String getBudgetAsString() throws GranException {
        if (item.getBudget() != null && item.getBudget() > 0) {
            try {
                HourFormatter hf = new HourFormatter(item.getBudget(), "h", Config.getInstance().getDefaultLocale());
                return hf.getString();
            } catch (Exception e) {
                throw new GranException(e);
            }
        } else return "";
    }

    public JSONObject getMstatus() {
        return this.mstatus;
    }

    public String getMstatusName() {
        return this.mstatus.optString("name", "");
    }

    public String getDescription() throws GranException {
        return this.item.getDescription();
    }

    public String getResolutionId() {
        return resolution.optString("id", null);
    }

    public String getResolutionName() {
        return resolution.optString("name");
    }

    public String getPriorityId() {
        return priority.optString("id", null);
    }

    public String getPriorityName() {
        return priority.optString("name");
    }

    public String getSubmitterId() {
        return submitter.optString("id", null);
    }

    public String getSubmitterLogin() {
        return submitter.optString("login");
    }

    public String getSubmitterName() {
        return submitter.optString("name");
    }

    public String getSubmitterEmail() {
        return submitter.optString("email");
    }

    public String getSubmitterTel() {
        return submitter.optString("tel");
    }

    public String getHandlerUserId() {
        return handler.optString("id", null);
    }

    public String getHandlerUserLogin() {
        return handler.optString("login");
    }

    public String getHandlerUserName() {
        return handler.optString("name");
    }

    public String getHandlerUserEmail() {
        return handler.optString("email");
    }

    public String getHandlerUserTel() {
        return handler.optString("tel");
    }


    public String getHandlerGroupId() {
        return handlerGroup.optString("id", null);
    }

    public String getHandlerGroupName() {
        return handlerGroup.optString("name");
    }
}
