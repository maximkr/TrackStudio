/*
 * @(#)TaskManager.java
 * Copyright 2009 TrackStudio, Inc. All rights reserved.
 */
package com.trackstudio.kernel.manager;

import java.io.File;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
import java.util.concurrent.atomic.AtomicLong;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.store.FSDirectory;

import com.trackstudio.app.Defaults;
import com.trackstudio.app.Preferences;
import com.trackstudio.exception.GranException;
import com.trackstudio.exception.UserException;
import com.trackstudio.kernel.cache.AttachmentCacheItem;
import com.trackstudio.kernel.cache.CategoryCacheItem;
import com.trackstudio.kernel.cache.CategoryCacheManager;
import com.trackstudio.kernel.cache.MessageCacheItem;
import com.trackstudio.kernel.cache.TaskRelatedInfo;
import com.trackstudio.kernel.cache.TaskRelatedManager;
import com.trackstudio.kernel.cache.UserRelatedManager;
import com.trackstudio.kernel.lock.LockManager;
import com.trackstudio.model.Category;
import com.trackstudio.model.Filter;
import com.trackstudio.model.Longtext;
import com.trackstudio.model.Notification;
import com.trackstudio.model.Priority;
import com.trackstudio.model.Report;
import com.trackstudio.model.Status;
import com.trackstudio.model.Subscription;
import com.trackstudio.model.Task;
import com.trackstudio.model.User;
import com.trackstudio.model.Usersource;
import com.trackstudio.startup.Config;
import com.trackstudio.tools.audit.trail.AuditUtil;

import net.jcip.annotations.Immutable;

/**
 * Класс TaskManager содержит методы для работы с задачами
 */
@Immutable
public class TaskManager extends KernelManager {

	private static final String className = "TaskManager.";
	private static final Log log = LogFactory.getLog(TaskManager.class);
	private static final TaskManager instance = new TaskManager();
	private static final LockManager lockManager = LockManager.getInstance();
	private final CategoryCacheManager cacheManager;
	private final TaskRelatedManager relatedManager;

	private final AtomicLong ids;

	/**
	 * Конструктор по умолчанию
	 */
	private TaskManager() {
		this.cacheManager = CategoryCacheManager.getInstance();
		this.relatedManager = TaskRelatedManager.getInstance();
		this.ids = new AtomicLong(this.loadMaxNumber());
	}

	private long loadMaxNumber() {
		try {
			return (Long) hu.getList(
							"select max(cast(task.number as long)) from com.trackstudio.model.Task as task"
					).get(0);
		} catch (Exception e) {
			throw new IllegalStateException(e);
		}
	}

	/**
	 * Возвращает экземпляр текущего класса
	 *
	 * @return Экземпляр TaskManager
	 */
	protected static TaskManager getInstance() {
		return instance;
	}

