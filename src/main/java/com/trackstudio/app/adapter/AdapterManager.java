package com.trackstudio.app.adapter;

import java.util.ArrayList;
import java.util.concurrent.CopyOnWriteArrayList;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.trackstudio.app.adapter.auth.AuthAdapterManager;
import com.trackstudio.app.adapter.email.FilterNotifyAdapterManager;
import com.trackstudio.app.adapter.email.SecuredSenderAdapterManager;
import com.trackstudio.app.adapter.macros.SecuredMacrosAdapterManager;
import com.trackstudio.app.adapter.store.StoreAdapterManager;
import com.trackstudio.exception.GranException;
import com.trackstudio.kernel.cache.AbstractPluginCacheItem;
import com.trackstudio.kernel.cache.CompiledPluginCacheItem;
import com.trackstudio.kernel.cache.PluginCacheManager;
import com.trackstudio.kernel.cache.PluginType;
import com.trackstudio.securedkernel.SecuredAclAdapterManager;
import com.trackstudio.securedkernel.SecuredAttachmentAdapterManager;
import com.trackstudio.securedkernel.SecuredBookmarkAdapterManager;
import com.trackstudio.securedkernel.SecuredCategoryAdapterManager;
import com.trackstudio.securedkernel.SecuredFilterAdapterManager;
import com.trackstudio.securedkernel.SecuredFindAdapterManager;
import com.trackstudio.securedkernel.SecuredIndexAdapterManager;
import com.trackstudio.securedkernel.SecuredMailImportAdapterManager;
import com.trackstudio.securedkernel.SecuredMessageAdapterManager;
import com.trackstudio.securedkernel.SecuredPrstatusAdapterManager;
import com.trackstudio.securedkernel.SecuredRegistrationAdapterManager;
import com.trackstudio.securedkernel.SecuredReportAdapterManager;
import com.trackstudio.securedkernel.SecuredStepAdapterManager;
import com.trackstudio.securedkernel.SecuredTSInfoAdapterManager;
import com.trackstudio.securedkernel.SecuredTaskAdapterManager;
import com.trackstudio.securedkernel.SecuredTemplateAdapterManager;
import com.trackstudio.securedkernel.SecuredUDFAdapterManager;
import com.trackstudio.securedkernel.SecuredUserAdapterManager;
import com.trackstudio.securedkernel.SecuredWorkflowAdapterManager;

import net.jcip.annotations.ThreadSafe;

/**
 * Общий класс для доступа ко всем адаптерам системы
 */
@ThreadSafe
public class AdapterManager {
    private static final AdapterManager instance = new AdapterManager();
    private final CopyOnWriteArrayList<Adapter> adapters = new CopyOnWriteArrayList<Adapter>();
    private static final Log log = LogFactory.getLog(AdapterManager.class);

    private final SecuredFindAdapterManager securedFindAdapterManager = new SecuredFindAdapterManager();
    private final SecuredBookmarkAdapterManager securedBookmarkAdapterManager = new SecuredBookmarkAdapterManager();
    private final SecuredTSInfoAdapterManager securedTSInfoAdapterManager = new SecuredTSInfoAdapterManager();
    private final SecuredReportAdapterManager securedReportAdapterManager = new SecuredReportAdapterManager();
    private final SecuredWorkflowAdapterManager securedWorkflowAdapterManager = new SecuredWorkflowAdapterManager();
    private final SecuredAclAdapterManager securedACLAdapterManager = new SecuredAclAdapterManager();
    private final SecuredUserAdapterManager securedUserAdapterManager = new SecuredUserAdapterManager();
    private final SecuredAttachmentAdapterManager securedAttachmentAdapterManager = new SecuredAttachmentAdapterManager();
    private final SecuredFilterAdapterManager securedFilterAdapterManager = new SecuredFilterAdapterManager();
    private final SecuredMessageAdapterManager securedMessageAdapterManager = new SecuredMessageAdapterManager();
    private final SecuredPrstatusAdapterManager securedPrstatusAdapterManager = new SecuredPrstatusAdapterManager();
    private final SecuredStepAdapterManager securedStepAdapterManager = new SecuredStepAdapterManager();
    private final SecuredTaskAdapterManager securedTaskAdapterManager = new SecuredTaskAdapterManager();
    private final SecuredUDFAdapterManager securedUDFAdapterManager = new SecuredUDFAdapterManager();
    private volatile AuthAdapterManager authAdapterManager = null;
    private final FilterNotifyAdapterManager filterNotifyAdapterManager = new FilterNotifyAdapterManager();
    private volatile StoreAdapterManager storeAdapterManager = null;
    private final SecuredCategoryAdapterManager securedCategoryAdapterManager = new SecuredCategoryAdapterManager();
    private final SecuredMailImportAdapterManager securedMailImportAdapterManager = new SecuredMailImportAdapterManager();
    private final SecuredRegistrationAdapterManager securedRegistrationAdapterManager = new SecuredRegistrationAdapterManager();
    private final SecuredIndexAdapterManager securedIndexAdapterManager = new SecuredIndexAdapterManager();
    private final SecuredTemplateAdapterManager securedTemplateAdapterManager = new SecuredTemplateAdapterManager();
    private volatile SecuredMacrosAdapterManager securedMacrosAdapterManager = null;
    private volatile SecuredSenderAdapterManager securedSenderAdapterManager = null;

