package com.trackstudio.action;

import java.util.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.trackstudio.kernel.cache.*;
import com.trackstudio.kernel.manager.IndexManager;
import org.ajaxtags.servlets.BaseAjaxServlet;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.codehaus.jackson.map.ObjectMapper;

import com.trackstudio.app.adapter.AdapterManager;
import com.trackstudio.app.session.SessionContext;
import com.trackstudio.exception.GranException;
import com.trackstudio.kernel.lock.LockManager;
import com.trackstudio.kernel.manager.KernelManager;
import com.trackstudio.model.Status;
import com.trackstudio.secured.SecuredTaskBean;
import com.trackstudio.secured.SecuredUserBean;
import com.trackstudio.startup.Config;
import com.trackstudio.tools.AutocompleteItem;
import com.trackstudio.tools.compare.FieldSort;
import com.trackstudio.tools.compare.SortUser;
import com.trackstudio.tools.textfilter.HTMLEncoder;
import com.trackstudio.tools.textfilter.MacrosUtil;
import org.codehaus.jackson.map.annotate.JsonSerialize;

import static com.trackstudio.tools.Null.isNotNull;
import static com.trackstudio.tools.Null.isNull;
import static com.trackstudio.tools.textfilter.MacrosUtil.*;
import static com.trackstudio.tools.textfilter.MacrosUtil.InputLocal.RU;
import static com.trackstudio.tools.textfilter.MacrosUtil.InputLocal.US;

public class PredictorServlet extends BaseAjaxServlet {
	private static final LockManager lockManager = LockManager.getInstance();

	private static Log log = LogFactory.getLog(PredictorServlet.class);
	private static final int LIMIT = Integer.valueOf(Config.getProperty("trackstudio.autocomplete.limit", "10"));
	private final int USER_LIST_LIMIT = Integer.valueOf(Config.getProperty("trackstudio.user.list.limit", "-1"));
	private final boolean SEARCH_BY_CONTAINS = Config.isTurnItOn("trackstudio.udf.user.search.by.contains");
	private final TaskRelatedManager tasks = TaskRelatedManager.getInstance();
	private final UserRelatedManager users = UserRelatedManager.getInstance();

	private AutocompleteItem.Item buildItem(String number, String name, String ico,  String value, boolean selected, String css, long update, String shortname, boolean shouldCut) {
		return new AutocompleteItem.Item(String.format("%s_%s", number, value), String.format("<span style='text-align:button;'><img src='%s'/> %s [%s]</span>", ico, convertName(name, shouldCut), number));
	}

	private static String convertName(String name, boolean shouldCut) {
		if (name == null) return null;
		if (shouldCut) {
			int limitSize = getIntegerOrDefault(Config.getProperty("trackstudio.auto.complete.limit"), 34);
			if (name.length() > limitSize) {
				name = name.substring(0, limitSize);
			}
		}
		return HTMLEncoder.encode(name);
	}

	private void buildEmptyItem(final List<AutocompleteItem.Item> items, String path, String imageServlet, String key) {
		String ico = path + imageServlet + "/cssimages/ico.search.gif";
		items.add(
				new AutocompleteItem.Item(null, String.format("<span style='text-align:button;'><img src='%s'/> %s </span>", ico, convertName(key, true)))
		);
	}

	private void buildTask(final List<AutocompleteItem.Item> items, String path, String imageServlet, TaskRelatedInfo info) throws GranException {
		String ico = path + imageServlet + "/icons/categories/" + KernelManager.getFind().findCategory(info.getCategoryId()).getIcon();
		Status status = KernelManager.getFind().findStatus(info.getStatusId());
		String statusIco = String.format(
				"<img title='%s' class='state' border='0' style='background-color: %s'; src='%s/cssimages/%s'/>",
				status.getName(),
				status.getColor(),
				path,
				status.isStart() && status.isFinish() ? "finishstate.png" : status.isStart() ? "startstate.png" : status.isFinish() ? "finishstate.png" : "state.png");
		;
		items.add(
				new AutocompleteItem.Item(
						String.format("%s_%s", String.format("#%s", info.getNumber()), info.getId()),
						String.format("<span style='text-align:button;'><img src='%s'/>&nbsp;%s %s [%s]</span>", ico, statusIco, convertName(info.getName(), true), info.getNumber()))
		);
	}

	private void buildUser(final List<AutocompleteItem.Item> items, String path, String imageServlet, UserRelatedInfo info, boolean plusPrstatus) throws GranException {
		String icon = info.isEnabled() ? "arw.usr.a.gif" : "arw.usr.gif";
		String ico = path + imageServlet + "/cssimages/" + icon;
		if (plusPrstatus) {
			items.add(buildItem(info.getLogin(), info.getName() + " [" + info.getPrstatus().getName() + "]", ico, info.getId(), false, "user", 0l, "", false));
		} else {
			items.add(buildItem(info.getLogin(), info.getName(), ico, "u-" + info.getId(), false, "user", 0l, "", true));
		}
	}

