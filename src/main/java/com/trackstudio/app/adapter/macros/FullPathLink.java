package com.trackstudio.app.adapter.macros;

import com.trackstudio.app.adapter.MacrosTaskAdapter;
import com.trackstudio.exception.GranException;
import com.trackstudio.secured.SecuredTaskBean;
import com.trackstudio.view.TaskView;

import net.jcip.annotations.Immutable;

@Immutable
public class FullPathLink extends DoubleSharp implements MacrosTaskAdapter {
    public String buildLink(TaskView view, SecuredTaskBean task) throws GranException {
        return view.getView(task).getName();
    }
}
