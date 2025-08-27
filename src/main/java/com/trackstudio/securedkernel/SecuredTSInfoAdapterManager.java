package com.trackstudio.securedkernel;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.trackstudio.app.session.SessionContext;
import com.trackstudio.exception.GranException;
import com.trackstudio.kernel.manager.KernelManager;

import net.jcip.annotations.Immutable;

/**
 * Класс SecuredTSInfoAdapterManager содержит методы для получения информации о TS
 */
@Immutable
public class SecuredTSInfoAdapterManager {

    private static final Log log = LogFactory.getLog(SecuredTSInfoAdapterManager.class);

    public static final Long PERIOD_MONTH = (long) 1000 * 30L * 24L * 60L * 60L;

    /**
     * Возвращает версию TS
     *
     * @param sc сессия пользователя
     * @return версия
     */
    public String getTSVersion(SessionContext sc) {
        log.trace("getTSVersion");
        return KernelManager.getTSInfo().getTSVersion();
    }

    
}