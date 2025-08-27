package com.trackstudio.securedkernel;

import java.util.ArrayList;
import java.util.List;
import java.util.TreeSet;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.trackstudio.app.adapter.AdapterManager;
import com.trackstudio.app.csv.CSVImport;
import com.trackstudio.app.session.SessionContext;
import com.trackstudio.constants.WorkflowConstants;
import com.trackstudio.exception.AccessDeniedException;
import com.trackstudio.exception.GranException;
import com.trackstudio.exception.InvalidParameterException;
import com.trackstudio.kernel.cache.Action;
import com.trackstudio.kernel.cache.TaskRelatedInfo;
import com.trackstudio.kernel.lock.LockManager;
import com.trackstudio.kernel.manager.KernelManager;
import com.trackstudio.kernel.manager.SafeString;
import com.trackstudio.secured.SecuredCategoryBean;
import com.trackstudio.secured.SecuredMstatusBean;
import com.trackstudio.secured.SecuredPriorityBean;
import com.trackstudio.secured.SecuredPrstatusBean;
import com.trackstudio.secured.SecuredResolutionBean;
import com.trackstudio.secured.SecuredStatusBean;
import com.trackstudio.secured.SecuredTransitionBean;
import com.trackstudio.secured.SecuredWorkflowBean;
import com.trackstudio.tools.EggBasket;
import com.trackstudio.tools.ExternalAdapterManagerUtil;
import com.trackstudio.tools.ParameterValidator;
import com.trackstudio.tools.SecuredBeanUtil;

import net.jcip.annotations.Immutable;

/**
 * Класс SecuredWorkflowAdapterManager содержит методы для работы с процессами
 */
@Immutable
public class SecuredWorkflowAdapterManager {

	private static final Log log = LogFactory.getLog(SecuredWorkflowAdapterManager.class);
	private static final ParameterValidator pv = new ParameterValidator();
	private static final LockManager lockManager = LockManager.getInstance();
	/**
	 * Возвращает список резолюций для типа сообщения
	 *
	 * @param sc        сессия пользователя
	 * @param mstatusId ID типа сообщения
	 * @return список резолюций
	 * @throws GranException при необходимости
	 * @see com.trackstudio.model.Resolution
	 */
	public ArrayList<SecuredResolutionBean> getResolutionList(SessionContext sc, String mstatusId) throws GranException {
		boolean w = lockManager.acquireConnection(SecuredWorkflowAdapterManager.class.getSimpleName());
		try {
			log.trace("getAvailableResolutionList");
			if (sc == null)
				throw new InvalidParameterException(this.getClass(), "getAvailableResolutionList", "sc", sc);
			if (mstatusId == null)
				throw new InvalidParameterException(this.getClass(), "getAvailableResolutionList", "mstatusId", sc);
			SecuredMstatusBean mstatus = AdapterManager.getInstance().getSecuredFindAdapterManager().findMstatusById(sc, mstatusId);
			if (!mstatus.canView())
				throw new AccessDeniedException(this.getClass(), "getAvailableResolutionList", sc, "!mstatus.canView()", mstatusId);
			return SecuredBeanUtil.toArrayList(sc, KernelManager.getWorkflow().getResolutionList(mstatusId), SecuredBeanUtil.RESOLUTION);
		} finally {
			if (w) lockManager.releaseConnection(SecuredWorkflowAdapterManager.class.getSimpleName());
		}
	}

	/**
	 * Возвращает список состояний для процесса
	 *
	 * @param sc         сессия пользователя
	 * @param workflowId ID процесса, для которого возвращается список состояний
	 * @return список состояний
	 * @throws GranException при необходимости
	 * @see com.trackstudio.model.Status
	 */
	public ArrayList<SecuredStatusBean> getStateList(SessionContext sc, String workflowId) throws GranException {
		log.trace("getStateList");
		if (sc == null)
			throw new InvalidParameterException(this.getClass(), "getStateList", "sc", sc);
		if (workflowId == null)
			throw new InvalidParameterException(this.getClass(), "getStateList", "workflowId", sc);
//        SecuredWorkflowBean workflow = AdapterManager.getInstance().getSecuredFindAdapterManager().findWorkflowById(sc, workflowId);
        /*if (!(sc.canAction(Action.viewState, workflow.getTaskId()) && workflow.canView()))
            throw new AccessDeniedException(this.getClass(), "getStateList", sc);*/
		return SecuredBeanUtil.toArrayList(sc, KernelManager.getWorkflow().getStateList(workflowId), SecuredBeanUtil.STATUS);
	}

	/**
	 * Создается состояние
	 *
	 * @param sc         сессия пользователя
	 * @param name       Название состояния
	 * @param isStart    Является ли состояние начальным
	 * @param isFinish   Является ли состояние конечным
	 * @param workflowId ID процесса, для которого создается состояние
	 * @param color      Цвет состояния
	 * @return ID созданного состояния
	 * @throws GranException при необходимости
	 */
	public String createState(SessionContext sc, String name, boolean isStart, boolean isFinish, String workflowId, String color) throws GranException {
		log.trace("createState");
		if (sc == null)
			throw new InvalidParameterException(this.getClass(), "createState", "sc", sc);
		if (name == null || pv.badSmallDesc(name))
			throw new InvalidParameterException(this.getClass(), "createState", "name", sc);
		if (workflowId == null)
			throw new InvalidParameterException(this.getClass(), "createState", "workflowId", sc);
		if (color == null || pv.badSmallDesc(color))
			throw new InvalidParameterException(this.getClass(), "createState", "color", sc);
		SecuredWorkflowBean workflow = AdapterManager.getInstance().getSecuredFindAdapterManager().findWorkflowById(sc, workflowId);
		if (!sc.canAction(Action.manageWorkflows, workflow.getTaskId()))
			throw new AccessDeniedException(this.getClass(), "createState", sc, "!sc.canAction(Action.createState, workflow.getTaskId())", workflowId);
		if (!workflow.canManage())
			throw new AccessDeniedException(this.getClass(), "createState", sc, "!workflow.canUpdate()", workflowId);
		return KernelManager.getWorkflow().createState(SafeString.createSafeString(name), isStart, isFinish, workflowId, color);
	}

