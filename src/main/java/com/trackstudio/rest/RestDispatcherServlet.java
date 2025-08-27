package com.trackstudio.rest;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Map;

public class RestDispatcherServlet extends HttpServlet {
    private final static NotFoundRest NOT_FOUND = new NotFoundRest();
    private final Map<String, RestService> services = new HashMap<>();

    public RestDispatcherServlet() {
        services.put("/task", new TaskRest());
        services.put("/auth", new AuthRest());
        services.put("/user", new UserRest());
    }

    @Override
    protected void service(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        resp.setContentType("application/json");
        resp.setCharacterEncoding("UTF-8");
        String path = req.getPathInfo();
        String json = services.getOrDefault(path, NOT_FOUND).service(req, resp);
        resp.getOutputStream().write(json.getBytes("UTF-8"));
    }
}
