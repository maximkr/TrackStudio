package com.trackstudio.secured;

import com.trackstudio.app.session.SessionContext;
import com.trackstudio.exception.GranException;
import com.trackstudio.kernel.cache.Action;
import com.trackstudio.model.Filter;
import com.trackstudio.soap.bean.FilterBean;
import com.trackstudio.tools.PropertyContainer;
import com.trackstudio.tools.textfilter.HTMLEncoder;

import net.jcip.annotations.Immutable;

/**
 * Bean which represents filter
 */
@Immutable
public class SecuredFilterBean extends Secured {

    private final String id;
    private final String description;
    private final boolean priv;
    private final String name;
    private final String preferences;
    private final String ownerId;
    private final String taskId;
    private final String userId;


    public SecuredFilterBean(Filter filter, SessionContext secure) throws GranException {
        this.id = filter.getId();
        this.sc = secure;
        this.taskId = filter.getTask() != null ? filter.getTask().getId() : null;
        this.userId = filter.getUser() != null ? filter.getUser().getId() : null;
        this.ownerId = filter.getOwner() != null ? filter.getOwner().getId() : null;
        this.priv = filter.isPrivate();
        this.preferences = filter.getPreferences();
        this.description = filter.getDescription();
        this.name = filter.getName();
    }

    public String getHtmlDesc() {
        String text = "";
        if (this.description != null) {
            text = HTMLEncoder.encodeTree(HTMLEncoder.escape2HTML(this.description));
        }
        return text;
    }

    public String getDescription() {
        return description;
    }

    public boolean isPrivate() {
        return priv;
    }

    public boolean getPriv() {
        return isPrivate();
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getUserId() {
        return userId;
    }

    public SecuredUserBean getUser() throws GranException {
        if (userId != null)
            return new SecuredUserBean(userId, getSecure());
        else return null;
    }

    public SecuredUserBean getOwner() throws GranException {

        return new SecuredUserBean(ownerId, getSecure());
    }

    public SecuredTaskBean getTask() throws GranException {
        return taskId != null ? new SecuredTaskBean(taskId, getSecure()) : null;
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
        SessionContext s = getSecure();
        return isAllowedByACL() &&
                (
                        (getTaskId() != null && ((s.canAction(Action.manageTaskPrivateFilters, getTaskId()) && getOwnerId().equals(s.getUserId())) || (s.canAction(Action.manageTaskPublicFilters, getTaskId()) && !isPrivate() && !getOwnerId().equals(s.getUserId()))))
                                ||
                                (getTaskId() == null && ((s.canAction(Action.manageUserPrivateFilters, getOwnerId()) && getOwnerId().equals(s.getUserId())) || (s.canAction(Action.manageUserPublicFilters, getOwnerId()) && !isPrivate() && !getOwnerId().equals(s.getUserId()))))
                );
    }

    public boolean canView() throws GranException {
        return getTaskId() != null ? getSecure().taskOnSight(getTaskId()) : getOwnerId() != null && (priv ? getOwnerId().equals(getSecure().getUserId()) : getUser().canView());
    }

    public FilterBean getSOAP() throws GranException {
        FilterBean bean = new FilterBean();
        bean.setDescription(description);
        bean.setPreferences(preferences);
        bean.setId(id);
        bean.setName(name);
        bean.setOwnerId(ownerId);
        bean.setPrivate(priv);
        bean.setTaskId(taskId);
        return bean;
    }

    public String getOwnerId() {
        return ownerId;
    }

    public String getTaskId() {
        return taskId;
    }

    public boolean isPriv() {
        return priv;
    }

    public String getPreferences() {
        return preferences;
    }

    public String getCorrectName() {
        String correctName = "";
        if (name != null) {
            correctName = name.replaceAll("\"", " ");
            correctName = correctName.replaceAll("'", " ");
        }
        return correctName;
    }

    public String getCorrectDesc() {
        String correctName = "";
        if (description != null) {
            correctName = description.replaceAll("\"", " ")
                    .replaceAll("'", " ").replaceAll("\t", " ")
            .replaceAll("\n", " ").replaceAll("\\s+", " ");
        }
        return correctName;
    }
}
