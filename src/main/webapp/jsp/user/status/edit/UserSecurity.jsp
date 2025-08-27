<%@ taglib uri="struts/html-el" prefix="html" %>
<%@ taglib uri="jstl/c" prefix="c" %>
<%@ taglib uri="jstl/fmt" prefix="I18n" %>
<%@ taglib uri="struts/tiles-el" prefix="tiles" %>
<c:set var="taskMenu" value="false"/>
<c:set var="userMenu" value="true"/>
<I18n:setLocale value='${sc.locale}'/>
<I18n:setTimeZone value='${sc.timezone}'/>
<I18n:setBundle basename='language'/>
<tiles:insert page="/jsp/layout/ListLayout.jsp" flush="true">
    <tiles:put name="customHeader" value="/jsp/user/status/StatusHeader.jsp"/>
    <tiles:put name="header" value="/jsp/user/UserHeader.jsp"/>
    <tiles:put name="tabs" value="/jsp/user/status/StatusSubMenu.jsp"/>
    <tiles:put name="main" type="string">
        <c:if test="${canView}">
            <div class="nblueborder">
                <div class="ncaption"></div>
                <html:form method="POST" action="/UserSecurityAction" styleId="checkunload" onsubmit="return validate(this);">
                    <html:hidden property="method" value="save"/>
                    <html:hidden property="session" value="${session}"/>
                    <html:hidden property="prstatusId" value="${prstatusId}"/>
                    <html:hidden property="id" value="${id}"/>
                    <div class="indent">
                        <div class="general">
                            <table class="general" cellpadding="0" cellspacing="0">
                                <COLGROUP>
                                    <COL class="col_1">
                                    <COL class="col_2">
                                </COLGROUP>
                                <caption>
                                    <I18n:message key="PRSTATUS_USER_ACTIONS_PERMISSIONS"/>
                                </caption>
                                <tr class="security-tr-border">
                                    <th><I18n:message key="USERS"/></th>
                                    <td>
                                        <div>
                                            <div class="security-tree-item" parent="0" nochilds="false" id="parentDivId1">
                                                <html:checkbox property="editUserHimself" styleId="1" onclick="treeset(this);"/><label for="1"><I18n:message key="Action.editUserHimself"/></label>
                                            </div>
                                            <div class="security-tree-item" parent="1" nochilds="false" id="parentDivId2">
                                                <img src="${contextPath}${ImageServlet}/cssimages/L.png">
                                                <html:checkbox property="editUserChildren" styleId="2" onclick="treeset(this);" styleClass="child"/><label for="2"><I18n:message key="Action.editUserChildren"/></label>
                                            </div>
                                            <div class="security-tree-item" parent="2" nochilds="false" id="parentDivId3">
                                                <img src="${contextPath}${ImageServlet}/cssimages/blank.png">
                                                <img src="${contextPath}${ImageServlet}/cssimages/L.png">
                                                <html:checkbox property="createUser" styleId="3" onclick="treeset(this);" styleClass="child"/><label for="3"><I18n:message key="Action.createUser"/></label>
                                            </div>
                                            <div class="security-tree-item" parent="3" nochilds="true" id="parentDivId4">
                                                <img src="${contextPath}${ImageServlet}/cssimages/blank.png">
                                                <img src="${contextPath}${ImageServlet}/cssimages/blank.png">
                                                <img src="${contextPath}${ImageServlet}/cssimages/L.png">
                                                <html:checkbox property="deleteUser" styleId="4" onclick="treeset(this);" styleClass="child"/><label for="4"><I18n:message key="Action.deleteUser"/></label>
                                            </div>
                                        </div>
                                        <div>
                                            <html:checkbox property="cutPasteUser" styleId="cutPasteUser"/><label for="cutPasteUser"><I18n:message key="Action.cutPasteUser"/></label>
                                        </div>
                                        <div>
                                            <div class="security-tree-item" parent="0" nochilds="false" id="parentDivId5">
                                                <html:checkbox property="editUserPasswordHimself" styleId="5" onclick="treeset(this);"/><label for="5"><I18n:message key="Action.editUserPasswordHimself"/></label>
                                            </div>
                                            <div class="security-tree-item" parent="5" nochilds="false" id="parentDivId6">
                                                <img src="${contextPath}${ImageServlet}/cssimages/L.png">
                                                <html:checkbox property="editUserChildrenPassword" styleId="6" onclick="treeset(this);" styleClass="child"/><label for="6"><I18n:message key="Action.editUserChildrenPassword"/></label>
                                            </div>
                                        </div>
                                    </td>
                                </tr>
                                <tr class="security-tr-border">
                                    <th><I18n:message key="FILTERS"/></th>
                                    <td>
                                        <div>
                                            <div class="security-tree-item" parent="0" nochilds="false" id="parentDivId10">
                                                <html:checkbox property="viewUserFilters" styleId="10" onclick="treeset(this);"/><label for="10"><I18n:message key="Action.viewUserFilters"/></label>
                                            </div>
                                            <div class="security-tree-item" parent="10" nochilds="false" id="parentDivId11">
                                                <img src="${contextPath}${ImageServlet}/cssimages/L.png">
                                                <html:checkbox property="manageUserPrivateFilters" styleId="11" onclick="treeset(this);" styleClass="child"/><label for="11"><I18n:message key="Action.manageUserPrivateFilters"/></label>
                                            </div>
                                            <div class="security-tree-item" parent="11" nochilds="true" id="parentDivId12">
                                                <img src="${contextPath}${ImageServlet}/cssimages/blank.png">
                                                <img src="${contextPath}${ImageServlet}/cssimages/L.png">
                                                <html:checkbox property="manageUserPublicFilters" styleId="12" onclick="treeset(this);" styleClass="child"/><label for="12"><I18n:message key="Action.manageUserPublicFilters"/></label>
                                            </div>
                                        </div>
                                    </td>
                                </tr>
                                <tr class="security-tr-border">
                                    <th><I18n:message key="ATTACHMENTS"/></th>
                                    <td>
                                        <div>
                                            <div class="security-tree-item" parent="0" nochilds="false" id="parentDivId7">
                                                <html:checkbox property="viewUserAttachments" styleId="7" onclick="treeset(this);"/><label for="7"><I18n:message key="Action.viewUserAttachments"/></label>
                                            </div>
                                            <div class="security-tree-item" parent="7" nochilds="false" id="parentDivId8">
                                                <img src="${contextPath}${ImageServlet}/cssimages/L.png">
                                                <html:checkbox property="createUserAttachments" styleId="8" onclick="treeset(this);" styleClass="child"/><label for="8"><I18n:message key="Action.createUserAttachments"/></label>
                                            </div>
                                            <div class="security-tree-item" parent="8" nochilds="true" id="parentDivId9">
                                                <img src="${contextPath}${ImageServlet}/cssimages/blank.png">
                                                <img src="${contextPath}${ImageServlet}/cssimages/L.png">
                                                <html:checkbox property="manageUserAttachments" styleId="9" onclick="treeset(this);" styleClass="child"/><label for="9"><I18n:message key="Action.manageUserAttachments"/></label>
                                            </div>
                                        </div>
                                    </td>
                                </tr>
                                <tr class="security-tr-border">
                                    <th><I18n:message key="ACL"/></th>
                                    <td>
                                        <div>
                                            <html:checkbox property="manageUserACLs" styleId="manageUserACLs"/><label for="manageUserACLs"><I18n:message key="Action.manageUserACLs"/></label>
                                        </div>
                                    </td>
                                </tr>
                                <tr class="security-tr-border">
                                    <th><I18n:message key="CUSTOM_FIELDS"/></th>
                                    <td>
                                        <div>
                                            <html:checkbox property="manageUserUDFs" styleId="manageUserUDFs"/><label for="manageUserUDFs"><I18n:message key="Action.manageUserUDFs"/></label>
                                        </div>
                                    </td>
                                </tr>
                                <tr class="security-tr-border">
                                    <th><I18n:message key="PRSTATUSES_LIST"/></th>
                                    <td>
                                        <div>
                                            <html:checkbox property="manageRoles" styleId="manageRoles"/><label for="manageRoles"><I18n:message key="Action.manageRoles"/></label>
                                        </div>
                                    </td>
                                </tr>
                            </table>
                        </div>
                        <c:if test="${canEdit}">
                            <div class="controls">
                                <input type="SUBMIT" class="iconized" value="<I18n:message key="SAVE"/>" name="SAVE">
                            </div>
                        </c:if>
                    </div>
                </html:form>
            </div>
        </c:if>
    </tiles:put>
</tiles:insert>
