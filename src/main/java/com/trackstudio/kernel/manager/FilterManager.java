/*
 * @(#)FilterManager.java
 * Copyright 2009 TrackStudio, Inc. All rights reserved.
 */
package com.trackstudio.kernel.manager;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashSet;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.trackstudio.app.filter.FValue;
import com.trackstudio.app.filter.TaskFValue;
import com.trackstudio.app.filter.UserFValue;
import com.trackstudio.common.FieldMap;
import com.trackstudio.constants.FilterConstants;
import com.trackstudio.exception.GranException;
import com.trackstudio.kernel.cache.TaskRelatedInfo;
import com.trackstudio.kernel.cache.TaskRelatedManager;
import com.trackstudio.kernel.cache.UserRelatedManager;
import com.trackstudio.kernel.lock.LockManager;
import com.trackstudio.model.CurrentFilter;
import com.trackstudio.model.Filter;
import com.trackstudio.model.Fvalue;
import com.trackstudio.model.Notification;
import com.trackstudio.model.Report;
import com.trackstudio.model.Subscription;
import com.trackstudio.model.Usersource;
import com.trackstudio.startup.I18n;
import com.trackstudio.tools.Null;

import net.jcip.annotations.Immutable;

/**
 * Класс FilterManager содержит методы для работы с фильтрами.
 */
@Immutable
public class FilterManager extends KernelManager {

	private static final Log log = LogFactory.getLog(FilterManager.class);
	private static final String className = "FilterManager.";
	private static final FilterManager instance = new FilterManager();
	private static final LockManager lockManager = LockManager.getInstance();
	/**
	 * Конструктор по умолчанию
	 */
	private FilterManager() {
	}

	/**
	 * Возвращает экземпляр текущего класса
	 *
	 * @return Экземпляр FilterManager
	 */
	protected static FilterManager getInstance() {
		return instance;
	}

	/**
	 * Редактирование фильтра для задач
	 *
	 * @param filterId    ID фильтра
	 * @param name        Название фильтра
	 * @param description Описание фильтра
	 * @param priv        Видимость фильтра (приватный или публичный)
	 * @param preferences Настройки фильтра
	 * @throws GranException при необходимости
	 * @see com.trackstudio.kernel.manager.SafeString
	 */
	public void updateTaskFilter(String filterId, SafeString name, SafeString description, boolean priv, SafeString preferences) throws GranException {
		boolean w = lockManager.acquireConnection(className);
		lockManager.getLock(filterId).lock();
		try {
			updateFilter(filterId, name, description, priv, preferences);
		} finally {
			if (w) lockManager.releaseConnection(className);
			lockManager.getLock(filterId).unlock();
		}
	}

	/**
	 * Редактирование фильтра для пользователей
	 *
	 * @param filterId    ID фильтра
	 * @param name        Название фильтра
	 * @param description Описание фильтра
	 * @param priv        Видимость фильтра (приватный или публичный)
	 * @param preferences НАстройки фильтра
	 * @throws GranException при необходимости
	 * @see com.trackstudio.kernel.manager.SafeString
	 */
	public void updateUserFilter(String filterId, SafeString name, SafeString description, boolean priv, SafeString preferences) throws GranException {
		boolean w = lockManager.acquireConnection(className);
		lockManager.getLock(filterId).lock();
		try {
			updateFilter(filterId, name, description, priv, preferences);
		} finally {
			if (w) lockManager.releaseConnection(className);
			lockManager.getLock(filterId).unlock();
		}
	}

	/**
	 * Редактирование фильтра
	 *
	 * @param filterId    ID фильтра
	 * @param name        Название фильтра
	 * @param description Описание фильтра
	 * @param priv        Видимость фильтра (приватный или публичный)
	 * @param preferences Настройки фильтра
	 * @throws GranException при необходимости
	 * @see com.trackstudio.kernel.manager.SafeString
	 */
	private void updateFilter(String filterId, SafeString name, SafeString description, boolean priv, SafeString preferences) throws GranException {
		Filter obj = KernelManager.getFind().findFilter(filterId);
		obj.setName(name != null ? name.toString() : null);
		obj.setDescription(description != null ? description.toString() : null);
		obj.setPrivate(priv);
		obj.setPreferences(preferences != null ? preferences.toString() : null);
		hu.updateObject(obj);
	}

	/**
	 * Возвращает список фильтров пользоватлелей для пользователя ownerId, которые доступны пользователю currentUserId
	 *
	 * @param currentUserId ID пользователя, для которого достаются фильтры
	 * @param ownerId       ID пользователя, на уровне которого созданы фильтры (по дереву пользователей)
	 * @return Спсок фильтров
	 * @throws GranException при необходимости
	 * @see com.trackstudio.model.Filter
	 */
	public List<Filter> getUserFilterList(String currentUserId, String ownerId) throws GranException {
		boolean r = lockManager.acquireConnection(className);
		try {
			ArrayList<Filter> result = new ArrayList<Filter>();
			HashSet<String> hs = new HashSet<String>();
			hs.addAll(UserRelatedManager.getInstance().getUserIdChain(null, currentUserId));
			for (String userId : hs)
				result.addAll(hu.getList("from com.trackstudio.model.Filter f where f.task is null and f.user=? and (f.priv=0 or f.owner=?)", userId, ownerId));
			return result;
		} finally {
			if (r) lockManager.releaseConnection(className);
		}
	}

