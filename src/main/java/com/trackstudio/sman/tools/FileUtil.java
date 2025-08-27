package com.trackstudio.sman.tools;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.trackstudio.startup.Config;

public class FileUtil {
    private static Log log = LogFactory.getLog(FileUtil.class);
    public static final String DELETED_TASKS = "deleted_tasks.log";

    /**
     * This method adds info about deteled taskd. It is an audit deleted tasks
     * @param text what tasks were deleted and who it did
     */
    public static void addInfoAboutDeletedTasks(String text) {
        File file = new File(Config.getProperty("trackstudio.uploadDir") + "/" + DELETED_TASKS);
        saveText(text, file);
    }

    /**
     * This method adds text in a file
     * @param text text
     * @param file file. if it does not exist it will be created.
     */
    public static void saveText(String text, File file) {
        try {
            PrintWriter pw = new PrintWriter(new OutputStreamWriter(new FileOutputStream(file, true)));
            pw.print(text);
            pw.close();
        } catch (Exception e) {
            log.error("Error", e);
        }
    }
}
