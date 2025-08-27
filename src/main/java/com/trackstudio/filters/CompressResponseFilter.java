package com.trackstudio.filters;

import java.io.File;
import java.io.IOException;
import java.sql.Driver;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Enumeration;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.trackstudio.app.adapter.scheduler.SchedulerManager;
import com.trackstudio.exception.GranException;
import com.trackstudio.kernel.manager.IndexManager;
import com.trackstudio.startup.Config;
import com.trackstudio.startup.I18n;
import com.trackstudio.tools.tag.StoreCssJs;

import net.jcip.annotations.Immutable;

/**
 * Фильтр для сжатия ответа сервера
 */
@Immutable
public class CompressResponseFilter implements Filter {
    private static final Log log = LogFactory.getLog(CompressResponseFilter.class);

    public void init(FilterConfig filterConfig) {
        try {
            I18n.loadConfig(filterConfig.getServletContext());
            Config.loadConfig(filterConfig.getServletContext());
            StoreCssJs.init(filterConfig.getServletContext().getRealPath("/") + File.separator);
        } catch (GranException e) {
            log.error("error loading config",e);
        }
    }

    public void destroy() {

        Enumeration<Driver> drs = DriverManager.getDrivers();
        while (drs.hasMoreElements()) {
            unregister(drs.nextElement());
        }
        IndexManager.getInstance().closeIndex();
        SchedulerManager.getInstance().shutdown();
    }

    private void unregister(Driver dr) {
        try {
            DriverManager.deregisterDriver(dr);
        } catch (SQLException sql) {
            sql.printStackTrace();
        }
    }

    public void doFilter(ServletRequest req, ServletResponse res, FilterChain chain) throws IOException, ServletException {
        //делаю тут а не в init, т.к. в init эти переменные могут быть еще не загружены
        boolean enabled = Config.getInstance().isUseGZIP();
        if (!enabled) {
            chain.doFilter(req, res);
            return;
        }
        HttpServletRequest request = (HttpServletRequest) req;
        HttpServletResponse response = (HttpServletResponse) res;
        response.setHeader("Cache-Control", "public");
        response.setHeader("Pragma", "public");
        response.setHeader("Access-Control-Allow-Methods", "POST, GET, PUT, DELETE");
        String ae = request.getHeader("accept-encoding");
        if (ae != null && ae.contains("gzip")) {
            response.addHeader("Content-Encoding", "gzip");
            GZipServletResponseWrapper gzipResponse = new GZipServletResponseWrapper(response);
            try {
                chain.doFilter(request, gzipResponse);
            } catch (Exception ex) {
                throw new ServletException(ex);
            } finally {
                gzipResponse.flushBuffer();
                gzipResponse.close();
            }
        } else {
            log.debug("Browser does not support gzip encoding");
            chain.doFilter(req, res);
        }
    }
}
