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
 * Tag handler for the autocomplete AJAX tag.
 * 
 * @author Darren Spurgeon
 * @version $Revision$ $Date$
 */
public class AjaxAutocompleteTag extends TagSupport {

  private String baseUrl;

  private String source;

  private String target;

  private String parameters;

  private String forceSelection;

  private String minimumCharacters;

  private String appendValue;

  private String appendSeparator;

  private String className;

  private String progressStyle;

  private String postFunction;

  private String emptyFunction;

  private String errorFunction;

  private String delay;

  public String getAppendSeparator() {
    return appendSeparator;
  }

  public void setAppendSeparator(String appendSeparator) {
    this.appendSeparator = appendSeparator;
  }

  public String getAppendValue() {
    return appendValue;
  }

  public void setAppendValue(String appendValue) {
    this.appendValue = appendValue;
  }

  public String getBaseUrl() {
    return baseUrl;
  }

  public void setBaseUrl(String baseUrl) {
    this.baseUrl = baseUrl;
  }

  public String getClassName() {
    return className;
  }

  public void setClassName(String className) {
    this.className = className;
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

  public String getForceSelection() {
    return forceSelection;
  }

  public void setForceSelection(String forceSelection) {
    this.forceSelection = forceSelection;
  }

  public String getMinimumCharacters() {
    return minimumCharacters;
  }

  public void setMinimumCharacters(String minimumCharacters) {
    this.minimumCharacters = minimumCharacters;
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

  public String getProgressStyle() {
    return progressStyle;
  }

  public void setProgressStyle(String progressStyle) {
    this.progressStyle = progressStyle;
  }

  public String getSource() {
    return source;
  }

  public void setSource(String source) {
    this.source = source;
  }

  public String getTarget() {
    return target;
  }

  public void setTarget(String target) {
    this.target = target;
  }

  public String getDelay() {
    return delay;
  }

  public void setDelay(String delay) {
    this.delay = delay;
  }

  public int doStartTag() throws JspException {
    // Required Properties
    this.baseUrl = (String) ExpressionEvaluatorManager.evaluate("baseUrl", this.baseUrl,
        String.class, this, super.pageContext);
    this.parameters = (String) ExpressionEvaluatorManager.evaluate("parameters", this.parameters,
        String.class, this, super.pageContext);
    this.source = (String) ExpressionEvaluatorManager.evaluate("source", this.source, String.class,
        this, super.pageContext);
    this.target = (String) ExpressionEvaluatorManager.evaluate("target", this.target, String.class,
        this, super.pageContext);
    this.className = (String) ExpressionEvaluatorManager.evaluate("className", this.className,
        String.class, this, super.pageContext);

    // Optional Properties
    if (this.forceSelection != null) {
      this.forceSelection = (String) ExpressionEvaluatorManager.evaluate("forceSelection",
          this.forceSelection, String.class, this, super.pageContext);
    }
    if (this.minimumCharacters != null) {
      this.minimumCharacters = (String) ExpressionEvaluatorManager.evaluate("minimumCharacters",
          this.minimumCharacters, String.class, this, super.pageContext);
    }
    if (this.progressStyle != null) {
      this.progressStyle = (String) ExpressionEvaluatorManager.evaluate("progressStyle",
          this.progressStyle, String.class, this, super.pageContext);
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
    if (this.delay != null) {
      this.delay = (String) ExpressionEvaluatorManager.evaluate("delay",
              this.delay, String.class, this, super.pageContext);
    }
    return SKIP_BODY;
  }

  public int doEndTag() throws JspException {
    OptionsBuilder options = new OptionsBuilder();
    options.add("source", this.source, true).add("target", this.target, true).add("parameters",
        this.parameters, true).add("className", this.className, true);
    if (this.progressStyle != null)
      options.add("progressStyle", this.progressStyle, true);
    if (this.minimumCharacters != null)
      options.add("minimumCharacters", this.minimumCharacters, true);
    if (this.forceSelection != null)
      options.add("forceSelection", this.forceSelection, true);
    if (this.appendValue != null)
      options.add("appendValue", this.appendValue, true);
    if (this.appendSeparator != null)
      options.add("appendSeparator", this.appendSeparator, true);
    if (this.postFunction != null)
      options.add("postFunction", this.postFunction, false);
    if (this.emptyFunction != null)
      options.add("emptyFunction", this.emptyFunction, false);
    if (this.errorFunction != null)
      options.add("errorFunction", this.errorFunction, false);
    if (this.delay != null)
      options.add("delay", this.delay, true);

    StringBuffer script = new StringBuffer();
    script.append("<script type=\"text/javascript\">\n");
    script.append("new AjaxJspTag.Autocomplete(\n");
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
    this.className = null;
    this.emptyFunction = null;
    this.errorFunction = null;
    this.forceSelection = null;
    this.minimumCharacters = null;
    this.appendValue = null;
    this.appendSeparator = null;
    this.parameters = null;
    this.postFunction = null;
    this.source = null;
    this.target = null;
    this.delay = null;
    super.release();
  }

}
