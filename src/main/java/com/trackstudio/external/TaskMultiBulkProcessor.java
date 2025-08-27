package com.trackstudio.external;

import java.util.List;

import com.trackstudio.exception.GranException;
import com.trackstudio.secured.SecuredTaskBean;

/**
 * This interface should be implemented for customers triggers which is used in multi bulks tasks processes.
 */
public interface TaskMultiBulkProcessor {
    /**
     * This method is executed when user makes multi bulk processes
     * @param tasks list selected tasks @{link com.trackstudio.secured.SecuredTaskBean}
     * @throws GranException for necessery
     */
    public void execute(List<SecuredTaskBean> tasks) throws GranException;
}