	/**
	 * Редактирует указанную задачу
	 *
	 * @param taskId         ID редактируемой задачи
	 * @param shortname      Алиас задачи
	 * @param name           Название задачи
	 * @param description    Описание задачи
	 * @param budget         Бюджет задачи
	 * @param deadline       Дедлайн задачи
	 * @param priorityId     ID приоритета задачи
	 * @param parentId       ID родительской задачи
	 * @param handlerUserId  ID ответственного пользователя
	 * @param handlerGroupId ID ответственного статуса
	 * @throws GranException при необходимости
	 */
	public void updateTask(String taskId, SafeString shortname, SafeString name, SafeString description, Long budget, Calendar deadline,
	                       String priorityId, String parentId, String handlerUserId, String handlerGroupId, Calendar submitDate, Calendar updateDate) throws GranException {
		log.trace("updateTask");
		boolean w = lockManager.acquireConnection(className);
		lockManager.getLock(taskId).lock();
		try {
			String oldlongtextid = null;
			Task par = null;
			String oldParent = null;
			boolean shortDesc = description == null || description.length() <= 2000;
			// log.debug("Task desc hu : " + description + " shortDesc : " + shortDesc);
			Task task = KernelManager.getFind().findTask(taskId);
			String usersourceId = KernelManager.getUser().getUsersource(handlerUserId, handlerGroupId);
			if (taskId.equals(parentId))
				parentId = null;
			if (parentId != null && parentId.length() != 0)
				par = (Task) hu.getObject(Task.class, parentId);
			if (task.getParent() != null)
				oldParent = task.getParent().getId();

			Priority pri = null;
			if (priorityId != null && priorityId.length() != 0)
				pri = (Priority) hu.getObject(Priority.class, priorityId);
			else {
				Category category = (Category) hu.getObject(Category.class, task.getCategory().getId());
				pri = Defaults.getPriority(category.getWorkflow().getId());
			}
			task.setShortname(shortname != null ? shortname.toString() : null);
			task.setName(name != null ? name.toString() : null);

			// in-db task has longtext, remember id and clear id
			if (task.getLongtext() != null)
				oldlongtextid = task.getLongtext().getId();

			// new description is also long, so re-create longtext for it with old id
			if (description != null && description.length() > 2000) {
				String longtextId;
				if (oldlongtextid != null && KernelManager.getLongText().getLongtext(oldlongtextid).equals(description.toString())) {
					longtextId = oldlongtextid;
					//System.out.println("use old description");
				} else {
					longtextId = KernelManager.getLongText().createLongtext(oldlongtextid, description.toString(), false);
					//System.out.println("recreate old description");
				}

				task.setLongtext((Longtext) hu.getObject(Longtext.class, longtextId));
				task.setDescription(null);
			} else {
				// set description
				task.setLongtext(null);
				task.setDescription(description != null ? description.toString() : null);
				//System.out.println("Short description");
			}

			task.setBudget(budget);
			task.setDeadline(deadline);

			task.setHandler((Usersource) hu.getObject(Usersource.class, usersourceId));
			if (submitDate != null)
				task.setSubmitdate(submitDate);
			if (updateDate != null) {
				task.setUpdatedate(updateDate);
			} else {
				task.setUpdatedate(Calendar.getInstance());
			}

			Status status = KernelManager.getFind().findStatus(task.getStatus().getId());
			if (status.isFinish() && updateDate != null && task.getClosedate() == null) {
				task.setClosedate(updateDate);
			}

			task.setPriority(pri);
			if (par != null) {
				task.setParent(par);
				//#19086
				//������� �������, ������� ����� �������� ������ ����� ���������� � ������� ��� ���� �������� notification/subscribe rules, ���, �������, ������� ������ ���� ���������� ������������� ������ All
				List<Filter> oldFilters = KernelManager.getFilter().getAllTaskFilterList(oldParent);
				List<Filter> newFilters = KernelManager.getFilter().getAllTaskFilterList(par.getId());
				oldFilters.removeAll(newFilters);
				List<String> subtasks = new ArrayList<String>();
				for (TaskRelatedInfo taskInfo : TaskRelatedManager.getInstance().getChildrenRecursive(taskId)) {
					subtasks.add(taskInfo.getId());
				}

				subtasks.add(taskId);
				for (Filter flt : oldFilters) {
					for (Notification n : KernelManager.getFilter().getFilterNotificationList(flt.getId())) {
						if (subtasks.contains(n.getTask().getId()))
							hu.deleteObject(Notification.class, n.getId());
					}
					for (Subscription n : KernelManager.getFilter().getFilterSubscriptionList(flt.getId())) {
						if (subtasks.contains(n.getTask().getId()))
							hu.deleteObject(Subscription.class, n.getId());
					}
					for (Report r : KernelManager.getReport().getFilterReportList(flt.getId())) {
						if (subtasks.contains(r.getTask().getId()))
							((Report) hu.getObject(Report.class, r.getId())).setFilter("1");
					}
				}
			}
			hu.updateObject(task);
			hu.cleanSession();

			// if replace old long description with new short - delete leaked longtext
			if (oldlongtextid != null && shortDesc)
				KernelManager.getLongText().deleteLongtext(oldlongtextid);

			if (oldParent == null || oldParent.equals(parentId) || par == null) {
				TaskRelatedManager.getInstance().invalidateTask(task.getId());
			} else {
				TaskRelatedManager.getInstance().invalidateAclWhenMove(task.getId());
				TaskRelatedManager.getInstance().invalidateWhenMove(task.getId(), oldParent, par.getId());
				CategoryCacheManager.getInstance().invalidate();
			}
			hu.cleanSession();
		} catch (Exception e) {
			throw new GranException(e);
		} finally {
			if (w) lockManager.releaseConnection(className);
			lockManager.getLock(taskId).unlock();
		}
	}

	/**
	 * Создает новую задачу
	 *
	 * @param parentId   ID родительской задачи
	 * @param userId     ID пользователя
	 * @param categoryId ID категории
	 * @param name       Название задачи
	 * @param deadline   Дедлайн задачи
	 * @return ID созданной задачи
	 * @throws GranException при необходимости
	 */
	public String createTask(String parentId, String userId, String categoryId, SafeString name, Calendar deadline, Calendar submitDate, Calendar updateDate) throws GranException {
		TaskRelatedInfo parentTask = TaskRelatedManager.getInstance().find(parentId);
		return _importTask(parentId, categoryId, null, name, null, null, deadline, null, parentTask.getHandlerUserId(), parentTask.getHandlerGroupId(), userId, submitDate, updateDate, null, null, null);

	}

	/**
	 * Создает новую задачу
	 *
	 * @param parentId   ID родительской задачи
	 * @param userId     ID пользователя
	 * @param categoryId ID категории
	 * @param name       Название задачи
	 * @param deadline   Дедлайн задачи
	 * @return ID созданной задачи
	 * @throws GranException при необходимости
	 */
	public String createTask(String parentId, String userId, String categoryId, SafeString name, Calendar deadline) throws GranException {
		TaskRelatedInfo parentTask = TaskRelatedManager.getInstance().find(parentId);
		return _importTask(parentId, categoryId, null, name, null, null, deadline, null, parentTask.getHandlerUserId(), parentTask.getHandlerGroupId(), userId, null, null, null, null, null);

	}

