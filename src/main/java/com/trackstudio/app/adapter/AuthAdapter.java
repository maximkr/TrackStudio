package com.trackstudio.app.adapter;

import javax.servlet.http.HttpServletRequest;

import com.trackstudio.exception.GranException;

/**
 * Данный интерфейс может быть использован для реализации
 * различных методов аутентификации (например, через LDAP).
 * За счет объединения адаптеров в pipeline можно последовательно
 * применять различные методы аутентификации.
 * Например, можно сначала попробовать выполнить аутентификацию
 * через LDAP, а если не получилось - использовать
 * встроенный метод.
 * Вне зависимости от используемого метода пользователь
 * должен быть создан в системе (должна быть запись в
 * gr_user).
 */
public interface AuthAdapter extends Adapter {
    /**
     * TrackStudio вызывает этот метод для авторизации пользователя
     *
     * @param userId   ID пользователя
     * @param password пароль
     * @param result   результат авторизации
     * @param request is needed only for SSL adapter
     * @return TRUE - если авторизация прошла удачно, FALSE - если нет
     * @throws GranException при необходимости
     */
    boolean authorizeImpl(String userId, String password, boolean result, HttpServletRequest request) throws GranException;

    /**
     * Метод Вызывается когда пользователь меняет пароль.
     *
     * @param userId   ID пользователя
     * @param password пароль
     * @throws GranException при необходимости
     */
    void changePasswordImpl(String userId, String password) throws GranException;
}
