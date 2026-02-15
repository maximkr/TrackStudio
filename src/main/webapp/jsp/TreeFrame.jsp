<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01//EN" "${contextPath}/strict.dtd">
<%@ page buffer="128kb" errorPage="/jsp/Error.jsp" %>
<%@ taglib uri="jstl/c" prefix="c" %>
<%@ taglib uri="jstl/fmt" prefix="I18n" %>
<%@ taglib uri="struts/html-el" prefix="html" %>
<%@ taglib uri="http://trackstudio.com" prefix="ts" %>
<I18n:setLocale value="${sc.locale}"/>
<I18n:setTimeZone value="${sc.timezone}"/>
<I18n:setBundle basename="language"/>
<html>
<head>
	<title>TrackStudio</title>
	<meta http-equiv="X-UA-Compatible" content="IE=edge,chrome=1">
	<meta http-equiv="content-type" content="text/html; charset=${encoding}">

	<script src="${contextPath}/html/fancytree-2.31.0/lib/jquery.js" type="text/javascript"></script>
	<script src="${contextPath}/html/fancytree-2.31.0/lib/jquery-ui.custom.js" type="text/javascript"></script>
    <link href="${contextPath}/html/fancytree-2.31.0/src/skin-xp/ui.fancytree.css" rel="stylesheet" type="text/css">
	<script src="${contextPath}/html/fancytree-2.31.0/src/jquery-ui-dependencies/jquery.fancytree.ui-deps.js"></script>
	<script src="${contextPath}/html/fancytree-2.31.0/src/jquery.fancytree.js" type="text/javascript"></script>
	<script src="${contextPath}/html/fancytree-2.31.0/src/jquery.fancytree.dnd5.js" type="text/javascript"></script>
    <script src="${contextPath}/html/fancytree-2.31.0/lib/contextmenu-abs/jquery.contextMenu-custom.js"></script>
    <link href="${contextPath}/html/fancytree-2.31.0/lib/contextmenu-abs/jquery.contextMenu.css" rel="stylesheet" >
	<style type="text/css">
		ul.fancytree-container {
			height: 99%;
		}
	</style>
	<script type="text/javascript">
		TREE_LOADED = false;
        // --- Contextmenu helper --------------------------------------------------
        function bindContextMenu(span) {
            $(span).contextMenu({menu: "myMenu"}, function() {
                expandSelected();
            });
        }

		$(function(){
			$("#task_tree").fancytree(
					{
                        extensions: ["dnd5"],
						source: {
							url: "${contextPath}/TreeLoaderAction.do?method=taskLoadChildren"
						},
						lazyLoad: function(event, data) {
							data.result = {
								url: "${contextPath}/TreeLoaderAction.do?method=taskLoadChildren&ti=" + data.node.key,
                                cache: false
							}
						},
						init: function(event, data, flag) {
							TREE_LOADED = true;
						},
						click: function(event, data) {
							if (data.targetType == 'title') {
								self.top.frames[1].location='${contextPath}/task/'+data.node.key + '?thisframe=true';
								data.node.setExpanded(true);
							}
                            if( $(".contextMenu:visible").length > 0 ){
                                $(".contextMenu").hide();
         // return false;
                            }
						},
						activate: function (event, data) {
							data.node.title = "test";
						},
                        createNode: function(event, data){
                            // bindContextMenu(data.node.span);
                        },
                    dnd5: {
                        preventRecursion: true, // Prevent dropping nodes on own descendants
                        preventVoidMoves: true, // Prevent moving nodes 'before self', etc.
                        dragStart: function(node, data) {
                            data.effectAllowed = "all";
                            data.dropEffect = "move";
                            return true;
                        },
                        dragDrop: function(node, data) {
                        }
                    }
					}
			);
		});

		function expandTsTree(tasks) {
			var tree = $("#task_tree").fancytree("getTree");
			expandRec(tree, tasks, 0);
		}

		function expandRec(tree, tasks, index) {
			if (tasks.length > index) {
				var node = tree.getNodeByKey(tasks[index]);
				if (node) {
                    ++index;
					if (node.isExpanded()) {
                        expandRec(tree, tasks, index);
					} else {
                        node.setExpanded(true).done(
                            function () {
                                if (tasks.length > index) {
                                    expandRec(tree, tasks, index);
                                } else {
                                    node.setActive(true);
                                    node.makeVisible();
                                }
                            }
                        );
                    }
				}
            } else {
                if (tasks.length === index && tasks.length !== 0) {
                    tree.getNodeByKey(tasks[index - 1]).setActive(true);
                }
            }
        }

		function reloadTsTree(parent) {
			var tree = $("#task_tree").fancytree("getTree");
			console.log(parent);
			if (parent === '1') {
				tree.reload();
			} else {
				var node = tree.getNodeByKey(parent);
				node.resetLazy();
				node.setExpanded(true);
			}
		}

		function selectNodesTsTree(nodes) {
			var tree = $("#task_tree").fancytree("getTree");
			for(var i=0;i!=nodes.length;++i) {
				var node = tree.getNodeByKey(nodes[i]);
				if (node) {
					node.setSelected(true);
				}
			}
		}

		function selectUsersTsTree(nodes) {
			var tree = $("#user_tree").fancytree("getTree");
			for(var i=0;i!=nodes.length;++i) {
				var node = tree.getNodeByKey(nodes[i]);
				if (node) {
					node.setSelected(true);
				}
			}
		}

		$(function() {
			$("#user_tree").fancytree(
					{
						source: {
							url: "${contextPath}/TreeLoaderAction.do?method=userLoadChildren"
						},
						lazyLoad: function(event, data) {
							data.result = {
								url: "${contextPath}/TreeLoaderAction.do?method=userLoadChildren&ti=" + data.node.key
							}
						},
						init: function(event, data, flag) {
							TREE_LOADED = true;
						},
						click: function(event, data) {
							var node = data.node;
							var target = data.targetType;
							if (target == 'title') {
								self.top.frames[1].location='${contextPath}/user/'+data.node.key + '?thisframe=true';
								data.node.setExpanded(true);
							}
						}
					}
			);
		});

		function reloadTsUserTree(parent, nodes) {
			var tree = $("#user_tree").fancytree("getTree");
			if (parent == 'root') {
				tree.reload();
			} else {
				if (nodes) {
					for (var i = 0; i != nodes.length; ++i) {
						var node = tree.getNodeByKey(nodes[i]);
						if (node) {
							node.remove();
						}
					}
				}
				var node = tree.getNodeByKey(parent);
				node.resetLazy();
				node.setExpanded(true);
			}
		}

        function expandSelected(){
            var tree = $("#task_tree").fancytree("getTree");
            var activeNode = tree.getActiveNode();
            expandChildren(activeNode);
        }

        function expandChildren(activeNode) {
            activeNode.setExpanded(true).done(
                    function () {
                        var children = activeNode.getChildren();
                        if (children) {
                            for (var i = 0; i < children.length; i++) {
                                expandChildren(children[i]);
                            }
                        }
                    }
            )
        }
	</script>
	<I18n:setLocale value="${sc.locale}"/>
	<I18n:setTimeZone value="${sc.timezone}"/>
	<I18n:setBundle basename="language"/>
	<c:set var="urlHtml" value="${isCompress}"/>
	<script type="text/javascript">
		var contextPath = '${contextPath}';
		var urlHtml = '${urlHtml}';
	</script>
	<ts:js request="${request}" response="${response}">
		<%--<ts:jsLink link="${urlHtml}/jquery/jquery-1.11.2.min.js"/>--%>
		<%--<ts:jsLink link="${urlHtml}/jquery/jquery-ui.min.js"/>--%>
		<ts:jsLink link="${urlHtml}/slidingframe.js"/>
		<ts:jsLink link="${urlHtml}/validate.js"/>
	</ts:js>

	<link rel="shortcut icon" href="favicon.ico" type="image/x-icon"/>
	<link rel="icon" href="${contextPath}/${ImageServlet}/favicon.png" type="image/png"/>

	<ts:css request="${request}">
		<ts:cssLink link="style_tokens.css"/>
		<ts:cssLink link="style_src.css"/>
		<ts:cssLink link="style-panel.css"/>
		<ts:cssLink link="style_components.css"/>
	</ts:css>
