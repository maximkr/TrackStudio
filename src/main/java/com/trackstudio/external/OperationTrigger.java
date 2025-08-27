package com.trackstudio.external;

import com.trackstudio.exception.GranException;
import com.trackstudio.secured.SecuredMessageTriggerBean;

/**
 * This interface should be implemented for customers triggers which is used in operations.
 */
public interface OperationTrigger {
    /**
     * This method is executed when user creates new operations
     * @param message {@link com.trackstudio.secured.SecuredMessageTriggerBean}
     * @return treated SecuredMessageTriggerBean
     * @throws GranException for necessery
     */
    public SecuredMessageTriggerBean execute(SecuredMessageTriggerBean message) throws GranException;
}
