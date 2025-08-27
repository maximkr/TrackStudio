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
 * Tag handler for the toggle (on/off, true/false) AJAX tag.
 * 
 * @author Darren Spurgeon
 * @version $Revision$ $Date$
 */
public class AjaxToggleTag extends TagSupport {

  private String baseUrl;

  private String image;

  private String state;

  private String stateXmlName;

  private String parameters;

  private String imagePattern;

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

  public String getImage() {
    return image;
  }

  public void setImage(String image) {
    this.image = image;
  }

  public String getImagePattern() {
    return imagePattern;
  }

  public void setImagePattern(String imagePattern) {
    this.imagePattern = imagePattern;
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

  public String getState() {
    return state;
  }

  public void setState(String state) {
    this.state = state;
  }

  public String getStateXmlName() {
    return stateXmlName;
  }

  public void setStateXmlName(String stateXmlName) {
    this.stateXmlName = stateXmlName;
  }

  public int doStartTag() throws JspException {
    // Required Properties
    this.baseUrl = (String) ExpressionEvaluatorManager.evaluate("baseUrl", this.baseUrl,
        String.class, this, super.pageContext);
    this.image = (String) ExpressionEvaluatorManager.evaluate("image", this.image, String.class,
        this, super.pageContext);
    this.imagePattern = (String) ExpressionEvaluatorManager.evaluate("imagePattern",
        this.imagePattern, String.class, this, super.pageContext);
    this.state = (String) ExpressionEvaluatorManager.evaluate("state", this.state, String.class,
        this, super.pageContext);
    this.stateXmlName = (String) ExpressionEvaluatorManager.evaluate("stateXmlName",
        this.stateXmlName, String.class, this, super.pageContext);

    // Optional Properties
    if (this.parameters != null) {
      this.parameters = (String) ExpressionEvaluatorManager.evaluate("parameters", this.parameters,
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
    options.add("state", this.state, true).add("stateXmlName", this.stateXmlName, true).add(
        "image", this.image, true).add("imagePattern", this.imagePattern, true);
    if (this.parameters != null)
      options.add("parameters", this.parameters, true);
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
    script.append("new AjaxJspTag.Toggle(\n");
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
    this.image = null;
    this.state = null;
    this.stateXmlName = null;
    this.imagePattern = null;
    this.parameters = null;
    this.eventType = null;
    this.postFunction = null;
    this.emptyFunction = null;
    this.errorFunction = null;
    super.release();
  }
}
