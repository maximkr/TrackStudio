# Список участников для задачи

Данный скрипт вычисляет значение дополнительного поля типа User и выводит список пользователей, оставивших к задаче комментарии (или выполнивших любые операции над ней). Скрипт возвращает значение типа ArrayList<String>. Это значение можно использовать в фильтрах по задачам (например, выводить фильтром только те задачи, участником в которых является текущий пользователь.

Значение поля для разных пользователей может быть разным. Оно зависит от настроек видимости различных операций. Значение поля можно (и нужно) кешировать.

Ниже приложен исходный код скрипта и его скомпилированная версия.

**package** scripts.task_custom_field_value;**import** com.trackstudio.exception.GranException;**import** com.trackstudio.external.TaskUDFValueScript;**import** com.trackstudio.secured.SecuredMessageBean;**import** com.trackstudio.secured.SecuredTaskBean;**import** java.util.ArrayList;**public** **class** Participants **implements** TaskUDFValueScript { **public** Object calculate(SecuredTaskBean securedTaskBean) **throws** GranException { ArrayList<String> userIds = **new** ArrayList<String>(); ArrayList<SecuredMessageBean> messages = securedTaskBean.getMessages(); **for** (SecuredMessageBean m: messages){ **if** (!userIds.contains(m.getSubmitter().getLogin())) userIds.add(m.getSubmitter().getLogin()); } **return** userIds; }}
