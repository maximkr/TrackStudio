package com.trackstudio.action;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
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
        if (isModernizedLegacyIcon(path)) {
            writeModernIcon(response, path);
            return;
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

    private boolean isModernizedLegacyIcon(String path) {
        String normalized = path.toLowerCase(Locale.ROOT);
        // Never touch TinyMCE, lightbox, logo, favicon
        if (normalized.contains("/tiny_mce/") || normalized.contains("/lightbox/")
                || normalized.contains("trackstudio-logo") || normalized.contains("favicon")) {
            return false;
        }
        // State icons have colored backgrounds — must stay as real files
        if (normalized.contains("state.png") || normalized.contains("state.gif")) {
            return false;
        }
        // Category/type icons are real uploaded files from plugins dir
        if (normalized.contains("/icons/categories/")) {
            return false;
        }
        // Structural/layout/background images — never replace
        if (normalized.endsWith("blank.png") || normalized.endsWith("blank.gif")
                || normalized.endsWith("empty.gif") || normalized.endsWith("dot.gif")
                || normalized.endsWith("shadow.gif") || normalized.endsWith("blc.shadow.gif")
                || normalized.endsWith("sprite.png") || normalized.endsWith("px")
                || normalized.endsWith("pspbrwse.jbf")
                || normalized.endsWith("tabborder.gif") || normalized.endsWith("tableft.gif")
                || normalized.endsWith("tabright.gif") || normalized.endsWith("qtab.gif")
                || normalized.endsWith("redcorner.gif") || normalized.endsWith("yellowcorner.gif")
                || normalized.endsWith("no_image.jpg")
                || normalized.endsWith("blue.png") || normalized.endsWith("eof.gif")
                || normalized.contains("trackstudio-rnd")) {
            return false;
        }
        // Tree connector lines (I.png, L.png, T.png, Lminus.png, Lplus.png, etc.)
        if (normalized.matches(".*[/\\\\][ilt](minus|plus)?\\.png$")) {
            return false;
        }
        // Replace all icons in cssimages/ and xtree images/ (structural files are excluded above)
        if (normalized.contains("cssimages/") || normalized.contains("html/xtree/images/")) {
            // Only PNG/GIF files are icons
            return normalized.endsWith(".png") || normalized.endsWith(".gif");
        }
        return false;
    }

    private void writeModernIcon(HttpServletResponse response, String path) throws IOException {
        response.setContentType("image/svg+xml");
        String svg = buildIconSvg(path);
        byte[] bytes = svg.getBytes(StandardCharsets.UTF_8);
        response.setContentLength(bytes.length);
        OutputStream out = response.getOutputStream();
        out.write(bytes);
    }

    private String buildIconSvg(String path) {
        String name = path;
        int slash = name.lastIndexOf('/');
        if (slash >= 0 && slash + 1 < name.length()) {
            name = name.substring(slash + 1);
        }
        int dot = name.lastIndexOf('.');
        if (dot > 0) {
            name = name.substring(0, dot);
        }
        String token = name.toLowerCase(Locale.ROOT);
        if (token.startsWith("ico.")) {
            token = token.substring(4);
        }
        if (token.startsWith("arw.")) {
            token = token.substring(4);
        }
        String glyph = glyphForToken(token);
        return "<svg xmlns='http://www.w3.org/2000/svg' width='16' height='16' viewBox='0 0 16 16'>"
                + "<rect x='0.75' y='0.75' width='14.5' height='14.5' rx='3' fill='#2f7396' stroke='#225975'/>"
                + glyph.replace("{fg}", "#ffffff")
                + "</svg>";
    }

    private String glyphForToken(String token) {
        // --- Compound tokens (must be checked before simple parts) ---

        // addtask — task doc with plus badge
        if (containsAny(token, "addtask")) {
            return "<rect x='4' y='3.8' width='8' height='8.8' rx='1.1' fill='none' stroke='{fg}' stroke-width='1.4'/>"
                    + "<line x1='8' y1='6' x2='8' y2='10.5' stroke='{fg}' stroke-width='1.4' stroke-linecap='round'/>"
                    + "<line x1='5.8' y1='8.2' x2='10.2' y2='8.2' stroke='{fg}' stroke-width='1.4' stroke-linecap='round'/>";
        }
        // adduser — person with plus badge
        if (containsAny(token, "adduser")) {
            return "<circle cx='6.5' cy='5' r='2' fill='{fg}'/>"
                    + "<path d='M2.5 11.5c.6-1.8 2-2.7 4-2.7s3.4.9 4 2.7' fill='none' stroke='{fg}' stroke-width='1.4' stroke-linecap='round'/>"
                    + "<line x1='12' y1='5' x2='12' y2='9' stroke='{fg}' stroke-width='1.4' stroke-linecap='round'/>"
                    + "<line x1='10' y1='7' x2='14' y2='7' stroke='{fg}' stroke-width='1.4' stroke-linecap='round'/>";
        }
        // new_msg — envelope with plus
        if (containsAny(token, "new_msg", "newmsg")) {
            return "<rect x='3.3' y='5' width='8' height='6.5' rx='1' fill='none' stroke='{fg}' stroke-width='1.3'/>"
                    + "<polyline points='3.8,5.6 7.3,8 10.8,5.6' fill='none' stroke='{fg}' stroke-width='1.2'/>"
                    + "<line x1='12.5' y1='4' x2='12.5' y2='8' stroke='{fg}' stroke-width='1.4' stroke-linecap='round'/>"
                    + "<line x1='10.5' y1='6' x2='14.5' y2='6' stroke='{fg}' stroke-width='1.4' stroke-linecap='round'/>";
        }
        // emailimport — envelope with down arrow
        if (containsAny(token, "emailimport")) {
            return "<rect x='3.3' y='4.5' width='9.4' height='7' rx='1.1' fill='none' stroke='{fg}' stroke-width='1.3'/>"
                    + "<polyline points='3.8,5.2 8,8 12.2,5.2' fill='none' stroke='{fg}' stroke-width='1.2'/>"
                    + "<line x1='8' y1='2' x2='8' y2='5.5' stroke='{fg}' stroke-width='1.2' stroke-linecap='round'/>"
                    + "<polyline points='6.5,4 8,5.5 9.5,4' fill='none' stroke='{fg}' stroke-width='1.1' stroke-linecap='round' stroke-linejoin='round'/>";
        }
        // delete.bookmark
        if (containsAny(token, "delete.bookmark")) {
            return "<path d='M5 3v9.5l3-2 3 2V3z' fill='none' stroke='{fg}' stroke-width='1.4'/>"
                    + "<line x1='6.5' y1='6' x2='9.5' y2='6' stroke='{fg}' stroke-width='1.3' stroke-linecap='round'/>";
        }
        // selecttask — task with pointer
        if (containsAny(token, "selecttask")) {
            return "<rect x='3.5' y='3.5' width='7' height='8' rx='1' fill='none' stroke='{fg}' stroke-width='1.3'/>"
                    + "<line x1='5' y1='5.8' x2='9' y2='5.8' stroke='{fg}' stroke-width='1'/>"
                    + "<line x1='5' y1='8' x2='9' y2='8' stroke='{fg}' stroke-width='1'/>"
                    + "<polyline points='10,9 12.5,11.5 13,7' fill='none' stroke='{fg}' stroke-width='1.4' stroke-linecap='round' stroke-linejoin='round'/>";
        }
        // selectuser — person with pointer
        if (containsAny(token, "selectuser")) {
            return "<circle cx='6.5' cy='5' r='2' fill='{fg}'/>"
                    + "<path d='M2.5 11.5c.6-1.8 2-2.7 4-2.7s3.4.9 4 2.7' fill='none' stroke='{fg}' stroke-width='1.4' stroke-linecap='round'/>"
                    + "<polyline points='10,9 12.5,11.5 13,7' fill='none' stroke='{fg}' stroke-width='1.4' stroke-linecap='round' stroke-linejoin='round'/>";
        }
        // userfilters — person with funnel
        if (containsAny(token, "userfilter")) {
            return "<circle cx='6' cy='4.8' r='1.8' fill='{fg}'/>"
                    + "<path d='M2.5 11c.5-1.5 1.8-2.4 3.5-2.4s3 .9 3.5 2.4' fill='none' stroke='{fg}' stroke-width='1.3' stroke-linecap='round'/>"
                    + "<path d='M10.5 5l2.5 3h-5l2.5-3z' fill='none' stroke='{fg}' stroke-width='1.1'/>"
                    + "<line x1='12.5' y1='8' x2='12.5' y2='11' stroke='{fg}' stroke-width='1.1'/>";
        }
        // userworkload — person with chart
        if (containsAny(token, "userworkload")) {
            return "<circle cx='6' cy='4.8' r='1.8' fill='{fg}'/>"
                    + "<path d='M2.5 11c.5-1.5 1.8-2.4 3.5-2.4s3 .9 3.5 2.4' fill='none' stroke='{fg}' stroke-width='1.3' stroke-linecap='round'/>"
                    + "<line x1='11' y1='6' x2='11' y2='11' stroke='{fg}' stroke-width='1.3' stroke-linecap='round'/>"
                    + "<line x1='13' y1='8' x2='13' y2='11' stroke='{fg}' stroke-width='1.3' stroke-linecap='round'/>";
        }
        // userinfo — person with (i)
        if (containsAny(token, "userinfo")) {
            return "<circle cx='6' cy='4.8' r='1.8' fill='{fg}'/>"
                    + "<path d='M2.5 11c.5-1.5 1.8-2.4 3.5-2.4s3 .9 3.5 2.4' fill='none' stroke='{fg}' stroke-width='1.3' stroke-linecap='round'/>"
                    + "<circle cx='12.5' cy='6' r='3' fill='none' stroke='{fg}' stroke-width='1'/>"
                    + "<text x='12.5' y='8.2' text-anchor='middle' fill='{fg}' font-size='5' font-weight='700' font-family='sans-serif'>i</text>";
        }
        // userlist — two persons
        if (containsAny(token, "userlist")) {
            return "<circle cx='5.5' cy='4.5' r='1.8' fill='{fg}'/>"
                    + "<path d='M2 11c.5-1.5 1.7-2.4 3.5-2.4s3 .9 3.5 2.4' fill='none' stroke='{fg}' stroke-width='1.3' stroke-linecap='round'/>"
                    + "<circle cx='10.5' cy='5' r='1.5' fill='{fg}'/>"
                    + "<path d='M8 11c.4-1.2 1.3-1.9 2.5-1.9s2.1.7 2.5 1.9' fill='none' stroke='{fg}' stroke-width='1.1' stroke-linecap='round'/>";
        }
        // tasklist — stacked docs
        if (containsAny(token, "tasklist")) {
            return "<rect x='4.5' y='3' width='7.5' height='8.5' rx='1' fill='none' stroke='{fg}' stroke-width='1.3'/>"
                    + "<line x1='6' y1='5.5' x2='10.5' y2='5.5' stroke='{fg}' stroke-width='1'/>"
                    + "<line x1='6' y1='7.5' x2='10.5' y2='7.5' stroke='{fg}' stroke-width='1'/>"
                    + "<line x1='6' y1='9.5' x2='9' y2='9.5' stroke='{fg}' stroke-width='1'/>"
                    + "<path d='M3.5 5v7.5c0 .5.5 1 1 1h7' fill='none' stroke='{fg}' stroke-width='1.1'/>";
        }
        // subtask — nested doc
        if (containsAny(token, "subtask")) {
            return "<rect x='3.5' y='3' width='7' height='8' rx='1' fill='none' stroke='{fg}' stroke-width='1.3'/>"
                    + "<line x1='5' y1='5.3' x2='9' y2='5.3' stroke='{fg}' stroke-width='1'/>"
                    + "<line x1='5' y1='7.3' x2='9' y2='7.3' stroke='{fg}' stroke-width='1'/>"
                    + "<rect x='7.5' y='7' width='5' height='4.5' rx='.8' fill='#2f7396' stroke='{fg}' stroke-width='1.1'/>";
        }
        // treereport — hierarchical report
        if (containsAny(token, "treereport")) {
            return "<rect x='3.5' y='3' width='9' height='10' rx='1' fill='none' stroke='{fg}' stroke-width='1.3'/>"
                    + "<line x1='5.5' y1='6' x2='6.5' y2='6' stroke='{fg}' stroke-width='1.2'/>"
                    + "<line x1='7' y1='8' x2='8.5' y2='8' stroke='{fg}' stroke-width='1.2'/>"
                    + "<line x1='7' y1='10' x2='8.5' y2='10' stroke='{fg}' stroke-width='1.2'/>"
                    + "<polyline points='5.5,6.5 5.5,8 6.5,8' fill='none' stroke='{fg}' stroke-width='1'/>";
        }
        // detreport — detail report (doc with magnifier)
        if (containsAny(token, "detreport")) {
            return "<rect x='3.5' y='3' width='9' height='10' rx='1' fill='none' stroke='{fg}' stroke-width='1.3'/>"
                    + "<line x1='5.5' y1='6' x2='10.5' y2='6' stroke='{fg}' stroke-width='1.2'/>"
                    + "<circle cx='9' cy='9.5' r='2' fill='none' stroke='{fg}' stroke-width='1.1'/>"
                    + "<line x1='10.4' y1='11' x2='11.5' y2='12' stroke='{fg}' stroke-width='1.1' stroke-linecap='round'/>";
        }
        // distreport — distribution report (bar chart)
        if (containsAny(token, "distreport")) {
            return "<rect x='3.5' y='3' width='9' height='10' rx='1' fill='none' stroke='{fg}' stroke-width='1.3'/>"
                    + "<line x1='5.5' y1='6' x2='5.5' y2='10.5' stroke='{fg}' stroke-width='1.8' stroke-linecap='round'/>"
                    + "<line x1='8' y1='8' x2='8' y2='10.5' stroke='{fg}' stroke-width='1.8' stroke-linecap='round'/>"
                    + "<line x1='10.5' y1='5' x2='10.5' y2='10.5' stroke='{fg}' stroke-width='1.8' stroke-linecap='round'/>";
        }
        // listreport — list report (doc with lines)
        if (containsAny(token, "listreport")) {
            return "<rect x='3.5' y='3' width='9' height='10' rx='1' fill='none' stroke='{fg}' stroke-width='1.3'/>"
                    + "<line x1='5.5' y1='6' x2='7' y2='6' stroke='{fg}' stroke-width='1.2'/>"
                    + "<line x1='5.5' y1='8' x2='10' y2='8' stroke='{fg}' stroke-width='1.2'/>"
                    + "<line x1='5.5' y1='10' x2='9' y2='10' stroke='{fg}' stroke-width='1.2'/>";
        }
        // filterproperties — funnel with gear
        if (containsAny(token, "filterproperties")) {
            return "<path d='M4 4h8l-2.5 3.5v4L8.5 13V7.5z' fill='none' stroke='{fg}' stroke-width='1.4'/>"
                    + "<circle cx='12' cy='10' r='1.5' fill='none' stroke='{fg}' stroke-width='1'/>";
        }
        // svnadd — version control +
        if (containsAny(token, "svnadd")) {
            return "<path d='M4 4.5c2-1.5 6-1.5 8 0' fill='none' stroke='{fg}' stroke-width='1.2'/>"
                    + "<path d='M4 8c2-1.5 6-1.5 8 0' fill='none' stroke='{fg}' stroke-width='1.2'/>"
                    + "<path d='M4 11.5c2-1.5 6-1.5 8 0' fill='none' stroke='{fg}' stroke-width='1.2'/>"
                    + "<line x1='8' y1='6' x2='8' y2='10' stroke='{fg}' stroke-width='1' stroke-linecap='round'/>"
                    + "<line x1='6' y1='8' x2='10' y2='8' stroke='{fg}' stroke-width='1' stroke-linecap='round'/>";
        }
        // svndel — version control -
        if (containsAny(token, "svndel")) {
            return "<path d='M4 4.5c2-1.5 6-1.5 8 0' fill='none' stroke='{fg}' stroke-width='1.2'/>"
                    + "<path d='M4 8c2-1.5 6-1.5 8 0' fill='none' stroke='{fg}' stroke-width='1.2'/>"
                    + "<path d='M4 11.5c2-1.5 6-1.5 8 0' fill='none' stroke='{fg}' stroke-width='1.2'/>"
                    + "<line x1='6' y1='8' x2='10' y2='8' stroke='{fg}' stroke-width='1.2' stroke-linecap='round'/>";
        }
        // svnmod — version control ~
        if (containsAny(token, "svnmod")) {
            return "<path d='M4 4.5c2-1.5 6-1.5 8 0' fill='none' stroke='{fg}' stroke-width='1.2'/>"
                    + "<path d='M4 8c2-1.5 6-1.5 8 0' fill='none' stroke='{fg}' stroke-width='1.2'/>"
                    + "<path d='M4 11.5c2-1.5 6-1.5 8 0' fill='none' stroke='{fg}' stroke-width='1.2'/>"
                    + "<path d='M6 7c.7-.6 1.3-.6 2 0s1.3.6 2 0' fill='none' stroke='{fg}' stroke-width='1' stroke-linecap='round'/>";
        }
        // unchecked — empty square
        if (containsAny(token, "unchecked")) {
            return "<rect x='4.5' y='4.5' width='7' height='7' rx='1.2' fill='none' stroke='{fg}' stroke-width='1.6'/>";
        }
        // closewin — window with X
        if (containsAny(token, "closewin")) {
            return "<rect x='3.5' y='4' width='9' height='8' rx='1' fill='none' stroke='{fg}' stroke-width='1.3'/>"
                    + "<line x1='3.5' y1='6.5' x2='12.5' y2='6.5' stroke='{fg}' stroke-width='1.2'/>"
                    + "<line x1='6.5' y1='8.2' x2='9.5' y2='11' stroke='{fg}' stroke-width='1.1' stroke-linecap='round'/>"
                    + "<line x1='9.5' y1='8.2' x2='6.5' y2='11' stroke='{fg}' stroke-width='1.1' stroke-linecap='round'/>";
        }
        // openwin — window with arrow
        if (containsAny(token, "openwin")) {
            return "<rect x='3.5' y='4' width='9' height='8' rx='1' fill='none' stroke='{fg}' stroke-width='1.3'/>"
                    + "<line x1='3.5' y1='6.5' x2='12.5' y2='6.5' stroke='{fg}' stroke-width='1.2'/>"
                    + "<polyline points='6.5,10 8,8 9.5,10' fill='none' stroke='{fg}' stroke-width='1.2' stroke-linecap='round' stroke-linejoin='round'/>";
        }
        // hidewin — window minimized
        if (containsAny(token, "hidewin")) {
            return "<rect x='3.5' y='4' width='9' height='8' rx='1' fill='none' stroke='{fg}' stroke-width='1.3'/>"
                    + "<line x1='3.5' y1='6.5' x2='12.5' y2='6.5' stroke='{fg}' stroke-width='1.2'/>"
                    + "<line x1='6' y1='10' x2='10' y2='10' stroke='{fg}' stroke-width='1.4' stroke-linecap='round'/>";
        }
        // overview — grid/dashboard
        if (containsAny(token, "overview")) {
            return "<rect x='3.5' y='3.5' width='4' height='4' rx='.8' fill='none' stroke='{fg}' stroke-width='1.3'/>"
                    + "<rect x='8.5' y='3.5' width='4' height='4' rx='.8' fill='none' stroke='{fg}' stroke-width='1.3'/>"
                    + "<rect x='3.5' y='8.5' width='4' height='4' rx='.8' fill='none' stroke='{fg}' stroke-width='1.3'/>"
                    + "<rect x='8.5' y='8.5' width='4' height='4' rx='.8' fill='none' stroke='{fg}' stroke-width='1.3'/>";
        }
        // timesheet — clock with lines
        if (containsAny(token, "timesheet")) {
            return "<circle cx='7' cy='8' r='4.2' fill='none' stroke='{fg}' stroke-width='1.4'/>"
                    + "<polyline points='7,5.5 7,8 9.5,9.5' fill='none' stroke='{fg}' stroke-width='1.3' stroke-linecap='round' stroke-linejoin='round'/>"
                    + "<line x1='12' y1='5' x2='13.5' y2='5' stroke='{fg}' stroke-width='1' stroke-linecap='round'/>"
                    + "<line x1='12' y1='7.5' x2='14' y2='7.5' stroke='{fg}' stroke-width='1' stroke-linecap='round'/>"
                    + "<line x1='12' y1='10' x2='13.5' y2='10' stroke='{fg}' stroke-width='1' stroke-linecap='round'/>";
        }
        // messagetypes — stacked envelopes
        if (containsAny(token, "messagetype")) {
            return "<rect x='3.3' y='5.5' width='9.4' height='6' rx='1' fill='none' stroke='{fg}' stroke-width='1.3'/>"
                    + "<polyline points='3.8,6.2 8,8.5 12.2,6.2' fill='none' stroke='{fg}' stroke-width='1.2'/>"
                    + "<path d='M4.5 5.5V4.5c0-.5.4-1 1-1h5c.5 0 1 .5 1 1v1' fill='none' stroke='{fg}' stroke-width='1.1'/>";
        }
        // categories — grid of items (category management)
        if (containsAny(token, "categor")) {
            return "<rect x='3.5' y='3.5' width='3.5' height='3.5' rx='.6' fill='{fg}'/>"
                    + "<rect x='9' y='3.5' width='3.5' height='3.5' rx='.6' fill='none' stroke='{fg}' stroke-width='1.2'/>"
                    + "<rect x='3.5' y='9' width='3.5' height='3.5' rx='.6' fill='none' stroke='{fg}' stroke-width='1.2'/>"
                    + "<rect x='9' y='9' width='3.5' height='3.5' rx='.6' fill='none' stroke='{fg}' stroke-width='1.2'/>";
        }

        // code_swarm — download/deploy
        if (containsAny(token, "code_swarm")) {
            return "<polyline points='8,3 8,10' fill='none' stroke='{fg}' stroke-width='1.5' stroke-linecap='round'/>"
                    + "<polyline points='5,8 8,11 11,8' fill='none' stroke='{fg}' stroke-width='1.5' stroke-linecap='round' stroke-linejoin='round'/>"
                    + "<line x1='4' y1='13' x2='12' y2='13' stroke='{fg}' stroke-width='1.3' stroke-linecap='round'/>";
        }
        // sman — package/cube
        if (containsAny(token, "sman")) {
            return "<path d='M8 3L3 5.5v5L8 13l5-2.5v-5z' fill='none' stroke='{fg}' stroke-width='1.3' stroke-linejoin='round'/>"
                    + "<line x1='8' y1='8' x2='8' y2='13' stroke='{fg}' stroke-width='1.1'/>"
                    + "<polyline points='3,5.5 8,8 13,5.5' fill='none' stroke='{fg}' stroke-width='1.1'/>";
        }

        // --- Simple tokens ---

        // Search / filter
        if (containsAny(token, "search", "filter", "find", "magnifier", "locate")) {
            return "<circle cx='7' cy='7' r='3.2' fill='none' stroke='{fg}' stroke-width='1.6'/>"
                    + "<line x1='9.7' y1='9.7' x2='13' y2='13' stroke='{fg}' stroke-width='1.8' stroke-linecap='round'/>";
        }
        // Add / create
        if (containsAny(token, "add", "create", "plus", "registration")) {
            return "<line x1='8' y1='4' x2='8' y2='12' stroke='{fg}' stroke-width='1.8' stroke-linecap='round'/>"
                    + "<line x1='4' y1='8' x2='12' y2='8' stroke='{fg}' stroke-width='1.8' stroke-linecap='round'/>";
        }
        // Delete / remove / close
        if (containsAny(token, "delete", "remove", "cut", "close", "deactivate")) {
            return "<line x1='5' y1='5' x2='11' y2='11' stroke='{fg}' stroke-width='1.8' stroke-linecap='round'/>"
                    + "<line x1='11' y1='5' x2='5' y2='11' stroke='{fg}' stroke-width='1.8' stroke-linecap='round'/>";
        }
        // Edit / pencil
        if (containsAny(token, "edit", "rename")) {
            return "<path d='M4 12l-.6-2.5L10 3l2.5 2.5L6 12z' fill='none' stroke='{fg}' stroke-width='1.3'/>"
                    + "<line x1='8.8' y1='4.2' x2='11.3' y2='6.7' stroke='{fg}' stroke-width='1.2'/>";
        }
        // Copy / clipboard
        if (containsAny(token, "copy", "clipboard", "paste")) {
            return "<rect x='5.5' y='5' width='7' height='8' rx='1' fill='none' stroke='{fg}' stroke-width='1.3'/>"
                    + "<path d='M3.5 10V4c0-.5.5-1 1-1h5' fill='none' stroke='{fg}' stroke-width='1.2'/>";
        }
        // Direction: expand / right
        if (containsAny(token, "expand", "openpanel")) {
            return "<polyline points='6,4 10,8 6,12' fill='none' stroke='{fg}' stroke-width='1.8' stroke-linecap='round' stroke-linejoin='round'/>";
        }
        // Direction: collapse / left
        if (containsAny(token, "collapse", "closepanel")) {
            return "<polyline points='10,4 6,8 10,12' fill='none' stroke='{fg}' stroke-width='1.8' stroke-linecap='round' stroke-linejoin='round'/>";
        }
        // Direction: up
        if (containsAny(token, "up")) {
            return "<polyline points='4,10 8,5 12,10' fill='none' stroke='{fg}' stroke-width='1.8' stroke-linecap='round' stroke-linejoin='round'/>";
        }
        // Direction: down
        if (containsAny(token, "down")) {
            return "<polyline points='4,6 8,11 12,6' fill='none' stroke='{fg}' stroke-width='1.8' stroke-linecap='round' stroke-linejoin='round'/>";
        }
        // Play
        if (containsAny(token, "play")) {
            return "<polygon points='5,3.5 13,8 5,12.5' fill='{fg}'/>";
        }
        // Pause
        if (containsAny(token, "pause")) {
            return "<rect x='4' y='4' width='3' height='8' rx='.8' fill='{fg}'/>"
                    + "<rect x='9' y='4' width='3' height='8' rx='.8' fill='{fg}'/>";
        }
        // Stop
        if (containsAny(token, "stop")) {
            return "<rect x='4' y='4' width='8' height='8' rx='1' fill='{fg}'/>";
        }
        // Checked / yes / ok / activate
        if (containsAny(token, "checked", "activate", "yes")) {
            return "<polyline points='4.2,8.2 6.8,10.8 11.8,5.6' fill='none' stroke='{fg}' stroke-width='1.8' stroke-linecap='round' stroke-linejoin='round'/>";
        }
        // No / cross indicator
        if (token.equals("no")) {
            return "<circle cx='8' cy='8' r='4' fill='none' stroke='{fg}' stroke-width='1.5'/>"
                    + "<line x1='5.2' y1='5.2' x2='10.8' y2='10.8' stroke='{fg}' stroke-width='1.5'/>";
        }
        // Refresh / reload
        if (containsAny(token, "refresh", "reload")) {
            return "<path d='M12 8a4 4 0 1 1-1-2.8' fill='none' stroke='{fg}' stroke-width='1.5' stroke-linecap='round'/>"
                    + "<polyline points='11,3 12,5.5 9.5,5.5' fill='none' stroke='{fg}' stroke-width='1.3' stroke-linecap='round' stroke-linejoin='round'/>";
        }
        // Scroll / target
        if (containsAny(token, "scroll", "current", "selected")) {
            return "<circle cx='8' cy='8' r='4.5' fill='none' stroke='{fg}' stroke-width='1.3'/>"
                    + "<circle cx='8' cy='8' r='1.5' fill='{fg}'/>"
                    + "<line x1='8' y1='2' x2='8' y2='4.5' stroke='{fg}' stroke-width='1'/>"
                    + "<line x1='8' y1='11.5' x2='8' y2='14' stroke='{fg}' stroke-width='1'/>"
                    + "<line x1='2' y1='8' x2='4.5' y2='8' stroke='{fg}' stroke-width='1'/>"
                    + "<line x1='11.5' y1='8' x2='14' y2='8' stroke='{fg}' stroke-width='1'/>";
        }
        // Help / question
        if (containsAny(token, "help")) {
            return "<circle cx='8' cy='8' r='5' fill='none' stroke='{fg}' stroke-width='1.4'/>"
                    + "<text x='8' y='11' text-anchor='middle' fill='{fg}' font-size='8' font-weight='700' font-family='sans-serif'>?</text>";
        }
        // Info (i)
        if (containsAny(token, "info")) {
            return "<circle cx='8' cy='8' r='5' fill='none' stroke='{fg}' stroke-width='1.4'/>"
                    + "<line x1='8' y1='7' x2='8' y2='11.5' stroke='{fg}' stroke-width='1.5' stroke-linecap='round'/>"
                    + "<circle cx='8' cy='5' r='.9' fill='{fg}'/>";
        }
        // Home
        if (containsAny(token, "home")) {
            return "<path d='M3.5 8.5L8 4l4.5 4.5' fill='none' stroke='{fg}' stroke-width='1.5' stroke-linecap='round' stroke-linejoin='round'/>"
                    + "<path d='M5 8v4.5h6V8' fill='none' stroke='{fg}' stroke-width='1.4'/>";
        }
        // Folder / openfolder / project
        if (containsAny(token, "folder", "project")) {
            return "<path d='M3.4 5.2h3l1-1.4h5.2c.6 0 1 .4 1 1v6.4c0 .6-.4 1-1 1H3.4c-.6 0-1-.4-1-1V6.2c0-.6.4-1 1-1z' fill='none' stroke='{fg}' stroke-width='1.4'/>";
        }
        // Bug / issue
        if (containsAny(token, "bug", "issue", "error")) {
            return "<ellipse cx='8' cy='8.3' rx='2.5' ry='2.8' fill='none' stroke='{fg}' stroke-width='1.4'/>"
                    + "<line x1='8' y1='4' x2='8' y2='6' stroke='{fg}' stroke-width='1.2'/>"
                    + "<line x1='5.3' y1='7' x2='3.8' y2='6' stroke='{fg}' stroke-width='1.1'/>"
                    + "<line x1='10.7' y1='7' x2='12.2' y2='6' stroke='{fg}' stroke-width='1.1'/>"
                    + "<line x1='5.3' y1='9.6' x2='3.8' y2='10.6' stroke='{fg}' stroke-width='1.1'/>"
                    + "<line x1='10.7' y1='9.6' x2='12.2' y2='10.6' stroke='{fg}' stroke-width='1.1'/>";
        }
        // Root / admin — person with shield
        if (containsAny(token, "root", "admin", "itadmin")) {
            return "<circle cx='8' cy='4.5' r='2' fill='{fg}'/>"
                    + "<path d='M4 12c.6-2 2-3 4-3s3.4 1 4 3' fill='none' stroke='{fg}' stroke-width='1.4' stroke-linecap='round'/>"
                    + "<path d='M6.5 9l1.5-1 1.5 1v2c0 .5-.7 1.2-1.5 1.5-.8-.3-1.5-1-1.5-1.5z' fill='{fg}'/>";
        }
        // User / person
        if (containsAny(token, "user", "usr", "login", "anonymous", "handler", "assigned", "submitter", "jsmith")) {
            return "<circle cx='8' cy='5.2' r='2.3' fill='{fg}'/>"
                    + "<path d='M3.8 12.2c.7-2 2.3-3 4.2-3s3.5 1 4.2 3' fill='none' stroke='{fg}' stroke-width='1.6' stroke-linecap='round'/>";
        }
        // Workflow — connected nodes
        if (containsAny(token, "workflow")) {
            return "<circle cx='4.5' cy='5' r='2' fill='none' stroke='{fg}' stroke-width='1.3'/>"
                    + "<circle cx='11.5' cy='5' r='2' fill='none' stroke='{fg}' stroke-width='1.3'/>"
                    + "<circle cx='8' cy='11' r='2' fill='none' stroke='{fg}' stroke-width='1.3'/>"
                    + "<line x1='6.2' y1='5.5' x2='9.8' y2='5.5' stroke='{fg}' stroke-width='1'/>"
                    + "<line x1='5.5' y1='6.7' x2='7' y2='9.3' stroke='{fg}' stroke-width='1'/>"
                    + "<line x1='10.5' y1='6.7' x2='9' y2='9.3' stroke='{fg}' stroke-width='1'/>";
        }
        // Template — doc with T
        if (containsAny(token, "template")) {
            return "<rect x='4' y='3' width='8' height='10' rx='1' fill='none' stroke='{fg}' stroke-width='1.4'/>"
                    + "<line x1='6' y1='6' x2='10' y2='6' stroke='{fg}' stroke-width='1.3' stroke-linecap='round'/>"
                    + "<line x1='8' y1='6' x2='8' y2='11' stroke='{fg}' stroke-width='1.3' stroke-linecap='round'/>";
        }
        // Task / status / priority
        if (containsAny(token, "task", "status", "priority")) {
            return "<rect x='4' y='3.8' width='8' height='8.8' rx='1.1' fill='none' stroke='{fg}' stroke-width='1.6'/>"
                    + "<line x1='5.4' y1='6' x2='10.6' y2='6' stroke='{fg}' stroke-width='1.2'/>"
                    + "<line x1='5.4' y1='8.2' x2='10.6' y2='8.2' stroke='{fg}' stroke-width='1.2'/>";
        }
        // SCM / version control
        if (containsAny(token, "scm", "svn")) {
            return "<path d='M4 4.5c2-1.5 6-1.5 8 0' fill='none' stroke='{fg}' stroke-width='1.2'/>"
                    + "<path d='M4 8c2-1.5 6-1.5 8 0' fill='none' stroke='{fg}' stroke-width='1.2'/>"
                    + "<path d='M4 11.5c2-1.5 6-1.5 8 0' fill='none' stroke='{fg}' stroke-width='1.2'/>";
        }
        // Attachment / file / document / page / pdf / word / txt / excel
        if (containsAny(token, "attachment", "file", "zip", "document", "page", "pdf",
                "word", "txt", "excel", "download", "box", "sh")) {
            return "<path d='M5 2.8h4.1L12 5.7V13H5z' fill='none' stroke='{fg}' stroke-width='1.4'/>"
                    + "<polyline points='9.1,2.8 9.1,5.8 12,5.8' fill='none' stroke='{fg}' stroke-width='1.2'/>";
        }
        // Mail / email / notification / message / chat
        if (containsAny(token, "mail", "email", "notify", "notification", "subscribe",
                "chat", "msg", "message")) {
            return "<rect x='3.3' y='4.5' width='9.4' height='7' rx='1.1' fill='none' stroke='{fg}' stroke-width='1.4'/>"
                    + "<polyline points='3.8,5.2 8,8 12.2,5.2' fill='none' stroke='{fg}' stroke-width='1.3'/>";
        }
        // Calendar / date
        if (containsAny(token, "calendar", "date")) {
            return "<rect x='3.6' y='4.1' width='8.8' height='8.4' rx='1.1' fill='none' stroke='{fg}' stroke-width='1.4'/>"
                    + "<line x1='3.8' y1='6.6' x2='12.2' y2='6.6' stroke='{fg}' stroke-width='1.2'/>"
                    + "<line x1='6' y1='3.2' x2='6' y2='5.2' stroke='{fg}' stroke-width='1.2'/>"
                    + "<line x1='10' y1='3.2' x2='10' y2='5.2' stroke='{fg}' stroke-width='1.2'/>";
        }
        // Clock / time
        if (containsAny(token, "time", "clock")) {
            return "<circle cx='8' cy='8' r='5' fill='none' stroke='{fg}' stroke-width='1.4'/>"
                    + "<polyline points='8,5 8,8 10.5,9.5' fill='none' stroke='{fg}' stroke-width='1.3' stroke-linecap='round' stroke-linejoin='round'/>";
        }
        // Key / security / access
        if (containsAny(token, "key", "security", "acl", "access", "effective")) {
            return "<circle cx='6.2' cy='8' r='2.1' fill='none' stroke='{fg}' stroke-width='1.6'/>"
                    + "<line x1='8.2' y1='8' x2='12.2' y2='8' stroke='{fg}' stroke-width='1.6' stroke-linecap='round'/>"
                    + "<line x1='10.4' y1='8' x2='10.4' y2='9.7' stroke='{fg}' stroke-width='1.2'/>";
        }
        // Star / bookmark / favorite
        if (containsAny(token, "star", "bookmark", "favorite")) {
            return "<path d='M8 3.2l1.4 2.8 3.1.4-2.2 2.1.6 3.1L8 10.2 5.1 11.6l.6-3.1L3.5 6.4l3.1-.4z' fill='{fg}'/>";
        }
        // Warning / alert / exclamation
        if (containsAny(token, "warning", "alert", "exclamation")) {
            return "<path d='M8 3.1l4.6 8H3.4z' fill='none' stroke='{fg}' stroke-width='1.5'/>"
                    + "<line x1='8' y1='6.1' x2='8' y2='9' stroke='{fg}' stroke-width='1.4'/>"
                    + "<circle cx='8' cy='10.8' r='0.8' fill='{fg}'/>";
        }
        // Trend — chart with arrow
        if (containsAny(token, "trend")) {
            return "<polyline points='3,11 6,7 9,9 13,4' fill='none' stroke='{fg}' stroke-width='1.5' stroke-linecap='round' stroke-linejoin='round'/>"
                    + "<polyline points='10,4 13,4 13,7' fill='none' stroke='{fg}' stroke-width='1.3' stroke-linecap='round' stroke-linejoin='round'/>";
        }
        // Export / share
        if (containsAny(token, "export", "share")) {
            return "<polyline points='8,3 8,10' fill='none' stroke='{fg}' stroke-width='1.5' stroke-linecap='round'/>"
                    + "<polyline points='5,5.5 8,3 11,5.5' fill='none' stroke='{fg}' stroke-width='1.5' stroke-linecap='round' stroke-linejoin='round'/>"
                    + "<path d='M4 9v3h8V9' fill='none' stroke='{fg}' stroke-width='1.3'/>";
        }
        // Import / open
        if (containsAny(token, "import", "open")) {
            return "<polyline points='8,10 8,3' fill='none' stroke='{fg}' stroke-width='1.5' stroke-linecap='round'/>"
                    + "<polyline points='5,7.5 8,10 11,7.5' fill='none' stroke='{fg}' stroke-width='1.5' stroke-linecap='round' stroke-linejoin='round'/>"
                    + "<path d='M4 10.5v2h8v-2' fill='none' stroke='{fg}' stroke-width='1.3'/>";
        }
        // Printer / print
        if (containsAny(token, "print")) {
            return "<rect x='4.5' y='6' width='7' height='4.5' rx='.8' fill='none' stroke='{fg}' stroke-width='1.3'/>"
                    + "<path d='M5.5 6V3.5h5V6' fill='none' stroke='{fg}' stroke-width='1.2'/>"
                    + "<path d='M5.5 10.5V13h5v-2.5' fill='none' stroke='{fg}' stroke-width='1.2'/>";
        }
        // Report / chart / diagram / audit
        if (containsAny(token, "report", "chart", "diagram", "audit")) {
            return "<rect x='3.5' y='3' width='9' height='10' rx='1' fill='none' stroke='{fg}' stroke-width='1.3'/>"
                    + "<line x1='5.5' y1='6' x2='7' y2='6' stroke='{fg}' stroke-width='1.2'/>"
                    + "<line x1='5.5' y1='8' x2='10' y2='8' stroke='{fg}' stroke-width='1.2'/>"
                    + "<line x1='5.5' y1='10' x2='9' y2='10' stroke='{fg}' stroke-width='1.2'/>";
        }
        // UDF field types
        if (containsAny(token, "udf_float", "udf_integer")) {
            return "<text x='8' y='12' text-anchor='middle' fill='{fg}' font-size='10' font-weight='700' font-family='sans-serif'>#</text>";
        }
        if (containsAny(token, "udf_string")) {
            return "<text x='8' y='11.5' text-anchor='middle' fill='{fg}' font-size='9' font-weight='700' font-family='sans-serif'>A</text>";
        }
        if (containsAny(token, "udf_memo")) {
            return "<rect x='3.5' y='4' width='9' height='8' rx='1' fill='none' stroke='{fg}' stroke-width='1.3'/>"
                    + "<line x1='5' y1='6.2' x2='11' y2='6.2' stroke='{fg}' stroke-width='1'/>"
                    + "<line x1='5' y1='8' x2='11' y2='8' stroke='{fg}' stroke-width='1'/>"
                    + "<line x1='5' y1='9.8' x2='8' y2='9.8' stroke='{fg}' stroke-width='1'/>";
        }
        if (containsAny(token, "udf_list", "udf_mlist")) {
            return "<line x1='4' y1='4.5' x2='12' y2='4.5' stroke='{fg}' stroke-width='1.2'/>"
                    + "<line x1='4' y1='7' x2='12' y2='7' stroke='{fg}' stroke-width='1.2'/>"
                    + "<line x1='4' y1='9.5' x2='12' y2='9.5' stroke='{fg}' stroke-width='1.2'/>"
                    + "<line x1='4' y1='12' x2='12' y2='12' stroke='{fg}' stroke-width='1.2'/>"
                    + "<polyline points='10,6 12,4.5 10,3' fill='none' stroke='{fg}' stroke-width='1' stroke-linecap='round' stroke-linejoin='round'/>";
        }
        if (containsAny(token, "udf_url")) {
            return "<path d='M7 9.5a2.5 2.5 0 0 1 0-3.5l1.5-1.5a2.5 2.5 0 0 1 3.5 3.5L10.5 9.5' fill='none' stroke='{fg}' stroke-width='1.3' stroke-linecap='round'/>"
                    + "<path d='M9 6.5a2.5 2.5 0 0 1 0 3.5L7.5 11.5a2.5 2.5 0 0 1-3.5-3.5L5.5 6.5' fill='none' stroke='{fg}' stroke-width='1.3' stroke-linecap='round'/>";
        }
        // Code / script
        if (containsAny(token, "code", "script")) {
            return "<polyline points='5.5,4.5 3,8 5.5,11.5' fill='none' stroke='{fg}' stroke-width='1.5' stroke-linecap='round' stroke-linejoin='round'/>"
                    + "<polyline points='10.5,4.5 13,8 10.5,11.5' fill='none' stroke='{fg}' stroke-width='1.5' stroke-linecap='round' stroke-linejoin='round'/>";
        }
        // Similar / duplicate
        if (containsAny(token, "similar")) {
            return "<rect x='3' y='4' width='6' height='7' rx='.8' fill='none' stroke='{fg}' stroke-width='1.3'/>"
                    + "<rect x='7' y='5' width='6' height='7' rx='.8' fill='none' stroke='{fg}' stroke-width='1.3'/>";
        }
        // Radio button
        if (containsAny(token, "radio")) {
            return "<circle cx='8' cy='8' r='4.5' fill='none' stroke='{fg}' stroke-width='1.5'/>"
                    + "<circle cx='8' cy='8' r='2' fill='{fg}'/>";
        }
        // Settings / config / colortable / customize / customfields
        if (containsAny(token, "setting", "config", "color", "custom", "compact", "wrapping")) {
            return "<circle cx='8' cy='8' r='2.2' fill='none' stroke='{fg}' stroke-width='1.5'/>"
                    + "<path d='M8 3v2M8 11v2M3 8h2M11 8h2M4.8 4.8l1.4 1.4M9.8 9.8l1.4 1.4M4.8 11.2l1.4-1.4M9.8 6.2l1.4-1.4' stroke='{fg}' stroke-width='1.1' stroke-linecap='round'/>";
        }
        // RSS / feed
        if (containsAny(token, "rss", "feed")) {
            return "<circle cx='5' cy='11' r='1.3' fill='{fg}'/>"
                    + "<path d='M3.8 7.5a5 5 0 0 1 5 5' fill='none' stroke='{fg}' stroke-width='1.4' stroke-linecap='round'/>"
                    + "<path d='M3.8 4a8.5 8.5 0 0 1 8.5 8.5' fill='none' stroke='{fg}' stroke-width='1.4' stroke-linecap='round'/>";
        }
        // New (generic)
        if (containsAny(token, "new")) {
            return "<line x1='8' y1='4' x2='8' y2='12' stroke='{fg}' stroke-width='1.8' stroke-linecap='round'/>"
                    + "<line x1='4' y1='8' x2='12' y2='8' stroke='{fg}' stroke-width='1.8' stroke-linecap='round'/>";
        }
        // Window
        if (containsAny(token, "hide", "show", "win")) {
            return "<rect x='3.5' y='4' width='9' height='8' rx='1' fill='none' stroke='{fg}' stroke-width='1.3'/>"
                    + "<line x1='3.5' y1='6.5' x2='12.5' y2='6.5' stroke='{fg}' stroke-width='1.2'/>";
        }
        // Logout / exit
        if (containsAny(token, "logout", "exit")) {
            return "<path d='M9 3H5.5c-.6 0-1 .4-1 1v8c0 .6.4 1 1 1H9' fill='none' stroke='{fg}' stroke-width='1.3'/>"
                    + "<polyline points='8,8 13,8' fill='none' stroke='{fg}' stroke-width='1.5' stroke-linecap='round'/>"
                    + "<polyline points='11,6 13,8 11,10' fill='none' stroke='{fg}' stroke-width='1.4' stroke-linecap='round' stroke-linejoin='round'/>";
        }
        // Default: generic dot
        return "<circle cx='8' cy='8' r='2.8' fill='{fg}'/>";
    }

    private boolean containsAny(String token, String... values) {
        for (String value : values) {
            if (token.contains(value)) {
                return true;
            }
        }
        return false;
    }

    public void doPost(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse) throws IOException, ServletException {
        doGet(httpServletRequest, httpServletResponse);
    }
}
