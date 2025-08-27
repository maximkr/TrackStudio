<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01//EN" "${contextPath}/strict.dtd">
<%@ page import="java.io.PrintWriter,
                 java.net.URLEncoder,
                 com.trackstudio.tools.textfilter.HTMLEncoder,
                 com.trackstudio.kernel.manager.TSPropertyManager"%>

<%@ page import="com.trackstudio.exception.UserException"%>
<%@ page import="com.trackstudio.exception.DuplicateUserLoginException"%>
<%@ page import="com.trackstudio.exception.InvalidParameterException"%>
<%@ page import="com.trackstudio.exception.LicenseException"%>
<%@ page import="com.trackstudio.exception.TriggerException"%>
<%@ page import="com.trackstudio.startup.Config"%>
<%@ page import="org.apache.commons.logging.Log"%>
<%@ page import="org.apache.commons.logging.LogFactory"%>
<%@ page import="com.trackstudio.app.adapter.AdapterManager" %>
<%@ page import="java.sql.Timestamp" %>
<%@ page import="com.trackstudio.tools.formatter.DateFormatter" %>
<%@ page import="java.util.Locale" %>
<%@ page import="java.util.TimeZone" %>
<%@ page isErrorPage="true" %>
<%
    String encoding = Config.getEncoding();
    String contextPath = Config.getContextPath(request);

    String versionPath = Config.getVersionPath();


    response.setContentType("text/html; charset="+encoding);
    response.setHeader("Cache-Control","public"); //HTTP 1.1
    response.setHeader("Pragma","public"); //HTTP 1.0
    response.setDateHeader ("Expires", 0L); //prevents caching at the proxy server
    Log log = LogFactory.getLog("TrackStudio Error");
    String expireHote = "";
    try {
        long support_date = AdapterManager.getInstance().getSecuredTSInfoAdapterManager().getSupportExpireDate();
        expireHote = new DateFormatter(TimeZone.getDefault(), Locale.ENGLISH).parse(new Timestamp(support_date));
    } catch (Exception e) { /*Empty*/}
%>
<html>
<head>
    <meta http-equiv="X-UA-Compatible" content="IE=edge,chrome=1">
    <link rel="stylesheet" type="text/css" href="<%=contextPath+versionPath%>/style_src.css" media="screen">
    <link rel="stylesheet" type="text/css" href="<%=contextPath+versionPath%>/style-print_src.css" media="print">
    <title>TrackStudio Error</title>
