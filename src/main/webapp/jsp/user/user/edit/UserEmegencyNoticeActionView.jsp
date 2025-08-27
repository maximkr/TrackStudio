<%@ page buffer="128kb" errorPage="/jsp/Error.jsp" %>
<%@ taglib uri="struts/html-el" prefix="html" %>
<%@ taglib uri="jstl/c" prefix="c" %>
<%@ taglib uri="jstl/fmt" prefix="I18n" %>
<%@ taglib uri="struts/tiles-el" prefix="tiles" %>
<c:set var="taskMenu" value="false"/>
<c:set var="userMenu" value="true"/>
<I18n:setLocale value="${sc.locale}"/>
<I18n:setTimeZone value="${sc.timezone}"/>
<I18n:setBundle basename="language"/>
<tiles:insert page="/jsp/layout/ListLayout.jsp" flush="true">
    <tiles:put name="header" value="/jsp/user/UserHeader.jsp"/>
    <tiles:put name="tabs" type="string"/>
    <tiles:put name="customHeader" type="string"/>
    <tiles:put name="main" type="string">
        <div class="blueborder">
            <div class="caption">
                <I18n:message key="USER_PROPERTIES"/>
            </div>
            <div class="indent">
                <html:form action="/UserEmegencyNoticeAction" method="post" enctype="multipart/form-data" styleId="checkunload">
                <html:hidden property="id"/>
                <html:hidden property="session"/>
                <html:hidden property="method"/>
                <div class="general">
                    <table class="general" cellpadding="0" cellspacing="0">
                        <colgroup>
                            <col class="col_1">
                        </colgroup>
                        <tr>
                            <th>
                                <label><I18n:message key="EMERGENCY_NOTICE"/></label>
                            </th>
                            <td>
                                <html:textarea property="emergencyNotice" cols="70" rows="10"/>
                            </td>
                        </tr>
                    </table>
                    <div class="controls">
                        <html:submit styleClass="iconized" property="saveButton">
                            <I18n:message key="SAVE"/>
                        </html:submit>
                        <html:button styleClass="iconized secondary" property="cancelButton" onclick="document.location='${referer}';">
                            <I18n:message key="CANCEL"/>
                        </html:button>
                    </div>
                    </html:form>
                </div>
            </div>
        </div>
    </tiles:put>
</tiles:insert>