	/**
	 * Создается тип сообщения
	 *
	 * @param sc          сессия пользователя
	 * @param workflowId  ID процесса, для которого создается тип сообщения
	 * @param name        Название типа сообщения
	 * @param description Описание типа сообщения
	 * @return ID созданного типа сообщения
	 * @throws GranException при необходимости
	 */
	public String createMstatus(SessionContext sc, String workflowId, String name, String description, String preferences) throws GranException {
		log.trace("createMstatus");
		if (sc == null)
			throw new InvalidParameterException(this.getClass().getName(), "createMstatus", "sc", "1");
		if (workflowId == null)
			throw new InvalidParameterException(this.getClass(), "createMstatus", "workflowId", sc);
		if (name == null || pv.badSmallDesc(name))
			throw new InvalidParameterException(this.getClass(), "createMstatus", "name", sc);
		//todo tdc108 #16412
//        if (description == null || pv.badSmallDesc(description))
//            throw new InvalidParameterException(this.getClass(), "createMstatus", "description", sc);
		SecuredWorkflowBean workflow = AdapterManager.getInstance().getSecuredFindAdapterManager().findWorkflowById(sc, workflowId);
		if (!sc.canAction(Action.manageWorkflows, workflow.getTaskId()))
			throw new AccessDeniedException(this.getClass(), "createMstatus", sc, "!sc.canAction(Action.createMessageType, workflow.getTaskId())", workflowId);
		if (!workflow.canManage())
			throw new AccessDeniedException(this.getClass(), "createMstatus", sc, "!workflow.canUpdate()", workflowId);
		String id = KernelManager.getWorkflow().createMstatus(workflowId, SafeString.createSafeString(name), SafeString.createSafeString(description), preferences);
//        HibernateSession.closeSession();
		for (SecuredPrstatusBean prsI : new TreeSet<SecuredPrstatusBean>(AdapterManager.getInstance().getSecuredPrstatusAdapterManager().getAvailablePrstatusList(sc, sc.getUserId()))) {
			AdapterManager.getInstance().getSecuredWorkflowAdapterManager().grantView(sc, WorkflowConstants.VIEW_ALL, prsI.getId(), id);
			AdapterManager.getInstance().getSecuredWorkflowAdapterManager().grantProcess(sc, WorkflowConstants.PROCESS_ALL, prsI.getId(), id);
			AdapterManager.getInstance().getSecuredWorkflowAdapterManager().grantBeHandler(sc, WorkflowConstants.BE_HANDLER_ALL, prsI.getId(), id);
		}
		return id;
	}

	/**
	 * Возвращает список типов сообщения для процесса
	 *
	 * @param sc         сессия пользователя
	 * @param workflowId ID процесса, для которого возвращается список приоритетов
	 * @return список приоритетов
	 * @throws GranException при необходимости
	 * @see com.trackstudio.model.Mstatus
	 */
	public ArrayList<SecuredMstatusBean> getMstatusList(SessionContext sc, String workflowId) throws GranException {
		boolean w = lockManager.acquireConnection(SecuredWorkflowAdapterManager.class.getSimpleName());
		try {
			log.trace("getMstatusList");
			if (sc == null)
				throw new InvalidParameterException(this.getClass().getName(), "getMstatusList", "sc", "1");
			if (workflowId == null)
				throw new InvalidParameterException(this.getClass(), "getMstatusList", "workflowId", sc);
			//SecuredWorkflowBean workflow = AdapterManager.getInstance().getSecuredFindAdapterManager().findWorkflowById(sc, workflowId);
            /* todo vmv 2 tdc тут нельзя так проверять. См. где вызывается
        if (!(sc.canAction(Action.viewMessageType, workflow.getTaskId()) && workflow.canView()))
            throw new AccessDeniedException(this.getClass(), "getMstatusList", sc);
            */
			return SecuredBeanUtil.toArrayList(sc, KernelManager.getWorkflow().getMstatusList(workflowId), SecuredBeanUtil.MSTATUS);
		} finally {
			if (w) lockManager.releaseConnection(SecuredWorkflowAdapterManager.class.getSimpleName());
		}
	}

	public boolean isAuditAvailable(SessionContext sc, String workflowId) throws GranException {
		boolean w = lockManager.acquireConnection(SecuredWorkflowAdapterManager.class.getSimpleName());
		try {
			boolean result = false;
			for (SecuredMstatusBean mstatus : this.getMstatusList(sc, workflowId)) {
				if (CSVImport.LOG_MESSAGE.equals(mstatus.getName())) {
					EggBasket<String, String> pref = ExternalAdapterManagerUtil.getMprstatusMap(mstatus.getId());
					if (!pref.keySet().isEmpty()) {
						result = true;
						break;
					}
				}
			}
			return result;
		} finally {
			if (w) lockManager.releaseConnection(SecuredWorkflowAdapterManager.class.getSimpleName());
		}
	}

	/**
	 * Возвращает список переходов для типа сообщения
	 *
	 * @param sc        сессия пользователя
	 * @param mstatusId ID типа сообщения, для которого создается переход
	 * @return список переходов
	 * @throws GranException при необходимости
	 */
	public ArrayList<SecuredTransitionBean> getTransitionList(SessionContext sc, String mstatusId) throws GranException {
		log.trace("getTransitionList");
		if (sc == null)
			throw new InvalidParameterException(this.getClass().getName(), "getTransitionList", "sc", "1");
		if (mstatusId == null)
			throw new InvalidParameterException(this.getClass(), "getTransitionList", "mstatusId", sc);
		return SecuredBeanUtil.toArrayList(sc, KernelManager.getWorkflow().getTransitionList(mstatusId), SecuredBeanUtil.TRANSITION);
	}

