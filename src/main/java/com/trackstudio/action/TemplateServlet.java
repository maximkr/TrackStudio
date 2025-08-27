package com.trackstudio.action;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.trackstudio.app.CalculatedValue;
import com.trackstudio.app.adapter.AdapterManager;
import com.trackstudio.app.adapter.email.EmailUtil;
import com.trackstudio.app.csv.CSVImport;
import com.trackstudio.app.session.SessionContext;
import com.trackstudio.app.session.SessionManager;
import com.trackstudio.exception.AccessDeniedException;
import com.trackstudio.exception.GranException;
import com.trackstudio.exception.UserException;
import com.trackstudio.kernel.cache.AbstractPluginCacheItem;
import com.trackstudio.kernel.cache.PluginCacheItem;
import com.trackstudio.kernel.cache.PluginCacheManager;
import com.trackstudio.kernel.cache.PluginType;
import com.trackstudio.kernel.cache.UserRelatedManager;
import com.trackstudio.kernel.lock.LockManager;
import com.trackstudio.kernel.manager.AttachmentManager;
import com.trackstudio.kernel.manager.FindManager;
import com.trackstudio.model.Template;
import com.trackstudio.secured.SecuredAttachmentBean;
import com.trackstudio.secured.SecuredFilterBean;
import com.trackstudio.secured.SecuredTaskBean;
import com.trackstudio.startup.Config;
import com.trackstudio.startup.I18n;
import com.trackstudio.tools.HibernateUtil;
import com.trackstudio.tools.TemplateUtil;
import com.trackstudio.tools.Uploader;
import com.trackstudio.tools.formatter.DateFormatter;

import freemarker.ext.beans.BeansWrapper;
import freemarker.template.TemplateException;
import freemarker.template.TemplateHashModel;
import freemarker.template.TemplateModelException;

public class TemplateServlet extends HttpServlet {

	private static Log log = LogFactory.getLog(TemplateServlet.class);
	private static String className = "TemplateServlet.";
	protected HibernateUtil hu = new HibernateUtil();
	private static final LockManager lockManager = LockManager.getInstance();

	private static final String TS_CURRENT_TRACKING = "ts_current_tracking";
	private static final String TS_PERSIST_TRACKING = "ts_persist_tracking";
	public static final String CHARSET = "charset";
	public static final String DATE_FORMATTER = "DateFormatter";
	public static final String HOURS_FORMATTER = "HourFormatter";
	public static final String TASK = "task";
	public static final String DEFAULT_FILTER = "defaultFilter";
	public static final String TEMPLATE_NAME = "template";
	public static final String TEMPLATE_TASK = "templateTask";
	public static final String CONTEXT_PATH = "contextPath";
	public static final String I18N = "i18n";
	public static final String UTIL = "Util";

	public static final String REQUEST_VALUES = "values";
	public static final String REQUEST_VALUE = "value";
	public static final String REQUEST_COOKIES = "cookies";
	public static final String REQUEST_ATTRIBUTES = "attributes";
	public static final String FILTERS = "filters";
	public static final String REQUEST = "request";
	public static final String USER = "user";
	public static final String SESSION = "session";
	public static final String SC = "sc";
	public static final String BSH = "Bsh";
	public static final String UPLOADER = "Uploader";
	public static final String TEMPLATE_LOGIN = "template_login";
	public static final String TEMPLATE_PASSWORD = "template_password";
	public static final String TEMPLATE_SESSION = "template_session";
	public static final String TEMPLATE_PATH = "/template/";
	public static final String TEMPLATE_ATTACHMENT = "template_attachment";
	public static final String TEMPLATE_FILE = "file";
	public static final String TEMPLATE_FILTER = "filter";
	public static final String WIKI = "Wiki";
	public static final String ATTACH_DESC = "forms_task_edit_attachment_desc";

