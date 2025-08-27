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
 * TODO: Document type AjaxTabPageTag.
 * 
 * @author Darren Spurgeon
 * @version $Revision$ $Date$
 */
public class AjaxTabPageTag extends TagSupport {

  private String caption;

  private String baseUrl;

  private String defaultTab;

  private String parameters = "";

  public String getCaption() {
    return caption;
  }

  public void setCaption(String caption) {
    this.caption = caption;
  }

  public String getDefaultTab() {
    return defaultTab;
  }

  public void setDefaultTab(String defaultTab) {
    this.defaultTab = defaultTab;
  }

  public String getParameters() {
    return parameters;
  }

  public void setParameters(String parameters) {
    this.parameters = parameters;
  }

  public String getBaseUrl() {
    return baseUrl;
  }

  public void setBaseUrl(String baseUrl) {
    this.baseUrl = baseUrl;
  }

  public int doStartTag() throws JspException {
    // Required Properties
    this.baseUrl = (String) ExpressionEvaluatorManager.evaluate("baseUrl", this.baseUrl,
        String.class, this, super.pageContext);
    if (this.caption != null) {
    this.caption = (String) ExpressionEvaluatorManager.evaluate("caption", this.caption,
        String.class, this, super.pageContext);
	}
    // Optional Properties
    if (this.parameters != null) {
      this.parameters = (String) ExpressionEvaluatorManager.evaluate("parameters", this.parameters,
          String.class, this, super.pageContext);
    }
    if (this.defaultTab != null) {
      this.defaultTab = (String) ExpressionEvaluatorManager.evaluate("defaultTab", this.defaultTab,
          String.class, this, super.pageContext);
    }
    StringBuffer script = new StringBuffer();
javax.servlet.jsp.tagext.Tag parentTag = getParent();
while (parentTag!=null && !(parentTag instanceof AjaxTabPanelTag)){
parentTag = parentTag.getParent();
}
if (parentTag==null) throw new JspException("AJAX Tag tab has no parent");
AjaxTabPanelTag par = (AjaxTabPanelTag)parentTag;
    if (Boolean.valueOf(this.defaultTab).booleanValue()) {
      script.append("<li><a id=\"");
      script.append(par.getCurrentStyleId());

      script.append("\" ");
        if (par.getCurrentStyleClass()!=null){
          script.append(" class=\"");
          script.append(par.getCurrentStyleClass());
          script.append("\"" );
      }
      par.setDefaultTabBaseUrl(this.baseUrl+par.getBase());
      par.setDefaultTabParameters(this.parameters);
    } else {
      script.append("<li><a ");
    }
    script.append("href=\"javascript://nop/\" onclick=\"executeAjaxTab");
      script.append(par.getPanelStyleId());
      script.append("(this, '");
      script.append(this.baseUrl).append(par.getBase());
    script.append("', '");
    script.append(this.parameters);


    script.append("'); return false;\" ");
      // patch to remove focus box from the tab button
      script.append("onfocus=\"blur()\" >");
    JspWriter writer = pageContext.getOut();
    try {
      writer.println(script);
    } catch (IOException e) {
      throw new JspException(e.getMessage());
    }

    return EVAL_BODY_INCLUDE;
  }

  public int doEndTag() throws JspException {
    StringBuffer script = new StringBuffer();
if (this.caption!=null)
    script.append(this.caption);
    script.append("</a></li>");
    JspWriter writer = pageContext.getOut();
    try {
      writer.println(script);
    } catch (IOException e) {
      throw new JspException(e.getMessage());
    }
    return EVAL_PAGE;
  }

  public void release() {
    this.caption = null;
    this.baseUrl = null;
    this.defaultTab = null;
    this.parameters = null;
    super.release();
  }

}
