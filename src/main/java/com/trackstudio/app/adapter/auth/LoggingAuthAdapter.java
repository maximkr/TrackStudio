package com.trackstudio.app.adapter.auth;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.trackstudio.app.adapter.AuthAdapter;
import com.trackstudio.exception.GranException;
import com.trackstudio.kernel.cache.UserRelatedInfo;
import com.trackstudio.kernel.cache.UserRelatedManager;

import net.jcip.annotations.Immutable;

/**
 * Класс авторизации при логине
 */
@Immutable
public class LoggingAuthAdapter implements AuthAdapter {

    private static final Log log = LogFactory.getLog(LoggingAuthAdapter.class);

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
        return "Logging Authentication Adapter";
    }

    /**
     * Производит авторизацию
     *
     * @param userId   ID пользователя
     * @param password пароль
     * @param result   Результат авторизации
     * @return TRUE - если авторизация прошла удачно, FALSE - если нет
     * @throws GranException при необходимости
     */
    public boolean authorizeImpl(String userId, String password, boolean result, HttpServletRequest request) throws GranException {
        UserRelatedInfo user = UserRelatedManager.getInstance().find(userId);
        log.trace("authorize");
        log.debug("login=" + user.getLogin());
        log.debug("password=" + password);
        log.debug("result=" + result);
        return result;
    }

    /**
     * Меняет пароль пользователя. Реализация не нужна
     *
     * @param userId   ID пользователя
     * @param password пароль
     * @throws GranException при необходимости
     */
    public void changePasswordImpl(String userId, String password) throws GranException {
        log.trace("changePassword");
        //log.debug("login=" + user.getLogin());
        log.debug("New password:" + password);
    }

}
