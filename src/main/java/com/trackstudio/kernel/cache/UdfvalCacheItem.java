package com.trackstudio.kernel.cache;

import java.util.Calendar;
import java.util.concurrent.atomic.AtomicReference;

import com.trackstudio.exception.GranException;
import com.trackstudio.kernel.manager.KernelManager;
import com.trackstudio.tools.Intern;

import net.jcip.annotations.ThreadSafe;

@ThreadSafe
public class UdfvalCacheItem {
    private final String id;
    private final String udfId;
    private final String str;
    private final Double num;
    private final Calendar dat;
    private final String udflistId;
    private volatile String udflistVal;
    private final String taskId;
    private final String userId;
    private final String sourceId;
    private final String longtextId;
    private final AtomicReference<String> longtextStr;

    /**
     * Конструктор
     *
     * @param id         ID значения
     * @param udfId      ID пользовательского поля
     * @param str        строковое значение
     * @param num        числовое значение
     * @param dat        значение даты
     * @param udflistId  ID списка значений
     * @param udflistVal значение списка значений
     * @param longtextId ID объекта longtext
     * @param taskId     ID задачи
     * @param userId     ID пользователя
     */
    public UdfvalCacheItem(String id, String udfId, String str, Double num, Calendar dat, String udflistId, String udflistVal, String longtextId, String taskId, String userId) {
        this(id, null, udfId, str, num, dat, udflistId, udflistVal, longtextId, taskId, userId);
    }

    /**
     * Этот конструктор используются в запросах хибернейта, сделано специально для weblogic, т.к. он глючит если использовать в конструкторах ''
     *
     * @param id         ID значения
     * @param udfId      ID пользовательского поля
     * @param str        строковое значение
     * @param num        числовое значение
     * @param dat        значение даты
     * @param udflistId  ID списка значений
     * @param longtextId ID объекта longtext
     * @param taskId     ID задачи
     * @param userId     ID пользователя
     */
    public UdfvalCacheItem(String id, String udfId, String str, Double num, Calendar dat, String udflistId, String longtextId, String taskId, String userId) {
        this(id, null, udfId, str, num, dat, udflistId, null, longtextId, taskId, userId);
    }

    /**
     * Этот конструктор используются в запросах хибернейта, сделано специально для weblogic, т.к. он глючит если использовать в конструкторах ''
     *
     * @param id         ID значения
     * @param sourceId   id исходника
     * @param udfId      ID пользовательского поля
     * @param str        строковое значение
     * @param num        числовое значение
     * @param dat        значение даты
     * @param udflistId  ID списка значений
     * @param longtextId ID объекта longtext
     * @param taskId     ID задачи
     * @param userId     ID пользователя
     */
    public UdfvalCacheItem(String id, String sourceId, String udfId, String str, Double num, Calendar dat, String udflistId, String longtextId, String taskId, String userId) {
        this(id, sourceId, udfId, str, num, dat, udflistId, null, longtextId, taskId, userId);
    }

    /**
     * Конструктор
     *
     * @param id         ID значения
     * @param sourceId   id исходника
     * @param udfId      ID пользовательского поля
     * @param str        строковое значение
     * @param num        числовое значение
     * @param dat        значение даты
     * @param udflistId  ID списка значений
     * @param udflistVal значение списка значений
     * @param longtextId ID объекта longtext
     * @param taskId     ID задачи
     * @param userId     ID пользователя
     */
    public UdfvalCacheItem(String id, String sourceId, String udfId, String str, Double num, Calendar dat, String udflistId, String udflistVal, String longtextId, String taskId, String userId) {
        this.id = id;
        this.udfId = Intern.process(udfId);
        this.str = str;
        this.num = num;
        this.dat = dat;
        this.udflistId = Intern.process(udflistId);
        this.udflistVal = Intern.process(udflistVal);
        this.taskId = Intern.process(taskId);
        this.userId = Intern.process(userId);
        this.sourceId = Intern.process(sourceId);
        this.longtextId = longtextId;
        this.longtextStr = new AtomicReference<String>(null);
    }

    /**
     * Устанавливает значение списка
     *
     * @param udflistVal значение списка
     */
    public void setUdflistVal(String udflistVal) {
        this.udflistVal = udflistVal.intern();
    }

    /**
     * Возвращается ID объекта
     *
     * @return id объекта
     */
    public String getId() {
        return id;
    }

    /**
     * id пользовательского поля
     *
     * @return id пользовательского поля
     */
    public String getUdfId() {
        return udfId;
    }

    public String getLongtextStr() throws GranException {
        String current = longtextStr.get();
        if (current != null)
            return current;

        String newValue = Intern.process(KernelManager.getLongText().getLongtext(longtextId));
        if (longtextStr.compareAndSet(null, newValue))
            return newValue;
        else
            return longtextStr.get();
    }

    /**
     * Возвращает строковое значение
     *
     * @return строковое значение
     */
    public String getStr() {
        try {
            if (longtextId == null)
                return str;
            else
                return getLongtextStr();
        } catch (Exception e) {
            return str;
        }
    }

    /**
     * Возвращает числовое значение
     *
     * @return числовое значение
     */
    public Double getNum() {
        return num;
    }

    /**
     * Возвращает значение даты
     *
     * @return значение даты
     */
    public Calendar getDat() {
        return dat;
    }

    /**
     * Возвращает ID списка значений
     *
     * @return ID списка значения
     */
    public String getUdflistId() {
        return udflistId;
    }

    /**
     * Возвращает значение списка значений
     *
     * @return значение
     */
    public String getUdflistVal() {
        return udflistVal;
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
     * Возвращает ID исходника
     *
     * @return ID исходника
     */
    public String getSourceId() {
        return sourceId;
    }

}