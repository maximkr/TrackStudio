package com.trackstudio.secured;

import java.util.concurrent.atomic.AtomicReference;

import com.trackstudio.app.CalculatedValue;
import com.trackstudio.app.session.SessionContext;
import com.trackstudio.exception.GranException;
import com.trackstudio.exception.UserException;
import com.trackstudio.external.UserUDFLookupScript;
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
 * Bean which represents user custom field
 */
@Immutable
public class SecuredUserUDFBean extends SecuredUDFBean {
    private final AtomicReference<SecuredUserBean> user = new AtomicReference<SecuredUserBean>();

    public SecuredUserUDFBean(UDFCacheItem u, SessionContext sc) throws GranException {
        super(u, sc);
        if (!sc.userOnSight(u.getUserId())) {
            this.udfSourceId = null;
            this.userId = null;
        }
    }

    public Object getLookupscriptCalc(Secured o) throws GranException {
        AbstractPluginCacheItem plugin = PluginCacheManager.getInstance().find(PluginType.USER_CUSTOM_FIELD_LOOKUP, getLookupscript());
        if (plugin != null) {

            if (plugin instanceof PluginCacheItem) {
                CalculatedValue calc = new CalculatedValue(((PluginCacheItem) plugin).getText(), o);
                try {
                    return calc.getValue();
                } catch (EvalError evalError) {
                    throw new GranException(evalError);
                }
            } else if (plugin instanceof CompiledPluginCacheItem) {
                Class compiledClass = ((CompiledPluginCacheItem) plugin).getCompiled();
                Object compiled;
                try {
                    compiled = compiledClass.newInstance();
                    try {
                        UserUDFLookupScript script = (UserUDFLookupScript) compiled;
                        return script.calculate((SecuredUserBean) o);
                    } catch (ClassCastException cce) {
                        log.error("Error",cce);
                        throw new UserException("This script " + plugin.getName() +" has type " + plugin.getType() + " for this reason you need to implement a com.trackstudio.external.UserUDFLookupScript interface!");
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
        return getUser().isAllowedByACL();
    }


    public boolean canManage() throws GranException {
        return isAllowedByACL() && getSecure().canAction(Action.manageUserUDFs, getUserId());
    }

    public boolean canView() throws GranException {
        return getUser().canView();
    }

    public SecuredUserBean getUser() throws GranException {
        SecuredUserBean u = user.get();
        if (u!=null)
            return u;

        SecuredUserBean u1 = new SecuredUserBean(userId, getSecure());
        if (user.compareAndSet(null, u1))
            return u1;
        else
            return user.get();
    }

    public String getUserId() {
        return userId;
    }
}
