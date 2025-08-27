package com.trackstudio.action;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.URLDecoder;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.trackstudio.exception.GranException;
import com.trackstudio.startup.Config;

public class ImageCacheServlet extends HttpServlet {

    private static Log logger = LogFactory.getLog(ImageCacheServlet.class);

    public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
        try {
            String requestURL = URLDecoder.decode(request.getRequestURL().toString(), Config.getEncoding());
            int i = requestURL.lastIndexOf("/ImageServlet/");
            if (i > -1) {
                requestURL = requestURL.substring(i + 14);
                i = requestURL.indexOf("/");
                if (i > -1) {
                    String path = requestURL.substring(i + 1);
                    response.setContentType(ConcatenateServlet.getContentType(path));
                    response.setHeader("Cache-Control", "public");
                    response.setHeader("Pragma", "public");
                    response.setDateHeader("Expires", ConcatenateServlet.calendar.getTimeInMillis());
                    process(request, response, path);
                }
            }
        } catch (IOException ignore) {
        } catch (GranException ex) {
            logger.error("Image cache servlet error", ex);
            request.setAttribute("javax.servlet.jsp.jspException", ex);
            RequestDispatcher requestDispatcher = request.getRequestDispatcher("/jsp/Error.jsp");
            requestDispatcher.forward(request, response);
        }
    }

    private File parsePath(String etc, String webapp, String path) {
        File file1 = new File(etc + "/" + path);
        File file2 = new File(webapp +  path);
        //logger.debug("Loading path " + etc + "/" + path  + " exists - " + file1.exists());
        //logger.debug("Loading path " + webapp + "/" + path + " exists - " + file2.exists());
        File file = null;
        if (file1.exists() && file1.isFile()) {
            file = file1;
        } else if (file2.exists() && file2.isFile()) {
            file = file2;
        }
        return file;
    }

    private void process(HttpServletRequest request, HttpServletResponse response, String path) throws IOException, GranException {
        int k = path.indexOf("?");
        if (k != -1) {
            path = path.substring(0, k);
        }
        k = path.indexOf(";jsessionid");
        if (k == -1) {
            k = path.indexOf(";JSESSIONID");
        }
        if (k != -1) {
            path = path.substring(0, k);
        }
        ServletContext context = getServletContext();
        String realPath1 = context.getRealPath("/");
        if (realPath1.endsWith("/") || realPath1.endsWith("\\"))
            realPath1 = realPath1.substring(0, realPath1.length() - 1);
        String realPath2 = Config.getInstance().getPluginsDir();
        OutputStream out = response.getOutputStream();
        BufferedInputStream in = null;
        try{
            File file = parsePath(realPath1, realPath2, path);
            if (file != null) {
	            response.setContentType("image");
                String etag = String.valueOf(file.lastModified());
                response.setHeader("Etag", etag);
                if (etag.equals(request.getHeader("If-None-Match"))) {
                    response.setStatus(304);
                    return;
                }
                in = new BufferedInputStream(new FileInputStream(file));
                response.setContentLength((int) file.length());
                byte[] b = new byte[1024];
                while (in.read(b) != -1) {
                    out.write(b);
                }
            }

        } finally {
            try {
                if (in != null) {
                    in.close();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public void doPost(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse) throws IOException, ServletException {
        doGet(httpServletRequest, httpServletResponse);
    }
}
