package com.trackstudio.app.adapter.macros;

import java.util.ArrayList;
import java.util.Map;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.regex.Matcher;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.trackstudio.app.adapter.MacrosTaskAdapter;
import com.trackstudio.exception.GranException;
import com.trackstudio.tools.Null;
import com.trackstudio.view.TaskView;

import net.jcip.annotations.Immutable;

@Immutable
public class SecuredMacrosAdapterManager {
    private final static Log log = LogFactory.getLog(SecuredMacrosAdapterManager.class);
    private final CopyOnWriteArrayList<MacrosTaskAdapter> am = new CopyOnWriteArrayList<MacrosTaskAdapter>();
    private final CopyOnWriteArrayList<AbstractOptionPatternMacro> optionPatternMacros = new CopyOnWriteArrayList<AbstractOptionPatternMacro>();

    /**
     * Конструктор
     *
     * @param adapters список адаптеров
     */
    public SecuredMacrosAdapterManager(ArrayList<MacrosTaskAdapter> adapters) {
        am.clear();
        am.addAll(adapters);
        for (MacrosTaskAdapter adp : am) {
            try {
               if (adp instanceof AbstractOptionPatternMacro) optionPatternMacros.add((AbstractOptionPatternMacro)adp);
            } catch (Exception e) {
                log.error("Error", e);
            }
            
        }
        am.removeAll(optionPatternMacros);
    }

	
    /**
     * Вызывает адаптеры
     *
     *
     * @return convert's text
     */
    public String process(TaskView view, String description, Map<String, String[]> parameters) {
        for (MacrosTaskAdapter adp : am) {
            try {
                if (adp instanceof IParametrable) {
                    ((IParametrable) adp).setParameters(parameters);
                }
               description = adp.convert(view, description);
            } catch (Exception e) {
                log.error("Error", e);
            } finally {
                if (adp instanceof IParametrable) {
                    ((IParametrable) adp).setParameters(null);
                }
            }
        }
        return Null.isNotNull(description) ? processOptionPatternMacro(view, description) : description;
    }


	protected String processOptionPatternMacro(TaskView view, String description) {
		Matcher matcher = MacrosTaskAdapter.filterPattern.matcher(description);
		StringBuffer current = new StringBuffer();
		while (matcher.find()) {
		    if ((matcher.group(1) != null || matcher.group(5) != null) &&  ((matcher.group(2) != null && matcher.group(2).trim().length() > 0) || (matcher.group(6) != null && matcher.group(6).trim().length() > 0))   && ((matcher.group(3) != null && matcher.group(3).trim().length()>0) || ((matcher.group(7) != null && matcher.group(7).trim().length()>0))) ){
		        String type = matcher.group(2)!=null ? matcher.group(2) : matcher.group(6);
		        String taskNumber = matcher.group(1)!=null ? matcher.group(1) : matcher.group(5);
		        String filterName = matcher.group(3)!=null ? matcher.group(3) : matcher.group(7);
		        String options = matcher.group(4);
		        for (AbstractOptionPatternMacro macro: optionPatternMacros){
		        	try{
		        	if (macro.match(type)) macro.setContext(type, taskNumber, filterName, options).convertSingle(view, current, matcher);
		        	} catch (GranException e) {
		        		log.error("Error", e);
					}
		        }
		    }
		}
		matcher.appendTail(current);

		if (current.toString().isEmpty()) {
		    return description;
		}  else {
		    return current.toString();
		}
	}
}
