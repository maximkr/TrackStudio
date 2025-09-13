package scripts.before_edit_task.example;

import com.trackstudio.exception.GranException;
import com.trackstudio.external.TaskTrigger;
import com.trackstudio.secured.SecuredTaskTriggerBean;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * WARNING. This is the demonstration example. if you would like to use it in your product configuration,
 * you should adapt it for you. You should use it careful, it can damage your data.
 */
public class BeforeEditTask implements TaskTrigger {
    private static final Logger log = LoggerFactory.getLogger(BeforeEditTask.class);

    @Override
    public SecuredTaskTriggerBean execute(SecuredTaskTriggerBean task) throws GranException {
        log.info("BeforeEditTask example");

        // here your actions
        return task;
    }
}
