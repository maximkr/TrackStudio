package scripts;

import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * TODO: comment
 * @author parsentev
 * @since 25.04.2016
 */
public class AlertToConsole implements Job {
	private static final Logger log = LoggerFactory.getLogger(AlertToConsole.class);

	@Override
	public void execute(JobExecutionContext jobExecutionContext) throws JobExecutionException {
		System.out.println("Alert");
	}
}
