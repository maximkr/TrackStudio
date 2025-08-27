/*
 * @(#)BookmarkManager.java
 * Copyright 2009 TrackStudio, Inc. All rights reserved.
 */
package com.trackstudio.kernel.manager;

import java.util.Calendar;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.trackstudio.action.GeneralAction;
import com.trackstudio.exception.GranException;
import com.trackstudio.kernel.lock.LockManager;
import com.trackstudio.model.Bookmark;

import net.jcip.annotations.Immutable;

/**
 * Класс BookmarkManager содержит методы для работы с закладками.<br/>
 * Закладки могут быть созданы для задач и пользователей.
 */
@Immutable
public class BookmarkManager extends KernelManager {

    private static final Log log = LogFactory.getLog(GeneralAction.class);
    protected static final String className = "BookmarkManager.";
    private static final BookmarkManager instance = new BookmarkManager();
    private static final LockManager lockManager = LockManager.getInstance();
    /**
     * Конструктор по умолчанию
     */
    private BookmarkManager() {
    }

    /**
     * Возвращает экземпляр текущего класса
     *
     * @return Экземпляр BookmarkManager
     */
    protected static BookmarkManager getInstance() {
        return instance;
    }

    /**
     * Создание новой закладки с указанным названием, датой и фильтром для задачи или пользователя
     *
     * @param name       Название закладки
     * @param createdate Дата создания
     * @param filterId   Фильтр
     * @param taskId     Задача
     * @param userId     Пользователь
     * @param ownerId    Автор закладки
     * @return ID созданной закладки
     * @throws GranException при необходимости
     */
    public String createBookmark(String name, Calendar createdate, String filterId, String taskId, String userId, String ownerId) throws GranException {
        boolean w = lockManager.acquireConnection(className);
        try {
            return hu.createObject(new Bookmark(name, createdate, taskId, filterId, userId, ownerId));
        } finally {
            if (w) lockManager.releaseConnection(className);
        }
    }

    /**
     * Удаление закладки по ее id
     *
     * @param bookmarkId ID закладки
     * @throws GranException при необходимости
     */
    public void deleteBookmark(String bookmarkId) throws GranException {
        boolean w = lockManager.acquireConnection(className);
        try {
            hu.deleteObject(Bookmark.class, bookmarkId);
        } finally {
            if (w) lockManager.releaseConnection(className);
        }
    }

    /**
     * Возвращает список закладок для заданного пользователя.
     *
     * @param ownerId ID пользователя
     * @return Список закладок для заданного пользователя
     * @throws GranException при необходимости
     * @see com.trackstudio.model.Bookmark
     */
    public List<Bookmark> getBookmarkList(String ownerId) throws GranException {
        boolean r = lockManager.acquireConnection(className);
        try {
            return hu.getList("from com.trackstudio.model.Bookmark as b where b.owner=?", ownerId);
        } finally {
            if (r) lockManager.releaseConnection(className);
        }
    }
}