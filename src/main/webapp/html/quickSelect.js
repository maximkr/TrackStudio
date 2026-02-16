function autoUnsetEdit(sender) {
    if (!sender.checked) {
        var td = sender.parentNode.parentNode.parentNode.getElementsByTagName("td")[1];
        var edit = td.getElementsByTagName("input")[0];
        if (edit.checked)
            edit.checked = false;
    }
}
function autoSetView(sender) {
    if (sender.checked) {
        var td = sender.parentNode.parentNode.parentNode.getElementsByTagName("td")[0];
        var view = td.getElementsByTagName("input")[0];
        if (!view.checked)
            view.checked = true;
    }
}
// for autoselect listboxes
function selectColumn(sender, selectIndex, columnIndex) {
    var trs = sender.parentNode.parentNode.parentNode.getElementsByTagName("tr");
    for (var i = 1; i < trs.length; i++) {
        var s = trs[i].getElementsByTagName("td")[columnIndex];
        if (s)
            s = s.getElementsByTagName("select")[0];
        if (s && !s.disabled)
            s.selectedIndex = selectIndex;
    }
    sender.selectedIndex = 0;
}
function selectRow(sender, selectIndex) {
    var tds = sender.parentNode.parentNode.parentNode.getElementsByTagName("td");
    for (var i = 0; i < tds.length; i++) {
        var s = tds[i].getElementsByTagName("select")[0];
        if (s && !s.disabled && s.id == "viewSelect" && s.options.length <= selectIndex)
            s.selectedIndex = 1;//All
        if (s && !s.disabled && s.options.length > selectIndex)
            s.selectedIndex = selectIndex;
    }
    sender.selectedIndex = 0;
}
function setCatpermRow(sender, selectIndex) {
    var tds = sender.parentNode.parentNode.parentNode.getElementsByTagName("td");
    if (!sender.multiple) {
        var viewSelect = null;
        var none = true;
        var all = false;
        var h = false;
        var sub = false;
        var sh = false;
        for (var i = 0; i < tds.length; i++) {
            var s = tds[i].getElementsByTagName("select")[0];
            if (s.multiple)
                continue;
            if (s.id == "viewSelect" && s && !s.disabled)
                viewSelect = s;

            if (s.selectedIndex == 1) {
                none = false;
                all = true;
            }
            if (s.selectedIndex == 2 && !all) {
                none = false;
                if (h) {
                    h = false;
                    sh = true;
                } else
                    sub = true;
            }
            if (s.selectedIndex == 3 && !all) {
                none = false;
                if (sub) {
                    sub = false;
                    sh = true;
                } else
                    h = true;
            }
            if (s.selectedIndex == 4 && !all) {
                none = false;
                sh = true;
            }
        }
        if (viewSelect) {
            if (all)
                viewSelect.selectedIndex = 1;
            else if (sh && viewSelect.selectedIndex != 1)
                viewSelect.selectedIndex = 4;
            else if (h && viewSelect.selectedIndex != 1 && viewSelect.selectedIndex != 4)
                viewSelect.selectedIndex = 3;
            else if (sub && viewSelect.selectedIndex != 1 && viewSelect.selectedIndex != 4)
                viewSelect.selectedIndex = 2;
        }
    }

    if (sender.multiple) {//task status combo
        var viewSelect = null;
        for (var i = 0; i < tds.length; i++) {
            var s = tds[i].getElementsByTagName("select")[0];
            if (!s.multiple)
                continue;
            if (s.id == "viewSelect" && s && !s.disabled)
                viewSelect = s;
            else {
                if (!viewSelect.options[1].selected) { // All
                    for (var k = 0; k < s.options.length; k++) {
                        if (s.options[k].selected)
                            viewSelect.options[k].selected = true;
                    }
                }
            }
            if (s.selectedIndex == -1)
                s.selectedIndex = 0;
        }
    }

}

