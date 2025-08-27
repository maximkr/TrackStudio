package com.trackstudio.containers;

import com.trackstudio.tools.PropertyComparable;
import com.trackstudio.tools.PropertyContainer;

import net.jcip.annotations.Immutable;

/**
 * Класс-контейнер для хранения данных о ссылке, UDF типа URL
 */
@Immutable
public class Link extends PropertyComparable {

    /**
     * Ссылка
     */
    protected final String link;
    /**
     * Описание
     */
    protected final String description;

    /**
     * Возвращает контейнер
     *
     * @return экземпляр текущего класса
     */
    protected PropertyContainer getContainer() {
        PropertyContainer pc = container.get();
        if (pc != null)
            return pc; // object in cache, return it

        PropertyContainer newPC = new PropertyContainer();
        newPC.put(description).put(link);

        if (container.compareAndSet(null, newPC)) // try to update
            return newPC; // we can update - return loaded value
        else
            return container.get(); // some other thread already updated it - use saved value
    }

    /**
     * Возвращает ссылку
     *
     * @return ссылка
     */
    public String getLink() {
        return link;
    }

    /**
     * Возвращает описание
     *
     * @return описание
     */
    public String getDescription() {
        return description;
    }


    /**
     * Конструктор
     *
     * @param link        ссылка
     * @param description описание
     */
    public Link(String link, String description) {
        this.link = link;
        this.description = description;
    }

    /**
     * Возвращает текстовое представление объекта
     *
     * @return текстовое представление объекта
     */
    public String toString() {
        return link + "\n" + description;
    }
}