	/**
	 * Создает новую задачу
	 *
	 * @param parentId   ID родительской задачи
	 * @param userId     ID пользователя
	 * @param categoryId ID категории
	 * @param name       Название задачи
	 * @param deadline   Дедлайн задачи
	 * @return ID созданной задачи
	 * @throws GranException при необходимости
	 */
	public String createTask(String parentId, String userId, String categoryId, SafeString name, Calendar deadline, Calendar submitDate, Calendar updateDate, String statusId) throws GranException {
		TaskRelatedInfo parentTask = TaskRelatedManager.getInstance().find(parentId);
		return _importTask(parentId, categoryId, null, name, null, null, deadline, null, parentTask.getHandlerUserId(), parentTask.getHandlerGroupId(), userId, submitDate, updateDate, null, statusId, null);

	}

	/**
	 * Импортируется задача
	 *
	 * @param parentId       ID родительской задачи
	 * @param categoryId     ID категории
	 * @param shortname      Алиас
	 * @param name           Название задачи
	 * @param budget         Бюдэет
	 * @param deadline       Дедлайн
	 * @param priorityId     ID приоритета
	 * @param handlerUserId  ID ответственного пользователя
	 * @param handlerGroupId ID ответственного статуса
	 * @param userId         ID автора задачи
	 * @param submitDate     Дата создания задачи
	 * @param updateDate     Дата редактирования задачи
	 * @param closeDate      Дата закрытия задачи
	 * @param statusId       ID текущего состояния задачи
	 * @param resolutionId   ID резолюции
	 * @return ID созданной задачи
	 * @throws GranException при необходимости
	 */
	private String _importTask(String parentId, String categoryId, SafeString shortname, SafeString name, SafeString description, Long budget,
	                           Calendar deadline, String priorityId, String handlerUserId, String handlerGroupId, String userId,
	                           Calendar submitDate, Calendar updateDate, Calendar closeDate, String statusId, String resolutionId) throws GranException {
		boolean w = lockManager.acquireConnection(className);
		try {
			String workflowId = KernelManager.getFind().findCategory(categoryId).getWorkflow().getId();
			statusId = statusId == null ? KernelManager.getWorkflow().getStartStateId(workflowId) : statusId;
			Task task = new Task(name != null ? name.toString() : null, categoryId, statusId, userId, parentId);
			Calendar st = Calendar.getInstance();
			if (submitDate == null)
				task.setSubmitdate(st);
			else
				task.setSubmitdate(submitDate);
			if (updateDate == null)
				task.setUpdatedate(st);
			else
				task.setUpdatedate(updateDate);
			task.setDeadline(deadline);
			if (priorityId == null)
				task.setPriority(Defaults.getPriority(workflowId));
			else
				task.setPriority((Priority) hu.getObject(Priority.class, priorityId));
			task.setHandler(KernelManager.getUser().getUsersource(handlerUserId, handlerGroupId));
			task.setNumber(String.valueOf(this.ids.incrementAndGet()));
			task.setAbudget(0L);
			task.setShortname(shortname != null ? shortname.toString() : null);
			task.setBudget(budget);
			task.setDeadline(deadline);
			if (closeDate != null)
				task.setClosedate(closeDate);
			if (resolutionId != null)
				task.setResolution(resolutionId);
			String newtaskid = hu.createObject(task);
			log.debug("CLOSE SESSION TaskManager import");
			hu.cleanSession();
			TaskRelatedManager.getInstance().invalidateWhenInsert(newtaskid);
			return newtaskid;
		} catch (Exception e) {
			throw new GranException(e);
		} finally {
			if (w) lockManager.releaseConnection(className);
		}
	}

	/**
	 * Обновляется дата закрытия задачи
	 *
	 * @param taskId    ID задачи
	 * @param closeDate Дата закрытия
	 * @throws GranException при необзодимости
	 */
	public void updateTaskCloseDate(String taskId, Calendar closeDate) throws GranException {
		boolean w = lockManager.acquireConnection(className);
		lockManager.getLock(taskId).lock();
		try {
			Task task = KernelManager.getFind().findTask(taskId);
			task.setClosedate(closeDate);
			hu.updateObject(task);
			hu.cleanSession();
			TaskRelatedManager.getInstance().invalidateTask(task.getId());
		} finally {
			if (w) lockManager.releaseConnection(className);
			lockManager.getLock(taskId).unlock();
		}
	}

	public void updateTaskStatus(String taskId, String statusId) throws GranException {
		boolean w = lockManager.acquireConnection(className);
		lockManager.getLock(taskId).lock();
		try {
			Task task = KernelManager.getFind().findTask(taskId);
			task.setStatus(KernelManager.getFind().findStatus(statusId));
			hu.updateObject(task);
			hu.cleanSession();
			TaskRelatedManager.getInstance().invalidateTask(task.getId());
		} finally {
			if (w) lockManager.releaseConnection(className);
			lockManager.getLock(taskId).unlock();
		}
	}

