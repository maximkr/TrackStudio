package com.trackstudio.rest;

import com.trackstudio.app.session.SessionContext;
import com.trackstudio.app.session.SessionManager;
import com.trackstudio.exception.GranException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Map;
import java.util.function.BiFunction;

public interface RestService {
    default String service(HttpServletRequest req, HttpServletResponse resp) {
        String action = req.getParameter("action");
        return actions().getOrDefault(action, NotFoundAction.NOT_FOUND_ACTION).apply(req, resp);
    }

    Map<String, BiFunction<HttpServletRequest, HttpServletResponse, String>> actions();

    default SessionContext session(HttpServletRequest req, HttpServletResponse resp) throws GranException {
        String sessionId = req.getParameter("sessionId");
        SessionContext sc = SessionManager.getInstance().getSessionContext(sessionId);
        if (sc == null) {
            resp.setStatus(401);
            throw new GranException("session id not found: " + sessionId);
        }
        return sc;
    }
}
