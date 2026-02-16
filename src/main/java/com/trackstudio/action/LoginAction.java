package com.trackstudio.action;

import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.TreeSet;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.actions.DispatchAction;

import com.trackstudio.app.adapter.AdapterManager;
import com.trackstudio.app.adapter.auth.SSLCerfAuthAdapter;
import com.trackstudio.app.session.SessionContext;
import com.trackstudio.app.session.SessionManager;
import com.trackstudio.exception.AuthException;
import com.trackstudio.exception.GranException;
import com.trackstudio.exception.UserException;
import com.trackstudio.form.UserForm;
import com.trackstudio.kernel.cache.UserAction;
import com.trackstudio.kernel.cache.UserRelatedInfo;
import com.trackstudio.kernel.cache.UserRelatedManager;
import com.trackstudio.kernel.lock.LockManager;
import com.trackstudio.kernel.manager.KernelManager;
import com.trackstudio.model.Registration;
import com.trackstudio.model.User;
import com.trackstudio.secured.SecuredUserBean;
import com.trackstudio.sman.properties.ConfigFile;
import com.trackstudio.startup.Config;
import com.trackstudio.startup.I18n;
import com.trackstudio.startup.TSLoader;
import com.trackstudio.tools.DetectTimeZone;
import com.trackstudio.tools.DualKey;
import com.trackstudio.tools.Pair;
import com.trackstudio.tools.PasswordValidator;
import com.trackstudio.tools.formatter.DateFormatter;
import com.trackstudio.tools.textfilter.HTMLEncoder;

public class LoginAction extends DispatchAction {
    private static Log log = LogFactory.getLog(LoginAction.class);
    private static final LockManager lockManager = LockManager.getInstance();

    public ActionForward execute(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        try {
            request.setAttribute("method", "page");
            request.setAttribute("compressHtml", "html");
            request.setAttribute("jsAlert", Config.isTurnItOn("trackstudio.show.js.error"));
            return super.execute(mapping, form, request, response);
        } catch (GranException ge) {
            request.setAttribute("javax.servlet.jsp.jspException", ge);
            return mapping.findForward("error");
        }
    }

    protected String getMethodName(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response, String parameter) throws Exception {
        if (request.getParameter(parameter) == null) {
            return "loginPage";
        }
        return request.getParameter(parameter);
    }

    public ActionForward loginPage(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        boolean w = lockManager.acquireConnection();
        try {
            log.trace("##########");
            if (TSLoader.getInitException() != null)
                throw TSLoader.getInitException();
            String dLocale = Config.getInstance().getDefaultLocale();
            request.setAttribute("useX509", ConfigFile.YES.equals(Config.getProperty("trackstudio.useX509.authorization")));
            request.setAttribute("title", "TrackStudio");
            request.setAttribute("helpTopic", I18n.getString(dLocale, "LOGIN_PAGE"));
            request.setAttribute("locale", dLocale);
            request.setAttribute("helpContent", I18n.getString(dLocale, "ACCOUNT_INFO_HELP"));
            request.setAttribute("showForgotPassword", !Config.getInstance().isLDAP() && Config.getInstance().isSendMail());
            request.setAttribute("tsHost", Config.getInstance().isTSHost() ? "true" : null);
            if (Config.getProperty("trackstudio.josso.auth.app") != null) {
                response.sendRedirect(request.getContextPath() + "/app-shell.html");
                return null;
            }

            UserForm uf = (UserForm) form;

            ArrayList<String> sessionIds = SessionContext.getCookies(request, response);
            SessionContext sc = null;
            for (String sessionId : sessionIds) {
                sc = SessionManager.getInstance().getSessionContext(sessionId);
                if (sc != null) break;
            }
            if (sc != null)
                sc.setSessionInCookies(true);


            if (sc != null) {
                String lastPath = uf.getLastPath();
                if (lastPath != null) {
                    HTMLEncoder sb = new HTMLEncoder(URLDecoder.decode(lastPath, Config.getEncoding()));
                    response.sendRedirect(sb.toString());
                    return null;
                }
                log.debug("TRY TO REDIRECT");
                response.sendRedirect(request.getContextPath() + "/app-shell.html");
                return null;
            }


            log.debug("LDO");
            defineObjects(request, response);
            request.setAttribute("showRegister", GeneralAction.getInstance().checkedRoleRegistration());
            request.setAttribute("validateDataBasePrimaryKey", Config.isValidePrimaryKey());

            if (request.getParameter("lastPath") != null)
                uf.setLastPath(request.getParameter("lastPath"));
            log.debug("RET");
            String anonymousUserId = KernelManager.getUser().findByLogin(UserRelatedInfo.ANONYMOUS_USER);
            if (anonymousUserId != null) {
                request.setAttribute("anonymous", UserRelatedManager.getInstance().find(anonymousUserId).isEnabled());
            }
            return mapping.findForward("loginJSP");
        } finally {
            if (w) lockManager.releaseConnection();
        }

    }

