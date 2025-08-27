<%@ page buffer="128kb" errorPage="/jsp/Error.jsp"%>
<%@ taglib uri="struts/html-el" prefix="html" %>
<%@ taglib uri="jstl/c" prefix="c" %>
<%@ taglib uri="jstl/fmt" prefix="I18n" %>
<%@ taglib uri="struts/tiles-el" prefix="tiles" %>


<I18n:setLocale value="${sc.locale}"/>
<I18n:setTimeZone value="${sc.timezone}"/>
<I18n:setBundle basename="language"/>
<tiles:insert page="/jsp/layout/ListLayout.jsp" flush="true">

<tiles:put name="header" value="/jsp/user/UserHeader.jsp" />
<tiles:put name="customHeader" type="string" />
    <tiles:put name="tabs" type="string"/>
    
<tiles:put name="main" type="string">

<div class="blueborder">
<div class="caption"><I18n:message key="FILE_ADD"/></div>
<div class="indent">
    <c:if test="${canCreateUserAttachments}">
<html:form method="post" enctype="multipart/form-data" action="/AttachmentEditAction" onsubmit="return validate(this);">
<html:hidden property="method" value="createUserAttachment"/>
<html:hidden property="id"/>
<html:hidden property="session"/>
<c:import url="/jsp/attachments/AttachmentCreateTile.jsp"/>
                <div class="controls">
                <input type="submit"  class="iconized" value="<I18n:message key="UPLOAD"/>" name="UPLOAD">
                    <html:button styleClass="iconized secondary" property="cancelButton"
        onclick="document.location='${contextPath}/UserViewAction.do?method=page&amp;id=${id}';">
        <I18n:message key="CANCEL"/>
        </html:button>
    </div>
</html:form>
</c:if>
    </div>
    </div>
</tiles:put>
</tiles:insert>
