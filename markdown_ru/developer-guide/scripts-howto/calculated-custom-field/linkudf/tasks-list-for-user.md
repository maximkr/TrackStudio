[Домой](../../../../index.md) | [Наверх (Ссылка (URL))](index.md)

---

# Список задач пользователя в проекте.

Скрипт возвращает ссылку, отфильтрованных по фильтру "Мои задачи", задач текущего пользователя.

**package** scripts.task_custom_field_value;**import** com.trackstudio.containers.Link;**import** com.trackstudio.exception.GranException;**import** com.trackstudio.external.TaskUDFValueScript;**import** com.trackstudio.secured.SecuredTaskBean;**import** org.eclipse.birt.report.model.metadata.SemanticTriggerDefn;**public** **class** URLudf **implements** TaskUDFValueScript{ **public** Object calculate(SecuredTaskBean task) **throws** GranException { *// адрес хоста* **final** String host = "localhost:8888"; *// Id фильтра мои задачи* **final** String filterid = "4028808a1934fdc7011935080447004e"; **return** **new** Link(String.format("http://%s/TrackStudio/task/%s/filter/%s",host,task.getNumber(),filterid), "Список моих задач в этом проекте"); }}

---

[Домой](../../../../index.md) | [Наверх (Ссылка (URL))](index.md)
