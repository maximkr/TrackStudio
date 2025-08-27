<%@ page buffer="128kb" errorPage="/jsp/Error.jsp" %>
<%@ taglib uri="jstl/c" prefix="c" %>
<%@ taglib uri="struts/html-el" prefix="html" %>
<HTML>
<head>
    <title>File Upload Manager</title>
    <script type="text/javascript">
        var parentWindow = opener;
        function updateParentWindow(update) {
            if (update) {
                parentWindow.location.reload(true)
            }
            self.close();
        }
    </script>
</head>
<body>
<center>
        <html:form action="/UploadAppletAction.do" method="post" target="_blank" styleId="exportFormId">
            <html:hidden property="method" value="view"/>
            <html:hidden property="id" value="${id}"/>
            <html:hidden property="session" value="${session}"/>
                    <APPLET CODE="com.trackstudio.applets.PictureUploadApplet" ARCHIVE="pupload.jar" WIDTH="750" HEIGHT="420">
                        <PARAM NAME=CODE VALUE="com.trackstudio.applets.PictureUploadApplet">
                        <PARAM NAME=ARCHIVE VALUE="plastic.jar, pupload.jar">
                        <PARAM NAME="type" VALUE="application/x-java-applet;version=1.4">
                        <PARAM NAME="scriptable" VALUE="false">
                        <PARAM NAME="uploadURL" VALUE="${contextPath}/writeOut.do?session=${sessionId}">
                        <PARAM NAME="taskId" VALUE="${ownerId}">
                        <PARAM NAME="attaches" VALUE="${attaches}">
                        <PARAM NAME="canDeleteUpload" VALUE="${canDeleteUpload}">
                        <PARAM NAME="locale" VALUE="${locale}">
                    </APPLET>
        </html:form>
</center>
</body>
</HTML>