	/**
	 * Импортируется новая задача
	 *
	 * @param parentId       ID родительской задачи
	 * @param categoryId     ID категории
	 * @param shortname      Алиас
	 * @param name           Название задачи
	 * @param description    Описание задачи
	 * @param budget         Бюдэет
	 * @param deadline       Дедлайн
	 * @param priorityId     ID приоритета
	 * @param handlerUserId  ID ответственного пользователя
	 * @param handlerGroupId ID ответственного статуса
	 * @param userId         ID автора задачи
	 * @param submitDate     Дата создания задачи
	 * @param updateDate     Дата редактирования задачи
	 * @param closeDate      Дата закрытия задачи
	 * @param statusId       ID текущего состояния задачи
	 * @param resolutionId   ID резолюции
	 * @return ID созданной задачи
	 * @throws GranException при необходимости
	 */
	public String importTask(String parentId, String categoryId, SafeString shortname, SafeString name, SafeString description, Long budget,
	                         Calendar deadline, String priorityId, String handlerUserId, String handlerGroupId, String userId,
	                         Calendar submitDate, Calendar updateDate, Calendar closeDate, String statusId, String resolutionId) throws GranException {

		String newId = _importTask(parentId, categoryId, shortname, name, description, budget, deadline, priorityId, handlerUserId, handlerGroupId, userId, submitDate, updateDate, closeDate, statusId, resolutionId);
		if (newId != null) {
			updateTask(newId, shortname, name, description,
					budget, deadline, priorityId, parentId,
					handlerUserId, handlerGroupId, submitDate, updateDate);
		}
		return newId;
	}

	/**
	 * Возвращает ID задачи по ее алиасу
	 *
	 * @param name Название задачи
	 * @return ID задачи
	 * @throws GranException при необзодимости
	 */
	public String findByShortName(String name) throws GranException {
		TaskRelatedInfo tci = TaskRelatedManager.getInstance().findByShortName(name);
		if (tci!=null) {
			return tci.getId();
		} else {
			return null;
		}
	}

	/**
	 * Возвращает ID задачи по ее номеру
	 *
	 * @param number Номер задачи
	 * @return ID задачи
	 * @throws GranException при необзодимости
	 */
	public String findByNumber(String number) {
		TaskRelatedInfo tci = TaskRelatedManager.getInstance().findByNumber(number);
		if (tci!=null) {
			return tci.getId();
		} else {
			return null;
		}
	}

	private interface OneArg {
		public String invoke(String key, TaskRelatedInfo info);
	}


	private static final OneArg predictor = new OneArg() {
		@Override
		public String invoke(String key, TaskRelatedInfo info) {
			String number = key;
			if (number.startsWith("#")) number = number.substring(1);
			if (info.getNumber().indexOf(number) != -1) {
				return info.getId();
			} else if (info.getShortname() != null && !info.getShortname().isEmpty() && info.getShortname().toUpperCase().indexOf(key.toUpperCase()) != -1) {
				return info.getId();
			} else if (info.getName().toUpperCase().indexOf(key.toUpperCase()) != -1) {
				return info.getId();
			} else {
				return null;
			}
		}
	};

	/**
	 * This method searchs task id by a word-key. It checks first of all number, aliace, name. It uses {@link String#indexOf(String)} method
	 * see details {@link #findByNumberByShortNameByNameFromCache}
	 *
	 * @param key word-key
	 * @return task id or null
	 * @throws GranException for necessery
	 */
	public String findByTaskIdIndexOf(String key) throws GranException {
		return findByNumberByShortNameByNameFromCache(key, predictor);
	}

	public List<String> getListByKey(String key, int limit) throws GranException {
		return searchCache(key, limit, predictor);
	}

	/**
	 * This method for search task id of the cache. Seacth for number, short name, name
	 *
	 * @param key   key search
	 * @param block interface for search
	 * @return task id task ID
	 * @throws GranException for need
	 *
	 */
	// TODO: should be replaced with TaskRelatedManager calls
	private String findByNumberByShortNameByNameFromCache(String key, OneArg block) throws GranException {
		boolean r = lockManager.acquireConnection(className);
		try {
			if (key == null) {
				return null;
			}
			List<TaskRelatedInfo> tree = TaskRelatedManager.getInstance().getCacheContents();
			String taskId;
			for (TaskRelatedInfo info : tree) {
				taskId = block.invoke(key, info);
				if (taskId != null) {
					return taskId;
				}
			}
			return null;
		} finally {
			if (r) lockManager.releaseConnection(className);
		}
	}

