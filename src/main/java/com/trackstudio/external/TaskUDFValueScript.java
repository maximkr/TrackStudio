package com.trackstudio.external;

import com.trackstudio.exception.GranException;
import com.trackstudio.secured.SecuredTaskBean;

/**
 * This interface should be implemented for customers triggers which is used in tasks udfs fields.
 */
public interface TaskUDFValueScript {
    /**
     * This method is used when user wants to fillfull a value for udfs fields 
     * @param task selected task {@link com.trackstudio.secured.SecuredTaskBean}
     * @return value for tasks udfs fields
     * @throws GranException for necessery
     */
    public Object calculate(SecuredTaskBean task) throws GranException;
}