    public ActionForward jsNotSupport(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        log.trace("##########");
        return mapping.findForward("jsNotSupport");
    }


    public ActionForward login(ActionMapping mapping, ActionForm form, HttpServletRequest request,
                               HttpServletResponse response) throws Exception {
        boolean w = lockManager.acquireConnection();
        try {
            log.trace("##########");
            UserForm uf = (UserForm) form;
//            System.out.println("Login.jsp Request session id = "+request.getParameter("session"));
//            System.out.println("Login.jsp Request URL = "+request.getRequestURI());
//            System.out.println("Login.jsp Request path = "+request.getPathTranslated()+ " query: "+request.getQueryString());
//            System.out.println("Login.jsp Request String = "+request.toString());
            String lastPath = uf.getLastPath() != null ? URLDecoder.decode(uf.getLastPath(), Config.getEncoding()) : null;
            String login = uf.getLogin() == null ? null : uf.getLogin().trim();
            if (Config.isTurnItOn("trackstudio.security.password.case"))
                login = login.toUpperCase(Locale.ENGLISH);
            String password = uf.getPassword();

            String sessionId = AdapterManager.getInstance().getSecuredUserAdapterManager().authenticate(login, password, request);
            SessionContext sc = SessionManager.getInstance().getSessionContext(sessionId);
            request.setAttribute("tsHost", Config.getInstance().isTSHost() ? "true" : null);

            // cookies might be empty
            /*if (request.getCookies() == null)
                throw new UserException("MSG_COOKIES_DISABLED");
             */
            sc.setCookies(request, response, uf.isRememberMe());
            if (sc.canAction(UserAction.editUserPasswordHimself, sc.getUserId())) {
                SecuredUserBean bean = sc.getUser();
                if (Config.isTurnItOn("trackstudio.security.password.changefirst")) {
                    //System.out.println("force change password");

                    if (bean.getLastLogonDate() == null) {
                        sc.setRequestAttribute(request, "sc", sc);
                        sc.setRequestAttribute(request, "id", sc.getUserId());
                        response.sendRedirect(request.getContextPath() + "/ForceChangePasswordAction.do?method=page&id=" + sc.getUserId() + (lastPath != null ? "&lastPath=" + URLEncoder.encode(lastPath, "UTF-8") : ""));
                        return null;//mapping.findForward("forceChangePasswordPage");//(mapping.findForward("forceChangePasswordPage"));
                    }

                    if (bean.getPasswordChangedDate() != null && bean.getPasswordChangedDate().after(bean.getLastLogonDate())) {
                        sc.setRequestAttribute(request, "sc", sc);
                        sc.setRequestAttribute(request, "id", sc.getUserId());
                        response.sendRedirect(request.getContextPath() + "/ForceChangePasswordAction.do?method=page&id=" + sc.getUserId() + (lastPath != null ? "&lastPath=" + URLEncoder.encode(lastPath, "UTF-8") : ""));
                        return null;//mapping.findForward("forceChangePasswordPage");//(mapping.findForward("forceChangePasswordPage"));
                    }

                }

                String maximumAge = Config.getInstance().getProperty("trackstudio.security.password.maxage");
                if (maximumAge != null && maximumAge.length() > 0) {
                    int maxAge = Integer.parseInt(maximumAge);
                    if (maxAge > 0) {
                        Calendar now = Calendar.getInstance();
                        now.setTimeInMillis(System.currentTimeMillis() - (long) maxAge * 3600L * 24L * 1000L);
                        if (bean.getPasswordChangedDate() == null || bean.getPasswordChangedDate().before(now)) {
                            sc.setRequestAttribute(request, "sc", sc);
                            sc.setRequestAttribute(request, "id", sc.getUserId());
                            response.sendRedirect(request.getContextPath() + "/ForceChangePasswordAction.do?method=page&id=" + sc.getUserId() + (lastPath != null ? "&lastPath=" + URLEncoder.encode(lastPath, "UTF-8") : ""));
                            return null;//mapping.findForward("forceChangePasswordPage");//(mapping.findForward("forceChangePasswordPage"));
                        }
                    }
                }
            }

            if (lastPath != null && lastPath.length() > 0 && lastPath.indexOf("app-shell.html") == -1 && (lastPath.indexOf("/task/") != -1 || lastPath.indexOf("/user/") != -1 || lastPath.indexOf("id=") != -1)) {
                HTMLEncoder en = new HTMLEncoder(lastPath);
                StringBuffer sb = en.getResult();
                if (sb.indexOf("?") > -1) {

                    sb.append("&amp;thisframe=true");
                } else {
                    int pos = sb.indexOf("&");
                    if (pos == -1) {
                        sb.append("?thisframe=true");
                    } else {
                        sb.replace(pos, pos + 1, "?");
                        sb.append("&amp;thisframe=true");
                    }
                }
                /*if(!remember)
                    sb.append("&amp;session=").append(sessionId);
                    */
                sc.setAttribute("mainFrameGoTo", sb.toString());
                response.sendRedirect(sb.toString());

                return null;
            }
            AdapterManager.getInstance().getSecuredUserAdapterManager().updateLastLogonDate(sc, sc.getUserId());
            response.sendRedirect(request.getContextPath() + "/app-shell.html");

            return null;
        } catch (UserException ue) {
            if (ue.getActionMessages() != null) {
                saveMessages(request, ue.getActionMessages());
                return mapping.findForward("loginPage");
            } else
                throw ue;
        } finally {
            if (w) lockManager.releaseConnection();
        }
    }

