package scripts.scheduler.example;

import com.trackstudio.external.IGeneralScheduler;

public class CommonJob implements IGeneralScheduler {

	@Override
	public String getClassName() {
		return this.getClass().getName();
	}

    @Override
    public String getCronTime() {
        return "0 0/1 * * * ?"; //every minute
    }

    @Override
    public String getName() {
        return "scheduler example";
    }

    @Override
    public String execute() {
        return "Executed successfully";
    }

    @Override
    public boolean isUse() {
        return true;
    }

    @Override
    public void shutdown() {

    }
}
