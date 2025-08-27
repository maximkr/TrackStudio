package com.trackstudio.app.adapter.store;

import java.text.SimpleDateFormat;
import java.util.Calendar;

import com.trackstudio.exception.GranException;
import com.trackstudio.startup.I18n;

import net.jcip.annotations.ThreadSafe;

@ThreadSafe
public class ResultImport {
    private volatile Message msg = Message.DOES_NOT_APPROPRIATE_FOR_IMPORT_RULE;
    private final StringBuilder text = new StringBuilder("<br/>");
    public final static SimpleDateFormat dateformatter = new SimpleDateFormat("kk:mm:ss dd.MM.yyyy");

    public void appendTxtLn(String value) {
        this.text.append(dateformatter.format(Calendar.getInstance().getTime())).append(" : ").append(value).append("<br/>");
    }

    public enum Message {
        OK("OK"),
        CURRENT_MESSAGE_NULL("CURRENT_MESSAGE_NULL"),
        CURRENT_MESSAGE_FROM_NULL("CURRENT_MESSAGE_FROM_NULL"),
        CURRENT_MESSAGE_FROM_USER_NULL("CURRENT_MESSAGE_FROM_USER_NULL"),
        DOES_EXIST_MAIN_IMPORT("DOES_EXIST_MAIN_IMPORT"),
        DOES_NOT_APPROPRIATE_FOR_IMPORT_RULE("DOES_NOT_APPROPRIATE_FOR_IMPORT_RULE"),
        EXTERNAL_ERROR("external error");

        private final String message;

        Message(String message) {
            this.message = message;
        }

        public String getMessage(String locale) throws GranException {
            return I18n.getString(locale, message);
        }
    }

    public void setMsg(Message msg) {
        this.msg = msg;
    }

    public String getText() {
        return this.text.toString();
    }

    public Message getMsg() {
        return msg;
    }
}
