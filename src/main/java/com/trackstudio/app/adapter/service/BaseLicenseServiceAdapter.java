package com.trackstudio.app.adapter.service;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.trackstudio.app.session.SessionContext;
import com.trackstudio.app.session.SessionManager;
import com.trackstudio.external.IGeneralScheduler;
import com.trackstudio.kernel.lock.LockManager;

public class BaseLicenseServiceAdapter implements IGeneralScheduler {

    private static final LockManager lockManager = LockManager.getInstance();
    private static final Log log = LogFactory.getLog(BaseFilterServiceAdapter.class);


    @Override
    public String getClassName() {
        return this.getClass().getName();
    }

    @Override
    public String getCronTime() {
        return "0 0 0 * * ?";
    }

    @Override
    public String getName() {
        return "Base License Service Adapter";
    }

    @Override
    public String execute() throws Exception {
        boolean w = lockManager.acquireConnection();
        try {
            SessionManager sessionManager = SessionManager.getInstance();
            for (SessionContext sc : sessionManager.getSessions()) {
                if (sc.getUser().isExpired()) {
                    sessionManager.remove(sc.getId());
                    log.info(sc.getUser().getLogin() + " was kicked as expired");
                }
            }
        } catch (Exception e) {
            log.error("Error", e);
        } finally {
            if (w) lockManager.releaseConnection();
        }
        return null;
    }

    @Override
    public boolean isUse() {
        return true;
    }

    @Override
    public void shutdown() {}

}
