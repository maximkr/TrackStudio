package com.trackstudio.external;

import com.trackstudio.exception.GranException;
import com.trackstudio.secured.SecuredTaskTriggerBean;


/**
 * This interface should be implemented for customers triggers which user creates or updates some task.
 */
public interface TaskTrigger {
    /**
     * This method is executed when user creates or updates some task
     * @param task created or updated task {@link com.trackstudio.secured.SecuredTaskTriggerBean}
     * @return treated task
     * @throws GranException for necessary
     */
    public SecuredTaskTriggerBean execute(SecuredTaskTriggerBean task) throws GranException;
}
