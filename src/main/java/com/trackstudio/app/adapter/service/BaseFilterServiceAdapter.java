package com.trackstudio.app.adapter.service;

import java.util.Calendar;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.trackstudio.app.adapter.AdapterManager;
import com.trackstudio.exception.GranException;
import com.trackstudio.external.IGeneralScheduler;
import com.trackstudio.kernel.lock.LockManager;
import com.trackstudio.kernel.manager.KernelManager;
import com.trackstudio.model.Subscription;
import com.trackstudio.startup.Config;
import com.trackstudio.tools.HibernateUtil;

import net.jcip.annotations.Immutable;

/**
 * This class makes time operations like send email for subscribes
 */
@Immutable
public class BaseFilterServiceAdapter implements IGeneralScheduler {

    private static final LockManager lockManager = LockManager.getInstance();
    private static final Log log = LogFactory.getLog(BaseFilterServiceAdapter.class);
    private static final HibernateUtil hu = new HibernateUtil();

    /**
     * This method returns adapters descriptions
     *
     * @return description
     */
    public String getDescription() {
        return "Base Filter Service Adapter";
    }


    /**
     * Make subscribe
     */
    public String execute() throws Exception {
        boolean w = lockManager.acquireConnection();
        try {
            if (!Config.getInstance().isSendMail()) {
                return null;
            }
            Calendar time = Calendar.getInstance();
//            List subList = hu.getList("select s.id from com.trackstudio.model.Subscription s ");
            List subList = hu.getList("select s.id from com.trackstudio.model.Subscription s where s.nextrun<=? and s.startdate<=? and s.stopdate>=?", time.getTime(), time.getTime(), time.getTime());
            for (Object o : subList) {
                String subId = (String) o;
                updateSubscription(subId);
                AdapterManager.getInstance().getFilterNotifyAdapterManager().processSubscription(subId, null, false);
            }

        } catch (Exception e) {
            log.error("Error", e);
        } finally {
            if (w) lockManager.releaseConnection();
        }
        return null;
    }

    /**
     * This method returns time next running
     *
     * @param subscription sebscription
     * @return time
     */
    private Calendar getNextNextRun(Subscription subscription) {
        // определяем когда мы должны выполнить фильтр в следующий раз.
        Calendar calendar = subscription.getNextrun();
        Calendar now = (Calendar) calendar.clone();
        now.setTimeInMillis(System.currentTimeMillis());
        // перегоняем время вперед, если нужно.
        if (calendar.before(now)) {
        	long diffMillis = now.getTimeInMillis() - calendar.getTimeInMillis();
        	long times = diffMillis/(60000L*subscription.getInterval())+1L;
            calendar.add(Calendar.MINUTE, subscription.getInterval()*(int)times);
        }
        // заносим время в timestamp
        log.debug("next run time " + calendar);
        return calendar;
    }

    /**
     * Update subscription
     *
     * @param subsc id subscription
     * @throws GranException for necessary
     */
    void updateSubscription(String subsc) throws GranException {
        log.trace("updateSubscription call");
        Subscription obj = KernelManager.getFind().findSubscription(subsc);
        obj.setNextrun(getNextNextRun(obj));
        hu.updateObject(obj);
    }

    @Override
    public String getCronTime() {
        return "0/10 * * * * ?";
    }

    @Override
    public String getName() {
        return "Base Filter Service Adapter";
    }

    @Override
    public boolean isUse() {
        return Config.getInstance().isSendMail();
    }

	@Override
	public String getClassName() {
		return this.getClass().getName();
	}

	@Override
    public void shutdown() {}
}