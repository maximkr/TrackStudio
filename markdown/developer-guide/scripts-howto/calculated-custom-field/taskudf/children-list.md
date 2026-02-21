# Список новых подзадач

Данный простейший скрипт для вычисляемого поля типа задача возвращает список подзадач в начальном состоянии для текущей задачи.

**package** scripts.task_custom_field_value;**import** com.trackstudio.exception.GranException;**import** com.trackstudio.external.TaskUDFValueScript;**import** com.trackstudio.secured.SecuredTaskBean;**import** java.util.ArrayList;**public** **class** TaskUDF **implements** TaskUDFValueScript{ **public** Object calculate (SecuredTaskBean task) **throws** GranException{ */* Создаем список в который будем добавлять номера нужных задач */* ArrayList list = **new** ArrayList(); */* Из списка подзадач извлекаем номера** * и добавляем их в строковый список */* **for**(SecuredTaskBean tsk:task.getChildren()) { **if**(tsk.getStatus().isStart()) list.add(tsk.getNumber()); } **return** list; }}
