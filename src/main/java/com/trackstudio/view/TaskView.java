package com.trackstudio.view;

import java.util.Collection;
import java.util.Iterator;

import com.trackstudio.app.adapter.AdapterManager;
import com.trackstudio.exception.GranException;
import com.trackstudio.secured.SecuredTaskBean;
import com.trackstudio.secured.SecuredUDFValueBean;
import com.trackstudio.secured.SecuredUserBean;
import com.trackstudio.startup.Config;
import com.trackstudio.startup.I18n;
import com.trackstudio.tools.Pair;

import net.jcip.annotations.ThreadSafe;

@ThreadSafe
public abstract class TaskView {
    public static final String PATH_DELIMITER = " > ";
    public static final String NUMBER_SIGN = "#";
    public static final String TERM_LINK = "#";
    private volatile boolean isNew = false;
    private volatile String newTaskName;
    protected final SecuredTaskBean task;
    protected volatile boolean freeAccess;

    public abstract String getName() throws GranException;

    public abstract String getAliasPath() throws GranException;

    public abstract String getSimplePath() throws GranException;

    public String getContext() throws GranException {
        return Config.getInstance().getSiteURL();
    }
    

    public boolean isNew() {
        return isNew;
    }

    public String getAliasPathForTaskSelect() throws GranException {
        return "";
    }

    public String getNameForTaskSelect() throws GranException {
        return "";
    }

    public void setNew(boolean aNew) {
        isNew = aNew;
    }

    public String getNewTaskName() {
        return newTaskName;
    }

    public void setNewTaskName(String newTaskName) {
        this.newTaskName = newTaskName;
    }

    public String pathDelimiter() {
        return PATH_DELIMITER;
    }

    protected TaskView(SecuredTaskBean task) throws GranException {
        this.task = task;
        this.freeAccess = task.canView();
    }

    public SecuredTaskBean getTask() {
        return task;
    }

    public String getRelativePath(String from) throws GranException {
        if (null != task) {
            StringBuffer bf = new StringBuffer();
            Iterator i1 = AdapterManager.getInstance().getSecuredTaskAdapterManager().getTaskChain(task.getSecure(), from, task.getId()).iterator();
            if (i1.hasNext()) {
                SecuredTaskBean stb = (SecuredTaskBean) i1.next();
                bf.append(stb.getId().equals(task.getId()) ? getView(stb).getName() : getView(stb).getAliasPath());
            }
            while (i1.hasNext()) {
                SecuredTaskBean stb = (SecuredTaskBean) i1.next();
                bf.append(pathDelimiter());
                bf.append(stb.getId().equals(task.getId()) ? getView(stb).getName() : getView(stb).getAliasPath());
            }
            return bf.toString();
        }
        return "";
    }


//    public String getRelativePath(String from) throws GranException {
//        if (null != task) {
//            StringBuffer bf = new StringBuffer();
//            Iterator i1 = AdapterManager.getInstance().getSecuredTaskAdapterManager().getTaskChain(task.getSecure(), from, task.getId()).iterator();
//            if (i1.hasNext()) {
//                while (i1.hasNext())
//                    bf.append(getView((SecuredTaskBean) i1.next()).getAliasPath());
//            }
//            return bf.toString();
//        }
//        return "";
//    }


    public String getFullPath(String startId) throws GranException {
        if (null != task) {
            if (freeAccess) {
                StringBuffer bf = new StringBuffer();
                Iterator i1 = AdapterManager.getInstance().getSecuredTaskAdapterManager().getTaskChain(task.getSecure(), startId, task.getId()).iterator();
                if (i1.hasNext()) {
                    SecuredTaskBean stb = (SecuredTaskBean) i1.next();
                    bf.append(!isNew && stb.getId().equals(task.getId()) ? getView(stb).getName() : getView(stb).getSimplePath());
                }
                while (i1.hasNext()) {
                    SecuredTaskBean stb = (SecuredTaskBean) i1.next();
                    bf.append(pathDelimiter());
                    bf.append(!isNew && stb.getId().equals(task.getId()) ? getView(stb).getName() : getView(stb).getSimplePath());
                }
                if (isNew) {
                    bf.append(pathDelimiter());
                    bf.append(newTaskName);
                }
                return bf.toString();
            } else return getName();
        }
        return "";
    }

    public String getFullPath() throws GranException {
        return getFullPath(null);
    }

    public String getFullPathForTaskSelect() throws GranException {
        if (null != task) {
            StringBuffer bf = new StringBuffer();
            Iterator i1 = AdapterManager.getInstance().getSecuredTaskAdapterManager().getTaskChain(task.getSecure(), null, task.getId()).iterator();
            if (i1.hasNext()) {
                SecuredTaskBean stb = (SecuredTaskBean) i1.next();
                bf.append(!isNew && stb.getId().equals(task.getId()) ? getView(stb).getNameForTaskSelect() : getView(stb).getAliasPathForTaskSelect());
            }
            while (i1.hasNext()) {
                SecuredTaskBean stb = (SecuredTaskBean) i1.next();
                bf.append(pathDelimiter());
                bf.append(!isNew && stb.getId().equals(task.getId()) ? getView(stb).getNameForTaskSelect() : getView(stb).getAliasPathForTaskSelect());
            }
            if (isNew) {
                bf.append(pathDelimiter());
                bf.append(newTaskName);
            }
            return bf.toString();
        }
        return "";
    }

    public abstract TaskView getView(SecuredTaskBean t) throws GranException;

    public abstract String getNumber() throws GranException;

    public abstract String getDeadline() throws GranException;

    public abstract String getSubmitdate() throws GranException;

    public abstract String getClosedate() throws GranException;

    public abstract String getUpdatedate() throws GranException;


    public abstract String getHandler() throws GranException;

    public abstract String getSubmitter() throws GranException;

    public abstract String getDescription() throws GranException;

    public abstract String getShortDescription() throws GranException;

    public abstract String getPriority() throws GranException;

    public abstract String getCategory() throws GranException;

    public abstract String getStatus() throws GranException;

    public abstract String getResolution() throws GranException;

    public abstract String getWorkflow() throws GranException;

/*    public abstract String getName() throws GranException; */

    public abstract String getSubmitterPrstatuses() throws GranException;

    public abstract String getHandlerPrstatuses() throws GranException;

    public abstract String getReferencedTasks(String udfName, Collection tasks) throws GranException;

    public abstract UDFValueView getUDFValueView(SecuredUDFValueBean bean) throws GranException;

    public abstract UserView getUserView(SecuredUserBean bean) throws GranException;

    public abstract String getAlias() throws GranException;


}
