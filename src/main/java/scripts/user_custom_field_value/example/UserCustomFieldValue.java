package scripts.user_custom_field_value.example;

import com.trackstudio.exception.GranException;
import com.trackstudio.external.UserUDFValueScript;
import com.trackstudio.secured.SecuredUserBean;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * WARNING. This is the demonstration example. if you would like to use it in your product configuration,
 * you should adapt it for you. You should use it careful, it can damage your data.
 */
public class UserCustomFieldValue implements UserUDFValueScript {
    private static final Logger log = LoggerFactory.getLogger(UserCustomFieldValue.class);

    @Override
    public Object calculate(SecuredUserBean user) throws GranException {
        log.info("UserCustomFieldValue example");
        // here your actions
        return user;
    }
}
