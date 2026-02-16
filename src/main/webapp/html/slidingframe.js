var gInnerFrameset = parent.document.getElementsByTagName("frameset")[0] || null;
var gBodyFrameNode = parent.document.getElementsByTagName("frame")[1] || null;
var gNavigationState = -30;
var gSlide = true;
var gNavigationWidth = 240;

function slideTree() {
    if (!gInnerFrameset) return;
    var cols = gInnerFrameset.cols;
    var _localWidth = parseInt(cols);
    if (_localWidth == 0) gNavigationState = 30;
    if (gNavigationState < 0) { // Hiding
        gNavigationWidth = _localWidth;
    }
    else { // Showing
        gInnerFrameset.setAttribute("border", 4);
        gInnerFrameset.setAttribute("frameSpacing", 2);
        gBodyFrameNode.setAttribute("frameBorder", 1);
    }
    gIntervalID = setInterval("_slideTree();", 5);
}

function _slideTree() {
    var cols = gInnerFrameset.cols;
    var currWidth = parseInt(cols);
    var newWidth = gSlide ? currWidth + gNavigationState : 0;

    if (newWidth <= 0 || newWidth >= gNavigationWidth) {
        clearInterval(gIntervalID);
        if (gNavigationState < 0) { // Hiding
            newWidth = 0;
            gInnerFrameset.setAttribute("border", 0);
            gInnerFrameset.setAttribute("frameSpacing", 0);
        }
        else { // Showing
            newWidth = gNavigationWidth;
        }
        gNavigationState *= -1;
        gChanging = false;
    }
    gInnerFrameset.cols = newWidth + ", *";
}

function scrollTree() {
    if (self.top.frames[1].taskTab) {
        if (self.top.frames[0] != null && self.top.frames[0].taskTree != null) {
            self.top.frames[0].taskTree.open = false;
            self.top.frames[0].taskTree.expand(true);
        }
    } else {
        if (self.top.frames[0] != null && self.top.frames[0].userTree != null) {
            self.top.frames[0].userTree.open = false;
            self.top.frames[0].userTree.expand(true);
        }
    }
}

function getNodeOfParent(parentHint, nodeHint) {
    try {
        var parentNode = self.top.frames[0].taskTree.getNodeByHint(parentHint);
        var count = parentNode.childNodes.length;
        for (var i = 0; i != count; i++) {
            var candidat = parentNode.childNodes[i];
            if (candidat) {
                var hintTemp = candidat.hint;
                if (candidat.hint.indexOf("#") != -1 && candidat.hint.indexOf(" ") != -1) {
                    hintTemp = candidat.hint.substring(candidat.hint.indexOf("#"), candidat.hint.indexOf(" "));
                }
                var hintNode = nodeHint;
                if (nodeHint.indexOf("#") != -1 && nodeHint.indexOf(" ") != -1) {
                    hintNode = nodeHint.substring(nodeHint.indexOf("#"), nodeHint.indexOf(" "));
                }
                if (hintTemp == hintNode) {
                    return candidat;
                }
            }
        }
        return null;
    } catch(err) {
        showError("getNodeOfParent", err);
    }
}

