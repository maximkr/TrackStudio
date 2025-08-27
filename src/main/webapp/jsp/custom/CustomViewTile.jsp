<%@ taglib uri="struts/html-el" prefix="html" %>
<%@ taglib uri="jstl/c" prefix="c" %>
<%@ taglib uri="jstl/fmt" prefix="I18n" %>
<I18n:setLocale value="${sc.locale}"/>
<I18n:setTimeZone value="${sc.timezone}"/>
<I18n:setBundle basename="language"/>
<div class="general">
    <table class="general" cellpadding="0" cellspacing="0">
        <caption>
            <I18n:message key="PROPERTIES"/>
        </caption>
        <colgroup>
            <col class="col_1">
            <col class="col_2">
        </colgroup>
        <tr>
            <th>
                <I18n:message key="CAPTION"/>
            </th>
            <td>
                <c:out value="${udf.caption}"/>
            </td>
        </tr>
        <c:if test="${udf.referencedbycaption ne null && type eq user}">
            <tr>
                <th title="<I18n:message key="BACK_REFERENCE_CAPTION_COMMENT"/>">
                    <I18n:message key="BACK_REFERENCE_CAPTION"/>
                </th>
                <td>
                    <c:out value="${udf.referencedbycaption}"/>
                </td>
            </tr>
        </c:if>
        <tr>
            <th>
                <I18n:message key="TYPE"/>
            </th>
            <c:choose>
                <c:when test="${type eq string}">
                    <td>
                        <I18n:message key="UDF_STRING"/>
                    </td>
                </c:when>
                <c:when test="${type eq 1}">
                    <td>
                        <I18n:message key="UDF_FLOAT"/>
                    </td>
                </c:when>
                <c:when test="${type eq integer}">
                    <td>
                        <I18n:message key="UDF_INTEGER"/>
                    </td>
                </c:when>
                <c:when test="${type eq date}">
                    <td>
                        <I18n:message key="UDF_DATE"/>
                    </td>
                </c:when>
                <c:when test="${type eq url}">
                    <td>
                        <I18n:message key="UDF_URL"/>
                    </td>
                </c:when>
                <c:when test="${type eq memo}">
                    <td>
                        <I18n:message key="UDF_MEMO"/>
                    </td>
                </c:when>
                <c:when test="${type eq list}">
                    <td>
                        <I18n:message key="UDF_LIST"/>
                    </td>
                </c:when>
                <c:when test="${type eq multilist}">
                    <td>
                        <I18n:message key="UDF_MULTILIST"/>
                    </td>
                </c:when>
                <c:when test="${type eq task}">
                    <td>
                        <I18n:message key="UDF_TASK"/>
                    </td>
                </c:when>
                <c:when test="${type eq user}">
                    <td>
                        <I18n:message key="UDF_USER"/>
                    </td>
                </c:when>
            </c:choose>
        </tr>
        <tr>
            <th>
                <I18n:message key="ORDER"/>
            </th>
            <td>
                <c:out value="${udf.order}"/>
            </td>
        </tr>
        <tr>
            <th>
                <I18n:message key="REQUIRED"/>
            </th>
            <td>
                <c:choose>
                    <c:when test="${udf.required}">
                        <html:img alt="" src="${contextPath}${ImageServlet}/cssimages/ico.checked.gif"/>
                    </c:when>
                    <c:otherwise>
                        <html:img alt="" src="${contextPath}${ImageServlet}/cssimages/ico.unchecked.gif"/>
                    </c:otherwise>
                </c:choose>
            </td>
        </tr>

        <c:if test="${(type eq string) || (type eq memo)}">
            <tr>
                <th>
                    <I18n:message key="HTML_VIEW"/>
                </th>
                <td>
                    <c:choose>
                        <c:when test="${udf.htmlview}">
                            <html:img alt="" src="${contextPath}${ImageServlet}/cssimages/ico.checked.gif"/>
                        </c:when>
                        <c:otherwise>
                            <html:img alt="" src="${contextPath}${ImageServlet}/cssimages/ico.unchecked.gif"/>
                        </c:otherwise>
                    </c:choose>
                </td>
            </tr>
        </c:if>

        <c:if test="${owner ne null}">
            <th>
                <I18n:message key="OWNER"/>
            </th>
            <td>
                <c:out value="${owner}" escapeXml='true'/>
            </td>
        </c:if>
        <c:if test="${udf.defaultUDF ne null && udf.defaultUDF != ''}">
            <c:if test="${type eq string || type eq integer || type eq date || type eq url || type eq memo}">
                <tr>
                    <th>
                        <I18n:message key="DEFAULT"/>
                    </th>
                    <td>
                        <c:out value="${udf.defaultUDF}" escapeXml="!${udf.htmlview}"/>
                    </td>
                </tr>
            </c:if>
            <c:if test="${type eq 1}">
                <tr>
                    <th>
                        <I18n:message key="DEFAULT"/>
                    </th>
                    <td>
                        <I18n:formatNumber value="${udf.defaultUDF}" groupingUsed="true" maxFractionDigits="${decimalFormatUdfFloat}"/>
                    </td>
                </tr>
            </c:if>
            <c:if test="${type eq list || type eq multilist}">
                <tr>
                    <th>
                        <I18n:message key="DEFAULT"/>
                    </th>
                    <td>
                        <c:out value="${defaultList}"/>
                    </td>
                </tr>
            </c:if>
        </c:if>
        <c:if test="${udf.initial ne null}">
            <tr>
                <th>
                    <I18n:message key="INITIAL"/>
                </th>
                <td>
                    <c:if test="${type eq task}">
                        <html:link styleClass="internal" href="${contextPath}/task/${tuval.number}?thisframe=true">
                            <c:if test="${tuval.category.icon ne null}">
                                <html:img  styleClass="icon" border="0" src="${contextPath}${ImageServlet}/icons/categories/${tuval.category.icon}"/>
                            </c:if>
                            <c:out value="${tuval.name}" escapeXml="true"/>
                        </html:link>
                    </c:if>
                    <c:if test="${type eq user}">
                        <html:link styleClass="internal" href="${contextPath}/user/${tuval.login}?thisframe=true" title="${tuval.login}">
                            <html:img  styleClass="icon" border="0" src="${contextPath}${ImageServlet}/cssimages/${tuval.active ? 'arw.usr.a.gif' : 'arw.usr.gif'}"/>
                            <c:out value="${tuval.name}" escapeXml="true"/>
                        </html:link>
                    </c:if>

                </td>
            </tr>
        </c:if>
    </table>
