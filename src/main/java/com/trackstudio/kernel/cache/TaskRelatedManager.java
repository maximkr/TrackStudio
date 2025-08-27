package com.trackstudio.kernel.cache;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.atomic.AtomicReference;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.trackstudio.app.UdfValue;
import com.trackstudio.exception.GranException;
import com.trackstudio.exception.TaskNotFoundException;
import com.trackstudio.kernel.manager.CategoryManager;
import com.trackstudio.kernel.manager.KernelManager;
import com.trackstudio.model.Task;
import com.trackstudio.model.Udflist;
import com.trackstudio.tools.HibernateUtil;
import com.trackstudio.tools.Intern;
import com.trackstudio.tools.tree.OrderedTree;

import net.jcip.annotations.GuardedBy;
import net.jcip.annotations.ThreadSafe;

/**
 * Класс для работы с кешем задач и сообщений
 */
@ThreadSafe
public class TaskRelatedManager {

	private static final String groupPrefix = "GROUP_";

	private static final HibernateUtil hu = new HibernateUtil();

	private static final Log log = LogFactory.getLog(TaskRelatedManager.class);

	private static final AtomicReference<TaskRelatedManager> instance = new AtomicReference<TaskRelatedManager>(null);

	private final static List<Integer> ONLY_ONE_VALUE = Collections.unmodifiableList(Arrays.asList(
		UdfValue.STRING,
		UdfValue.DATE,
		UdfValue.INTEGER,
		UdfValue.MEMO,
		UdfValue.URL,
		UdfValue.FLOAT,
		UdfValue.LIST));

	private static UserRelatedManager userRelatedManager;

	private static CategoryManager categoryManager;

	private final static ConcurrentMap<String, Boolean> attachmentsIsDeleted = new ConcurrentHashMap<String, Boolean>();

	private final ReentrantReadWriteLock rwl = new ReentrantReadWriteLock();

	@GuardedBy("rwl")
	private final OrderedTree<TaskRelatedInfo> cache;

	@GuardedBy("rwl")
	private final HashMap<Integer, String> taskNumbers = new HashMap<>();

	/**
	 * Возвращает экземпляр текущего класса
	 *
	 * @return экземпляр класса TaskRelatedManager
	 * @throws GranException при необзодимости
	 */
	public static TaskRelatedManager getInstance() {
		TaskRelatedManager ins = instance.get();
		if (ins != null) {
			return ins;
		}

		try {
			if (instance.compareAndSet(null, new TaskRelatedManager())) {
				log.info("Initializing task cache");
				instance.get().fillTasks();
				instance.get().taskNumbers.put(1,"1");
				log.info("Initializing task ACL");
				instance.get().fillAcl();

				// should be called single thread first time, otherwise second call will see initialized, but not filled instance
			}
			return instance.get();
		} catch (GranException e) {
			throw new IllegalStateException("Cannot init TaskRelatedManager", e);
		}
	}

	/**
	 * Конструктор по умолчанию
	 *
	 * @throws GranException при необходимости
	 */
	private TaskRelatedManager() throws GranException {
		Task t = (Task)hu.getObject(Task.class, "1");
		String longText = null;
		if (t.getLongtext() != null) {
			longText = t.getLongtext().getId();
		}
		TaskRelatedInfo tr = new TaskRelatedInfo(
			t.getId(),
			t.getDescription(),
			longText,
			t.getName(),
			t.getShortname(),
			t.getSubmitdate(),
			t.getUpdatedate(),
			t.getClosedate(),
			t.getAbudget(),
			t.getBudget(),
			t.getDeadline(),
			t.getNumber(),
			t.getSubmitter().getId(),
			t.getHandlerId(),
			t.getHandlerUserId(),
			t.getHandlerGropuId(),
			null,
			t.getCategory().getId(),
			t.getCategory().getWorkflow().getId(),
			t.getStatus() != null ? t.getStatus().getId() : null,
			t.getResolution() != null ? t.getResolution().getId() : null,
			t.getPriority() != null ? t.getPriority().getId() : null);
		this.cache = new OrderedTree<TaskRelatedInfo>(tr);

		userRelatedManager = UserRelatedManager.getInstance();
		categoryManager = KernelManager.getCategory();
	}

	/**
	 * Возвращает деревянный кеш
	 *
	 * @return деревянный кеш
	 */
	public List<TaskRelatedInfo> getCacheContents() {
		rwl.readLock().lock();
		try {
			return cache.getContents();
		} finally {
			rwl.readLock().unlock();
		}

	}

	/**
	 * Добавляет задачу в кеш
	 *
	 * @param id ID задачи
	 * @param o  задача
	 * @throws GranException при  необзодимости
	 */
	protected void add(String id, TaskRelatedInfo o) throws GranException {
		rwl.writeLock().lock();
		try {
			cache.add(new TaskRelatedInfo(id), o);
			updateInsert(o);
		} finally {
			rwl.writeLock().unlock();
		}

	}

	/**
	 * Обновляет дерево после догбавления задачи
	 *
	 * @param o добавляемая задача
	 * @throws GranException при необзодимости
	 */
	private void updateInsert(TaskRelatedInfo o) throws GranException {
		rwl.writeLock().lock();
		try {
			List<TaskRelatedInfo> parents = cache.getAncestors(o);
			List<TaskRelatedInfo> children = cache.getChildren(o);
			int childcount = 0;
			Calendar date = o.getUpdatedate();
			//still equals Abudget
			Long budget = o.getAbudget();
			for (TaskRelatedInfo related : children) {
				childcount += related.getChildCount() + 1;
				if (date == null || related.getUpdatedate() != null && date.getTimeInMillis() < related.getUpdatedate().getTimeInMillis()) {
					date = related.getUpdatedate();
				}
				budget += related.getAbudget();
			}
			o.setActualBudget(budget);
			if (date != null) {
				o.setUpdatedate(date);
			}
			o.setChildCount(childcount);
			for (TaskRelatedInfo oper : parents) {
				oper.updateUpdateDate(o.getUpdatedate());
				oper.setActualBudget(oper.getActualBudget() + o.getAbudget());
				oper.setChildCount(oper.getChildCount() + o.getChildCount() + 1);
			}
			taskNumbers.put(o.getNumberInt(), Intern.process(o.getId()));
		} finally {
			rwl.writeLock().unlock();
		}

	}

	/**
	 * Возвращает задачу по ее ID
	 *
	 * @param id ID задачи
	 * @return задача
	 */
	public TaskRelatedInfo find(String id) {
		rwl.readLock().lock();
		try {
			return cache.get(new TaskRelatedInfo(id));
		} finally {
			rwl.readLock().unlock();
		}

	}

	/**
	 * Возвращает задачу по ее ID. Не блокирован, использовать только внутри этого класса и только
	 * в случае, когда readlock на rwl ставится вышестоящим методом.
	 *
	 * @param id ID задачи
	 * @return задача
	 */
	private TaskRelatedInfo findUnlocked(String id) {
		//noinspection FieldAccessNotGuarded
		return cache.get(new TaskRelatedInfo(id));
	}