	public void doGet(HttpServletRequest mrequest, HttpServletResponse response) throws IOException, ServletException {
		boolean w = lockManager.acquireConnection();
		try {
			response.reset();
			mrequest.setCharacterEncoding(Config.getEncoding());
			// parse possible multipart request;
			Uploader uploader = new Uploader(mrequest);
			int i = mrequest.getRequestURI().indexOf(mrequest.getContextPath() + "/");
			if (i != -1) {
				String requestUrl = mrequest.getRequestURI().substring(i);

				HashMap templateParameters = TemplateUtil.parseTemplateURL(requestUrl);
				log.debug("PARAMETRS TEMPLATE:\n" + templateParameters + "\n");
				if (templateParameters.containsKey(TEMPLATE_NAME) && templateParameters.containsKey(TASK)) {
					String taskId = CSVImport.findTaskIdByNumber(templateParameters.get(TASK).toString());
					if (taskId != null) {
						Template found = null;
						for (Template t : FindManager.getTemplate().getTemplateList(taskId)) {
							if (t.getName().equals(templateParameters.get(TEMPLATE_NAME)) &&
							    t.getActive() != null && t.getActive() == 1) {
								found = t;
								break;

							}
						}
						if (found != null) {
							SessionContext sc = null;
							if (uploader.getParameter(TEMPLATE_LOGIN) != null) {
								String login = uploader.getParameter(TEMPLATE_LOGIN);
								String password = uploader.getParameter(TEMPLATE_PASSWORD);
								String sessionId = AdapterManager.getInstance().getSecuredUserAdapterManager().authenticate(login, password, mrequest);
								sc = SessionManager.getInstance().getSessionContext(sessionId);

								Cookie c = new Cookie(TEMPLATE_SESSION, sessionId);
								c.setMaxAge(60 * 60 * 24 * 365);
								c.setPath(mrequest.getContextPath() + TEMPLATE_PATH + found.getName());
								response.addCookie(c);
								mrequest.setAttribute(TEMPLATE_SESSION, sc);

							}
							if (sc == null && mrequest.getCookies() != null) {
								for (ListIterator i1 = Arrays.asList(mrequest.getCookies()).listIterator(); i1.hasNext(); ) {
									Cookie c = (Cookie) i1.next();
									if (c.getName().equals(TEMPLATE_SESSION) && c.getValue() != null && c.getValue().length() > 0) {
										sc = SessionManager.getInstance().getSessionContext(c.getValue());
										if (sc != null) {
											sc.setSessionInCookies(true);
											mrequest.setAttribute(TEMPLATE_SESSION, sc);
											break;
										}
									}
								}
							} else if (sc == null && mrequest.getAttribute(TEMPLATE_SESSION) != null) {
								sc = (SessionContext) mrequest.getAttribute(TEMPLATE_SESSION);
								mrequest.setAttribute(TEMPLATE_SESSION, sc);
							}
							if (uploader.getParameter("_logout") != null) {
								sc = GeneralAction.getInstance().imports(mrequest, response);
								if (sc != null) {
									AdapterManager.getInstance().getSecuredUserAdapterManager().updateLastLogonDate(sc, sc.getUserId());
									SessionManager.getInstance().remove(sc.getId());
								}
								LoginAction.defineObjects(mrequest, response);
								log.debug("INVALIDATE COOKIES!");
								SessionContext.resetCookies(mrequest, response);
								mrequest.removeAttribute("session");
								mrequest.getSession().setAttribute("autologin", null);
								mrequest.getSession().setAttribute("autopassword", null);
								sc = null;
							}
							if (sc == null) {
								if (found.getUser() != null)
									sc = SessionManager.getInstance().getSessionContext(SessionManager.getInstance().create(UserRelatedManager.getInstance().find(found.getUser().getId())));
								else {
									sc = GeneralAction.getInstance().imports(mrequest, response);
									if (sc == null) {
										unauthorizedTemplate(mrequest, response, found);
										return;
									}
								}
							}
							// template found

							if (templateParameters.containsKey(TEMPLATE_ATTACHMENT)) {
								downloadAttachment(sc, mrequest, response, templateParameters.get(TEMPLATE_ATTACHMENT).toString());


							} else {
								boolean result = parseTemplate(sc, uploader, mrequest, response, taskId, found, templateParameters.get(TEMPLATE_FILE), templateParameters.get(TEMPLATE_FILTER));
								if (!result) {
									error404(mrequest, response);
								}
							}
						} else error404(mrequest, response);
					} else error404(mrequest, response);
				} else error404(mrequest, response);
			} else error404(mrequest, response);
		} catch (Exception ex) {
			log.error("Error", ex);
			try {
				EmailUtil.buildTemplate("error.ftl", new HashMap<String, Object>(), response.getWriter(), PluginType.WEB);
				response.flushBuffer();
			} catch (Exception e) {
				log.error("Error", ex);
				mrequest.setAttribute("javax.servlet.jsp.jspException", ex);
				RequestDispatcher requestDispatcher = mrequest.getRequestDispatcher("/jsp/Error.jsp");
				requestDispatcher.forward(mrequest, response);
			}
		} finally {
			if (w) lockManager.releaseConnection();
		}

	}