function selectViewRow(sender, selectIndex) {
    var tds = sender.parentNode.parentNode.parentNode.getElementsByTagName("td");
    var allNone = selectIndex == 0;
    for (var i = 0; i < tds.length; i++) {
        var s = tds[i].getElementsByTagName("select")[0];
        if (s.id != "rowSelector" && s.id != "createSelect" && s && !s.disabled && s.options.length > selectIndex) {
            if (!sender.multiple && !s.multiple) {
                if (allNone) {
                    s.selectedIndex = 0;//None
                    continue;
                }
                if (selectIndex == 2 && s.selectedIndex != 0) {
                    s.selectedIndex = 2;//Submitter
                    continue;
                }
                if (selectIndex == 3 && s.selectedIndex != 0) {
                    s.selectedIndex = 3;//Handler
                    continue;
                }
                if (selectIndex == 4 && s.selectedIndex != 0 && s.selectedIndex != 2 && s.selectedIndex != 3) {
                    s.selectedIndex = 4;//sh
                    continue;
                }
            } else if (sender.multiple && s.multiple) {
                if (!sender.options[1].selected) // All
                    for (var k = 0; k < sender.options.length; k++) {
                        if (!sender.options[k].selected)
                            s.options[k].selected = false;
                    }
                if (s.selectedIndex == -1)
                    s.selectedIndex = 0;
            }
        }
    }
}

function selectCatpermColumn(sender, selectIndex, columnIndex) {
    var trs = sender.parentNode.parentNode.parentNode.parentNode.getElementsByTagName("tr");
    for (var i = 1; i < trs.length; i++) {
        var s = trs[i].getElementsByTagName("td")[columnIndex];
        if (s)
            s = s.getElementsByTagName("select")[0];
        if (s && !s.disabled) {
            s.selectedIndex = selectIndex;
            if (s.id == "viewSelect")
                selectViewRow(s, selectIndex);
            else
                setCatpermRow(s, selectIndex);
        }
    }
    sender.selectedIndex = 0;
}


function selectAll(sender, selectIndex, columnIndex) {
    if (!columnIndex)
        columnIndex = 0;
    var trs = sender.parentNode.parentNode.parentNode.getElementsByTagName("tr");
    for (var i = 1; i < trs.length; i++) {
        var tds = trs[i].getElementsByTagName("td");
        for (ii = columnIndex; ii < tds.length; ii++) {
            var s = tds[ii].getElementsByTagName("select")[0];
            if (s && !s.disabled && s.options.length > selectIndex)
                s.selectedIndex = selectIndex;
        }
    }
    sender.selectedIndex = 0;
}

function copyToInput(sender) {
    var trs = sender.parentNode.getElementsByTagName("input");
    for (var i = 0; i < trs.length; i++) {
        trs[i].value = sender.options[sender.selectedIndex].text;
    }
}

// for autoselect checkboxes
function selectAllCheckboxes(sender, group) {
    var col = sender.form.elements;
    for (var i = 0; i < col.length; i++) {
        if (col[i].type == "checkbox" && (group == null || col[i].getAttribute("quickCheckboxSelectGroup") == group || col[i].getAttribute("alt") == group) && !col[i].disabled) {
            col[i].checked = sender.checked;

        }
    }
}
function checkradio(Sender) {
    if (Sender.checked == false) {
        ;
        Sender.checked = false;
    }
    else {
        var col = document.forms["cform"].elements[Sender.name];
        for (var i = 0; i < col.length; i++) {
            col[i].checked = false;
        }
        Sender.checked = true;
    }
}
function checkRequiedRadio(Sender, ids) {
    if (Sender.checked == true) {
        if (!ids)
            ids = 'radio';
        //var col = document.forms["cform"].elements[Sender.name];
        var col = document.getElementsByTagName("input");
        for (var i = 0; i < col.length; i++)
            if (col[i].type == 'checkbox' && col[i].id == ids)
                col[i].checked = false;
    }
    Sender.checked = true;
}
function changeTransitionsCheckBox(Sender) { // use on editTransitions
    if (Sender.checked == false) {
        Sender.checked = false;
    } else {
        var col = Sender.parentNode.parentNode.parentNode.getElementsByTagName("input");
        for (var i = 0; i < col.length; i++)
            col[i].checked = false;
        Sender.checked = true;
    }
}

