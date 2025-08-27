package com.trackstudio.app.adapter.auth;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.trackstudio.app.adapter.AuthAdapter;
import com.trackstudio.exception.GranException;
import com.trackstudio.exception.UserException;

import net.jcip.annotations.Immutable;

/**
 * Класс менеджер адаптеров авторизации
 */
@Immutable
public class AuthAdapterManager {
    private static final Log log = LogFactory.getLog(AuthAdapterManager.class);
    private final CopyOnWriteArrayList<AuthAdapter> am = new CopyOnWriteArrayList<AuthAdapter>();

    /**
     * Конструктор
     *
     * @param adapters адаптер
     */
    public AuthAdapterManager(List<AuthAdapter> adapters) {
        am.clear();
        am.addAll(adapters);
    }

    /**
     * Производит авторизацию
     *
     * @param userId   ID пользователя
     * @param password пароль
     * @param request is needed only for SSL adapter
     * @return TRUE - если авторизация прошла удачно, FALSE - если нет
     * @throws GranException при необходимости
     */
    public boolean authorize(String userId, String password, HttpServletRequest request) throws GranException {
        boolean result = false;
        String exceptionMessage = "";
        for (AuthAdapter adp : am) {
            try {
                result = adp.authorizeImpl(userId, password, result, request);
                log.debug(" josso adapter : " + adp.getClass() + " result : " + result);
                if (result)
                    break;
            } catch (Exception e) {
                log.error("authorize", e);
                exceptionMessage += e.getMessage() + "<br>";
            }
        }
        if (!result && !exceptionMessage.isEmpty()) {
            throw new UserException(exceptionMessage, false);
        }
        return result;
    }

    /**
     * Меняет пароль пользователя
     *
     * @param userId   ID пользователя
     * @param password пароль
     * @throws GranException при необходимости
     */
    public void changePassword(String userId, String password) throws GranException {
        for (AuthAdapter adp : am) {
            adp.changePasswordImpl(userId, password);
        }
    }
}