	/**
	 * Возвращает список фильтров пользоватлелей для пользователя ownerId, которые доступны пользователю currentUserId,
	 * включая фильтры, созданные для дочерних пользовталей
	 *
	 * @param currentUserId ID пользователя, для которого достаются фильтры
	 * @param ownerId       ID пользователя, на уровне которого созданы фильтры (по дереву пользователей)
	 * @return Спсок фильтров
	 * @throws GranException при необходимости
	 * @see com.trackstudio.model.Filter
	 */
	public List<Filter> getAllUserFilterList(String currentUserId, String ownerId) throws GranException {
		boolean r = lockManager.acquireConnection(className);
		try {
			ArrayList<Filter> result = new ArrayList<Filter>();
			HashSet<String> hs = new HashSet<String>();
			hs.addAll(KernelManager.getUser().getUserAndChildrenListIdOnly(currentUserId));
			hs.addAll(UserRelatedManager.getInstance().getUserIdChain(null, currentUserId));
			for (String userId : hs)
				result.addAll(hu.getList("from com.trackstudio.model.Filter f where f.task is null and  f.user=? and (f.priv=0  or f.owner=?)", userId, ownerId));
			return result;
		} finally {
			if (r) lockManager.releaseConnection(className);
		}
	}

	/**
	 * Возвращает список фильтров задач для задачи taskId, которые доступны пользователю userId
	 *
	 * @param taskId ID задачи, на уровне которой созданы фильтры (по дереву задач)
	 * @param userId ID пользователя, для которого достаются фильтры
	 * @return Спсок фильтров
	 * @throws GranException при необходимости
	 * @see com.trackstudio.model.Filter
	 */
	public List<Filter> getTaskFilterList(String taskId, String userId) throws GranException {
		boolean r = lockManager.acquireConnection(className);
		try {
			ArrayList<Filter> result = new ArrayList<Filter>();
			for (TaskRelatedInfo it : TaskRelatedManager.getInstance().getTaskRelatedInfoChain(null, taskId)) {
				String task = it.getId();
				result.addAll(hu.getList("from com.trackstudio.model.Filter f where " +
				                         " (f.priv=0 or f.owner=?) and f.task=?", userId, task));
			}
			return result;
		} finally {
			if (r) lockManager.releaseConnection(className);
		}
	}

	/**
	 * Возвращает список фильтров задач для задачи taskId, которые доступны пользователю userId,
	 * включая фильтры, созданные для подзадач
	 *
	 * @param taskId ID задачи, на уровне которой созданы фильтры (по дереву задач)
	 * @param userId ID пользователя, для которого достаются фильтры
	 * @return Спсок фильтров
	 * @throws GranException при необходимости
	 * @see com.trackstudio.model.Filter
	 */
	public List<Filter> getAllTaskFilterList(String taskId, String userId) throws GranException {
		boolean r = lockManager.acquireConnection(className);
		try {
			ArrayList<Filter> result = new ArrayList<Filter>();
			List<Filter> allFilters = hu.getList("from com.trackstudio.model.Filter f where f.task is not null and " +
			                                     " (f.priv=0 or f.owner=?)", userId);
			for (Filter fil : allFilters) {
				if (TaskRelatedManager.getInstance().hasPath(taskId, fil.getTask().getId()))
					result.add(fil);
			}
			return result;
		} finally {
			if (r) lockManager.releaseConnection(className);
		}
	}

	/**
	 * Возвращает список всех фильтров задач для задачи taskId
	 *
	 * @param taskId ID задачи, для которой достаются фильтры
	 * @return Спсок фильтров
	 * @throws GranException при необходимости
	 * @see com.trackstudio.model.Filter
	 */
	public List<Filter> getAllTaskFilterList(String taskId) throws GranException {
		boolean r = lockManager.acquireConnection(className);
		try {
			ArrayList<Filter> result = new ArrayList<Filter>();
			for (TaskRelatedInfo it : TaskRelatedManager.getInstance().getTaskRelatedInfoChain(null, taskId)) {
				String task = it.getId();
				result.addAll(hu.getList("from com.trackstudio.model.Filter f where f.task=?", task));
			}
			return result;
		} finally {
			if (r) lockManager.releaseConnection(className);
		}
	}

	/**
	 * Возвращает список нотификаций для заданного пользователя
	 *
	 * @param userId ID пользователя, для которого достаются нотификации
	 * @return Спсок нотификаций
	 * @throws GranException при необходимости
	 * @see com.trackstudio.model.Notification
	 */
	public List<Notification> getUserNotificationList(String userId) throws GranException {
		boolean r = lockManager.acquireConnection(className);
		try {
			return hu.getList("from com.trackstudio.model.Notification as n where n.user.user is not null and n.user.user=?", userId);
		} finally {
			if (r) lockManager.releaseConnection(className);
		}
	}

