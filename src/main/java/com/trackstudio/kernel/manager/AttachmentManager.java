/*
 * @(#)AttachmentManager.java
 * Copyright 2009 TrackStudio, Inc. All rights reserved.
 */
package com.trackstudio.kernel.manager;

import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.trackstudio.exception.GranException;
import com.trackstudio.kernel.cache.AttachmentCacheItem;
import com.trackstudio.kernel.cache.TaskRelatedManager;
import com.trackstudio.kernel.lock.LockManager;
import com.trackstudio.model.Attachment;
import com.trackstudio.startup.Config;
import com.trackstudio.tools.ListBreaker;

import net.jcip.annotations.Immutable;

import static com.trackstudio.tools.ParameterValidator.cutToValidName;

/**
 * Manage task/user attachments, chats
 */
@Immutable
public class AttachmentManager extends KernelManager {

    protected static final Log log = LogFactory.getLog(AttachmentManager.class);
    protected static final String className = "AttachmentManager.";
    protected static final AttachmentManager instance = new AttachmentManager();
    private static final LockManager lockManager = LockManager.getInstance();
    /**
     * limit number of param for query IN
     */
    private static final int LIMIT_BATCH = 500;

    /**
     */
    protected AttachmentManager() {
    }

    /**
     */
    public static AttachmentManager getInstance() {
        return instance;
    }


    /**
     */
    public static String getAttachmentDirPath(String taskId, String userId, boolean archive) {
        String dirName = taskId != null ? taskId : "user" + userId;
        String dir = archive ? Config.getInstance().getArchiveDir() : Config.getInstance().getUploadDir();
        if (dirName.length() > 20) {
            String parentDirName = dirName.substring(0, 20);
            String childDirName = dirName.substring(20);
            return dir + File.separator + parentDirName + File.separator + childDirName;
        } else {
            return dir + File.separator + dirName;
        }
    }

    /**
     */
    public File getAttachmentFile(String taskId, String userId, String attId, boolean archive) {

        String dirPath = getAttachmentDirPath(taskId, userId, archive);
        File dir = new File(dirPath);
        if (!dir.exists() && !dir.isDirectory()) {
            return null;
        }
        File file = new File(dirPath + File.separator + attId);
        if (!file.exists()) {
            return null;
        }
        return file;
    }

    /**
     */
    private File createAttachmentFile(String taskId, String userId, String attId) throws IOException {
        String dirPath = getAttachmentDirPath(taskId, userId, false);
        File dir = new File(dirPath);
        if (!dir.exists()) {
            if (!dir.mkdirs()) {
                throw new IOException("Can't create dir " + dir.getName());
            }
        }
        File file = new File(dirPath + File.separator + attId);
        if (!file.exists()) {
            if (!file.createNewFile()) {
                throw new IOException("Can't create file " + file.getName());
            }
        }
        return file;
    }

    /**
     */
    public ArrayList<String> createAttachment(String taskId, String messageId, String userId, final List<AttachmentArray> attachments) throws GranException {
        boolean w = lockManager.acquireConnection(className);
        try {
            ArrayList<String> created = new ArrayList<String>();
            for (AttachmentArray atta : attachments) {
                String attId = createAttachmentHibernate(taskId, messageId, userId, cutToValidName(atta.getName().toString(), atta.getName().toString()), atta.getDescription() != null ? atta.getDescription().toString() : null);
                atta.setInitialID(attId);
                File file;

                File f = getAttachmentFile(taskId, userId, attId, false);
                if (f == null)
                    file = createAttachmentFile(taskId, userId, attId);
                else {
                    throw new GranException("Attachment already exists");
                }
                fileCreate(file, atta.getData(), atta.getLen());
                created.add(attId);
                if (atta.isUploadFromApplet() && atta.getLen() != file.length()) {
                    updateAttachment(attId, atta.getName().append(".part"), atta.getDescription());
                }
            }
            for (String id : created) {
                KernelManager.getIndex().reIndexAttachment(id);
            }
            return created;
        } catch (Exception e) {
            throw new GranException(e);
        } finally {
            if (w) lockManager.releaseConnection(className);
        }
    }

