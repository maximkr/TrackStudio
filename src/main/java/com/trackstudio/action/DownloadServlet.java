package com.trackstudio.action;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URLEncoder;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.Optional;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.trackstudio.app.adapter.AdapterManager;
import com.trackstudio.secured.SecuredAttachmentBean;
import com.trackstudio.secured.SecuredTaskAttachmentBean;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.trackstudio.app.session.SessionContext;
import com.trackstudio.exception.GranException;
import com.trackstudio.kernel.cache.Action;
import com.trackstudio.kernel.cache.AttachmentCacheItem;
import com.trackstudio.kernel.cache.TaskRelatedInfo;
import com.trackstudio.kernel.cache.TaskRelatedManager;
import com.trackstudio.kernel.cache.UserRelatedInfo;
import com.trackstudio.kernel.cache.UserRelatedManager;
import com.trackstudio.kernel.lock.LockManager;
import com.trackstudio.kernel.manager.KernelManager;
import com.trackstudio.startup.Config;
import com.trackstudio.tools.Null;

public class DownloadServlet extends HttpServlet {
    private static final Log log = LogFactory.getLog(DownloadServlet.class);
    private static final LockManager lockManager = LockManager.getInstance();


    public void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        doGet(request, response);
    }

    private static class CheckerDirectUrl {
        private final boolean checkUrl;
        private final SessionContext sc;

        private CheckerDirectUrl(HttpServletRequest request, HttpServletResponse response) throws GranException {
            this.checkUrl = !"true".equals(Config.getProperty("trackstudio.allow.direct.url"));
            if (this.checkUrl) {
                this.sc = GeneralAction.getInstance().imports(request, response);
            } else {
                this.sc = null;
            }
        }

        private boolean validSessionContext() {
            return !checkUrl || this.sc != null;
        }

        private boolean accessUserAttach(String userId) throws GranException {
            return !checkUrl || sc.canAction(Action.viewUserAttachments, userId) && sc.allowedByUser(userId);
        }

        private boolean accessTaskAttach(String taskId) throws GranException {
            return !checkUrl || sc.canAction(Action.viewTaskAttachments, taskId) && sc.allowedByACL(taskId);
        }
    }

    public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        ServletOutputStream out = null;
        boolean w = lockManager.acquireConnection();
        try {
            CheckerDirectUrl checker = new CheckerDirectUrl(request, response);
            if (!checker.validSessionContext()) {
                response.sendError(403);
                return;
            }
            out = response.getOutputStream();
            boolean imageViewer = "image".equals(request.getParameter("type"));
            response.setCharacterEncoding(Config.getEncoding());
            String url = request.getPathInfo();
            String[] names = url.split("/");
            boolean zip = names[3].equals("zipped");
            AttachmentCacheItem item = findFile(checker, names, request.getParameter("archive") != null);
            if (item == null || item.getFile() == null || !item.getFile().exists()) {
                log.error("File not found or you don't have access for a attachment!");
                response.sendError(403);
                return;
            }
            File file = item.getFile();
            if (imageViewer) {
                response.setContentType("image/png; charset=" + Config.getEncoding());
            } else {
                response.setContentType("text/plain; charset=" + Config.getEncoding());
            }
            String name = item.getName();
            if (zip) {
                name = names[names.length - 1].replaceAll(" ", "_");
            }
            if (!zip) {
                response.setContentLength((int) file.length());
            }
            if (!imageViewer) buildHeaderForBrowser(request, response, name);
            if (zip) {
                downloadZip(out, file, name);
            } else {
                download(out, file);
            }
        } catch (Exception ex) {
            log.error("Error",ex);
            request.setAttribute("javax.servlet.jsp.jspException", ex);
            RequestDispatcher requestDispatcher = request.getRequestDispatcher("/jsp/Error.jsp");
            requestDispatcher.forward(request, response);
        } finally {
            if (out != null) {
                out.flush();
                out.close();
            }
            if (w) lockManager.releaseConnection();
        }
    }

    private AttachmentCacheItem findFile(CheckerDirectUrl checker, String[] names, boolean archive) {
        if (names[1].equals("task")) {
            if (names[3].equals("zipped")) {
                return getAttachmentTask(checker, names[2], names[4], archive);
            }
            return getAttachmentTask(checker, names[2], names[3], archive);
        }
        if (names[1].equals("user")) {
            if (names[3].equals("zipped")) {
                return getAttachmentUser(checker, names[2], names[4]);
            }
            return getAttachmentUser(checker, names[2], names[3]);
        } else {
            return null;
        }
    }

    private AttachmentCacheItem getAttachmentTask(CheckerDirectUrl checker, String number, String attachId, boolean archive) {
        try {
            if (archive) {
                List<SecuredTaskAttachmentBean> atts = AdapterManager.getInstance().getSecuredTaskAdapterManager().archiveByNumber(checker.sc, number).getAtts();
                List<AttachmentCacheItem> items = new ArrayList<>();
                for (SecuredTaskAttachmentBean att : atts) {
                    items.add(att.getItem());
                }
                return searchAttachById(
                        items, attachId
                ).orElseGet(() -> null);
            } else {
                String taskId = KernelManager.getTask().findByNumber(number);
                TaskRelatedInfo tci = TaskRelatedManager.getInstance().find(taskId);
                if (tci != null && checker.accessTaskAttach(taskId)) {
                    List<AttachmentCacheItem> list = KernelManager.getAttachment().getAttachmentList(tci.getId(), null, null);
                    if (list != null) {
                        return searchAttachById(list, attachId).orElseGet(() -> null);
                    }
                }
            }
        } catch (GranException ge) {
            log.error("Error", ge);
        }
        return null;
    }

    private Optional<AttachmentCacheItem> searchAttachById(List<? extends AttachmentCacheItem> atts, String attachId) {
        Optional<AttachmentCacheItem> rsl = Optional.empty();
        for (AttachmentCacheItem attach : atts) {
            if (attach.getFile() != null && attach.getFile().exists() && attach.getId().equals(attachId)) {
                rsl = Optional.of(attach);
                break;
            }
        }
        return rsl;
    }

    private AttachmentCacheItem getAttachmentUser(CheckerDirectUrl checker, String login, String attachId) {
        try {
            String userId = KernelManager.getUser().findByLogin(login);
            UserRelatedInfo uri = UserRelatedManager.getInstance().find(userId);
            if (uri != null && checker.accessUserAttach(userId)) {
                List<AttachmentCacheItem> list = KernelManager.getAttachment().getAttachmentList(null, null, userId);
                if (list != null) {
                    return searchAttachById(list, attachId).orElseGet(() -> null);
                }
            }
        } catch (GranException ge) {
            log.error("Error",ge);
        }
        return null;
    }

    private void download(OutputStream os, File file) throws IOException {
        try {
            FileInputStream fis = new FileInputStream(file);
            byte[] buffer = new byte[16384];
            while (true) {
                int result = fis.read(buffer);
                if (result == -1) {
                    break;
                }
                if (result > 0) {
                    os.write(buffer, 0, result);
                }
            }
        } catch (IOException ex) {
            log.debug(ex.getMessage());
        } finally {
            os.flush();
            os.close();
        }
    }

    /**
     * This method pushes the inner file from zip archive
     * @param os stream
     * @param file zip file
     * @param zippedName inner file
     * @throws IOException for unpredictable situation
     */
    private void downloadZip(OutputStream os, File file, String zippedName) throws IOException {
        final int BUFFER = 10;
        ZipFile zf = new ZipFile(file, Charset.defaultCharset());
        try {
            int i = 0;
            for (Enumeration entries = zf.entries(); entries.hasMoreElements(); i++) {
                ZipEntry zip = (ZipEntry) entries.nextElement();
                if (zip.isDirectory()) continue;
                if (zip.getName().replaceAll(" ", "_").endsWith(zippedName)) {
                    int count;
                    byte data[] = new byte[BUFFER];
                    InputStream is = zf.getInputStream(zip);
                    while ((count = is.read(data, 0, BUFFER)) != -1) {
                        os.write(data, 0, count);
                    }
                    break;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            log.debug("Error", e);
        } finally {
            zf.close();
            os.flush();
            os.close();
        }
    }

    public static void buildHeaderForBrowser(HttpServletRequest request, HttpServletResponse response, String name) throws Exception {
        System.out.println(Config.getEncoding());
        name = URLEncoder.encode(name, Config.getEncoding())
                .replaceAll("\\+", " ")
                .replaceAll("%28", "(")
                .replaceAll("%29", ")")
                .replaceAll("%21", "!")
                .replaceAll("%5B", "[")
                .replaceAll("%5D", "]")
                .replaceAll("%3B", " ")
                .replaceAll("%27", "'")
                .replaceAll("%2C", ",")
                .replaceAll("%3D", "=")
                .replaceAll("%40", "@")
                .replaceAll("%23", "#")
                .replaceAll("%24", "\\$")
                .replaceAll("%25", "%")
                .replaceAll("%5E", "^")
                .replaceAll("%26", "&")
                .replaceAll("%2B", "+")
                .replaceAll("%7B", "{")
                .replaceAll("%7D", "}")
                .replaceAll("%7E", "~")
                .replaceAll("%60", "`");
        response.setCharacterEncoding(Config.getEncoding());
        response.setContentType("name=" + name);
        String type = request.getParameter("type");
        if (Null.isNotNull(type)) {
            response.setContentType(type);
            response.setHeader("Content-Disposition","attachment; filename=\""+name+"\"");
        } else {
            response.setHeader("Content-disposition", "attachment; filename*=utf-8" + "''" + name + ";");
            if (request.getHeader("user-agent") != null) {
                if (request.getHeader("user-agent").indexOf("MSIE") != -1 || request.getHeader("user-agent").indexOf("Chrome") != -1) {
                    response.setHeader("Content-disposition", "attachment; filename=" + name);
                } else if (request.getHeader("user-agent").indexOf("Safari") != -1) {
                    response.setHeader("Content-disposition", "attachment; filename="+name+";");
                    response.setContentType("application/octet-stream");
                    response.setHeader("Content-Transfer-Encoding", "binary");
                } else if (request.getHeader("user-agent").indexOf("Mozilla") != -1) {
                    response.setHeader("Content-disposition", "attachment; filename=\"" + name + "\"");
                }
            }
        }
    }
}