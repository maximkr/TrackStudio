/*
 * @(#)LongTextManager.java
 * Copyright 2009 TrackStudio, Inc. All rights reserved.
 */
package com.trackstudio.kernel.manager;

import java.util.List;
import com.trackstudio.exception.GranException;
import com.trackstudio.kernel.lock.LockManager;
import com.trackstudio.model.Longtext;

import net.jcip.annotations.Immutable;

/**
 * Класс LongTextManager предназначен для работы с текстами длиннее 2000 символов (такими могут быть описания задач/сообщений).
 * Длинные тексты разделяются на блоки не длинее 2000 символов и записываются последовательно в отдельную таблицу,
 * при извлечении такиз строк они последовательно соединяются. Это необходимо для совместимости с базами данных различных производителей
 */
@Immutable
public class LongTextManager extends KernelManager {

    private static final String className = "LongTextManager.";
    private static final LongTextManager instance = new LongTextManager();
    private static final LockManager lockManager = LockManager.getInstance();

    /**
     * Конструктор по умолчанию
     */
    private LongTextManager() {
    }

    /**
     * Возвращает экземпляр текущего класса
     *
     * @return Экземпляр LongTextManager
     */
    protected static LongTextManager getInstance() {
        return instance;
    }

    /**
     * Создается объект Longtext
     *
     * @param id       ID объекта Longtext, если он был создан ранее
     * @param longText Сохраняемый текст
     * @return ID объекта Longtext
     * @throws GranException при необходимости
     */
    public String createLongtext(String id, String longText) throws GranException {
        return createLongtext(id, longText, true);
    }

    /**
     * Создается объект Longtext
     *
     * @param id             ID объекта Longtext, если он был создан ранее
     * @param longText       Сохраняемый текст
     * @param useTransaction Необходимо ли использовать транзакции
     * @return ID объекта Longtext
     * @throws GranException при необходимости
     */
    public String createLongtext(String id, String longText, boolean useTransaction) throws GranException {
        boolean w = lockManager.acquireConnection(className);
        lockManager.getLock(id).lock();
        try {
            if (longText == null || longText.length() == 0)
                longText = " ";//oracle feature #19708
            Longtext oldLongText = null;
            if (id != null) {
                oldLongText = (Longtext) hu.getObject(Longtext.class, id);
                hu.executeDML("delete from com.trackstudio.model.Longtext l where l.reference=?", id);
                hu.cleanSession();
            }
            String masterkey;
            int size = longText.length() / 2000;
            if (size >= 1) {
                if (oldLongText == null) {
                    Longtext first = new Longtext(null, 0, longText.substring(0, 2000));
                    masterkey = hu.createObject(first);
                } else {
                    oldLongText.setValue(longText.substring(0, 2000));
                    hu.updateObject(oldLongText);
                    masterkey = oldLongText.getId();
                }

                for (int i = 1; i < size; i++) {
                    String value = longText.substring(i * 2000, i * 2000 + 2000);
                    Longtext l = new Longtext(masterkey, i, value);
                    hu.createObject(l);
                }
                Longtext last = new Longtext(masterkey, size, longText.substring(size * 2000));
                hu.createObject(last);
            } else {
                if (oldLongText == null)
                    masterkey = hu.createObject(new Longtext(null, 0, longText));
                else {
                    oldLongText.setValue(longText);
                    hu.updateObject(oldLongText);
                    masterkey = oldLongText.getId();
                }
            }
            hu.cleanSession();
            return masterkey;
        } finally {
            if (w) lockManager.releaseConnection(className);
            lockManager.getLock(id).unlock();
        }
    }

    /**
     * Удаляет объект Longtext
     *
     * @param id ID удаляемого объекта
     * @throws GranException при необходимости
     */
    public void deleteLongtext(String id) throws GranException {
        lockManager.getLock(id).lock();
        try {
            hu.executeDML("delete from com.trackstudio.model.Longtext l where l.reference=?",id);
            hu.executeDML("delete from com.trackstudio.model.Longtext l where l.id=?",id);
            //hu.deleteObject(Longtext.class, id);
        } finally {
            lockManager.getLock(id).unlock();
        }
    }

    /**
     * Возвращает текст, полученный из объекта Longtext
     *
     * @param id ID объекта Longtext
     * @return текст
     * @throws GranException при необходимости
     */
    public String getLongtext(String id) throws GranException {
        boolean r = lockManager.acquireConnection(className);
        lockManager.getLock(id).lock();
        try {
            if (id == null) return null;
            Longtext ltext = KernelManager.getFind().findLongtext(id);
            String text = ltext.getValue();
            if (text.length() < 1800) // sometimes 2000 chars there doesn't work, use some threshould
                return text; // short string

            StringBuilder result = new StringBuilder(10000);
            result.append(text);
            List<String> values = hu.getList("select l.value from com.trackstudio.model.Longtext l where l.reference=? order by l.order", false, id);
            return result.append(String.join(",", values)).toString();
        } finally {
            if (r) lockManager.releaseConnection(className);
            lockManager.getLock(id).unlock();
        }
    }
}