	/**
	 * Возвращает список нотификаций для заданного статуса
	 *
	 * @param prstatusId ID статуса, для которого достаются нотификации
	 * @return Спсок нотификаций
	 * @throws GranException при необходимости
	 * @see com.trackstudio.model.Notification
	 */
	public List<Notification> getPrstatusNotificationList(String prstatusId) throws GranException {
		boolean r = lockManager.acquireConnection(className);
		try {
			return hu.getList("from com.trackstudio.model.Notification as n where n.user.prstatus is not null and n.user.prstatus=?", prstatusId);
		} finally {
			if (r) lockManager.releaseConnection(className);
		}
	}

	/**
	 * Возвращает список нотификаций для заданного фильтра
	 *
	 * @param filterId ID фильтра, для которого достаются нотификации
	 * @return Спсок нотификаций
	 * @throws GranException при необходимости
	 * @see com.trackstudio.model.Notification
	 */
	protected List<Notification> getFilterNotificationList(String filterId) throws GranException {
		return hu.getList("from com.trackstudio.model.Notification as n where n.filter=?", filterId);
	}

	/**
	 * Возвращает список подписок для заданного пользователя
	 *
	 * @param userId ID пользователя, для которого достаются подписки
	 * @return Спсок нотификаций
	 * @throws GranException при необходимости
	 * @see com.trackstudio.model.Subscription
	 */
	public List<Subscription> getUserSubscriptionList(String userId) throws GranException {
		boolean r = lockManager.acquireConnection(className);
		try {
			return hu.getList("from com.trackstudio.model.Subscription as s where s.user.user is not null and s.user.user=?", userId);
		} finally {
			if (r) lockManager.releaseConnection(className);
		}
	}

	/**
	 * Возвращает список подписок для заданного статуса
	 *
	 * @param prstatusId ID статуса, для которого достаются подписки
	 * @return Спсок нотификаций
	 * @throws GranException при необходимости
	 * @see com.trackstudio.model.Subscription
	 */
	public List<Subscription> getPrstatusSubscriptionList(String prstatusId) throws GranException {
		boolean r = lockManager.acquireConnection(className);
		try {
			return hu.getList("from com.trackstudio.model.Subscription as s where s.user.prstatus is not null and s.user.prstatus=?", prstatusId);
		} finally {
			if (r) lockManager.releaseConnection(className);
		}
	}

	/**
	 * Возвращает список подписок для заданного фильтра
	 *
	 * @param filterId ID фильтра, для которого достаются подписки
	 * @return Спсок нотификаций
	 * @throws GranException при необходимости
	 * @see com.trackstudio.model.Subscription
	 */
	protected List<Subscription> getFilterSubscriptionList(String filterId) throws GranException {
		return hu.getList("from com.trackstudio.model.Subscription as s where s.filter=?", filterId);
	}

	/**
	 * Создает копию указанного фильтра для пользоватлея (клонирует)
	 *
	 * @param filterId ID фильтра, который клонируем
	 * @param locale   Локаль пользователя (нужна для переименовая фильтра в дальнейшем)
	 * @param userId   ID пользователя, который клонирует фильтр
	 * @return ID нового фильтра
	 * @throws GranException при необходимости
	 */
	public String cloneTaskFilter(String filterId, String currentTaskId, String locale, String userId) throws GranException {
		boolean w = lockManager.acquireConnection(className);
		try {
			return cloneFilter(filterId, currentTaskId, null, locale, userId);
		} finally {
			if (w) lockManager.releaseConnection(className);
		}
	}

	/**
	 * Возвращает параметры для фильтра пользователей
	 *
	 * @param filterId ID фильтра, для которого получаем параметры
	 * @return параметры для фильтра в виде объекта UserFValue
	 * @throws GranException при необходимости
	 * @see com.trackstudio.app.filter.UserFValue
	 */
	public String cloneUserFilter(String filterId, String locale, String userId) throws GranException {
		boolean w = lockManager.acquireConnection(className);
		try {
			return cloneFilter(filterId, null, userId, locale, userId);
		} finally {
			if (w) lockManager.releaseConnection(className);
		}
	}

	/**
	 * Возвращает параметры для фильтра пользователей
	 *
	 * @param filterId ID фильтра, для которого получаем параметры
	 * @return параметры для фильтра в виде объекта UserFValue
	 * @throws GranException при необходимости
	 * @see com.trackstudio.app.filter.UserFValue
	 */
	private String cloneFilter(String filterId, String currentTaskId, String currentUserId, String locale, String userId) throws GranException {
		Filter filter = (Filter) hu.getObject(Filter.class, filterId);
		String name = filter.getName();
		Filter filterCopy = new Filter(name.endsWith(' ' + I18n.getString(locale, "CLONED")) ? name : name + ' ' + I18n.getString(locale, "CLONED"),
				filter.getDescription(), filter.isPrivate(), currentTaskId, currentUserId, userId, filter.getPreferences());

		String filterid = hu.createObject(filterCopy);
		FValue fValue = new TaskFValue();
		for (Fvalue fvalue : (List<Fvalue>) hu.getList("from com.trackstudio.model.Fvalue as fvalue where fvalue.filter=?", filterId)) {
			fValue.putItem(fvalue.getKey(), fvalue.getValue());
		}
		KernelManager.getFilter().setFValue(filterid, fValue);
		return filterid;
	}

