package com.trackstudio.external;

/**
 * This interface should be implement general scheduler job
 */
public interface IGeneralScheduler {

	/**
	 * Return full class name.
	 * @return full class name
	 */
	String getClassName();

    /**
     * This method returns a mask for cron
     * @return mask cron time. For example 0 1 * * *
     */
    public String getCronTime();

    /**
     * This method returns a name of job
     * @return names job
     */
    public String getName();

    /**
     * This method implements jobs actions
     */
    public String execute() throws Exception;

    /**
     * Switcher
     * @return true if scheduler should be used
     */
    public boolean isUse();

    /**
     * This method executed before shutdown
     */
    public void shutdown();

}
