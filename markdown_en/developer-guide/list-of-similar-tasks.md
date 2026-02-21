[Home](../index.md) | [Up (Developer's Guide)](index.md)

---

# List of Similar Tasks

Script returns the list of similar tasks. It is useful during communication with the users through TrackStudio

package scripts.task_custom_field_value;import com.trackstudio.app.adapter.AdapterManager;import com.trackstudio.exception.GranException;import com.trackstudio.external.TaskUDFValueScript;import com.trackstudio.secured.SecuredSearchItem;import com.trackstudio.secured.SecuredSearchTaskItem;import com.trackstudio.secured.SecuredTaskBean;import org.apache.commons.logging.Log;import org.apache.commons.logging.LogFactory;import java.util.*;/** * Returns the list of similar tasks. */public class AutoSimilarTasks implements TaskUDFValueScript { public static int LIMIT=5; public Object calculate(SecuredTaskBean securedTaskBean) throws GranException { HashMap tasks = AdapterManager.getInstance().getSecuredTaskAdapterManager().findSimilar(securedTaskBean.getSecure(), securedTaskBean.getId()); ArrayList results = new ArrayList(); for (Map.Entry e : tasks.entrySet()) { Float ratio = (Float) e.getValue(); SecuredSearchTaskItem sstask = new SecuredSearchTaskItem(0, ratio, (SecuredTaskBean) e.getKey(), "", ""); results.add(sstask); } Collections.sort(results); List taskIds = new ArrayList(); for (int i=0; i<LIMIT && i<results.size(); i++){ taskIds.add("#"+results.get(i).getTask().getNumber()); } return taskIds; }}

---

[Home](../index.md) | [Up (Developer's Guide)](index.md)