// for autoselect checkboxes at edit statuses page
function treesetup(sender) {
    sender.checked = true;
    var div = document.getElementById("parentDivId" + sender.id);
    var parent = div.getAttribute("parent");
    if (parent != "0") {
        treesetup(document.getElementById(parent));
    }
}
function treesetdown(sender) {
    var parents = new Array();
    parents[0] = sender.id;
    var col = sender.parentNode.parentNode.getElementsByTagName("input");
    for (var i = 0; i < col.length; i++) {
        if (col[i].type == "checkbox") {
            if (sender.id == "1") {
                col[i].checked = false;
                parents[parents.length] = col[i].id;
                continue;
            }
            for (var j = 0; j < parents.length; j ++) {
                var div = document.getElementById("parentDivId" + col[i].id);
                if (div.getAttribute("parent") == parents[j]) {
                    col[i].checked = false;
                    if (div.getAttribute("nochilds") != "true")
                        parents[parents.length] = col[i].id;
                    break;
                }
            }
        }
    }
}
function treeset(sender) {
    if (sender.checked) {
        treesetup(sender);
    } else {
        treesetdown(sender);
    }
}

// use on Filter page (filter sort level counter)
function sortorderAction(Sender) {
    var id = Sender.value;
    var ne = false;
    if (id.charAt(0) == '_') {
        id = id.substring(1);
        ne = true;
    }

    if (Sender.checked == true) {
        var element = Sender.parentNode.parentNode;
        var col = element.getElementsByTagName("input");
        var add = true;
        for (var i = 0; i < col.length; i++) {
            if (col[i].type == "checkbox" && col[i].value == (ne ? id : "_" + id) && col[i].checked == true) {
                col[i].checked = false;
                add = false;
            }
        }
        if (add)
            levelArray[levelArray.length] = id;
    } else {
        for (var counter = 0; counter < levelArray.length; counter++) {
            if (levelArray[counter] == id) {
                var tmpArray1 = levelArray.slice(0, counter);
                var tmpArray2 = levelArray.slice(counter + 1);
                levelArray = tmpArray1.concat(tmpArray2);
                break;
            }
        }
    }

    for (var counter = 0; counter < levelArray.length; counter++) {
        document.getElementById(levelArray[counter]).innerHTML = counter + 1;
        document.getElementById("h" + levelArray[counter]).value = counter + 1;
    }
    if (Sender.checked == false) {
        document.getElementById(id).innerHTML = "";
        document.getElementById("h" + id).value = 0;
    }
}

// disable/enable controls elements on filterEdit page
function switchByControl(Sender) {
    var element = Sender.parentNode.parentNode;
    if (Sender.checked == true) {
        var col = element.getElementsByTagName("input");
        for (var i = 0; i < col.length; i++)
            if (col[i].getAttribute("alt") != "true" && (col[i].type != "checkbox" || col[i].name == "sortorder" || col[i].name == "hide")) {
                col[i].disabled = false;
                if (col[i].type != "checkbox")
                    col[i].style.backgroundColor = "white";
            }
        col = element.getElementsByTagName("select");
        for (var i = 0; i < col.length; i++) {
            col[i].disabled = false;
            var opt = col[i].getElementsByTagName("option");
            for (var j = 0; j < opt.length; j++) {
                if (opt[j].value == "0")
                    opt[j].selected = true;
                else
                    opt[j].selected = false;
            }
        }
    } else {
        var col = element.getElementsByTagName("input");
        for (var i = 0; i < col.length; i++)
            if (col[i].type != "checkbox" || col[i].name == "sortorder" || col[i].name == "hide") {
                col[i].disabled = true;
                if (col[i].type != "checkbox")
                    col[i].style.backgroundColor = "#d7d7d7";
            }
        col = element.getElementsByTagName("select");
        for (var i = 0; i < col.length; i++) {
            var opt = col[i].getElementsByTagName("option");
            for (var j = 0; j < opt.length; j++)
                opt[j].selected = false;
            col[i].disabled = true;
        }
    }
}

// disable/enable controls elements on messageAddForm
function disableControls(Sender) {
    var element = Sender.parentNode.parentNode.parentNode.parentNode;
    var col = element.getElementsByTagName("input");
    is_new = true;
    for (var i = 0; i < col.length; i++)
        if (col[i].type != "radio") {
            col[i].disabled = true;
            col[i].style.backgroundColor = "#d7d7d7";
        }
    col = element.getElementsByTagName("select");
    for (var i = 0; i < col.length; i++)
        col[i].disabled = true;
    element = Sender.parentNode.parentNode.parentNode;
    col = element.getElementsByTagName("input");
    for (var i = 0; i < col.length; i++)
        if (col[i].type != "radio") {
            col[i].disabled = false;
            col[i].style.backgroundColor = "white";
        }
    col = element.getElementsByTagName("select");
    for (var i = 0; i < col.length; i++)
        col[i].disabled = false;
}