</div>

<c:if test="${udf.script ne null}">
    <div class="general">
        <table class="general" cellpadding="0" cellspacing="0">
            <caption>
                <I18n:message key="CALCULATED_SETTINGS"/>
            </caption>
            <colgroup>
                <col class="col_1">
                <col class="col_2">
            </colgroup>
            <tr>
                <th>
                    <I18n:message key="SCRIPT"/>
                </th>
                <td>
                    <c:out value="${udf.script}" escapeXml='true'/>
                </td>
            </tr>
            <tr>
                <th>
                    <I18n:message key="CACHE_VALUES"/>
                </th>
                <td>
                    <c:choose>
                        <c:when test="${udf.cachevalues}">
                            <html:img src="${contextPath}${ImageServlet}/cssimages/ico.checked.gif"/>
                        </c:when>
                        <c:otherwise>
                            <html:img src="${contextPath}${ImageServlet}/cssimages/ico.unchecked.gif"/>
                        </c:otherwise>
                    </c:choose>
                </td>
            </tr>
        </table>
    </div>
</c:if>

<c:if test="${udf.lookupscript ne null}">
    <div class="general">
        <table class="general" cellpadding="0" cellspacing="0">
            <caption>
                <I18n:message key="LOOKUP_SETTINGS"/>
            </caption>
            <colgroup>
                <col class="col_1">
                <col class="col_2">
            </colgroup>
            <tr>
                <th>
                    <I18n:message key="LOOKUP_SCRIPT"/>
                </th>
                <td>
                    <c:out value="${udf.lookupscript}" escapeXml='true'/>
                </td>
            </tr>
            <tr>
                <th>
                    <I18n:message key="LOOKUP_ONLY"/>
                </th>
                <td>
                    <c:choose>
                        <c:when test="${udf.lookuponly}">
                            <html:img src="${contextPath}${ImageServlet}/cssimages/ico.checked.gif"/>
                        </c:when>
                        <c:otherwise>
                            <html:img src="${contextPath}${ImageServlet}/cssimages/ico.unchecked.gif"/>
                        </c:otherwise>
                    </c:choose>
                </td>
            </tr>
        </table>
    </div>
</c:if>

<c:if test="${(type eq list || type eq multilist) && !empty udflist}">
    <div class="general">
        <table class="general" cellpadding="0" cellspacing="0">
            <caption>
                <I18n:message key="LIST_VALUES"/>
            </caption>
            <tr>
                <td>
                    <ul>
                        <c:forEach var="ul" items="${udflist}">
                            <li>
                                <c:out value="${ul.value}" escapeXml='true'/>
                            </li>
                        </c:forEach>
                    </ul>
                </td>
            </tr>
        </table>
    </div>
