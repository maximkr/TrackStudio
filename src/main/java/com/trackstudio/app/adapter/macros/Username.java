package com.trackstudio.app.adapter.macros;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.trackstudio.app.adapter.MacrosTaskAdapter;
import com.trackstudio.app.session.SessionContext;
import com.trackstudio.exception.GranException;
import com.trackstudio.kernel.cache.UserRelatedManager;
import com.trackstudio.kernel.manager.KernelManager;
import com.trackstudio.secured.SecuredUserBean;
import com.trackstudio.view.TaskView;

import net.jcip.annotations.Immutable;

@Immutable
public class Username implements MacrosTaskAdapter {


    @Override
    /**
     * @root max@gmail.com (@root) (max.v@gmail.com)
     */
    public String convert(TaskView view, String description) throws GranException {

        StringBuffer sb = new StringBuffer();
        Pattern usernamePattern = Pattern.compile("@(\\w+)(\\.?)(\\w+)(\\.?)(\\w+)");
        Matcher matcher = usernamePattern.matcher(description);
        SessionContext sc = view.getTask().getSecure();
        while (matcher.find()) {
            String login = matcher.group().substring(1);
            String userId = KernelManager.getUser().findByLogin(login);
            if (userId != null) {
                matcher.appendReplacement(sb, view.getUserView(new SecuredUserBean(UserRelatedManager.getInstance().find(userId), sc)).getPath());
            }
        }
        matcher.appendTail(sb);
        if (sb.toString().isEmpty()) {
            return description;
        }  else {
            return sb.toString();
        }
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