	/**
	 * Возвращает список приоритетов для процесса
	 *
	 * @param sc         сессия пользователя
	 * @param workflowId ID процесса, для которого возвращается список приоритетов
	 * @return список приоритетов
	 * @throws GranException при необходимости
	 * @see com.trackstudio.model.Priority
	 */
	public ArrayList<SecuredPriorityBean> getPriorityList(SessionContext sc, String workflowId) throws GranException {
		log.trace("getPriorityList");
		if (sc == null)
			throw new InvalidParameterException(this.getClass(), "getPriorityList", "sc", sc);
		if (workflowId == null)
			throw new InvalidParameterException(this.getClass(), "getPriorityList", "workflowId", sc);
		SecuredWorkflowBean workflow = AdapterManager.getInstance().getSecuredFindAdapterManager().findWorkflowById(sc, workflowId);
		if (!workflow.canView())
			throw new AccessDeniedException(this.getClass(), "getPriorityList", sc, "!(workflow.canView())", workflowId);
		boolean w = lockManager.acquireConnection(SecuredWorkflowAdapterManager.class.getSimpleName());
		try {
			return SecuredBeanUtil.toArrayList(sc, KernelManager.getWorkflow().getPriorityList(workflowId), SecuredBeanUtil.PRIORITY);
		} finally {
			if (w) lockManager.releaseConnection(SecuredWorkflowAdapterManager.class.getSimpleName());
		}
	}

	/**
	 * Возвращает список доступных процессов для задачи
	 *
	 * @param sc     сессия пользователя
	 * @param taskId ID задачи, для которой возвращается список процессов
	 * @return список процессов
	 * @throws GranException при необходимости
	 * @see com.trackstudio.model.Workflow
	 */
	public ArrayList<SecuredWorkflowBean> getAvailableWorkflowList(SessionContext sc, String taskId) throws GranException {
		log.trace("getAvailableWorkflowList");
		if (sc == null)
			throw new InvalidParameterException(this.getClass(), "getAvailableWorkflowList", "sc", sc);
		if (taskId == null)
			throw new InvalidParameterException(this.getClass(), "getAvailableWorkflowList", "taskId", sc);
        /* todo vmv 2 tdc тут нельзя так проверять. См. где вызывается
        if (!((sc.canAction(Action.viewWorkflowList, taskId) || sc.canAction(Action.viewCategoryList, taskId)) && sc.taskOnSight(taskId)))
            throw new AccessDeniedException(this.getClass(), "getAvailableWorkflowList", sc);
            */
		return SecuredBeanUtil.toArrayList(sc, KernelManager.getWorkflow().getAvailableWorkflowList(taskId), SecuredBeanUtil.WORKFLOW);
	}

	/**
	 * Возвращает список всех доступных процессов для задачи
	 *
	 * @param sc     сессия пользователя
	 * @param taskId ID задачи, для которой возвращается список процессов
	 * @return список процессов
	 * @throws GranException при необходимости
	 * @see com.trackstudio.model.Workflow
	 */
	public ArrayList<SecuredWorkflowBean> getAllAvailableWorkflowList(SessionContext sc, String taskId) throws GranException {
		log.trace("getAvailableWorkflowList");
		if (sc == null)
			throw new InvalidParameterException(this.getClass(), "getAvailableWorkflowList", "sc", sc);
		if (taskId == null)
			throw new InvalidParameterException(this.getClass(), "getAvailableWorkflowList", "taskId", sc);
        /* todo vmv 2 tdc тут нельзя так проверять. См. где вызывается
    if (!((sc.canAction(Action.viewWorkflowList, taskId) || sc.canAction(Action.viewCategoryList, taskId)) && sc.taskOnSight(taskId)))
        throw new AccessDeniedException(this.getClass(), "getAvailableWorkflowList", sc);
        */
		return SecuredBeanUtil.toArrayList(sc, KernelManager.getWorkflow().getAllAvailableWorkflowList(taskId), SecuredBeanUtil.WORKFLOW);
	}

	/**
	 * Возвращает список категорий для процесса
	 *
	 * @param sc         сессия пользователя
	 * @param workflowId ID процесса, для которого возвращается список категшорий
	 * @return список категорий
	 * @throws GranException при необходимости
	 * @see com.trackstudio.model.Category
	 */
	public ArrayList<SecuredCategoryBean> getCategoryList(SessionContext sc, String workflowId) throws GranException {
		boolean w = lockManager.acquireConnection(SecuredWorkflowAdapterManager.class.getSimpleName());
		try {
			log.trace("getCategoryList");
			if (sc == null)
				throw new InvalidParameterException(this.getClass(), "getCategoryList", "sc", sc);
			if (workflowId == null)
				throw new InvalidParameterException(this.getClass(), "getCategoryList", "taskId", sc);
			return SecuredBeanUtil.toArrayList(sc, KernelManager.getWorkflow().getCategoryList(workflowId), SecuredBeanUtil.CATEGORY);
		} finally {
			if (w) lockManager.releaseConnection(SecuredWorkflowAdapterManager.class.getSimpleName());
		}
	}

	/**
	 * Возвращает список доступных процессов для статуса
	 *
	 * @param sc       сессия пользователя
	 * @param statusId ID статуса
	 * @return список процессов
	 * @throws GranException при необходимости
	 */
	public ArrayList<SecuredWorkflowBean> getAllWorkflowListForUser(SessionContext sc, String statusId) throws GranException {
		log.trace("getAvailableWorkflowList");
		if (sc == null)
			throw new InvalidParameterException(this.getClass(), "getAvailableWorkflowList", "sc", sc);
		if (statusId == null)
			throw new InvalidParameterException(this.getClass(), "getAvailableWorkflowList", "statusId", sc);

		ArrayList<SecuredWorkflowBean> list = SecuredBeanUtil.toArrayList(sc, KernelManager.getWorkflow().getAllWorkflowList(), SecuredBeanUtil.WORKFLOW);
		ArrayList<SecuredWorkflowBean> ret = new ArrayList<SecuredWorkflowBean>();
		SecuredPrstatusBean pr = AdapterManager.getInstance().getSecuredFindAdapterManager().findPrstatusById(sc, statusId);
		if (pr == null)
			throw new InvalidParameterException(this.getClass().getName(), "getAvailableWorkflowList", "statusId", sc.getUserId());
		ArrayList<SecuredPrstatusBean> availabePrs = AdapterManager.getInstance().getSecuredPrstatusAdapterManager().getAvailablePrstatusList(sc, sc.getUserId());
		// Check if we can edit current prstatus
		boolean canEdit = false;
		for (SecuredPrstatusBean spb : availabePrs) {
			if (spb.getId().equals(statusId)) {
				canEdit = true;
				break;
			}
		}
		for (SecuredWorkflowBean scb : list) {
			if (scb.getTaskId() == null) continue;
			if (canEdit) {
				if (sc.taskOnSight(scb.getTaskId()))
					ret.add(scb);
			} else {
				if (scb.canManage())
					ret.add(scb);
			}
		}
		return ret;
	}