    public ActionForward forgotPasswordPage(ActionMapping mapping, ActionForm form, HttpServletRequest request,
                                            HttpServletResponse response) throws Exception {
        boolean w = lockManager.acquireConnection();
        try {
            log.trace("##########");
            defineObjects(request, response);
            String defaultLocale = Config.getInstance().getDefaultLocale();
            request.setAttribute("title", "TrackStudio");
            request.setAttribute("helpTopic", I18n.getString(defaultLocale, "HELP_TOPIC"));
            request.setAttribute("helpContent", I18n.getString(defaultLocale, "FORGOT_PASSWORD_HELP"));

            return mapping.findForward("forgotPasswordJSP");
        } finally {
            if (w) lockManager.releaseConnection();
        }

    }

    public ActionForward forgotPassword(ActionMapping mapping, ActionForm form, HttpServletRequest request,
                                        HttpServletResponse response) throws Exception {
        boolean w = lockManager.acquireConnection();
        try {
            log.trace("##########");
            UserForm uf = (UserForm) form;
            request.setAttribute("tsHost", Config.getInstance().isTSHost() ? "true" : null);
            AdapterManager.getInstance().getSecuredUserAdapterManager().forgotPassword(uf.getLogin(), uf.getEmail());

            return mapping.findForward("loginPage");
        } catch (UserException ue) {
            if (ue.getActionMessages() != null) {
                saveMessages(request, ue.getActionMessages());
                setLocale(request, new Locale("en", "US"));
                return mapping.findForward("forgotPasswordPage");
            } else
                throw ue;
        } finally {
            if (w) lockManager.releaseConnection();
        }
    }