	/**
	 * This method searches in a cache by name, short name, number
	 * @param key
	 * @param block
	 * @return
	 * @throws GranException
	 */
	private List<String> searchCache(String key, int limit, OneArg block) throws GranException {
		boolean r = lockManager.acquireConnection(className);
		try {
			if (key == null) {
				return null;
			}
			List<String> result = new ArrayList<String>();
			List<TaskRelatedInfo> tree = TaskRelatedManager.getInstance().getCacheContents();
			String taskId;
			for (TaskRelatedInfo info : tree) {
				taskId = block.invoke(key, info);
				if (taskId != null) {
					result.add(taskId);
				}
				if (result.size() >= limit) {
					break;
				}
			}
			return result;
		} finally {
			if (r) lockManager.releaseConnection(className);
		}
	}

	/**
	 * Возвращает колличество сообщений для задачи
	 *
	 * @param id ID хажачи
	 * @return колличество сообщений
	 * @throws GranException при необходимости
	 */
	public int getMessageCount(String id) throws GranException {
		List<Number> list = hu.getList("select count(m.id) from com.trackstudio.model.Message m where m.task =?", id);
		if (list.isEmpty())
			return 0;
		else
			return (list.get(0)).intValue();
	}

	/**
	 * Удаляет задачу (вспомогательный приватный метод)
	 *
	 * @param taskId ID удаляемой задачи
	 * @throws GranException при необходимости
	 */
	private void removeTask(String taskId) throws GranException {
		boolean w = lockManager.acquireConnection(className);
		lockManager.getLock(taskId).lock();
		try {
			List<AttachmentCacheItem> attachmentList = KernelManager.getAttachment().getAttachmentList(taskId, null, null);
			Task task = (Task) hu.getObject(Task.class, taskId);
			List<User> users = hu.getList("select user from com.trackstudio.model.User as user where user.defaultProject=?", taskId);
			for (User user : users) {
				user.setNullDefaultProject();
				hu.updateObject(user);
				UserRelatedManager.getInstance().invalidateUser(user.getId(), true); // invalidateAll default project
			}
			TaskRelatedManager.getInstance().invalidateTaskTypeUDF(taskId);
			hu.executeDML("update Udf u set initialtask=null where initialtask=?", taskId);
			for (AttachmentCacheItem anAttachmentList : attachmentList)
				KernelManager.getAttachment().deleteAttachment(anAttachmentList.getId(), false);
			hu.deleteObject(Task.class, task.getId());
			TaskRelatedManager.getInstance().invalidateWhenRemove(taskId);
			CategoryCacheManager.getInstance().invalidate();
			hu.cleanSession();
		} catch (UserException de) {
			throw de;
		} catch (Exception e) {
			throw new GranException(e);
		} finally {
			if (w) lockManager.releaseConnection(className);
			lockManager.getLock(taskId).unlock();
		}
	}

	/**
	 * Удаляет задачу
	 *
	 * @param taskId ID удаляемой задачи
	 * @throws GranException при необходимости
	 */
	public void deleteTask(String taskId, String login) throws GranException {
		lockManager.getLock(taskId).lock();
		try {

			List<String> taskToDelete = new ArrayList<String>();
			List<String> level = new ArrayList<String>();
			level.add(taskId);
			while (!level.isEmpty()) {
				taskToDelete.addAll(level);
				List<String> children = new ArrayList<String>();
				for (String tid : level) {
					List<String> childList = TaskRelatedManager.getInstance().getChildrenId(tid);
					children.addAll(childList);
				}
				level = children;
			}
			Collections.reverse(taskToDelete);
			for (String aTaskToDelete : taskToDelete) {
				AuditUtil.addAuditAboutDeletedTasks(aTaskToDelete, login);
				removeTask(aTaskToDelete);
			}
		} finally {
			lockManager.getLock(taskId).unlock();
		}
	}

	/**
	 * Возвращает задачи, подобные указазнной
	 *
	 * @param taskId ID задачи, для которой изем подобные
	 * @return карта (Map), содержащая ID найденных задач и числовые значения, означающие степень "подобности"
	 * @throws GranException при необходимости
	 */
	public Map<String, String> findSimilar(String taskId) throws GranException {
		boolean r = lockManager.acquireConnection(className);
		try {
			Map<String, String> list = new HashMap<String, String>();
			if (DirectoryReader.indexExists(FSDirectory.open(new File(Config.getInstance().getIndexDir()).toPath()))) {
				TaskRelatedInfo stb = TaskRelatedManager.getInstance().find(taskId);
				String text = stb.getName() + " " + stb.getDescription();
				list = IndexManager.getInstance().searchTasksWithHighLight(text);
			}
			return list;
		} catch (Exception e) {
			throw new GranException(e);
		} finally {
			if (r) lockManager.releaseConnection(className);
		}
	}

	/**
	 * Возвращает ID задачи по ее названию
	 *
	 * @param name Название задачи
	 * @return ID задачи
	 * @throws GranException при необходимости
	 */
	private String findTaskIdByName(String name) throws GranException {
		TaskRelatedInfo tci = TaskRelatedManager.getInstance().findByName(name);
		if (tci!=null) {
			return tci.getId();
		} else {
			return null;
		}
	}