function updateTaskInTree(taskNumber, taskName, parentNumber, icons, statuses, actions, taskIds, parentId) {
    try {
        if (self.top.frames[0] != null && self.top.frames[0].taskTree != null && (taskName.length != 0 || statuses.length != 0)) {
            var tempNode = self.top.frames[0].taskTree.getNodeByHint(taskNumber);
            if (tempNode != null && parentNumber.indexOf("#1 ") == -1) {
                if (tempNode.parentNode.action.indexOf('&group=') >= 0) {
                    self.top.frames[0].taskTree.updateNodeAction(tempNode.parentNode.hint, '-', statuses[0]);
                }
                self.top.frames[0].taskTree.updateNodeAction(parentNumber, '-', statuses[0]);
            }
            // ����� ���� ���� �� ����
            if (parentNumber.length == 0) {// ���� �������������� ������
                if (tempNode != null) {
                    parentNumber = tempNode.parentNode.hint;
                    if (parentNumber.toString().indexOf("#1 ") == -1) {
                        self.top.frames[0].taskTree.updateNodeAction(parentNumber, '-', statuses);
                    }
                    tempNode.remove();
                    var onlyNumber = taskNumber.toString().substring(taskNumber.toString().indexOf("#") + 1, taskNumber.toString().indexOf(" "));
                    var numbers = new Array();
                    numbers[0] = "#" + onlyNumber + " " + taskName;
                    addTaskToTree(numbers, taskName, parentNumber, icons, statuses, actions, taskIds, parentId);
                }
            } else {
                if (tempNode != null) {
                    tempNode.remove();
                    var parent = self.top.frames[0].taskTree.getNodeByHint(parentNumber);
                    for (var inx=0;inx<parent.childNodes.length;inx++) {
                        var child = parent.childNodes[inx];
                        if (child.hint == tempNode.hint) {
                            child.remove();
                        }
                    }
                    if (tempNode.parentNode.action.indexOf('&group=') >= 0 && self.top.frames[0].taskTree.getCountNodeNotClose(tempNode.parentNode) <= 0)
                        tempNode.parentNode.remove();
                }
                var hide = self.top.frames[0].taskTree.getHide(actions[0]);
                if ((hide == "E") || (hide == "F" && statuses[0].indexOf('finishstate') == -1)) {
                    var node = addTaskToTree(taskNumber, taskName, parentNumber, icons, statuses, actions, taskIds, parentId);
                    node.reload();
                }
            }
        }
    } catch (err) {
        showError("updateTaskInTree()", err);
    }
}

/**
 * ����� ������ "&group=" ����� ��� ������������, ����������� �� ������ �� ������ ��� ������� ����������
 * ���������� ��������.
 *
 * ����� ������ "&group=1" ����� ��� �����������, �������� �� ������ ����� ������ � ��������, ���������� ���
 * ������������ �������. ���� ���, �� ��� ������ ���������.
 */
function addTaskToTree(taskNumbers, taskNames, parentNumber, icons, statuses, actions, taskIds, parentId) {
    try {
//        console.log(" log 0 " + taskNumbers);
        var tempNode, imageNodeGroup, action;
        var parentNode = self.top.frames[0].taskTree.getNodeByHint(parentNumber);
        var grouping = false;
        var todayNode = null;
//        console.log(" addTaskToTree : " + parentNumber);
        if (parentNode) {
//            console.log(" addTaskToTree try to add in parent : " + parentNumber);
            for (var i = 0; i < taskNumbers.length; i++) {
                var hide = self.top.frames[0].taskTree.getHide(actions[i]);
                if ((hide == "E") || (hide == "F" && statuses[i].toString().indexOf('finishstate') == -1)) {
                    if (parentNumber.toString().indexOf("#1 ") == -1) {
                        self.top.frames[0].taskTree.updateNodeAction(parentNumber, '+', statuses[i]);
                    }
                    if (self.top.frames[0] != null && self.top.frames[0].taskTree != null) {
                        var folder = new AddNodeInFolder(parentNode, imageNodeGroup, action, grouping, todayNode);
                        grouping = folder.grouping;
                        try {
                            if (grouping) {
                                parentNode = folder.parentNode;
                                imageNodeGroup = folder.imageNodeGroup;
                                action = folder.action;
                                todayNode = folder.todayNode;
                                if (todayNode == null) {
                                    parentNode = parentNode.add(new self.top.frames[0].WebFXLoadTreeItem(self.top.frames[0].webFXTreeConfig.todayNodeLocaleName, self.top.frames[0].webFXTreeConfig.todayNodeLocaleName, parentNode.src + '&group=1', action, null, imageNodeGroup, ''));
                                    parentNode.reload();
                                } else {
                                    parentNode = todayNode;
                                }
                                if (parentNumber.toString().indexOf("#1 ") == -1) {
                                    self.top.frames[0].taskTree.updateNodeAction(parentNode.hint, '+', statuses[i]);
                                }
                                if (self.top.frames[0].taskTree.openGroupNode(parentNode.hint)) {
                                    parentNode.expand();
                                    return;
                                }
                            }
                        } catch(err) {
                            showError("addTaskToTree do in group", err);
                        }
                        try {
                            var children = self.top.frames[0].taskTree.parseActionForChildrensTask(actions[i]);
                            if (children > 0) taskNames[i] = self.top.frames[0].taskTree.getNameParse(taskNames[i], children);
                            tempNode = new self.top.frames[0].WebFXLoadTreeItem(taskNames[i], taskNumbers[i], contextPath + '/TreeLoaderAction.do?method=taskUserTree&ti=' + taskIds[i], actions[i], parentNode, icons[i], statuses[i]);
                            var sotrorder = self.top.frames[0].taskTree.isSortOrder(tempNode);
                            if (sotrorder != "true") {
                                sortUpdateTask(parentNode.hint, tempNode.hint);
                            } else {
                                sortAscTask(parentNode);
                            }
                            parentNode.reload();
                        } catch(err) {
                            showError("addTaskToTree add node", err);
                        }
                    }
                }
            }
        }
        return tempNode;
    } catch (err) {
        showError("addTaskToTree()", err);
    }
}

