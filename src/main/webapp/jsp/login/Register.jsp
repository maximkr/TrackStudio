<%@ taglib uri="struts/html-el" prefix="html" %>
<%@ taglib uri="jstl/c" prefix="c" %>
<% if (request.getAttribute("title") == null) {%>
<c:redirect url="${request.contextPath}/LoginAction.do?method=registerPage"/>
<% }%>
<%@ taglib uri="struts/tiles-el" prefix="tiles" %>
<%@ taglib uri="jstl/fmt" prefix="I18n" %>
<%@ taglib uri="http://trackstudio.com" prefix="ts" %>
<I18n:setLocale value="${defaultLocale}"/>
<I18n:setTimeZone value="${sc.timezone}"/>
<I18n:setBundle basename="language"/>
<tiles:insert page="/jsp/layout/LoginLayout.jsp" flush="true">
    <tiles:put name="topic" value="${helpTopic}"/>
    <tiles:put name="content" value="${helpContent}"/>
    <tiles:put name="form" type="string">


        <script type="text/javascript">
            var contextPath = "<c:out value="${contextPath}"/>";
            var versionPath = '<c:out value="${versionPath}"/>';

            var MSG_NOT_CHOOSEN = "<I18n:message key="NOT_CHOOSEN"/>";
        </script>

        <html:form method="POST" action="/LoginAction" onsubmit="return validate(this);">
            <html:hidden property="method" value="register"/>
            <table class="login">
                <colgroup>
                    <col class="col_1">
                    <col class="col_2">
                </colgroup>
                <caption>
                    <I18n:message key="REGISTRATION_INFO"/>
                </caption>
                <tr>
                    <th><label for="registration"><I18n:message key="REGISTER_AS"/></label></th>
                    <td>
                        <c:choose>
                            <c:when test="${prj ne null}">
                                <html:hidden property="registration" value="${prj.rightPart}"/>
                                <html:hidden property="project" value="${prj.rightPart}"/>
                                <c:out value="${prj.leftPart}"/>
                            </c:when>
                            <c:otherwise>

                                <html:select property="registration" styleId="registration" alt="mustBeChoose">
                                    <html:option value="NotChoosen"><I18n:message key="CHOOSE_ONE"/></html:option>
                                    <c:forEach items="${registrationList}" var="reg">
                                        <html:option value="${reg.id}"
                                                     styleId="${defaultLocales[reg.user.id]}:${defaultTimezones[reg.user.id]}">
                                            <c:out value="${reg.name}"/>
                                        </html:option>
                                    </c:forEach>

                                </html:select>
                            </c:otherwise>
                        </c:choose>
                    </td>
                </tr>
                <tr>
                    <th><label for="login"><I18n:message key="LOGIN"/> *</label></th>
                    <td>
                        <html:text maxlength="100" size="21" property="login" alt=">0" styleId="login"/>
                        <span class="sample"><I18n:message key="LOGIN_SAMPLE"/></span>
                    </td>
                </tr>
                <tr>
                    <th><label for="nameId"><I18n:message key="USER_NAME"/> *</label></th>
                    <td>
                        <html:text maxlength="40" size="30" property="name" alt=">0" styleId="nameId"/>
                        <span class="sample"><I18n:message key="USER_NAME_SAMPLE"/></span>
                    </td>
                </tr>
                <tr>
                    <th><label for="email"><I18n:message key="EMAIL"/> *</label></th>
                    <td>
                        <html:text styleId="email" alt=">0,email" property="email" size="30" maxlength="200"/>
                        <span class="sample"><I18n:message key="EMAIL_SAMPLE"/></span>
                    </td>

                </tr>
                <tr>
                    <th><label for="company"><I18n:message key="COMPANY"/></label></th>
                    <td>
                        <html:text maxlength="40" size="30" property="company" styleId="company"/>
                        <span class="sample"><I18n:message key="COMPANY_SAMPLE"/></span>
                    </td>
                </tr>
                <tr>
                    <th><label for="localeId"><I18n:message key="LOCALE"/></label></th>
                    <td>
                        <html:select property="locale" styleId="localeId">
                            <c:forEach var="lc" items="${locales}">
                                <html:option value="${lc.key}">
                                    <c:out value="${lc.value}"/>
                                </html:option>
                            </c:forEach>
                        </html:select>
                        <span class="sample"><I18n:message key="LOCALE_SAMPLE"/></span>
                    </td>
                </tr>
                <tr>
                    <th><label for="timezone"><I18n:message key="TIME_ZONE"/></label></th>
                    <td>
                        <html:select styleId="timezone" property="timezone">
                            <c:forEach var="item" items="${timezones}">
                                <html:option value="${item.key}">
                                    <c:out value="${item.value}" escapeXml="true"/>
                                </html:option>
                            </c:forEach>
                        </html:select>
                        <input type="checkbox" onclick="document.getElementById('timezone').disabled = !this.checked;"><I18n:message key="EDIT"/>
                        <c:set var="urlHtml" value="html"/>
                        <script type="text/javascript" src="<%=request.getContextPath()%>/${urlHtml}/detect_timezone.js"></script>
                        <script type="text/javascript">document.getElementById('timezone').disabled = true;</script>
                    </td>
                </tr>

            </table>

            <div class="controls">
                <input type="submit" class="iconized" value="<I18n:message key="REGISTER"/>" name="NEWUSER"/>
            </div>
            <script language="JavaScript">
                <!--
                var visitortime = new Date();
                document.write('<input type="hidden" name="xVisitorTimeZoneOffset" ');
                if (visitortime) {
                    var zone = "/GMT" + visitortime.getTimezoneOffset() * -1 / 60;
                    var zone2 = "/GMT+" + visitortime.getTimezoneOffset() * -1 / 60;
                    var language = (navigator["language"]) ? navigator["language"] : navigator["userLanguage"];
                    try
                    {
                        for (var k = 0; k < document.getElementById("localeId").options.length; k++)
                        {
                            if (document.getElementById("localeId").options[k].value.indexOf(language.replace("-", "_")) == 0)
                            {
                                document.getElementById("localeId").options[k].selected = true;
                                break;
                            }

                        }
                        for (var k = 0; k < document.getElementById("timezoneId").options.length; k++)
                        {
                            var opt = document.getElementById("timezoneId").options[k].value;
                            if (opt.indexOf(zone) != -1 || opt.indexOf(zone2) != -1)
                            {
                                document.getElementById("timezoneId").options[k].selected = true;
                                break;
                            }
                        }
                    } catch(ex)
                    {

                    }
                }
                else {
                    document.write('value="JavaScript not Date() enabled">');
                }
                // -->
            </script>
        </html:form>
    </tiles:put>
</tiles:insert>