	/**
	 * Возвращает параметры для фильтра пользователей
	 *
	 * @param filterId ID фильтра, для которого получаем параметры
	 * @return параметры для фильтра в виде объекта UserFValue
	 * @throws GranException при необходимости
	 * @see com.trackstudio.app.filter.UserFValue
	 */
	public UserFValue getUserFValue(String filterId) throws GranException {
		boolean r = lockManager.acquireConnection(className);
		try {
			UserFValue map = new UserFValue();
//            HibernateSession.closeSession();
			makeFValue(map, filterId);
			return map;
		} finally {
			if (r) lockManager.releaseConnection(className);
		}
	}

	/**
	 * Заполняет данным объект FValue для указанного фильтра
	 *
	 * @param map      То, что заполняем данными
	 * @param filterId ID фильтра, для которого берутся данные
	 * @throws GranException при необходимости
	 * @see com.trackstudio.app.filter.FValue
	 */
	private void makeFValue(FValue map, String filterId) throws GranException {
		List<Object[]> list = (List<Object[]>) hu.getList("select fvalue.key, fvalue.value from com.trackstudio.model.Fvalue as fvalue where fvalue.key is not null and fvalue.filter=?", filterId);
		for (Object[] fv : list) {
			String key = (String) fv[0];
			String val = (String) fv[1];
			map.putItem(key, val);
		}
		updateFValue(map, filterId);
	}

	/**
	 * Временный метод для чистки фильтров, из за баги #72050
	 *
	 * @param map      То, что заполняем данными
	 * @param filterId ID фильтра, для которого берутся данные
	 * @throws GranException при необходимости
	 * @see com.trackstudio.app.filter.FValue
	 */
	private void updateFValue(FValue map, String filterId) throws GranException {
		boolean updateDB = false;
		for (String u : map.getUse()) {
			if (u.equals(FieldMap.TASK_NUMBER.getFilterKey()) ||
			    u.equals(FieldMap.TASK_BUDGET.getFilterKey()) ||
			    u.equals(FieldMap.TASK_PARENT.getFilterKey()) ||
			    u.equals(FieldMap.TASK_ABUDGET.getFilterKey()) ||
			    u.equals(FieldMap.TASK_MESSAGECOUNT.getFilterKey()) ||
			    u.equals(FieldMap.TASK_CHILDCOUNT.getFilterKey()) ||
			    u.equals(FieldMap.TASK_NAME.getFilterKey()) ||
			    u.equals(FieldMap.TASK_SHORTNAME.getFilterKey()) ||
			    u.equals(FieldMap.FULLPATH.getFilterKey())) {
				String value = map.getOriginalAsString(u);
				map.remove(u);
				if (value != null) {
					updateDB = true;
					String result = value;
					if (value.lastIndexOf('_') != -1) {
						result = value.substring(value.lastIndexOf('_') + "_".length(), value.length());
					}
					if (result.length() != 0) {
						updateDB = true;
						map.putItem(u, value);
					}
					if (value.equals(map.getOriginalAsString(u))) {
						updateDB = false;
					}
				}
				continue;
			}
			if (u.equals(FieldMap.TASK_UPDATEDATE.getFilterKey()) ||
			    u.equals(FieldMap.TASK_SUBMITDATE.getFilterKey()) ||
			    u.equals(FieldMap.TASK_CLOSEDATE.getFilterKey()) ||
			    u.equals(FieldMap.TASK_DEADLINE.getFilterKey())) {
				updateDB = true;
				String periodValue = map.getAsString(FValue.PERIOD + u);
				String amountValue = map.getAsString(FValue.AMNT + u);
				String intervalValue = map.getAsString(FValue.INTERVAL + u);
				String beforeAfterValue = map.getAsString(FValue.BA + u);
				String earlyLaterValue = map.getAsString(FValue.EL + u);
				String propertyFromValue = map.getAsString(FValue.SUB + u);
				String propertyToValue = map.getAsString(u);
				map.remove(FValue.PERIOD + u);
				map.remove(FValue.AMNT + u);
				map.remove(FValue.INTERVAL + u);
				map.remove(FValue.BA + u);
				map.remove(FValue.EL + u);
				map.remove(u);
				map.remove(FValue.SUB + u);
				if (periodValue != null) {
				    if (!"0".equals(periodValue)) {
				        map.putItem(FValue.PERIOD + u, periodValue);
				        updateDB = false;
                    }
                } else {
                    if (amountValue != null) {
                        if (intervalValue != null && beforeAfterValue != null && earlyLaterValue != null) {
                            map.putItem(FValue.AMNT + u, amountValue);
                            map.putItem(FValue.INTERVAL + u, intervalValue);
                            map.putItem(FValue.BA + u, beforeAfterValue);
                            map.putItem(FValue.EL + u, earlyLaterValue);
                            updateDB = false;
                        }
                    } else {
                        boolean fromTime = Null.isNotNull(propertyFromValue);
                        boolean toTime = Null.isNotNull(propertyToValue);
                        if (fromTime || toTime) {
                            map.putItem(u, propertyToValue);
                            map.putItem(FValue.SUB + u, propertyFromValue);
                            updateDB = false;
                        }
                    }
                }
				continue;
			}
			if (u.equals(FieldMap.HUSER_NAME.getFilterKey()) ||
			    u.equals(FieldMap.TASK_PRIORITY.getFilterKey()) ||
			    u.equals(FieldMap.TASK_CATEGORY.getFilterKey()) ||
			    u.equals(FieldMap.TASK_STATUS.getFilterKey()) ||
			    u.equals(FieldMap.TASK_RESOLUTION.getFilterKey()) ||
			    u.equals(FieldMap.SUSER_NAME.getFilterKey()) ||
			    u.equals(FieldMap.SUSER_STATUS.getFilterKey()) ||
			    u.equals(FieldMap.MESSAGEVIEW.getFieldKey())) {
				updateDB = true;
				List<String> propertyValue = map.getOriginValues(u);
				map.remove(u);
				if (propertyValue != null && propertyValue.size() != 0 && !"0".equals(propertyValue.get(0))) {
					map.put(u, propertyValue);
					updateDB = false;
				}
			}
		}
		if (updateDB) {
			KernelManager.getFilter().setFValue(filterId, map);
		}
	}

