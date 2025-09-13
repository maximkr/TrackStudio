package scripts.task_custom_field_value.example;

import com.trackstudio.exception.GranException;
import com.trackstudio.external.TaskUDFValueScript;
import com.trackstudio.secured.SecuredTaskBean;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * WARNING. This is the demonstration example. if you would like to use it in your product configuration,
 * you should adapt it for you. You should use it careful, it can damage your data.
 */
public class TaskCustomFieldValue implements TaskUDFValueScript {
    private static final Logger log = LoggerFactory.getLogger(TaskCustomFieldValue.class);

    @Override
    public Object calculate(SecuredTaskBean task) throws GranException {
        log.info("TaskCustomFieldValue example");
        // here your actions
        return task;
    }
}
