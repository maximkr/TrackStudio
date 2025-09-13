package scripts.macros.example;

import com.trackstudio.app.adapter.MacrosTaskAdapter;
import com.trackstudio.exception.GranException;
import com.trackstudio.view.TaskView;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * WARNING. This is the demonstration example. if you would like to use it in your product configuration,
 * you should adapt it for you. You should use it careful, it can damage your data.
 */
public class Macros implements MacrosTaskAdapter {
    private static final Logger log = LoggerFactory.getLogger(MacrosTaskAdapter.class);

    @Override
    public String convert(TaskView taskView, String text) throws GranException {
        log.info("MacrosTaskAdapter example");
        // here your actions
        return text;
    }

    @Override
    public boolean init() {
        return true;
    }

    @Override
    public String getDescription() {
        return null;
    }
}
