package com.trackstudio.app.adapter.macros;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;

import com.trackstudio.app.adapter.AdapterManager;
import com.trackstudio.app.adapter.MacrosTaskAdapter;
import com.trackstudio.app.filter.TaskFValue;
import com.trackstudio.app.session.SessionContext;
import com.trackstudio.exception.GranException;
import com.trackstudio.secured.SecuredFilterBean;
import com.trackstudio.secured.SecuredTaskBean;
import com.trackstudio.tools.textfilter.MacrosUtil;
import com.trackstudio.view.TaskView;
import com.trackstudio.view.TaskViewEmailHTML;
import com.trackstudio.view.TaskViewEmailText;

import net.jcip.annotations.Immutable;

import static com.trackstudio.tools.textfilter.MacrosUtil.getListTask;

@Immutable
public class ChartFactory extends AbstractOptionPatternMacro implements MacrosTaskAdapter {

    protected static final String BURNDOWN= "burndownchart";
    protected static final String PERSONALACTIVITY= "personalactivitychart";
    protected static final String TEAMALACTIVITY= "teamactivitychart";
    protected static final String TREND= "trendchart";
    protected static final String STATE= "statechart";
    protected static final String RESOLUTION= "resolutionchart";
    protected static final String WORKLOAD= "workloadchart";
    protected static final String TIME= "spenttimechart";

    
    
    protected static SecuredFilterBean getFilterId(SecuredTaskBean task, String name) throws GranException{
        ArrayList<SecuredFilterBean> filters = AdapterManager.getInstance().getSecuredFilterAdapterManager().getTaskFilterList(task.getSecure(), task.getId());
        for (SecuredFilterBean b : filters){
            if (b.getName().equals(name)) return b;
        }
        return null;
    }

    @Override
    public void convertSingle(TaskView view, StringBuffer current, Matcher matcher) throws GranException{
                SessionContext sc = view.getTask().getSecure();
				SecuredTaskBean task = AdapterManager.getInstance().getSecuredTaskAdapterManager().findTaskByNumber(sc, taskNumber);
                if (task != null && task.canView()) {
                	if (view instanceof TaskViewEmailHTML || view instanceof TaskViewEmailText){
                		matcher.appendReplacement(current, "");
                	} else{
                    String filterId = MacrosUtil.getFilterId(filterName, task.getId(), sc.getUserId(), true);
                    if (filterId != null) {
                        TaskFValue taskFValue = AdapterManager.getInstance().getSecuredFilterAdapterManager().getTaskFValue(sc, filterId).getFValue();
                        List<SecuredTaskBean> tasks = getListTask(task, taskFValue, filterId);
                        Chart c = null;
                        if (type.equals(BURNDOWN))
                            c = new BurndownChart(task, tasks, options);
                        else if (type.equals(PERSONALACTIVITY))
                            c = new PersonalActivityChart(task, tasks, options);
                        else if (type.equals(TEAMALACTIVITY))
                            c = new TeamActivityChart(task, tasks, options);
                        else if (type.equals(TREND))
                            c = new TrendChart(task, tasks, options);
                        else if (type.equals(TIME))
                            c = new SpentTimeChart(task, tasks, options);
                        else if (type.equals(WORKLOAD))
                            c = new WorkloadChart(task, tasks, options);
                        else if (type.equals(STATE))
                            c = new StateChart(task, tasks, options);
                        else if (type.equals(RESOLUTION))
                            c = new ResolutionChart(task, tasks, options);

                        if (c!=null)
                            matcher.appendReplacement(current, c.calculate());
                    }
                	}
                }
            
    }

    @Override
    public boolean init() {
        return true;
    }

    @Override
    public String getDescription() {
        return null;
    }

	@Override
	public boolean match(String t) {
		
		return BURNDOWN.equals(t) || PERSONALACTIVITY.equals(t) 
				|| TEAMALACTIVITY.equals(t) || TREND.equals(t) 
				|| TIME.equals(t) || WORKLOAD.equals(t) 
				|| STATE.equals(t) ||  RESOLUTION.equals(t);
	}
}
