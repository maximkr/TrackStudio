package com.trackstudio.action.task.items;

import java.util.ArrayList;

import com.trackstudio.secured.SecuredCategoryBean;
import com.trackstudio.secured.SecuredMstatusBean;
import com.trackstudio.secured.SecuredTaskBean;
import com.trackstudio.tools.PropertyComparable;
import com.trackstudio.tools.PropertyContainer;

/**
 * Created by IntelliJ IDEA.
 * User: User
 * Date: 13.07.2007
 * Time: 13:19:24
 * To change this template use File | Settings | File Templates.
 */
public class WorkflowListItem extends PropertyComparable {
    public String id;
    public String name;
    public SecuredTaskBean connectedTo;
    public String taskName;
    public boolean canManage;

    public ArrayList<SecuredCategoryBean> categories;
    public ArrayList<SecuredMstatusBean> processAll;
    public ArrayList<SecuredMstatusBean> processSubmitter;
    public ArrayList<SecuredMstatusBean> processHandler;
    public ArrayList<SecuredMstatusBean> processSAH;

    public WorkflowListItem(String id, String name) {
        this.id = id;
        this.name = name;
        processAll = new ArrayList<SecuredMstatusBean>();
        processSubmitter = new ArrayList<SecuredMstatusBean>();
        processHandler = new ArrayList<SecuredMstatusBean>();
        processSAH = new ArrayList<SecuredMstatusBean>();

    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }


    public SecuredTaskBean getConnectedTo() {
        return connectedTo;
    }

    public void setConnectedTo(SecuredTaskBean connectedTo) {
        this.connectedTo = connectedTo;
    }


    public boolean isCanManage() {
        return canManage;
    }

    public void setCanManage(boolean canManage) {
        this.canManage = canManage;
    }

    public String getTaskName() {
        return taskName;
    }

    public void setTaskName(String taskName) {
        this.taskName = taskName;
    }


    public ArrayList getCategories() {
        return categories;
    }

    public void setCategories(ArrayList categories) {
        this.categories = categories;
    }


    public ArrayList<SecuredMstatusBean> getProcessAll() {
        return processAll;
    }

    public void setProcessAll(ArrayList<SecuredMstatusBean> processAll) {
        this.processAll = processAll;
    }

    public ArrayList<SecuredMstatusBean> getProcessSubmitter() {
        return processSubmitter;
    }

    public void setProcessSubmitter(ArrayList<SecuredMstatusBean> processSubmitter) {
        this.processSubmitter = processSubmitter;
    }

    public ArrayList<SecuredMstatusBean> getProcessHandler() {
        return processHandler;
    }

    public void setProcessHandler(ArrayList<SecuredMstatusBean> processHandler) {
        this.processHandler = processHandler;
    }

    public ArrayList<SecuredMstatusBean> getProcessSAH() {
        return processSAH;
    }

    public void setProcessSAH(ArrayList<SecuredMstatusBean> processSAH) {
        this.processSAH = processSAH;
    }

    protected PropertyContainer getContainer() {
        PropertyContainer pc = container.get();
        if (pc != null)
            return pc; // object in cache, return it

        PropertyContainer newPC = new PropertyContainer();
        newPC.put(name).put(taskName).put(id);

        if (container.compareAndSet(null, newPC)) // try to update
            return newPC; // we can update - return loaded value
        else
            return container.get(); // some other thread already updated it - use saved value
    }
}