function AddNodeInFolder(parentNode, imageNodeGroup, action, grouping, todayNode) {
    try {
        for (var j = 0; j < parentNode.childNodes.length; j++) {
            if (parentNode.childNodes[j].src) {
                if (parentNode.childNodes[j].src.toString().indexOf('&group=') >= 0) {
                    grouping = true;
                    imageNodeGroup = parentNode.childNodes[j].icon;
                    action = parentNode.childNodes[j].action.substring(0, parentNode.childNodes[j].action.indexOf('group=')) + 'group=1; childs=\'0(0)\';}';
                    if (parentNode.childNodes[j].src.indexOf('&group=1') >= 0) {
                        todayNode = parentNode.childNodes[j];
                        break;
                    }
                }
            }
        }
    } catch (err) {
        showError("AddNodeInFolder", err);
    }
    this.parentNode = parentNode;
    this.imageNodeGroup = imageNodeGroup;
    this.action = action;
    this.grouping = grouping;
    this.todayNode = todayNode;
}

function sortUpdateTask(parentNumber, firstNode) {
    try {
        var parentNode = self.top.frames[0].taskTree.getNodeByHint(parentNumber);
        if (parentNode) {
            var sizeNode = parentNode.childNodes.length;
            if (sizeNode > 1) {
                for (var i = sizeNode - 1; i > 0; i--) {
                    var candidate = parentNode.childNodes[i];
                    if (candidate.hint == firstNode) {
                        candidate.remove();
                        break;
                    }
                    if (self.top.frames[0].taskTree.isSortOrder(candidate) == "true") {
                        nodeAdd(candidate);
                        i++;
                    }
                }
            }
        }
    } catch(err) {
        showError("sortUpdateTask", err);
    }
}

/**
 * ����� ��������� ������ � ���������� �������, ������� �� action
 * @param parentNumber ���� ��������
 */
function sortAscTask(parentNode) {
    if (parentNode && parentNode.childNodes.length > 1) {
        var newNode = parentNode.childNodes[0];
        var sizeNode = parentNode.childNodes.length;
        var firstNode = parentNode.childNodes[1];
        var stopAdd = 0;
        for (var i = sizeNode - 1; i >= 0; i--) {
            var candidate = parentNode.childNodes[i];
            if (candidate) {
                if (self.top.frames[0].taskTree.isSortOrder(candidate) == "true") {
                    if ((candidate.text).toLowerCase() < (newNode.text).toLowerCase() && (candidate.hint != newNode.hint) && stopAdd == 0) {
                        nodeAdd(newNode);
                        stopAdd++;
                    }
                    nodeAdd(candidate);
                    i++;
                }
                if (candidate.hint == firstNode.hint) break;
            }
        }
    }
}

