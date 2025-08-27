package com.trackstudio.action;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.MalformedURLException;
import java.util.Arrays;
import java.util.logging.Logger;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.trackstudio.app.adapter.AdapterManager;
import com.trackstudio.app.filter.TaskFValue;
import com.trackstudio.app.report.handmade.HandMadeReportManager;
import com.trackstudio.app.session.SessionContext;
import com.trackstudio.exception.AccessDeniedException;
import com.trackstudio.exception.GranException;
import com.trackstudio.exception.UserException;
import com.trackstudio.kernel.lock.LockManager;
import com.trackstudio.secured.SecuredReportBean;
import com.trackstudio.securedkernel.SecuredReportAdapterManager;

import static com.trackstudio.securedkernel.SecuredReportAdapterManager.*;

public class WebReportServlet extends HttpServlet {

	private static final long serialVersionUID = -3656202991475539637L;

	private static final LockManager lockManager = LockManager.getInstance();

	protected static final Logger logger = Logger.getLogger("com.trackstudio.action.WebReportServlet");

	public WebReportServlet() {
		super();
	}

	public void destroy() {
		super.destroy();
	}

	public void buildReportNotBirt(
		SessionContext sc, SecuredReportBean srb, String reportType, HttpServletResponse resp, HttpServletRequest req,
		String taskId, String encoding, String delimiter, TaskFValue fv) throws GranException, IOException
	{
		String filterId = srb.getFilterId();

		String export = new HandMadeReportManager().generate(sc, taskId, filterId, fv, reportType, delimiter, encoding);
		if (SecuredReportAdapterManager.RT_CSV.equalsIgnoreCase(reportType)) {
			resp.setContentType("text/html; charset=" + encoding);
		}
		if (SecuredReportAdapterManager.RT_TREE_XML.equalsIgnoreCase(reportType)) {
			resp.setContentType("text/xml; charset=" + encoding);
		}
		if (SecuredReportAdapterManager.RT_CSV.equalsIgnoreCase(reportType)) {
			PrintWriter out = resp.getWriter();
			try {
				String name = srb.getName().replaceAll(" ", "_") + ".csv";
				DownloadServlet.buildHeaderForBrowser(req, resp, name);
				out.write(export);
			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				out.close();
			}
			return;
		}
		resp.getWriter().println(export);
	}

	@Override
	public void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		boolean w = lockManager.acquireConnection();
		try {
			SessionContext sc = GeneralAction.getInstance().imports(req, resp);
			if (sc == null || req.getParameter("taskId") == null || req.getParameter("repId") == null) {
				throw new UserException("reportId, sc or taskId are not set");
			}

			SecuredReportBean srb = AdapterManager.getInstance().getSecuredFindAdapterManager().findReportById(sc, req.getParameter("repId"));
			TaskFValue fv = AdapterManager.getInstance().getSecuredReportAdapterManager().getFValue(sc, req.getParameter("repId"));

			buildReportNotBirt(sc, srb, req.getParameter("repType"), resp, req,
				req.getParameter("taskId"), req.getParameter("charset"), req.getParameter("delimiter"), fv);
			return;
		} catch (FileNotFoundException fe) {
			logger.warning(fe.getMessage());
		} catch (MalformedURLException me) {
			logger.warning(me.getMessage());
		} catch (AccessDeniedException acd) {
			resp.sendError(HttpServletResponse.SC_FORBIDDEN, acd.getMessage());
		} catch (UserException ue) {
			resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
			resp.sendError(HttpServletResponse.SC_BAD_REQUEST, ue.getMessage());
		} catch (Exception ex) {
			log.error("Error occurred in web report", ex);
			req.setAttribute("javax.servlet.jsp.jspException", ex);
			RequestDispatcher requestDispatcher = req.getRequestDispatcher("/jsp/Error.jsp");
			requestDispatcher.forward(req, resp);
		} finally {
			if (w) {
				lockManager.releaseConnection();
			}
		}
	}

	public void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		doGet(request, response);
	}

	public void init() throws ServletException {
	}
}