</head>
<body>
<br><br>
<DIV class=helpcontainer>
    <%
        Integer status = (Integer) request.getAttribute("status");
        if (status == null) {
            if ((exception instanceof UserException && !(exception instanceof InvalidParameterException)) || (exception instanceof ServletException && ((ServletException)exception).getRootCause() instanceof UserException)) {
                if (exception instanceof DuplicateUserLoginException)
                    session.removeAttribute("r_login");

    %>
    <div class="helptopic"><%=exception.getMessage() != null ? (exception instanceof LicenseException || exception instanceof TriggerException ? exception.getMessage() : HTMLEncoder.encode(exception.getMessage())) : "error"
    %>
    </div>
    <div class="help">Please press the <b>Back</b> button and try again.
    </div>


    <%
    } else {
        Runtime jre = Runtime.getRuntime();
    %>



    <div class="helptopic">
        TrackStudio Error
    </div>
    <div class="help">
        A system error has occurred.
        <br>
        Please send an email to <a class="internal" href="mailto:support@trackstudio.com?subject=An%20error%20<%=exception != null && exception.getMessage() != null ? URLEncoder.encode(exception.getMessage(), "UTF-8") : ""%>%20has%20occured">support@trackstudio.com</a> <% if (!Config.isTurnItOn("trackstudio.disable.show.stack.trace")) { %> with the following information:
        <ul>
            <li>description of your problem</li>
            <li>the information found below</li>
        </ul>
        <br>
        <b>System Information & Stack Trace:</b><br>
        <textarea  name="stacktrace" rows=60 cols=100 readonly="true">
            <%
                String info="System Information:\n"+
                        "Java Information:\n"+
                        "Java Version: " + System.getProperty("java.version") + "\n"+
                        "Java Vendor: " + System.getProperty("java.vendor") + "\n"+
                        "Java Specification Vendor: " + System.getProperty("java.specification.vendor") + "\n"+
                        "Java Specification Version: " + System.getProperty("java.specification.version") + "\n"+
                        "Java Home: " + System.getProperty("java.home") + "\n"+
                        "Java&nbsp;Classpath: " + System.getProperty("java.class.path") + "\n"+
                        "Servlet Container Information:\n"+
                        "Servlet Version: " + pageContext.getServletContext().getMajorVersion() + pageContext.getServletContext().getMinorVersion() + "\n"+
                        "AS Vendor: " + pageContext.getServletContext().getServerInfo() + "\n"+
                        "Virtual Machine Information:\n"+
                        "VM Vendor: " + System.getProperty("java.vm.vendor") + "\n"+
                        "VM: " + System.getProperty("java.vm.name") + "\n"+
                        "VM Version: " + System.getProperty("java.vm.version") + "\n"+
                        "Runtime Version: " + System.getProperty("java.runtime.version") + "\n"+
                        "VM Specification Version: " + System.getProperty("java.vm.specification.version") + "\n"+
                        "VM Info: " + System.getProperty("java.vm.info") + "\n"+
                        "System information:\n"+
                        "OS Name: " + System.getProperty("os.arch") + "-" + System.getProperty("os.name") + "(" + System.getProperty("os.version") + ")\n"+
                        "Total Memory: " + jre.totalMemory() + "\n"+
                        "Free Memory: " + jre.freeMemory() + "\n"+
                        "Product Information:\n"+
                        "Version: " + com.trackstudio.app.adapter.AdapterManager.getInstance().getSecuredTSInfoAdapterManager().getTSVersion(null) + "\n"+
                        "Licensee: " + Config.getInstance().getProperty("trackstudio.license.licensee") + "\n"+
                        "License Type: " + Config.getInstance().getProperty("trackstudio.license.type") + "\n"+
                        "Database Information:\n"+
                        "Database: " + Config.getInstance().getDatabaseMetadata().getDatabaseProductName() + "\n" +
                        "Database version:" + Config.getInstance().getDatabaseMetadata().getDatabaseProductVersion() + "\n"+
                        "Database major version:" + Config.getInstance().getDatabaseMetadata().getDatabaseMajorVersion() + "\n"+
                        "Database minor version:" + Config.getInstance().getDatabaseMetadata().getDatabaseMinorVersion() + "\n"+
                        "Database driver name:" + Config.getInstance().getDatabaseMetadata().getDriverName() + "\n"+
                        "Database driver version:" + Config.getInstance().getDatabaseMetadata().getDriverVersion() + "\n"+
                        "Database driver major version:" + Config.getInstance().getDatabaseMetadata().getDriverMajorVersion() + "\n"+
                        "Database driver minor version:" + Config.getInstance().getDatabaseMetadata().getDriverMinorVersion() + "\n"+
                        "Hibernate Dialect: " + Config.getProperty("hibernate.dialect") + "\n"+
                        "Hibernate Driver: " + Config.getProperty("hibernate.connection.driver_class") + "\n"+
                        "Expire Date: " + expireHote + "\n"+
                        "Stack Trace:";
            %>
            <%=info%>
            <%
                if (exception!=null) {
                    Throwable cause;
                    if (exception instanceof com.trackstudio.exception.GranException)
                        cause = ((com.trackstudio.exception.GranException) exception).getTSCause();
                    else {
                        cause = exception;
                        cause.printStackTrace();
                    }
                    log.error(info+"\nTrackStudio Error: ", cause);
                    cause.printStackTrace(new PrintWriter(out));
                }
            %></textarea>
        <BR>

        <%
                }
            }
        } else if (HttpServletResponse.SC_BAD_REQUEST == status) {
        %> Sorry. This task does not exist, number of task = <%=request.getAttribute("key")%> <%
    } else if (HttpServletResponse.SC_FORBIDDEN == status) {
    %> Sorry. You (<%=request.getAttribute("login")%>) don't have an access to this task, number of task = <%=request.getAttribute("key")%> <%
        }
    %>
    </div>
    <div class="controls">
        <input type="button" class="iconized" onclick="self.location.replace('<%=request.getContextPath()%>/LoginAction.do?method=loginPage')" value="Back">
    </div>

</div>
</body>
</html>