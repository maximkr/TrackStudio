[Home](../index.md) | [Up (Developer's Guide)](index.md)

---

# How to check data before saving the task (Before Task Edit Trigger)

So as to check the data before saving the current task, trigger Before Edit Task must be connected to its workflow.

It must conform to the interface **com.trackstudio.external.TaskTrigger** and located in the folder

```
./etc/plugins/scripts/before_edit_task/
```

Below given is the example of trigger, which checks, whether the deadline indicated for the task is correct or not. So that the user is informed about the error and the task is not saved, UserException is thrown in the trigger. Thus the user returns to the edit page of the task. The Data remain typed.

package scripts.before_edit_task;import com.trackstudio.exception.GranException;import com.trackstudio.exception.UserException;import com.trackstudio.external.TaskTrigger;import com.trackstudio.secured.SecuredTaskTriggerBean;import java.util.Calendar;public class CheckDeadline implements TaskTrigger { public SecuredTaskTriggerBean execute(SecuredTaskTriggerBean securedTaskTriggerBean) throws GranException { Calendar deadline = securedTaskTriggerBean.getDeadline(); Calendar now = Calendar.getInstance(); if (deadline == null || deadline.before(now)) throw new UserException("Indicate the deadline of the task"); return securedTaskTriggerBean; }}

With the help of triggers Before Edit Task, generally those tasks are performed which have to be executed before saving the task, so that if these actions were not successful the user could return to the input page and repeat the actions.

---

[Home](../index.md) | [Up (Developer's Guide)](index.md)
