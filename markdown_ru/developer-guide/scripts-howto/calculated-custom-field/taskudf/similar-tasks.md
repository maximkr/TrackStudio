[Домой](../../../../index.md) | [Наверх (Список задач (Task))](index.md)

---

# Список похожих задач

Скрипт возвращает список похожих задач. Будет полезен при общении с пользователями через TrackStudio.

**package** scripts.task_custom_field_value;**import** com.trackstudio.app.adapter.AdapterManager;**import** com.trackstudio.exception.GranException;**import** com.trackstudio.external.TaskUDFValueScript;**import** com.trackstudio.secured.SecuredSearchTaskItem;**import** com.trackstudio.secured.SecuredTaskBean;**import** java.util.*;*/**** * Возвращает список похожих задач** */***public** **class** AutoSimilarTasks **implements** TaskUDFValueScript { **public** **static** **int** LIMIT=25; **public** Object calculate(SecuredTaskBean securedTaskBean) **throws** GranException { HashMap<SecuredTaskBean, Float> tasks =AdapterManager.getInstance().getSecuredTaskAdapterManager().findSimilar(securedTaskBean.getSecure(), securedTaskBean.getId());ArrayList<SecuredSearchTaskItem> results = **new** ArrayList<SecuredSearchTaskItem>(); **for** (Map.Entry e : tasks.entrySet()) { Float ratio = (Float) e.getValue(); SecuredSearchTaskItem sstask = **new** SecuredSearchTaskItem(0, ratio, (SecuredTaskBean) e.getKey(), "", ""); results.add(sstask); } Collections.sort(results); List<String> taskIds = **new** ArrayList<String>(); **for** (**int** i=0; i<LIMIT && i<results.size(); i++){ taskIds.add('#'+results.get(i).getTask().getNumber()); } **return** taskIds; }}

---

[Домой](../../../../index.md) | [Наверх (Список задач (Task))](index.md)
