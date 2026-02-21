[Home](../index.md) | [Up (Developer's Guide)](index.md)

---

# How to Make Dynamic List of Possible Values of Custom Field

For making the selection of values possible for custom field from the dropdown list, you will need to create the list itself of type “String” and connect it with the script, corresponding to the interface

**com.trackstudio.external.TaskUDFLookupScript**.

Here is an example of script, which displays the list of sprints, which can store the history (For Details : о [SCRUM in TrackStudio](http://www.trackstudio.com/scrum.html))

package scripts.task_custom_field_lookup;import com.trackstudio.external.TaskUDFLookupScript;import com.trackstudio.secured.SecuredTaskBean;import com.trackstudio.exception.GranException;import com.trackstudio.app.adapter.AdapterManager;import java.util.List;import java.util.ArrayList;import scripts.CommonScrum;/** * Displays the list of sprints for selection */public class SprintList extends CommonScrum implements TaskUDFLookupScript{ public Object calculate(SecuredTaskBean task) throws GranException { List list = new ArrayList(); list.add(""); String category = SCRUM_SRINT_CATEGORY; List sprints = AdapterManager.getInstance().getSecuredTaskAdapterManager().getTaskListByQuery(task.getSecure(), "SELECT t.id FROM com.trackstudio.model.Task as t WHERE t.category.id = \'"+category+"\'"); for (SecuredTaskBean t: sprints){ if (t.canView() && !t.getStatus().isFinish()){ list.add(t.getName()+" [#"+t.getNumber()+"]"); } } return list; }}

---

[Home](../index.md) | [Up (Developer's Guide)](index.md)
