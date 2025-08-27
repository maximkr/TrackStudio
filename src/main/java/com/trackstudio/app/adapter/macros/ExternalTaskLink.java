package com.trackstudio.app.adapter.macros;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.trackstudio.app.adapter.MacrosTaskAdapter;
import com.trackstudio.app.session.SessionContext;
import com.trackstudio.exception.GranException;
import com.trackstudio.startup.Config;
import com.trackstudio.view.TaskView;

import net.jcip.annotations.Immutable;

@Immutable
public class ExternalTaskLink implements MacrosTaskAdapter {
	 
	    @Override
	    public String convert(TaskView view, String description) throws GranException {
	        SessionContext sc = view.getTask().getSecure();
	        
	        
	        StringBuffer sb = new StringBuffer();
	        Pattern filterPattern = Pattern.compile("(?<!#)#\\[(\\w+)\\](\\d+)(?![\\{0-9\\w])");
	        Matcher matcher = filterPattern.matcher(description);
	        while (matcher.find()) {
	            String name = description.substring(matcher.start(), matcher.end()-1);
	            if (description.lastIndexOf("<a", matcher.start()) != -1 && description.indexOf("</a>", matcher.start()) != -1) {
	                int first = description.lastIndexOf("<a", matcher.start());
	                int middle = description.indexOf("</a>", first);
	                int end = description.indexOf("</a>", matcher.start());
	                if (middle == end) {
	                    continue;
	                }
	            }
	            String instance = matcher.group(1);
	            String number = matcher.group(2);
	            String instanceAddress = Config.getInstance().getProperty("trackstudio.external."+instance);
	            if (instanceAddress!=null && instanceAddress.length()>0){
	            
	            
	                String linkByEvent = instanceAddress + "/task/" + number + "?thisframe=true";
	                StringBuilder stringBuilder = new StringBuilder();
	    			stringBuilder.append("<a target=\"_blank\" class=\'internal\' ");
	    			
	    			stringBuilder.append("href=\"");
	    			stringBuilder.append(linkByEvent);
	    			stringBuilder.append("\">");
	    			stringBuilder.append("#");
	    			stringBuilder.append(number);
	    			stringBuilder.append("</a>");
	                matcher.appendReplacement(sb, stringBuilder.toString());
	            
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
