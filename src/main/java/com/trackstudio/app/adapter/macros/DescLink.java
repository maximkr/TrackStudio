package com.trackstudio.app.adapter.macros;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.trackstudio.app.adapter.MacrosTaskAdapter;
import com.trackstudio.exception.GranException;
import com.trackstudio.kernel.cache.TaskRelatedManager;
import com.trackstudio.kernel.manager.TaskManager;
import com.trackstudio.view.TaskView;

/**
 * This macros gets a description from task and load in to desc in current task.
 * Usage DescLink{number}
 */
public class DescLink implements MacrosTaskAdapter {

    private final Pattern pt = Pattern.compile("DescLink\\{(\\d+)\\}");
    /**
     * ${@inheritDoc}
     */
    @Override
    public String convert(TaskView view, String description) throws GranException {
        StringBuffer sb = new StringBuffer();
        Matcher matcher = pt.matcher(description);
        while (matcher.find()) {
            String number = matcher.group(1);
            String taskId = TaskManager.getTask().findByNumber(number);
            matcher.appendReplacement(sb,
                    Matcher.quoteReplacement(
                            TaskRelatedManager.getInstance().find(taskId).getDescription()
                    )
            );
        }
        matcher.appendTail(sb);
        if (sb.toString().isEmpty()) {
            return description;
        }  else {
            return sb.toString();
        }
    }

    /**
     * ${@inheritDoc}
     */
    @Override
    public boolean init() {
        return true;
    }

    /**
     * ${@inheritDoc}
     */
    @Override
    public String getDescription() {
        return null;
    }
}
