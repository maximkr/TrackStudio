package com.trackstudio.external;

import com.trackstudio.exception.GranException;
import com.trackstudio.secured.SecuredUserBean;

/**
 * This interface should be implemented for customet trigger which is used in udfs fields.
 */
public interface UserUDFValueScript {
    /**
     * This method is used when user wants to fillfull a value for users udfs fields
     * @param user selected user {@link com.trackstudio.secured.SecuredUserBean}
     * @return value for udfs fields
     * @throws GranException for necessery
     */
    public Object calculate(SecuredUserBean user) throws GranException;
}
