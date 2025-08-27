package com.trackstudio.rest;

import org.json.JSONObject;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.function.BiFunction;

public class NotFoundAction implements BiFunction<HttpServletRequest, HttpServletResponse, String> {

    public static final NotFoundAction NOT_FOUND_ACTION = new NotFoundAction();

    private NotFoundAction() {
    }

    @Override
    public String apply(HttpServletRequest req, HttpServletResponse resp) {
        JSONObject jo = new JSONObject();
        jo.put("error", "action not found");
        jo.put("action", req.getParameter("action"));
        return jo.toString();
    }
}