	/**
	 * Catch FileNotFoundException
	 *
	 * @param sc
	 * @param request
	 * @param response
	 * @param attId
	 * @param attId
	 * @throws GranException
	 */
	private void downloadAttachment(SessionContext sc, HttpServletRequest request, HttpServletResponse response, String attId) throws GranException {


		ServletConfig config = getServletConfig();

		try {
			SecuredAttachmentBean bean = AdapterManager.getInstance().getSecuredFindAdapterManager().findAttachmentById(sc, attId);
			if (bean.isAllowedByACL()) {
				File f = AttachmentManager.getInstance().getAttachmentFile(bean.getTaskId(), bean.getUserId(), attId, false);

				FileInputStream fis;

				fis = new FileInputStream(f);
				String fn = URLEncoder.encode(bean.getName(), "UTF-8");
				byte[] bytes = new byte[(int) f.length()];
				fis.read(bytes);
				fis.close();
				String MIME = config.getServletContext().getMimeType(AttachmentManager.getAttachmentDirPath(bean.getTaskId(), bean.getUserId(), false) + '/' + bean.getName());
				response.setContentType((MIME == null ? "application/octet-stream" : MIME + " ;charset=\"UTF-8\""));
				response.setContentLength(bytes.length);
				if (request.getHeader("user-agent") != null && request.getHeader("user-agent").indexOf("MSIE") > -1)
					response.setHeader("Content-disposition", "attachment; filename=" + fn);
				else
					response.setHeader("Content-disposition", "attachment; filename*=utf-8" + "''" + fn + ";");

				response.setHeader("Cache-Control", "public");
				response.setHeader("Pragma", "public");
				response.setDateHeader("Expires", System.currentTimeMillis() + 300L);

				ServletOutputStream ouputStream = response.getOutputStream();
				ouputStream.write(bytes, 0, bytes.length);
				ouputStream.flush();
				ouputStream.close();
			}
		} catch (IOException io) {

		}

	}

	private String getCookie(HttpServletRequest request, String name) {
		if (request.getCookies() != null) {
			for (ListIterator i1 = Arrays.asList(request.getCookies()).listIterator(); i1.hasNext(); ) {
				Cookie c = (Cookie) i1.next();
				if (c.getName().equals(name))
					return c.getValue();
			}
		}
		return null;
	}

	private void setCookie(HttpServletResponse response, String name, String value, String path, int age) {
		Cookie c = new Cookie(name, value);
		c.setMaxAge(age);
		c.setPath(path);
		response.addCookie(c);
	}

