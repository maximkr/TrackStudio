package com.trackstudio.secured;

import java.util.Calendar;

import com.trackstudio.app.session.SessionContext;
import com.trackstudio.exception.GranException;
import com.trackstudio.model.Bookmark;
import com.trackstudio.soap.bean.BookmarkBean;
import com.trackstudio.tools.PropertyContainer;

import net.jcip.annotations.Immutable;

@Immutable
public class SecuredBookmarkBean extends Secured {

    private final String id;
    private final String name;
    private final String taskId;
    private final String filterId;
    private final String userId;
    private final String ownerId;
    private final Calendar createdate;

    public SecuredBookmarkBean(Bookmark bookmark, SessionContext secure) throws GranException {
        this.id = bookmark.getId();
        this.sc = secure;
        this.name = bookmark.getName();
        this.createdate = (Calendar)bookmark.getCreatedate().clone();
        this.taskId = bookmark.getTask() != null ? bookmark.getTask().getId() : null;
        this.filterId = bookmark.getFilter() != null ? bookmark.getFilter().getId() : null;
        this.userId = bookmark.getUser() != null ? bookmark.getUser().getId() : null;
        this.ownerId = bookmark.getOwner() != null ? bookmark.getOwner().getId() : null;
    }

    public SecuredUserBean getUser() throws GranException {
        return userId != null ? new SecuredUserBean(userId, getSecure()) : null;
    }

    public SecuredUserBean getOwner() throws GranException {
        return ownerId != null ? new SecuredUserBean(ownerId, getSecure()) : null;
    }

    public SecuredTaskBean getTask() throws GranException {
        return taskId != null ? new SecuredTaskBean(taskId, getSecure()) : null;
    }

    public boolean isAllowedByACL() throws GranException {
        return getOwner().isAllowedByACL();
    }

    public boolean canManage() throws GranException {
        return true;
    }

    public boolean canView() throws GranException {
        return true;
    }

    public String getId() {
        return id;
    }

    public String getTaskId() {
        return taskId;
    }

    public String getFilterId() {
        return filterId;
    }

    public String getUserId() {
        return userId;
    }

    public String getOwnerId() {
        return ownerId;
    }

    public String getName() {
        return name;
    }

    public Calendar getCreateDate() {
        return createdate;
    }

    protected PropertyContainer getContainer() {
        PropertyContainer pc = container.get();
        if (pc != null)
            return pc; // object in cache, return it

        PropertyContainer newPC = new PropertyContainer();
        newPC.put(getCreateDate()).put(getName()).put(getId());

        if (container.compareAndSet(null, newPC)) // try to update
            return newPC; // we can update - return loaded value
        else
            return container.get(); // some other thread already updated it - use saved value
    }

    public BookmarkBean getSOAP() throws GranException {
        BookmarkBean bookmarkBean = new BookmarkBean();
        bookmarkBean.setId(id);
        bookmarkBean.setName(name);
        bookmarkBean.setCreatedate(getCreateDate() != null ? getCreateDate().getTimeInMillis() : -1L);
        bookmarkBean.setFilterId(filterId);
        bookmarkBean.setTaskId(taskId);
        bookmarkBean.setUserId(userId);
        bookmarkBean.setOwnerId(ownerId);
        return bookmarkBean;
    }
}