	private void buildUser(final List<AutocompleteItem.Item> items, UserRelatedInfo info, String udfId) throws GranException {
		items.add(new AutocompleteItem.Item(String.format("%s_%s", udfId, info.getId()),  convertName(info.getName() + " [" + info.getPrstatus().getName()+ "]", false)));
	}

	private void searchByUdfUser(HttpServletRequest request, String searchFor, final List<AutocompleteItem.Item> items, SessionContext sc) throws GranException {
		String taskId = request.getParameter("taskId");
		String udfId = request.getParameter("udfId");
		SecuredTaskBean task = new SecuredTaskBean(taskId, sc);
		List<SecuredUserBean> userList;
		if ("true".equals(request.getParameter("create"))) {
			userList = task.getMapUDFValuesForNewTask(request.getParameter("workflowId")).get(udfId).getLimitList(-1);
		} else {
			userList = task.getUDFValues().get(udfId).getLimitList(-1);
		}
		int sizeUserList = 0;
		searchFor = searchFor.toLowerCase();
		List<SecuredUserBean> finds = new ArrayList<SecuredUserBean>(USER_LIST_LIMIT > 0 ? USER_LIST_LIMIT : 100);
		Collections.sort(userList, new SortUser(FieldSort.NAME));
		for (SecuredUserBean user : userList) {
			if (USER_LIST_LIMIT != -1 && sizeUserList >= USER_LIST_LIMIT) {
				break;
			}
			if (isNull(searchFor) ||
					this.searchAlgorithm(user.getLogin(), searchFor) ||
					this.searchAlgorithm(user.getName(), searchFor) ||
					this.searchAlgorithm(user.getPrstatus().getName(), searchFor)) {
				finds.add(user);
				sizeUserList++;
			}
		}
		Collections.sort(finds, new SortUser(FieldSort.REVERSE_NAME));
		for (SecuredUserBean user : finds) {
			buildUser(items, user.getUser(), udfId);
		}
	}

	/**
	 * Ищем по имени, алиасу, номеру
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	public String getXmlContent(HttpServletRequest request, HttpServletResponse response) throws Exception {
		final List<AutocompleteItem.Item> items = new ArrayList<AutocompleteItem.Item>();
		boolean w = lockManager.acquireConnection();
		try {
			request.setCharacterEncoding(Config.getEncoding());
			String imageServlet = "/ImageServlet/" + GeneralAction.SERVLET_KEY;
			//if this exists, we need to add subtasks.
			String defaultTask = request.getParameter("defaultTask");
			String searchFor = request.getParameter("key");
			boolean byTask = "true".equals(request.getParameter("byTask"));
			boolean byUdf = "true".equals(request.getParameter("byUdf"));
			boolean onlyTask = "task".equals(request.getParameter("type"));
			String path = request.getContextPath();
			SessionContext sc = GeneralAction.getInstance().imports(request, response, true);
			if (sc != null) {
				if (byUdf) {
					this.searchByUdfUser(request, searchFor, items, sc);
				} else if (byTask) {
					for (String number : searchFor.split(";")) {
						TaskRelatedInfo info = tasks.find(
								KernelManager.getTask().findByNumber(number)
						);
						buildTask(items, path, imageServlet, info);
					}
				} else if (isNotNull(searchFor)) {
					buildEmptyItem(items, path, imageServlet, searchFor);
					List<TaskRelatedInfo> rslTasks = new ArrayList<TaskRelatedInfo>();
					for (String taskId : IndexManager.getInstance().searchTasks(searchFor).keySet()) {
						if (sc.taskOnSight(taskId)) {
							rslTasks.add(tasks.find(taskId));
							if (rslTasks.size() >= LIMIT) {
								break;
							}
						}
					}
					for (TaskRelatedInfo info : rslTasks) {
						buildTask(items, path, imageServlet, info);
					}
					if (!onlyTask && rslTasks.size() < LIMIT) {
						int count = rslTasks.size();
						for (String userId : IndexManager.getInstance().searchUsers(searchFor, LIMIT).keySet()){
							if (sc.userOnSight(userId)) {
								buildUser(items, path, imageServlet, users.find(userId), false);
								count++;
							}
							if (count >= LIMIT) {
								break;
							}
						}
					}
				}
			}
		} catch (Exception e) {
			log.error("Error predictor servlet", e);
		} finally {
			if (w) lockManager.releaseConnection();
		}
		ObjectMapper mapper = new ObjectMapper();
		mapper.setSerializationInclusion(JsonSerialize.Inclusion.NON_NULL);
		return mapper.writeValueAsString(items);
	}

	private boolean searchAlgorithm(String left, String right) {
		final boolean result;
		if (SEARCH_BY_CONTAINS) {
			result = left.toLowerCase().contains(right.toLowerCase());
		} else {
			result = left.toLowerCase().startsWith(right.toLowerCase());
		}
		return result;
	}
}


