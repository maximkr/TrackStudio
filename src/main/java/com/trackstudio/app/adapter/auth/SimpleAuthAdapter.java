package com.trackstudio.app.adapter.auth;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.trackstudio.app.adapter.AuthAdapter;
import com.trackstudio.exception.GranException;
import com.trackstudio.kernel.cache.UserRelatedInfo;
import com.trackstudio.kernel.cache.UserRelatedManager;
import com.trackstudio.kernel.manager.KernelManager;
import com.trackstudio.model.Prstatus;
import com.trackstudio.startup.Config;
import com.trackstudio.tools.MD5;
import com.trackstudio.tools.PasswordValidator;

import net.jcip.annotations.Immutable;

/**
 * Класс обычной авторизации
 */

@Immutable
public class SimpleAuthAdapter implements AuthAdapter {

    private static final Log log = LogFactory.getLog(SimpleAuthAdapter.class);

    /**
     * Инициализирует адаптер
     *
     * @return TRUE - инициализация прошла успешно, FALSE - нет
     */
    public boolean init() {
        return true;
    }

    /**
     * Возвращает текстовое описание адаптера
     *
     * @return adapter's description
     */
    public String getDescription() {
        return "Basic Database Authentication Adapter";
    }

    /**
     * Производит авторизацию
     *
     * @param user   пользователь
     * @param password пароль
     * @return TRUE - если авторизация прошла удачно, FALSE - если нет
     * @throws GranException при необходимости
     */
    public boolean authorizeLocal(UserRelatedInfo user, String password) throws GranException {

        try {
            String s = "";
            if (user != null && user.getPassword() != null) {
                s = user.getPassword().substring(0, PasswordValidator.END_INDEX);
            }
            String anotherString = MD5.encode(password);
//            log.debug("password check "+ s + " "+ anotherString);
            return s.compareTo(anotherString) == 0;
        } catch (Exception e) {
            throw new GranException(e);
        }
    }

    public boolean authorizeImpl(String userId, String password, boolean result, HttpServletRequest request) throws GranException {
        ArrayList<String> allowed = null;
        ArrayList<String> denied = null;

        log.trace("authorize");
        String allowedString = Config.getInstance().getProperty("trackstudio.security.allowed");
        String deniedString = Config.getInstance().getProperty("trackstudio.security.denied");

        if (allowedString != null) {
            allowed = new ArrayList<String>();
            try {
                String res = new String(allowedString.getBytes("ISO-8859-1"), "UTF-8");
                for (String a : res.split(";")) {
                    allowed.add(a);
                }
            } catch (UnsupportedEncodingException e) {
                log.error("Encoding exception", e);
            }

        }
        if (deniedString != null) {
            denied = new ArrayList<String>();
            try {
                String res = new String(deniedString.getBytes("ISO-8859-1"), "UTF-8");
                for (String a : res.split(";")) {
                    denied.add(a);
                }
            } catch (UnsupportedEncodingException e) {
                log.error("Encoding exception", e);
            }
        }
        if (result) return result;
        UserRelatedInfo user = UserRelatedManager.getInstance().find(userId);
        Prstatus check = KernelManager.getFind().findPrstatus(user.getPrstatusId());
        if (allowed == null && denied == null) return authorizeLocal(user, password);

        if (denied != null && denied.contains(check.getName())) return false;
        if (allowed != null && allowed.contains(check.getName())) {
            return authorizeLocal(user, password);
        } else if (allowed == null) return authorizeLocal(user, password);
        else return false;
    }

    /**
     * Меняет пароль пользователя. Реализация не нужна
     *
     * @param userId   ID пользователя
     * @param password пароль
     * @throws GranException при необходимости
     */
    public void changePasswordImpl(String userId, String password) throws GranException {
        KernelManager.getUser().changePassword(userId, password);
    }
}