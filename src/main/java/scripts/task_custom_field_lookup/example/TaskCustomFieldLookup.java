package scripts.task_custom_field_lookup.example;

import com.trackstudio.exception.GranException;
import com.trackstudio.external.TaskUDFLookupScript;
import com.trackstudio.secured.SecuredTaskBean;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * WARNING. This is the demonstration example. if you would like to use it in your product configuration,
 * you should adapt it for you. You should use it careful, it can damage your data.
 */
public class TaskCustomFieldLookup implements TaskUDFLookupScript {
    private static final Logger log = LoggerFactory.getLogger(TaskCustomFieldLookup.class);

    @Override
    public Object calculate(SecuredTaskBean task) throws GranException {
        log.info("TaskCustomFieldLookup example");
        // here your actions
        return task;
    }
}
