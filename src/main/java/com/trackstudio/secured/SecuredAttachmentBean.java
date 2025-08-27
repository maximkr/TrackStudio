package com.trackstudio.secured;

import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.Calendar;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

import javax.imageio.ImageIO;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.trackstudio.app.adapter.AdapterManager;
import com.trackstudio.app.session.SessionContext;
import com.trackstudio.exception.GranException;
import com.trackstudio.kernel.cache.AttachmentCacheItem;
import com.trackstudio.kernel.manager.AttachmentManager;
import com.trackstudio.tools.ComponentHouseResizer;
import com.trackstudio.tools.MD5;
import com.trackstudio.tools.PropertyContainer;

import net.jcip.annotations.Immutable;
import net.jcip.annotations.ThreadSafe;

/**
 * Bean which represents file attachment
 */
@ThreadSafe
public abstract class SecuredAttachmentBean extends Secured {

    protected final static Log log = LogFactory.getLog(SecuredAttachmentBean.class);

    protected final String id;
    protected volatile String name;

    protected final String description;
    protected final File file;
    protected final String userId;
    protected volatile String taskId;

    protected final boolean zipHasMore;

    public boolean isZipHasMore() {
        return zipHasMore;
    }

    public List getZippedFiles() {
        return Collections.EMPTY_LIST;
    }

    public String getUserId() {
        return userId;
    }

    public boolean isThumbnailed() {
        String[] extend = new String[] {".gif", ".jpg", ".jpeg", ".bmp", ".png"};
        return isTypeFile(extend);
    }

    public boolean isTxt() {
        String[] extend = new String[] {".txt"};
        return isTypeFile(extend);
    }

    public boolean isWord() {
        String[] extend = new String[] {".doc", ".docx", ".rtf", ".odt"};
        return isTypeFile(extend);
    }

    public boolean isExcel() {
        String[] extend = new String[] {".xls"};
        return isTypeFile(extend);
    }

    public boolean isPdf() {
        String[] extend = new String[] {".pdf"};
        return isTypeFile(extend);
    }

    public boolean isMovie() {
        String[] extend = new String[] {".avi", ".wmv"};
        return isTypeFile(extend);
    }

    private boolean isTypeFile(String[] extend) {
        if (file == null || !file.exists()) {
            return false;
        } else {
            String correctName = getName().toLowerCase(Locale.ENGLISH);
            for (String ext : extend) {
                if (correctName.indexOf(ext) != -1) {
                    return true;
                }
            }
        }
        return false;
    }

    public SecuredUserBean getUser() throws GranException {
        return getUserId() != null ? new SecuredUserBean(getUserId(), getSecure()) : null;
    }

    public String getNameMD5(){
        return MD5.encode(getName());
    }

    private boolean canBeZip(){
        return name.endsWith(".zip") || name.endsWith(".jar") ;
    }

    public SecuredAttachmentBean(AttachmentCacheItem attachment, SessionContext sec) {
        this.id = attachment.getId();
        this.sc = sec;
        this.name = attachment.getName();
        this.description = attachment.getDescription();
        this.userId = attachment.getUserId();
        this.taskId = attachment.getTaskId();
        this.file = attachment.getFile();

        boolean zhm = false;
        this.zipHasMore = false;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getTaskId() {
        return taskId;
    }

    public String getDescription() {
        return this.description;
    }

    public Long getSize() throws GranException {
        if (file != null && !isDeleted() && file.isFile())
            return file.length();
        else
            return -1L;
    }

    public Double getSizeDoubleValue() throws GranException {
        return (double)getSize();
    }

    public Calendar getLastModified() {
        if (file!=null){
            Calendar c = Calendar.getInstance();
            c.setTimeInMillis(file.lastModified());
            return c;
        } else return null;
    }


    public File getFile() {
        return file;
    }

    public byte[] getThumbData(int widthThumnail, int heightThumnail) throws IOException {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        try {
            if (file == null || !file.isFile())
                return null;
            File f = AttachmentManager.getInstance().getAttachmentFile(getTaskId(), getUserId(), getId(), false);
            if (f == null || !f.exists()) {
                return null;
            }
            int width, height;

            BufferedImage image = ImageIO.read(f);
            if (image != null) {
                width = image.getWidth();
                height = image.getHeight();
                if (height > width) {
                    width = heightThumnail * width / height;
                    height = heightThumnail;
                } else {
                    height = widthThumnail * height / width;
                    width = widthThumnail;
                }
                if (height == 0) {
                    height = image.getHeight();
                }
                if (width == 0) {
                    width = image.getWidth();
                }
                ImageIO.write(ComponentHouseResizer.resize(image, width, height), "png", out);
                return out.toByteArray();
            } else {
                return null;
            }
        } catch (IOException e) {
            log.error("Error creating thumbnail buffered image", e);
            return null;
        } finally {
            out.close();
        }
    }

    public Boolean getDeleted() throws GranException {
        return AdapterManager.getInstance().getSecuredAttachmentAdapterManager().getAttachmentIsDeleted(getSecure(), id);
    }

    public Boolean isDeleted() throws GranException {
        return getDeleted();
    }


    protected PropertyContainer getContainer() {
        PropertyContainer pc = container.get();
        if (pc != null)
            return pc; // object in cache, return it

        PropertyContainer newPC = new PropertyContainer();
        newPC.put(getLastModified()).put(getName()).put(getId());

        if (container.compareAndSet(null, newPC)) // try to update
            return newPC; // we can update - return loaded value
        else
            return container.get(); // some other thread already updated it - use saved value
    }

    public boolean isPart() {
        return name.endsWith(".part");
    }


    public String getShortDescription() {
        return description != null && description.length() > 100 ? description.substring(0, 100) + "..." : description;
    }
}