	private void unauthorizedTemplate(HttpServletRequest mrequest, HttpServletResponse response,
	                                  Template found) throws GranException, IOException, TemplateException {

		Map<String, Object> datamap = getDatamap(null, null, mrequest, response, null, found, null);
		response.setContentType("text/html; charset=" + Config.getEncoding());
		response.setHeader("Cache-Control", "public"); //HTTP 1.1
		response.setHeader("Pragma", "public"); //HTTP 1.0
		response.setDateHeader("Expires", 0L); //prevents caching at the proxy server
		String templateFolder = found.getFolder();
		String template = templateFolder + "/unauthorized.ftl";

		AbstractPluginCacheItem pci = PluginCacheManager.getInstance().find(PluginType.WEB, template);
		if (pci != null && ((PluginCacheItem) pci).getText() != null) {
			EmailUtil.buildTemplate(template, datamap, response.getWriter(), PluginType.WEB);
		}
		response.flushBuffer();

	}

	private boolean parseTemplate(SessionContext sc, Uploader u, HttpServletRequest mrequest, HttpServletResponse response, String taskId,
	                              Template found, Object file, Object filterName) throws GranException, IOException, TemplateException {
		Map<String, Object> datamap = getDatamap(sc, u, mrequest, response, new SecuredTaskBean(taskId, sc), found, filterName);

		response.setContentType("text/html; charset=" + Config.getEncoding());
		response.setHeader("Cache-Control", "public"); //HTTP 1.1
		response.setHeader("Pragma", "public"); //HTTP 1.0
		response.setDateHeader("Expires", 0L); //prevents caching at the proxy server
		String templateFolder = found.getFolder();
		String template = templateFolder;
		if (file != null) {
			String fileName = file.toString();
			if (fileName.length() > 0) template = templateFolder + fileName;
			else template = templateFolder + "/index.ftl";
		}
		AbstractPluginCacheItem pci = PluginCacheManager.getInstance().find(PluginType.WEB, template);
		if (pci == null) {
			return false;
		} else if (((PluginCacheItem) pci).getText() != null) {
			EmailUtil.buildTemplate(template, datamap, response.getWriter(), PluginType.WEB);
		}
		response.flushBuffer();
		return true;
	}

