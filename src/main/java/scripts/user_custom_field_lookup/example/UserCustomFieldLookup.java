package scripts.user_custom_field_lookup.example;

import com.trackstudio.exception.GranException;
import com.trackstudio.external.UserUDFLookupScript;
import com.trackstudio.secured.SecuredUserBean;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * WARNING. This is the demonstration example. if you would like to use it in your product configuration,
 * you should adapt it for you. You should use it careful, it can damage your data.
 */
public class UserCustomFieldLookup implements UserUDFLookupScript {
    private static final Logger log = LoggerFactory.getLogger(UserCustomFieldLookup.class);

    @Override
    public Object calculate(SecuredUserBean user) throws GranException {
        log.info("UserCustomFieldLookup example");
        // here your actions
        return user;
    }
}
