# Как изменить проект по умолчанию для нескольких пользователей

Bulk скрипты в TrackStudio предназначены для выполнения операций над одной или несколькими выбранными из списка задачами. Однако использовать их можно и для более широких целей. Например, для изменения проекта по умолчанию для группы пользователей.

В данном скрипте меняется проект по умолчанию для всех пользователей, которые имеют доступ к выбранной задаче. При желании вы можете модифицировать код и менять другие параметры. Например, часовой пояс.

В скрипте мы получаем список пользователей, доступных через ACL, с помощью метода **getUserList** адаптера **SecuredAclAdapterManager**. Затем, перебирая список, мы устанавливаем новые свойства для пользователей через **updateUser **адаптера **SecuredUserAdapterManager**.

**package** scripts.bulk;**import** java.util.ArrayList;**import** com.trackstudio.app.adapter.AdapterManager;**import** com.trackstudio.exception.GranException;**import** com.trackstudio.external.TaskBulkProcessor;**import** com.trackstudio.secured.SecuredTaskBean;**import** com.trackstudio.secured.SecuredUserBean;**import** com.trackstudio.securedkernel.SecuredUserAdapterManager;**public** **class** ChangeUsersProperties **implements** TaskBulkProcessor { @Override **public** SecuredTaskBean execute(SecuredTaskBean task) **throws** GranException { SecuredUserAdapterManager uam = AdapterManager.getInstance() .getSecuredUserAdapterManager(); ArrayList<SecuredUserBean> users = AdapterManager.getInstance() .getSecuredAclAdapterManager() .getUserList(task.getSecure(), task.getId()); **for** (SecuredUserBean u : users) uam.updateUser(task.getSecure(), u.getId(), u.getLogin(), u.getName(), u.getTel(), u.getEmail(), u.getPrstatusId(), u.getManagerId(), u.getTimezone(), u.getLocale(), u.getCompany(), u.getTemplate(), task.getId(), u.getExpireDate(), u.getPreferences(), u.isEnabled()); **return** task; }}

Для изменения других свойств пользователей новые значения нужно подставить в метод **updateUser** и [перекомпилировать скрипт](scripts-debug-idea.md). Исходные коды и файл класса прилагаются.

Файл класса нужно положить в папку **etc/plugins/scripts/bulk** вашего экземпляра TrackStudio.