function nodeAdd(node) {
    try {
        var parentNode = node.parentNode;
        var name = self.top.frames[0].taskTree.nameNodeAdd(node.hint);
        var action = self.top.frames[0].taskTree.actionNodeAdd(node.hint);
        node.remove();
        if (node && node.src) {
            var tempNode = new self.top.frames[0].WebFXLoadTreeItem(name, node.hint, node.src, action, parentNode, node.icon, node.statusIcon);
//            tempNode.expand();
//            tempNode.collapse();
        } else {
            parentNode.add(node);
        }
//        parentNode.reload();
    } catch (err) {
        showError("nodeAdd(node)", err);
    }
}

function removeTasksFromTree(taskNumbers) {
    try {
        var tempNode, parentHint, deleteNodeGroup = true;
        for (var i = 0; i < taskNumbers.length; i++) {
            tempNode = self.top.frames[0].taskTree.getNodeByHint(taskNumbers[i]);
            if (tempNode) {
                if (tempNode.parentNode.action.indexOf('&group=') >= 0) {
                    if (tempNode.parentNode.hint.indexOf("#1") != -1) {
                        self.top.frames[0].taskTree.updateNodeAction(tempNode.parentNode.hint, '-', '');
                    }

                    parentHint = tempNode.parentNode.parentNode.hint;
                    if (self.top.frames[0].taskTree.getCountNodeAll(tempNode.parentNode) <= 0) {
                        tempNode.parentNode.remove();
                        deleteNodeGroup = false;
                    }
                } else {
                    parentHint = tempNode.parentNode.hint;
                }
                if (deleteNodeGroup) {
                    tempNode.remove();
                }
                if (parentHint.indexOf("#1 ") == -1) {
                    self.top.frames[0].taskTree.updateNodeAction(parentHint, '-', '');
                }
            }
        }
    } catch (err) {
        showError("removeTasksFromTree", err);
    }
}

function copyTasksInTree(taskNumbers, copiedTaskNumbers) {
    try {
        clearTaskOperationStatusInTree(copiedTaskNumbers);
        for (var i = 0; i < taskNumbers.length; i++) {
            self.top.frames[0].taskTree.setClassName(taskNumbers[i], 'copy');
        }
    } catch (err) {
        showError("copyTasksInTree", err);
    }
}

function copyRecursivelyTasksInTree(taskNumbers, copiedTaskNumbers) {
    try {
        clearTaskOperationStatusInTree(copiedTaskNumbers);
        for (var i = 0; i < taskNumbers.length; i++) {
            self.top.frames[0].taskTree.setClassName(taskNumbers[i], 'copy_recursively');
        }
    } catch (err) {
        showError("copyRecursivelyTasksInTree", err);
    }
}

function cutTasksInTree(taskNumbers, copiedTaskNumbers) {
    try {
        clearTaskOperationStatusInTree(copiedTaskNumbers);
        for (var i = 0; i < taskNumbers.length; i++) {
            self.top.frames[0].taskTree.setClassName(taskNumbers[i], 'cut');
        }
    } catch (err) {
        showError("cutTasksInTree", err);
    }
}

function pasteCutTasksToTree(taskNumbers, taskNames, parentNumber, icons, statuses, actions, taskIds, parentHintForCopieds, parentId) {
    try {
        removeTasksFromTree(taskNumbers);
        addTaskToTree(taskNumbers, taskNames, parentNumber, icons, statuses, actions, taskIds, parentId);
    } catch (err) {
        showError("pasteCutTasksToTree", err);
    }
}

function pasteCopiedTasksToTree(taskNumbers, taskNames, parentNumber, icons, statuses, actions, taskIds, copiedTaskNumbers, parentId) {
    try {
        clearTaskOperationStatusInTree(copiedTaskNumbers);
        addTaskToTree(taskNumbers, taskNames, parentNumber, icons, statuses, actions, taskIds, parentId);
    } catch (err) {
        showError("pasteCopiedTasksToTree", err);
    }
}

