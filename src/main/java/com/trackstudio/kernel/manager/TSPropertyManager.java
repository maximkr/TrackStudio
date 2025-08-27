/*
 * @(#)TSPropertyManager.java
 * Copyright 2009 TrackStudio, Inc. All rights reserved.
 */
package com.trackstudio.kernel.manager;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.trackstudio.exception.GranException;
import com.trackstudio.kernel.lock.LockManager;
import com.trackstudio.model.Property;
import com.trackstudio.tools.HibernateUtil;

import net.jcip.annotations.ThreadSafe;

/**
 * Класс TSPropertyManager содержит методы для работы с настройками TrackStudio
 */
@ThreadSafe
public class TSPropertyManager {

    private static final String className = "TSPropertyManager.";
    private static final Log log = LogFactory.getLog(TSPropertyManager.class);
    private static final HibernateUtil hu = new HibernateUtil();

    private final ConcurrentMap<String, String> properties = new ConcurrentHashMap<String, String>();
    private static volatile TSPropertyManager instance;
    private static final LockManager lockManager = LockManager.getInstance();
    /**
     * Конструкторп о умолчанию
     *
     * @throws GranException при необходимости
     */
    private TSPropertyManager() throws GranException {
        List<Property> list = hu.getList("select p from com.trackstudio.model.Property as p", false, Collections.emptyList());
        for (Property p : list) {
            properties.put(p.getName(), p.getValue());
        }
    }

    /**
     * Возвращает экземпляр текущего класса
     *
     * @return Экземпляр TSPropertyManager
     * @throws GranException при необходимости
     */
    public static synchronized TSPropertyManager getInstance() throws GranException {
        if (instance == null)
            instance = new TSPropertyManager();
        return instance;
    }


    /**
     * Возвращает значение настройки TS по ее имени
     *
     * @param name название настройки
     * @return значение нгастройки
     */
    public String get(String name) {
        if (properties.get(name) == null)
            return null;
        else
            return properties.get(name);
    }

    /**
     * Устанавливает указанную настройку для TS
     *
     * @param name  Название настройки
     * @param value Значение настройки
     * @throws GranException при необзодимости
     */
    public void set(String name, String value) throws GranException {
        boolean w = lockManager.acquireConnection(className);
        try {
            log.trace("set(name='" + name + "', value='" + value + "')");
            List l = hu.getList("from com.trackstudio.model.Property p where p.name=?", name);
            if (l.isEmpty()) {
                hu.createObject(new Property(name, value));
            } else {
                Property obj = (Property) l.get(0);
                if (!obj.getValue().equals(value)) {
                    obj.setValue(value);
                    hu.updateObject(obj);
                }
            }
            properties.put(name, value);
        } finally {
            if (w) lockManager.releaseConnection(className);
        }
    }

    public Map<String, String> getProperties() {
        return properties;
    }
}