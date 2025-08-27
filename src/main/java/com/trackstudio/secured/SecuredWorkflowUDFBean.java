package com.trackstudio.secured;

import java.util.concurrent.atomic.AtomicReference;

import com.trackstudio.app.CalculatedValue;
import com.trackstudio.app.adapter.AdapterManager;
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
import com.trackstudio.kernel.manager.KernelManager;

import bsh.EvalError;
import net.jcip.annotations.Immutable;

/**
 * Bean which represents workflow custom field
 */
@Immutable
public class SecuredWorkflowUDFBean extends SecuredUDFBean {
    private final AtomicReference<SecuredWorkflowBean> workflow = new AtomicReference<SecuredWorkflowBean>();


    public SecuredWorkflowUDFBean(UDFCacheItem u, SessionContext sec) throws GranException {
        super(u, sec);
        String tid = KernelManager.getFind().findWorkflow(u.getWorkflowId()).getTask().getId();
//      olegko: VERY SLOW
//        if (sc.taskOnSight(tid)) {
            this.taskId = tid;
//        } else {
//            this.udfSourceId = null;
//            this.workflowId = null;
//            this.taskId = null;
//        }
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
                    if (compiledClass != null) {
                        compiled = compiledClass.newInstance();
                        if (compiled instanceof TaskUDFLookupScript) {
                            TaskUDFLookupScript script = (TaskUDFLookupScript) compiled;
                            return script.calculate((SecuredTaskBean) o);
                        } else {
                            throw new UserException("This script " + plugin.getName() +" has type " + plugin.getType() + " for this reason you need to implement a implement com.trackstudio.external.TaskUDFLookupScript interface!");
                        }
                    }
                } catch (InstantiationException e) {
                    throw new GranException(e);
                } catch (IllegalAccessException e) {
                    throw new GranException(e);
                }
            }
            return null;
        } else {
            return null;
        }
    }

    public boolean isAllowedByACL() throws GranException {
        return getWorkflow().isAllowedByACL();
    }


    public boolean canManage() throws GranException {
        return isAllowedByACL() && getSecure().canAction(Action.manageWorkflows, getTaskId());
    }

    public boolean canView() throws GranException {
        return getWorkflow().canView();
    }

    public SecuredWorkflowBean getWorkflow() throws GranException {
        SecuredWorkflowBean w = workflow.get();
        if (w!=null)
            return w;

        SecuredWorkflowBean w1 = AdapterManager.getInstance().getSecuredFindAdapterManager().findWorkflowById(getSecure(), workflowId);
        if (workflow.compareAndSet(null, w1))
            return w1;
        else
            return workflow.get();
    }

    public String getWorkflowId() {
        return workflowId;
    }

    public String getTaskId() {
        return taskId;
    }
}
