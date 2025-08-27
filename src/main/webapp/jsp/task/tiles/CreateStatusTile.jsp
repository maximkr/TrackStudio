<%@ page buffer="128kb" errorPage="/jsp/Error.jsp" %>
<%@ taglib uri="jstl/c" prefix="c" %>
<%@ taglib uri="jstl/fmt" prefix="I18n" %>
<%@ taglib uri="struts/html-el" prefix="html" %>
<I18n:setLocale value="${sc.locale}"/>
<I18n:setTimeZone value="${sc.timezone}"/>
<I18n:setBundle basename="language"/>

<c:if test="${_can_create}">
    <div class="yellowbox" id="<c:out value="${param.tileId}"/>" style="display: none">
        <div class="general">
            <html:form method="POST" action="/TaskStatusAction.do" onsubmit="return validate(this);">
                <html:hidden property="session" value="${session}"/>
                <html:hidden property="method" value="create"/>
                <html:hidden property="id" value="${id}"/>
                <html:hidden property="workflowId" value="${flow.id}"/>
                <table class="general" cellpadding="0" cellspacing="0">
                    <colgroup>
                        <col class="col_1">
                        <col class="col_2">
                    </colgroup>
                    <caption>
                        <I18n:message key="STATE_ADD"/>
                    </caption>
                    <tr>
                        <th>
                            <I18n:message key="NAME"/>
                            *
                        </th>
                        <td>
                            <input type="text" name="name" size=20 maxlength='200' alt=">0">
                        </td>
                    </tr>
                    <tr>
                        <th>
                            <I18n:message key="COLOR"/>
                        </th>
                        <td><span
                                style="float: left; background-color: #FFFFFF; width: 20px; height: 20px; border: #5e5e4c 1px solid; margin-left: 12px; margin-right: 12px"></span><input
                                onChange="return changeColorSample(this);" alt="color" type="text"
                                value="<c:out value="${defaultColor}"/>" name="color" id="STATE_COLOR">&nbsp;
                            <!--html:img src="${contextPath}${ImageServlet}/cssimages/ico.colortable.gif" border="0" style="cursor:pointer;" onclick="javascript:TCP.popup('${contextPath}',document.getElementById('STATE_COLOR'), 1); return false;" width="17" height="17"/-->
                            <html:img onclick="if (top.udfWin!=null) {
                            top.udfWin.close();
                            top.udfWin=null;
                         }
                         top.udfWin = window.open('${contextPath}/html/colorpicker/colorPicker.htm', 'udfWin', 'dependent=yes,menubar=no,toolbar=no,status=no,scrollbars=no,titlebar=no,left=370,top=245,width=362,height=245,resizable=yes');
                         is_new=true;top.udfWin.opener = window;top.udfWin.statusId='STATE_COLOR'"
                                      src="${contextPath}${ImageServlet}/cssimages/ico.colortable.gif" border="0"
                                      styleClass="calendaricon" width="17" height="17"/>
                        </td>
                    </tr>
                    <tr>
                        <th>
                            <I18n:message key="START"/>
                        </th>
                        <td><input id='radio' class="checkbox" type="checkbox" name="start" value="new"></td>
                    </tr>
                    <tr>
                        <th>
                            <I18n:message key="FINAL"/>
                        </th>
                        <td><input class="checkbox" type="checkbox" name="finish" value="new">
                        </td>
                    </tr>

                </table>

                <div class="controls">
                    <input type="submit" class="iconized" value="<I18n:message key="STATE_ADD"/>" name="NEW">
                </div>
            </html:form>
        </div>
    </div>
</c:if>
