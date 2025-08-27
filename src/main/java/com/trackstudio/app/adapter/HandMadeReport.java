package com.trackstudio.app.adapter;

import com.trackstudio.app.filter.TaskFValue;
import com.trackstudio.app.session.SessionContext;
import com.trackstudio.exception.GranException;

/**
 * Интерфейс на основании которого реализуются классы для работы с экспортом данных и генерацуией отчетов
 */
public interface HandMadeReport extends Adapter {

    /**
     * Генерирует экспортные данные или данные отчета
     *
     * @param sc       сессия пользователя
     * @param taskId   ID задачи
     * @param filterId ID фильтра
     * @param fvalue   Параметры постфильтрации
     * @param encoding Кодировка
     * @return Данные экспорта
     * @throws GranException при необходимости
     */
    String generateImpl(SessionContext sc, String taskId, String filterId, TaskFValue fvalue, String encoding) throws GranException;
}