package scripts.before_add_message.example;

import com.trackstudio.exception.GranException;
import com.trackstudio.external.OperationTrigger;
import com.trackstudio.secured.SecuredMessageTriggerBean;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * WARNING. This is the demonstration example. if you would like to use it in your product configuration,
 * you should adapt it for you. You should use it careful, it can damage your data.
 */
public class BeforeAddMessage implements OperationTrigger {
    private static final Logger log = LoggerFactory.getLogger(BeforeAddMessage.class);

    @Override
    public SecuredMessageTriggerBean execute(SecuredMessageTriggerBean message) throws GranException {
        log.info("BeforeAddMessage example");
        // here your actions
        return message;
    }
}
