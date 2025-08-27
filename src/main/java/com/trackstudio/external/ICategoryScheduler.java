package com.trackstudio.external;

import com.trackstudio.kernel.cache.TaskRelatedInfo;

/**
 * This interface should be implement for observed tasks concrete category
 */
public interface ICategoryScheduler {

    /**
     * This method returns a category ID
     * @return category ID
     */
    public String getCategoryId();

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
     * This method defines actions for task.
     * @param task TaskRelatedInfo
     */
    public void execute(TaskRelatedInfo task);
}
