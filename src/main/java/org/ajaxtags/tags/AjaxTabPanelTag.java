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
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.JspWriter;
import javax.servlet.jsp.tagext.TagSupport;

import org.apache.taglibs.standard.lang.support.ExpressionEvaluatorManager;

/**
 * TODO: Document type AjaxTabPanelTag.
 *
 * @author Darren Spurgeon
 * @version $Revision$ $Date$
 */
public class AjaxTabPanelTag extends TagSupport {
    /**
     </ul>
     </div>
     <div style="display: none" id="yellowbox"></div>
     <script type="text/javascript">
     function loadAjaxTabSubtasksAction(elem, url, params) {
     document.getElementById("yellowbox").innerHTML='';
     var myAjax = $.ajax(url, {
     cache: false,
     source: elem,
     currentStyleId: "selected",
     data: params,
     target: "yellowbox"
     , success: function(data) {
     $('#yellowbox').html(data);
     }});
     document.getElementById("yellowbox").style.display='block';
     }

     function executeAjaxTabSubtasksAction(elem, url, params) {
     if (elem.id!=null && elem.id!='') {
     elem.id='';
     elem.className='';
     resetTabPanel("SubtasksAction");
     document.getElementById("yellowbox").style.display='none';
     } else {
     loadAjaxTabSubtasksAction(elem, url, params);
     elem.id='selected';
     elem.className='null';
     };
     }

     </script>
     */

    private String panelStyleId;
    private String cache = "false";
    private String panelStyleClass;
    private String baseUrl;
    private String contentStyleId;
    private String currentStyleClass;

    private transient String defaultTabBaseUrl;

    private String currentStyleId;

    private transient String defaultTabParameters = "";

    private String postFunction;

    private String emptyFunction;

    private String errorFunction;

    public String getContentStyleId() {
        return contentStyleId;
    }

    public void setContentStyleId(String contentStyleId) {
        this.contentStyleId = contentStyleId;
    }

    public String getCurrentStyleId() {
        return currentStyleId;
    }
    public String getCache() {
        return cache;
    }

    public void setCache(String cache) {
        this.cache = cache;
    }

    public String getCurrentStyleClass() {
        return currentStyleClass;
    }
    public void setCurrentStyleId(String currentStyleId) {
        this.currentStyleId = currentStyleId;
    }
    public void setCurrentStyleClass(String currentStyleClass) {
        this.currentStyleClass = currentStyleClass;
    }
    public String getDefaultTabParameters() {
        return defaultTabParameters;
    }

    public void setDefaultTabParameters(String defaultTabParameters) {
        this.defaultTabParameters = defaultTabParameters;
    }

    public String getDefaultTabBaseUrl() {
        return defaultTabBaseUrl;
    }

