package com.trackstudio.rest;

import org.json.JSONObject;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Map;
import java.util.function.BiFunction;

public class NotFoundRest implements RestService {
    @Override
    public String service(HttpServletRequest req, HttpServletResponse resp) {
        JSONObject jo = new JSONObject();
        jo.put("error", "path not found");
        jo.put("path", req.getPathInfo());
        return jo.toString();
    }

    @Override
    public Map<String, BiFunction<HttpServletRequest, HttpServletResponse, String>> actions() {
        throw new IllegalStateException("Method not supported");
    }
}