	/**
	 * Возвращает параметры для фильтра задач
	 *
	 * @param filterId ID фильтра, для которого получаем параметры
	 * @return параметры для фильтра в виде объекта UserFValue
	 * @throws GranException при необходимости
	 * @see com.trackstudio.app.filter.UserFValue
	 */
	public TaskFValue getTaskFValue(String filterId) throws GranException {
		boolean r = lockManager.acquireConnection(className);
		try {
			TaskFValue map = new TaskFValue();
//            HibernateSession.stopTimer();
			makeFValue(map, filterId);
			// log.debug("TaskFValue map: " + map);
			return map;
		} finally {
			if (r) lockManager.releaseConnection(className);
		}
	}

	/**
	 * Возвращает список подписок для задачи и фильтра
	 *
	 * @param filterId ID фильтра, для которого получаем подписки
	 * @param taskId   ID задачи, для которой получаем подписки
	 * @return список подписок
	 * @throws GranException при необходимости
	 * @see com.trackstudio.model.Subscription
	 */
	public List<Subscription> getSubscriptionList(String filterId, String taskId) throws GranException {
		boolean r = lockManager.acquireConnection(className);
		try {
			List<Subscription> result = new ArrayList<Subscription>();
			for (TaskRelatedInfo it : TaskRelatedManager.getInstance().getTaskRelatedInfoChain(KernelManager.getFind().findFilter(filterId).getTask().getId(), taskId))
				result.addAll(hu.getList("from com.trackstudio.model.Subscription s where s.filter=? and s.task=?", filterId, it.getId()));
			return result;
		} finally {
			if (r) lockManager.releaseConnection(className);
		}
	}

	/**
	 * Возвращает список подписок для фильтра
	 *
	 * @param filterId ID фильтра, для которого получаем подписки
	 * @return список подписок
	 * @throws GranException при необходимости
	 * @see com.trackstudio.model.Subscription
	 */
	public List<Subscription> getAllSubscriptionList(String filterId) throws GranException {
		boolean r = lockManager.acquireConnection(className);
		try {
			return hu.getList("from com.trackstudio.model.Subscription s where s.filter=?", filterId);
		} finally {
			if (r) lockManager.releaseConnection(className);
		}
	}

	/**
	 * This method returns all subscriptions in the system
	 * @return all subscriptions
	 * @throws GranException for necessary
	 */
	public List<Subscription> getAllSubscriptionList() throws GranException {
		boolean r = lockManager.acquireConnection(className);
		try {
			return hu.getList("select s from com.trackstudio.model.Subscription s where s.task is not null");
		} finally {
			if (r) lockManager.releaseConnection(className);
		}
	}

	/**
	 * Удаляет подписку
	 *
	 * @param subscribeId ID подписки, которую удаляем
	 * @throws GranException при необходимости
	 */
	public void unSubscribe(String subscribeId) throws GranException {
		boolean w = lockManager.acquireConnection(className);
		try {
			hu.deleteObject(Subscription.class, subscribeId);
		} finally {
			if (w) lockManager.releaseConnection(className);
		}
	}

	/**
	 * Создает новую подписку
	 *
	 * @param name       Название подписки
	 * @param userId     ID подписанного пользователя
	 * @param groupId    ID подписанной группы
	 * @param taskId     ID задаче, на уровне которой создается подписка
	 * @param filterId   ID фильтра, для которого получаем подписки
	 * @param startDate  Дата/время начала рассылки
	 * @param stopDate   Дата/время окончания рассылки
	 * @param nextRun    Время следующего запуска
	 * @param interval   Истервал рассылки
	 * @param templateId ID шаблона письма
	 * @return ID созданной подписки
	 * @throws GranException при необходимости
	 * @see com.trackstudio.kernel.manager.SafeString
	 */
	public String createSubscription(SafeString name, String userId, String groupId, String taskId, String filterId,
	                                 Calendar startDate, Calendar stopDate, Calendar nextRun,
	                                 Integer interval, String templateId) throws GranException {
		boolean w = lockManager.acquireConnection(className);
		try {
			String usersourceId = KernelManager.getUser().getUsersource(userId, groupId);
			return hu.createObject(new Subscription(name != null ? name.toString() : null, usersourceId, filterId, taskId, startDate, stopDate, nextRun,
					interval, templateId));
		} finally {
			if (w) lockManager.releaseConnection(className);
		}
	}