	/**
	 * Создается новый процесс
	 *
	 * @param sc     сессия пользователя
	 * @param taskId ID задачи, для которой создается новый процесс
	 * @param name   Название процесса
	 * @return ID созданного процесса
	 * @throws GranException при необходимости
	 */
	public String createWorkflow(SessionContext sc, String taskId, String name) throws GranException {
		log.trace("createWorkflow");
		if (sc == null)
			throw new InvalidParameterException(this.getClass(), "createWorkflow", "sc", sc);
		if (taskId == null)
			throw new InvalidParameterException(this.getClass(), "createWorkflow", "taskId", sc);
		if (name == null || pv.badSmallDesc(name))
			throw new InvalidParameterException(this.getClass(), "createWorkflow", "name", sc);
		//if (!(sc.canAction(Action.createWorkflow, taskId) && sc.allowedByACL(taskId)))
		//    throw new AccessDeniedException(this.getClass(), "createWorkflow", sc);
		if (!sc.canAction(Action.manageWorkflows, taskId))
			throw new AccessDeniedException(this.getClass(), "createWorkflow", sc, "!sc.canAction(Action.createWorkflow, taskId)", taskId);
		if (!sc.allowedByACL(taskId))
			throw new AccessDeniedException(this.getClass(), "createWorkflow", sc, "!sc.allowedByACL(taskId)", taskId);
		return KernelManager.getWorkflow().createWorkflow(taskId, SafeString.createSafeString(name));
	}

	/**
	 * Создает копию процесса (клонирует его) со всеми состояниями, приоритетами, типа сообщений, резолючиями и т.д.
	 *
	 * @param sc         сессия пользователя
	 * @param workflowId ID копируемого процесса
	 * @param taskId     ID задачи
	 * @return ID созаднного процесса
	 * @throws GranException при необходимости
	 */
	public String cloneWorkflow(SessionContext sc, String workflowId, String taskId) throws GranException {
		log.trace("cloneWorkflow");
		if (sc == null)
			throw new InvalidParameterException(this.getClass(), "cloneWorkflow", "sc", sc);
		if (workflowId == null)
			throw new InvalidParameterException(this.getClass(), "cloneWorkflow", "workflowId", sc);
		if (taskId == null)
			throw new InvalidParameterException(this.getClass(), "cloneWorkflow", "taskId", sc);
		SecuredWorkflowBean workflow = AdapterManager.getInstance().getSecuredFindAdapterManager().findWorkflowById(sc, workflowId);
		//if (!(sc.canAction(Action.copyWorkflow, taskId) && sc.allowedByACL(taskId) && workflow.canView()))
		//    throw new AccessDeniedException(this.getClass(), "cloneWorkflow", sc);
		if (!sc.canAction(Action.manageWorkflows, taskId))
			throw new AccessDeniedException(this.getClass(), "cloneWorkflow", sc, "!sc.canAction(Action.copyWorkflow, taskId)", taskId);
		if (!sc.allowedByACL(taskId))
			throw new AccessDeniedException(this.getClass(), "cloneWorkflow", sc, "!sc.allowedByACL(taskId)", taskId);
		if (!workflow.canView())
			throw new AccessDeniedException(this.getClass(), "cloneWorkflow", sc, "!workflow.canView()", workflowId);
		return KernelManager.getWorkflow().cloneWorkflow(workflowId, taskId, sc.getLocale());
	}

	/**
	 * Создается резолюция
	 *
	 * @param sc        сессия пользователя
	 * @param mstatusId ID типа сообщения, для которого создается резолюция
	 * @param name      Название резолюции
	 * @param isdefault По умолчанию ли резолюция
	 * @return ID созданной резолюции
	 * @throws GranException при необходимости
	 */
	public String createResolution(SessionContext sc, String mstatusId, String name, boolean isdefault) throws GranException {
		log.trace("createResolution");
		if (sc == null)
			throw new InvalidParameterException(this.getClass(), "createResolution", "sc", sc);
		if (name == null || pv.badSmallDesc(name))
			throw new InvalidParameterException(this.getClass(), "createResolution", "name", sc);
		SecuredMstatusBean mstatus = AdapterManager.getInstance().getSecuredFindAdapterManager().findMstatusById(sc, mstatusId);
		//if (!(sc.canAction(Action.editResolution, mstatus.getWorkflow().getTaskId()) && mstatus.canUpdate()))
		//    throw new AccessDeniedException(this.getClass(), "createResolution", sc);
		if (!sc.canAction(Action.manageWorkflows, mstatus.getWorkflow().getTaskId()))
			throw new AccessDeniedException(this.getClass(), "createResolution", sc, "!sc.canAction(Action.editResolution, mstatus.getWorkflow().getTaskId())", mstatusId);
		if (!mstatus.canUpdate())
			throw new AccessDeniedException(this.getClass(), "createResolution", sc, "!mstatus.canUpdate()", mstatusId);
		return KernelManager.getWorkflow().createResolution(mstatusId, SafeString.createSafeString(name), isdefault);
	}

	/**
	 * Редактируется резолюция
	 *
	 * @param sc           сессия пользователя
	 * @param resolutionId ID резолюции, которая редактируется
	 * @param name         Название резолюции
	 * @param isdefault    По умолчанию ли резолюция
	 * @throws GranException при необходимости
	 */
	public void updateResolution(SessionContext sc, String resolutionId, String name, boolean isdefault) throws GranException {
		log.trace("updateResolution");
		if (sc == null)
			throw new InvalidParameterException(this.getClass(), "updateResolution", "sc", sc);
		if (resolutionId == null)
			throw new InvalidParameterException(this.getClass(), "updateResolution", "resolutionId", sc);
		if (name == null || pv.badSmallDesc(name))
			throw new InvalidParameterException(this.getClass(), "updateResolution", "name", sc);
		SecuredResolutionBean resolution = AdapterManager.getInstance().getSecuredFindAdapterManager().findResolutionById(sc, resolutionId);
		if (!sc.canAction(Action.manageWorkflows, resolution.getMstatus().getWorkflow().getTaskId()))
			throw new AccessDeniedException(this.getClass(), "updateResolution", sc, "!sc.canAction(Action.editResolution, resolution.getMstatus().getWorkflow().getTaskId())", resolutionId);
		if (!resolution.canManage())
			throw new AccessDeniedException(this.getClass(), "updateResolution", sc, "!resolution.canUpdate()", resolutionId);
		KernelManager.getWorkflow().updateResolution(resolutionId, SafeString.createSafeString(name), isdefault);
	}

