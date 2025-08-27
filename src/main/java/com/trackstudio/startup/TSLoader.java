package com.trackstudio.startup;

import java.lang.management.ManagementFactory;

import javax.management.MBeanServer;
import javax.management.ObjectName;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.struts.action.ActionServlet;
import org.apache.struts.action.PlugIn;
import org.apache.struts.config.ModuleConfig;

import com.trackstudio.app.adapter.scheduler.SchedulerManager;
import com.trackstudio.app.session.SessionManager;
import com.trackstudio.exception.GranException;
import com.trackstudio.exception.UserException;
import com.trackstudio.jmx.CacheMXBeanImpl;
import com.trackstudio.jmx.MailImportMXBeanImpl;
import com.trackstudio.jmx.TimingTaskMXBeanImpl;
import com.trackstudio.jmx.UserSessionsMXBeanImpl;
import com.trackstudio.kernel.cache.PluginCacheManager;
import com.trackstudio.kernel.cache.TaskRelatedManager;
import com.trackstudio.kernel.cache.UserRelatedManager;
import com.trackstudio.kernel.lock.LockManager;
import com.trackstudio.kernel.manager.KernelManager;

import net.jcip.annotations.ThreadSafe;
import net.sf.ehcache.CacheManager;

/**
 * Класс-загрузчик системы
 */
@ThreadSafe
public class TSLoader implements PlugIn {
    private static final LockManager lockManager = LockManager.getInstance();
    private static final Log log = LogFactory.getLog(TSLoader.class);
    private static volatile GranException initException = null;

    /**
     * Возвращает было ло исключение при старте системы
     *
     * @return GranException
     */
    public static GranException getInitException() {
        return initException;
    }

    /**
     * Срабатывает при закрытии приложения
     */
    public void destroy() {
    }

    /**
     * Срабатывает при старте прилоежния
     *
     * @param actionServlet действие
     * @param config        конфиг
     * @throws ServletException при необходимости
     */
    public void init(ActionServlet actionServlet, ModuleConfig config) throws ServletException {
        boolean lock = lockManager.acquireConnection(TSLoader.class.getName());
        try {
            ServletContext servletContext = actionServlet.getServletContext();
            I18n.loadConfig(servletContext);

            if (Config.loadConfig(servletContext)){
                Config.getInstance().checkAndSetConfigParameters();
                int delay = Config.getInstance().getStartupDelay();
                if (delay != 0)
                    Thread.sleep((long) (delay * 1000));
                AttachmentValidator.getInstance().validate();
                DatabaseValidator.getInstance().validate();
                CacheManager.create();
                TaskRelatedManager.getInstance(); // Must be single threaded first time, otherwise partially initialized objects occurs
                UserRelatedManager.getInstance();
                KernelManager.getIndex();
                SessionManager.getInstance();
                MBeanServer mbs = ManagementFactory.getPlatformMBeanServer();
                mbs.registerMBean(new CacheMXBeanImpl(), new ObjectName("com.trackstudio.jmx:type="+CacheMXBeanImpl.class.getSimpleName()));
                mbs.registerMBean(TimingTaskMXBeanImpl.getInstance(), new ObjectName("com.trackstudio.jmx:type="+TimingTaskMXBeanImpl.class.getSimpleName()));
                mbs.registerMBean(new UserSessionsMXBeanImpl(), new ObjectName("com.trackstudio.jmx:type="+UserSessionsMXBeanImpl.class.getSimpleName()));
                mbs.registerMBean(MailImportMXBeanImpl.getInstance(), new ObjectName("com.trackstudio.jmx:type="+MailImportMXBeanImpl.class.getSimpleName()));
                PluginCacheManager.getInstance();
                SchedulerManager.getInstance();
                Config.getInstance().registerAdapters("trackstudio.adapters.properties");
/*
                int counter = 0;
                long time = System.currentTimeMillis();
                for (int i=0;i<100000;i++) {
                    TaskRelatedInfo tci = TaskRelatedManager.getInstance().findByNumber(String.valueOf(i));
                    if (tci==null)
                        continue;
                    counter++;

                }
                log.info("Search time = " + (System.currentTimeMillis() - time + " items found " + counter));
                */
            }
        } catch (UserException e) {
            initException = e;
            log.error("Can't initialize", e);
        } catch (Exception e) {
            initException = new GranException(e);
            log.error(e.getMessage(), e);
        } finally {
            if (lock) lockManager.releaseConnection(TSLoader.class.getName());
        }
    }
}