    /**
     */
    public void updateFileAttachment(String taskId, String userId, String attachId, AttachmentArray atta) throws GranException {
        boolean w = lockManager.acquireConnection(className);
        try {
            File file = getAttachmentFile(taskId, userId, attachId, false);
            if (file == null) {
                file = createAttachmentFile(taskId, userId, attachId);
            }
            fileCreate(file, atta.getData(), atta.getLen());
            KernelManager.getIndex().reIndexAttachment(attachId);
        } catch (Exception e) {
            throw new GranException(e);
        } finally {
            if (w) lockManager.releaseConnection(className);
        }
    }

    /**
     */
    public void updateAttachment(String attachmentId, SafeString name, SafeString description) throws GranException {
        boolean w = lockManager.acquireConnection(className);
        Attachment a = FindManager.getInstance().findAttachment(attachmentId);
        try {
            a.setName(name.toString());
            a.setDescription(description != null ? description.toString() : null);
            hu.updateObject(a);
            KernelManager.getIndex().reIndexAttachment(attachmentId);
        } finally {
            if (w) lockManager.releaseConnection(className);
        }
    }


    /**
     */
    private String createAttachmentHibernate(String taskId, String messageId, String userId, String file, String description) throws GranException {
        return hu.createObject(new Attachment(taskId, messageId, userId, file, description));
    }

    /**
     */
    public byte[] getAttachment(String taskId, String userId, String attId) throws GranException {
        boolean r = lockManager.acquireConnection(className);
        try {
            File f = getAttachmentFile(taskId, userId, attId, false);
            FileInputStream sourceStream = new FileInputStream(f);
            byte[] bt = new byte[16384];
            int byteCount;
            ByteArrayOutputStream bis = new ByteArrayOutputStream();
            while ((byteCount = sourceStream.read(bt, 0, 16384)) != -1) {
                bis.write(bt, 0, byteCount);
            }
            sourceStream.close();
            bis.flush();
            byte[] data = bis.toByteArray();
            bis.close();

            return data;
        } catch (IOException e) {
            log.error("Exception ", e);
        } finally {
            if (r) lockManager.releaseConnection(className);
        }
        return null;
    }

    /**
     */
    public void deleteAttachment(String attachmentId) throws GranException {
        deleteAttachment(attachmentId, true);
    }

    /**
     */
    public void deleteAttachment(String attachmentId, boolean reIndex) throws GranException {
        boolean w = lockManager.acquireConnection(className);
        try {
            Attachment att = KernelManager.getFind().findAttachment(attachmentId);
            File f = getAttachmentFile(att.getTask() != null ? att.getTask().getId() : null, att.getUser() != null ? att.getUser().getId() : null, attachmentId, false);
            if (f != null)
                f.delete();
            hu.deleteObject(Attachment.class, attachmentId);
            hu.cleanSession();
            if (reIndex)
                KernelManager.getIndex().deleteAttachment(attachmentId);
        } finally {
            if (w) lockManager.releaseConnection(className);
        }
    }

    /**
     */
    public List<AttachmentCacheItem> getAllAttachmentList() throws GranException {
        log.trace("getAllAttachmentList");
        boolean r = lockManager.acquireConnection(className);
        try {
            return hu.getList("select new com.trackstudio.kernel.cache.AttachmentCacheItem(att.id, att.task.id, att.message.id, att.user.id, att.name, att.description) from com.trackstudio.model.Attachment as att");
        } finally {
            if (r) lockManager.releaseConnection(className);
        }
    }

