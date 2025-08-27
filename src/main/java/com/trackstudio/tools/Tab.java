package com.trackstudio.tools;

import net.jcip.annotations.ThreadSafe;

/**
 * Класс используется для задания доступный и установленных страниц (табоб) для пользователей
 */
@ThreadSafe
public class Tab {

    private volatile boolean allowed;
    private volatile boolean selected;

    /**
     * Определяет доступен таб или нет
     *
     * @return TRUE - доступен, FALSE - нет
     */
    public boolean isAllowed() {
        return allowed;
    }

    /**
     * Устанавливает доступен таб или нет
     *
     * @param allowed TRUE - доступен, FALSE - нет
     */
    public void setAllowed(boolean allowed) {
        this.allowed = allowed;
    }

    /**
     * Определяет выбран таб или нет
     *
     * @return TRUE - выбран, FALSE - нет
     */
    public boolean isSelected() {
        return selected;
    }

    /**
     * Устанавливает выбран таб или нет
     *
     * @param selected TRUE - выбран, FALSE - нет
     */
    public void setSelected(boolean selected) {
        this.selected = selected;
    }

    /**
     * Конструктор
     *
     * @param allowed  доступен ли таб
     * @param selected выбран ли таб
     */
    public Tab(boolean allowed, boolean selected) {
        this.allowed = allowed;
        this.selected = selected;
    }

    /**
     * Возвращает строковое представление таба
     *
     * @return строковое представление таба
     */
    public String toString() {
        return allowed + ", " + selected;
    }
}