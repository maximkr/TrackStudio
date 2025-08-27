package com.trackstudio.tools;

import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import net.jcip.annotations.GuardedBy;
import net.jcip.annotations.ThreadSafe;

/**
 * Класс-контейнер для хранения свойств
 */
@ThreadSafe
public class PropertyContainer {
    private final ReentrantReadWriteLock rwl = new ReentrantReadWriteLock();

    private static final String STRUT = "000";
    private static final int REV_STRUT = 999;
    private final String[] properties;

    @GuardedBy("rwl")
    private int size = 0;

    @GuardedBy("rwl")
    private volatile int current = 1;
    private final ThreadLocal<DateFormat> df = new ThreadLocal<DateFormat>();
    /**
     * Флаг инверсии
     */
    public final boolean inverse;

    /**
     * Конструктор
     */
    public PropertyContainer() {
        this.properties = new String[10];
        // fill 0-element with empty string
        this.properties[0] = "";
        this.inverse = false;
    }

    public PropertyContainer(boolean inverse) {
        this.properties = new String[10];
        // fill 0-element with empty string
        this.properties[0] = "";
        this.inverse = inverse;
    }


    private DateFormat getDateFormat()
    {   if (df.get()==null)
            df.set(new SimpleDateFormat("yyyyMMddHHmmssSSS"));
        return df.get();
    }

    /**
     * Добавляет свойство в контейнер
     *
     * @param a свойство
     */
    protected void add(String a) {
        rwl.writeLock().lock();
        try {
            size++;
            properties[size] = properties[size - 1] + a;
        } finally {
            rwl.writeLock().unlock();
        }

    }

    /**
     * Добевляет свойство в контейнер и возвращает контейнер свойств
     *
     * @param prop свойство
     * @return конейнер свойств
     */
    public PropertyContainer put(String prop) {
        add(prop);
        return this;
    }

    /**
     * Добевляет числовое свойство в контейнер и возвращает контейнер свойств
     *
     * @param prop свойство
     * @return конейнер свойств
     */
    public PropertyContainer put(Integer prop) {
        String strut;
        if (prop == null) {
            strut = STRUT + "0";
        } else {
            strut = STRUT + prop.toString();
        }
        add(strut.substring(strut.length() - STRUT.length()));
        return this;
    }
    
    public PropertyContainer putInverse(Integer prop) {
        String strut;
        
        if (prop == null) {
            prop = 0;
        }
        strut = STRUT+ new Integer(REV_STRUT - prop).toString();
        
        add(strut.substring(strut.length() - STRUT.length()));
        return this;
    }

    /**
     * Добевляет лробное свойство в контейнер и возвращает контейнер свойств
     *
     * @param prop свойство
     * @return конейнер свойств
     */
    public PropertyContainer put(Calendar prop) {

        if (prop!=null) add(getDateFormat().format(prop.getTime()));
        return this;
    }
    public PropertyContainer put(Float prop) {
        NumberFormat nf = new DecimalFormat("0000000000.000");
        add(nf.format(prop.doubleValue()));
        return this;
    }
    /**
     * Возвращает строковое представление контейнера свойств
     *
     * @return строковое представление
     */
    public String toString() {
        rwl.readLock().lock();
        try {
            return properties[size];
        } finally {
            rwl.readLock().unlock();
        }

    }

    /**
     * Возращает следующее по порядку свойство
     *
     * @return свойство
     */
    public String next() {
        rwl.writeLock().lock();
        try {
            if (current <= size) {
                String s = properties[current];
                current++;
                return s;
            } else
                return null;
        } finally {
            rwl.writeLock().unlock();
        }

    }

    /**
     * Восстанавливает исходное состояние контейнера
     */
    public void reset() {
        rwl.writeLock().lock();
        try {
            current = 1;
        } finally {
            rwl.writeLock().unlock();
        }

    }

    /**
     * Очищает контейнер
     */
    public void clear() {
        rwl.writeLock().lock();
        try {
            reset();
            size = 0;
            properties[0] = "";
        } finally {
            rwl.writeLock().unlock();
        }

    }

    /**
     * Возвращает хеш контейнера
     *
     * @return хеш
     */
    public int getHashCode() {
        rwl.readLock().lock();
        try {
            return properties[size].hashCode();
        } finally {
            rwl.readLock().unlock();
        }

    }

    /**
     * Возвращает хеш контейнера
     *
     * @return хеш
     */
    public int hashCode() {
        return getHashCode();
    }
}