package com.trackstudio.app.adapter;

/**
 * Базовый интерфейс для классов-адаптеров
 */
public interface Adapter {

    /**
     * Инициализирует адаптер. Метод вызывается при старте системы
     *
     * @return TRUE - если адаптер инициализирован, FALSE - если нет
     */
    boolean init();

    /**
     * Возвращает текстовое описание адаптера
     *
     * @return текстовое описание адаптера
     * 
     */

    String getDescription();
}
