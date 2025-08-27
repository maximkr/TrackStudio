package com.trackstudio.constants;

/**
 * Интерфейс, описывающий константы для параметров фильтрации
 */
public interface FValueConstants {
    /**
     * Внутри списка/диапазона
     */
    int IN_CONDITION = 0;
    /**
     * Вне списка/диапазона
     */
    int NOT_IN_CONDITION = 1;
    /**
     * Не удовлетворяет условию
     */
    int NE_CONDITION = 2;
    /**
     * Удовлетвоярет условию
     */
    int EQ_CONDITION = 3;
    /**
     * Меньше условия
     */
    int GE_CONDITION = 4;
    /**
     * больше условия
     */
    int LE_CONDITION = 5;
}
