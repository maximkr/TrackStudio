package com.trackstudio.external;

import com.trackstudio.exception.GranException;
import com.trackstudio.secured.SecuredTaskBean;

/**
 * This interface should be implemented for customers triggers which is used in bulks tasks processes.
 */
public interface TaskBulkProcessor {
    /**
     * This method is executed when user makes bulks processes
     * @param task is selected a task {@link com.trackstudio.secured.SecuredTaskBean}
     * @return treated SecuredTaskBean
     * @throws GranException for necessery
     */
    public SecuredTaskBean execute(SecuredTaskBean task) throws GranException;
}