</c:if>
<c:if test="${viewPermission}">
    <div class="general">
        <table class="general" cellpadding="0" cellspacing="0">
            <caption>
                <I18n:message key="PERMISSIONS_VIEW"/>
            </caption>
            <colgroup>
                <col class="col_1">
                <col class="col_2">
            </colgroup>
            <tr>
                <th>
                    <I18n:message key="CAN_VIEW"/>
                </th>
                <td>
                    <c:if test="${!empty viewAll}">
                        <c:forEach items="${viewAll}" var="v" varStatus="c">
                            <span style="white-space: nowrap;"><img src="${contextPath}${ImageServlet}/cssimages/ico.status.gif"
                                                                    alt=""><c:out value="${v.name}"/><c:if
                                    test="${!c.last}">,
                            </c:if></span>
                        </c:forEach>

                    </c:if>
                    <c:if test="${!empty viewHandler}">
                        <c:forEach items="${viewHandler}" var="v" varStatus="c">
                            <span style="white-space: nowrap;"><img src="${contextPath}${ImageServlet}/cssimages/ico.status.gif"
                                                                    alt=""><c:out value="${v.name}"/> (<I18n:message
                                    key="HANDLER_ONLY"/>)<c:if test="${!c.last}">,</c:if></span>
                        </c:forEach>

                    </c:if>
                    <c:if test="${!empty viewSubmitter}">
                        <c:forEach items="${viewSubmitter}" var="v" varStatus="c">
                            <span style="white-space: nowrap;"><img src="${contextPath}${ImageServlet}/cssimages/ico.status.gif"
                                                                    alt=""><c:out value="${v.name}"/> (<I18n:message
                                    key="SUBMITTER_ONLY"/>)<c:if test="${!c.last}">,</c:if></span>
                        </c:forEach>

                    </c:if>
                    <c:if test="${!empty viewSubAndHandler}">
                        <c:forEach items="${viewSubAndHandler}" var="v" varStatus="c">
                            <span style="white-space: nowrap;"><img src="${contextPath}${ImageServlet}/cssimages/ico.status.gif"
                                                                    alt=""><c:out value="${v.name}"/> (<I18n:message
                                    key="SUBMITTER_OR_HANDLER"/>)<c:if test="${!c.last}">,</c:if></span>
                        </c:forEach>

                    </c:if>
                </td>
            </tr>
            <tr>
                <th>
                    <I18n:message key="CAN_EDIT"/>
                </th>
                <td>
                    <c:if test="${!empty editAll}">
                        <c:forEach items="${editAll}" var="v" varStatus="c">
                            <span style="white-space: nowrap;"><img src="${contextPath}${ImageServlet}/cssimages/ico.status.gif"
                                                                    alt=""><c:out value="${v.name}"/><c:if
                                    test="${!c.last}">,
                            </c:if></span>
                        </c:forEach>

                    </c:if>
                    <c:if test="${!empty editHandler}">
                        <c:forEach items="${editHandler}" var="v" varStatus="c">
                            <span style="white-space: nowrap;"><img src="${contextPath}${ImageServlet}/cssimages/ico.status.gif"
                                                                    alt=""><c:out value="${v.name}"/> (<I18n:message
                                    key="HANDLER_ONLY"/>)<c:if test="${!c.last}">,</c:if></span>
                        </c:forEach>

                    </c:if>
                    <c:if test="${!empty editSubmitter}">
                        <c:forEach items="${editSubmitter}" var="v" varStatus="c">
                            <span style="white-space: nowrap;"><img src="${contextPath}${ImageServlet}/cssimages/ico.status.gif"
                                                                    alt=""><c:out value="${v.name}"/> (<I18n:message
                                    key="SUBMITTER_ONLY"/>)<c:if test="${!c.last}">,</c:if></span>
                        </c:forEach>

                    </c:if>
                    <c:if test="${!empty editSubAndHandler}">
                        <c:forEach items="${editSubAndHandler}" var="v" varStatus="c">
                            <span style="white-space: nowrap;"><img src="${contextPath}${ImageServlet}/cssimages/ico.status.gif"
                                                                    alt=""><c:out value="${v.name}"/> (<I18n:message
                                    key="SUBMITTER_OR_HANDLER"/>)<c:if test="${!c.last}">,</c:if></span>
                        </c:forEach>

                    </c:if>
                </td>
            </tr>
        </table>
    </div>
</c:if>
