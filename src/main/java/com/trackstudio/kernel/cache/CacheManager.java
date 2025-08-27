package com.trackstudio.kernel.cache;

import com.trackstudio.tools.HibernateUtil;

import net.jcip.annotations.Immutable;

/**
 * Абстрактный класс, от которого наследуются все остальные кеширующие менеджеры
 */
@Immutable
public abstract class CacheManager {
    /**
     * Объект для доступа к методам работы с базой данных
     */
    protected static final HibernateUtil hu = new HibernateUtil();
}
