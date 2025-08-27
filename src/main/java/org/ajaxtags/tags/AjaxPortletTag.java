package org.ajaxtags.tags;

import java.io.IOException;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.JspWriter;
import javax.servlet.jsp.tagext.TagSupport;

import org.apache.taglibs.standard.lang.support.ExpressionEvaluatorManager;

/**
 * Tag handler for the portlet AJAX tag.
 * 
 * @author Darren Spurgeon
 * @version $Revision$ $Date$ $Author$
 */
public class AjaxPortletTag extends TagSupport {

  private String baseUrl;

  private String source;

  private String parameters;

  private String classNamePrefix;

  private String title;

  private String imageClose;

  private String imageMaximize;

  private String imageMinimize;

  private String imageRefresh;

  private String refreshPeriod;

  private String executeOnLoad;

  private String expireDays;

  private String expireHours;

  private String expireMinutes;

  private String postFunction;

  private String emptyFunction;

  private String errorFunction;

  public String getBaseUrl() {
    return baseUrl;
  }

  public void setBaseUrl(String baseUrl) {
    this.baseUrl = baseUrl;
  }

  public String getClassNamePrefix() {
    return classNamePrefix;
  }

  public void setClassNamePrefix(String classNamePrefix) {
    this.classNamePrefix = classNamePrefix;
  }

  public String getImageClose() {
    return imageClose;
  }

  public void setImageClose(String imageClose) {
    this.imageClose = imageClose;
  }

  public String getEmptyFunction() {
    return emptyFunction;
  }

  public void setEmptyFunction(String emptyFunction) {
    this.emptyFunction = emptyFunction;
  }

  public String getExecuteOnLoad() {
    return executeOnLoad;
  }

  public void setExecuteOnLoad(String executeOnLoad) {
    this.executeOnLoad = executeOnLoad;
  }

  public String getExpireDays() {
    return expireDays;
  }

  public void setExpireDays(String expireDays) {
    this.expireDays = expireDays;
  }

  public String getExpireHours() {
    return expireHours;
  }

  public void setExpireHours(String expireHours) {
    this.expireHours = expireHours;
  }

  public String getExpireMinutes() {
    return expireMinutes;
  }

  public void setExpireMinutes(String expireMinutes) {
    this.expireMinutes = expireMinutes;
  }

  public String getErrorFunction() {
    return errorFunction;
  }

  public void setErrorFunction(String errorFunction) {
    this.errorFunction = errorFunction;
  }

  public String getImageMaximize() {
    return imageMaximize;
  }

  public void setImageMaximize(String imageMaximize) {
    this.imageMaximize = imageMaximize;
  }

  public String getImageMinimize() {
    return imageMinimize;
  }

  public void setImageMinimize(String imageMinimize) {
    this.imageMinimize = imageMinimize;
  }

  public String getParameters() {
    return parameters;
  }

  public void setParameters(String parameters) {
    this.parameters = parameters;
  }

  public String getPostFunction() {
    return postFunction;
  }

  public void setPostFunction(String postFunction) {
    this.postFunction = postFunction;
  }

  public String getImageRefresh() {
    return imageRefresh;
  }

  public void setImageRefresh(String imageRefresh) {
    this.imageRefresh = imageRefresh;
  }

  public String getRefreshPeriod() {
    return refreshPeriod;
  }

  public void setRefreshPeriod(String refreshPeriod) {
    this.refreshPeriod = refreshPeriod;
  }

  public String getSource() {
    return source;
  }

  public void setSource(String source) {
    this.source = source;
  }

  public String getTitle() {
    return title;
  }

  public void setTitle(String title) {
    this.title = title;
  }

