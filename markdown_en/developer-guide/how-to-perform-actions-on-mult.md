[Home](../index.md) | [Up (Developer's Guide)](index.md)

---

# How to perform actions on multiple tasks

Actions can be performed over multiple selected tasks with the help of Bulk-scripts. In contrast to triggers, bulk-scripts are not executed automatically when the task is edited or operation is executed, but then, when user selects several tasks in the list, selects the required script below the list, and presses the button “Execute”.

These scripts are saved in the folder **./etc/plugins/scripts/bulk/** and implement the interface **com.trackstudio.external. TaskBulkProcessor**.

Below given is an example of bulk-script, which executes the operation Note on several tasks.

package scripts.bulk;import com.trackstudio.app.csv.CSVImport;import com.trackstudio.exception.GranException;import com.trackstudio.external.TaskBulkProcessor;import com.trackstudio.secured.SecuredMessageTriggerBean;import com.trackstudio.secured.SecuredTaskBean;import java.util.Calendar;/** * Executes the operation Note on specified task */public class ProcessNoteOperation implements TaskBulkProcessor { public SecuredTaskBean execute(SecuredTaskBean securedTaskBean) throws GranException { /** * Search the identifier of operation */ String mstatusId = CSVImport.findMessageTypeIdByName("Note", securedTaskBean.getCategory().getName()); /** * Creating SecuredMessageTriggerBean */ SecuredMessageTriggerBean createMessage = new SecuredMessageTriggerBean( null /* identifier */, "Activation" /* text of the note */, Calendar.getInstance() /* operation execution time */, null /* spent time */, null /* deadline */, null /* budget */, securedTaskBean.getId() /* task */, securedTaskBean.getSecure().getUserId() /* submitter of operation */, null /* resolution */, null /* priority */, null /* assignees */, securedTaskBean.getSecure().getUserId() /* assignee */, null /* assignee, if group is to be configured as assignee*/, mstatusId /* type of operation*/, null /* Map with custom fields*/, securedTaskBean.getSecure() /* SessionContext */, null /* attachments*/); /** * execute */ createMessage.create(true); /** * must return SecuredTaskBean */ return securedTaskBean; }}

The compiled class must be saved in the folder**./etc/plugins/scripts/bulk**

![](../images/Multibulk.png)

Then log into TrackStudio and go to the list of sub-tasks. Left lower corner of the page will have a list with Your bulk-scripts, select the script **ProcessNoteOperation**. Select the tasks for which the script needs to be executed. There is a button “Run” on the right side of list, and after this button is pressed, the script will be executed for each selected task.

---

[Home](../index.md) | [Up (Developer's Guide)](index.md)
