package com.trackstudio.tools.tag;

import java.io.IOException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.jsp.JspException;
import javax.servlet.jsp.JspTagException;
import javax.servlet.jsp.JspWriter;
import javax.servlet.jsp.tagext.BodyContent;
import javax.servlet.jsp.tagext.BodyTagSupport;

import org.apache.taglibs.standard.lang.support.ExpressionEvaluatorManager;

import com.trackstudio.action.GeneralAction;
import com.trackstudio.app.session.SessionContext;
import com.trackstudio.app.session.SessionManager;
import com.trackstudio.exception.GranException;
import com.trackstudio.kernel.cache.TaskRelatedManager;
import com.trackstudio.secured.SecuredTaskBean;
import com.trackstudio.tools.textfilter.MacrosUtil;
import com.trackstudio.tools.textfilter.Wiki;
import com.trackstudio.view.TaskViewHTML;

import net.jcip.annotations.ThreadSafe;

/**
 * Класс кастомного JSTL-тега, описывающий тег < ts:htmlfilter >. <br/>
 * Используется для обработки Description
 */
@ThreadSafe
public class HtmlFilterTag extends BodyTagSupport {
	 // Сессия пользователя
	private volatile String session = null;
	private volatile SessionContext sc = null;
	 // Тело тега
	private volatile String body = null;
    private volatile HttpServletRequest request;
    private volatile boolean macros;
    private volatile boolean audit;

	public void setRequest(HttpServletRequest request) {
		this.request = request;
	}


	public boolean isAudit() {
		return audit;
	}

	public void setAudit(boolean audit) {
		this.audit = audit;
	}

	public boolean isMacros() {
		return macros;
	}

	public void setMacros(boolean macros) {
		this.macros = macros;
	}

	/**
	 * Возвращает сессию пользователя
	 * 
	 * @return сессия пользователя
	 */
	public String getSession() {
		return session;
	}

	/**
	 * Устанавливает сессию пользователя
	 * 
	 * @param session
	 *            сессия пользователя
	 */
	public void setSession(String session) {
		this.session = session;
	}

	/**
	 * Обрабатывает первое вхождение тега
	 * 
	 * @return код, что делать дальше
	 * @throws JspException
	 *             при необходимости
	 */
	public int doStartTag() throws JspException {
		this.session = (String) ExpressionEvaluatorManager.evaluate("session",
				this.session, String.class, this, super.pageContext);
		try {
			sc = SessionManager.getInstance().getSessionContext(this.session);
		} catch (GranException ge) {
			throw new JspException(ge.getMessage(), ge);
		}
		return EVAL_BODY_BUFFERED;
	}

	/**
	 * Обрабатывает последнее вхождение тега
	 * 
	 * @return код, что делать дальше
	 * @throws JspException
	 *             при необходимости
	 */
	public int doEndTag() throws JspException {
		return EVAL_PAGE;
	}

	/**
	 * Обрабатывает тело тега
	 * 
	 * @return код, что делать дальше
	 * @throws JspTagException
	 *             при необходимости
	 */
	public int doAfterBody() throws JspTagException {
		BodyContent bc = getBodyContent();
		// получить bc в виде строки
		if (bc != null)
			body = bc.getString();
		// do something to the body here.
		StringBuffer script = new StringBuffer();
		if (sc != null && body != null) {
			TaskViewHTML view;
			try {
                String id = GeneralAction.getInstance().getId(request, null);
                id = TaskRelatedManager.getInstance().isTaskExists(id) && sc.allowedByACL(id) ? id : "1";
				view = new TaskViewHTML(new SecuredTaskBean(id, sc), MacrosUtil.buildRealURL(request));
				Wiki w = new Wiki(view);
                w.setParameters(this.request.getParameterMap());
				script.append(w.toMacros(body));
			} catch (GranException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		JspWriter out = bc.getEnclosingWriter();
		if (body != null) {
			try {
				out.print(script.toString());
			} catch (IOException e) {
				throw new JspTagException(e.getMessage());
			}
		}
		return SKIP_BODY;
	}

	/**
	 * Срабатывает при завершении работы с тегом
	 */
	public void release() {
		sc = null;
		session = null;
		body = null;
		super.release();
	}
}