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
package org.ajaxtags.servlets;

import java.io.PrintWriter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.Action;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import com.trackstudio.startup.Config;

/**
 * TODO Document type BaseAjaxAction.
 * 
 * @author Darren Spurgeon
 * @version $Revision: 1.1 $ - $Date: 2005/09/15 20:11:42 $
 */
public abstract class BaseAjaxAction extends Action {

  public final ActionForward execute(
      ActionMapping mapping, ActionForm form, HttpServletRequest request,
      HttpServletResponse response) throws Exception {

    String xml = null;

    try {
      xml = getXmlContent(mapping, form, request, response);
    } catch (Exception ex) {
      // Send back a 500 error code.
      response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Can not create response");
      return null;
    }

    // Set content to xml
    response.setContentType("text/xml; charset="+ Config.getEncoding());
    response.setHeader("Cache-Control", "public");
    response.setHeader("Pragma", "no-cache");
    response.setDateHeader("Expires", 0L); //prevents caching at the proxy server
    PrintWriter pw = response.getWriter();
    pw.write(xml);
    pw.close();

    return null;
  }

  /**
   * Each child class should override this method to generate the specific XML content necessary for
   * each AJAX action.
   * 
   * @param mapping
   * @param form


   
   * @throws Exception
   */
  public abstract String getXmlContent(
      ActionMapping mapping, ActionForm form, HttpServletRequest request,
      HttpServletResponse response) throws Exception;

}