	/**
	 * Удаляется процесс
	 *
	 * @param sc         сессия пользователя
	 * @param workflowId ID процесса, который удаляется
	 * @throws GranException при необходимости
	 */
	public void deleteWorkflow(SessionContext sc, String workflowId) throws GranException {
		log.trace("deleteWorkflow");
		if (sc == null)
			throw new InvalidParameterException(this.getClass(), "deleteWorkflow", "sc", sc);
		if (workflowId == null)
			throw new InvalidParameterException(this.getClass(), "deleteWorkflow", "workflowId", sc);
		SecuredWorkflowBean workflow = AdapterManager.getInstance().getSecuredFindAdapterManager().findWorkflowById(sc, workflowId);
		if (!sc.canAction(Action.manageWorkflows, workflow.getTaskId()))
			throw new AccessDeniedException(this.getClass(), "deleteWorkflow", sc, "!sc.canAction(Action.deleteWorkflow, workflow.getTaskId())", workflowId);
		if (!workflow.canManage())
			throw new AccessDeniedException(this.getClass(), "deleteWorkflow", sc, "!workflow.canUpdate()", workflowId);
		KernelManager.getWorkflow().deleteWorkflow(workflowId);
	}

	/**
	 * Редактируется название процесса
	 *
	 * @param sc         сессия пользователя
	 * @param workflowId ID процесса, название которого редактируется
	 * @param name       Название процесса
	 * @throws GranException при необходимости
	 */
	public void updateWorkflowName(SessionContext sc, String workflowId, String name) throws GranException {
		log.trace("updateWorkflowName");
		if (sc == null)
			throw new InvalidParameterException(this.getClass(), "updateWorkflowName", "sc", sc);
		if (workflowId == null)
			throw new InvalidParameterException(this.getClass(), "updateWorkflowName", "workflowId", sc);
		if (name == null || pv.badSmallDesc(name))
			throw new InvalidParameterException(this.getClass(), "updateWorkflowName", "name", sc);
		SecuredWorkflowBean workflow = AdapterManager.getInstance().getSecuredFindAdapterManager().findWorkflowById(sc, workflowId);
		if (!sc.canAction(Action.manageWorkflows, workflow.getTaskId()))
			throw new AccessDeniedException(this.getClass(), "updateWorkflowName", sc, "!sc.canAction(Action.editWorkflow, workflow.getTaskId())", workflowId);
		if (!workflow.canManage())
			throw new AccessDeniedException(this.getClass(), "updateWorkflowName", sc, "!workflow.canUpdate()", workflowId);
		KernelManager.getWorkflow().updateWorkflowName(workflowId, SafeString.createSafeString(name));
	}

	/**
	 * Удаляется резолюция
	 *
	 * @param sc           сессия пользователя
	 * @param resolutionId ID резолюции, которая удаляется
	 * @throws GranException при необходимости
	 */
	public void deleteResolution(SessionContext sc, String resolutionId) throws GranException {
		log.trace("deleteResolution");
		if (sc == null)
			throw new InvalidParameterException(this.getClass(), "deleteResolution", "sc", sc);
		if (resolutionId == null)
			throw new InvalidParameterException(this.getClass(), "deleteResolution", "resolutionId", sc);
		SecuredResolutionBean resolution = AdapterManager.getInstance().getSecuredFindAdapterManager().findResolutionById(sc, resolutionId);
		if (!sc.canAction(Action.manageWorkflows, resolution.getMstatus().getWorkflow().getTaskId()))
			throw new AccessDeniedException(this.getClass(), "deleteResolution", sc, "!sc.canAction(Action.editResolution, resolution.getMstatus().getWorkflow().getTaskId())", resolutionId);
		if (!resolution.canManage())
			throw new AccessDeniedException(this.getClass(), "deleteResolution", sc, "!resolution.canUpdate()", resolutionId);
		KernelManager.getWorkflow().deleteResolution(resolutionId);
	}

	/**
	 * Удаляется тип сообщения
	 *
	 * @param sc        сессия пользователя
	 * @param mstatusId ID удаляемого типа сообщения
	 * @throws GranException при необходимости
	 */
	public void deleteMstatus(SessionContext sc, String mstatusId) throws GranException {
		log.trace("deleteMstatus");
		if (sc == null)
			throw new InvalidParameterException(this.getClass(), "deleteMstatus", "sc", sc);
		if (mstatusId == null)
			throw new InvalidParameterException(this.getClass(), "deleteMstatus", "mstatusId", sc);
		SecuredMstatusBean mstatus = AdapterManager.getInstance().getSecuredFindAdapterManager().findMstatusById(sc, mstatusId);
		if (!sc.canAction(Action.manageWorkflows, mstatus.getWorkflow().getTaskId()))
			throw new AccessDeniedException(this.getClass(), "deleteMstatus", sc, "!sc.canAction(Action.deleteMessageType, mstatus.getWorkflow().getTaskId())", mstatusId);
		if (!mstatus.canManage())
			throw new AccessDeniedException(this.getClass(), "deleteMstatus", sc, "!mstatus.canUpdate()", mstatusId);
		KernelManager.getWorkflow().deleteMstatus(mstatusId);
	}

