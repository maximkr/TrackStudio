package com.trackstudio.securedkernel;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.trackstudio.app.session.SessionContext;
import com.trackstudio.exception.GranException;
import com.trackstudio.exception.InvalidParameterException;
import com.trackstudio.kernel.manager.KernelManager;
import com.trackstudio.secured.SecuredBookmarkBean;
import com.trackstudio.tools.SecuredBeanUtil;

import net.jcip.annotations.Immutable;

/**
 * Класс BookmarkManager содержит методы для работы с закладками.<br/>
 * Закладки могут быть созданы для задач и пользователей.
 */
@Immutable
public class SecuredBookmarkAdapterManager {

    private final static Log log = LogFactory.getLog(SecuredBookmarkAdapterManager.class);

    /**
     * Создание новой закладки с указанным названием, датой и фильтром для задачи или пользователя
     *
     * @param sc         сессия пользователя
     * @param name       Название закладки
     * @param createdate Дата создания
     * @param filterId   Фильтр
     * @param taskId     Задача
     * @param userId     Пользователь
     * @param ownerId    Автор закладки
     * @return ID созданной закладки
     * @throws GranException при необходимости
     */
    public String createBookmark(SessionContext sc, String name, Calendar createdate, String filterId, String taskId, String userId, String ownerId) throws GranException {
        log.trace("createBookmark");
        if (sc == null)
            throw new InvalidParameterException(this.getClass(), "createBookmark", "sc", sc);
        if (ownerId == null)
            throw new InvalidParameterException(this.getClass(), "createBookmark", "owner", sc);
        if (taskId == null && userId == null)
            throw new InvalidParameterException(this.getClass(), "createBookmark", "taskId == null && userId == null", sc);
        if (name == null || name.equals(""))
            throw new InvalidParameterException(this.getClass(), "createBookmark", "name == null || name.equals(\"\")", sc);
        return KernelManager.getBookmark().createBookmark(name, createdate, filterId, taskId, userId, ownerId);
    }

    /**
     * Удаление закладки по ее id
     *
     * @param sc         сессия пользователя
     * @param bookmarkId ID закладки
     * @throws GranException при необходимости
     */
    public void deleteBookmark(SessionContext sc, String bookmarkId) throws GranException {
        log.trace("deleteBookmark");
        if (sc == null)
            throw new InvalidParameterException(this.getClass(), "deleteBookmark", "sc", sc);

        KernelManager.getBookmark().deleteBookmark(bookmarkId);
    }

    /**
     * Возвращает список закладок для заданного пользователя
     *
     * @param sc      сессия пользователя
     * @param ownerId ID пользователя
     * @return Список закладок для заданного пользователя
     * @throws GranException при необходимости
     * @see com.trackstudio.model.Bookmark
     */
    public List<SecuredBookmarkBean> getBookmarkList(SessionContext sc, String ownerId) throws GranException {
        log.trace("getBookmarkList");
        if (sc == null)
            throw new InvalidParameterException(this.getClass(), "getBookmarkList", "sc", sc);
        if (ownerId == null)
            throw new InvalidParameterException(this.getClass(), "getBookmarkList", "owner", sc);
        return (ArrayList<SecuredBookmarkBean>) SecuredBeanUtil.toList(sc, KernelManager.getBookmark().getBookmarkList(ownerId), SecuredBeanUtil.BOOKMARK);
    }
}
