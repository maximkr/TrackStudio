/**
 * Copyright 2005 Darren L. Spurgeon
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.ajaxtags.tags;

import java.io.IOException;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.JspWriter;
import javax.servlet.jsp.tagext.TagSupport;

import org.apache.taglibs.standard.lang.support.ExpressionEvaluatorManager;

/**
 * Tag handler for the callout AJAX tag.
 * 
 * @author Darren Spurgeon
 * @version $Revision$ $Date$
 */
public class AjaxCalloutTag extends TagSupport {

  private String baseUrl;

  private String source;

  private String sourceClass;

  private String parameters;

  private String classNamePrefix;

  private String boxPosition;

  private String title;

  private String useTitleBar;

  private String timeout;

  private String eventType;

  private String postFunction;

  private String emptyFunction;

  private String errorFunction;

  public String getBaseUrl() {
    return baseUrl;
  }

  public void setBaseUrl(String baseUrl) {
    this.baseUrl = baseUrl;
  }

  public String getBoxPosition() {
    return boxPosition;
  }

  public void setBoxPosition(String boxPosition) {
    this.boxPosition = boxPosition;
  }

  public String getClassNamePrefix() {
    return classNamePrefix;
  }

  public void setClassNamePrefix(String classNamePrefix) {
    this.classNamePrefix = classNamePrefix;
  }

  public String getEmptyFunction() {
    return emptyFunction;
  }

  public void setEmptyFunction(String emptyFunction) {
    this.emptyFunction = emptyFunction;
  }

  public String getErrorFunction() {
    return errorFunction;
  }

  public void setErrorFunction(String errorFunction) {
    this.errorFunction = errorFunction;
  }

  public String getEventType() {
    return eventType;
  }

  public void setEventType(String eventType) {
    this.eventType = eventType;
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

  public String getSource() {
    return source;
  }

  public void setSource(String source) {
    this.source = source;
  }

  public String getSourceClass() {
    return sourceClass;
  }

  public void setSourceClass(String sourceClass) {
    this.sourceClass = sourceClass;
  }

  public String getTimeout() {
    return timeout;
  }

  public void setTimeout(String timeout) {
    this.timeout = timeout;
  }

  public String getTitle() {
    return title;
  }

  public void setTitle(String title) {
    this.title = title;
  }

  public String getUseTitleBar() {
    return useTitleBar;
  }

  public void setUseTitleBar(String useTitleBar) {
    this.useTitleBar = useTitleBar;
  }

  public int doStartTag() throws JspException {
    // Required Properties
    this.baseUrl = (String) ExpressionEvaluatorManager.evaluate("baseUrl", this.baseUrl,
        String.class, this, super.pageContext);
    this.parameters = (String) ExpressionEvaluatorManager.evaluate("parameters", this.parameters,
        String.class, this, super.pageContext);
    this.classNamePrefix = (String) ExpressionEvaluatorManager.evaluate("classNamePrefix",
        this.classNamePrefix, String.class, this, super.pageContext);
    if (this.title != null) {
      this.title = (String) ExpressionEvaluatorManager.evaluate("title", this.title, String.class,
          this, super.pageContext);
    }

    // Optional Properties
    if (this.source != null) {
      this.source = (String) ExpressionEvaluatorManager.evaluate("source", this.source,
          String.class, this, super.pageContext);
    }
    if (this.sourceClass != null) {
      this.sourceClass = (String) ExpressionEvaluatorManager.evaluate("sourceClass",
          this.sourceClass, String.class, this, super.pageContext);
    }
    if (this.boxPosition != null) {
      this.boxPosition = (String) ExpressionEvaluatorManager.evaluate("boxPosition",
          this.boxPosition, String.class, this, super.pageContext);
    }
    if (this.useTitleBar != null) {
      this.useTitleBar = (String) ExpressionEvaluatorManager.evaluate("useTitleBar",
          this.useTitleBar, String.class, this, super.pageContext);
    }
    if (this.title != null) {
      this.title = (String) ExpressionEvaluatorManager.evaluate("title", this.title, String.class,
          this, super.pageContext);
    }
    if (this.timeout != null) {
      this.timeout = (String) ExpressionEvaluatorManager.evaluate("timeout", this.timeout,
          String.class, this, super.pageContext);
    }
    if (this.eventType != null) {
      this.eventType = (String) ExpressionEvaluatorManager.evaluate("eventType", this.eventType,
          String.class, this, super.pageContext);
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
    return SKIP_BODY;
  }

  public int doEndTag() throws JspException {
    OptionsBuilder options = new OptionsBuilder();
    if (this.source != null) {
      options.add("source", this.source, true);
    } else {
      options.add("sourceClass", this.sourceClass, true);
    }
    options
        .add("parameters", this.parameters, true)
        .add("classNamePrefix", this.classNamePrefix, true);
    if (this.boxPosition != null)
      options.add("boxPosition", this.boxPosition, true);
    if (this.useTitleBar != null)
      options.add("useTitleBar", this.useTitleBar, true);
    if (this.title != null)
      options.add("title", this.title, true);
    if (this.timeout != null)
      options.add("timeout", this.timeout, true);
    if (this.eventType != null)
      options.add("eventType", this.eventType, true);
    if (this.postFunction != null)
      options.add("postFunction", this.postFunction, false);
    if (this.emptyFunction != null)
      options.add("emptyFunction", this.emptyFunction, false);
    if (this.errorFunction != null)
      options.add("errorFunction", this.errorFunction, false);

    StringBuffer script = new StringBuffer();
    script.append("<script type=\"text/javascript\">\n");
    script.append("new AjaxJspTag.Callout(\n");
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
    this.sourceClass = null;
    this.parameters = null;
    this.classNamePrefix = null;
    this.boxPosition = null;
    this.title = null;
    this.useTitleBar = null;
    this.timeout = null;
    this.eventType = null;
    this.postFunction = null;
    this.emptyFunction = null;
    this.errorFunction = null;
    super.release();
  }

}