	/**
	 * Редактирует тип сообщения
	 *
	 * @param sc          сессия пользователя
	 * @param mstatusId   ID типа сообщения, который редактируем
	 * @param name        Название типа сообщения
	 * @param description Описание типа сообщения
	 * @param preferences Настройки типа сообщения
	 * @param action      Описание действия, которое выполняет тип сообщения
	 * @throws GranException при необходимости
	 */
	public void updateMstatus(SessionContext sc, String mstatusId, String name, String description, String preferences, String action) throws GranException {
		log.trace("updateMstatus");
		if (sc == null)
			throw new InvalidParameterException(this.getClass(), "updateMstatus", "sc", sc);
		if (mstatusId == null)
			throw new InvalidParameterException(this.getClass(), "updateMstatus", "mstatusId", sc);
		if (name == null || pv.badSmallDesc(name))
			throw new InvalidParameterException(this.getClass(), "updateMstatus", "name", sc);
		// tdc108 #16412
//        if (description == null || pv.badSmallDesc(description))
//            throw new InvalidParameterException(this.getClass(), "updateMstatus", "description", sc);
		SecuredMstatusBean mstatus = AdapterManager.getInstance().getSecuredFindAdapterManager().findMstatusById(sc, mstatusId);
		//if (!(sc.canAction(Action.editMessageType, mstatus.getWorkflow().getTaskId()) &&
		//        mstatus.canUpdate()))
		//    throw new AccessDeniedException(this.getClass(), "updateMstatus", sc);
		if (!sc.canAction(Action.manageWorkflows, mstatus.getWorkflow().getTaskId()))
			throw new AccessDeniedException(this.getClass(), "updateMstatus", sc, "!sc.canAction(Action.editMessageType, mstatus.getWorkflow().getTaskId())", mstatusId);
		if (!mstatus.canManage())
			throw new AccessDeniedException(this.getClass(), "updateMstatus", sc, "!mstatus.canUpdate()", mstatusId);
		KernelManager.getWorkflow().updateMstatus(mstatusId, SafeString.createSafeString(name), SafeString.createSafeString(description), preferences, SafeString.createSafeString(action));
	}

	/**
	 * Задает права доступа на действие над типом сообщения для указанного статуса
	 *
	 * @param sc         сессия пользователя
	 * @param access     тип доступа
	 * @param prstatusId ID cnfnecf
	 * @param mstatusId  ID типа сообщения
	 * @throws GranException при необходимости
	 * @see com.trackstudio.constants.WorkflowConstants
	 */
	public void grantProcess(SessionContext sc, String access, String prstatusId, String mstatusId) throws GranException {
		grant(sc, access, prstatusId, mstatusId, "grantProcess");
	}

	/**
	 * Задает права доступа на право быть ответсвенным над типом сообщения для указанного статуса
	 *
	 * @param sc         сессия пользователя
	 * @param access     тип доступа
	 * @param prstatusId ID cnfnecf
	 * @param mstatusId  ID типа сообщения
	 * @throws GranException при необходимости
	 * @see com.trackstudio.constants.WorkflowConstants
	 */
	public void grantBeHandler(SessionContext sc, String access, String prstatusId, String mstatusId) throws GranException {
		grant(sc, access, prstatusId, mstatusId, "grantBeHandler");
	}

	/**
	 * Задает права доступа на просмотр над типом сообщения для указанного статуса
	 *
	 * @param sc         сессия пользователя
	 * @param access     тип доступа
	 * @param prstatusId ID cnfnecf
	 * @param mstatusId  ID типа сообщения
	 * @throws GranException при необходимости
	 * @see com.trackstudio.constants.WorkflowConstants
	 */
	public void grantView(SessionContext sc, String access, String prstatusId, String mstatusId) throws GranException {
		grant(sc, access, prstatusId, mstatusId, "grantView");
	}

	private void grant(SessionContext sc, String access, String prstatusId, String mstatusId, String methodName) throws GranException {
		log.trace(methodName);
		if (sc == null)
			throw new InvalidParameterException(this.getClass(), methodName, "sc", sc);
		if (prstatusId == null)
			throw new InvalidParameterException(this.getClass(), methodName, "prstatusId", sc);
		if (mstatusId == null)
			throw new InvalidParameterException(this.getClass(), methodName, "mstatusId", sc);
		SecuredMstatusBean mstatus = AdapterManager.getInstance().getSecuredFindAdapterManager().findMstatusById(sc, mstatusId);
		SecuredPrstatusBean prstatus = AdapterManager.getInstance().getSecuredFindAdapterManager().findPrstatusById(sc, prstatusId);
		if (sc.canAction(Action.manageWorkflows, mstatus.getWorkflow().getTask().getId()) && (mstatus.canView() || prstatus.canView())) {
			if (methodName == null) {
				KernelManager.getWorkflow().removeBeMstatusByPrstatus(prstatusId, mstatusId);
			} else if (methodName.equals("grantView"))
				KernelManager.getWorkflow().grantView(access, prstatusId, mstatusId);
			else if (methodName.equals("grantBeHandler"))
				KernelManager.getWorkflow().grantBeHandler(access, prstatusId, mstatusId);
			else if (methodName.equals("grantProcess"))
				KernelManager.getWorkflow().grantProcess(access, prstatusId, mstatusId);
			else
				throw new GranException("bad methodName");
		}
	}

	/**
	 * Удаляется приоритет
	 *
	 * @param sc         сессия пользователя
	 * @param priorityId ID приоритета, который удаляется
	 * @throws GranException при необходимости
	 */
	public void deletePriority(SessionContext sc, String priorityId) throws GranException {
		log.trace("deletePriority");
		if (sc == null)
			throw new InvalidParameterException(this.getClass(), "deletePriority", "sc", sc);
		if (priorityId == null)
			throw new InvalidParameterException(this.getClass(), "deletePriority", "priorityId", sc);
		SecuredPriorityBean priority = AdapterManager.getInstance().getSecuredFindAdapterManager().findPriorityById(sc, priorityId);
		if (!sc.canAction(Action.manageWorkflows, priority.getWorkflow().getTaskId()))
			throw new AccessDeniedException(this.getClass(), "deletePriority", sc, "!sc.canAction(Action.deletePriority, priority.getWorkflow().getTaskId())", priorityId);
		if (!priority.canManage())
			throw new AccessDeniedException(this.getClass(), "deletePriority", sc, "!priority.canUpdate()", priorityId);
		KernelManager.getWorkflow().deletePriority(priorityId);
	}

	/**
	 * Удаляется состояние
	 *
	 * @param sc       сессия пользователя
	 * @param statusId ID удаляемого состояния
	 * @throws GranException при необходимости
	 * @deprecated Legacy API
	 */
	public void deleteStatus(SessionContext sc, String statusId) throws GranException {
		deleteState(sc, statusId);
	}

