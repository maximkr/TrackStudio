package com.trackstudio.tools;

import java.util.HashMap;

/**
 * Интерфейс менеджера прогресса
 */
public interface ProgressManager {
    /**
     * Устанавливает прогресс
     *
     * @param p значение прогресса
     */
    void setProgress(int p);

    /**
     * Возвращает значение прогресса
     *
     * @return значение прогресса
     */
    int getProgress();

    /**
     * Возвращает размер прогрессбара
     *
     * @return рамер
     */
    int getSize();

    /**
     * Устанавливает размер прогрессбара
     *
     * @param t размер
     */
    void setSize(int t);

    /**
     * Устанавливает закончился прогресс или нет
     *
     * @return TRUE = закончился, FALSE - нет
     */
    boolean isFinished();

    /**
     * Устанавливает начался прогресс или нет
     *
     * @return TRUE = начался, FALSE - нет
     */
    boolean isStarted();

    /**
     * Возвращает описание прогресса
     *
     * @return описание
     */
    String getDescription();

    /**
     * Возвращает информацию о прогрессе
     *
     * @return информация о прогрессе
     */
    HashMap getInfo();
}