  public int doStartTag() throws JspException {
    // Required Properties
    this.baseUrl = (String) ExpressionEvaluatorManager.evaluate("baseUrl", this.baseUrl,
        String.class, this, super.pageContext);
    if (this.parameters != null) {
      this.parameters = (String) ExpressionEvaluatorManager.evaluate("parameters", this.parameters,
          String.class, this, super.pageContext);
    }
    if (this.classNamePrefix != null) {
      this.classNamePrefix = (String) ExpressionEvaluatorManager.evaluate("classNamePrefix",
          this.classNamePrefix, String.class, this, super.pageContext);
    }

    // Optional Properties
    if (this.source != null) {
      this.source = (String) ExpressionEvaluatorManager.evaluate("source", this.source,
          String.class, this, super.pageContext);
    }
    if (this.title != null) {
      this.title = (String) ExpressionEvaluatorManager.evaluate("title", this.title, String.class,
          this, super.pageContext);
    }
    if (this.imageClose != null) {
      this.imageClose = (String) ExpressionEvaluatorManager.evaluate("imageClose", this.imageClose,
          String.class, this, super.pageContext);
    }
    if (this.imageMaximize != null) {
      this.imageMaximize = (String) ExpressionEvaluatorManager.evaluate("imageMaximize",
          this.imageMaximize, String.class, this, super.pageContext);
    }
    if (this.imageMinimize != null) {
      this.imageMinimize = (String) ExpressionEvaluatorManager.evaluate("imageMinimize",
          this.imageMinimize, String.class, this, super.pageContext);
    }
    if (this.imageRefresh != null) {
      this.imageRefresh = (String) ExpressionEvaluatorManager.evaluate("imageRefresh",
          this.imageRefresh, String.class, this, super.pageContext);
    }
    if (this.refreshPeriod != null) {
      this.refreshPeriod = (String) ExpressionEvaluatorManager.evaluate("refreshPeriod",
          this.refreshPeriod, String.class, this, super.pageContext);
    }
    if (this.executeOnLoad != null) {
      this.executeOnLoad = (String) ExpressionEvaluatorManager.evaluate("executeOnLoad",
          this.executeOnLoad, String.class, this, super.pageContext);
    }
    if (this.postFunction != null) {
      this.postFunction = (String) ExpressionEvaluatorManager.evaluate("postFunction",
          this.postFunction, String.class, this, super.pageContext);
    }
    if (this.errorFunction != null) {
      this.errorFunction = (String) ExpressionEvaluatorManager.evaluate("errorFunction",
          this.errorFunction, String.class, this, super.pageContext);
    }
    if (this.emptyFunction != null) {
      this.emptyFunction = (String) ExpressionEvaluatorManager.evaluate("emptyFunction",
          this.emptyFunction, String.class, this, super.pageContext);
    }
    StringBuffer script = new StringBuffer();

    script.append("<div id=\"")
          .append(this.source)
          .append("\" class=\"")
          .append(this.classNamePrefix)
            .append("\">");
      if (this.title!=null && this.title.length()>0){
    script.append("<div class=\"")
          .append(this.classNamePrefix)
          .append("Tools\">");
    if (this.imageRefresh != null) {
      script.append("<img class=\"")
            .append(this.classNamePrefix)
            .append("Refresh\" src=\"")
            .append(this.imageRefresh)
            .append("\"/></a>");
    }
    if (this.imageMaximize != null && this.imageMinimize != null) {
      script.append("<img class=\"")
            .append(this.classNamePrefix)
            .append("Size\" src=\"")
            .append(this.imageMinimize)
            .append("\"/></a>");
    }
    if (this.imageClose != null) {
      script.append("<img class=\"")
            .append(this.classNamePrefix)
            .append("Close\" src=\"")
            .append(this.imageClose)
            .append("\"/></a>");
    }
    script.append("</div>");
    script.append("<div class=\"")
          .append(this.classNamePrefix)
          .append("Title\">");
      };
      JspWriter writer = pageContext.getOut();
    try {
      writer.println(script);
    } catch (IOException e) {
      throw new JspException(e.getMessage());
    }
    return EVAL_BODY_INCLUDE;
  }

  public int doEndTag() throws JspException {
      OptionsBuilder options = new OptionsBuilder();
                options.add("source", this.source, true);
                options.add("classNamePrefix", this.classNamePrefix, true);
                if (this.title != null)
                options.add("title", this.title, true);
                if (this.imageClose != null)
                  options.add("imageClose", this.imageClose, true);
                if (this.imageMaximize != null)
                  options.add("imageMaximize", this.imageMaximize, true);
                if (this.imageMinimize != null)
                  options.add("imageMinimize", this.imageMinimize, true);
                if (this.imageRefresh != null)
                  options.add("imageRefresh", this.imageRefresh, true);
                if (this.parameters != null)
                  options.add("parameters", this.parameters, true);
                if (this.refreshPeriod != null)
                  options.add("refreshPeriod", this.refreshPeriod, true);
                if (this.executeOnLoad != null)
                  options.add("executeOnLoad", this.executeOnLoad, true);
                if (this.expireDays != null)
                  options.add("expireDays", this.expireDays, true);
                if (this.expireHours != null)
                  options.add("expireHours", this.expireHours, true);
                if (this.expireMinutes != null)
                  options.add("expireMinutes", this.expireMinutes, true);
                if (this.postFunction != null)
                  options.add("postFunction", this.postFunction, false);
                if (this.emptyFunction != null)
                  options.add("emptyFunction", this.emptyFunction, false);
                if (this.errorFunction != null)
                  options.add("errorFunction", this.errorFunction, false);

      StringBuffer script = new StringBuffer();
         if (this.title!=null && this.title.length()>0){
          script.append("</div>");
         };
    script.append("<div class=\"")
          .append(this.classNamePrefix)
          .append("Content\"></div>");

    script.append("</div>\n");
    script.append("<script type=\"text/javascript\">\n");
    script.append("new AjaxJspTag.Portlet(\n");
    script.append('\"');
    script.append(this.baseUrl);
    script.append("\", {\n");
    script.append(options.toString());
    script.append("});\n");
    script.append("</script>\n\n");

    JspWriter writer = pageContext.getOut();
    try {
      writer.println(script);
    } catch (IOException e) {
      throw new JspException(e.getMessage());
    }
    return EVAL_PAGE;
  }

  public void release() {
    this.baseUrl = null;
    this.source = null;
    this.parameters = null;
    this.classNamePrefix = null;
    this.title = null;
    this.imageClose = null;
    this.imageMaximize = null;
    this.imageMinimize = null;
    this.imageRefresh = null;
    this.refreshPeriod = null;
    this.executeOnLoad = null;
    this.expireDays = null;
    this.expireHours = null;
    this.expireMinutes = null;
    this.postFunction = null;
    this.emptyFunction = null;
    this.errorFunction = null;
    super.release();
  }

}