    private AdapterManager() {
    }

    /**
     * Возвращает экземпляр текущего класса
     *
     * @return экземпляр AdapterManager
     */
    public static AdapterManager getInstance() {
        return instance;
    }

    /**
     * Регистрирует новый адаптер по названию
     *
     * @param name название адаптера
     * @throws GranException при необходимости
     */
    public void registerAdapter(String name) throws GranException {
        try {
            for (Adapter ad : adapters) {
                if (ad.getClass().getName().equals(name))
                    return; // this adapter already registered
            }
            try {
                Adapter currentAdapter;
                if (name.endsWith(".class")) {
                    AbstractPluginCacheItem item = PluginCacheManager.getInstance().find(PluginType.MACROS, name);
                    currentAdapter = (Adapter) ((CompiledPluginCacheItem) item).getCompiled().newInstance();
                } else {
                    Class.forName(name);
                    currentAdapter = (Adapter) Class.forName(name).newInstance();
                }
                if (currentAdapter.init()) {
                    log.debug("registerAdapter: " + name + " (" + currentAdapter.getDescription() + ')');
                    adapters.add(currentAdapter);
                }
            } catch (ClassNotFoundException cn) {
                log.error("Exception", cn);
            }
    } catch (Exception e) {
        throw new GranException(e);
    }
}

    /**
     * Возвращает список зарегестрированных адаптеров для интерфейса
     *
     * @param interfaceName название интерфейса
     * @return Список адаптеров
     */
    private ArrayList<Adapter> getAdaptersByInterface(String interfaceName) {
        ArrayList<Adapter> interfaceAdapters = new ArrayList<Adapter>();
        for (Adapter ad : adapters) {
            Class[] interfaces = ad.getClass().getInterfaces();
            int icnt = interfaces.length;
            for (int i = 0; i < icnt; i++) {
                if (interfaces[i].getName().equals(interfaceName)) {
                    interfaceAdapters.add(ad);
                    break;
                }
            }
        }
        return interfaceAdapters;
    }

    /**
     * Возвращает экземпляр SecuredTSInfoAdapterManager
     *
     * @return экземпляр SecuredTSInfoAdapterManager
     */
    public SecuredTSInfoAdapterManager getSecuredTSInfoAdapterManager() {
        return securedTSInfoAdapterManager;
    }

    /**
     * Возвращает экземпляр SecuredFindAdapterManager
     *
     * @return экземпляр SecuredFindAdapterManager
     */
    public SecuredFindAdapterManager getSecuredFindAdapterManager() {
        return securedFindAdapterManager;
    }

    /**
     * Возвращает экземпляр SecuredBookmarkAdapterManager
     *
     * @return экземпляр SecuredBookmarkAdapterManager
     */
    public SecuredBookmarkAdapterManager getSecuredBookmarkAdapterManager() {
        return securedBookmarkAdapterManager;
    }

    /**
     * Возвращает экземпляр SecuredReportAdapterManager
     *
     * @return экземпляр SecuredReportAdapterManager
     */
    public SecuredReportAdapterManager getSecuredReportAdapterManager() {
        return securedReportAdapterManager;
    }

    /**
     * Возвращает экземпляр SecuredAclAdapterManager
     *
     * @return экземпляр SecuredAclAdapterManager
     */
    public SecuredAclAdapterManager getSecuredAclAdapterManager() {
        return securedACLAdapterManager;
    }

    /**
     * Возвращает экземпляр AuthAdapterManager
     *
     * @return экземпляр AuthAdapterManager
     */
    public synchronized AuthAdapterManager getAuthAdapterManager() {
        if (authAdapterManager == null) {
            ArrayList adapters = getAdaptersByInterface("com.trackstudio.app.adapter.AuthAdapter");
            authAdapterManager = new AuthAdapterManager(adapters);
        }
        return authAdapterManager;
    }

    /**
     * Возвращает экземпляр FilterNotifyAdapterManager
     *
     * @return экземпляр FilterNotifyAdapterManager
     */
    public FilterNotifyAdapterManager getFilterNotifyAdapterManager() {
        return filterNotifyAdapterManager;
    }

    /**
     * Возвращает экземпляр StoreAdapterManager
     *
     * @return экземпляр StoreAdapterManager
     */
    public synchronized StoreAdapterManager getStoreAdapterManager() {
        if (storeAdapterManager == null) {
            ArrayList adapters = getAdaptersByInterface("com.trackstudio.app.adapter.StoreAdapter");
            storeAdapterManager = new StoreAdapterManager(adapters);
        }
        return storeAdapterManager;
    }

