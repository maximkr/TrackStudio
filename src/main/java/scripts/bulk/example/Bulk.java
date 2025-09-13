package scripts.bulk.example;

import com.trackstudio.exception.GranException;
import com.trackstudio.external.TaskBulkProcessor;
import com.trackstudio.secured.SecuredTaskBean;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * WARNING. This is the demonstration example. if you would like to use it in your product configuration,
 * you should adapt it for you. You should use it careful, it can damage your data.
 */
public class Bulk implements TaskBulkProcessor {
    private static final Logger log = LoggerFactory.getLogger(Bulk.class);

    @Override
    public SecuredTaskBean execute(SecuredTaskBean task) throws GranException {
        log.info("Bulk example");
        // here your actions
        return task;
    }
}