</head>

<body class="ts-tree-body">
<script type="text/javascript">
</script>

<ul id="myMenu" class="contextMenu">
    <li class="copy"><a href="#"><I18n:message key="MORE"/></a></li>
    </ul>

<div id="panel_1" style="display: block">
	<div class="head">
		<label class="header" for="panel_1" onclick="">
			<html:img alt="" src="${contextPath}${ImageServlet}/${urlHtml}/xtree/images/taskMGM.gif" style="vertical-align: middle"/>
			<I18n:message key="BROWSE_TASKS"/>
		</label>
	</div>
	<div id='task_tree' class="content">
	</div>
	<div class="foot">
		<label class="header" for="panel_2"
		       onclick="document.getElementById('panel_1').style.display='none';document.getElementById('panel_2').style.display='block';document.getElementById('panel_3').style.display='none';">
			<html:img alt="" src="${contextPath}${ImageServlet}/${urlHtml}/xtree/images/userMGM.gif"
			          style="vertical-align: middle"/>
			<I18n:message key="BROWSE_USERS"/>
		</label>

		<label class="header" for="panel_3"
		       onclick="document.getElementById('panel_1').style.display='none';document.getElementById('panel_2').style.display='none';document.getElementById('panel_3').style.display='block';  updateBookmarks('${contextPath}/bookmark');">
			<html:img src="${contextPath}${ImageServlet}/cssimages/ico.star.gif" border="0"
			          style="vertical-align: middle"/>
			<I18n:message key="BOOKMARKS"/>
		</label>
	</div>
