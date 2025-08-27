package com.trackstudio.kernel.cache;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.trackstudio.kernel.manager.KernelManager;
import com.trackstudio.model.Workflow;
import com.trackstudio.tools.Intern;
import com.trackstudio.tools.PropertyComparable;
import com.trackstudio.tools.PropertyContainer;

import net.jcip.annotations.Immutable;

/**
 * Вспомогательный класс, используется для кеширования пользовательских полей
 */
@Immutable
public class UDFCacheItem extends PropertyComparable {
    protected final static Log log = LogFactory.getLog(UDFCacheItem.class);
    protected final String id;
    protected final String udfsourceId;
    protected final String workflowId;
    protected final String taskId;
    protected final String userId;

    protected final String caption;
    protected final String referencedbycaption;
    protected final Integer type;
    protected final Integer order;
    protected final String def;
    protected final String initialtaskId;
    protected final String initialuserId;
    protected final Integer required;
    protected final Integer htmlview;
    // тут мы храним только имя файла
    protected final String script;
    protected final Integer cachevalues;
    // тут мы храним только имя файла
    protected final String lookupscript;
    protected final Integer lookuponly;
    protected final String sourceId;

    /**
     * Конструктор
     *
     * @param id                  ID обхекта
     * @param udfSourceId         ID  исходника
     * @param workflowId          ID процесса
     * @param taskId              ID Задачи
     * @param userId              ID gjkmpjdfntkz
     * @param caption             Название поля
     * @param referencedbycaption обратный загаловок
     * @param type                тип поля
     * @param order               сортировка
     * @param def                 значение типа дата
     * @param initialtaskId       ID начальной задачи
     * @param initialuserId       ID начального пользователя
     * @param required            обязательность поля
     * @param htmlview            представление поля
     * @param script              скрипт
     * @param cachevalues         необходимость кеширования значения
     * @param lookupscript        скрипт lookup
     * @param lookuponly          только ли скрипт
     * @param sourceId            ID исходника
     */
    public UDFCacheItem(String id, String udfSourceId, String workflowId, String taskId, String userId, String caption, String referencedbycaption, Integer type, Integer order, String def, String initialtaskId, String initialuserId, Integer required, Integer htmlview, String script, Integer cachevalues, String lookupscript, Integer lookuponly, String sourceId) {
        this.id = id;
        this.udfsourceId = Intern.process(udfSourceId);
        this.workflowId = Intern.process(workflowId);
        this.taskId = Intern.process(taskId);
        this.userId = Intern.process(userId);
        this.caption = Intern.process(caption);
        this.referencedbycaption = Intern.process(referencedbycaption);
        this.type = type;
        this.order = order;
        this.def = Intern.process(def);
        this.initialtaskId = Intern.process(initialtaskId);
        this.initialuserId = Intern.process(initialuserId);
        this.required = required;
        this.htmlview = htmlview ;
        this.script = Intern.process(script);
        this.cachevalues = cachevalues;
        this.lookupscript = Intern.process(lookupscript);
        this.lookuponly = lookuponly;
        this.sourceId = Intern.process(sourceId);
    }

    /**
     * Конструктор
     *
     * @param id                  ID обхекта
     * @param udfSourceId         ID  исходника
     * @param workflowId          ID процесса
     * @param taskId              ID Задачи
     * @param userId              ID gjkmpjdfntkz
     * @param caption             Название поля
     * @param referencedbycaption обратный загаловок
     * @param type                тип поля
     * @param order               сортировка
     * @param def                 значение типа дата
     * @param initialtaskId       ID начальной задачи
     * @param initialuserId       ID начального пользователя
     * @param required            обязательность поля
     * @param htmlview            представление поля
     * @param script              скрипт
     * @param cachevalues         необходимость кеширования значения
     * @param lookupscript        скрипт lookup
     * @param lookuponly          только ли скрипт
     */
    public UDFCacheItem(String id, String udfSourceId, String workflowId, String taskId, String userId, String caption, String referencedbycaption, Integer type, Integer order, String def, String initialtaskId, String initialuserId, Integer required, Integer htmlview, String script, Integer cachevalues, String lookupscript, Integer lookuponly) {
        this(id, udfSourceId, workflowId, taskId, userId, caption, referencedbycaption, type, order, def, initialtaskId, initialuserId,
                required, htmlview, script, cachevalues, lookupscript, lookuponly, null);
    }

