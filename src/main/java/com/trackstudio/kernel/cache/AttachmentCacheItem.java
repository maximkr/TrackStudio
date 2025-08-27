package com.trackstudio.kernel.cache;

import java.io.File;
import java.io.Serializable;
import java.util.Locale;

import com.trackstudio.exception.GranException;
import com.trackstudio.kernel.manager.KernelManager;
import com.trackstudio.tools.Intern;

import net.jcip.annotations.ThreadSafe;

/**
 * Вспомогательный класс, используется для кеширования приложенных файлов
 */
@ThreadSafe
public class AttachmentCacheItem implements Comparable, Serializable {

    private final String id;
    private final String taskId;
    private final String messageId;
    private final String userId;
    private final String name;
    private final String description;
    private volatile File file;

    /**
     * Конструктор
     *
     * @param id          ID приложенного файла
     * @param taskId      ID задачи, к которой прилоежен файл
     * @param messageId   ID сообщения, к которому приложен файл
     * @param userId      ID пользователя, к которому прилоежен файл
     * @param name        Название прилоежнного файла
     * @param description Описание прилоежнного файла
     */
    public AttachmentCacheItem(String id, String taskId, String messageId, String userId, String name, String description) {
        this.id = Intern.process(id);
        this.taskId = Intern.process(taskId);
        this.messageId = Intern.process(messageId);
        this.userId = Intern.process(userId);
        this.name = Intern.process(name);
        this.description = Intern.process(description);
    }


    /**
     * Устанавливает прилоеженный файл
     *
     * @param file файл
     */
    public void setFile(File file) {
        this.file = file;
    }

    /**
     * Возвращает ID сообщения, к которому приложен файл
     *
     * @return ID сообщения
     */
    public String getMessageId() {
        return messageId;
    }

    /**
     * Возвращает ID прилоежнного файла
     *
     * @return ID файла
     */
    public String getId() {
        return id;
    }

    /**
     * ВОзвращает ID задачи, к которой прилоежн файл
     *
     * @return ID задачи
     */
    public String getTaskId() {
        return taskId;
    }

    /**
     * Возвращает ID пользователя, к которому прилоежн файл
     *
     * @return ID пользователя
     */
    public String getUserId() {
        return userId;
    }

    /**
     * Возвращает название прилоежнного файла
     *
     * @return название
     */
    public String getName() {
        return name;
    }

    /**
     * Возвращает описание прилоежнного файла
     *
     * @return описание
     */
    public String getDescription() {
        return description;
    }

    /**
     * Возвращает прилоежнный файл
     *
     * @return файл
     */
    public File getFile() {
        return file;
    }

    /**
     * Сравнивает два объекта текущего класса
     *
     * @param o Сравниваемый обхект
     * @return +1, 0 или -1
     */
    public int compareTo(Object o) {
        if (getFile() == null)
            return -1;
        if (((AttachmentCacheItem) o).getFile() == null)
            return 1;
        return getFile().compareTo(((AttachmentCacheItem) o).getFile());
    }

    /**
     * Сравнивает два обхекта текущего класса
     *
     * @param o Скравниваемый обхект
     * @return TREU если равны, FALSE если нет
     */
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        final AttachmentCacheItem that = (AttachmentCacheItem) o;

        if (!id.equals(that.id)) return false;
        if (!taskId.equals(that.taskId)) return false;
        if (!messageId.equals(that.messageId)) return false;
        if (!userId.equals(that.userId)) return false;

        return true;
    }

    /**
     * Возвращает hash code
     *
     * @return hashCode
     */
    public int hashCode() {
        int result;
        result = id.hashCode();
        result = 29 * result + (messageId != null ? messageId.hashCode() : ((taskId != null) ? taskId.hashCode() : userId.hashCode()));
        return result;
    }

    // don't remove, used in MessageTile.jsp
    public boolean isDeleted() throws GranException {
        return KernelManager.getAttachment().getAttachmentIsDeleted(id);
    }

    // don't remove, used in MessageTile.jsp
    public boolean isThumbnailed() throws GranException {
        String[] extend = new String[] {".gif", ".jpg", ".jpeg", ".bmp", ".png"};
        return isTypeFile(extend);
    }

    private boolean isTypeFile(String[] extend) throws GranException {
        if (!isDeleted()) {
            String correctName = getName().toLowerCase(Locale.ENGLISH);
            for (String ext : extend) {
                if (correctName.indexOf(ext) != -1) {
                    return true;
                }
            }
        }
        return false;
    }
}
