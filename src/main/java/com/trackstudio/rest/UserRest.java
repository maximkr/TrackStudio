package com.trackstudio.rest;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.BiFunction;

import com.trackstudio.app.Slider;
import com.trackstudio.app.adapter.AdapterManager;
import com.trackstudio.app.filter.FilterSettings;
import com.trackstudio.app.filter.UserFValue;
import com.trackstudio.app.session.SessionContext;
import com.trackstudio.kernel.lock.LockManager;
import com.trackstudio.secured.SecuredFilterBean;
import com.trackstudio.secured.SecuredUserBean;
import com.trackstudio.soap.bean.FilterBean;
import com.trackstudio.soap.bean.UserBean;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * {@inheritDoc}
 */
public class UserRest implements RestService {
    protected static final LockManager lockManager = LockManager.getInstance();
    private final Map<String, BiFunction<HttpServletRequest, HttpServletResponse, String>> actions = new HashMap<>();

    public UserRest() {
        actions.put("login", this::auth);
    }

    public String auth(HttpServletRequest req, HttpServletResponse resp) {
        boolean w = lockManager.acquireConnection();
        try {
            return getSessionContext(req.getParameter("login"), req.getParameter("password")).getId();
        } finally {
            if (w) lockManager.releaseConnection();
        }
    }

    private SessionContext getSessionContext(String login, String password) {
        return null;
    }

    /**
     * {@inheritDoc}
     */
    public UserBean getUserByLogin(String userLogin, String login, String password) throws Exception {
        boolean w = lockManager.acquireConnection();
        try {
            SessionContext sc = getSessionContext(login, password);
            return findUser(sc, userLogin).getSOAP();
        } finally {
            if (w) lockManager.releaseConnection();
        }
    }

    /**
     * This method returns the user or throws the exception
     * @param sc SessionContext
     * @param userLogin searched user login
     * @return user or exception
     * @throws Exception if user is not found
     */
    private SecuredUserBean findUser(SessionContext sc, String userLogin) throws Exception {
        SecuredUserBean user = AdapterManager.getInstance().getSecuredUserAdapterManager().findByLogin(sc, userLogin);
        if (user == null) {
            throw new Exception("User " + userLogin + " is not found." );
        }
        return user;
    }

    /**
     * {@inheritDoc}
     */
    public List<UserBean> getSubusersByLogin(String userLogin, String login, String password) throws Exception {
        boolean w = lockManager.acquireConnection();
        try {
            SessionContext sc = getSessionContext(login, password);
            List<UserBean> userBeans = new ArrayList<UserBean>();
            for (SecuredUserBean subuser : findUser(sc, userLogin).getChildren()) {
                userBeans.add(subuser.getSOAP());
            }
            return userBeans;
        } finally {
            if (w) lockManager.releaseConnection();
        }
    }

    /**
     * {@inheritDoc}
     */
    public List<FilterBean> getFilterUsersByLogin(String userLogin, String login, String password) throws Exception {
        boolean w = lockManager.acquireConnection();
        try {
            SessionContext sc = getSessionContext(login, password);
            SecuredUserBean user = findUser(sc, userLogin);
            List<SecuredFilterBean> filters = AdapterManager.getInstance().getSecuredFilterAdapterManager().getAllUserFilterList(sc, user.getId());
            List<FilterBean> filterBeans = new ArrayList<FilterBean>();
            for (SecuredFilterBean filter : filters) {
                filterBeans.add(filter.getSOAP());
            }
            return filterBeans;
        } finally {
            if (w) lockManager.releaseConnection();
        }
    }

    /**
     * {@inheritDoc}
     */
    public List<UserBean> getUsersByFilter(String userLogin, String filter, String login, String password) throws Exception {
        boolean w = lockManager.acquireConnection();
        try {
            SessionContext sc = getSessionContext(login, password);
            SecuredUserBean user = findUser(sc, userLogin);
            UserFValue val = AdapterManager.getInstance().getSecuredFilterAdapterManager().getUserFValue(sc, filter).getFValue();
            FilterSettings filterSettings = new FilterSettings(val, user.getId(), filter);
            Slider<SecuredUserBean> usl = AdapterManager.getInstance().getSecuredUserAdapterManager().getUserList(sc, user.getId(), (UserFValue) filterSettings.getSettings(), -1, true, null);
            List<UserBean> users = new ArrayList<UserBean>();
            for (SecuredUserBean fuser : usl.getOriginalList()) {
                users.add(fuser.getSOAP());
            }
            return users;
        } finally {
            if (w) lockManager.releaseConnection();
        }
    }

    @Override
    public Map<String, BiFunction<HttpServletRequest, HttpServletResponse, String>> actions() {
        return actions;
    }
}