function pasteCopiedRecursivelyTasksToTree(taskNumbers, taskNames, parentNumber, icons, statuses, actions, taskIds, copiedTaskNumbers, parentId) {
    try {
        clearTaskOperationStatusInTree(copiedTaskNumbers);
        addTaskToTree(taskNumbers, taskNames, parentNumber, icons, statuses, actions, taskIds, parentId);
        var node = self.top.frames[0].taskTree.getNodeByHint(parentNumber);
        node.reload();
    } catch (err) {
        showError("pasteCopiedTasksToTree", err);
    }
}

function clearTaskOperationStatusInTree(taskNumbers) {
    var tempNode;
    for (var i = 0; i < taskNumbers.length; i++) {
        tempNode = self.top.frames[0].taskTree.getNodeByHint(taskNumbers[i]);
        if (tempNode != null) self.top.frames[0].document.getElementById(tempNode.id + '-anchor').className = 'deselect';
    }
}

function addUserToTree(userLogins, userNames, parentLogin, icons, statuses, actions, userIds) {
    try {
        var tempNode;
        var parentNode = self.top.frames[0].userTree.getNodeByHint(parentLogin);
        var grouping = false;
        var groupNode = null;
        var newFolder = false;
        if (parentNode) {
            for (var i = 0; i < userLogins.length; i++) {
                if (parseActiveUser(actions[i]) == 'true') {
                    if (self.top.frames[0] != null && self.top.frames[0].userTree != null && self.top.frames[0].userTree.getNodeByHint(userLogins[i]) == null) {
                        for (var j = 0; j < parentNode.childNodes.length; j++) {
                            if (parentNode.childNodes[j].action != null && parentNode.childNodes[j].action.indexOf('group=6') != -1) {
                                grouping = true;
                                var groupTmp = userNames[i].substr(0, 1).toUpperCase();
                                if (parentNode.childNodes[j].text.substr(0, 1) == groupTmp) {
                                    groupNode = parentNode.childNodes[j];
                                    break;
                                }
                            }
                        }
                        if (grouping) {
                            if (groupNode == null) {
                                var actionGroup = 'javascript:{ti=' + groupTmp + 'group=6;}';
                                var groupId = parentNode.childNodes.length + 2;
                                var srcGroup = contextPath + "/TreeLoaderAction.do?method=taskUserTree&ui=" + userIds[i] + "&group=" + groupId;
                                parentNode = new self.top.frames[0].WebFXLoadTreeItem(groupTmp, groupTmp, srcGroup, actionGroup, parentNode, contextPath + self.top.frames[0].webFXTreeConfig.FOLDER_IMG, '');
                                newFolder = true;
                            }
                            else {
                                parentNode = groupNode;
                            }
                        }
                        tempNode = new self.top.frames[0].WebFXLoadTreeItem(userNames[i], userLogins[i], contextPath + '/TreeLoaderAction.do?method=taskUserTree&ui=' + userIds[i], actions[i], parentNode, icons[i], statuses[i]);
                        parentNode.expand();
                        tempNode.expand();
                        tempNode.collapse();
                    }
                }
            }
            var newUser = tempNode;
            parentNode = newUser.parentNode;
            if (!grouping && parentNode.childNodes.length > 1) {
                tempNode.remove();
                var sizeNode = parentNode.childNodes.length;
                var firstChild = parentNode.childNodes[0];
                var k = 0;
                var stopNode = 0;
                for (i = sizeNode - 1; i >= 0; i--) {
                    var candidat = parentNode.childNodes[i];
                    if (newUser.text > candidat.text && stopNode == 0) {
                        tempNode = new self.top.frames[0].WebFXLoadTreeItem(newUser.text, newUser.hint, userSrc(newUser.action), newUser.action, parentNode, newUser.icon, newUser.statusIcon);
                        tempNode.expand();
                        tempNode.collapse();
                        stopNode++;
                        i++;
                    }
                    candidat.remove();
                    tempNode = new self.top.frames[0].WebFXLoadTreeItem(candidat.text, candidat.hint, userSrc(candidat.action), candidat.action, parentNode, candidat.icon, candidat.statusIcon);
                    tempNode.expand();
                    tempNode.collapse();
                    i++;
                    if (candidat == firstChild) k++;
                    if (k == 1) {
                        if (stopNode == 0) {
                            tempNode = new self.top.frames[0].WebFXLoadTreeItem(newUser.text, newUser.hint, userSrc(newUser.action), newUser.action, parentNode, newUser.icon, newUser.statusIcon);
                            tempNode.expand();
                            tempNode.collapse();
                        }
                        break;
                    }
                }
            }
        }
    } catch (err) {
        showError("addUserToTree", err);
    }
}

