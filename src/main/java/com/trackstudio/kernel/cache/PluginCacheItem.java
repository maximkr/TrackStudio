package com.trackstudio.kernel.cache;

import net.jcip.annotations.ThreadSafe;

/**
 * Вспомогательный класс для кеширования плагинов
 */
@ThreadSafe
public class PluginCacheItem extends AbstractPluginCacheItem {

    private volatile String text;

    /**
     * Возвращает текст плагина
     *
     * @return текст
     */
    public String getText() {
        return text;
    }

    /**
     * Устанавливает текст плагина
     *
     * @param text текст
     */
    public void setText(String text) {
        this.text = text;
    }

    /**
     * Конструктор по умолчанию
     *
     * @param type тип плагина
     * @param name название плагина
     */
    public PluginCacheItem(PluginType type, String name) {
        super(type, name);
    }
}