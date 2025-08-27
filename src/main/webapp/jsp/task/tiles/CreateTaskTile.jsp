<%@ page buffer="128kb" errorPage="/jsp/Error.jsp"%>
<%@ taglib uri="jstl/c" prefix="c" %>
<%@ taglib uri="jstl/fmt" prefix="I18n" %>
<%@ taglib uri="struts/html-el" prefix="html" %>
<I18n:setLocale value="${sc.locale}"/>
<I18n:setTimeZone value='${sc.timezone}'/>
<I18n:setBundle basename="language"/>

<c:if test="${!empty categorySet}">
       <div  class="general">
       <c:choose>
           <c:when test="${empty categorySet}">
           <div class="general">
    <table class="general" cellpadding="0" cellspacing="0">
                    <tr>
                        <td><I18n:message key="NO_AVAIL_CATEGORY_FOR_TASK_CREATION"/></td>
                    </tr>
                </table>
            </div>               
           </c:when>
           <c:otherwise>
                <c:if test="${showHelp}">
                <div class="helpcontainer">
                   <div class="helptopic">
                       <I18n:message key="HELP_TOPIC"/>
                   </div>

                   <div class="help">
                       <I18n:message key="HELP_TILE_ADD_NEW_TASK"/>
                   </div>
               </div>
               </c:if>

               <html:form method="POST" action="/TaskCreateAction.do" onsubmit="return validate(this);">
                   <html:hidden property="session" value="${session}"/>
                   <html:hidden property="method" value="create"/>
                   <html:hidden property="id" value="${id}"/>
                   <html:hidden property="newTask" value="true"/>

    <table class="general" cellpadding="0" cellspacing="0">
                       <COLGROUP>
                           <COL class="col_1">
                           <COL class="col_2">
                       </COLGROUP>
                   <caption><I18n:message key="TASK_CREATION"/></caption>
                       <tr>
                           <th>
                               <I18n:message key="CATEGORY"/>
                           </th>
                           <td>
                               <select name="category" alt="mustChoose(<I18n:message key="CHOOSE_CATEGORY"/>)">
                                   <option selected value="<I18n:message key="NOT_CHOOSEN"/>"><I18n:message key="CHOOSE_ONE"/></option>
                                   <c:forEach var="category" items="${categorySet}">
                                       <option value="<c:out value="${category.id}"/>"><c:out value="${category.name}" escapeXml="true"/></option>
                                   </c:forEach>
                               </select>
                           </td>
                       </tr>
                       <tr>
                           <th><I18n:message key="TASK_NAME"/></th>
                           <td><html:text property="name" size="60" maxlength='200' alt=">0"/></td>
                       </tr>
                   </table>

                   <div class="controls">
                       <input type="submit"  class="iconized" value="<I18n:message key="CREATE"/>" name="NEW">
                   </div>
               </html:form>
           </c:otherwise>
       </c:choose>
       </div>
       </c:if>
