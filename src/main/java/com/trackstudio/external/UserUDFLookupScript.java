package com.trackstudio.external;

import com.trackstudio.exception.GranException;
import com.trackstudio.secured.SecuredUserBean;


/**
 * This interface should be implemented for customet trigger which is used in udf lookup script.
 */
public interface UserUDFLookupScript {
    /**
     * This method is executed when user wants to have some limited list values for users udfs fields
     * @param user selected user {@link com.trackstudio.secured.SecuredUserBean}
     * @return list values for users udfs fields
     * @throws GranException for necessery
     */
    public Object calculate(SecuredUserBean user) throws GranException;
}
