package com.trackstudio.app.adapter.macros;

import java.util.regex.Matcher;

import com.trackstudio.app.adapter.AdapterManager;
import com.trackstudio.app.adapter.MacrosTaskAdapter;
import com.trackstudio.app.csv.CSVImport;
import com.trackstudio.exception.GranException;
import com.trackstudio.secured.SecuredTaskBean;
import com.trackstudio.secured.SecuredUDFValueBean;
import com.trackstudio.view.TaskView;

import net.jcip.annotations.Immutable;

@Immutable
public class UDFViewMacro  extends  AbstractOptionPatternMacro implements MacrosTaskAdapter{

    @Override
    public boolean init() {
        return true;
    }

    @Override
    public String getDescription() {
        return null;
    }

    @Override
    public void convertSingle(TaskView view, StringBuffer current, Matcher matcher) throws GranException {
        SecuredTaskBean task = AdapterManager.getInstance().getSecuredTaskAdapterManager().findTaskByNumber(view.getTask().getSecure(), taskNumber);
        if (task != null) {
            String fieldId = CSVImport.findUDFIdByName(filterName);
            if (fieldId != null) {
                SecuredUDFValueBean value = task.getUDFValues().get(fieldId);
                if (value!=null){
                    String replacement = view.getUDFValueView(value).getValue(task);
                    replacement = replacement.replaceAll("\\$","\\\\\\$");
                    matcher.appendReplacement(current, replacement);
                }
            }
        }
    }

    @Override
    public boolean match(String t) {
        return "udf".equals(t);
    }
}
