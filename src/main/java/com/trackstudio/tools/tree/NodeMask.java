package com.trackstudio.tools.tree;

import com.trackstudio.exception.GranException;
import com.trackstudio.secured.SecuredTaskBean;

public class NodeMask {
    /**
     * Method gets the tasks' name by mask.
     * if mask is empty, it will return the origin tasks' name.
     * @param task tasks' name.
     * @param treeNode TreeLoaderAction true, SubtaskAction false
     * @return tasks' name
     * @throws GranException if somethings is happened.
     */
    public static String nameByMask(SecuredTaskBean task, boolean treeNode) throws GranException {
        return task.getName();
    }
}
