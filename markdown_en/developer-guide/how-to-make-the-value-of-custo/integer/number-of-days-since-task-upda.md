[Home](../../../index.md) | [Up (Integer)](index.md)

---

# Number of days since task update

Script returns the number of days since last update of the open task.

package scripts.task_custom_field_value;import com.trackstudio.exception.GranException;import com.trackstudio.external.TaskUDFValueScript;import com.trackstudio.secured.SecuredTaskBean;import java.util.Calendar;/** * Returns the number of days since last update of the open task. * Task is considered to be closed which is in the final state. * 0 is returned for it. */public class DaysSinceUpdate implements TaskUDFValueScript { public Object calculate(SecuredTaskBean securedTaskBean) throws GranException { if (!securedTaskBean.getStatus().isFinish()) { Calendar now = Calendar.getInstance(); now.set(Calendar.HOUR_OF_DAY, 0); now.set(Calendar.MINUTE, 0); now.set(Calendar.SECOND, 0); now.set(Calendar.MILLISECOND, 0); Calendar update = securedTaskBean.getUpdatedate(); update.set(Calendar.HOUR_OF_DAY, 0); update.set(Calendar.MINUTE, 0); update.set(Calendar.SECOND, 0); update.set(Calendar.MILLISECOND, 0); long l = now.getTimeInMillis() - update.getTimeInMillis(); return (int) (l / (24 * 60 * 60 * 1000)); } else return 0; }}

---

[Home](../../../index.md) | [Up (Integer)](index.md)