	/**
	 * Возвращает задачу по ее ID
	 *
	 * @param id ID задачи
	 * @return задача
	 * @throws GranException при необзодимости
	 */
	private static TaskRelatedInfo loadItem(String id) throws GranException {
		TaskRelatedInfo vObj = null;
		try {
			List<TaskRelatedInfo> col = hu.getList(
				"select new com.trackstudio.kernel.cache.TaskRelatedInfo(task.id, task.description, task.longtext.id, task.name, task.shortname, task.submitdate, task.updatedate, task.closedate, task.abudget, task.budget, task.deadline, task.number, task.submitter.id, task.handler.id, task.handler.user.id, task.handler.prstatus.id, task.parent.id, task.category.id, task.category.workflow.id, task.status.id, task.resolution.id, task.priority.id) from com.trackstudio.model.Task as task where task.id=?",
				id);
			if (col.size() > 0) {
				vObj = col.get(0);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		if (vObj == null) {
			throw new TaskNotFoundException(id);
		}
		return vObj;

	}

	/**
	 * ПОмещает задачу с указанным ID в карту
	 *
	 * @param map Карта задач
	 * @param id  ID задачи
	 * @throws GranException при необходимости
	 */
	private void putIn(TreeMap<String, TaskRelatedInfo> map, String id) throws GranException {
		rwl.writeLock().lock();
		try {
			if (findUnlocked(id) == null) {
				TaskRelatedInfo tri = map.get(id);
				if (tri == null) {
					log.error("Cannot load task with id = " + id);
				}
				if (tri.getParentId() != null) {
					if (findUnlocked(tri.getParentId()) == null) {
						putIn(map, tri.getParentId());
					}
					add(tri.getParentId(), tri);
				}
			}
		} finally {
			rwl.writeLock().unlock();
		}

	}

	/**
	 * Заполняет задачи данными (права, пользовательские поля и т.д.)
	 *
	 * @throws GranException при необзодимости
	 */
	private void fillTasks() throws GranException {
		rwl.writeLock().lock();
		try {
			TreeMap<String, TaskRelatedInfo> map = new TreeMap<String, TaskRelatedInfo>();
			for (Object o : hu.getList(
				"select new com.trackstudio.kernel.cache.TaskRelatedInfo(task.id, task.description, task.longtext.id, task.name, task.shortname, task.submitdate, task.updatedate, task.closedate, task.abudget, task.budget, task.deadline, task.number, task.submitter.id, task.handler.id, task.handler.user.id, task.handler.prstatus.id,  task.parent.id, task.category.id, task.category.workflow.id, task.status.id, task.resolution.id, task.priority.id) from com.trackstudio.model.Task as task")) {
				TaskRelatedInfo tri = (TaskRelatedInfo)o;
				map.put(tri.getId(), tri);
			}

			log.debug("total tasks: " + map.size());
			for (String id : map.keySet()) {
				putIn(map, id);
			}
			TreeSet<String> allowedPrstatuses = getAllowedPrstatuses("1", "1");
			initUDFValues(map.values(), allowedPrstatuses);
			log.debug("cached tasks: " + cache.size());
		} finally {
			rwl.writeLock().unlock();
		}

	}

	/**
	 * Достается список всех тасковых ACL в системе, затем из этого списка
	 * для каждого ACL берется задача, находится в кэше,
	 * к этой задаче в кэш добавляется найденный ACL.
	 *
	 * @throws GranException при необзодимости
	 */
	private void fillAcl() throws GranException {
		rwl.writeLock().lock();
		try {
			for (Object o : hu.getList(
				"select new com.trackstudio.kernel.cache.InternalACLIntermediate(acl.task.id, acl.id, acl.usersource.user.id, acl.usersource.prstatus.id, acl.prstatus.id, acl.owner.id, acl.override) from com.trackstudio.model.Acl as acl where acl.task is not null")) {
				InternalACLIntermediate iaclim = (InternalACLIntermediate)o;
				TaskRelatedInfo tr = findUnlocked(iaclim.getObjectId());
				tr.addAcl(iaclim.getInternalACL());
				cache.replaceWith(tr, tr);
			}
		} finally {
			rwl.writeLock().unlock();
		}

	}

	/**
	 * Очищает кеш ACL для указанного пользователя, задачи и статуса
	 *
	 * @param id      ID задачи
	 * @param userId  ID пользователя
	 * @param groupId ID статуса
	 * @throws GranException при необходимости
	 */
	public void invalidateAcl(String id, String userId, String groupId) throws GranException {
		rwl.writeLock().lock();
		try {
			TaskRelatedInfo tr = findUnlocked(id);
			ConcurrentHashMap acl = tr.getAcl();
			if (userId != null) {
				acl.remove(userId);
			} else {
				acl.remove(groupPrefix + groupId);
			}

			List q;
			if (userId != null) {
				q = hu.getList(
					"select new com.trackstudio.kernel.cache.InternalACL(acl.id, acl.usersource.user.id, acl.usersource.prstatus.id, acl.prstatus.id, acl.owner.id, acl.override) from com.trackstudio.model.Acl as acl  where acl.task=? and acl.usersource.user=?",
					id,
					userId);
			} else {
				q = hu.getList(
					"select new com.trackstudio.kernel.cache.InternalACL(acl.id, acl.usersource.user.id, acl.usersource.prstatus.id, acl.prstatus.id, acl.owner.id, acl.override) from com.trackstudio.model.Acl as acl  where acl.task=? and acl.usersource.prstatus=?",
					id,
					groupId);
			}
			for (InternalACL iacl : (List<InternalACL>)q) {
				addAcl(acl, iacl);
			}
			tr.setAcl(acl);
			cache.replaceWith(tr, tr);

			Collection<TaskRelatedInfo> coll = cache.getDescendents(tr);
			coll.add(tr);
			for (TaskRelatedInfo tri : coll) {
				cache.replaceWith(tri, tri);
			}
			CategoryCacheManager.getInstance().invalidateCategoryIsViewable();
		} finally {
			rwl.writeLock().unlock();
		}

	}

	/**
	 * Очищает кеш задачи при ее перемещаении
	 *
	 * @param id ID перемещаемой задачи
	 * @throws GranException при необзодимости
	 */
	public void invalidateAclWhenMove(String id) throws GranException {
		rwl.writeLock().lock();
		try {
			TaskRelatedInfo tr = findUnlocked(id);
			for (Iterator it = cache.getDescendents(tr).iterator(); it.hasNext(); ) {
				TaskRelatedInfo tri = (TaskRelatedInfo)it.next();
				cache.replaceWith(tri, tri);
			}
			// remove cached effective statuses
			cache.replaceWith(tr, tr);
			CategoryCacheManager.getInstance().invalidateCategoryIsViewable();
		} finally {
			rwl.writeLock().unlock();
		}

	}

	/**
	 * Очищает кеш задачи при изменении ее ответственного
	 *
	 * @param userId      ID нового ответственного
	 * @param oldPrstatus старый статус
	 * @throws GranException при необзодимости
	 */
	public void invalidateAclWhenChangeStatus(String userId, String oldPrstatus) throws GranException {
		rwl.writeLock().lock();
		try {
			Set<TaskRelatedInfo> taskForInvalidate = new HashSet<TaskRelatedInfo>();
			String currentPrstatus = KernelManager.getFind().findUser(userId).getPrstatus().getId();
			Map<String, Collection> paramMap = new LinkedHashMap<String, Collection>();
			paramMap.put("prstatuses", Arrays.asList(oldPrstatus, currentPrstatus));
			Map<String, String> paramList = new LinkedHashMap<String, String>();
			paramList.put("userId", userId);
			for (Object o : hu.getListMap(
				"select new com.trackstudio.kernel.cache.InternalACLIntermediate(acl.task.id, acl.id, acl.usersource.user.id, acl.usersource.prstatus.id, acl.prstatus.id, acl.owner.id, acl.override) from com.trackstudio.model.Acl as acl  where acl.task is not null and (acl.usersource.user=:userId or acl.usersource.prstatus.id in (:prstatuses))",
				paramList,
				paramMap)) {
				InternalACLIntermediate iaclim = (InternalACLIntermediate)o;
				TaskRelatedInfo tri = findUnlocked(iaclim.getObjectId());
				taskForInvalidate.addAll(cache.getDescendents(tri));
				taskForInvalidate.add(tri);
				taskForInvalidate.addAll(cache.getAncestors(tri));
			}
			hu.cleanSession();
			for (TaskRelatedInfo tri : taskForInvalidate) {
				cache.replaceWith(tri, tri);
			}
			hu.cleanSession();
			CategoryCacheManager.getInstance().invalidateCategoryIsViewable();
		} finally {
			rwl.writeLock().unlock();
		}

	}

	/**
	 * К существующему набору ACL добавляется новый. Если этот ACL задан
	 * через группу, к его ID добавляется префикс
	 *
	 * @param acl  карта ACL
	 * @param iacl добавляемый ACL
	 */
	protected static void addAcl(Map<String, TreeSet<InternalACL>> acl, InternalACL iacl) {
		String id = iacl.getUserId() != null ? iacl.getUserId() : groupPrefix + iacl.getGroupId();
		TreeSet<InternalACL> o = acl.get(id);
		if (o == null) {
			o = new TreeSet<InternalACL>();
		}
		o.add(iacl);
		acl.put(id, o);
	}

	/**
	 * Возвращает цепочку задач от одной до другой
	 *
	 * @param fromid начальная задача
	 * @param id     конечная задача
	 * @return список цепочки задач
	 * @throws GranException при необзодимости
	 */
	public ArrayList<TaskRelatedInfo> getTaskRelatedInfoChain(String fromid, String id) throws GranException {
		rwl.readLock().lock();
		try {
			TaskRelatedInfo tr = findUnlocked(id);
			// ordered
			ArrayList<TaskRelatedInfo> l = cache.getAncestors(tr);
			if (tr != null) {
				l.add(tr);
			}
			if (fromid != null) {
				TaskRelatedInfo tr2 = findUnlocked(fromid);
				if (!l.contains(tr2)) {
					return null;
				}
				List<TaskRelatedInfo> l2 = cache.getAncestors(tr2);
				l.removeAll(l2);
			}
			return l;
		} finally {
			rwl.readLock().unlock();
		}

	}

	/**
	 * Проверяет является ли указанные задачи связаны между собой
	 *
	 * @param fromid ID начальной задачи
	 * @param id     ID конечной задачи
	 * @return TRUE - есть связь, FALSE - нет связи
	 * @throws GranException при необзодимости
	 */
	public boolean hasPath(String fromid, String id) throws GranException {
		rwl.readLock().lock();
		try {
			return getTaskRelatedInfoChain(fromid, id) != null || getTaskRelatedInfoChain(id, fromid) != null;
		} finally {
			rwl.readLock().unlock();
		}
	}

	/**
	 * Для заданного пользователя и статуса, начиная с заданной задачи и вверх достаем наборы ACL, привязанные к задаче
	 * (assigned ACL) через этого пользователя, т.е. не через prstatus. Из этих ACL достаем prstatusы, добавляем к ним
	 * собственный статус пользователя.
	 *
	 * @param userId         ID пользователя
	 * @param taskId         ID задачи
	 * @param userPrstatusId ID пользовательского статуса
	 * @return список ID статусов
	 * @throws GranException при необходимости
	 */

	public TreeSet<String> getAllowedPrstatuses(String userId, String taskId, String userPrstatusId) throws GranException {
		rwl.readLock().lock();
		try {
			TreeSet<String> ret = new TreeSet<String>();
			TaskRelatedInfo tr = findUnlocked(taskId);
			boolean overrided = false; // означает что на данном уровне был переопределен статус и дальше можно не проверять

			String prstatusId = userPrstatusId;
			if (userPrstatusId == null) {
				prstatusId = userRelatedManager.find(userId).getPrstatusId();
			}

			// check task tree, from the current task to the parent
			while (tr != null) {
				Map acl = tr.getFastUnsafeAcl();
				TreeSet<String> prstatuses = new TreeSet<String>();

				// получаем список assigned ACL для одной конкретной задачи и одного юзера
				TreeSet<InternalACL> assignedACL = (TreeSet<InternalACL>)acl.get(userId);
				if (assignedACL != null) {
					for (InternalACL a : assignedACL) {
						if (a.getPrstatusId() != null) {
							prstatuses.add(a.getPrstatusId());
						}

						// если хотя бы один из ACL был override, то запоминаем это и продолжаем обрабатывать остальные ACL
						// для этой задачи
						if (a.getOverride()) {
							overrided = true;
						}
					}
					ret.addAll(prstatuses);
					// если все вышестоящие ACL были переопределены, то дальше идти нет смысла.
					if (overrided) {
						break;
					}
				}
				tr = cache.getParent(tr);

				// check for group acl with overided status
				for (Object o : acl.keySet()) {
					TreeSet<InternalACL> acls = (TreeSet<InternalACL>)acl.get(o);
					for (InternalACL a : acls) {
						if (a.getGroupId() != null && a.getGroupId().equals(prstatusId) && a.getPrstatusId() != null
							&& userRelatedManager.getUserIdChain(a.getOwnerId(), userId) != null)
						{
							ret.add(a.getPrstatusId());
							if (a.getOverride()) {
								overrided = true;
							}
						}
					}
				}
				if (overrided) {
					break;
				}
			}

			// теперь добавляем в список собственный статус пользователя
			// это не нужно делать если статус был переопределен
			if (!overrided) {
				ret.add(userPrstatusId != null ? userPrstatusId : prstatusId);
			}
			return ret;
		} finally {
			rwl.readLock().unlock();
		}

	}

	/**
	 * Проверяет наличие доступа пользователя и статуса к задаче
	 *
	 * @param userid     ID пользователя
	 * @param taskId     ID задачи
	 * @param prstatusId ID статуса
	 * @return TRUE - есть доступ, FALSE - нет
	 * @throws GranException при необходимости
	 */
	public boolean hasAccess(String userid, String taskId, String prstatusId) throws GranException {
		rwl.readLock().lock();
		try {
			List<TaskRelatedInfo> l = getTaskRelatedInfoChain(null, taskId);
			boolean access = false;
			for (TaskRelatedInfo tr : l) {
				Map acl = tr.getFastUnsafeAcl();
				if (acl.containsKey(userid)) {
					access = true;
					break;
				}
				if (acl.containsKey(groupPrefix + prstatusId)) {
					for (InternalACL iacl : ((TreeSet<InternalACL>)acl.get(groupPrefix + prstatusId))) {
						if (userRelatedManager.getUserIdChain(iacl.getOwnerId(), userid) != null) {
							access = true;
							break;
						}
					}
					if (access) {
						break;
					}
				}
			}
			return access && isTaskViewable(l, userid, prstatusId);
		} finally {
			rwl.readLock().unlock();
		}

	}

	/**
	 * Проверяет наличие прав на просмотр пользователя и статуса на задачу
	 *
	 * @param userid      ID пользователя
	 * @param taskId      ID задачи
	 * @param prstatusId  ID статуса
	 * @param checkAccess проверять ли доступ
	 * @return TRUE - может, FALSE - нет
	 * @throws GranException при необходимости
	 */
	public boolean onSight(String userid, String taskId, String prstatusId, boolean checkAccess) throws GranException {
		rwl.readLock().lock();
		try {
			if (taskId.equals("1")) {
				return true;
			}
			if (checkAccess && hasAccess(userid, taskId, prstatusId)) {
				return true;
			}
			if (!isTaskViewable(taskId, userid, prstatusId)) {
				return false;
			}
			String withPrefixPrstatus = groupPrefix + prstatusId;
			for (Iterator it = cache.getDescendents(new TaskRelatedInfo(taskId)).iterator(); it.hasNext(); ) {
				if (onSightAllAclUnlocked((TaskRelatedInfo)it.next(), userid, withPrefixPrstatus, prstatusId)) {
					return true;
				}
			}
			return onSightAllAclUnlocked(findUnlocked(taskId), userid, groupPrefix + prstatusId, prstatusId);
		} finally {
			rwl.readLock().unlock();
		}

	}

	private boolean onSightAllAclUnlocked(TaskRelatedInfo tr, String userid, String withPrefixPrstatusId, String prstatusId) throws GranException {
		Map<String, TreeSet<InternalACL>> acls = tr.getFastUnsafeAcl();
		boolean aclSeted = false;
		if (acls.containsKey(userid)) {
			aclSeted = true;
		} else {
			TreeSet<InternalACL> sets = acls.get(withPrefixPrstatusId);
			if (sets != null) {
				for (InternalACL iacl : sets) {
					if (userRelatedManager.getUserIdChain(iacl.getOwnerId(), userid) != null) {
						aclSeted = true;
						break;
					}
				}
			}
		}
		return aclSeted && isTaskViewable(tr.getId(), userid, prstatusId);
	}

	/**
	 * Проверяет можно ли пользователю и и статусу смотреть задачу
	 *
	 * @param userid     ID пользователя
	 * @param taskId     ID задачи
	 * @param prstatusId ID статуса
	 * @return TRUE - можно, FALSE - нет
	 * @throws GranException при необходимости
	 */
	private boolean isTaskViewable(String taskId, String userid, String prstatusId) throws GranException {
		rwl.readLock().lock();
		try {
			return isTaskViewable(getTaskRelatedInfoChain(null, taskId), userid, prstatusId);
		} finally {
			rwl.readLock().unlock();
		}

	}

	/**
	 * Проверяет можно ли пользователю и и статусу смотреть задачи
	 *
	 * @param userid     ID пользователя
	 * @param taskChain  список задач
	 * @param prstatusId ID статуса
	 * @return TRUE - можно, FALSE - нет
	 * @throws GranException при необходимости
	 */

	private boolean isTaskViewable(List<TaskRelatedInfo> taskChain, String userid, String prstatusId) throws GranException {
		rwl.readLock().lock();
		try {
			boolean canView = true;
			for (TaskRelatedInfo t : taskChain) {
				if (!categoryManager.isCategoryViewable(t.getId(), userid, t.getCategoryId(), prstatusId)) {
					canView = false;
					break;
				}
			}
			return canView;
		} finally {
			rwl.readLock().unlock();
		}

	}

	/**
	 * Для заданного пользователя и статуса, начиная с заданной задачи и вверх достаем наборы ACL, привязанные к задаче
	 * (assigned ACL) через этого пользователя, т.е. не через prstatus. Из этих ACL достаем prstatusы, добавляем к ним
	 * собственный статус пользователя.
	 *
	 * @param userId ID пользователя
	 * @param taskId ID задачи
	 * @return список статусов
	 * @throws GranException при необходимости
	 */
	public TreeSet<String> getAllowedPrstatuses(String userId, String taskId) throws GranException {
		rwl.readLock().lock();
		try {
			return getAllowedPrstatuses(userId, taskId, null);
		} finally {
			rwl.readLock().unlock();
		}

	}

	/**
	 * Возвращает коллекцию userid, которым есть доступ на эту задачу (иерархический)
	 *
	 * @param taskId ID задачи
	 * @return список пользователей
	 * @throws GranException при необходимости
	 */
	public TreeSet<String> getAllowedUsers(String taskId) throws GranException {
		rwl.readLock().lock();
		try {
			TreeSet<String> ret = new TreeSet<String>();

			List<TaskRelatedInfo> l = getTaskRelatedInfoChain(null, taskId);
			for (TaskRelatedInfo tr : l) {
				Map<String, TreeSet<InternalACL>> acl = tr.getFastUnsafeAcl();
				for (String id : acl.keySet()) {
					if (id.startsWith(groupPrefix)) {
						ret.addAll(getSubordinatedUsersForPrstatus(id.substring(groupPrefix.length()), acl.get(id), taskId, true));
					} else {
						if (isTaskViewable(taskId, id, userRelatedManager.find(id).getPrstatusId())) {
							ret.add(id);
						}
					}
				}
			}
			return ret;
		} finally {
			rwl.readLock().unlock();
		}

	}

	public HashMap<String, Boolean> getEffectiveUsers(String taskId) throws GranException {
		rwl.readLock().lock();
		try {
			HashMap<String, Boolean> ret = new HashMap<String, Boolean>();
			List<TaskRelatedInfo> l = getTaskRelatedInfoChain(null, taskId);
			for (TaskRelatedInfo tr : l) {
				Map<String, TreeSet<InternalACL>> acl = tr.getFastUnsafeAcl();
				for (String id : acl.keySet()) {
					if (id.startsWith(groupPrefix)) {
						ret.putAll(getEffectiveUsersForPrstatus(id.substring(groupPrefix.length()), acl.get(id), taskId));
					} else {
						boolean view = isTaskViewable(taskId, id, userRelatedManager.find(id).getPrstatusId());
						ret.put(id, view);
					}
				}
			}
			return ret;
		} finally {
			rwl.readLock().unlock();
		}

	}

	private TreeSet<String> getSubordinatedUsersForPrstatus(String prstatusId, TreeSet<InternalACL> aclSet, String taskId, boolean checkViewable)
		throws GranException
	{
		rwl.readLock().lock();
		try {
			TreeSet<String> ret = new TreeSet<String>();
			List<String> list = hu.getList("select u.id from com.trackstudio.model.User u where u.prstatus=? and u.active=1", prstatusId);
			Set<String> subordUsers = new HashSet<String>();
			for (InternalACL iacl : aclSet) {
				subordUsers.addAll(userRelatedManager.getDescendents(iacl.getOwnerId()));
				subordUsers.add(iacl.getOwnerId());
			}
			list.retainAll(subordUsers);
			if (checkViewable) {
				for (String uId : list) {
					if (isTaskViewable(taskId, uId, prstatusId)) {
						ret.add(uId);
					}
				}
			} else {
				ret.addAll(list);
			}
			return ret;
		} finally {
			rwl.readLock().unlock();
		}

	}

	private HashMap<String, Boolean> getEffectiveUsersForPrstatus(String prstatusId, TreeSet<InternalACL> aclSet, String taskId) throws GranException {
		rwl.readLock().lock();
		try {
			HashMap<String, Boolean> ret = new HashMap<String, Boolean>();
			List<String> list = hu.getList("select u.id from com.trackstudio.model.User u where u.prstatus=? and u.active=1", prstatusId);
			Set<String> subordUsers = new HashSet<String>();
			for (InternalACL iacl : aclSet) {
				subordUsers.addAll(userRelatedManager.getDescendents(iacl.getOwnerId()));
				subordUsers.add(iacl.getOwnerId());
			}
			list.retainAll(subordUsers);
			for (String uId : list) {
				boolean view = isTaskViewable(taskId, uId, prstatusId);
				ret.put(uId, view);
			}

			return ret;
		} finally {
			rwl.readLock().unlock();
		}

	}

	/**
	 * Возвращает список ID пользователей, которые задействованы в указанной задаче
	 *
	 * @param taskId ID задачи
	 * @return список пользователей
	 * @throws GranException при необходимости
	 */

	public List<String> getUsedUsersIdList(String taskId) throws GranException {
		rwl.readLock().lock();
		try {
			TreeSet<String> list = new TreeSet<String>();

			for (Iterator i = cache.getDescendents(new TaskRelatedInfo(taskId)).iterator(); i.hasNext(); ) {
				TaskRelatedInfo a = (TaskRelatedInfo)i.next();
				Map<String, TreeSet<InternalACL>> st = a.getFastUnsafeAcl();
				for (String id : st.keySet()) {
					if (id.startsWith(groupPrefix)) {
						list.addAll(getSubordinatedUsersForPrstatus(id.substring(groupPrefix.length()), st.get(id), taskId, false));
					} else {
						list.add(id);
					}
				}
			}
			return new ArrayList<String>(list);
		} finally {
			rwl.readLock().unlock();
		}

	}

	/**
	 * Возвращает список доступных статусов
	 *
	 * @param taskId ID задачи
	 * @return список статусов
	 * @throws GranException при необходимости
	 */
	public TreeSet<String> getAllowedUsersStatuses(String taskId) throws GranException {
		rwl.readLock().lock();
		try {
			return getPrstatuses(getTaskRelatedInfoChain(null, taskId));
		} finally {
			rwl.readLock().unlock();
		}

	}

	private TreeSet<String> getPrstatuses(List<TaskRelatedInfo> l) throws GranException {
		rwl.readLock().lock();
		try {
			TreeSet<String> ret = new TreeSet<String>();
			for (TaskRelatedInfo tr : l) {
				Map<String, TreeSet<InternalACL>> acl = tr.getFastUnsafeAcl();
				for (String id : acl.keySet()) {
					if (id.startsWith(groupPrefix)) {
						Set<InternalACL> set = acl.get(id);
						for (InternalACL a : set) {
							if (a.getPrstatusId() != null) {
								ret.add(a.getPrstatusId());
							} else if (a.getGroupId() != null) {
								ret.add(a.getGroupId());
							}
						}
					} else {
						TreeSet<InternalACL> q = acl.get(id);
						if (q != null) {
							for (InternalACL inACL : q) {
								String prstatusId = inACL.getPrstatusId();
								if (prstatusId != null) {
									ret.add(prstatusId);
								}
							}
						}
					}
				}
			}
			return ret;
		} finally {
			rwl.readLock().unlock();
		}

	}

	/**
	 * Возвращает список доступных статусов
	 *
	 * @param taskId ID задачи
	 * @return список статусов
	 * @throws GranException при необходимости
	 */

	public TreeSet<String> getAllowedGroups(String taskId) throws GranException {
		rwl.readLock().lock();
		try {
			TreeSet<String> ret = new TreeSet<String>();
			List<TaskRelatedInfo> l = getTaskRelatedInfoChain(null, taskId);
			for (TaskRelatedInfo tr : l) {
				Map<String, TreeSet<InternalACL>> acl = tr.getFastUnsafeAcl();
				for (String id : acl.keySet()) {
					if (id.startsWith(groupPrefix)) {
						Set<InternalACL> set = acl.get(id);
						for (InternalACL a : set) {
							if (a.getPrstatusId() != null) {
								ret.add(a.getPrstatusId());
							} else if (a.getGroupId() != null) {
								ret.add(a.getGroupId());
							}
						}
					}
				}
			}
			return ret;
		} finally {
			rwl.readLock().unlock();
		}

	}

	/**
	 * Возвращает список ID прав доступа
	 *
	 * @param taskId ID задачи
	 * @return список прав доступа
	 * @throws GranException при необходимости
	 */
	public ArrayList<String> getAclList(String taskId) throws GranException {
		rwl.readLock().lock();
		try {
			ArrayList<String> list = new ArrayList<String>();
			for (TreeSet<InternalACL> re : findUnlocked(taskId).getFastUnsafeAcl().values()) {
				for (InternalACL aRe : re) {
					list.add(aRe.getAclId());
				}
			}
			return list;
		} finally {
			rwl.readLock().unlock();
		}

	}

	/**
	 * Возвращает список ID статусов, которые задействованы в задаче
	 *
	 * @param taskId ID задачи
	 * @return список статусов
	 * @throws GranException при необходимости
	 */

	public List<String> getUsedStatusesIdList(String taskId) throws GranException {
		rwl.readLock().lock();
		try {
			List<TaskRelatedInfo> l = cache.getDescendents(new TaskRelatedInfo(taskId));
			TreeSet<String> ret = getPrstatuses(l);
			return new ArrayList<String>(ret);
		} finally {
			rwl.readLock().unlock();
		}

	}

	/**
	 * Проверяет существование задачи
	 *
	 * @param taskid ID задачи
	 * @return TRUE - задача существует, FALSE - нет
	 * @throws GranException при необходимости
	 */
	public boolean isTaskExists(String taskid) throws GranException {
		// тут findUnlocked использовать не можем, т.к. нет вышестоящего лока
		return find(taskid) != null;
	}

	/**
	 * Возвращает список всех дочерних задач из кэша
	 *
	 * @param id ID задачи
	 * @return список дочерних задач из кэша
	 * @throws GranException при необходимости
	 */
	public List<TaskRelatedInfo> getChildrenRecursive(String id) throws GranException {
		rwl.readLock().lock();
		try {
			ArrayList<TaskRelatedInfo> subtasks = new ArrayList<TaskRelatedInfo>();
			for (Iterator<TaskRelatedInfo> subtasksIterator = cache.getDescendents(new TaskRelatedInfo(id)).iterator(); subtasksIterator.hasNext(); ) {
				TaskRelatedInfo key = subtasksIterator.next();
				subtasks.add(key);
			}
			return subtasks;
		} finally {
			rwl.readLock().unlock();
		}

	}

	public List<String> getParents(Collection<String> tasks) throws GranException {
		rwl.readLock().lock();
		try {
			List<String> result = new ArrayList<String>();
			for (String taskId : tasks) {
				if (findUnlocked(taskId).getChildCount() > 0) {
					result.add(taskId);
				}
			}
			return result;
		} finally {
			rwl.readLock().unlock();
		}

	}

	/**
	 * Возвращает список дочерних задач для проекта
	 *
	 * @param id ID задачи
	 * @return список задач
	 * @throws GranException при необходимости
	 */

	public ArrayList<String> getProjectChildren(String id) throws GranException {
		rwl.readLock().lock();
		try {
			ArrayList<TaskRelatedInfo> children = (ArrayList<TaskRelatedInfo>)cache.getChildren(new TaskRelatedInfo(id));
			ArrayList<String> subtasksId = new ArrayList<String>();
			for (TaskRelatedInfo tri : children) {
				if (tri.getChildCount() != 0) {
					subtasksId.add(tri.getId());
				}
			}
			return subtasksId;
		} finally {
			rwl.readLock().unlock();
		}
	}

	private class ViewableTaskResult {

		public Map<String, Boolean> viewableItems = new HashMap<String, Boolean>();

		public List<String> itemsToCheckNextTime = new ArrayList<>();
	}

	/**
	 * Used only inside locked methods, so internal locks not required (slow)
	 *
	 * @param userId
	 * @param prstatusId
	 * @param taskId
	 * @param parentHasAccess
	 * @return
	 * @throws GranException
	 */
	private ViewableTaskResult getViewableTasks(String userId, String prstatusId, String taskId, boolean parentHasAccess) throws GranException {
		List<Map.Entry<TaskRelatedInfo, Integer>> allowed = cache.getChildrenWithSubchildrenCount(new TaskRelatedInfo(taskId));
		ViewableTaskResult result = new ViewableTaskResult();

		for (Map.Entry<TaskRelatedInfo, Integer> item : allowed) {
			TaskRelatedInfo tr = item.getKey();
			boolean hasAccess;
			hasAccess = hasAccess(userId, tr.getId(), prstatusId);
			if (hasAccess || onSight(userId, tr.getId(), prstatusId, false)) {
				result.viewableItems.put(tr.getId(), true); // list of items that visible
				if (item.getValue() > 0) {
					result.itemsToCheckNextTime.add(tr.getId()); // list of items which have other items, we'll check them next time
				}
			}
		}
		return result;
	}

	/**
	 * Возвращает карту доступных задач с подзадачами
	 *
	 * @param userId     ID пользователя
	 * @param prstatusId ID статуса
	 * @param taskId     ID задачи
	 * @return карта задач
	 * @throws GranException при необходимости
	 */
	public Map<String, Boolean> getAllowedChildrenWithSubtasksMap(String userId, String prstatusId, String taskId) throws GranException {
		rwl.readLock().lock();
		try {
			Map<String, Boolean> result = new HashMap<String, Boolean>();
			boolean parentHasAccess = hasAccess(userId, taskId, prstatusId);
			ViewableTaskResult viewableTasks = getViewableTasks(userId, prstatusId, taskId, parentHasAccess);
			if (viewableTasks.viewableItems.isEmpty()) {
				return result;
			}
			result.putAll(viewableTasks.viewableItems);
			List<String> checkTasks = viewableTasks.itemsToCheckNextTime;
			// List<String> checkTasks = new ArrayList<String>(viewableTasks.viewableItems.keySet());
			while (!checkTasks.isEmpty()) {
				String id = checkTasks.get(0);
				checkTasks.remove(0);
				ViewableTaskResult vt2 = getViewableTasks(userId, prstatusId, id, result.get(id));
				result.putAll(vt2.viewableItems);
				checkTasks.addAll(vt2.itemsToCheckNextTime);
			}
			return result;
		} finally {
			rwl.readLock().unlock();
		}

	}

	/**
	 * Возвращает карту доступных задач
	 *
	 * @param userId     ID пользователя
	 * @param prstatusId ID статуса
	 * @param taskId     ID задачи
	 * @return карта задач
	 * @throws GranException при необходимости
	 */

	public Map<String, Boolean> getAllowedChildrenMap(String userId, String prstatusId, String taskId) throws GranException {
		rwl.readLock().lock();
		try {
			ViewableTaskResult result = getViewableTasks(userId, prstatusId, taskId, hasAccess(userId, taskId, prstatusId));
			return result.viewableItems;
		} finally {
			rwl.readLock().unlock();
		}

	}

	/**
	 * Возвращает список ID дочерних задач
	 *
	 * @param id ID задачи
	 * @return список задач
	 * @throws GranException при необходимости
	 */
	public List<String> getChildrenId(String id) throws GranException {
		rwl.readLock().lock();
		try {
			if (id == null) {
				return null;
			}

			List<TaskRelatedInfo> children = cache.getChildren(new TaskRelatedInfo(id));
			List<String> hop = new ArrayList<String>();
			for (TaskRelatedInfo t : children) {
				hop.add(t.getId());
			}
			return hop;
		} finally {
			rwl.readLock().unlock();
		}

	}

	/**
	 * Возвращает список сообщений для задачи
	 *
	 * @param id ID задачи
	 * @return Список сообщений, отсортированных по премени
	 * @throws GranException при необходимости
	 */
	public static List<MessageCacheItem> getMessages(final String id) throws GranException {
		final List<MessageCacheItem> msgs = new ArrayList<MessageCacheItem>();
		// Зачем это делается ? Вроде же оно должно вызываться только при изменении задач.
		// У олежке при экспорте в XML дергается этот метод, в итоге commit
		// и пересоздание соединений происходит 2000 раз в секунду
		// hu.cleanSession();
		List<MessageCacheItem> list = hu.getList(
			"select new com.trackstudio.kernel.cache.MessageCacheItem(message.id, message.description, message.time, message.hrs, message.deadline, message.budget, message.task.id, message.submitter.id, message.resolution.id, message.priority.id, message.handler.id, message.handler.user.id, message.handler.prstatus.id, message.mstatus.id, message.longtext.id) from com.trackstudio.model.Message as message where message.task.id=? order by message.time asc",
			id);
		msgs.addAll(list);
		return msgs;
	}

	//инициализируем для TaskRelatedInfo-ов currentTaskUDFCacheItem и workflowUDFCacheItems
	//allowedPrstatuses - эффективные статусы для залогиненного юзера. используются для загрузки прав в UprstatusCacheManager
	private void initUDFs(Collection<TaskRelatedInfo> idColl, Set<String> allowedPrstatuses) throws GranException {
		rwl.writeLock().lock();
		try {
			List<String> tasks = new ArrayList<String>();
			Set<String> udfSet = new HashSet<String>(); //в это множество собираем все используемые udf-ы чтобы разом загрузить в UprstatusCacheManager права на них для allowedPrstatuses
			Set<String> workflowSet = new HashSet<String>();
			//выбираем задачи, для которых не проинициализированы currentTaskUDFCacheItem и workflows для которых есть задачи с непроинициализировынными workflowUDFCacheItems
			for (TaskRelatedInfo tci : idColl) {
				if (!tci.isTaskUDFInitialized()) {
					tasks.add(tci.getId());
				}
				if (!tci.isWorkflowUDFInitialized()) {
					workflowSet.add(tci.getWorkflowId());
				}
			}

			//Инициализируем currentTaskUDFCacheItem
			initCurrentTaskUDFCacheItem(tasks, udfSet);

			//Инициализируем workflowUDFCacheItems
			initWorkflowUDFCache(workflowSet, udfSet, idColl);

			//одним запросом загружаем в UprstatusCacheManager права на udf-ы из udfSet для статусов allowecPrstatuses
			UprstatusCacheManager.getInstance().initUDFPermissions(udfSet, allowedPrstatuses);
		} finally {
			rwl.writeLock().unlock();
		}

	}

	private void initWorkflowUDFCache(Set<String> workflowSet, Set<String> udfSet, Collection<TaskRelatedInfo> idColl) throws GranException {
		if (idColl.isEmpty()) {
			return;
		}
		rwl.writeLock().lock();
		try {
			if (!workflowSet.isEmpty()) {
				Map<String, Collection> params = new LinkedHashMap<String, Collection>();
				params.put("workflowSet", workflowSet);
				List<UDFCacheItem> workflowUdfList = hu.getListMap(
					"select new com.trackstudio.kernel.cache.UDFCacheItem (udf.id, udf.udfsource.id, udf.udfsource.workflow.id, udf.udfsource.task.id, udf.udfsource.user.id, udf.caption, udf.referencedbycaption, udf.type, udf.order, udf.def, udf.initialtask.id, udf.initialuser.id, udf.required, udf.htmlview, udf.script, udf.cachevalues, udf.lookupscript, udf.lookuponly, udf.udfsource.workflow.id) from com.trackstudio.model.Udf as udf where udf.udfsource.workflow.id in (:workflowSet) order by udf.udfsource.workflow.id",
					new LinkedHashMap<String, String>(0),
					params);
				String curWFId = "";
				ArrayList<UDFCacheItem> retUdfList = new ArrayList<UDFCacheItem>();
				Map<String, ArrayList<UDFCacheItem>> workflowUDFMap = new HashMap<String, ArrayList<UDFCacheItem>>();
				//собираем в workflowUDFMap пары (workflowId, коллекция workflowUDFCacheItems)
				for (UDFCacheItem uval : workflowUdfList) {
					udfSet.add(uval.getId());
					if (!curWFId.equals(uval.getSourceId())) {
						if (curWFId.length() > 0) {
							workflowUDFMap.put(curWFId, retUdfList);
						}
						retUdfList = new ArrayList<UDFCacheItem>();
					}
					retUdfList.add(uval);
					curWFId = uval.getSourceId();
				}
				if (curWFId.length() > 0) {
					workflowUDFMap.put(curWFId, retUdfList);
				}

				for (TaskRelatedInfo tci : idColl) {
					if (!tci.isWorkflowUDFInitialized())//�� ������ workflowUDFMap �������������� workflowUDFCacheItems ��� �����. ���� ��� ��������� workflowId � workflowUDFMap ������ ��� - �������������� ArrayList-��
					{
						tci.setWorkflowUDFs(
							workflowUDFMap.get(tci.getWorkflowId()) != null ? workflowUDFMap.get(tci.getWorkflowId()) : new ArrayList<UDFCacheItem>());
					}
				}
			}
		} catch (Exception e) {
			throw new GranException(e);
		} finally {
			rwl.writeLock().unlock();
		}

	}

	/**
	 * Метод заполняет udf поля задачи. только задачи. Работает следующим образом. Сначало получаем весь список udf. в
	 * порядке сортировке по taskId В curTaskId заносим первое значения и собираем список пока он не изменится. для
	 * этого и получаем весь списов в порядке сортировки по taskId Для последний задачи. отдельно заполняем этого поле.
	 *
	 * @param tasks  список всех задач
	 * @param udfSet список для разобора прав. переделается просто ссылка
	 * @throws GranException при необходимости
	 */
	private void initCurrentTaskUDFCacheItem(Collection<String> tasks, Set<String> udfSet) throws GranException {
		log.trace("########");
		rwl.writeLock().lock();
		try {
			if (!tasks.isEmpty()) {
				String curTaskId = "";
				List taskUdfList = hu.getList(
					"select new com.trackstudio.kernel.cache.UDFCacheItem (udf.id, udf.udfsource.id, udf.udfsource.workflow.id, udf.udfsource.task.id, udf.udfsource.user.id, udf.caption, udf.referencedbycaption, udf.type, udf.order, udf.def, udf.initialtask.id, udf.initialuser.id, udf.required, udf.htmlview, udf.script, udf.cachevalues, udf.lookupscript, udf.lookuponly, udf.udfsource.task.id) from com.trackstudio.model.Udf as udf order by udf.udfsource.task.id");
				ArrayList<UDFCacheItem> retUdfList = new ArrayList<UDFCacheItem>();
				for (Object aTaskUdfList : taskUdfList) {
					UDFCacheItem uval = (UDFCacheItem)aTaskUdfList;
					if (curTaskId != null && curTaskId.length() != 0) {
						// Запоминаем udf, потом мы для него будет permissions выставлять
						if (uval.getTaskId() != null || uval.getWorkflowId() != null) {
							udfSet.add(uval.getId());
						} else if (uval.getSourceId() != null) {
							log.error("Udf with missed source found id=" + uval.getSourceId());
						}
						//udfval-ы выбираются отсортированными по таск id. Если taskId меняется, значит нужно собирать currentTaskUDFCacheItem-ы для другого таска.
						if (!curTaskId.equals(uval.getTaskId())) {
							if (curTaskId.length() > 0) {
								// todo UDFCachItem
								findUnlocked(curTaskId).setUDFs(retUdfList);
							}
							retUdfList = new ArrayList<UDFCacheItem>();
						}
						if (uval.getTaskId() != null || uval.getWorkflowId() != null) {
							retUdfList.add(uval);
						} else if (uval.getSourceId() != null) {
							log.error("Udf with missed source found id=" + uval.getSourceId());
						}
					}
					String old_id = curTaskId;
					curTaskId = uval.getTaskId();
					if ((old_id == null || old_id.length() == 0) && curTaskId != null) {
						if (uval.getTaskId() != null || uval.getWorkflowId() != null) {
							retUdfList.add(uval);
						} else if (uval.getSourceId() != null) {
							log.error("Udf with missed source found id=" + uval.getSourceId());
						}
					}

				}
				if (curTaskId != null) {
					//Инициализируем currentTaskUDFCacheItem для последней задачи
					if (curTaskId.length() > 0) {
						findUnlocked(curTaskId).setUDFs(retUdfList);
					}
				}
				//Если остались задачи с непроинициализированными currentTaskUDFCacheItem, то это задачи без доступных udf-ов. Инициализируем ArrayList-ами
				for (String id : tasks) {
					TaskRelatedInfo tci = findUnlocked(id);
					if (!tci.isTaskUDFInitialized()) {
						tci.setUDFs(new ArrayList<UDFCacheItem>());
					}
				}
			}
		} finally {
			rwl.writeLock().unlock();
		}
	}

	/**
	 * Для TaskRelatedInfo-ов, id-шники которых переданы в коллекции инициализируем udfValues <br>Передается коллекция
	 * из SPLIT_SIZE=800 элементов. <br> <cite>winzard 2 all: а что за коллекция Prstatuses?</cite>
	 *
	 * @param idColl
	 * @param allowedPrstatuses
	 * @throws GranException
	 */
	private void initUDFValues(Collection<TaskRelatedInfo> idColl, Set<String> allowedPrstatuses) throws GranException {
		rwl.writeLock().lock();
		try {
			if (idColl.isEmpty()) {
				return;
			}

			// getTasksWithNonInitializedUDFValuesList
			ArrayList<TaskRelatedInfo> tasks1 = new ArrayList<TaskRelatedInfo>();
			for (TaskRelatedInfo tci : idColl) {
				if (!tci.isUDFValuesInitialized()) {
					tasks1.add(tci);
				}
			}
			final List<TaskRelatedInfo> tasks = tasks1;
			if (tasks.isEmpty()) {
				return;
			}
			final Map<String, ArrayList<UdfvalCacheItem>> values = buildMapUdfValue(tasks);
			initUDFs(tasks, allowedPrstatuses);//инициализируем для TaskRelatedInfo-ов UDFs и workflowUDFs
			fullingValueForAllUdfs(tasks, values);
		} finally {
			rwl.writeLock().unlock();
		}

	}

	private Map<String, ArrayList<UdfvalCacheItem>> buildMapUdfValue(final List<TaskRelatedInfo> tasks) throws GranException {
		rwl.readLock().lock();
		try {
			log.trace("########");
			List<Udflist> listValue = hu.getList("from com.trackstudio.model.Udflist");
			Map<String, String> listValueMap1 = new HashMap<String, String>();
			for (Udflist ul : listValue) {
				listValueMap1.put(ul.getId(), ul.getVal());
			}
			Map<String, String> listValueMap = listValueMap1;
			final Map<String, ArrayList<UdfvalCacheItem>> values = new LinkedHashMap<String, ArrayList<UdfvalCacheItem>>(tasks.size());
			for (TaskRelatedInfo task : tasks) {
				values.put(task.getId(), new ArrayList<UdfvalCacheItem>(50));
			}
			//todo ���������� winzard
			List<UdfvalCacheItem> udfvalCacheItems = hu.getList(
				"select new com.trackstudio.kernel.cache.UdfvalCacheItem (udfval.id, udfval.udfsource.task.id, udfval.udf.id, udfval.str, udfval.num, udfval.dat, udfval.udflist.id, udfval.longtext.id, udfval.task.id, udfval.user.id) from com.trackstudio.model.Udfval as udfval");
			for (UdfvalCacheItem udfvalCacheItem : udfvalCacheItems) {
				if (udfvalCacheItem.getUdflistId() != null) {
					udfvalCacheItem.setUdflistVal(listValueMap.get(udfvalCacheItem.getUdflistId()));
				}
				ArrayList<UdfvalCacheItem> vals = values.get(udfvalCacheItem.getSourceId());
				if (vals != null) {
					vals.add(udfvalCacheItem);
				}
			}
			return values;
		} finally {
			rwl.readLock().unlock();
		}

	}

	/**
	 * This method is fulling value for all udfs
	 *
	 * @param tasks  which tasks
	 * @param values udfs values
	 * @throws GranException for necessery
	 */
	private void fullingValueForAllUdfs(final List<TaskRelatedInfo> tasks, final Map<String, ArrayList<UdfvalCacheItem>> values) throws GranException {
		rwl.writeLock().lock();
		try {
			for (TaskRelatedInfo tci : tasks) {
				List<UDFCacheItem> udfs = tci.getUDFCacheItemsWithoutCache();
				List<UdfvalCacheItem> vals = values.get(tci.getId());

				List<UdfValue> retUdfvalList = new ArrayList<UdfValue>(); // тут храним UdfValue с установленными значениями (если есть)
				for (UDFCacheItem udfCacheItem : udfs) {
					UdfValue udfValue = new UdfValue(udfCacheItem);
					int type = udfCacheItem.getType();

					// ищем в hashmap значение для текущей задачи и udf
					for (UdfvalCacheItem uvci : vals) {
						if (uvci.getUdfId().equals(udfCacheItem.getId())) {
							udfValue.setValue(uvci);
							if (ONLY_ONE_VALUE.contains(type)) {
								break; // поля этих типов имеют максимум одно значение, дальше продолжать нет смысла.
							}
						}
					}
					retUdfvalList.add(udfValue);
				}
				tci.setUDFValues(retUdfvalList);  // указываем задаче ее список UdfValue
			}
			log.info("Udf init finished");
		} finally {
			rwl.writeLock().unlock();
		}

	}

	/**
	 * Очищает кеш для типа поля
	 *
	 * @param taskId ID задачи
	 * @throws GranException при необходимости
	 */
	public void invalidateTaskTypeUDF(String taskId) throws GranException {
		rwl.writeLock().lock();
		try {
			for (List<String> relatedTaskIds : KernelManager.getIndex().getReferencedTasksForTask(taskId).values()) {
				for (String relatedTaskId : relatedTaskIds) {
					TaskRelatedInfo tci = cache.get(new TaskRelatedInfo(relatedTaskId));
					if (tci != null) {
						tci.invalidateUDFValues();
						KernelManager.getIndex().reIndexTask(tci);
					} else {
						KernelManager.getIndex().deleteTask(relatedTaskId);
					}
				}
			}
		} finally {
			rwl.writeLock().unlock();
		}
	}

	/**
	 * Очищает кеш пользовательских полей при изменении списка знапчений
	 *
	 * @param udfId  ID поля
	 * @param value  значение
	 * @param listId ID списка
	 * @throws GranException при необходимости
	 */
	public void invalidateWFUDFWhenChangeList(String udfId, String value, String listId) throws GranException {
		rwl.writeLock().lock();
		try {
			for (TaskRelatedInfo tci : cache.getContents()) {
				tci.invalidateWFUDFWhenChangeList(udfId, value, listId);
			}
		} finally {
			rwl.writeLock().unlock();
		}

	}

	/**
	 * Очищает кеш пользовательских полей для процесса
	 *
	 * @param wfId  ID процесса
	 * @param udfId ID поля
	 * @throws GranException при необходиомсти
	 */
	public void invalidateWFUDFs(String wfId, String udfId) throws GranException {
		rwl.writeLock().lock();
		try {
			UDFCacheItem uci = KernelManager.getUdf().getUDFCacheItem(udfId);
			for (TaskRelatedInfo tci : cache.getContents()) {
				if (tci.getWorkflowId().equals(wfId)) {
					tci.invalidateWFUDF(udfId, uci);
				}
			}
		} finally {
			rwl.writeLock().unlock();
		}

	}

	/**
	 * Очищает кеш полей при изменении категории
	 *
	 * @param wfId ID категории
	 * @throws GranException при необходимости
	 */
	public void invalidateWFUDWhenChangeCategory(String wfId) throws GranException {
		rwl.writeLock().lock();
		try {
			for (TaskRelatedInfo tci : cache.getContents()) {
				if (tci != null && tci.getWorkflowId().equals(wfId)) {
					tci.invalidateUDF();
					if (tci != null) {
						KernelManager.getIndex().reIndexTask(tci);
					}
				}
			}
		} finally {
			rwl.writeLock().unlock();
		}

	}

	/**
	 * Очищает кеш для пользовательского поля
	 *
	 * @param id ID задачи
	 * @throws GranException при необходимости
	 */
	public void invalidateUDF(String id) throws GranException {
		rwl.writeLock().lock();
		try {
			TaskRelatedInfo tci = findUnlocked(id);
			if (tci != null) {
				tci.invalidateUDFValues();
			}
		} finally {
			rwl.writeLock().unlock();
		}

	}

	/**
	 * Очищает кеш пользовательского поля при изменении списка значений
	 *
	 * @param udfId    ID поля
	 * @param newValue новое значение
	 * @param listId   ID списка
	 * @throws GranException при необходимости
	 */
	public void invalidateUDFWhenChangeList(String udfId, String newValue, String listId) throws GranException {
		rwl.writeLock().lock();
		try {
			for (TaskRelatedInfo tci : cache.getContents()) {
				tci.invalidateUDFWhenChangeList(udfId, newValue, listId);
			}
		} finally {
			rwl.writeLock().unlock();
		}

	}

	/**
	 * Очищает кеш пользовательского поля при изменении
	 *
	 * @param task  ID задачи
	 * @param udfId ID поля
	 * @throws GranException при необходимости
	 */
	public void invalidateUDFs(String task, String udfId) throws GranException {
		rwl.writeLock().lock();
		try {
			UDFCacheItem uci = KernelManager.getUdf().getUDFCacheItem(udfId);
			for (TaskRelatedInfo tci : cache.getContents()) {
				// Проверяет является ли указанные задачи id дочерней по отношении к fromid
				if (getTaskRelatedInfoChain(task, tci.getId()) != null) {
					tci.invalidateUDF(udfId, uci);
				}
			}
			findUnlocked(task).invalidateUDF();
		} finally {
			rwl.writeLock().unlock();
		}

	}

	/**
	 * Обновляет кеш при ищменении задачи
	 *
	 * @param taskId ID задачи
	 * @throws GranException при необходимости
	 */
	public void invalidateTask(String taskId) throws GranException {
		rwl.writeLock().lock();
		try {
			TaskRelatedInfo n = loadItem(taskId);
			TaskRelatedInfo old = cache.get(n);
			int oldchildcount = old.getChildCount();
			List<TaskRelatedInfo> parents = cache.getAncestors(n);
			List<TaskRelatedInfo> children = cache.getChildren(n);
			int childcount = 0;
			Calendar date = n.getUpdatedate();
			Long budget = n.getAbudget();
			for (TaskRelatedInfo related : children) {
				childcount += related.getChildCount() + 1;
				if (date == null || related.getUpdatedate() != null && date.getTimeInMillis() < related.getUpdatedate().getTimeInMillis()) {
					date = related.getUpdatedate();
				}
				budget += related.getActualBudget();
			}
			n.setActualBudget(budget);
			if (date != null) {
				n.setUpdatedate(date);
			}
			n.setChildCount(childcount);
			n.setAcl(old.getAcl());
			cache.replaceWith(old, n);
			for (TaskRelatedInfo oper : parents) {
				oper.updateUpdateDate(n.getUpdatedate());
				oper.setActualBudget(oper.getActualBudget() - old.getAbudget() + n.getAbudget());
				oper.setChildCount(oper.getChildCount() - oldchildcount + n.getChildCount());
			}
			TaskRelatedInfo tri = findUnlocked(taskId);
			KernelManager.getIndex().reIndexTask(tri);
		} finally {
			rwl.writeLock().unlock();
		}

	}

	/**
	 * Удаляет задачу из кеша при ее удалении
	 *
	 * @param id ID задачи
	 * @throws GranException при необходимости
	 */
	public void invalidateWhenRemove(String id) throws GranException {
		rwl.writeLock().lock();
		try {
			TaskRelatedInfo o = new TaskRelatedInfo(id);
			List<TaskRelatedInfo> parents = cache.getAncestors(o);
			TaskRelatedInfo old = cache.get(o);
			for (TaskRelatedInfo oper : parents) {
				oper.updateUpdateDate(old.getUpdatedate());
				oper.setActualBudget(oper.getActualBudget() - old.getAbudget());
				oper.setChildCount(oper.getChildCount() - old.getChildCount() - 1);
			}
			cache.remove(o);
			KernelManager.getIndex().deleteTask(id);
			taskNumbers.remove(o.getNumberInt());
		} finally {
			rwl.writeLock().unlock();
		}

	}

	/**
	 * Обновляет кеш при изменении категории
	 *
	 * @throws GranException при необходимости
	 */
	public void invalidateWhenChangeWorkflow() throws GranException {
		rwl.writeLock().lock();
		try {
			for (TaskRelatedInfo tci : cache.getContents()) {
				if (tci != null) {
					tci.invalidateWorkflowUDF();
				}
			}
		} finally {
			rwl.writeLock().unlock();
		}

	}

	/**
	 * Обновляет кеш при переносе задачи
	 *
	 * @param taskId ID задачи
	 * @param from   откуда перемещает
	 * @param to     куда перемещает
	 * @throws GranException при необходимости
	 */
	public void invalidateWhenMove(String taskId, String from, String to) throws GranException {
		rwl.writeLock().lock();
		try {
			invalidateTask(taskId);
			TaskRelatedInfo object = findUnlocked(taskId);
			cache.moveTree(object, findUnlocked(to));
			updateInsert(object);
			invalidateTask(from);
			invalidateTask(to);
			TaskRelatedInfo tri = findUnlocked(taskId);
			KernelManager.getIndex().reIndexTask(tri);
		} finally {
			rwl.writeLock().unlock();
		}

	}

	/**
	 * Обновляет кеш при добавлении задачи
	 *
	 * @param id ID задачи
	 * @throws GranException при необходимости
	 */

	public void invalidateWhenInsert(String id) throws GranException {
		rwl.writeLock().lock();
		try {
			//  на этапе добавления задачи ACL для нее еще не выставили
			TaskRelatedInfo tci = loadItem(id);
			// добавляем в индекс поиска по имени
			add(tci.getParentId(), tci);
			TaskRelatedInfo tri = findUnlocked(id);
			KernelManager.getIndex().reIndexTask(tri);
		} finally {
			rwl.writeLock().unlock();
		}

	}

	public List<TaskRelatedInfo> getChildren(TaskRelatedInfo tri) {
		rwl.readLock().lock();
		try {
			return cache.getChildren(tri);
		} finally {
			rwl.readLock().unlock();
		}

	}

	/**
	 * Возвращает сообщение по еге ID
	 *
	 * @param id ID сообщения
	 * @return сообщение
	 * @throws GranException при необходимости
	 */
	public static MessageCacheItem findMessage(String id) throws GranException {
		MessageCacheItem vObj = null;
		for (Object o : hu.getList(
			"select new com.trackstudio.kernel.cache.MessageCacheItem(" +
					"message.id, message.description, message.time, message.hrs, message.deadline, message.budget, " +
					"message.task.id, message.submitter.id, message.resolution.id, message.priority.id, message.handler.id, " +
					"message.handler.user.id, message.handler.prstatus.id, message.mstatus.id, message.longtext.id) " +
					"from com.trackstudio.model.Message as message where message.id=?",
			id)) {
			vObj = (MessageCacheItem) o;
		}
		return vObj;
	}

	/**
	 * Возвращает удален приложенный файл или нет
	 *
	 * @param id ID приложенного файла
	 * @return TRUE - удален, FALSE - нет
	 * @throws GranException при необходимости
	 */
	public Boolean getAttachmentIsDeleted(String id) throws GranException {
		return attachmentsIsDeleted.get(id);
	}

	/**
	 * Устанавливает удален приложенный файл или нет
	 *
	 * @param id       ID приложенного файла
	 * @param isDelete TRUE - удален, FALSE - нет
	 * @throws GranException при необходимости
	 */
	public void setAttachmentIsDeleted(String id, Boolean isDelete) throws GranException {
		attachmentsIsDeleted.put(id, isDelete);
	}

	/**
	 * Очищает кеш "удален аттач или нет" для конкретного приложенного файла
	 *
	 * @param id ID приложенного файла
	 * @throws GranException при необходимости
	 */
	public void invalidateAttachmentIsDeleted(String id) throws GranException {
		attachmentsIsDeleted.remove(id);
	}

	/**
	 * Возвращает список значений пользовательских полей
	 *
	 * @return список значение пользовательских полей
	 * @throws GranException при необзодимости
	 * @see com.trackstudio.app.UdfValue
	 */
	public List<UdfValue> getUDFValues(String id) throws GranException {
		rwl.readLock().lock();
		try {
			TaskRelatedInfo task = findUnlocked(id);
			return task.getUDFValues();
		} finally {
			rwl.readLock().unlock();
		}
	}

	public TaskRelatedInfo findByName(String name)
	{
		if (name==null)
			return null;

		rwl.readLock().lock();
		try {

			Optional<TaskRelatedInfo> result = cache.findAny(x -> x!=null && x.getName()!=null && x.getName().equals(name));
			if (result.isPresent()) {
				return result.get();
			} else {
				return null;
			}

		} finally {
			rwl.readLock().unlock();
		}
	}

	public TaskRelatedInfo findByShortName(String shortName)
	{
		if (shortName==null)
			return null;

		rwl.readLock().lock();
		try {

			Optional<TaskRelatedInfo> result = cache.findAny(x -> x!=null && x.getShortname()!=null && x.getShortname().equals(shortName));
			if (result.isPresent()) {
				return result.get();
			} else {
				return null;
			}

		} finally {
			rwl.readLock().unlock();
		}
	}

	public TaskRelatedInfo findByNumber(String number)
	{
		if (number==null)
			return null;

		rwl.readLock().lock();
		try {
			final int num;
			if (!number.matches("\\d+"))
				return null;

			if (number != null && number.startsWith("#")) {
				num = Integer.valueOf(number.substring(1));
			} else {
				num = Integer.valueOf(number);
			}

			String taskId = taskNumbers.get(num);
			if (taskId != null) {
				return find(taskId);
			} else {
				return null; // not found
			}
		} catch (NumberFormatException ex) {
			// this occurs if search string too large (34534535353534534534) and doesn't like Integer.valueOf
			return null;
		} finally {
			rwl.readLock().unlock();
		}
	}

}
