[Домой](../../index.md) | [Наверх (Как писать скрипты и триггеры в TrackStudio)](index.md)

---

# Как выполнить действия над несколькими задачами

Выполнить действия над несколькими выбранными задачами можно с помощью Bulk-скриптов. В отличие от триггеров, bulk-скрипты выполняются не автоматически при редактировании задачи или выполнении операции, а тогда, когда пользователь выберет несколько задач в списке, выберет внизу списка нужный скрипт и нажмет кнопку "Выполнить".

Такие скрипты помещаются в папку **./etc/plugins/scripts/bulk/** и реализуют интерфейс **com.trackstudio.external. TaskBulkProcessor**.

Ниже приведен пример bulk-скрипта, который выполняет операцию Note над выбранными задачами.

**package** scripts.bulk;**import** com.trackstudio.app.csv.CSVImport;**import** com.trackstudio.exception.GranException;**import** com.trackstudio.external.TaskBulkProcessor;**import** com.trackstudio.secured.SecuredMessageTriggerBean;**import** com.trackstudio.secured.SecuredTaskBean;**import** java.util.Calendar;**public** **class** ProcessNoteOperation **implements** TaskBulkProcessor {**public** SecuredTaskBean execute(SecuredTaskBean task) **throws** GranException {*//Ищем идентификатор операции* String mstatusId = CSVImport.findMessageTypeIdByName("Note", task.getCategory().getName());*//Создаем сообщение* TriggerManager.getInstance().createMessage(task.getSecure(), task.getId(), mstatusId, "Activation", **null**, task.getHandlerUserId(), task.getHandlerGroupId(), task.getResolutionId(), task.getPriorityId(), **null**, 0l, **null**, **true**, **null**);*//Возвращаем task* **return** task; }}

Скомпилированный класс нужно положить в папку **./etc/plugins/scripts/bulk**

![](../../images/clip0049.png)

Далее зайдите в TrackStudio и перейдите в список подзадач. Внизу страницы, с левой стороны появится список с Вашими bulk-скриптами, выберите скрипт **ProcessNoteOperation**. Отметьте задачи для которых необходимо выполнить скрипт. Далее с правой стороны списка находится кнопка "Выполнить". После нажатия на кнопку, скрипт будет выполняться для каждой выделенной задачи.

---

[Домой](../../index.md) | [Наверх (Как писать скрипты и триггеры в TrackStudio)](index.md)