function parseActiveUser(action) {
    var begin = action.indexOf("active='");
    var end = action.lastIndexOf('\'');
    return action.substring(begin + "active='".length + 1, end);
}

function userSrc(str) {
    var first = str.indexOf("id=");
    var end = str.lastIndexOf("&");
    return contextPath + '/TreeLoaderAction.do?method=taskUserTree&ui=' + str.substring(first + 3, end);
}


function renameUserInTree(userNewLogin, userName, userLogin) {
    try {
        if (self.top.frames[0] != null && self.top.frames[0].userTree != null) {
            self.top.frames[0].userTree.renameNode(userLogin[0], userName[0]);
            self.top.frames[0].userTree.renameNodeHint(userLogin[0], userNewLogin[0]);
        }
    } catch (err) {
        showError("renameUserInTree", err);
    }
}

function removeUsersFromTree(userLogins) {
    try {
        var tempNode;
        for (var i = 0; i < userLogins.length; i++) {
            tempNode = self.top.frames[0].userTree.getNodeByHint(userLogins[i]);
            if (tempNode != null) {
                tempNode.remove();
                var parentNode = tempNode.parentNode;
                if (parentNode.action.indexOf('group=6') != -1) {
                    var countChildrens = parentNode.childNodes.length;
                    if (countChildrens < 1)
                        parentNode.remove();
                }
            }
        }
    } catch (err) {
        showError("removeUsersFromTree", err);
    }
}

function cutUsersInTree(userLogins, copiedUserLogins) {
    try {
        clearUserOperationStatusInTree(copiedUserLogins);
        var tempNode;
        for (var i = 0; i < userLogins.length; i++) {
            tempNode = self.top.frames[0].userTree.getNodeByHint(userLogins[i]);
            var elAnch = self.top.frames[0].document.getElementById(tempNode.id + '-anchor');
            if (tempNode != null && elAnch) elAnch.className = 'cut';
        }
    } catch (err) {
        showError("cutUsersInTree", err);
    }
}

function pasteCutUsersToTree(userLogins, userNames, parentLogin, icons, statuses, actions, userIds) {
    try {
        removeUsersFromTree(userLogins);
        addUserToTree(userLogins, userNames, parentLogin, icons, statuses, actions, userIds);
    } catch (err) {
        showError("pasteCutUsersToTree", err);
    }
}

function clearUserOperationStatusInTree(userLogins) {
    var tempNode;
    for (var i = 0; i < userLogins.length; i++) {
        tempNode = self.top.frames[0].userTree.getNodeByHint(userLogins[i]);
        if (tempNode != null) {
            var elAn = self.top.frames[0].document.getElementById(tempNode.id + '-anchor');
            if (elAn) {
                elAn.className = 'deselect';
            }
        }
    }
}

function setSlidingState(value) {
    var curCookie = "tstree=" + escape(value);
    document.cookie = curCookie;
}

function getSlidingState() {
    var dc = parent.window.document.cookie;

    var prefix = "tstree=";
    var begin = dc.indexOf("; " + prefix);

    if (begin == -1) {
        begin = dc.indexOf(prefix);
        if (begin != 0) return null;
    } else {
        begin += 2;
    }

    var end = dc.indexOf(";", begin);
    if (end == -1)
        end = dc.length;

    return unescape(dc.substring(begin + prefix.length, end));
}