    public void setDefaultTabBaseUrl(String defaultTab) {
        this.defaultTabBaseUrl = defaultTab;
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

    public String getPanelStyleId() {
        return panelStyleId;
    }

    public void setPanelStyleId(String panelStyleId) {
        this.panelStyleId = panelStyleId;
    }

    public String getPanelStyleClass() {
        return panelStyleClass;
    }

    public void setPanelStyleClass(String panelStyleClass) {
        this.panelStyleClass = panelStyleClass;
    }

    public String getPostFunction() {
        return postFunction;
    }

    public void setPostFunction(String postFunction) {
        this.postFunction = postFunction;
    }


    public int doStartTag() throws JspException {
        // Required Properties
        this.panelStyleId = (String) ExpressionEvaluatorManager.evaluate("panelStyleId",
                this.panelStyleId, String.class, this, super.pageContext);
        this.contentStyleId = (String) ExpressionEvaluatorManager.evaluate("contentStyleId",
                this.contentStyleId, String.class, this, super.pageContext);
        this.currentStyleId = (String) ExpressionEvaluatorManager.evaluate("currentStyleId",
                this.currentStyleId, String.class, this, super.pageContext);

        // Optional Properties
        if (this.postFunction != null) {
            this.postFunction = (String) ExpressionEvaluatorManager.evaluate("postFunction",
                    this.postFunction, String.class, this, super.pageContext);
        }
        if (this.cache != null) {
            this.cache = (String)ExpressionEvaluatorManager.evaluate("cache",
                    this.cache, String.class, this, super.pageContext);
        }
        if (this.errorFunction != null) {
            this.errorFunction = (String) ExpressionEvaluatorManager.evaluate("errorFunction",
                    this.errorFunction, String.class, this, super.pageContext);
        }
        if (this.emptyFunction != null) {
            this.emptyFunction = (String) ExpressionEvaluatorManager.evaluate("emptyFunction",
                    this.emptyFunction, String.class, this, super.pageContext);
        }
        if (this.baseUrl != null) {
            this.baseUrl = (String) ExpressionEvaluatorManager.evaluate("baseUrl",
                    this.baseUrl, String.class, this, super.pageContext);
        }
        if (this.currentStyleClass != null) {
        this.currentStyleClass = (String) ExpressionEvaluatorManager.evaluate("currentStyleClass",
                this.currentStyleClass, String.class, this, super.pageContext);
        }
        StringBuffer script = new StringBuffer();
        script.append("<div class=\"");
        script.append(this.panelStyleClass);
        script.append("\" id=\"");
        script.append(this.panelStyleId);
        script.append("\">\n<ul>");
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
        options.add("source", "elem", false);
        options.add("target", this.contentStyleId, true);
        options.add("currentStyleId", this.currentStyleId, true);

        options.add("data", "params", false);
        if (this.postFunction != null)
            options.add("postFunction", this.postFunction, false);
        if (this.cache != null)
            options.add("cache", this.cache, false);
        if (this.emptyFunction != null)
            options.add("emptyFunction", this.emptyFunction, false);
        if (this.currentStyleClass != null)
        options.add("currentStyleClass", this.currentStyleClass, true);
        if (this.errorFunction != null)
            options.add("errorFunction", this.errorFunction, false);

        StringBuffer script = new StringBuffer();
        script.append("</ul>\n</div>\n<div style=\"display: none\" id=\"");
        script.append(this.contentStyleId);
        script.append("\"></div>\n");
        script.append("<script type=\"text/javascript\">\n");
        script.append("function loadAjaxTab");
        script.append(this.panelStyleId);
        script.append("(elem, url, params) {\n");
        if (this.cache != null && this.cache.equals("true")){
        script.append("if (!document.getElementById(\"");
        script.append(this.contentStyleId);
        script.append("\") || ");
        script.append("document.getElementById(\"");
        script.append(this.contentStyleId);
        script.append("\").innerHTML.length==0) {\n");
        }
        script.append(" document.getElementById(\"");
        script.append(this.contentStyleId);
        script.append("\").innerHTML=\'\';\n");
        script.append("$.ajax(url, {\n");
        script.append("success: function(data) { $('#").append(this.contentStyleId).append("').html(data);").append("}");
        script.append("});\n");
        if (this.cache.equals("true")){
        script.append("};\n");
        }
        script.append(" document.getElementById(\"");
        script.append(this.contentStyleId);
        script.append("\").style.display=\'block\';\n");
        script.append("}\n\n");

        script.append("function executeAjaxTab");
        script.append(this.panelStyleId);
        script.append("(elem, url, params) {\n");
        script.append(" if (elem.id!=null && elem.id!=\'\') {\n");
        script.append(" elem.id=\'\';\n");
        script.append(" elem.className=\'\';\n");
        script.append(" resetTabPanel(\"");
        script.append(this.panelStyleId);
        script.append("\");\n");
        script.append(" document.getElementById(\"");
        script.append(this.contentStyleId);
        script.append("\").style.display=\'none\';\n");
        script.append(" } else {\n");
        script.append(" loadAjaxTab");
        script.append(this.panelStyleId);
        script.append("(elem, url, params);\n");
        script.append(" elem.id=\'");
        script.append(this.currentStyleId);
        script.append("\';\n");
        script.append(" elem.className=\'");
        script.append(this.currentStyleClass);
        script.append("\';\n");

        script.append(" };\n");
        script.append("}\n\n");

        if (this.getDefaultTabBaseUrl() != null && this.getDefaultTabBaseUrl().length() > 0) {

            script.append("$(function(){loadAjaxTab");
            script.append(this.panelStyleId);
            script.append("($('");
            script.append(this.currentStyleId);
            script.append("'), '");
            script.append(this.getDefaultTabBaseUrl());
            script.append("', '");
            script.append(this.defaultTabParameters);
            script.append("');});\n");
            this.setDefaultTabBaseUrl(null);
        }

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
        this.panelStyleId = null;
        this.contentStyleId = null;
        this.defaultTabBaseUrl = null;
        this.defaultTabParameters = null;
        this.currentStyleId = null;
        this.currentStyleClass = null;
        this.postFunction = null;
        this.emptyFunction = null;
        this.errorFunction = null;
        this.baseUrl = null;
        this.cache = null;
        super.release();
    }

    public String getBaseUrl() {
        return baseUrl;
    }

    public void setBaseUrl(String baseUrl) {
        this.baseUrl = baseUrl;
    }

    public String getBase(){
        try{
        if (this.baseUrl!=null) return "&return="+ URLEncoder.encode(this.baseUrl, "UTF-8");
        } catch(UnsupportedEncodingException o){

        }
        return "";
    }
}
