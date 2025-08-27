<%@ page import="com.trackstudio.startup.Config"%>
<%@ page buffer="128kb" errorPage="/jsp/Error.jsp"%>
<%@ page session="false"%>

<script type="text/javascript">
    self.top.location.replace('<%=Config.getInstance().getLogoutURL(request.getContextPath())%>');
</script>
