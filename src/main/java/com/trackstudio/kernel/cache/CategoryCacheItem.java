package com.trackstudio.kernel.cache;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.atomic.AtomicReference;

import com.trackstudio.exception.GranException;
import com.trackstudio.kernel.manager.KernelManager;
import com.trackstudio.tools.Intern;

import net.jcip.annotations.Immutable;

/**
 * Вспомогательный класс, используется для кеширования категорий
 */
@Immutable
public class CategoryCacheItem implements Serializable {
    private static final long serialVersionUID = 1L;
    private final String id;
    private final String name;
    private final String budget;
    private final String preferences;
    private final String workflowId;
    private final String workflowName;
    private final transient AtomicReference<String> template;
    private final String icon;
    private final String taskId;
    private final Integer handlerRequiredInt;
    private final Integer groupHandlerAllowedInt;
    private final transient AtomicReference<CopyOnWriteArrayList<String>> subcategories;

    /**
     * Возвращает ID категории
     *
     * @return ID категории
     */
    //lock: don't need
    public String getId() {
        return id;
    }

    /**
     * Возвращает ID процесса
     *
     * @return ID процесса
     */
    public String getWorkflowId() {
        return workflowId;
    }

    /**
     * Возвращает название процесса
     *
     * @return название процесса
     */
    public String getWorkflowName() {
        return workflowName;
    }

    /**
     * Возвращает шаблон категории
     *
     * @return шаблон категории
     * @throws GranException при необходимости
     */
    public String getTemplate() throws GranException {
        // Get current value in cache
        String current = template.get();
        if (current != null)
            return current; // value in cache exists, return it

        // now load data from db
        String newItem = KernelManager.getCategory().getTemplate(id);
        if (template.compareAndSet(null, newItem)) // try to update
            return newItem; // we can update - return loaded value
        else
            return template.get(); // some other thread already updated it - use saved value
    }

    /**
     * Возвращает ID задачи
     *
     * @return ID задачи
     */
    public String getTaskId() {
        return taskId;
    }

    /**
     * Определдяет необходим ли обязательный ответственный
     *
     * @return TRUE если необходим, FALSE если нет
     */
    public boolean isHandlerRequired() {
        return handlerRequiredInt != null && handlerRequiredInt == 1;
    }

    /**
     * Определяет Можно ли задавать группу в качестве ответственного
     *
     * @return TRUE если можно, FALSE - если нет
     */
    public boolean isGroupHandlerAllowed() {
        return groupHandlerAllowedInt != null && groupHandlerAllowedInt == 1;
    }

    /**
     * Возвращает название категории
     *
     * @return название
     */
    public String getName() {
        return name;
    }

    /**
     * Возвращает 1 если необходим обязательный ответственный
     *
     * @return 1 - если необходим ответственный, другое число - нет
     */
    public Integer getHandlerRequiredInt() {
        return handlerRequiredInt;
    }

    /**
     * Возвращает 1 если можно задачать гуппу в качестве ответственного
     *
     * @return 1 - если можно, другое число - нет
     */
    public Integer getGroupHandlerAllowedInt() {
        return groupHandlerAllowedInt;
    }

    /**
     * Возвращает список ID подкатегорий
     *
     * @return список ID подгатегорий
     * @throws GranException при необходимости
     */
    public List<String> getSubcategories() throws GranException {
        // Get current value in cache
        List<String> current = subcategories.get();
        if (current != null)
            return current; // value in cache exists, return it

        // now load data from db
        CopyOnWriteArrayList<String> newVal = new CopyOnWriteArrayList<>(Intern.process(KernelManager.getCategory().getChildrenCategoryIdList(id)));
        if (subcategories.compareAndSet(null, newVal)) // try to update
            return newVal; // we can update - return loaded value
        else
            return subcategories.get(); // some other thread already updated it - use saved value
    }

    /**
     * Конструктор
     *
     * @param id                     ID категории
     * @param name                   Название категории
     * @param workflowId             ID процесса
     * @param workflowName           Название процесса
     * @param taskId                 ID задачи
     * @param handlerRequiredInt     Необходим ли ответственный
     * @param groupHandlerAllowedInt Можно ли задавать группу в качестве ответственного
     * @param budget                 Формат бюджета
     * @param preferences            Настройки
     */
    public CategoryCacheItem(String id, String name, String workflowId, String workflowName, String taskId, Integer handlerRequiredInt, Integer groupHandlerAllowedInt, String budget, String preferences, String icon) {
        this.id = id;
        this.name = name;
        this.workflowId = Intern.process(workflowId);
        this.workflowName = Intern.process(workflowName);
        this.taskId = Intern.process(taskId);
        this.handlerRequiredInt = handlerRequiredInt;
        this.groupHandlerAllowedInt = groupHandlerAllowedInt;
        this.budget = budget;
        this.preferences = Intern.process(preferences);
        this.icon = icon;
        this.template = new AtomicReference<>(null);
        this.subcategories = new AtomicReference<>(null);
    }

    /**
     * Возвращает строковое представление тегущегно класса
     *
     * @return строковое представление
     */
    public String toString() {
        return this.getName() + "(" + getId() + ")";
    }

    /**
     * Возвращает формат бюджета
     *
     * @return формат бюдета
     */
    public String getBudget() {
        return budget;
    }

    /**
     * Возвращает настройки категории
     *
     * @return настройки
     */
    public String getPreferences() {
        return preferences;
    }

    public String getIcon() {
        return icon;
    }

    /**
     * Пользовательская сериализация для корректной обработки AtomicReference полей
     */
    private void writeObject(ObjectOutputStream oos) throws IOException {
        oos.defaultWriteObject();
        // Сериализуем значения AtomicReference полей
        oos.writeObject(template.get());
        oos.writeObject(subcategories.get());
    }

    /**
     * Пользовательская десериализация для корректной обработки AtomicReference полей
     */
    @SuppressWarnings("unchecked")
    private void readObject(ObjectInputStream ois) throws IOException, ClassNotFoundException {
        ois.defaultReadObject();
        // Восстанавливаем AtomicReference поля
        String templateValue = (String) ois.readObject();
        CopyOnWriteArrayList<String> subcategoriesValue = (CopyOnWriteArrayList<String>) ois.readObject();
        
        // Используем рефлексию для установки final полей
        try {
            java.lang.reflect.Field templateField = this.getClass().getDeclaredField("template");
            templateField.setAccessible(true);
            templateField.set(this, new AtomicReference<>(templateValue));
            
            java.lang.reflect.Field subcategoriesField = this.getClass().getDeclaredField("subcategories");
            subcategoriesField.setAccessible(true);
            subcategoriesField.set(this, new AtomicReference<>(subcategoriesValue));
        } catch (Exception e) {
            throw new IOException("Failed to deserialize AtomicReference fields", e);
        }
    }

}