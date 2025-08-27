package com.trackstudio.app.adapter.scheduler;

import java.text.ParseException;
import java.util.Calendar;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.quartz.*;
import org.quartz.impl.JobDetailImpl;
import org.quartz.impl.StdSchedulerFactory;
import org.quartz.impl.triggers.CronTriggerImpl;

import com.trackstudio.exception.GranException;
import com.trackstudio.external.ICategoryScheduler;
import com.trackstudio.external.IGeneralScheduler;
import com.trackstudio.kernel.cache.AbstractPluginCacheItem;
import com.trackstudio.kernel.cache.CompiledPluginCacheItem;
import com.trackstudio.kernel.cache.PluginCacheManager;
import com.trackstudio.kernel.cache.PluginType;
import com.trackstudio.kernel.cache.TaskRelatedInfo;
import com.trackstudio.kernel.cache.TaskRelatedManager;
import com.trackstudio.kernel.lock.LockManager;
import com.trackstudio.kernel.manager.KernelManager;
import com.trackstudio.kernel.manager.MessageManager;
import com.trackstudio.secured.SecuredMessageBean;

import net.jcip.annotations.ThreadSafe;

import static com.trackstudio.app.Preferences.*;
import static com.trackstudio.kernel.cache.PluginCacheManager.loadPlugins;
import static org.quartz.TriggerKey.triggerKey;

@ThreadSafe
public class SchedulerManager {
    private static final LockManager lockManager = LockManager.getInstance();
    private static final Log log = LogFactory.getLog(SchedulerManager.class);
    private final static SchedulerManager instance = new SchedulerManager();
    private final ConcurrentHashMap<String, CopyOnWriteArrayList<String>> tasks = new ConcurrentHashMap<String, CopyOnWriteArrayList<String>>();
    private final ConcurrentHashMap<String, ICategoryScheduler> categoryJob = new ConcurrentHashMap<String, ICategoryScheduler>();
    private final CopyOnWriteArrayList<IGeneralScheduler> generalJob = new CopyOnWriteArrayList<IGeneralScheduler>();
    private final static String CATEGORY_ID = "categoryId";
    private final static String INSTANCE = "instance";
    private final StdSchedulerFactory schedulerFactory = new StdSchedulerFactory();
    private static final String CATEGORY_SCHEDULER_GROUP = "CATEGORY_SCHEDULER_GROUP";
    private static final String TRIGGER = "TRIGGER.";
    private static final String EVERY_MIN = "0 * * ? * *";

    private final Scheduler scheduler;

    private SchedulerManager() {
        this.scheduler = this.initScheduler();
        this.loadScripts();
    }

    public static SchedulerManager getInstance() {
        return instance;
    }

    public CopyOnWriteArrayList<IGeneralScheduler> getGeneralJob() {
        return (CopyOnWriteArrayList) this.generalJob.clone();
    }

    public void observedTask(SecuredMessageBean message) throws GranException {
        String categoryId = message.getTask().getCategoryId();
        String taskId = message.getTaskId();
        if (SCHEDULER.equals(scheduler(message.getMstatus().getPreferences()))) {
            putTasks(categoryId, taskId, true);
        } else if (UN_SCHEDULER.equals(scheduler(message.getMstatus().getPreferences()))) {
            putTasks(categoryId, taskId, false);
        }
    }

    private void putTasks(final String categoryId, final String taskId, boolean add) {
        CopyOnWriteArrayList<String> queue = tasks.get(categoryId);
        if (queue == null) {
            tasks.putIfAbsent(categoryId, new CopyOnWriteArrayList<String>());
            queue = tasks.get(categoryId);
        }
        if (add) {
            queue.addIfAbsent(taskId);
        } else {
            queue.remove(taskId);
        }
    }

    private Scheduler initScheduler() {
        try {
            Properties properties = new Properties();
            properties.setProperty("org.quartz.threadPool.threadCount", String.valueOf(Runtime.getRuntime().availableProcessors()));
            schedulerFactory.initialize(properties);
            Scheduler scheduler = schedulerFactory.getScheduler();
            scheduler.start();
            return scheduler;
        } catch (Exception e) {
            log.error("Error", e);
        }
        return null;
    }