	/**
	 * Редактирует существующую подписку
	 *
	 * @param name           Название подписки
	 * @param subscriptionId ID редактируемой подписки
	 * @param filterId       ID фильтра, для которого получаем подписки
	 * @param startDate      Дата/время начала рассылки
	 * @param stopDate       Дата/время окончания рассылки
	 * @param nextRun        Время следующего запуска
	 * @param interval       Истервал рассылки
	 * @param templateId     ID шаблона письма
	 * @throws GranException при необходимости
	 * @see com.trackstudio.kernel.manager.SafeString
	 */
	public void updateSubscription(SafeString name, String subscriptionId, String filterId, String templateId, Calendar startDate, Calendar stopDate,
	                               Calendar nextRun, Integer interval, String userId, String groupId) throws GranException {
		boolean w = lockManager.acquireConnection(className);
		try {
			Subscription obj = KernelManager.getFind().findSubscription(subscriptionId);
			obj.setName(name != null ? name.toString() : null);
			obj.setInterval(interval);
			obj.setNextrun(nextRun);
			obj.setStartdate(startDate);
			obj.setStopdate(stopDate);
			obj.setTemplate(templateId);
			obj.setFilter(filterId);
			String usersourceId = KernelManager.getUser().getUsersource(userId, groupId);
			obj.setUser(new Usersource(usersourceId));
			hu.updateObject(obj);
		} finally {
			if (w) lockManager.releaseConnection(className);
		}
	}

	/**
	 * Возвращает текущий выбранный пользователем фильтр на уровне задачи taskId
	 *
	 * @param taskId ID Задачи, на уровне которой берется фильтр
	 * @param userId ID Пользователя, для которого берется фильтр
	 * @return ID текущего фильтра пользователя
	 * @throws GranException при необходимости
	 */
	public String getCurrentTaskFilterId(String taskId, String userId) throws GranException {
		boolean r = lockManager.acquireConnection(className);
		try {
			String filterId;
			List<String> list = hu.getList("select cf.filter.id from " +
			                               "com.trackstudio.model.CurrentFilter cf, " +
			                               "com.trackstudio.model.Filter fl " +
			                               "where cf.task=? and cf.owner=? and cf.filter=fl.id",
					taskId, userId);
			if (!list.isEmpty()) {
				filterId = list.get(0);
			} else {
				filterId = FilterConstants.DEFAULT_TASK_FILTER_ID;
			}
			return filterId;
		} finally {
			if (r) lockManager.releaseConnection(className);
		}
	}

	/**
	 * Устанавливает текущий выбранный фильтр для пользователя или задачи
	 *
	 * @param taskId   ID задачи, для которой устанавливается текущий выбранный фильтр
	 * @param userId   ID пользователя, для которого устанавливается фильтр
	 * @param filterId ID фильтра, который устанавливается
	 * @return ID установленного фильтра
	 * @throws GranException при необходимости
	 */
	public String setCurrentFilter(String taskId, String userId, String filterId) throws GranException {
		// to optimize: delete from gr_currentfilter where currentfilter_fil='1' or currentfilter_fil='0'
		hu.executeDML("delete from com.trackstudio.model.CurrentFilter cf where cf.task=? and cf.owner=?", taskId, userId);
		return hu.createObject(new CurrentFilter(taskId, null, userId, filterId));
	}

	/**
	 * Создает уведомление о событиях
	 *
	 * @param name       Название уведомления
	 * @param filterId   ID фильтра, с использованием которого фильтруются задачи для рассылки уведомлений
	 * @param userId     ID пользователя, для которого создается уведомление
	 * @param groupId    ID группы, для которой создается уведомление
	 * @param taskId     ID хадачи, на уровне которой создается уведомление
	 * @param templateId ID шабло письма
	 * @return ID созданного уведомления
	 * @throws GranException при необходимости
	 */
	public String setNotification(SafeString name, String filterId, String userId, String groupId, String taskId, String templateId) throws GranException {
		boolean w = lockManager.acquireConnection(className);
		try {
			String usersourceId = KernelManager.getUser().getUsersource(userId, groupId);
			return hu.createObject(new Notification(name != null ? name.toString() : null, usersourceId, filterId, taskId, templateId, "NUAM"));
		} finally {
			if (w) lockManager.releaseConnection(className);
		}
	}

