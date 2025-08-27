package com.trackstudio.tools;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentSkipListMap;
import java.util.concurrent.CopyOnWriteArrayList;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileUploadException;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.trackstudio.action.TemplateServlet;
import com.trackstudio.app.adapter.AdapterManager;
import com.trackstudio.exception.GranException;
import com.trackstudio.kernel.manager.AttachmentArray;
import com.trackstudio.kernel.manager.SafeString;
import com.trackstudio.secured.SecuredMessageBean;
import com.trackstudio.secured.SecuredTaskBean;
import com.trackstudio.startup.Config;

import net.jcip.annotations.Immutable;

/**
 * Впомогательный класс для загрузки прикладываемых файлов
 */
@Immutable
public class Uploader {

    private static final Log log = LogFactory.getLog(Uploader.class);

    /**
     * Массив для хранения приложенных файлов
     */
    private final CopyOnWriteArrayList<AttachmentArray> attachments = new CopyOnWriteArrayList<AttachmentArray>();
    /**
     * Карта параметров
     */
    private final ConcurrentSkipListMap<String, List<String>> parameterMap = new ConcurrentSkipListMap<String, List<String>>();

    /**
     * Конструктор, получает на вход http-запрос, разбирает его и записывает параметры запроса в соответствующие массивы
     *
     * @param request запрос
     */
    public Uploader(HttpServletRequest request) {
        boolean isMultipart = ServletFileUpload.isMultipartContent(request);
        if (isMultipart) {
            DiskFileItemFactory factory = new DiskFileItemFactory();
            // Set factory constraints
            factory.setSizeThreshold(256000000);
            factory.setRepository(new File(Config.getInstance().getUploadDir()));
            // Create a new file upload handler
            ServletFileUpload upload = new ServletFileUpload(factory);

            // Parse the request
            List<FileItem> items = null;
            try {
                items = upload.parseRequest(request);
            } catch (FileUploadException e) {
                log.error("Exception ", e);
            }
            ArrayList<AttachmentArray> atts = new ArrayList<AttachmentArray>();
            if (items != null)
                for (FileItem item : items) {
                    if (item.isFormField()) {
                        String field = item.getFieldName();
                        String value;
                        try {
                            value = item.getString(Config.getEncoding());
                        } catch (UnsupportedEncodingException e) {
                            value = item.getString();
                        }
                        if (value != null) {
                            if (parameterMap.containsKey(field)) {
                                List<String> values = parameterMap.get(field);
                                values.add(value);
                                parameterMap.put(field, values);
                            } else {
                                List<String> values = new ArrayList<String>();
                                values.add(value);
                                parameterMap.put(field, values);
                            }
                        }
                    } else if (item.getName() != null && item.getName().length() > 0 && item.getContentType() != null) {
                        AttachmentArray arr = null;
                        try {
                            arr = new AttachmentArray(SafeString.createSafeString(item.getName()), SafeString.createSafeString(""), new BufferedInputStream(item.getInputStream()));
                        } catch (IOException e) {
                            log.error("Exception ", e);
                        }
                        atts.add(arr);
                    }
                }
            List<String> descAtt = parameterMap.get(TemplateServlet.ATTACH_DESC);
            List<AttachmentArray> attsTemp = new ArrayList<AttachmentArray>();
            if (descAtt != null) {
                for (int i=0;i!=atts.size();i++) {
                    AttachmentArray attTemp = atts.get(i);
                    if (i < descAtt.size()) {
                        String value = descAtt.get(i);
                        attTemp.setDescription(SafeString.createSafeString(value));
                    }
                    attsTemp.add(attTemp);
                }
            }
            this.attachments.addAll(attsTemp);

        } else {
            Map m = request.getParameterMap();
            for (Object o : m.keySet()) {
                String paramName = o.toString();
                String[] paramValue = (String[]) m.get(paramName);
                parameterMap.put(paramName, Arrays.asList(paramValue));
            }
        }
    }

    /**
     * Возвращает карту параметров
     *
     * @return карта параметров
     */
    public Map<String, List<String>> getParameterMap() {
        return parameterMap;
    }

    /**
     * Возвращает параметр по ключу
     *
     * @param key ключ
     * @return параметр
     */
    public String getParameter(String key) {
        if (getParameterMap().containsKey(key)) {
            Object o = getParameterMap().get(key).get(0);
            return o != null ? o.toString() : null;
        } else return null;
    }

    /**
     * Создает приложенные файлы для указанной задачи
     *
     * @param task задача
     */
    public void upload(SecuredTaskBean task) {
        if (attachments != null && !attachments.isEmpty()) {
            try {
                AdapterManager.getInstance().getSecuredAttachmentAdapterManager().createAttachment(task.getSecure(), task.getId(), null, task.getSecure().getUserId(), attachments);
            } catch (GranException e) {
                log.error("Exception ", e);
            }
        }
    }

    public void upload(SecuredMessageBean message) {
        if (attachments != null && !attachments.isEmpty()) {
            try {
                AdapterManager.getInstance().getSecuredAttachmentAdapterManager().createAttachment(message.getSecure(), null, message.getId(), message.getSecure().getUserId(), attachments);
            } catch (GranException e) {
                log.error("Exception ", e);
            }
        }
    }
}