    private void loadScripts() {
        try {
            for (Class<IGeneralScheduler> job : loadPlugins("handler.service", IGeneralScheduler.class, PluginType.SCHEDULER_JOB)) {
                generalJob.add(job.newInstance());
            }
            loadScheduler(null);
            initGeneralScheduler();
            initCategoryScheduler();
        } catch (Exception e) {
            log.error("Error", e);
        }
    }

    private void initGeneralScheduler() throws ParseException, SchedulerException {
        for (IGeneralScheduler job : generalJob) {
            loadGeneralTrigger(job);
        }
    }

    private void initCategoryScheduler() throws Exception {
        List<Object[]> msgs = MessageManager.getMessage().getMessageForScheduler();
        Map<String, Object[]> tasks = new LinkedHashMap<String, Object[]>();
        for (Object[] msg : msgs) {
            String taskId = (String) msg[0];
            Object[] lastMsg = tasks.get(taskId);
            if (lastMsg != null) {
                if (((Calendar) msg[1]).getTimeInMillis() > ((Calendar) lastMsg[1]).getTimeInMillis()) {
                    lastMsg = msg;
                }
            } else {
                lastMsg = msg;
            }
            tasks.put(taskId, lastMsg);

        }
        for (Map.Entry<String, Object[]> entry : tasks.entrySet()) {
            loadExistObserver((String) entry.getValue()[0], (String) entry.getValue()[2], (String) entry.getValue()[3]);
        }
        this.loadTriggers();
    }

    private void loadExistObserver(final String taskId, final String  preferences, final String categoryId) throws GranException {
        if (SCHEDULER.equals(scheduler(preferences))) {
            putTasks(categoryId, taskId, true);
        }
    }

    public ICategoryScheduler reloadCategoryScheduler(String categoryId) throws GranException {
        try {
            this.loadScheduler(categoryId);
            ICategoryScheduler categoryScheduler = this.categoryJob.get(categoryId);
            if (categoryScheduler != null) {
                scheduler.unscheduleJob(triggerKey(TRIGGER + categoryId));
                this.loadTrigger(categoryScheduler);
            }
            return categoryScheduler;
        } catch (Exception e) {
            throw new GranException(e);
        }
    }

    private void loadGeneralTrigger(IGeneralScheduler generalTask) throws ParseException, SchedulerException {
        log.debug("load general : " + generalTask.getClass().getName());
        JobDetailImpl job = new JobDetailImpl();
        job.setName(generalTask.getName());
        job.setJobClass(GeneralJob.class);
        JobDataMap dataMap = new JobDataMap();
        dataMap.put(INSTANCE, generalTask);
        job.setJobDataMap(dataMap);

        CronTriggerImpl trigger = new CronTriggerImpl();
        trigger.setName("trigger ".concat(generalTask.getName()));
        String time = generalTask.getCronTime();
        if (!CronExpression.isValidExpression(time)) {
            log.warn(String.format("Cron time is not valid : %s. Script : %s",generalTask.getCronTime(), generalTask.getClass().getName()));
            time = EVERY_MIN;
        }
        trigger.setCronExpression(time);
        trigger.setStartTime(new Date(System.currentTimeMillis() + 1*60*1000)); //wait 1 minutes
        scheduler.scheduleJob(job, trigger);
    }

    private void loadTriggers() throws Exception {
        for (ICategoryScheduler categoryScheduler : categoryJob.values()) {
            this.loadTrigger(categoryScheduler);
        }
    }

    private void loadTrigger(final ICategoryScheduler categoryScheduler) throws ParseException, SchedulerException {
        JobDetailImpl job = new JobDetailImpl();
        job.setKey(new JobKey(categoryScheduler.getCategoryId(), CATEGORY_SCHEDULER_GROUP));
        job.setJobClass(CategoryJob.class);
        JobDataMap dataMap = new JobDataMap();
        dataMap.put(CATEGORY_ID, categoryScheduler.getCategoryId());
        job.setJobDataMap(dataMap);

        CronTriggerImpl trigger = new CronTriggerImpl();
        trigger.setJobKey(new JobKey(categoryScheduler.getCategoryId(), CATEGORY_SCHEDULER_GROUP));
        trigger.setName(TRIGGER + categoryScheduler.getCategoryId());
        trigger.setCronExpression(categoryScheduler.getCronTime());
        scheduler.scheduleJob(job, trigger);
    }

