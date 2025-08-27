package com.trackstudio.app.filter.customizer;

import java.io.Serializable;

import javax.servlet.http.HttpServletRequest;

import com.trackstudio.app.filter.FValue;
import com.trackstudio.app.session.SessionContext;
import com.trackstudio.common.FieldMap;
import com.trackstudio.exception.GranException;

public class CheckboxCustomizer extends Customizer implements Serializable {

    /**
     * Конструктор
     *
     * @param map карта полей
     */
    public CheckboxCustomizer(FieldMap map) {
        super(map);
    }

    @Override
    public String draw(SessionContext sc, FValue filter, String contextPath) throws GranException {
        return null;
    }

    @Override
    public void setFilter(SessionContext sc, HttpServletRequest request, FValue filter) throws GranException {
        String checked = request.getParameter(map.getFilterKey());
        if ("true".equals(checked) || "on".equals(checked)) {
            filter.set(FValue.SUBTASK, "1");
        } else {
            filter.remove(FValue.SUBTASK);
        }
    }

    @Override
    public String drawInput(SessionContext sc, FValue filter, String contextPath) throws GranException {
        StringBuffer outp = new StringBuffer(200);
        boolean checked = "1".equals(filter.getAsString(FValue.SUBTASK));
        outp.append("<td><input type=\"checkbox\" name=\"").append(map.getFilterKey()).append("\"").append(checked ? " checked " : "").append("></td>");
        return outp.toString();
    }
}