	/**
	 * Возвращает цепочку задач от начальной до конечной
	 *
	 * @param startTaskId ID начальной задачи
	 * @param stopTaskId  ID конечной задачи
	 * @return список задач
	 * @throws GranException при необходимости
	 * @see com.trackstudio.kernel.cache.TaskRelatedInfo
	 */
	public ArrayList<TaskRelatedInfo> getTaskChain(String startTaskId, String stopTaskId) throws GranException {
		boolean r = lockManager.acquireConnection(className);
		try {
			ArrayList<TaskRelatedInfo> list = new ArrayList<TaskRelatedInfo>();
			if (stopTaskId == null)
				return list;
			if (startTaskId != null && startTaskId.equals(stopTaskId)) {
				list.add(TaskRelatedManager.getInstance().find(stopTaskId));
				return list;
			}
			return TaskRelatedManager.getInstance().getTaskRelatedInfoChain(startTaskId, stopTaskId);
		} finally {
			if (r) lockManager.releaseConnection(className);
		}
	}

	/**
	 * Возвращает цепочку ID задач от начальной до конечной
	 *
	 * @param startTaskId ID начальной задачи
	 * @param stopTaskId  ID конечной задачи
	 * @return список ID задач
	 * @throws GranException при необходимости
	 */
	public ArrayList<String> getTaskIdChain(String startTaskId, String stopTaskId) throws GranException {
		boolean r = lockManager.acquireConnection(className);
		try {
			ArrayList<String> returnList = new ArrayList<String>();
			ArrayList<TaskRelatedInfo> list = getTaskChain(startTaskId, stopTaskId);
			for (TaskRelatedInfo tr : list) {
				returnList.add(tr.getId());
			}
			return returnList;
		} finally {
			if (r) lockManager.releaseConnection(className);
		}
	}

	/**
	 * Возвращает ID задачи. Поиск идет вначале по номеру, потом по алиасу, потом по названию, если задача не найдена, то возвращается null
	 *
	 * @param quick_go номер, алиас или название задачи
	 * @return ID задачи
	 * @throws GranException при необходимости
	 */
	public String findTaskIdByQuickGo(String quick_go) throws GranException {
		boolean r = lockManager.acquireConnection(className);
		try {
			String task_id = null;
			if (quick_go != null && quick_go.length() != 0)
				task_id = KernelManager.getTask().findByNumber(quick_go);
			if (task_id == null && quick_go != null && quick_go.length() != 0)
				task_id = KernelManager.getTask().findByShortName(quick_go);
			if (task_id == null && quick_go != null && quick_go.length() != 0)
				task_id = KernelManager.getTask().findTaskIdByName(quick_go);
			return task_id;
		} finally {
			if (r) lockManager.releaseConnection(className);
		}
	}

	/**
	 * ПРоверяет корректность родительской задачи для операции copy/paste
	 *
	 * @param taskId   ID дочерней задачи
	 * @param parentId ID родительской задачи
	 * @return TRUE - если все хорошо, FALSE - если нет
	 * @throws GranException при необходимости
	 */
	public boolean isValidParent(String sessionId, String taskId, String parentId) throws GranException {
		boolean r = lockManager.acquireConnection(className);
		try {
			TaskRelatedInfo curTask = TaskRelatedManager.getInstance().find(taskId);
			boolean allowedParent = taskId.equals("1") || parentId == null;
			if (!allowedParent)
				allowedParent = curTask.getParentId().equals(parentId);
			if (!allowedParent)
				allowedParent = !parentId.equals(taskId) && TaskRelatedManager.getInstance().getTaskRelatedInfoChain(taskId, parentId) == null;
			if (!allowedParent)
				throw new UserException("ERROR_CANT_MOVE_TASK_TO_SUBTASK");
			if (!KernelManager.getCategory().getCreatableCategoryList(parentId == null ? taskId : parentId, sessionId, true, true).contains(KernelManager.getFind().findCategory(curTask.getCategoryId())))
				throw new UserException("ERROR_CATEGORY_NOT_ALLOWED_FOR_PARENT");
			return allowedParent;
		} finally {
			if (r) lockManager.releaseConnection(className);
		}
	}

	/**
	 * Проверяет правильность родительской задачи для дочерней
	 *
	 * @param taskId Дочерняя задача
	 * @param toTask Родительская задача
	 * @return TRUE - если все хорошо, FALSE - если нет
	 * @throws GranException при необходимости
	 */
	public boolean isParentValidForOperation(String taskId, String toTask) throws GranException {
		List<String> chain = getTaskIdChain(null, toTask);
		for (String aTasksId : new String[]{taskId})
			if (chain.contains(aTasksId))
				return false;
		return true;
	}

