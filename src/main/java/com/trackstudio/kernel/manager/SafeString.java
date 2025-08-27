package com.trackstudio.kernel.manager;

import com.trackstudio.tools.textfilter.HTMLEncoder;

import net.jcip.annotations.Immutable;

/**
 * Вспомогательный класс SafeString, предназначен для получения из исходной строки - строки
 * без html-аттрибутов, которые могут быть потенциально опасны при выводе
 */
@Immutable
public class SafeString implements java.io.Serializable, Comparable<String>, CharSequence {

    protected final StringBuilder internal;

    /**
     * Конструктор, создающий из исходный строки объект SafeString
     *
     * @param s исходная строка
     */
    private SafeString(String s) {
    	this.internal = new StringBuilder(HTMLEncoder.safe(s));
       }

    /**
     * Возвращает "безопасную" строку
     *
     * @return строка
     */
    public String toString() {
        return internal.toString();
    }

    /**
     * Возвращает Hash code строки
     *
     * @return #String.hashCode
     */
    public int hashCode() {
        return internal.hashCode();
    }

    /**
     * Осуществляет сравнение двух строк
     *
     * @param o Строка с которой сравниваем
     * @return #String.compareTo
     */
    public int compareTo(String o) {
        return internal.toString().compareTo(o);
    }

    /**
     * Возвращает длинну строки
     *
     * @return длинна
     */
    public int length() {
        return internal.length();
    }

    /**
     * Возвращает символ в указанной позиции
     *
     * @param index позиция
     * @return символ
     */
    public char charAt(int index) {
        return internal.charAt(index);
    }

    /**
     * Возвращает подпоследовательность
     *
     * @param start начало подпоследовательности
     * @param end   конец подпоследовательности
     * @return подпоследовательность
     * @see CharSequence
     */
    public CharSequence subSequence(int start, int end) {
        return internal.subSequence(start, end);
    }

    /**
     * Возвращает SafeString или null (если исходная строка тоже null)
     *
     * @param s исходная строка
     * @return SafeString
     */
    public static SafeString createSafeString(String s) {
        if (s != null)
            return new SafeString(s);
        else
            return new SafeString("");
    }
    
    public SafeString append(String txt) {
        this.internal.append(HTMLEncoder.safe(txt));
        return this;
    }
}