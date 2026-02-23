[Домой](../../../../index.md) | [Наверх (Множественный список (Multilist))](index.md)

---

# E-mail адреса пользователей для оповещений через CC

Данный скрипт вычисляет e-mail адреса участников задачи для поля CC, [которое используется для оповещения пользователей.](../../../../admin-guide/howto-notify-unregistered-users.md)

**package** scripts.task_custom_field_value;**import** com.trackstudio.exception.GranException;**import** com.trackstudio.external.TaskUDFValueScript;**import** com.trackstudio.secured.SecuredMessageBean;**import** com.trackstudio.secured.SecuredTaskBean;**import** com.trackstudio.secured.SecuredUserBean;**import** java.util.HashSet;**public** **class** MultilistUdf **implements** TaskUDFValueScript{ **public** **void** add(SecuredUserBean user, HashSet<String> emails) **throws** GranException{ **if**(user!=**null** && user.getEmail()!=**null**) emails.add(user.getEmail()); } **public** Object calculate(SecuredTaskBean task) **throws** GranException{ **if**(task.getMessageCount()!=0) { HashSet<String> emails = **new** HashSet<String>(); **for**(SecuredMessageBean message:task.getMessages()) { add(message.getHandler(), emails); add(message.getSubmitter(), emails); } **return** emails; } **else** **return** **null**; }}

---

[Домой](../../../../index.md) | [Наверх (Множественный список (Multilist))](index.md)