	/**
	 * Проверяет возможность рекурсивного копирования указанной задачи пользователем
	 *
	 * @param taskId ID копируемой задачи
	 * @param toTask ID задачи куда копируем
	 * @param userId ID пользователя, который копирует
	 * @return TRUE - если можно копировать, FALSE - если нет
	 * @throws GranException при необходимости
	 */
	public boolean canRecursivelyCopyTask(String taskId, String toTask, String userId) throws GranException {
		boolean r = lockManager.acquireConnection(className);
		try {
			List<Category> creatableCategories = KernelManager.getCategory().getCreatableCategoryList(toTask, userId, false, false);
			for (TaskRelatedInfo taskRelatedInfo : TaskRelatedManager.getInstance().getChildrenRecursive(taskId)) {
				String subtaskId = taskRelatedInfo.getId();
				Category cat = KernelManager.getFind().findCategory(TaskRelatedManager.getInstance().find(subtaskId).getCategoryId());
				if (!creatableCategories.contains(cat))
					throw new UserException("Can not create category : " + cat.getName() + " subtaskId : " + subtaskId, false);
				//throw new UserException("ERROR_CATEGORY_NOT_ALLOWED", new Object[]{cat.getName()});
				if (getTaskChain(cat.getTask().getId(), toTask) == null)
					return false;
			}
			return true;
		} finally {
			if (r) lockManager.releaseConnection(className);
		}
	}


	/**
	 * Возвращает список открытых задач
	 *
	 * @param parentId ID задачи, для которой озвращаем список открытых подзадач
	 * @return Список ID задач
	 * @throws GranException при необходимости
	 */
	public List<String> getNotFinishChildren(String parentId) throws GranException {
		boolean r = lockManager.acquireConnection(className);
		try {
			List<String> children = this.relatedManager.getChildrenId(parentId);
			List<String> task = new ArrayList<String>(200);
			if (!children.isEmpty()) {
				Map<String, String> shouldCheckStatus = new LinkedHashMap<String, String>();
				Set<String> status = new TreeSet<String>();
				Map<String, Preferences.TreeCategory> categoryCache = new LinkedHashMap<String, Preferences.TreeCategory>();
				for (String id : this.relatedManager.getChildrenId(parentId)) {
					TaskRelatedInfo info = this.relatedManager.find(id);
					Preferences.TreeCategory treeCategory = categoryCache.get(info.getCategoryId());
					if (treeCategory == null) {
						CategoryCacheItem item = this.cacheManager.find(info.getCategoryId());
						treeCategory = this.determinateTreeCategory(item.getPreferences());
						categoryCache.put(item.getId(), treeCategory);
					}
					if (treeCategory == Preferences.TreeCategory.OPEN) {
						task.add(info.getId());
					} else if (treeCategory == Preferences.TreeCategory.CHECK_STATUS) {
						shouldCheckStatus.put(info.getId(), info.getStatusId());
						status.add(info.getStatusId());
					}
				}
				task.addAll(this.getCheckedStatusTask(status, shouldCheckStatus));
			}
			return task;
		} finally {
			if (r) lockManager.releaseConnection(className);
		}
	}

	private List<String> getCheckedStatusTask(final Set<String> status, final Map<String, String> shouldCheckStatus) throws GranException {
		List<String> task = new ArrayList<String>();
		if (!status.isEmpty()) {
			List notFinishStatus = this.getNotFinishStatusByListId(status);
			for (Map.Entry<String, String> entry : shouldCheckStatus.entrySet()) {
				if (notFinishStatus.contains(entry.getValue())) {
					task.add(entry.getKey());
				}
			}
		}
		return task;
	}

	private Preferences.TreeCategory determinateTreeCategory(String ref) {
		Preferences.TreeCategory treeCategory = Preferences.TreeCategory.HIDE;
		if (ref != null) {
			if (ref.contains("E")) {
				treeCategory = Preferences.TreeCategory.OPEN;
			} else if (ref.contains("F") || (!ref.contains("E") && !ref.contains("F") && !ref.contains("N"))) {
				treeCategory = Preferences.TreeCategory.CHECK_STATUS;
			}
		}
		return treeCategory;
	}

	private List<String> getNotFinishStatusByListId(Set<String> status) throws GranException {
		// it doesn't work with empty list
		if (status.isEmpty())
			return new ArrayList<String>();

		Map<String, Collection> param = new LinkedHashMap<String, Collection>();
		param.put("status", status);
		return this.hu.getListMap("select status.id from com.trackstudio.model.Status status where status.id in (:status) and " +
		                          "(status.isfinish is null or status.isfinish = 0)", Collections.EMPTY_MAP, param);
	}

	public int getTotalNotFinishChildren(String parentId, String userId, String prstatusId) throws GranException {
		List<String> list = getNotFinishChildren(parentId);
		if (list.isEmpty()) {
			return 0;
		} else {
			int total = 0;
			for (String taskId : list) {
				boolean acl = TaskRelatedManager.getInstance().hasAccess(userId, taskId, prstatusId);
				if (acl) {
					++total;
				} else if (TaskRelatedManager.getInstance().onSight(userId, taskId, prstatusId, true)) {
					++total;
				}
			}
			return total;
		}
	}