// write input on CustomEdit page
//mozilla don't like innerHTML!!!
function writeInput(sel, pattern) {
    var udfDefaultValue = document.getElementById("udfDefaultValue");
    var calendarIcon = document.getElementById("calendarIcon");
    var listOfValues = document.getElementById("listOfValues");
    var type;
    for (var i = 0; i < sel.options.length; i++) {
        if (sel.options[i].selected) {
            type = parseInt(sel.options[i].value);
            break;
        }
    }
    switch (type) {
        case 0: // String
            calendarIcon.style.display = "none";
            udfDefaultValue.size = 20;

            udfDefaultValue.alt = "";
            udfDefaultValue.value = "";
            udfDefaultValue.maxlength = "1800";
            udfDefaultValue.disabled = false;
            udfDefaultValue.style.display = "";
            listOfValues.style.display = "none";
            listOfValues.alt = "";

            break;
        case 1: // Float
            calendarIcon.style.display = "none";
            udfDefaultValue.size = 20;

            udfDefaultValue.alt = "float";
            udfDefaultValue.maxlength = "40";
            udfDefaultValue.val = "";
            udfDefaultValue.disabled = false;
            udfDefaultValue.style.display = "";
            listOfValues.style.display = "none";
            listOfValues.alt = "";

            break;
        case 2: // Date
            calendarIcon.style.display = "";
            udfDefaultValue.size = 18;
            udfDefaultValue.value = "";
            udfDefaultValue.alt = "date(" + pattern + ")";
            udfDefaultValue.maxlength = "40";
            udfDefaultValue.disabled = false;
            udfDefaultValue.style.display = "";
            listOfValues.style.display = "none";
            listOfValues.alt = "";

            break;
        case 3:
            calendarIcon.style.display = "none";
            udfDefaultValue.size = 20;
            udfDefaultValue.style.display = "none";
            udfDefaultValue.value = "";
            udfDefaultValue.disabled = true;
            listOfValues.style.display = "";
            listOfValues.alt = ">0";

            break;
        case 4: // Integer
            calendarIcon.style.display = "none";
            udfDefaultValue.size = 20;

            udfDefaultValue.alt = "integer";
            udfDefaultValue.maxlength = "40";
            udfDefaultValue.value = "";
            udfDefaultValue.disabled = false;
            udfDefaultValue.style.display = "";
            listOfValues.style.display = "none";
            listOfValues.alt = "";

            break;
        case 5: // Memo
            calendarIcon.style.display = "none";
            udfDefaultValue.size = 20;
            udfDefaultValue.alt = "";
            udfDefaultValue.maxlength = "1940";
            udfDefaultValue.disabled = false;
            udfDefaultValue.style.display = "";
            udfDefaultValue.value = "";
            listOfValues.style.display = "none";
            listOfValues.alt = "";
            break;
        case 6: // MULTILIST
            calendarIcon.style.display = "none";
            udfDefaultValue.size = 20;
            udfDefaultValue.style.display = "none";
            udfDefaultValue.disabled = true;
            listOfValues.style.display = "";
            listOfValues.alt = ">0";
            break;
        case 7: // TASK
            calendarIcon.style.display = "none";
            udfDefaultValue.size = 20;
            udfDefaultValue.disabled = false;
            udfDefaultValue.value = "";
            udfDefaultValue.style.display = "none";
            listOfValues.style.display = "none";
            listOfValues.alt = "";

            break;
        case 8: // USER
            calendarIcon.style.display = "none";
            udfDefaultValue.size = 20;
            udfDefaultValue.disabled = false;
            udfDefaultValue.style.display = "none";
            udfDefaultValue.value = "";
            listOfValues.style.display = "none";
            listOfValues.alt = "";
            break;
        case 9: // URL
            calendarIcon.style.display = "none";
            udfDefaultValue.size = 20;
            udfDefaultValue.alt = "url";
            udfDefaultValue.maxlength = "1800";
            udfDefaultValue.val = "";
            udfDefaultValue.disabled = false;
            udfDefaultValue.style.display = "";
            listOfValues.style.display = "none";
            listOfValues.alt = "";
            break;

    }
}

