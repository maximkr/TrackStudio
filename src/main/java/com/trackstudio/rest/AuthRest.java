package com.trackstudio.rest;

import com.trackstudio.app.adapter.AdapterManager;
import com.trackstudio.app.session.SessionContext;
import com.trackstudio.app.session.SessionManager;
import com.trackstudio.exception.GranException;
import com.trackstudio.kernel.lock.LockManager;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.json.JSONObject;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.HashMap;
import java.util.Map;
import java.util.function.BiFunction;

public class AuthRest implements RestService {
    private final Log log = LogFactory.getLog(TaskRest.class);

    private static final LockManager lockManager = LockManager.getInstance();

    private final Map<String, BiFunction<HttpServletRequest, HttpServletResponse, String>> actions = new HashMap<>();

    public AuthRest() {
        actions.put("login", this::auth);
        actions.put("session", this::currentUser);
    }

    private String currentUser(HttpServletRequest req, HttpServletResponse resp) {
        JSONObject jo = new JSONObject();
        try {
            SessionContext sc = session(req, resp);
            jo.put("user", new JSONObject(sc.getUser().getSOAP()));
        } catch (GranException e) {
            log.error("Rest auth", e);
            jo.put("error", e.getMessage());
        }
        return jo.toString();
    }


    private String auth(HttpServletRequest req, HttpServletResponse resp) {
        JSONObject jo = new JSONObject();
        try {
            String login = req.getParameter("login");
            String password = req.getParameter("password");
            String sessionId = AdapterManager.getInstance().getSecuredUserAdapterManager().authenticate(login, password, null);
            SessionContext sc = SessionManager.getInstance().getSessionContext(sessionId);
            jo.put("sessionId", sc.getId());
        } catch (GranException e) {
            resp.setStatus(401);
            log.error("Rest auth", e);
            jo.put("error", e.getMessage());
        }
        return jo.toString();
    }

    @Override
    public Map<String, BiFunction<HttpServletRequest, HttpServletResponse, String>> actions() {
        return actions;
    }
}
