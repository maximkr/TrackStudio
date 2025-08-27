package com.trackstudio.app.adapter.macros;

import java.util.regex.Matcher;

import com.trackstudio.app.adapter.MacrosTaskAdapter;
import com.trackstudio.exception.GranException;
import com.trackstudio.view.TaskView;

import net.jcip.annotations.ThreadSafe;

@ThreadSafe
public abstract class AbstractOptionPatternMacro implements MacrosTaskAdapter {
	protected volatile String  taskNumber;
    protected volatile String filterName;
    protected volatile String options;
    protected volatile String type;
	
	
	public abstract boolean match(String t);
	
	public AbstractOptionPatternMacro() {
		// TODO Auto-generated constructor stub
	}

	@Override
	public boolean init() {
		// TODO Auto-generated method stub
		return true;
	}

	@Override
	public String getDescription() {
		// TODO Auto-generated method stub
		return null;
	}
	
	
	public String convert(TaskView view, String description)
			throws GranException {
		Matcher matcher = MacrosTaskAdapter.filterPattern.matcher(description);
        StringBuffer current = new StringBuffer();
        while (matcher.find()) {
            if ((matcher.group(1) != null || matcher.group(5) != null) &&  ((matcher.group(2) != null && matcher.group(2).trim().length() > 0) || (matcher.group(6) != null && matcher.group(6).trim().length() > 0))   && ((matcher.group(3) != null && matcher.group(3).trim().length()>0) || ((matcher.group(7) != null && matcher.group(7).trim().length()>0))) ){
                String type = matcher.group(2)!=null ? matcher.group(2) : matcher.group(6);
                String taskNumber = matcher.group(1)!=null ? matcher.group(1) : matcher.group(5);
                String filterName = matcher.group(3)!=null ? matcher.group(3) : matcher.group(7);
                String options = matcher.group(4);
                	if (match(type)) setContext(type, taskNumber, filterName, options).convertSingle(view, current, matcher);
            }
        }
        matcher.appendTail(current);

        if (current.toString().isEmpty()) {
            return description;
        }  else {
            return current.toString();
        }
	}
	
	
	public abstract void convertSingle(TaskView view, StringBuffer current, Matcher matcher) throws GranException;
	
	public AbstractOptionPatternMacro setContext(String type, String taskNumber, String filterName, String options){
		this.type = type;
		this.taskNumber = taskNumber;
		this.filterName = filterName;
		this.options = options;
		return this;
	}

}
