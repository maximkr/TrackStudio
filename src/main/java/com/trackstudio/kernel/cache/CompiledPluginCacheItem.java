package com.trackstudio.kernel.cache;

import net.jcip.annotations.ThreadSafe;

/**
 * Вспомогательный класс для кеширования скомпилированных плагинов
 */
@ThreadSafe
public class CompiledPluginCacheItem extends AbstractPluginCacheItem {

    public volatile Class compiled;

    /**
     * Конструктор
     *
     * @param type тип плагина
     * @param name Название плагина
     */
    public CompiledPluginCacheItem(PluginType type, String name) {
        super(type, name);
    }

    /**
     * Возвращает скомпилированный класс
     *
     * @return класс
     */
    public Class getCompiled() {
        return compiled;
    }

    /**
     * Устанавливает скомпилированный класс
     *
     * @param compiled класс
     */
    public void setCompiled(Class compiled) {
        this.compiled = compiled;
    }


}