function changeColorSample(el) {
    if (!colorValid(el.value)) {
        disValid(el, "Must be a color value in format \"#dddddd\". ");
    } else {
        el.title = "";
        el.style.backgroundColor = "";
        el.parentNode.getElementsByTagName("span")[0].style.backgroundColor = el.value;
    }
    return true;
}

function fullDescription(show) {
    if (show) {
        document.getElementById("showDescription").style.display = "none";
        document.getElementById("hideDescription").style.display = "";
        document.getElementById("shortDescriptionDiv").style.height = "auto";
        ;
    } else {
        document.getElementById("hideDescription").style.display = "none";
        document.getElementById("showDescription").style.display = "";
        document.getElementById("shortDescriptionDiv").style.height = "60px";
    }
}

function isChecked(frm) {
    try {
        var col = frm.elements;
        for (var i = 0; i < col.length; i++) {
            if (col[i].type == "checkbox" && (col[i].getAttribute("alt") == "delete1") && !col[i].disabled)
                if (col[i].checked) return true;
        }
    } catch(ex) {
        return true;
    }
    return false;
}

function deleteConfirm(msg, formName) {
    var needConfirm = false;
    try {
        var col = document.forms[formName].elements;
        for (var i = 0; i < col.length; i++) {
            if ((col[i].type == "checkbox") && (col[i].getAttribute("alt") == "delete1") && !col[i].disabled && col[i].checked) {
                needConfirm = true;
                break;
            }
        }
    } catch(ex) {
        needConfirm = true;
    }
    if (needConfirm) {
        TSDialog.confirm(msg, function(ok) {
            if (ok) document.forms[formName].submit();
        });
    }
    return false;
}

function deleteCheck(parentId, attachmentId, method, msg, taskOrUser, attaDiv) {
    TSDialog.confirm(msg, function(ok) {
        if (!ok) return;
        var link = contextPath + "/AttachmentViewAction.do";
        var params;
        if (taskOrUser) {
            params = {taskId : parentId, method : method, "delete" : attachmentId};
        } else {
            params = {userId : parentId, method : method, "delete" : attachmentId};
        }
        $.ajax(link, {
            data : params,
            method: "post"
        });
        var tr = document.getElementById(attachmentId);
        tr.remove();
        --countAtt;
        if (countAtt <= 0) {
            document.getElementById(attaDiv).remove();
        }
    });
}

function deleteConfirmForCurrentForm(msg, form) {
    var needConfirm = false;
    try {
        var col = form.elements;
        for (var i = 0; i < col.length; i++) {
            if ((col[i].type == "checkbox") && (col[i].getAttribute("alt") == "delete1") && !col[i].disabled && col[i].checked) {
                needConfirm = true;
                break;
            }
        }
    } catch(ex) {
        needConfirm = true;
    }
    if (needConfirm) {
        TSDialog.confirm(msg, function(ok) {
            if (ok) form.submit();
        });
    }
    return false;
}

function deleteDefaultAlertForCurrentForm(msg, form, defaultResolutionId) {
    try {
        var col = form.elements;
        var allCheckboxCount = 0;
        var allCheckedCheckboxCount = 0;
        for (var i = 0; i < col.length; i++) {
            if ((col[i].type == "checkbox") && (col[i].getAttribute("alt") == "delete1") && !col[i].disabled)
                allCheckboxCount++;
        }
        for (var i = 0; i < col.length; i++) {
            if ((col[i].type == "checkbox") && (col[i].getAttribute("alt") == "delete1") && !col[i].disabled && col[i].checked)
                allCheckedCheckboxCount++;
        }
        if (allCheckedCheckboxCount == allCheckboxCount) return false;
        for (var i = 0; i < col.length; i++) {
            if ((col[i].value == defaultResolutionId) && (col[i].type == "checkbox") && (col[i].getAttribute("alt") == "delete1") && !col[i].disabled && col[i].checked) {
                TSDialog.alert(msg, function() { form.submit(); });
                return false;
            }
        }
    } catch(ex) {
        return false;
    }
    return false;
}

