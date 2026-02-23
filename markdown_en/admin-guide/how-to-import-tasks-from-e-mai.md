[Home](../index.md) | [Up (Administrator's Guide)](index.md)

---

# How to import tasks from e-mails sent by unregistered users

Some of our clients needed to import tasks from mail, including the ones sent by unregistered users. This feature was there in TrackStudio 3.5, but it had lots of problems associated with it, and now all these issues have been resolved. In TrackStudio 4.0, this can be done with the help of a single trigger.

```
Attention! This script works very well in TrackStudio right from version 4.0.6.
```

We will create the tasks from email messages by using inbuilt import feature, while at the same time we will check for the presence of this user in the system, and, if required, we will create the new user, assign him the rights to the project and create the task in his name.

package scripts.before_create_task;import com.trackstudio.app.adapter.AdapterManager;import com.trackstudio.exception.GranException;import com.trackstudio.external.TaskTrigger;import com.trackstudio.kernel.manager.KernelManager;import com.trackstudio.kernel.manager.UserManager;import com.trackstudio.secured.SecuredTaskTriggerBean;import com.trackstudio.tools.Null;import java.util.regex.Matcher;import java.util.regex.Pattern;public class CreateUserFromEmail implements TaskTrigger { public SecuredTaskTriggerBean execute(SecuredTaskTriggerBean task) throws GranException { String s = task.getDescription(); String userStatusId = task.getSubmitter().getPrstatusId(); if (s != null && s.length() > 0) { String emailPattern = "From:\\s*\\\"*(\\S+\\s*\\S+\\\"*){0,1}\\s+<*(\\S+@\\S+)>*"; // From: [max.vasenkov@gmail.com](mailto:max.vasenkov@gmail.com) // From: Maxim Vasenkov <max.vasenkov@gmail.com> // From: Winzard <i@winzard.ru> // From: Admin <admin@localhost> // From: " Maxim Vasenkov " <vasenkov@any.place.com> Pattern pat = Pattern.compile(emailPattern); Matcher mat = pat.matcher(s); if (mat.find()) { String userName = mat.group(1); String userEmail = mat.group(2); if (userName == null) userName = userEmail; String fId = KernelManager.getUser().findUserIdByEmailNameProject(userEmail, userName, task.getParentId()); if (fId==null){ String id = AdapterManager.getInstance().getSecuredUserAdapterManager().createUser(task.getSecure(), Null.beNull(task.getSubmitterId()), userEmail, userName, Null.beNull(userStatusId)); String aclid = AdapterManager.getInstance().getSecuredAclAdapterManager().createAcl(task.getSecure(), task.getParentId(), null, id, null); AdapterManager.getInstance().getSecuredAclAdapterManager().updateTaskAcl(task.getSecure(), aclid, userStatusId, false); if (id != null) task.setSubmitterId(id); } } } return task; }}

Here the user is created with the same role, as the user executes indicated in the import rules, through which import is executed. User is given the rights to the higher task w.r.t the one being imported.

This **Create Task/Before Trigger** must be connected in the settings to the **categories**** **of those tasks, which are planned to be imported.

---

[Home](../index.md) | [Up (Administrator's Guide)](index.md)
