package com.trackstudio.tools.audit.trail;

import java.util.List;

import com.trackstudio.app.session.SessionContext;
import com.trackstudio.exception.GranException;
import com.trackstudio.kernel.manager.KernelManager;
import com.trackstudio.secured.SecuredTaskBean;

import net.jcip.annotations.Immutable;

@Immutable
public class AuditTrailAttachment {
    private final AuditUtil builder;
    private final SessionContext sc;
    private final SecuredTaskBean currentTask;

    public AuditTrailAttachment(SessionContext sc, String taskId, AuditUtil builder) throws GranException {
        this.sc = sc;
        this.currentTask = new SecuredTaskBean(taskId, sc);
        this.builder = builder;
    }

    public void auditAttachment(List<String> attachment) throws GranException {
        if (builder.isCreateMessage()) {
            for (String attachId : attachment) {
                builder.addText("<tr><td>");
                builder.addText(KernelManager.getFind().findAttachment(attachId).getName());
                builder.addText("</td></tr>");
            }
            builder.createLogMessage(sc, currentTask.getId(), builder.getTable());
        }
    }
}
