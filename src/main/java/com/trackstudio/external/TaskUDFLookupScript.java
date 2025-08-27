package com.trackstudio.external;

import com.trackstudio.exception.GranException;
import com.trackstudio.secured.SecuredTaskBean;


/**
 * This interface should be implemented for customers triggers which is used in udf lookup script.
 */
public interface TaskUDFLookupScript {
    /**
     * This method is executed when user wants to have some limited list values for udfs fields
     * @param task selected task {@link com.trackstudio.secured.SecuredTaskBean}
     * @return list values for udfs fields
     * @throws GranException for necessery
     */
    public Object calculate(SecuredTaskBean task) throws GranException;
}
