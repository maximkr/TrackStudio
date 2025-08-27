package com.trackstudio.secured;

import java.util.concurrent.atomic.AtomicReference;

import com.trackstudio.app.CalculatedValue;
import com.trackstudio.app.session.SessionContext;
import com.trackstudio.exception.GranException;
import com.trackstudio.exception.UserException;
import com.trackstudio.external.TaskUDFLookupScript;
import com.trackstudio.kernel.cache.AbstractPluginCacheItem;
import com.trackstudio.kernel.cache.Action;
import com.trackstudio.kernel.cache.CompiledPluginCacheItem;
import com.trackstudio.kernel.cache.PluginCacheItem;
import com.trackstudio.kernel.cache.PluginCacheManager;
import com.trackstudio.kernel.cache.PluginType;
import com.trackstudio.kernel.cache.UDFCacheItem;

import bsh.EvalError;
import net.jcip.annotations.Immutable;

/**
 * Bean which represents task custom field
 */
@Immutable
public class SecuredTaskUDFBean extends SecuredUDFBean {
    private final AtomicReference<SecuredTaskBean> task = new AtomicReference<SecuredTaskBean>();

    public SecuredTaskUDFBean(UDFCacheItem u, SessionContext sc) throws GranException {
        super(u, sc);
// olegko: very slow
//        if (!sc.taskOnSight(u.getTaskId())) {
//            udfSourceId = null;
//            taskId = null;
//        }
    }

    public boolean isAllowedByACL() throws GranException {
        return getTask().isAllowedByACL();
    }


    public boolean canManage() throws GranException {
        return isAllowedByACL() && getSecure().canAction(Action.manageTaskUDFs, getTaskId());
    }

    public boolean canView() throws GranException {
        return getTask().canView();
    }

    public SecuredTaskBean getTask() throws GranException {
        SecuredTaskBean t = task.get();
        if (t!=null)
            return t;

        SecuredTaskBean t1 = new SecuredTaskBean(taskId, getSecure());
        if (task.compareAndSet(null, t1))
            return t1;
        else
            return task.get();
    }

    public String getTaskId() {
        return taskId;
    }

    public Object getLookupscriptCalc(Secured o) throws GranException {
        AbstractPluginCacheItem plugin = PluginCacheManager.getInstance().find(PluginType.TASK_CUSTOM_FIELD_LOOKUP, getLookupscript());
        if (plugin != null) {
            if (plugin instanceof PluginCacheItem) {

                try {
                    CalculatedValue calc = new CalculatedValue(((PluginCacheItem) plugin).getText(), o);
                    return calc.getValue();
                } catch (EvalError evalError) {
                    throw new GranException(evalError);
                }
            } else if (plugin instanceof CompiledPluginCacheItem) {
                Class compiledClass = ((CompiledPluginCacheItem) plugin).getCompiled();
                Object compiled;
                try {
                    try {
                        if (compiledClass != null) {
                            compiled = compiledClass.newInstance();
                            TaskUDFLookupScript script = (TaskUDFLookupScript) compiled;
                            return script.calculate((SecuredTaskBean) o);
                        } else {
                            throw new UserException("This script " + plugin.getName() +" has type " + plugin.getType() + " for this reason you need to implement a com.trackstudio.external.TaskUDFLookupScript interface!");
                        }
                    } catch (ClassCastException cce) {
                        log.error("Error",cce);
                        throw new UserException("This script " + plugin.getName() +" has type " + plugin.getType() + " for this reason you need to implement a com.trackstudio.external.TaskUDFLookupScript interface!");
                    }
                } catch (InstantiationException e) {
                    log.error("Error", e);
                    throw new GranException(e);
                } catch (IllegalAccessException e) {
                    log.error("Error", e);
                    throw new GranException(e);
                }
            }

        }
        return null;
    }

    @Override
    public void setId(String id) {
    }
}