	/**
	 * Произодится редактирование уведомления о событиях
	 *
	 * @param notificationId ID редактируемого уведомления
	 * @param name           Название уведомления
	 * @param template       ID шаблона письма
	 * @param filterId       ID фильтра, на основании которого фильтруются задачи для рассылки уведомлений
	 * @param condition      Перечень события, на которые идет реакция
	 * @throws GranException при необходимости
	 */
	public void updateNotification(String notificationId, SafeString name, SafeString template, String filterId, String condition, String userId, String groupId) throws GranException {
		boolean w = lockManager.acquireConnection(className);
		try {
			Notification obj = KernelManager.getFind().findNotification(notificationId);
			Filter f = KernelManager.getFind().findFilter(filterId);
			obj.setName(name != null ? name.toString() : null);
			obj.setTemplate(template != null ? template.toString() : null);
			obj.setCondition(condition);
			obj.setFilter(f);
			String usersourceId = KernelManager.getUser().getUsersource(userId, groupId);
			obj.setUser(new Usersource(usersourceId));
			hu.updateObject(obj);
		} finally {
			if (w) lockManager.releaseConnection(className);
		}
	}

	/**
	 * Удаляет уведомление о событиях
	 *
	 * @param notificationId ID удаляемого уведомления
	 * @throws GranException при необходимости
	 */
	public void deleteNotification(String notificationId) throws GranException {
		boolean w = lockManager.acquireConnection(className);
		try {
			hu.deleteObject(Notification.class, notificationId);
		} finally {
			if (w) lockManager.releaseConnection(className);
		}
	}

	/**
	 * Возвращает список оповещений для определенного фильтра, которые могу рассылаться для указанной задачи.
	 *
	 * @param filterId ID фильтра
	 * @param taskId   текущая задача
	 * @return список уведомлений
	 * @throws GranException при необходимости
	 * @see com.trackstudio.model.Notification
	 */
	public List<Notification> getNotificationList(String filterId, String taskId) throws GranException {
		boolean r = lockManager.acquireConnection(className);
		try {
			List<Notification> result = new ArrayList<Notification>();
			for (TaskRelatedInfo it : TaskRelatedManager.getInstance().getTaskRelatedInfoChain(KernelManager.getFind().findFilter(filterId).getTask().getId(), taskId))
				result.addAll(hu.getList("select n from com.trackstudio.model.Notification n where n.filter=? and n.task=?", filterId, it.getId()));
			return result;
		} finally {
			if (r) lockManager.releaseConnection(className);
		}
	}

	/**
	 * Возвращает список оповещений, созданных для данного фильтра
	 *
	 * @param filterId ID фильтра, для которого беруться уведомления
	 * @return список уведомлений
	 * @throws GranException при необходимости
	 * @see com.trackstudio.model.Notification
	 */
	public List<Notification> getAllNotificationList(String filterId) throws GranException {
		boolean r = lockManager.acquireConnection(className);
		try {
			return hu.getList("from com.trackstudio.model.Notification n where n.filter=?", filterId);
		} finally {
			if (r) lockManager.releaseConnection(className);
		}
	}

	/**
	 * This method returns all notifications in the system
	 * @return all notifications
	 * @throws GranException for necessary
	 */
	public List<Notification> getAllNotificationList() throws GranException {
		boolean r = lockManager.acquireConnection(className);
		try {
			return hu.getList("select n from com.trackstudio.model.Notification n where n.task is not null");
		} finally {
			if (r) lockManager.releaseConnection(className);
		}
	}

	/**
	 * Возвращает текущий выбранный пользователем фильтр на уровне пользователя userId
	 *
	 * @param userId  ID Пользователя, на уровле которого берется фильтр
	 * @param ownerId ID Пользователя, для которого берется фильтр
	 * @return ID текущего фильтра пользователя
	 * @throws GranException при необходимости
	 */
	public String getCurrentUserFilterId(String userId, String ownerId) throws GranException {
		boolean r = lockManager.acquireConnection(className);
		try {
			String filterId;
			List list = hu.getList("select cf.filter.id from com.trackstudio.model.CurrentFilter cf, " +
			                       "com.trackstudio.model.Filter fl where cf.user=? and cf.owner=? and " +
			                       "cf.filter=fl.id", userId, ownerId);
			if (!list.isEmpty()) {
				filterId = (String) list.get(0);
			} else
				filterId = FilterConstants.DEFAULT_USER_FILTER_ID;
			return filterId;
		} finally {
			if (r) lockManager.releaseConnection(className);
		}
	}

	/**
	 * Устанавливает текущий выбранный фильтр для пользователя или задачи
	 *
	 * @param userId   ID пользователя, для которого устанавливается текущий выбранный фильтр
	 * @param ownerId  ID пользователя, который устанавливает фильтр
	 * @param filterId ID фильтра, который устанавливается
	 * @return ID установленного фильтра
	 * @throws GranException при необходимости
	 */
	public String setCurrentUserFilter(String userId, String filterId, String ownerId) throws GranException {
		hu.executeDML("delete from com.trackstudio.model.CurrentFilter cf where cf.user=? and cf.owner=?", userId, ownerId);
		return hu.createObject(new CurrentFilter(null, userId, ownerId, filterId));
	}