    /**
     */
    public List<AttachmentCacheItem> getAttachmentList(final String taskId, final String messageId, final String userId) throws GranException {
        log.trace("getAttachmentList(" + taskId + "," + messageId + "," + userId + ")");
        boolean r = lockManager.acquireConnection(className);
        try {
            final List<AttachmentCacheItem> result = new ArrayList<AttachmentCacheItem>();
            List<AttachmentCacheItem> attachments = new ArrayList<AttachmentCacheItem>();
            List list = null;
            if (taskId != null) {
                list = hu.getList("select new com.trackstudio.kernel.cache.AttachmentCacheItem(att.id, att.task.id, att.message.id, att.user.id, att.name, att.description) from com.trackstudio.model.Attachment as att where att.task.id=?", taskId);
            } else if (userId != null) {
                list = hu.getList("select new com.trackstudio.kernel.cache.AttachmentCacheItem(att.id, att.task.id, att.message.id, att.user.id, att.name, att.description) from com.trackstudio.model.Attachment as att where att.user.id=? and att.task.id=null", userId);
            }
            if (list != null) {
                for (AttachmentCacheItem att : (List<AttachmentCacheItem>) list) {
                    att.setFile(getAttachmentFile(taskId, userId, att.getId(), false));
                    if (messageId == null || messageId.equals(att.getMessageId()))
                        attachments.add(att);
                }
                for (AttachmentCacheItem att : attachments) {
                    TaskRelatedManager.getInstance().invalidateAttachmentIsDeleted(att.getId());
                    result.add(att);
                }
            }
            Collections.sort(result);
            return result;
        } finally {
            if (r) lockManager.releaseConnection(className);
        }
    }

    /**
     * This method gets attachments for message ids
     * @param tasksIds list tasks ids
     * @return map message id = list attachments
     * @throws GranException for necessary
     */
    public Map<String, List<AttachmentCacheItem>> getAttachmentsMap(List<String> tasksIds) throws GranException {
        boolean r = lockManager.acquireConnection(className);
        try {
            final Map<String, List<AttachmentCacheItem>> result = new LinkedHashMap<String, List<AttachmentCacheItem>>();
            if (!tasksIds.isEmpty()) {
                for (List<String> subTasksId : new ListBreaker<List<String>>(tasksIds, LIMIT_BATCH)) {
                    Map<String, Collection> params = new LinkedHashMap<String, Collection>();
                    params.put("tasks", subTasksId);
                    List<AttachmentCacheItem> list = hu.getListMap("select new com.trackstudio.kernel.cache.AttachmentCacheItem(att.id, att.task.id, att.message.id, att.user.id, att.name, att.description) from com.trackstudio.model.Attachment as att where att.task.id in (:tasks) and att.message.id is not null", Collections.<String, String>emptyMap(), params);
                    for (AttachmentCacheItem attachment : list) {
                        List<AttachmentCacheItem> items = result.get(attachment.getMessageId());
                        if (items == null) {
                            items = new ArrayList<AttachmentCacheItem>();
                            result.put(attachment.getMessageId(), items);
                        }
                        items.add(attachment);
                    }
                }
            }
            return result;
        } finally {
            if (r) lockManager.releaseConnection(className);
        }
    }

    /**
     */
    public Boolean getAttachmentIsDeleted(String attachmentId) throws GranException {
        boolean w = lockManager.acquireConnection(className);
        try {
            Boolean isDeleted = TaskRelatedManager.getInstance().getAttachmentIsDeleted(attachmentId);
            if (isDeleted == null) {
                Attachment att = FindManager.getInstance().findAttachment(attachmentId);
                if (att.getTask() == null && att.getUser() == null) {
                    return true;
                }
                isDeleted = getAttachmentFile(att.getTask() != null ? att.getTask().getId() : null, att.getUser() != null ? att.getUser().getId() : null, attachmentId, false) == null;
                TaskRelatedManager.getInstance().setAttachmentIsDeleted(attachmentId, isDeleted);
            }
            return isDeleted;
        } finally {
            if (w) lockManager.releaseConnection(className);
        }
    }

    private static void fileCreate(File file, InputStream is, int len) throws GranException {
        OutputStream os = null;
        try {
            os = new BufferedOutputStream(new FileOutputStream(file));
            if (len != 0) {
                int bt;
                int left = len;
                while (left > 0 && (bt = is.read()) != -1) {
                    left--;
                    os.write((byte) bt);
                }
            } else {
                for (int b = is.read(); b != -1; b = is.read()) {
                    os.write(b);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (os != null) {
                try {
                    os.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (len == 0)
                try {
                    is.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
        }
    }

    public boolean existAttachment(String id) throws GranException {
        boolean r = lockManager.acquireConnection(className);
        try {
            return !hu.getList("select attach.id from "+Attachment.class.getName()+" as attach where attach.id=?", id).isEmpty();
        } finally {
            if (r) lockManager.releaseConnection(className);
        }
    }
}