	private Map<String, Object> getDatamap(SessionContext sc, Uploader uploader, HttpServletRequest mrequest, HttpServletResponse response, SecuredTaskBean task, Template template, Object filterName) throws GranException {
		Map<String, Object> root = new HashMap<String, Object>();
		try {
			String tz = sc != null ? sc.getTimezone() : Config.getInstance().getDefaultTimezone();
			String locale = sc != null ? sc.getLocale() : Config.getInstance().getDefaultLocale();
			root.put(CHARSET, Config.getEncoding());
			root.put(DATE_FORMATTER, BeansWrapper.getDefaultInstance().wrap(new DateFormatter(tz, locale)));
			TemplateHashModel staticModels = BeansWrapper.getDefaultInstance().getStaticModels();
			TemplateHashModel hf = (TemplateHashModel) staticModels.get("com.trackstudio.tools.formatter.HourFormatter");
			root.put(HOURS_FORMATTER, hf);
			root.put(TASK, BeansWrapper.getDefaultInstance().wrap(task));
			if (filterName != null)
				root.put(DEFAULT_FILTER, filterName);
			String nameTemplate = "unname";
			if (sc != null && template != null) {
				try {
					nameTemplate = URLEncoder.encode(template.getName(), Config.getEncoding());
				} catch (UnsupportedEncodingException e) {
					nameTemplate = "unname";
				}
				root.put(TEMPLATE_NAME, nameTemplate);
				root.put(TEMPLATE_TASK, BeansWrapper.getDefaultInstance().wrap(new SecuredTaskBean(template.getTask().getId(), sc)));
			}
			root.put(CONTEXT_PATH, mrequest.getContextPath());
			root.put(I18N, BeansWrapper.getDefaultInstance().wrap(I18n.getInstance()));
			root.put(UTIL, BeansWrapper.getDefaultInstance().wrap(new TemplateUtil(sc)));
			if (sc != null) {
				ArrayList<String> filterSet = new ArrayList<String>();
				for (SecuredFilterBean prs1 : AdapterManager.getInstance().getSecuredFilterAdapterManager().getTaskFilterList(sc, task.getId())) {
					filterSet.add(prs1.getName());
				}
				Map requestMap = new HashMap();

				HashMap<String, List<String>> values = new HashMap<String, List<String>>();
				HashMap<String, String> value = new HashMap<String, String>();

				Map attributes = new HashMap();
				HashMap<String, String> cookies = new HashMap<String, String>();
				Map<String, List<String>> urequestData = uploader.getParameterMap();
				// first parse Cookies from request
				// first parse Cookies from request
				String currentTrackingCookie = getCookie(mrequest, TS_CURRENT_TRACKING);
				String persistentTrackingCookie = getCookie(mrequest, TS_PERSIST_TRACKING);
				if (currentTrackingCookie == null && persistentTrackingCookie != null) {
					setCookie(response, TS_CURRENT_TRACKING, persistentTrackingCookie, mrequest.getContextPath() + TEMPLATE_PATH + nameTemplate, -1);
					cookies.put(TS_CURRENT_TRACKING, currentTrackingCookie);
				}
				setCookie(response, TS_PERSIST_TRACKING, Long.toString(System.currentTimeMillis()), mrequest.getContextPath() + TEMPLATE_PATH + nameTemplate, 3600 * 24 * 365);

				if (mrequest.getCookies() != null) {
					for (ListIterator i1 = Arrays.asList(mrequest.getCookies()).listIterator(); i1.hasNext(); ) {
						Cookie c = (Cookie) i1.next();
						if (c.getValue() != null && c.getValue().length() > 0) {
							cookies.put(c.getName(), c.getValue());
						}
					}
				}

				for (String paramName : urequestData.keySet()) {

					List<String> paramValue = urequestData.get(paramName);

					values.put(paramName, paramValue);
					if (!paramValue.isEmpty()) value.put(paramName, paramValue.get(0));
					if (paramName.equals("__save_cookies")) {
						for (Object aParamValue : paramValue) {
							String key = aParamValue.toString();
							String val = uploader.getParameter(key);
							if (val != null) {
								Cookie c = new Cookie(key, val);
								c.setMaxAge(60 * 60 * 24 * 365);
								//c.setPath(mrequest.getContextPath() + "/template/");
								c.setPath(mrequest.getContextPath() + TEMPLATE_PATH + nameTemplate);
								response.addCookie(c);
								mrequest.setAttribute(key, val);
								cookies.put(c.getName(), c.getValue());
							}
						}
					}
				}
				for (Enumeration en = mrequest.getAttributeNames(); en.hasMoreElements(); ) {
					String paramName = en.nextElement().toString();
					Object paramValue = mrequest.getAttribute(paramName);
					attributes.put(paramName, paramValue);
				}


				requestMap.put(REQUEST_VALUES, values);
				requestMap.put(REQUEST_VALUE, value);
				requestMap.put(REQUEST_COOKIES, cookies);
				requestMap.put(REQUEST_ATTRIBUTES, attributes);
				root.put(FILTERS, BeansWrapper.getDefaultInstance().wrap(filterSet));

				root.put(REQUEST, requestMap);

				root.put(USER, sc.getUser());
				root.put(SESSION, sc);
				root.put(SC, sc);
			}
			root.put("req", mrequest);
			root.put("resp", response);
			root.put(BSH, BeansWrapper.getDefaultInstance().wrap(CalculatedValue.getInstance()));
			root.put(UPLOADER, uploader);

		} catch (AccessDeniedException acc) {
			// possible error 404 or else?
			throw new UserException("You has no access to this data");
		} catch (TemplateModelException e) {
			throw new GranException(e);
		}
		return root;
	}

	public void doPost(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse) throws IOException, ServletException {
		doGet(httpServletRequest, httpServletResponse);
	}

	public void error404(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse) throws IOException, ServletException {
		httpServletResponse.sendError(404);
	}
}