    private void loadScheduler(String categoryId) throws GranException {
        boolean w = lockManager.acquireConnection();
        try {
            List<AbstractPluginCacheItem> scripts = PluginCacheManager.getInstance().list(PluginType.SCHEDULER_JOB).get(PluginType.SCHEDULER_JOB);
            if (scripts != null) {
                for (AbstractPluginCacheItem item : scripts) {
                    if (item != null) {
                        Class<?> compiledClass = ((CompiledPluginCacheItem) item).getCompiled();
                        Object observer = compiledClass.newInstance();
                        if (observer instanceof ICategoryScheduler) {
                            ICategoryScheduler categoryScheduler = (ICategoryScheduler) observer;
                            if (KernelManager.getFind().isCategoryExists(categoryScheduler.getCategoryId())) {
                                log.debug("loaded scheduler for" +
                                        " category : " + categoryScheduler.getCategoryId() +
                                        " time : " + categoryScheduler.getCronTime() +
                                        " script : " + categoryScheduler.getClass().getName());
                                if (categoryId == null || categoryId.equals(categoryScheduler.getCategoryId())) {
                                    categoryJob.put(categoryScheduler.getCategoryId(), categoryScheduler);
                                }
                            }
                        } else if (observer instanceof IGeneralScheduler){
                            if (!generalJob.contains(observer)) {
                                generalJob.add((IGeneralScheduler) observer);
                            }
                        }

                    }
                }
            }
        } catch (Exception e) {
            throw new GranException(e);
        } finally {
            if (w) lockManager.releaseConnection();
        }
    }

    public void shutdown() {
        try {
            for (IGeneralScheduler iScheduler:generalJob) {
                iScheduler.shutdown();
            }
            for (Scheduler scheduler : schedulerFactory.getAllSchedulers()) {
                scheduler.shutdown();
            }
        } catch (Exception e) {
            log.error("Error", e);
        }
    }

	/**
	 * This method schedules a new job
	 * @param detail job details
	 * @param trigger trigger
	 * @throws SchedulerException
	 * @throws ParseException
	 */
	public void scheduleJob(JobDetail detail, Trigger trigger) throws SchedulerException, ParseException {
		this.scheduler.scheduleJob(detail, trigger);
	}

	/**
	 * This method deletes the job from scheduler manager.
	 * @param name job name
	 * @throws SchedulerException
	 */
	public void deleteJob(String name) throws SchedulerException {
		this.scheduler.deleteJob(new JobKey(name));
	}

	public static class CategoryJob implements Job {
        public void execute(JobExecutionContext context) throws JobExecutionException {
            String categoryId = (String) context.getJobDetail().getJobDataMap().get("categoryId");
            CopyOnWriteArrayList<String> list = getInstance().tasks.get(categoryId);
            if (list != null) {
                ICategoryScheduler handler = getInstance().categoryJob.get(categoryId);
                int pos = 0;
                for (String id : list) {
                    log.debug(" observed : " + id + " " + handler.getName());
                    TaskRelatedInfo task = TaskRelatedManager.getInstance().find(id);
                    if (task != null) {
                        handler.execute(task);
                    } else {
                        list.remove(pos);
                        pos--;
                    }
                    pos++;
                }
            }
        }
    }

    public static class GeneralJob implements Job {
        @Override
        public void execute(JobExecutionContext context) {
            boolean lock = lockManager.acquireConnection(this.getClass().getName());
            try {
                Object instance = context.getJobDetail().getJobDataMap().get(INSTANCE);
                if (instance instanceof IGeneralScheduler) {
                    IGeneralScheduler generalScheduler = ((IGeneralScheduler) instance);
                    if (generalScheduler.isUse()) {
                        generalScheduler.execute();
                    }
                }
            } catch (Exception e) {
                log.error("Error occurred in execution general job", e);
            } finally {
                if (lock) lockManager.releaseConnection(this.getClass().getName());
            }
        }
    }
}
