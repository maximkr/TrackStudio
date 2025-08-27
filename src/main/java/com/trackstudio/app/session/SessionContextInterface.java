package com.trackstudio.app.session;

import com.trackstudio.exception.GranException;
import com.trackstudio.secured.SecuredUserBean;

/**
 * Интерфейс, на основании которого создаются классы с пользовательскими сессиями
 */
public interface SessionContextInterface {
    /**
     * Возвращает сессию
     *
     * @return сессия
     */
    String getSession();

    /**
     * Возвращает пользователя
     *
     * @return пользователь
     * @throws GranException при необходимости
     */
    SecuredUserBean getUser() throws GranException;

    /**
     * Возвращает ID пользователя
     *
     * @return ID пользователя
     */
    String getUserId();

    /**
     * Возвращает ID пользователя
     * если toUserId - child loggedUser-a, то возвращаем loggedUserId, иначе - toUserId
     * используется для создания объектов (script/status/registration/etc), для юзеров
     * находящихся в другой ветке дерева, но на которых есть права.
     *
     * @param toUserId ID пользователя
     * @return ID пользователя
     * @throws GranException при необходимости
     */
    String getUserId(String toUserId) throws GranException;

    /**
     * Возвращает ID сессии
     *
     * @return ID сессии
     */
    String getId();

    /**
     * Проверяет есть ли доступ залогиненного пользователя к указанной задаче посредством прав доступа
     *
     * @param taskid ID задачи
     * @return TRUE - если доступ есть, FALSE - если нет
     * @throws GranException при необходимости
     */
    boolean allowedByACL(String taskid) throws GranException;

    /**
     * Проверяет есть ли доступ залогиненного пользователя к указанномупользователю посредством прав доступа
     *
     * @param userid ID пользователя
     * @return TRUE - если доступ есть, FALSE - если нет
     * @throws GranException при необходимости
     */
    boolean allowedByUser(String userid) throws GranException;

    /**
     * Воззвращает локаль пользователя
     *
     * @return локаль пользователя
     * @throws GranException при необходимости
     */
    String getLocale() throws GranException;

    /**
     * Возвращает таймзону пользователя
     *
     * @return таймзона
     * @throws GranException при необходимости
     */
    String getTimezone() throws GranException;

    /**
     * Проверяет может ли залогиненный пользователь просматривать указанную задачу
     *
     * @param taskid ID задачи
     * @return TRUE - может, FALSE - нет
     * @throws GranException при необходимости
     */
    boolean taskOnSight(String taskid) throws GranException;

    /**
     * Проверяет может ли залогиненный пользователь просматривать указанного пользователя
     *
     * @param userid ID пользователя
     * @return TRUE - может, FALSE - нет
     * @throws GranException при необходимости
     */
    boolean userOnSight(String userid) throws GranException;

    /**
     * Возвращает атрибут из сессии пользователя
     *
     * @param key ключ
     * @return значение
     */
    Object getAttribute(String key);
}