    public ActionForward logoutPage(ActionMapping mapping, ActionForm form, HttpServletRequest request,
                                    HttpServletResponse response) throws Exception {
        boolean w = lockManager.acquireConnection();
        try {
            log.trace("##########");
            SessionContext sc = GeneralAction.getInstance().imports(request, response);
            if (sc != null) {
                AdapterManager.getInstance().getSecuredUserAdapterManager().updateLastLogonDate(sc, sc.getUserId());
                SessionManager.getInstance().remove(sc.getId());
            }
            defineObjects(request, response);
            log.debug("INVALIDATE COOKIES!");
            SessionContext.resetCookies(request, response);
            request.removeAttribute("session");
            request.getSession().setAttribute("autologin", null);
            request.getSession().setAttribute("autopassword", null);
            //response.setHeader("WWW-Authenticate", "BASIC realm=\"users\"");
            //response.sendError(HttpServletResponse.SC_UNAUTHORIZED);
            return mapping.findForward("logoutJSP");
        } finally {
            if (w) lockManager.releaseConnection();
        }

    }


    public ActionForward registerInfoPage(ActionMapping mapping, ActionForm form, HttpServletRequest request,
                                          HttpServletResponse response) throws Exception {
        boolean w = lockManager.acquireConnection();
        try {
            log.trace("##########");
            defineObjects(request, response);
            log.debug("RegisterAction.jsp");
            log.debug("INVALIDATE COOKIES!");
            String locale = Config.getInstance().getDefaultLocale();
            request.setAttribute("title", "TrackStudio");
            request.setAttribute("helpTopic", I18n.getString(locale, "REGISTER_SUCCESSFULLY"));
            request.setAttribute("helpContent", I18n.getString(locale, "MAILBOX_MESSAGE"));
            SessionContext.resetCookies(request, response);
            return mapping.findForward("registerInfoJSP");
        } finally {
            if (w) lockManager.releaseConnection();
        }

    }

    public ActionForward sslcerf(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        boolean w = lockManager.acquireConnection();
        try {
            log.trace("##########");
            SSLCerfAuthAdapter adapter = new SSLCerfAuthAdapter();
            if (adapter.authorizeImpl(null, null, true, request)) {
                String userScId = SessionManager.getInstance().create(adapter.getUser());
                SessionContext sc = SessionManager.getInstance().getSessionContext(userScId);
                AdapterManager.getInstance().getSecuredUserAdapterManager().updateLastLogonDate(sc, sc.getUserId());
                response.sendRedirect(request.getContextPath() + "/app-shell.html");
                return null;
            } else {
                saveMessages(request, new AuthException().getActionMessages());
                return mapping.findForward("loginPage");
            }
        } finally {
            if (w) lockManager.releaseConnection();
        }

    }

    public ActionForward register(ActionMapping mapping, ActionForm form, HttpServletRequest request,
                                  HttpServletResponse response) throws Exception {
        UserForm uf = (UserForm) form;
        boolean w = lockManager.acquireConnection();

        try {
            log.trace("##########");
            request.setCharacterEncoding(Config.getEncoding());
            String registration = uf.getRegistration();
            String login = uf.getLogin();
            String name = uf.getName();
            String email = uf.getEmail();
            if (registration == null || registration.equals("0") || registration.equals(I18n.getString("NOT_CHOOSEN")) || login == null || name == null || email == null || login.length() == 0 || name.length() == 0 || email.length() == 0)
                return mapping.findForward("loginPage");
            else {
                String zone = uf.getTimezone();
                if (zone != null) {
                    zone = DetectTimeZone.getCurrentTimeZone(zone);
                }
                AdapterManager.getInstance().getSecuredRegistrationAdapterManager().register(null, login, name, email, uf.getLocale(), zone, uf.getCompany(), uf.getRegistration());
                return mapping.findForward("registerInfoPage");
            }
        } catch (UserException ue) {
            if (ue.getActionMessages() != null) {
                saveMessages(request, ue.getActionMessages());
                setLocale(request, new Locale("en", "US"));
                return mapping.findForward("registerPage");
            } else
                throw ue;
        } finally {
            if (w) lockManager.releaseConnection();
        }
    }

