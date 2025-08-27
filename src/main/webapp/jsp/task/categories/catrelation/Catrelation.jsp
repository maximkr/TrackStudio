<%@ page buffer="128kb" errorPage="/jsp/Error.jsp" %>
<%@ taglib uri="struts/html-el" prefix="html" %>
<%@ taglib uri="jstl/c" prefix="c" %>
<%@ taglib uri="jstl/fmt" prefix="I18n" %>
<%@ taglib uri="struts/tiles-el" prefix="tiles" %>
<%@ taglib uri="http://trackstudio.com" prefix="ts" %>

<I18n:setLocale value="${sc.locale}"/>
<I18n:setTimeZone value="${sc.timezone}"/>
<I18n:setBundle basename="language"/>
<tiles:insert page="/jsp/layout/ListLayout.jsp" flush="true">
    <tiles:put name="header" value="/jsp/task/TaskHeader.jsp"/>
    <tiles:put name="customHeader" value="/jsp/task/categories/CategoryHeader.jsp"/>
    <tiles:put name="tabs" value="/jsp/task/categories/CategorySubMenu.jsp"/>
    <tiles:put name="main" type="string">
                <c:set var="urlHtml" value="html"/>
        <ts:js request="${request}" response="${response}">
            <ts:jsLink link="${urlHtml}/filtersort.js"/>
        </ts:js>
        <c:if test="${!isValidParentCategory}">
            <div class="indent">
            <div class="general">
                <table class="error" cellpadding="0" cellspacing="0">
                    <caption>
                        <I18n:message key="CATEGORY_INVALID_RELATION_OVERVIEW"/>
                    </caption>
                    <tr class="line<c:out value="${counter mod 2}"/>">
                        <td>
                            <I18n:message key="CATEGORY_INVALID_HASNT_PARENT"/>
                        </td>
                    </tr>
                </table>
            </div>
            </div>
        </c:if>

        <div class="nblueborder">
            <div class="ncaption"></div>
            <div class="indent">
                <html:form action="/CategoryRelationAction" method="post">
                    <html:hidden property="method" value="save"/>
                    <html:hidden property="id" value="${id}"/>
                    <html:hidden property="session" value="${session}"/>
                    <html:hidden property="categoryId" value="${categoryId}"/>
                    <div class="blueborder">
                    <div class="caption"><I18n:message key="CATEGORY_RELATIONS"/></div>
                       <div class="indent">

                            <table class="allowdeny">

                                <tr>
                                    <th class="denied">
                                        <I18n:message key="NOT_RELATED_CATEGORIES"/>
                                    </th>
                                    <th></th>
                                    <th class="allowed">
                                        <I18n:message key="RELATED_CATEGORIES"/>
                                    </th>
                                </tr>
                                <tr>
                                    <td>
                                        <html:select property="from" multiple="true" size="10" styleClass="monospaced fixedwidth">
                                            <c:forEach items="${categoryList}" var="category">
                                                <html:option value="${category.id}">
                                                    <c:out value="${category.name}"/>
                                                </html:option>
                                            </c:forEach>
                                        </html:select>
                                    </td>
                                    <td>
                                        <input type="button" name="add" class="iconized"
                                               onclick="addSelectedItems(this.form, 'from', 'to'); return false;"
                                               value="&gt;"><br>
                                        <input type="button" name="remove" class="iconized"
                                               onclick="removeSelectedItems(this.form, 'from', 'to'); return false;"
                                               value="&lt;">
                                    </td>
                                    <td>
                                        <html:select property="to" multiple="true" size="10" styleClass="monospaced fixedwidth">
                                            <c:forEach items="${selectedCategories}" var="category">
                                                <html:option value="${category.id}">
                                                    <c:out value="${category.name}"/>
                                                </html:option>
                                            </c:forEach>
                                        </html:select>
                                    </td>
                                </tr>
                            </table>
                            <input type="hidden" name="hiddento" value="${categoryForm.hiddento}">
                        </div>
                    </div>
                    <c:if test="${canEdit}">
                        <div class="controls">
                            <input type="submit" class="iconized"
                                   value="<I18n:message key="SAVE"/>"
                                   name="DELETE">
                        </div>
                    </c:if>
                </html:form>
            </div>
        </div>
    </tiles:put>
</tiles:insert>