	/**
	 * Удаляется состояние
	 *
	 * @param sc       сессия пользователя
	 * @param statusId ID удаляемого состояния
	 * @throws GranException при необходимости
	 */
	public void deleteState(SessionContext sc, String statusId) throws GranException {
		log.trace("deleteState");
		if (sc == null)
			throw new InvalidParameterException(this.getClass(), "deleteState", "sc", sc);
		if (statusId == null)
			throw new InvalidParameterException(this.getClass(), "deleteState", "statusId", sc);
		SecuredStatusBean status = AdapterManager.getInstance().getSecuredFindAdapterManager().findStatusById(sc, statusId);
		//if (!(sc.canAction(Action.deleteState, status.getWorkflow().getTaskId()) && status.canUpdate()))
		//    throw new AccessDeniedException(this.getClass(), "deleteState", sc);
		if (!sc.canAction(Action.manageWorkflows, status.getWorkflow().getTaskId()))
			throw new AccessDeniedException(this.getClass(), "deleteState", sc, "!sc.canAction(Action.deleteState, status.getWorkflow().getTaskId())", statusId);
		if (!status.canUpdate())
			throw new AccessDeniedException(this.getClass(), "deleteState", sc, "!status.canUpdate()", statusId);
		KernelManager.getWorkflow().deleteState(statusId);
	}

	/**
	 * Редактируется состояние
	 *
	 * @param sc       сессия пользователя
	 * @param statusId ID состояния, которое редактируется
	 * @param name     Название состояния
	 * @param start    Является ли состояние начальным
	 * @param finish   Является ли состояние конечным
	 * @param color    Цвет состояния
	 * @throws GranException при необходимости
	 */
	public void updateState(SessionContext sc, String statusId, String name, boolean start, boolean finish, String color) throws GranException {
		log.trace("updateState");
		if (sc == null)
			throw new InvalidParameterException(this.getClass(), "updateState", "sc", sc);
		if (statusId == null)
			throw new InvalidParameterException(this.getClass(), "updateState", "statusId", sc);
		if (name == null || pv.badSmallDesc(name))
			throw new InvalidParameterException(this.getClass(), "updateState", "name", sc);
		if (color == null || pv.badSmallDesc(color))
			throw new InvalidParameterException(this.getClass(), "updateState", "color", sc);
		SecuredStatusBean status = AdapterManager.getInstance().getSecuredFindAdapterManager().findStatusById(sc, statusId);
		if (!sc.canAction(Action.manageWorkflows, status.getWorkflow().getTaskId()))
			throw new AccessDeniedException(this.getClass(), "updateState", sc, "!sc.canAction(Action.editState, status.getWorkflow().getTaskId())", statusId);
		if (!status.canManage())
			throw new AccessDeniedException(this.getClass(), "updateState", sc, "!status.canUpdate()", statusId);
		KernelManager.getWorkflow().updateState(statusId, SafeString.createSafeString(name), start, finish, color);
	}

	/**
	 * Редактируется приоритет
	 *
	 * @param sc              сессия пользователя
	 * @param priorityId      ID приоритета, который редактируется
	 * @param name            Название приоритета
	 * @param description     Описание приоритета
	 * @param order           Порядок приоритета
	 * @param defaultPriority Является ли приоритет по умолчанию
	 * @throws GranException при необходимости
	 */
	public void updatePriority(SessionContext sc, String priorityId, String name, String description, int order,
	                           boolean defaultPriority) throws GranException {
		log.trace("updatePriority");
		if (sc == null)
			throw new InvalidParameterException(this.getClass(), "updatePriority", "sc", sc);
		if (priorityId == null)
			throw new InvalidParameterException(this.getClass(), "updatePriority", "priorityId", sc);
		if (name == null || pv.badSmallDesc(name))
			throw new InvalidParameterException(this.getClass(), "updatePriority", "name", sc);
		//todo tdc108 #16412
//        if (description == null || pv.badDesc(description))
//            throw new InvalidParameterException(this.getClass(), "updatePriority", "description", sc);
		SecuredPriorityBean priority = AdapterManager.getInstance().getSecuredFindAdapterManager().findPriorityById(sc, priorityId);
		if (!sc.canAction(Action.manageWorkflows, priority.getWorkflow().getTaskId()))
			throw new AccessDeniedException(this.getClass(), "updatePriority", sc, "!sc.canAction(Action.editPriority, priority.getWorkflow().getTaskId())", priorityId);
		if (!priority.canManage())
			throw new AccessDeniedException(this.getClass(), "updatePriority", sc, "!priority.canUpdate()", priorityId);
		KernelManager.getWorkflow().updatePriority(priorityId, SafeString.createSafeString(name), SafeString.createSafeString(description), order, defaultPriority);
	}

	/**
	 * Создается приоритет
	 *
	 * @param sc          сессия пользователя
	 * @param name        Название приоритета
	 * @param description Описание приоритета
	 * @param order       Порядок приоритета
	 * @param isdefault   Является ли приоритет по умолчанию
	 * @param workflowId  ID процесса, для которого создан приоритет
	 * @return ID созданного приоритета
	 * @throws GranException при необходимости
	 */
	public String createPriority(SessionContext sc, String name, String description, int order, boolean isdefault, String workflowId) throws GranException {
		log.trace("createPriority");
		if (sc == null)
			throw new InvalidParameterException(this.getClass(), "createPriority", "sc", sc);
		if (name == null || pv.badSmallDesc(name))
			throw new InvalidParameterException(this.getClass(), "createPriority", "name", sc);
		//todo tdc108 #16412
//        if (description == null || pv.badDesc(description))
//            throw new InvalidParameterException(this.getClass(), "createPriority", "description", sc);
		if (workflowId == null)
			throw new InvalidParameterException(this.getClass(), "createPriority", "workflowId", sc);
		SecuredWorkflowBean workflow = AdapterManager.getInstance().getSecuredFindAdapterManager().findWorkflowById(sc, workflowId);
		if (!sc.canAction(Action.manageWorkflows, workflow.getTaskId()))
			throw new AccessDeniedException(this.getClass(), "createPriority", sc, "!sc.canAction(Action.createPriority, workflow.getTaskId())", workflowId);
		if (!workflow.canManage())
			throw new AccessDeniedException(this.getClass(), "createPriority", sc, "!workflow.canUpdate()", workflowId);
		return KernelManager.getWorkflow().createPriority(SafeString.createSafeString(name), SafeString.createSafeString(description), order, isdefault, workflowId);
	}