var labelHeight = 26;


function slidePanels(top, bottom) {
    initialTopHeight = labelHeight * top;
    initialBottom = labelHeight * bottom;
    document.getElementById('content').style.top = initialTopHeight + "px";
    document.getElementById('content').style.bottom = initialBottom + "px";
    document.getElementById('content').style.borderTopWidth = initialTopHeight + "px";
    document.getElementById('content').style.borderBottomWidth = initialBottom + "px";
    document.getElementById('head').style.height = initialTopHeight + "px";
    document.getElementById('foot').style.height = initialBottom + "px";
}


function showBookmarkPanel() {
    slidePanels(3, 0);
    document.getElementById('bottompanel_1').style.display = 'none';
    document.getElementById('bottompanel_2').style.display = 'none';
    document.getElementById('bottompanel_3').style.display = 'none';
    document.getElementById('toppanel_1').style.display = 'block';
    document.getElementById('toppanel_2').style.display = 'block';
    document.getElementById('toppanel_3').style.display = 'block';
    document.getElementById('panel_1').style.display = 'none';
    document.getElementById('panel_2').style.display = 'none';
    document.getElementById('panel_3').style.display = 'block';
}

function showTaskPanel() {
    slidePanels(1, 2);
    document.getElementById('bottompanel_1').style.display = 'block';
    document.getElementById('bottompanel_2').style.display = 'block';
    document.getElementById('bottompanel_3').style.display = 'block';
    document.getElementById('toppanel_1').style.display = 'none';
    document.getElementById('toppanel_2').style.display = 'none';
    document.getElementById('toppanel_3').style.display = 'none';
    document.getElementById('panel_1').style.display = 'block';
    document.getElementById('panel_2').style.display = 'none';
    document.getElementById('panel_3').style.display = 'none';
}

function showUserPanel() {
    slidePanels(2, 1);
    document.getElementById('bottompanel_1').style.display = 'none';
    document.getElementById('bottompanel_2').style.display = 'none';
    document.getElementById('bottompanel_3').style.display = 'block';
    document.getElementById('toppanel_1').style.display = 'block';
    document.getElementById('toppanel_2').style.display = 'block';
    document.getElementById('toppanel_3').style.display = 'none';
    document.getElementById('panel_1').style.display = 'none';
    document.getElementById('panel_2').style.display = 'block';
    document.getElementById('panel_3').style.display = 'none';
}

function showTree() {
    // New app-shell layout: use TS.sidebar API
    if (window.top.TS && window.top.TS.sidebar) {
        window.top.TS.sidebar.toggle();
        return;
    }
    // Legacy frameset fallback
    slideTree();
    if (document.getElementById('openpanel').style.display == 'none') {
        document.getElementById('closepanel').style.display = 'none';
        document.getElementById('openpanel').style.display = 'inline';
        setTabCookie("isShowTree", "false");
    } else {
        setTabCookie("isShowTree", "true");
        document.getElementById('closepanel').style.display = 'inline';
        document.getElementById('openpanel').style.display = 'none';
    }
}

function closeTree() {
    // New app-shell layout: use TS.sidebar API
    if (window.top.TS && window.top.TS.sidebar) {
        window.top.TS.sidebar.close();
        return;
    }
    // Legacy frameset fallback
    if (!gInnerFrameset) return;
    var cols = gInnerFrameset.cols;
    var _localWidth = parseInt(cols);
    if (_localWidth > 0) {
        if (gNavigationState < 0) { // Hiding
            gNavigationWidth = _localWidth;
        }
        gIntervalID = setInterval("_slideTree();", 5);
    }
}

function setPrstateInHeader(prstateName) {
    var prstate = parent.headerFrame.document.getElementById('prstate');
    if (prstate) {
        prstate.innerHTML = prstateName;
    }
}