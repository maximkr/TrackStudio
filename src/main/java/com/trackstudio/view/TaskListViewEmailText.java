package com.trackstudio.view;

import com.trackstudio.exception.GranException;
import com.trackstudio.secured.SecuredTaskBean;
import com.trackstudio.secured.SecuredUDFValueBean;

import net.jcip.annotations.Immutable;

@Immutable
public class TaskListViewEmailText extends TaskViewEmailText {
    public TaskListViewEmailText(SecuredTaskBean task) throws GranException {
        super(task);
    }

    public UDFValueView getUDFValueView(SecuredUDFValueBean bean) throws GranException {
        return new UDFValueViewEmailTextShort(bean);
    }

}