function deleteDefaultAlert(msg, formName, defaultPriorityId) {
    try {
        var col = document.forms[formName].elements;
        var allCheckboxCount = 0;
        var allCheckedCheckboxCount = 0;
        for (var i = 0; i < col.length; i++) {
            if ((col[i].type == "checkbox") && (col[i].getAttribute("alt") == "delete1") && !col[i].disabled)
                allCheckboxCount++;
        }
        for (var i = 0; i < col.length; i++) {
            if ((col[i].type == "checkbox") && (col[i].getAttribute("alt") == "delete1") && !col[i].disabled && col[i].checked)
                allCheckedCheckboxCount++;
        }
        if (allCheckedCheckboxCount == allCheckboxCount) return false;
        for (var i = 0; i < col.length; i++) {
            if ((col[i].value == defaultPriorityId) && (col[i].type == "checkbox") && (col[i].getAttribute("alt") == "delete1") && !col[i].disabled && col[i].checked) {
                TSDialog.alert(msg, function() { document.forms[formName].submit(); });
                return false;
            }
        }
    } catch(ex) {
        return false;
    }
    return false;
}

function startStateAlert(msg, formName, startStateId) {
    try {
        var col = document.forms[formName].elements;
        for (var i = 0; i < col.length; i++) {
            if ((col[i].value == startStateId) && (col[i].type == "checkbox") && (col[i].getAttribute("alt") == "delete1") && !col[i].disabled && col[i].checked) {
                TSDialog.alert(msg, function() { document.forms[formName].submit(); });
                return false;
            }
        }
    } catch(ex) {
        return false;
    }
    return false;
}

function lookCalcEn(sender, lookCacl) {

    var sel, chb, calc, sel2, calc2;
    if (lookCacl) {
        sel = sender.form.elements['lscript'];
        chb = sender.form.elements['lookuponly'];
        calc = sender.form.elements['calculen'];
        sel2 = sender.form.elements['script'];
        calc2 = sender.form.elements['cachevalues'];
        chb.disabled = false;
    } else {
        sel = sender.form.elements['script'];
        chb = sender.form.elements['cachevalues'];
        calc = sender.form.elements['lookupen'];
        sel2 = sender.form.elements['lscript'];
        calc2 = sender.form.elements['lookuponly'];
        chb.disabled = false;
    }

    if (calc != null)
        if (calc.value == "on") {
            calc.checked = false;
            if (sel2)
                sel2.disabled = true;
            if (calc2 != null)
                calc2.disabled = true;
        }
}

function refreshPrstQuick(sender, selected) {
    var prstSelect = document.getElementById('prstatusComboId');
    if (prstSelect) {
        for (var i = 0; i < prstSelect.options.length; i++) {
            prstSelect.options[i] = null;
        }
        for (var i = 0; i < idArr[sender.value].length; i++) {
            prstSelect.options[i] = new Option(nameArr[sender.value][i], idArr[sender.value][i]);
            if (selected != null && selected == idArr[sender.value][i])
                prstSelect.selectedIndex = i;
        }
        if (prstSelect.options.length != 0) {
            document.getElementById('prstatusAvailableId').style.display = "";
            document.getElementById('noPrstatusId').style.display = "none";
        } else {
            document.getElementById('prstatusAvailableId').style.display = "none";
            document.getElementById('noPrstatusId').style.display = "";
        }
    }
}

function changeFormat(sender) {
    var form = document.getElementById("exportFormId");
    var tr = document.getElementById('trForHideId');
    var elem = document.getElementById('filter');
    if (sender.options[sender.selectedIndex].id == "zip")
        form.target = "";
    else
        form.target = "_blank";

    if (sender.value == "TreeXML" || sender.value == "MS Project") {
        tr.style.display = 'none';
        elem.style.display = 'none';
    } else {
        tr.style.display = '';
        elem.style.display = '';
    }
}

function clonConfirm(msg, formName) {
    var needConfirm = false;
    try {
        var col = document.forms[formName].elements;
        for (var i = 0; i < col.length; i++) {
            if ((col[i].type == "checkbox") && (col[i].getAttribute("alt") == "delete1") && !col[i].disabled && col[i].checked) {
                needConfirm = true;
                break;
            }
        }
    } catch(ex) {
        needConfirm = true;
    }
    if (needConfirm) {
        TSDialog.confirm(msg, function(ok) {
            if (ok) document.forms[formName].submit();
        });
    }
    return false;
}
