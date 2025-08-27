package com.trackstudio.jmx;

import com.trackstudio.exception.GranException;
import com.trackstudio.jmx.beans.ICacheMXBean;
import com.trackstudio.kernel.cache.TaskRelatedManager;
import com.trackstudio.kernel.cache.UserRelatedManager;

public class CacheMXBeanImpl implements ICacheMXBean {
    @Override
    public long getTotalTasks() throws GranException {
        return TaskRelatedManager.getInstance().getCacheContents().size();
    }

    @Override
    public long getTotalUsers() throws GranException {
        return UserRelatedManager.getInstance().getCacheContents().size();
    }

    @Override
    public String getMemorySizeTasks() throws GranException, IllegalAccessException {
        return ((float) ObjectSizeFetcher.getObjectSize(TaskRelatedManager.getInstance().getCacheContents()) / (1024*1024)) + "Mb";
    }

    @Override
    public String getMemorySizeUsers() throws GranException, IllegalAccessException {
        throw new GranException("TrackStudio 2.0 does not support this operation");
    }
}