    public ActionForward registerPage(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {

        boolean w = lockManager.acquireConnection();
        try {
            log.trace("##########");
            defineObjects(request, response);
            String locale = Config.getInstance().getDefaultLocale();
            request.setAttribute("title", "TrackStudio");
            request.setAttribute("helpTopic", I18n.getString(locale, "HELP_TOPIC"));
            request.setAttribute("helpContent", I18n.getString(locale, "REGISTER_HELP"));
            request.setAttribute("mustChoose", "mustChoose(" + I18n.getString(locale, "CHOOSE_PROJECT") + ")ifNoEmpty(TERM_LOGIN)");
            UserForm uf = (UserForm) form;
            if (request.getAttribute("form") != null) {
                UserForm prevForm = (UserForm) request.getAttribute("form");
                uf.setLocale(prevForm.getLocale());
                uf.setTimezone(prevForm.getTimezone());
            } else {
                uf.setLocale(Config.getInstance().getDefaultLocale());
                uf.setTimezone(Config.getInstance().getDefaultTimezone());
            }

            TreeSet<Pair> locales = Config.getInstance().getAvailableLocales(DateFormatter.toLocale(Config.getInstance().getDefaultLocale()));
            request.setAttribute("timezones", DetectTimeZone.getTimeZone());
            request.setAttribute("locales", locales);

            if (uf.getProject() != null) {
                for (Registration reg : KernelManager.getRegistration().getAllRegistrastionList()) {
                    DualKey dk = reg.getDualKey();
                    if (uf.getProject().equals(dk.getRightPart())) {
                        request.setAttribute("prj", dk);
                        return mapping.findForward("registerJSP");
                    }
                }
            }
            List<Registration> registrationList = KernelManager.getRegistration().getPublicRegistrationList();
            if (registrationList == null)
                return mapping.findForward("loginPage");
            registrationList = new ArrayList<Registration>(new TreeSet<Registration>(registrationList));
            if (registrationList.isEmpty())
                return mapping.findForward("loginPage");
            //List<User> users = new ArrayList<User>();
            HashMap<String, String> defaultLocales = new HashMap<String, String>();
            HashMap<String, String> defaultZones = new HashMap<String, String>();

            for (Registration r : registrationList) {
                User user = KernelManager.getFind().findUser(r.getUser().getId());
                defaultLocales.put(user.getId(), user.getLocale());
                defaultZones.put(user.getId(), user.getTimezone());
                //users.add(user);
            }

            request.setAttribute("registrationList", registrationList);
            request.setAttribute("defaultLocales", defaultLocales);
            request.setAttribute("defaultTimezones", defaultZones);

            return mapping.findForward("registerJSP");
        } finally {
            if (w) lockManager.releaseConnection();
        }

    }


    public static void defineObjects(HttpServletRequest request, HttpServletResponse response) throws Exception {
        boolean w = lockManager.acquireConnection();
        try {
            log.trace("##########");
            response.setContentType("text/html; charset=" + Config.getEncoding());
            response.setHeader("Cache-Control", "public"); //HTTP 1.1
            response.setHeader("Pragma", "public"); //HTTP 1.0
            response.setDateHeader("Expires", 0L); //prevents caching at the proxy server

            request.setAttribute("contextPath", request.getContextPath());
            request.setAttribute("versionPath", Config.getVersionPath());
            request.setAttribute("config", Config.getInstance());
            request.setAttribute("defaultLocale", Config.getInstance().getDefaultLocale());
            request.setAttribute("tsHost", Config.getInstance().isTSHost() ? "true" : null);
            request.setAttribute("ImageServlet", "/ImageServlet/" + GeneralAction.SERVLET_KEY);

            request.setAttribute("request", request); //this line is important!!!
            request.setAttribute("response", response);
        } finally {
            if (w) lockManager.releaseConnection();
        }

    }
}