    /**
     * Возвращает экземпляр SecuredWorkflowAdapterManager
     *
     * @return экземпляр SecuredWorkflowAdapterManager
     */
    public SecuredWorkflowAdapterManager getSecuredWorkflowAdapterManager() {
        return securedWorkflowAdapterManager;
    }

    /**
     * Возвращает экземпляр SecuredUserAdapterManager
     *
     * @return экземпляр SecuredUserAdapterManager
     */
    public SecuredUserAdapterManager getSecuredUserAdapterManager() {
        return securedUserAdapterManager;
    }

    /**
     * Возвращает экземпляр SecuredAttachmentAdapterManager
     *
     * @return экземпляр SecuredAttachmentAdapterManager
     */
    public SecuredAttachmentAdapterManager getSecuredAttachmentAdapterManager() {
        return securedAttachmentAdapterManager;
    }

    /**
     * Возвращает экземпляр SecuredFilterAdapterManager
     *
     * @return экземпляр SecuredFilterAdapterManager
     */
    public SecuredFilterAdapterManager getSecuredFilterAdapterManager() {
        return securedFilterAdapterManager;
    }

    /**
     * Возвращает экземпляр SecuredMessageAdapterManager
     *
     * @return экземпляр SecuredMessageAdapterManager
     */
    public SecuredMessageAdapterManager getSecuredMessageAdapterManager() {
        return securedMessageAdapterManager;
    }

    /**
     * Возвращает экземпляр SecuredPrstatusAdapterManager
     *
     * @return экземпляр SecuredPrstatusAdapterManager
     */
    public SecuredPrstatusAdapterManager getSecuredPrstatusAdapterManager() {
        return securedPrstatusAdapterManager;
    }

    /**
     * Возвращает экземпляр SecuredStepAdapterManager
     *
     * @return экземпляр SecuredStepAdapterManager
     */
    public SecuredStepAdapterManager getSecuredStepAdapterManager() {
        return securedStepAdapterManager;
    }

    /**
     * Возвращает экземпляр SecuredTaskAdapterManager
     *
     * @return экземпляр SecuredTaskAdapterManager
     */
    public SecuredTaskAdapterManager getSecuredTaskAdapterManager() {
        return securedTaskAdapterManager;
    }

    /**
     * Возвращает экземпляр SecuredUDFAdapterManager
     *
     * @return экземпляр SecuredUDFAdapterManager
     */
    public SecuredUDFAdapterManager getSecuredUDFAdapterManager() {
        return securedUDFAdapterManager;
    }

    /**
     * Возвращает экземпляр SecuredCategoryAdapterManager
     *
     * @return экземпляр SecuredCategoryAdapterManager
     */
    public SecuredCategoryAdapterManager getSecuredCategoryAdapterManager() {
        return securedCategoryAdapterManager;
    }

    /**
     * Возвращает экземпляр SecuredMailImportAdapterManager
     *
     * @return экземпляр SecuredMailImportAdapterManager
     */
    public SecuredMailImportAdapterManager getSecuredMailImportAdapterManager() {
        return securedMailImportAdapterManager;
    }

    /**
     * Возвращает экземпляр SecuredRegistrationAdapterManager
     *
     * @return экземпляр SecuredRegistrationAdapterManager
     */
    public SecuredRegistrationAdapterManager getSecuredRegistrationAdapterManager() {
        return securedRegistrationAdapterManager;
    }

    /**
     * Возвращает экземпляр SecuredIndexAdapterManager
     *
     * @return экземпляр SecuredIndexAdapterManager
     */
    public SecuredIndexAdapterManager getSecuredIndexAdapterManager() {
        return securedIndexAdapterManager;
    }

    /**
     * Возвращает экземпляр SecuredTemplateAdapterManager
     *
     * @return экземпляр SecuredTemplateAdapterManager
     */
    public SecuredTemplateAdapterManager getSecuredTemplateAdapterManager() {
        return securedTemplateAdapterManager;
    }


    public synchronized SecuredMacrosAdapterManager getSecuredMacrosAdapterManager() {
        if (securedMacrosAdapterManager == null) {
            ArrayList adapters = getAdaptersByInterface("com.trackstudio.app.adapter.MacrosTaskAdapter");
            securedMacrosAdapterManager = new SecuredMacrosAdapterManager(adapters);
        }
        return securedMacrosAdapterManager;
    }

    public synchronized SecuredSenderAdapterManager getSecuredSenderAdapterManager() {
        if (securedSenderAdapterManager == null) {
            ArrayList adapters = getAdaptersByInterface("com.trackstudio.app.adapter.SenderAdapter");
            securedSenderAdapterManager = new SecuredSenderAdapterManager(adapters);
        }
        return securedSenderAdapterManager;
    }
}