    /**
     * Возвращаем обратный заголовок
     *
     * @return обратный заголовок
     */
    public String getReferencedbycaption() {
        return referencedbycaption;
    }

    /**
     * Возвращаем ID объекта
     *
     * @return ID объекта
     */
    public String getId() {
        return id;
    }

    /**
     * Возвращает ID объекта udfsource
     *
     * @return ID объекта udfsource
     */
    public String getUdfsourceId() {
        return udfsourceId;
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
     * Возвращает ID задачи
     *
     * @return ID задачи
     */
    public String getTaskId() {
        return taskId;
    }

    /**
     * Возвращает ID пользователя
     *
     * @return ID пользователя
     */
    public String getUserId() {
        return userId;
    }

    /**
     * Возвращает заголовок
     *
     * @return заголовок
     */
    public String getCaption() {
        return caption;
    }

    /**
     * Возвращает тип
     *
     * @return тип
     */
    public Integer getType() {
        return type;
    }

    /**
     * Возвращает порядок сортировки
     *
     * @return порядок сортировки
     */
    public Integer getOrder() {
        return order;
    }

    /**
     * Возвращает значение по умолчанию
     *
     * @return значение по умолчанию
     */
    public String getDef() {
        return def;
    }

    /**
     * Возвращает ID начальной задачи
     *
     * @return ID начальной задачи
     */
    public String getInitialtaskId() {
        return initialtaskId;
    }

    /**
     * Возвращает ID начального пользователя
     *
     * @return ID начального пользователя
     */
    public String getInitialuserId() {
        return initialuserId;
    }

    /**
     * Возвращает обязательность поля
     *
     * @return обязательность поля
     */
    public Integer getRequired() {
        return required;
    }

    /**
     * Возвращает представление поля
     *
     * @return представление поля
     */
    public Integer getHtmlview() {
        return htmlview;
    }

    /**
     * Возвращает скрипт
     *
     * @return скрипт
     */
    public String getScript() {
        return script;
    }


    /**
     * Возвращает кеширование  поля
     *
     * @return кеширование поля
     */
    public Integer getCachevalues() {
        return cachevalues;
    }

    /**
     * Возвращает lookup скрипт
     *
     * @return lookup скрипт
     */
    public String getLookupscript() {
        return lookupscript;
    }

    /**
     * Возвращает только ли скрипт надо использовать
     *
     * @return только ли скрипт
     */
    public Integer getLookuponly() {
        return lookuponly;
    }

    /**
     * Возвращает ID исходника
     *
     * @return ID bc[jlybrf
     */
    public String getSourceId() {
        return sourceId;
    }

    /**
     * Возвращает свойства
     *
     * @return свойства
     * @see com.trackstudio.tools.PropertyContainer
     */
    protected PropertyContainer getContainer() {
            PropertyContainer pc = container.get();
            if (pc != null)
                return pc; // object in cache, return it

            PropertyContainer newPC = new PropertyContainer();
            newPC.put(order).put(caption).put(id);

            if (container.compareAndSet(null, newPC)) // try to update
                return newPC; // we can update - return loaded value
            else
                return container.get(); // some other thread already updated it - use saved value
    }

    /**
     * Возвращает текстовое представление класса
     *
     * @return текстовое представление
     */
    public String toString() {
        return id;
    }

    /**
     * This method builds name for udf which common to workflows
     * @return name
     */
    public String buildName() {
        try {
            Workflow workflow = KernelManager.getFind().findWorkflow(workflowId);
            if (workflow != null) {
                return caption + " (" + workflow.getName() + ") ";
            }
        } catch (Exception e) {
            log.error("Error", e);
        }
        return caption;
    }
}