	/**
	 * Создает фильтр для задач
	 *
	 * @param name        название фильтра
	 * @param description описание фильтра
	 * @param priv        указывает приватный фильтр или нет
	 * @param taskId      ID задачи на уровне которой создается фильтр
	 * @param ownerId     ID пользователя, который создает фильтр
	 * @return ID созданного фильтра
	 * @throws GranException при необходимости
	 */
	public String createTaskFilter(SafeString name, SafeString description, boolean priv, String taskId, String ownerId, String preferences) throws GranException {
		boolean w = lockManager.acquireConnection(className);
		try {
			String filterId = createFilter(name, description, priv, taskId, null, ownerId, preferences);
			FValue fv = new TaskFValue();
			ArrayList<String> display = new ArrayList<String>();
			display.add(FieldMap.TASK_NAME.getFilterKey());
			fv.setList(FValue.DISPLAY, display);
			fv.putItem(FieldMap.TASK_NAME.getFilterKey(), FValue.EQ);
			KernelManager.getFilter().setFValue(filterId, fv);
			return filterId;
		} finally {
			if (w) lockManager.releaseConnection(className);
		}
	}

	/**
	 * Создает фильтр для полдьзователей
	 *
	 * @param name        Название задачи
	 * @param description Описание задачи
	 * @param priv        указывает приватный фильтр или нет
	 * @param userId      ID пользователя на уровне которого создается фильтр
	 * @param ownerId     ID пользователя, который создает фильтр
	 * @param preferences показывать на панели или нет
	 * @return ID созданного фильтра
	 * @throws GranException при необходимости
	 */
	public String createUserFilter(SafeString name, SafeString description, boolean priv, String userId, String ownerId, String preferences) throws GranException {
		boolean w = lockManager.acquireConnection(className);
		try {
			String filterId = createFilter(name, description, priv, null, userId, ownerId, preferences);
			FValue fv = new UserFValue();
			ArrayList<String> display = new ArrayList<String>();
			display.add(FieldMap.TASK_NAME.getFilterKey());
			fv.setList(FValue.DISPLAY, display);
			fv.putItem(FieldMap.TASK_NAME.getFilterKey(), FValue.EQ);
			KernelManager.getFilter().setFValue(filterId, fv);
			return filterId;
		} finally {
			if (w) lockManager.releaseConnection(className);
		}
	}

	/**
	 * Создает фильтр
	 *
	 * @param name        Название фильтра
	 * @param description Описание фильтра
	 * @param priv        Указывает приватный фильтр или нет
	 * @param task        ID задачи, на уровне которой создается фильтр
	 * @param user        ID пользователя на уровне которого создается фильтр
	 * @param owner       ID пользователя который создает фильтр
	 * @return ID созданного фильтра
	 * @throws GranException при необходимости
	 */
	private String createFilter(SafeString name, SafeString description, boolean priv, String task, String user, String owner, String preferences) throws GranException {
		boolean w = lockManager.acquireConnection(className);
		try {
			return hu.createObject(new Filter(name != null ? name.toString() : null, description != null ? description.toString() : null, priv, task, user, owner, preferences));
		} finally {
			if (w) lockManager.releaseConnection(className);
		}
	}

	/**
	 * Удаляет фильтр для задач
	 *
	 * @param filterId ID фильтра, Который удаляем
	 * @throws GranException при необходимости
	 */
	public void deleteTaskFilter(String filterId) throws GranException {
		boolean w = lockManager.acquireConnection(className);
		try {
			deleteFilter(filterId);
		} finally {
			if (w) lockManager.releaseConnection(className);
		}
	}

	/**
	 * Удаляет фильтр для пользователей
	 *
	 * @param filterId ID фильтра, который удаляем
	 * @throws GranException при необходимости
	 */
	public void deleteUserFilter(String filterId) throws GranException {
		boolean w = lockManager.acquireConnection(className);
		try {
			deleteFilter(filterId);
		} finally {
			if (w) lockManager.releaseConnection(className);
		}
	}

	/**
	 * Удаляет фильтр
	 *
	 * @param filterId ID фильтра, который удаляем
	 * @throws GranException при необходимости
	 */
	private void deleteFilter(String filterId) throws GranException {
		if (filterId.equals("1"))
			return;
		for (CurrentFilter cf : (List<CurrentFilter>) hu.getList("from com.trackstudio.model.CurrentFilter cfilter where cfilter.filter=?", filterId)) {
			cf.setFilter("1");
			hu.updateObject(cf);
		}
		for (Report r : (List<Report>) hu.getList("from com.trackstudio.model.Report report where report.filter=?", filterId)) {
			r.setFilter("1");
			hu.updateObject(r);
		}
		hu.deleteObject(Filter.class, filterId);
	}

	/**
	 * Сохраняются параметры фильтра
	 *
	 * @param filterId Id фильтра, параметры которого сохраняются
	 * @param map      Сохраняемые параметры
	 * @throws GranException при необходимости
	 */
	public void setFValue(String filterId, FValue map) throws GranException {
		boolean w = lockManager.acquireConnection(className);
		lockManager.getLock(filterId).lock();
		try {
			hu.executeDML("delete from com.trackstudio.model.Fvalue as fvalue where fvalue.filter=?", filterId, null);
			for (String key : map.keySet()) {
				List<String> list = map.getOriginValues(key);
				if (list != null) {
					for (String s : list) {
						hu.createObject(new Fvalue(filterId, key, s));
					}
				}
			}
		} finally {
			if (w) lockManager.releaseConnection(className);
			lockManager.getLock(filterId).unlock();

		}
	}
}