</div>

<div id="panel_2" style="display: none;">
	<div class="head">
		<label class="header" for="panel_1"
		       onclick="document.getElementById('panel_1').style.display='block';document.getElementById('panel_2').style.display='none';document.getElementById('panel_3').style.display='none';">
			<html:img alt="" src="${contextPath}${ImageServlet}/${urlHtml}/xtree/images/taskMGM.gif"
			          style="vertical-align: middle"/>
			<I18n:message key="BROWSE_TASKS"/>
		</label>

		<label class="header" for="panel_2"
		       onclick="document.getElementById('panel_1').style.display='none';document.getElementById('panel_2').style.display='block';document.getElementById('panel_3').style.display='none';">
			<html:img alt="" src="${contextPath}${ImageServlet}/${urlHtml}/xtree/images/userMGM.gif"
			          style="vertical-align: middle"/>
			<I18n:message key="BROWSE_USERS"/>
		</label>
	</div>
	<div id='user_tree' class="content">
	</div>
	<div class="foot">
		<label class="header" for="panel_3"
		       onclick="document.getElementById('panel_1').style.display='none';document.getElementById('panel_2').style.display='none';document.getElementById('panel_3').style.display='block';  updateBookmarks('${contextPath}/bookmark');">
			<html:img src="${contextPath}${ImageServlet}/cssimages/ico.star.gif" border="0"
			          style="vertical-align: middle"/>
			<I18n:message key="BOOKMARKS"/>
		</label>
	</div>
</div>
<div id="panel_3" style="display: none;">
	<div class="head">
		<label class="header" for="panel_1"
		       onclick="document.getElementById('panel_1').style.display='block';document.getElementById('panel_2').style.display='none';document.getElementById('panel_3').style.display='none';">
			<html:img alt="" src="${contextPath}${ImageServlet}/${urlHtml}/xtree/images/taskMGM.gif"
			          style="vertical-align: middle"/>
			<I18n:message key="BROWSE_TASKS"/>
		</label>

		<label class="header" for="panel_2"
		       onclick="document.getElementById('panel_1').style.display='none';document.getElementById('panel_2').style.display='block';document.getElementById('panel_3').style.display='none';">
			<html:img alt="" src="${contextPath}${ImageServlet}/${urlHtml}/xtree/images/userMGM.gif"
			          style="vertical-align: middle"/>
			<I18n:message key="BROWSE_USERS"/>
		</label>

		<label class="header" for="panel_3"
		       onclick="document.getElementById('panel_1').style.display='none';document.getElementById('panel_2').style.display='none';document.getElementById('panel_3').style.display='block'; updateBookmarks('${contextPath}/bookmark');">
			<html:img src="${contextPath}${ImageServlet}/cssimages/ico.star.gif" border="0"
			          style="vertical-align: middle"/>
			<I18n:message key="BOOKMARKS"/>
		</label>
	</div>
	<div class="content">
		<script type="text/javascript">
			function updateBookmarks(url) {
				$.ajax(url, {
					success: function(data) {
						$('#bookmarkPanel').html(data);
					}
				});
			}
			updateBookmarks("${contextPath}/bookmark");
		</script>
		<div id="bookmarkPanel" class="bookmarkPanel"></div>
	</div>
	<div class="foot">
	</div>
</div>
</body>
</html>