package scripts.multibulk.example;

import com.trackstudio.exception.GranException;
import com.trackstudio.external.TaskMultiBulkProcessor;
import com.trackstudio.secured.SecuredTaskBean;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

/**
 * WARNING. This is the demonstration example. if you would like to use it in your product configuration,
 * you should adapt it for you. You should use it careful, it can damage your data.
 */
public class MultiBulk implements TaskMultiBulkProcessor {
    private static final Logger log = LoggerFactory.getLogger(MultiBulk.class);

    @Override
    public void execute(List<SecuredTaskBean> tasks) throws GranException {
        log.info("MultiBulk example");
        // here your actions
    }
}