	/**
	 * Возвращает список ID задач по SQL-запросу
	 *
	 * @param query запрос вида SELECT t.id FROM com.trackstudio.model.Task AS t WHERE ...
	 * @return список ID Задач
	 * @throws GranException при необзодимости
	 */
	public List<String> getTaskListByQuery(String query) throws GranException {
		boolean r = lockManager.acquireConnection(className);
		try {
			return (List<String>) hu.getList(query);
		} finally {
			if (r) lockManager.releaseConnection(className);
		}
	}

	/**
	 * This method gets tasks list where user is used
	 *
	 * @param ownId user id
	 * @return tasks list
	 * @throws GranException for necessery
	 */
	public List<TaskRelatedInfo> getTaskUseUserList(String ownId) throws GranException {
		boolean r = lockManager.acquireConnection(className);
		try {
			List<TaskRelatedInfo> list = hu.getList("select new com.trackstudio.kernel.cache.TaskRelatedInfo(task.id, task.description, task.longtext.id, task.name, task.shortname, task.submitdate, task.updatedate, task.closedate, task.abudget, task.budget, task.deadline, task.number, task.submitter.id, task.handler.id, task.handler.user.id, task.handler.prstatus.id, task.parent.id, task.category.id, task.category.workflow.id, task.status.id, task.resolution.id, task.priority.id)  from com.trackstudio.model.Task task where task.submitter=?", ownId);
			list.addAll(hu.getList("select new com.trackstudio.kernel.cache.TaskRelatedInfo(task.id, task.description, task.longtext.id, task.name, task.shortname, task.submitdate, task.updatedate, task.closedate, task.abudget, task.budget, task.deadline, task.number, task.submitter.id, task.handler.id, task.handler.user.id, task.handler.prstatus.id, task.parent.id, task.category.id, task.category.workflow.id, task.status.id, task.resolution.id, task.priority.id) from com.trackstudio.model.Task task where task.handler=?", ownId));
			return list;
		} finally {
			if (r) lockManager.releaseConnection(className);
		}
	}

	/**
	 * This method gets tasks list with specail category
	 *
	 * @param categoryId category id
	 * @return tasks list
	 * @throws GranException for necessery
	 */
	public List<TaskRelatedInfo> getTaskCategoryList(String categoryId) throws GranException {
		boolean r = lockManager.acquireConnection(className);
		try {
			return hu.getList("select new com.trackstudio.kernel.cache.TaskRelatedInfo(task.id, task.description, task.longtext.id, task.name, task.shortname, task.submitdate, task.updatedate, task.closedate, task.abudget, task.budget, task.deadline, task.number, task.submitter.id, task.handler.id, task.handler.user.id, task.handler.prstatus.id, task.parent.id, task.category.id, task.category.workflow.id, task.status.id, task.resolution.id, task.priority.id) from com.trackstudio.model.Task task where task.category=?", categoryId);
		} finally {
			if (r) lockManager.releaseConnection(className);
		}
	}

	/**
	 * This method get tasks list with tasks where to be specail operations
	 *
	 * @param mstatusId operation id
	 * @return tasks list
	 * @throws GranException for necessery
	 */
	public List<TaskRelatedInfo> getTaskMstatusList(String mstatusId) throws GranException {
		boolean r = lockManager.acquireConnection(className);
		try {
			List<MessageCacheItem> messages = KernelManager.getMessage().getMessageMstatusList(mstatusId);
			Set<String> tasksId = new TreeSet<String>();
			for (MessageCacheItem message : messages) {
				tasksId.add(message.getTaskId());
			}
			List<TaskRelatedInfo> tasks = new ArrayList<TaskRelatedInfo>();
			for (String taskId : tasksId) {
				tasks.add(TaskRelatedManager.getInstance().find(taskId));
			}
			return tasks;
		} finally {
			if (r) lockManager.releaseConnection(className);
		}
	}

	/**
	 * This method gets tasks list with special status id
	 *
	 * @param statusId status id
	 * @return tasks list
	 * @throws GranException for necessery
	 */
	public List<TaskRelatedInfo> getTaskStatusList(String statusId) throws GranException {
		boolean r = lockManager.acquireConnection(className);
		try {
			return hu.getList("select new com.trackstudio.kernel.cache.TaskRelatedInfo(task.id, task.description, task.longtext.id, task.name, task.shortname, task.submitdate, task.updatedate, task.closedate, task.abudget, task.budget, task.deadline, task.number, task.submitter.id, task.handler.id, task.handler.user.id, task.handler.prstatus.id, task.parent.id, task.category.id, task.category.workflow.id, task.status.id, task.resolution.id, task.priority.id) from com.trackstudio.model.Task as task where task.status=?", statusId);
		} finally {
			if (r) lockManager.releaseConnection(className);
		}
	}
}