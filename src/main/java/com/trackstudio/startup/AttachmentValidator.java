package com.trackstudio.startup;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.channels.FileChannel;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import net.jcip.annotations.Immutable;

/**
 * Класс для проверки приложенный файлов на жестком диске. В случае если найдены файлы в старом формате, то класс преобразует их в новый формат
 */
@Immutable
public class AttachmentValidator {

    private static final Log log = LogFactory.getLog(AttachmentValidator.class);
    private static final AttachmentValidator ourInstance = new AttachmentValidator();

    /**
     * Возвращает экземпляр текущего класса
     *
     * @return экземпляр AttachmentValidator
     */
    public static AttachmentValidator getInstance() {
        return ourInstance;
    }

    private AttachmentValidator() {
    }

    /**
     * Вроизводит валидацию прилоежнных файлов
     */
    public void validate() {
        log.trace("#######");
        log.info("Checking attachments");
        String uploadDirPath = Config.getInstance().getUploadDir();
        File uploadDir = new File(uploadDirPath);
        if (uploadDir.exists()) {
            for (File dir : uploadDir.listFiles()) {
                if (dir.isDirectory()) {
                    String dirName = dir.getName();
                    if (dirName.length() > 20) {
                        String parentDirName = dirName.substring(0, 20);
                        String childDirName = dirName.substring(20, dirName.length());
                        String parentDirPath = uploadDirPath + File.separator + parentDirName;
                        String childDirPath = uploadDirPath + File.separator + parentDirName + File.separator + childDirName;
                        File parentDir = new File(parentDirPath);
                        if (!parentDir.exists()) {
                            parentDir.mkdirs();
                        }
                        File childDir = new File(childDirPath);
                        if (!childDir.exists()) {
                            childDir.mkdirs();
                        }
                        for (File source : dir.listFiles()) {
                            try {
                                copyFile(source, new File(childDirPath + File.separator + source.getName()));
                            } catch (IOException e) {
                                log.error("File " + source.getAbsolutePath(), e);
                            }
                        }
                        deleteDir(dir);
                    }
                }
            }
        } else {
            log.error("uploads dir not found");
        }
        log.debug("validateAttachments() done");
    }

    private void deleteDir(File dir) {
        if (!dir.isDirectory()) {
            dir.delete();
        } else {
            File[] fs = dir.listFiles();
            if (fs != null && fs.length > 0) {
                for (File file : dir.listFiles()) {
                    deleteDir(file);
                }
            }
            dir.delete();
        }
    }

    private void copyFile(File from, File to) throws IOException {
        FileInputStream src = new FileInputStream(from);
        FileOutputStream dest = new FileOutputStream(to);
        FileChannel srcChannel = src.getChannel();
        FileChannel destChannel = dest.getChannel();
        srcChannel.transferTo(0, srcChannel.size(), destChannel);
        src.close();
        dest.close();
    }
}