	/**
	 * Редактируется переход
	 *
	 * @param sc             сессия пользователя
	 * @param mstatusId      ID типа сообщения для которого редактируется переход
	 * @param startStatusId  ID начального состояния
	 * @param finishStatusId ID конечного состояния
	 * @throws GranException при необходимости
	 */
	public void updateTransition(SessionContext sc, String mstatusId, String startStatusId, String finishStatusId) throws GranException {
		log.trace("updateTransition");
		if (sc == null)
			throw new InvalidParameterException(this.getClass(), "updateTransition", "sc", sc);
		if (mstatusId == null)
			throw new InvalidParameterException(this.getClass(), "updateTransition", "mstatusId", sc);
		if (startStatusId == null)
			throw new InvalidParameterException(this.getClass(), "updateTransition", "startStatusId", sc);
		if (finishStatusId == null)
			throw new InvalidParameterException(this.getClass(), "updateTransition", "finishStatusId", sc);
		SecuredMstatusBean mstatus = AdapterManager.getInstance().getSecuredFindAdapterManager().findMstatusById(sc, mstatusId);
		//if (!(sc.canAction(Action.editTransition, mstatus.getWorkflow().getTaskId()) && mstatus.canUpdate()))
		//    throw new AccessDeniedException(this.getClass(), "updateTransition", sc);
		if (!sc.canAction(Action.manageWorkflows, mstatus.getWorkflow().getTaskId()))
			throw new AccessDeniedException(this.getClass(), "updateTransition", sc, "!sc.canAction(Action.editTransition, mstatus.getWorkflow().getTaskId())", mstatusId);
		if (!mstatus.canUpdate())
			throw new AccessDeniedException(this.getClass(), "updateTransition", sc, "!mstatus.canUpdate()", mstatusId);
		KernelManager.getWorkflow().updateTransition(mstatusId, startStatusId, finishStatusId);
	}

	/**
	 * Удляляется переход
	 *
	 * @param sc           сессия пользователя
	 * @param transitionId ID перехода, который удаляется
	 * @throws GranException при необходимости
	 */
	public void deleteTransition(SessionContext sc, String transitionId) throws GranException {
		log.trace("deleteTransition");
		if (sc == null)
			throw new InvalidParameterException(this.getClass(), "deleteTransition", "sc", sc);
		if (transitionId == null)
			throw new InvalidParameterException(this.getClass(), "deleteTransition", "transitionId", sc);
		SecuredTransitionBean tran = AdapterManager.getInstance().getSecuredFindAdapterManager().findTransitionById(sc, transitionId);
		if (!sc.canAction(Action.manageWorkflows, tran.getMstatus().getWorkflow().getTaskId()))
			throw new AccessDeniedException(this.getClass(), "deleteTransition", sc, "!sc.canAction(Action.editTransition, tran.getMstatus().getWorkflow().getTaskId())", transitionId);
		if (!tran.canManage())
			throw new AccessDeniedException(this.getClass(), "deleteTransition", sc, "!tran.canUpdate()", transitionId);
		KernelManager.getWorkflow().deleteTransition(transitionId);
	}

	/**
	 * Устанавливает триггеры для типа сообщения
	 *
	 * @param sc        сессия пользователя
	 * @param mstatusId ID типа сообщения
	 * @param before    before-триггер
	 * @param insteadOf instanseOf-триггер
	 * @param after     after-триггер
	 * @throws GranException при необходимости
	 */
	public void setMstatusTrigger(SessionContext sc, String mstatusId, String before, String insteadOf, String after) throws GranException {
		log.trace("deleteTransition");
		if (sc == null)
			throw new InvalidParameterException(this.getClass(), "setMstatusTrigger", "sc", sc);
		if (mstatusId == null)
			throw new InvalidParameterException(this.getClass(), "setMstatusTrigger", "mstatusId", sc);
		SecuredMstatusBean mstat = AdapterManager.getInstance().getSecuredFindAdapterManager().findMstatusById(sc, mstatusId);
		if (!sc.canAction(Action.manageWorkflows, mstat.getWorkflow().getTaskId()))
			throw new AccessDeniedException(this.getClass(), "setMstatusTrigger", sc, "!sc.canAction(Action.editMessageTypeTrigger, mstat.getWorkflow().getTaskId())", mstatusId);
		if (!mstat.canManage())
			throw new AccessDeniedException(this.getClass(), "setMstatusTrigger", sc, "!mstat.canUpdate()", mstatusId);
		KernelManager.getWorkflow().setMstatusTrigger(mstatusId, SafeString.createSafeString(before), SafeString.createSafeString(insteadOf), SafeString.createSafeString(after));
	}

	/**
	 * Проверяет процесс на валидность
	 *
	 * @param sc         сессия пользователя
	 * @param workflowId ID процесса, который проверяем на валидность
	 * @return TRUE - если валидный, FALSE - если нет
	 * @throws GranException при необходимости
	 */
	public Boolean getWorkflowIsValid(SessionContext sc, String workflowId) throws GranException {
		log.trace("getWorkflowIsValid");
		if (sc == null)
			throw new InvalidParameterException(this.getClass(), "getWorkflowIsValid", "sc", sc);
		if (workflowId == null)
			throw new InvalidParameterException(this.getClass(), "getWorkflowIsValid", "workflowId", sc);
		return KernelManager.getWorkflow().getWorkflowIsValid(workflowId);
	}

	public List<TaskRelatedInfo> getMstatusTask(SessionContext sc, String mstatusId) throws GranException {
		log.trace("getMstatusTask");
		if (sc == null)
			throw new InvalidParameterException(this.getClass(), "getMstatusTask", "sc", sc);
		if (mstatusId == null)
			throw new InvalidParameterException(this.getClass(), "getMstatusTask", "mstatusId", sc);
		return KernelManager.getTask().getTaskMstatusList(mstatusId);
	}

	public List<TaskRelatedInfo> getStateTask(SessionContext sc, String statusId) throws GranException {
		log.trace("getStateTask");
		if (sc == null)
			throw new InvalidParameterException(this.getClass(), "deleteState", "sc", sc);
		if (statusId == null)
			throw new InvalidParameterException(this.getClass(), "deleteState", "statusId", sc);

		return KernelManager.getTask().getTaskStatusList(statusId);
	}
}
