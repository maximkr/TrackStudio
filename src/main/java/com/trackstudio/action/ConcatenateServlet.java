package com.trackstudio.action;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.util.Calendar;
import java.util.Locale;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.trackstudio.exception.GranException;
import com.trackstudio.tools.tag.StoreCssJs;

public class ConcatenateServlet extends HttpServlet {
    private static Log log = LogFactory.getLog(ConcatenateServlet.class);

    public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
        try {
            response.setHeader("Cache-Control", "public");
            response.setHeader("Pragma", "public");
            response.setDateHeader("Expires", calendar.getTimeInMillis());
            StringBuffer sb = request.getRequestURL();
            if (sb.lastIndexOf("/CSSServlet/cssimages/") != -1) {
                process(response, sb.substring(sb.lastIndexOf("/CSSServlet/") + "/CSSServlet/".length()));
            } else if (sb.lastIndexOf("/CSSServlet/") != -1) {
                String cssId = sb.substring(sb.lastIndexOf("/CSSServlet/") + "/CSSServlet/".length());
                process(request, response, cssId, "text/css");
            } else if (sb.lastIndexOf("/JSServlet/") != -1) {
                String jsId = sb.substring(sb.lastIndexOf("/JSServlet/") + "/JSServlet/".length());
                process(request, response, jsId, "text/javascript");
            }
        } catch (IOException ignore) {
        } catch (GranException ex) {
            log.error("Error",ex);
            request.setAttribute("javax.servlet.jsp.jspException", ex);
            RequestDispatcher requestDispatcher = request.getRequestDispatcher("/jsp/Error.jsp");
            requestDispatcher.forward(request, response);
        }
    }

    public static final Calendar calendar = Calendar.getInstance();
    static {
        calendar.set(Calendar.YEAR, calendar.get(Calendar.YEAR) + 10);
    }

    private void process(HttpServletRequest request, HttpServletResponse response, String id, String contentType) throws IOException, GranException {
        response.setContentType(contentType);
        try {
            if (id.equals(request.getHeader("If-None-Match"))) {
                response.setStatus(304);
            } else {
                String out = StoreCssJs.getInstance().getData(id);
                if (out!=null) {
                    response.setContentLength(out.length());
                    PrintWriter writer = response.getWriter();
                    writer.println(out);
                }
            }
        } catch (IOException ignore) {
        } catch (Exception e) {
            log.error("Error", e);
        }
    }

    private void process(HttpServletResponse response, String path) throws IOException, GranException {
        int k = path.indexOf("?");
        if (k != -1) {
            path = path.substring(0, k);
        }
        ServletContext context = getServletContext();
        String realPath = context.getRealPath("/");
        if (realPath.endsWith("/") || realPath.endsWith("\\"))
            realPath = realPath.substring(0, realPath.length() - 1);
        response.setContentType(getContentType(path));
        OutputStream out = null;
        BufferedInputStream in = null;
        try {
            out = response.getOutputStream();
            in = new BufferedInputStream(new FileInputStream(new File(realPath + "/" + path)));
            byte[] b = new byte[1024];
            while (in.read(b) != -1) {
                out.write(b);
            }
        } catch (IOException ignore) {
        } finally {
            try {
                if (in != null) {
                    in.close();
                }
                if (out != null ) {
                    out.flush();
                    out.close();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }


    public static String getContentType(String path) {
        path = path.toLowerCase(Locale.ENGLISH).trim();
        if (path.endsWith(".png"))
            return "image/png";
        else if (path.endsWith(".gif"))
            return "image/gif";
        else if (path.endsWith(".jpg") || path.endsWith(".jpeg"))
            return "image/jpg";
        return "image";
    }

    public void doPost(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse) throws IOException, ServletException {
        doGet(httpServletRequest, httpServletResponse);
    }
}
