Function.prototype.bind = function(object) {
	var __method = this;
	return function() {
		__method.apply(object, arguments);
	}
}

AjaxForm = function() {
};

AjaxForm.prototype = {
	replaceContent: function() {
		if (this.http_request.readyState == 4) {
			if (this.http_request.status == 200) {
				result = this.http_request.responseText;
				this.replaced.parentNode.innerHTML = result;
			} else {
				alert(ERROR_PROBLEM_WITH_REQUEST + this.http_request.status);
			}
		}
	},

	makePOSTRequest: function() {
		this.http_request = false;
		if (window.XMLHttpRequest) { // Mozilla, Safari,...
			this.http_request = new XMLHttpRequest();
			if (this.http_request.overrideMimeType) {
				//set type accordingly to anticipated content type
				//http_request.overrideMimeType('text/xml');
				this.http_request.overrideMimeType('text/html');
			}
		} else if (window.ActiveXObject) { // IE
			try {
				this.http_request = new ActiveXObject("Msxml2.XMLHTTP");
			} catch (e) {
				try {
					this.http_request = new ActiveXObject("Microsoft.XMLHTTP");
				} catch (e) {
				}
			}
		}
		if (!this.http_request) {

			alert(ERROR_CANT_CREATE_XMLHTTP);
			return false;
		}

		this.http_request.onreadystatechange = this.replaceContent.bind(this);
		this.http_request.open('POST', this.url, true);
		this.http_request.setRequestHeader("Content-type", "application/x-www-form-urlencoded");
		this.http_request.setRequestHeader("Content-length", this.parameters.length);
		this.http_request.setRequestHeader("Connection", "close");
		this.http_request.send(this.parameters);

	},

	reset: function (obj) {
		var poststr = "";
		for (var i = 0; i < obj.elements.length; i++) {
			if (obj.elements[i].type != "button" && obj.elements[i].type != "submit") {
				if (obj.elements[i].type == "select" || obj.elements[i].type == "select-multiple") {
					for (var j = 0; j < obj.elements[i].options.length; j++) {
						if (obj.elements[i].options[j].selected) {
							poststr += obj.elements[i].name + "=" + encodeURI(obj.elements[i].options[j].value) + "&";
						}
					}
				} else {
					poststr += obj.elements[i].name + "=" + encodeURI(obj.elements[i].value) + "&";
				}
			}
		}
		this.parameters = poststr;
		this.url = obj.action;
		this.replaced = obj;
		this.makePOSTRequest();
		return false;
	},

	submit: function (obj, deleteElement) {
		var poststr = "";
		for (var i = 0; i < obj.elements.length; i++) {
			if (obj.elements[i].type != "button" && obj.elements[i].type != "submit" && obj.elements[i].type != "reset") {
				if (obj.elements[i].type == "select" || obj.elements[i].type == "select-multiple") {
					for (var j = 0; j < obj.elements[i].options.length; j++) {
						if (obj.elements[i].options[j].selected) {
							poststr += obj.elements[i].name + "=" + encodeURI(obj.elements[i].options[j].value) + "&";
						}
					}
				} else {
					if (obj.elements[i].alt != null && obj.elements[i].alt.length != 0 && obj.elements[i].alt.indexOf("listcheckbox") != -1) {
						if (obj.elements[i].checked) {
							poststr += obj.elements[i].name + "=" + encodeURI(obj.elements[i].value) + "&";
						}
					} else if (obj.elements[i].type == "checkbox") {
						poststr += obj.elements[i].name + "=" + obj.elements[i].checked + "&";
					} else {
						if (encodeURI(obj.elements[i].value).length != 0) {
							poststr += obj.elements[i].name + "=" + encodeURI(obj.elements[i].value) + "&";
						}
					}
				}
			}
		}

		this.parameters = poststr + deleteElement;
		this.url = obj.action;
		this.replaced = obj;
		this.makePOSTRequest();
		return false;
	}
}

var valid = true;

function hideLoad(Sender) {
	document.getElementById("l_" + Sender).style.display = 'block';
	labelShow(document.getElementById("l_" + Sender));
}

function copyToClipboard(copyText) {
	if (window.clipboardData) { // IE send-to-clipboard method.
		window.clipboardData.setData('Text', copyText);

	} else if (navigator.product == "Gecko") {

		// You have to sign the code to enable this or allow the action in about:config by changing user_pref("signed.applets.codebase_principal_support", true);
		try {
			netscape.security.PrivilegeManager.enablePrivilege('UniversalXPConnect');
		} catch (e) {

			alert(MOZILLA_PRINCIPAL);
		}

		// Store support string in an object.
		var str = Components.classes["@mozilla.org/supports-string;1"].createInstance(Components.interfaces.nsISupportsString);
		if (!str) return false;

		str.data = copyText;

		// Make transferable.
		var trans = Components.classes["@mozilla.org/widget/transferable;1"].createInstance(Components.interfaces.nsITransferable);
		if (!trans) return false;

		// Specify what datatypes we want to obtain, which is text in this case.
		trans.addDataFlavor("text/unicode");
		trans.setTransferData("text/unicode", str, copyText.length * 2);

		var clipid = Components.interfaces.nsIClipboard;
		var clip = Components.classes["@mozilla.org/widget/clipboard;1"].getService(clipid);
		if (!clip) return false;

		clip.setData(trans, null, clipid.kGlobalClipboard);
	}
}

function appendOrRemove(oldValue, copyText) {
	if (oldValue != null && oldValue.length > 0) {
		if (oldValue == copyText) {
			oldValue = "";
		}
		else if (oldValue.indexOf(copyText + ';') > -1) {
			var newValue = oldValue;
			var j = newValue.indexOf(copyText + ';');
			oldValue = newValue.substring(0, j) + newValue.substring(j + copyText.length + 1);
		} else if (oldValue.indexOf(';' + copyText) > -1) {
			var newValue = oldValue;
			var j = newValue.indexOf(';' + copyText);
			oldValue = newValue.substring(0, j) + newValue.substring(j + copyText.length + 1);
		}
		else {
			oldValue += ';' + copyText;
		}
	} else {
		oldValue = copyText;
	}
	return oldValue;
}

function appendClipboard(copyText) {
	// ; is separator
	if (window.clipboardData) { // IE send-to-clipboard method.
		var oldValue = window.clipboardData.getData('Text');

		oldValue = appendOrRemove(oldValue, copyText);

		window.clipboardData.setData('Text', oldValue);
	} else if (navigator.product == "Gecko") {
		// You have to sign the code to enable this or allow the action in about:config by changing user_pref("signed.applets.codebase_principal_support", true);
		var oldValue = getMozillaClipboardContent();
		oldValue = appendOrRemove(oldValue, copyText);
		try {
			netscape.security.PrivilegeManager.enablePrivilege('UniversalXPConnect');
		} catch (e) {
			alert(MOZILLA_PRINCIPAL);
		}

		// Store support string in an object.
		var str = Components.classes["@mozilla.org/supports-string;1"].createInstance(Components.interfaces.nsISupportsString);
		if (!str) return false;

		str.data = oldValue;

		// Make transferable.
		var trans = Components.classes["@mozilla.org/widget/transferable;1"].createInstance(Components.interfaces.nsITransferable);
		if (!trans) return false;

		// Specify what datatypes we want to obtain, which is text in this case.
		trans.addDataFlavor("text/unicode");
		trans.setTransferData("text/unicode", str, oldValue.length * 2);

		var clipid = Components.interfaces.nsIClipboard;
		var clip = Components.classes["@mozilla.org/widget/clipboard;1"].getService(clipid);
		if (!clip) return false;

		clip.setData(trans, null, clipid.kGlobalClipboard);
	}
}

function getMozillaClipboardContent() {
	try {
		netscape.security.PrivilegeManager.enablePrivilege('UniversalXPConnect');
	} catch (e) {
		alert(MOZILLA_PRINCIPAL);
	}
	var clip = Components.classes["@mozilla.org/widget/clipboard;1"].getService(Components.interfaces.nsIClipboard);
	if (!clip) return "";
	var trans = Components.classes["@mozilla.org/widget/transferable;1"].createInstance(Components.interfaces.nsITransferable);
	if (!trans) return "";
	trans.addDataFlavor("text/unicode");
	clip.getData(trans, clip.kGlobalClipboard);
	var str = new Object();
	var strLength = new Object();
	trans.getTransferData("text/unicode", str, strLength);
	if (str) str = str.value.QueryInterface(Components.interfaces.nsISupportsString);
	if (str) pastetext = str.data.substring(0, strLength.value / 2);
	return pastetext;
}

function changeClass(SourceId) {
	var Link = document.getElementById("contentLink" + SourceId);
	var Place = document.getElementById("contentPlace" + SourceId);
	var Frm = document.getElementById("msgForm" + SourceId);
	if (Link.style.display == 'inline') {
		Link.style.display = 'none';
		Place.style.display = 'inline';
		Frm.style.display = 'block';
	}
	else {
		Link.style.display = 'inline';
		Place.style.display = 'none';
		Frm.style.display = 'none';
	}
}

function labelShow(Sender) {
	document.getElementById(Sender.htmlFor).style.display = 'block';
	Sender.className = "expandable opened";
}

function messageShow(Sender) {
	document.getElementById(Sender.htmlFor).style.display = 'block';
	var trs = Sender.getElementsByTagName("a");
	if (trs.length >= 2) {
		trs[0].style.display = 'none';
		trs[1].style.display = 'inline';
	}
	var tr = Sender.parentNode.parentNode;
	tr.className = 'vis';
	var trs2 = tr.getElementsByTagName("td")[0].getElementsByTagName("div");
	if (trs.length >= 2) {
		trs2[0].style.display = 'none';
		trs2[1].style.display = 'block';
	}
}

function propertyShow(Sender) {
	document.getElementById(Sender.htmlFor).style.display = 'block';
	var trs = Sender.getElementsByTagName("a");
	trs[0].style.display = 'none';
	trs[1].style.display = 'inline';
	var tr = Sender.parentNode.parentNode;
	tr.className = 'vis';

	var trs2 = tr.getElementsByTagName("td");
	for (i = 0; i < trs2.length; i++) {
		var span = trs2[i].getElementsByTagName("em");
		if (span.length > 0) {
			span[0].className = "mopened";
			if (span.length > 1) {
				span[1].className = "mclosed";
			}
		}
	}
}

function messageHide(Sender) {
	document.getElementById(Sender.htmlFor).style.display = 'none';
	var trs = Sender.getElementsByTagName("a");
	trs[0].style.display = 'inline';
	trs[1].style.display = 'none';
	var tr = Sender.parentNode.parentNode;
	tr.className = '';
	var trs2 = tr.getElementsByTagName("td")[0].getElementsByTagName("div");
	trs2[0].style.display = 'block';
	trs2[1].style.display = 'none';
}

function propertyHide(Sender) {
	document.getElementById(Sender.htmlFor).style.display = 'none';
	var trs = Sender.getElementsByTagName("a");
	trs[0].style.display = 'inline';
	trs[1].style.display = 'none';
	var tr = Sender.parentNode.parentNode;
	tr.className = '';
	var trs2 = tr.getElementsByTagName("td");
	for (var i = 0; i < trs2.length; i++) {
		var span = trs2[i].getElementsByTagName("em");
		if (span.length > 0) {
			span[0].className = "mclosed";
			if (span.length > 1) {
				span[1].className = "mopened";
			}
		}
	}
}

function labelHide(Sender) {
	if (Sender.htmlFor != "")
		document.getElementById(Sender.htmlFor).style.display = 'none';
	Sender.className = "expandable closed";
}

function showHide(Sender) {
	if (document.getElementById(Sender.htmlFor).style.display == 'none') {
		var trs = Sender.parentNode.getElementsByTagName("label");
		for (var i = 0; i < trs.length; i++) labelHide(trs[i]);
		labelShow(Sender);
		setTabCookie(top.document.title, Sender.htmlFor, null, null, null, null);
	} else {
		labelHide(Sender);
		if (getTabCookie(encodeURI(top.document.title)) == Sender.htmlFor) {
			setTabCookie(top.document.title, "none", null, null, null, null);
		}
	}
}

function switchPanel(Sender) {
	if (document.getElementById(Sender.htmlFor).style.display == 'none') {
		document.getElementById(Sender.htmlFor).style.display = 'block';
		setTabCookie(Sender.htmlFor, Sender.htmlFor, null, null, null, null);
	} else {
		document.getElementById(Sender.htmlFor).style.display = 'none';
		setTabCookie(Sender.htmlFor, "false", null, null, null, null);
	}
}

function showHideProperty(Sender) {
	if (document.getElementById(Sender.htmlFor).style.display == 'none') {
		propertyShow(Sender);
		setTabCookie(Sender.htmlFor, Sender.htmlFor, null, null, null, null);
	} else {
		propertyHide(Sender);
		setTabCookie(Sender.htmlFor, "false", null, null, null, null);
	}
}

function openMessage(Sender) {
	var labels;
	if (Sender.className == 'history-opened') {
		Sender.className = 'history-closed';
		labels = Sender.parentNode.parentNode.getElementsByTagName("label");
		for (var i = 0; i < labels.length; i++) {
			if (labels[i].parentNode.className.indexOf("msgbox-opened") > -1) labels[i].parentNode.className = 'msgbox-closed';
		}
	}
	else {
		Sender.className = 'history-opened';
		labels = Sender.parentNode.parentNode.getElementsByTagName("label");
		for (var j = 0; j < labels.length; j++) {
			if (labels[j].parentNode.className.indexOf("msgbox-closed") > -1) labels[j].parentNode.className = 'msgbox-opened';
		}
	}
}

function openSubtasksMessage() {
	var msg = document.getElementsByClassName("messagelabel");
	for (var i=0;i!=msg.length;++i) {
		if (msg[i].id.indexOf("label") == -1) {
			msgSwitch(msg[i]);
		}
	}
}

function msgSwitch(Sender) {
	var t = document.getElementById(Sender.htmlFor);
	var color;
	var label = document.getElementById('label' + Sender.htmlFor);
	if (t.className == 'msgbox-opened') {
		color = '#666';
		t.className = 'msgbox-closed';
	} else {
		color = '#000000';
		t.className = 'msgbox-opened';
	}
	label.style.color = color;
	setParentClosedOpenedForMessageHistory();
}

function setParentClosedOpenedForMessageHistory() {
	var tempElement;
	var allMessageHistoryChildsOpened = true;
	var allMessageHistoryChildsClosed = true;
	var msgHistory = document.getElementById("messageHistory");
	if (msgHistory != null) {
		var divElem = msgHistory.getElementsByTagName("div");
		if (divElem != null) {
			for (var j = 0; j < divElem.length; j++) {
				tempElement = divElem[j];
				if (tempElement != null && tempElement.className == "msgbox-closed") allMessageHistoryChildsOpened = false;
				if (tempElement != null && tempElement.className == "msgbox-opened") allMessageHistoryChildsClosed = false;
			}
		}
	}

	var msgHistoryHeader = document.getElementById("messageHistoryHeader");
	if (msgHistoryHeader != null) {
		if (allMessageHistoryChildsOpened) msgHistoryHeader.className = "history-opened";
		if (allMessageHistoryChildsClosed) msgHistoryHeader.className = "history-closed";
	}
}

function openProperty(Sender) {
	var labels;
	if (Sender.htmlFor == "") {
		var trs = Sender.getElementsByTagName("a");
		if (trs[0].style.display == 'none') {
			trs[0].style.display = 'inline';
			trs[1].style.display = 'none';
			labels = Sender.parentNode.parentNode.getElementsByTagName("label");
			for (var i = 0; i < labels.length; i++) {
				if (labels[i].className.indexOf("openProperty") > -1) {
					propertyHide(labels[i]);
					setTabCookie(labels[i].htmlFor, "false", null, null, null, null);
				}
			}
		} else {
			trs[0].style.display = 'none';
			trs[1].style.display = 'inline';
			labels = Sender.parentNode.parentNode.getElementsByTagName("label");
			for (var j = 0; j < labels.length; j++) {
				if (labels[j].className.indexOf("openProperty") > -1) {
					propertyShow(labels[j]);
					setTabCookie(labels[j].htmlFor, labels[j].htmlFor, null, null, null, null);
				}
			}
		}
	}
	else {
		if (document.getElementById(Sender.htmlFor).style.display == 'none') {
			propertyShow(Sender);
		} else propertyHide(Sender);
	}
}

function openClose(Sender) {
	if (document.getElementById(Sender.htmlFor).style.display == 'none') {
		labelShow(Sender);
	} else {
		labelHide(Sender);
	}
}

function setTabCookie(name, value, expires, path, domain, secure) {
	try {

		if (value.length >= 3000) return false;
		else {
			var curCookie = name + "=" + encodeURI(value) +
			                ((expires) ? "; expires=" + expires.toGMTString() : "") +
			                ((path) ? "; path=/" : "; path=/") +
			                ((domain) ? "; domain=" + domain : "") +
			                "; version=1" +
			                ((secure) ? "; secure" : "");
			document.cookie = curCookie;
			return true;
		}
	} catch(err) {
		showError("setTabCookie", err);
	}

}

/*
 name - name of the desired cookie
 return string containing value of specified cookie or null
 if cookie does not exist
 */

function getLabelsByClassName(className) {
	var children = document.getElementsByTagName('label');
	var elements = new Array();
	for (var i = 0; i < children.length; i++) {
		var child = children[i];
		if (child.className.indexOf(className) > 0) {
			elements.push(child);
		}
	}
	return elements;
}

function getTabCookie(name) {
	var dc = parent.window.document.cookie;

	var prefix = name + "=";
	var begin = dc.indexOf("; " + prefix);

	if (begin == -1) {
		begin = dc.indexOf(prefix);

		if (begin != 0) return null;
	} else
		begin += 2;
	var end = dc.indexOf(";", begin);
	if (end == -1)
		end = dc.length;

	return decodeURI(dc.substring(begin + prefix.length, end));
}

/*
 name - name of the cookie
 [path] - path of the cookie (must be same as path used to create cookie)
 [domain] - domain of the cookie (must be same as domain used to
 create cookie)
 path and domain default if assigned null or omitted if no explicit
 argument proceeds
 */

function deleteTabCookie(name, path, domain) {
	setTabCookie(name, "none", null, path, domain);
}

// date - any instance of the Date object
// * hand all instances of the Date object to this function for "repairs"

function fixDate(date) {
	var base = new Date(0);
	var skew = base.getTime();
	if (skew > 0)
		date.setTime(date.getTime() - skew);
}

function trim_spaces() {
	var temp_string = this;
	while (temp_string.substring(0, 1) == " ")
		temp_string = temp_string.substring(1);
	while (temp_string.substring(temp_string.length - 1) == " ")
		temp_string = temp_string.substring(0, temp_string.length - 2);
	return temp_string;
}

String.prototype.trim = trim_spaces;

function isEmail(who) {
	function isEmpty(who) {
		var testArr = who.split("");
		if (testArr.length == 0)
			return true;
		var toggle = 0;
		for (var i = 0; i < testArr.length; i++) {
			if (testArr[i] == " ") {
				toggle = 1;
				break;
			}
		}
		return toggle;
	}

	function isValid(who) {
		var invalidChars = new Array("~", "!", "@", "#", "$", "%", "^", "&", "*", "(", ")", "+", "=", "[", "]", ":", ";", ",", "\"", "'", "|", "{", "}", "\\", "/", "<", ">", "?");
		var testArr = who.split("");
		for (var i = 0; i < testArr.length; i++) {
			for (var j = 0; j < invalidChars.length; j++) {
				if (testArr[i] == invalidChars[j]) {
					return false;
				}
			}
		}
		return true;
	}

	function isfl(who) {
		var invalidChars = new Array("-", "_", ".");
		var testArr = who.split("");
		var which = 0;
		for (var i = 0; i < 2; i++) {
			for (var j = 0; j < invalidChars.length; j++) {
				if (testArr[which] == invalidChars[j]) {
					return false;
				}
			}
			which = testArr.length - 1;
		}
		return true;
	}

	function isDomain(who) {
		var invalidChars = new Array("-", "_", ".");
		var testArr = who.split("");
		if (testArr.length < 2 || testArr.length > 4) {
			return false;
		}
		for (var i = 0; i < testArr.length; i++) {
			for (var j = 0; j < invalidChars.length; j++) {
				if (testArr[i] == invalidChars[j]) {
					return false;
				}
			}
		}
		return true;
	}

	var testArr = who.split("@");
	if (testArr.length <= 1 || testArr.length > 2) return false;
	else {
		if (isValid(testArr[0]) && isfl(testArr[0]) && isValid(testArr[1])) {
			if (!isEmpty(testArr[testArr.length - 1]) && !isEmpty(testArr[0])) {
				var testArr2 = testArr[testArr.length - 1].split(".");
				if (testArr2.length >= 2) {
					var toggle = 1;
					for (var i = 0; i < testArr2.length; i++) {
						if (isEmpty(testArr2[i]) || !isfl(testArr2[i])) {
							toggle = 0;
							break;
						}
					}
					return toggle && isDomain(testArr2[testArr2.length - 1]);
				}
				return false;
			}
		}
	}
}

/* With RegExp */
function isEmail2(who) {
	var email = /^[A-Za-z0-9]+([_\.\\'\\`-][A-Za-z0-9]+)*@[A-Za-z0-9]+([_\.-][A-Za-z0-9]+)*$/i;
	return(email.test(who));
}

function isEmails(who) {
	var emailArr = who.split(",");
	for (var i = 0; i < emailArr.length; i++) {
		if (!isEmail2((emailArr[i].trim())))
			return false;
	}
	return true;
}

function URL(url) {
	if (url.length == 0) eval('throw "Invalid URL [' + url + '];');
	this.url = url;
	this.port = -1;
	this.query = (this.url.indexOf('?') >= 0) ? this.url.substring(this.url.indexOf('?') + 1) : '';
	if (this.query.indexOf('#') >= 0) this.query = this.query.substring(0, this.query.indexOf('#'));
	this.protocol = '';
	this.host = '';
	if (url.indexOf("//") == 0)        // network share like '//HOST/SHARE'
		this.url = "file:" + url;
	var protocolSepIndex = this.url.indexOf('://');
	if (protocolSepIndex >= 0) {
		this.protocol = this.url.substring(0, protocolSepIndex).toLowerCase();
		this.host = this.url.substring(protocolSepIndex + 3);
		while (this.host.indexOf('/') == 0) {
			this.host = this.host.substring(1);
		}
		if (this.host.indexOf('/') >= 0) this.host = this.host.substring(0, this.host.indexOf('/'));
		var atIndex = this.host.indexOf('@');
		if (atIndex >= 0) {
			var credentials = this.host.substring(0, atIndex);
			var colonIndex = credentials.indexOf(':');
			if (colonIndex >= 0) {
				this.username = credentials.substring(0, colonIndex);
				this.password = credentials.substring(colonIndex);
			} else {
				this.username = credentials;
			}
			this.host = this.host.substring(atIndex + 1);
		}
		var portColonIndex = this.host.indexOf(':');
		if (portColonIndex >= 0) {
			this.port = this.host.substring(portColonIndex);
			this.host = this.host.substring(0, portColonIndex);
		}
		this.file = this.url.substring(protocolSepIndex + 3);
		this.file = this.file.substring(this.file.indexOf('/'));
	} else {
		this.file = this.url;
	}
	if (this.file.indexOf('?') >= 0) this.file = this.file.substring(0, this.file.indexOf('?'));
	var refSepIndex = url.indexOf('#');
	if (refSepIndex >= 0) {
		this.file = this.file.substring(0, refSepIndex);
		this.reference = this.url.substring(this.url.indexOf('#'));
	} else {
		this.reference = '';
	}
	this.path = this.file;
	if (this.query.length > 0) this.file += '?' + this.query;
	if (this.reference.length > 0) this.file += '#' + this.reference;

	this.getPort = getPort;
	this.getQuery = getQuery;
	this.getProtocol = getProtocol;
	this.getHost = getHost;
	this.getUserName = getUserName;
	this.getPassword = getPassword;
	this.getFile = getFile;
	this.getReference = getReference;
	this.getPath = getPath;
	this.getArgumentValue = getArgumentValue;
	this.getArgumentValues = getArgumentValues;
	this.toString = toString;

	/* Returns the port part of this URL, i.e. '8080' in the url 'http://server:8080/' */
	function getPort() {
		return this.port;
	}

	/* Returns the query part of this URL, i.e. 'Open' in the url 'http://server/?Open' */
	function getQuery() {
		return this.query;
	}

	/* Returns the protocol of this URL, i.e. 'http' in the url 'http://server/' */
	function getProtocol() {
		return this.protocol;
	}

	/* Returns the host name of this URL, i.e. 'server.com' in the url 'http://server.com/' */
	function getHost() {
		return this.host;
	}

	/* Returns the user name part of this URL, i.e. 'joe' in the url 'http://joe@server.com/' */
	function getUserName() {
		return this.username;
	}

	/* Returns the password part of this url, i.e. 'secret' in the url 'http://joe:secret@server.com/' */
	function getPassword() {
		return this.password;
	}

	/* Returns the file part of this url, i.e. everything after the host name. */
	function getFile() {
		return this.file;
	}

	/* Returns the reference of this url, i.e. 'bookmark' in the url 'http://server/file.html#bookmark' */
	function getReference() {
		return this.reference;
	}

	/* Returns the file path of this url, i.e. '/dir/file.html' in the url 'http://server/dir/file.html' */
	function getPath() {
		return this.path;
	}

	/* Returns the FIRST matching value to the specified key in the query.
	 If the url has a non-value argument, like 'Open' in '?Open&bla=12', this method
	 returns the same as the key: 'Open'...
	 The url must be correctly encoded, ampersands must encoded as &amp;
	 I.e. returns 'value' if the key is 'key' in the url 'http://server/?Open&amp;key=value' */
	function getArgumentValue(key) {
		var a = this.getArgumentValues();
		if (a.length < 1) return '';
		for (var i = 0; i < a.length; i++) {
			if (a[i][0] == key) return a[i][1];
		}
		return '';
	}

	/* Returns all key / value pairs in the query as a two dimensional array */
	function getArgumentValues() {
		var a = new Array();
		var b = this.query.split('&amp;');
		if (b == null) b = this.query.split('&');
		var c = '';
		if (b.length < 1) return a;
		for (var i = 0; i < b.length; i++) {
			c = b[i].split('=');
			a[i] = new Array(c[0], ((c.length == 1) ? c[0] : c[1]));
		}
		return a;
	}

	/* Returns a String representation of this url */
	function toString() {
		return this.url;
	}
}

function allOK(el, docLabels) {
	var Sender;
	for (i = 0; i < docLabels.length; i++) {
		if (docLabels[i].htmlFor == el.id) {
			Sender = docLabels[i];
			break;
		}
	}
	el.style.backgroundColor = "";
	if (el.className != null && el.className.indexOf("selectbox") != -1) {
		var checkboxes = el.getElementsByTagName("input");
		if (checkboxes) {
			for (var c = 0; c != checkboxes.length; c++) {
				var childer = checkboxes[c];
				childer.parentNode.style.backgroundColor = "";
			}
		}
	}
	if (Sender) {
		var cl = Sender.className;
		if (cl != null && cl.indexOf(" invalidInput") > -1) {
			var n = cl.indexOf(" invalidInput");
			Sender.className = cl.substring(0, n);
			Sender.title = "";
		}
	}
	el.title = "";
}

function disValid(el, title, docLabels) {
	//dnikitin ����� ������������ task/user udf-�.
	var Sender;
	var elid;
	if (el.type == "radio") {
		elid = el.parentNode.parentNode.parentNode.parentNode.id;
	} else {
		elid = el.id;
	}

	for (var i = 0; i < docLabels.length; i++) {
		if (docLabels[i].htmlFor == elid) {
			Sender = docLabels[i];
			break;
		}
	}
	if (el.type == "radio") {
		el.style.backgroundColor = "#FFA090";
		if (document.getElementById("roles") != null) {
			document.getElementById("roles").style.backgroundColor = "#FFA090";
		}
		if (document.getElementById("userlist") != null) {
			document.getElementById("userlist").style.backgroundColor = "#FFA090";
		}
	} else {
		el.style.backgroundColor = "#FFA090";
	}
	if (el.className != null && el.className.indexOf("selectbox") != -1) {
		var checkboxes = el.getElementsByTagName("input");
		if (checkboxes) {
			for (var c = 0; c != checkboxes.length; c++) {
				var childer = checkboxes[c];
				childer.parentNode.style.backgroundColor = "#FFA090";
			}
		}
	}

	if (Sender) {
//        Sender.className += " invalidInput";
//        Sender.title += title;
	}
	el.title += title;
	valid = false;
}

function validate(Sender) {
	try {
		valid = true;
		var task_number = "";
		var ownerDoc = Sender.ownerDocument;
		var docLabels = ownerDoc ? ownerDoc.getElementsByTagName("label") : null;
		if (typeof(tinyMCE) != 'undefined' && tinyMCE != null) {
			tinyMCE.triggerSave();
		}
		var radios = {};
		if (!self.dontValid)
			for (var i = 0; i < Sender.elements.length; i++) {
				var el = Sender.elements[i];

				if (el.className.indexOf("hintTextbox") != -1) {
					if (el.value == el.title) {
						el.value = "";
					}
				}
				if (el.name == 'addlist') {    // text area from UdfListValueTile
					var s = el.value.split("\r\n"); // split the string into an array based on the enter chars (ascii 13 and 10) linefeed carriage return
					for (var j = 0; j < s.length; j++) {
						if (s[j].length > 200) {

							disValid(el, ERROR_INVALID_LINE_LENGTH);
							alert(ERROR_INVALID_LINE_LENGTH);
							return false;
						}
					}
				}
				if (el.disabled == true)
					continue;
				if (el.parentNode.style == "none")
					continue;
				if (el.parentNode.parentNode.style && el.parentNode.parentNode.style.display == "none")
					continue;
				if (el.parentNode.parentNode.parentNode.style && el.parentNode.parentNode.parentNode.style.display == "none")
					continue;
				if (Sender[el.name + "___Config"] != null) continue;
				var type = el.type;
				var alt = el.getAttribute("alt");
				var value = el.value;
				var name = el.name;
				var pwd;
				if (el.name.indexOf("_task_number") != -1)
					task_number = value;
				if (!el.isDisabled && type != "checkbox" && type != "submit" && type != "image") {
					el.style.backgroundColor = "";
					el.title = "";

					if (alt != null && value != null && alt.indexOf("mustChoose") > -1 && value.length > 0) {

						var noEmpty = null;
						if (alt.indexOf("ifNoEmpty") > -1) {
							var noEmptyName = alt.substring(alt.indexOf("ifNoEmpty(") + 10, alt.indexOf(")", alt.indexOf("ifNoEmpty(") + 10));
							noEmpty = Sender.elements[noEmptyName];
						}
						if (value == "NotChoosen" && (!noEmpty || noEmpty.value.length > 0)) {

							disValid(el, alt.substring(alt.indexOf("mustChoose(") + 11, alt.indexOf(")", alt.indexOf("mustChoose(") + 11)), docLabels)

						}
						else allOK(el, docLabels);
						if (value == "NotChoosen" && (!noEmpty || noEmpty.value.length > 0)) {
							disValid(el, alt.substring(alt.indexOf("mustChoose(") + 11, alt.indexOf(")", alt.indexOf("mustChoose(") + 11)), docLabels)
						}
						else allOK(el, docLabels);
					}
					if (alt != null && value != null && alt.indexOf("mustBeChoose") > -1 && value.length > 0) {

						if (value == "NotChoosen") {

							disValid(el, CHOOSE_PROJECT, docLabels);

						} else  allOK(el, docLabels);
					}
					if (alt != null && value != null && alt.indexOf("email") > -1 && value.length > 0) {
						if (!isEmail2(value)) {

							disValid(el, ERROR_EMAIL_INCORRECT, docLabels);
						}
						else allOK(el, docLabels);
					}
					if (alt != null && value != null && alt.indexOf("elist") > -1 && value.length > 0) {
						if (!isEmails(value)) {
							disValid(el, ERROR_EMAIL_INCORRECT, docLabels);
						}
						else allOK(el, docLabels);
					}
					if (alt != null && value != null && alt.indexOf("url") > -1 && value.length > 0) {
						if (new URL(value).getHost().length == 0) {
							var reg = /[a-zA-Z]:\w/;
							if (!reg.exec(value.toLowerCase())) {
								disValid(el, ERROR_INCORRECT_URL, docLabels);
							} else {
								allOK(el, docLabels);
							}
						}
					}
					if (alt != null && value != null && alt.indexOf("float") > -1 && value.length > 0) {
						value = value.trim();
						var re1 = new RegExp("[^\\d\\-\\.\\,\\E]");
						if (value.indexOf("+") == 0) {
							value = value.substring(1, value.length);
						}
						var re2 = new RegExp("-?\\d[\\.\\,]?\\d*");
						var regExpanent = new RegExp("-?\\d[\\.\\,]?\\d*E?-?\\d*");
						var ans = false;
						if (name.indexOf("UDF") != -1 && $("_" + name) != null) {
							if ($("#_" + name).val().indexOf("_in_") != -1) {
								ans = true;
							}
						}
						if (!ans) {
							if (value.search(re1) > -1 || (parseFloat(value.match(re2)) != parseFloat(value) && parseFloat(value.match(regExpanent)) != parseFloat(value))) {
								disValid(el, ERROR_FLOAT, docLabels);
							} else {
								allOK(el, docLabels);
							}
						} else {
							var mass = [' ', '-', ';'];
							var values = value;
							for (j = 0; j < value.length; j++) {
								if (j > 0) {
									var ch2 = value.charAt(j - 1);
									if (ch == ' ') {
										if (ch2 == ' ')
											disValid(el, ERROR_FLOAT, docLabels);
									} else
									if (ch2 == ' ' || ch2 == ';' || ch2 == ',' || ch2 == '-')
										disValid(el, ERROR_FLOAT, docLabels);
								}
								else
									disValid(el, ERROR_FLOAT, docLabels);
							}
							while (values.length > 0) {
								var index = values.length;
								for (i = 0; i < mass.length; i++) {
									if (values.indexOf(mass[i]) != -1)
										if (index > values.indexOf(mass[i]))
											index = values.indexOf(mass[i]);
								}
								var val = values.substring(0, index);
								if (val.search(re1) > -1 || val.match(re2) != val)
									disValid(el, ERROR_FLOAT, docLabels);
								else
									allOK(el, docLabels);
								values = values.substring(index + 1, values.length);
							}
							break;
						}
					}
					if (alt != null && value != null && alt.indexOf("integer") > -1 && value.length > 0) {
						value = value.trim();
						if (value.indexOf("+") == 0) {
							value = value.substring(1, value.length);
						}
						var re1 = new RegExp("[^\\d\\-]");
						var re2 = new RegExp("-?\\d+");
						var ans = false;
						if (name.indexOf("UDF") != -1 && $("#_" + name) != null) {
							if ($("#_" + name).val().indexOf("_in_") != -1) {
								ans = true;
							}
						}
						if (!ans) {
							if (value.search(re1) > -1 || value.match(re2)[0] != value) {
								disValid(el, ERROR_INTEGER, docLabels);
							} else {
								if (parseInt(value) < -2147483648 || parseInt(value) > 2147483647) {
									disValid(el, ERROR_INTEGER, docLabels);
								} else {
									allOK(el, docLabels);
								}
							}
						} else {
							var mass = [' ', '-', ';', ','];
							var values = value;
							for (j = 0; j < value.length; j++) {
								if (j > 0) {
									var ch2 = value.charAt(j - 1);
									if (ch == ' ') {
										if (ch2 == ' ')
											disValid(el, ERROR_INTEGER, docLabels);
									} else
									if (ch2 == ' ' || ch2 == ';' || ch2 == ',' || ch2 == '-')
										disValid(el, ERROR_INTEGERL, docLabels);
								}
								else
									disValid(el, ERROR_INTEGER, docLabels);
							}
							while (values.length > 0) {
								var index = values.length;
								for (i = 0; i < mass.length; i++) {
									if (values.indexOf(mass[i]) != -1)
										if (index > values.indexOf(mass[i]))
											index = values.indexOf(mass[i]);
								}
								var val = values.substring(0, index);
								if (val.search(re1) > -1 || val.match(re2) != val)
									disValid(el, ERROR_INTEGER, docLabels);
								else {
									if (parseInt(val) < -2147483648 || parseInt(val) > 2147483647) {
										disValid(el, ERROR_INTEGER, docLabels);
									} else {
										allOK(el, docLabels);
									}
								}
								values = values.substring(index + 1, values.length);
							}
							break;
						}
					}
					if (alt != null && value != null && alt.indexOf("seconds") > -1 && value.length > 0) {
						var re1 = new RegExp("[^\\d\\-]");
						var re2 = new RegExp("-?\\d*");
						if (value.search(re1) > -1 || value.match(re2) != value || value < 0 || value > 59) {

							disValid(el, ERROR_SECONDS, docLabels);
						}
						else allOK(el.docLabels);
					}
					if (alt != null && value != null && alt.indexOf("natural") > -1 && value.length > 0) {
						var re1 = new RegExp("\\D");
						var ans = null;
						if ($('#_tasknumber') != null) {
							if ($('#_tasknumber').val() && $('#_tasknumber').val().indexOf("_in_") != -1) {
								ans = 1;
							}
						}
						if (ans == null) {
							if (value.search(re1) > -1) {
								disValid(el, ERROR_NATURAL, docLabels);
							}
							else allOK(el, docLabels);
						} else {
							for (j = 0; j < value.length; j++) {
								var ch = value.charAt(j);
								if (ch == ' ' || ch == ';' || ch == ',' || ch == '-') {
									if (j == value.length - 1) {

										disValid(el, ERROR_LIST_NATURAL, docLabels);
									}
									if (j > 0) {
										var ch2 = value.charAt(j - 1);
										if (ch == ' ') {
											if (ch2 == ' ')
												disValid(el, ERROR_LIST_NATURAL, docLabels);
										} else
										if (ch2 == ' ' || ch2 == ';' || ch2 == ',' || ch2 == '-')
											disValid(el, ERROR_LIST_NATURAL, docLabels);
									}
									else
										disValid(el, ERROR_LIST_NATURAL, docLabels);
								}
								else if (ch < '0' || ch > '9')
									disValid(el, ERROR_LIST_NATURAL, docLabels);
							}
						}
					}
					if (alt != null && value != null && alt.indexOf("login") > -1 && value.length > 0) {
						if (value.indexOf("\'") != -1 || value.indexOf("\"") != -1) {

							disValid(el, ERROR_INCORRECT_LOGIN);
						}
					}
					if (alt != null && value != null && alt.indexOf("taskNumber") > -1 && document.getElementById("REG_NAME_ID").value != "") {
						if (value.indexOf("#") == 0)
							value = value.substring(1);
						var re1 = new RegExp("\\D");
						if (value.search(re1) > -1 || value.length == 0) {

							disValid(el, ERROR_INCORRECT_TASK_NUMBER, docLabels);
							alert(ERROR_INCORRECT_TASK_NUMBER);
							return false;
						}
					}
					if (alt != null && value != null && alt.indexOf("task") > -1 && document.getElementById("REG_NAME_ID").value != "") {
						if (value.indexOf("#") == 0)
							value = value.substring(1);
						if (value.length == 0) {

							disValid(el, ERROR_INCORRECT_TASK, docLabels);
							alert(ERROR_INCORRECT_TASK);
							return false;
						}
					}
					if (alt != null && value != null && alt.indexOf("numTask") > -1) {
						if (value.indexOf("#") == 0)
							value = value.substring(1);
						var re1 = new RegExp("[^\\d\\-]");

						if (value.length == 0 || value.search(re1) > -1) {
							disValid(el, ERROR_INCORRECT_TASK, docLabels);
							alert(ERROR_INCORRECT_TASK);
							return false;
						}
					}
					if (alt != null && value != null && alt.indexOf("color") > -1 && value.length > 0) {
						if (!colorValid(value)) {

							disValid(el, ERROR_INCORRECT_COLOR, docLabels);
						}
						else allOK(el, docLabels);
					}
					if (alt != null && value != null && alt.indexOf("date") > -1 && value.length > 0) {
						var pattern = "";
						var mStart = alt.indexOf('date(');
						if (mStart > -1) {
							var mFinish = alt.indexOf(')', mStart + 1);
							if (mFinish == -1) {
								pattern = alt.substring(mStart + 5, alt.length);
							} else {
								pattern = alt.substring(mStart + 5, mFinish);
							}
						}
						var calendar = new Calendar(1, null, selected, closeHandler)
						var res2 = calendar.parseDate(value, pattern, true);
						if (res2 == null || res2.indexOf("%") != -1) // function toDate defined in calendar.js
						{

							disValid(el, ERROR_INCORRECT_DATE + pattern + "'. ", docLabels);
						}
						else {
							el.value = res2;
						}
					}
					if (type != "radio") {
						var minLength = null;
						var maxLength = null;
						if (alt != null && alt.length > 0) {
							var re = /[^,\d]+\d+/g;
							var rd = /\d+/;
							do {
								bound = re.exec(alt);
								if (bound != null) {
									pre = bound[0];
									if (pre.indexOf('>') > -1 || pre.indexOf('&gt;') > -1) {
										dig = rd.exec(pre);
										minLength = dig[0];
									}
									if (pre.indexOf('<') > -1 || pre.indexOf('&lt;') > -1) {
										dig = rd.exec(pre);
										maxLength = dig[0];
									}

								}
							} while (bound != null);

							if (el.className == "form-autocomplete") {
								var selectBox = document.getElementById("_" + el.name);
								if (selectBox && selectBox.childNodes) {
									var itsok = false;
									for (var u = 0; u < selectBox.childNodes.length; u++) {
										var _label = selectBox.childNodes[u];
										if (_label && _label.htmlFor) {
											var _inp = document.getElementById(_label.htmlFor);
											if (_inp && _inp.checked) itsok = true;
										}
									}
									if (!itsok) {
										disValid(el, ERROR_FIELD_LENGTH_1 + value.length + ERROR_FIELD_LENGTH_2 + maxLength + ((minLength != null) ? (ERROR_FIELD_LENGTH_3 + minLength + ". " ) : ""), docLabels);
									}
								} else {
									disValid(el, ERROR_FIELD_LENGTH_1 + value.length + ERROR_FIELD_LENGTH_2 + maxLength + ((minLength != null) ? (ERROR_FIELD_LENGTH_3 + minLength + ". " ) : ""), docLabels);
								}
							} else {
								if (maxLength == null) maxlength = el.getAttribute("maxlength");
								if ((maxLength != null && maxLength < value.length) || (minLength != null && minLength >= value.trim().length)) {

									disValid(el, ERROR_FIELD_LENGTH_1 + value.length + ERROR_FIELD_LENGTH_2 + maxLength + ((minLength != null) ? (ERROR_FIELD_LENGTH_3 + minLength + ". " ) : ""), docLabels);
								}
							}
						}
					} else {
						// radio group
						if (alt != null && alt.length > 0) {
							var re = /[^,\d]+\d+/g;
							var rd = /\d+/;
							do {
								bound = re.exec(alt);
								if (bound != null) {
									pre = bound[0];
									if (pre.indexOf('>') > -1 || pre.indexOf('&gt;') > -1) {
										dig = rd.exec(pre);
										minLength = dig[0];
									}
									if (pre.indexOf('<') > -1 || pre.indexOf('&lt;') > -1) {
										dig = rd.exec(pre);
										maxLength = dig[0];
									}

								}
							}
							while (bound != null);
						}
						if (el.checked) {
							if (radios[el.name] == null || radios[el.name] != "") radios[el.name] = "";
						}
						if (minLength != null && radios[el.name] == null) radios[el.name] = el;
					}
					if (alt != null && alt.indexOf("pwd1") > -1) {
						pwd = value;
						if (alt.substring(alt.lastIndexOf("pwd1, ") + "pwd1, ".length, alt.length) == "true")
							if (!correctPasswordValue(pwd)) {
								disValid(el, ERROR_SIMPLE_PASSWORD, docLabels);
								alert(ERRROR_CORRECT_FIELDS);
								return false;
							}
					}
					if (alt != null && alt.indexOf("pwd2") > -1) {
						if (pwd != value) {
							disValid(el, ERROR_INCORRECT_PASSWORD, docLabels);
						}
					}
					if (alt != null && alt.indexOf("tinymce") > -1) {
						var size = alt.substring(1, alt.indexOf(','));
						var textareaId = el.getAttribute("id");
						var frame = document.getElementById(textareaId + "_ifr");
						if (frame) {
							var body = frame.contentWindow.document.getElementById('tinymce');
							if (parseInt(value.length) > parseInt(size)) {
								body.style.backgroundColor = "#FFA090";
								disValid(el, ERROR_TOO_MUCH_TEXT, docLabels);
							}
						}
					}
					if (alt != null && alt.indexOf("filter") > -1) {
						value = el.innerHTML;
						if (value == null || value.length == 0) {
							disValid(el, ERROR_FILTER_FIELD, docLabels);
						}
					}
					if (alt != null && alt.indexOf("select<") > -1) {
						var limit = alt.substring(alt.indexOf("select<") + "select<".length, alt.length);
						value = el.options;
						if (value != null && value.length != 0) {
							for (var sel_i = 0; sel_i != value.length; sel_i++) {
								if (value[sel_i].value.length >= parseInt(limit)) {
									disValid(el, ERROR_TOO_MUCH_TEXT, docLabels);
									alert(ERROR_TOO_MUCH_TEXT);
									return false;
								}
							}
						}
					}
					if (alt != null && alt.indexOf("textarea>") > -1) {
						var result = alt.substring(alt.indexOf("textarea>") + "textarea>".length, alt.length);
						var min = result.substring(0, result.indexOf("and"));
						var max = result.substring(result.indexOf("and<") + "and<".length, result.length);
						var massvalue = value.split("\n");
						for (var ms = 0; ms != massvalue.length; ms++) {
							value = massvalue[ms];
							if (value != null) {
								if (value.length <= parseInt(min) || value.length >= parseInt(max)) {
									if (value.length == 0 && ms == massvalue.length - 1) {
										continue;
									}
									disValid(el, ERROR_TOO_MUCH_TEXT, docLabels);
									alert(ERROR_TOO_MUCH_TEXT);
									return false;
								}
							}
						}
					}
					if (alt != null && alt.indexOf("divbox") > -1) {
						var box = document.getElementById("udf" + el.id);
						if (box) {
							var checkboxs = box.getElementsByTagName("input");
							var checkedBox = false;
							for (var k = 0; k != checkboxs.length; k++) {
								var childer = checkboxs[k];
								if (childer.type == "checkbox" && childer.checked) {
									checkedBox = true;
								}
							}
							if (!checkedBox) {
								disValid(box, ERRROR_CORRECT_FIELDS, docLabels);
							} else {
								allOK(box, docLabels);
							}
						}
					}
				}
			}
		for (var r in radios) {
			if (radios[r] != "") {
				disValid(radios[r], ERROR_CHOOSE_RADIO, docLabels);
			}
		}
		if (!valid) {
			alert(ERRROR_CORRECT_FIELDS);
			return false;
		} else {
			return true;
		}
	} catch (err) {
		showError("validate(Sender)", err);
	}
}

// color value validating
function colorValid(val) {
	var re = new RegExp("#[abcdef\\d]{6}", "i");
	return val.match(re) == val;
}


// "CLOSE_MSG" block begin
var isSubmitting = false;
var is_new = false;

function allow(sender) {
	isSubmitting = true;
	sender.onsubmit = function() {
		return false;
	};
	return true;
}

window.onbeforeunload = function() {
	if (!isSubmitting)
		for (var j = 0; j < document.forms.length; j++)
			if ((document.forms[j].alt == 'checkunload' || document.forms[j].id == 'checkunload') && isDirty(document.forms[j]))
				return WIN_CLOSE_MSG;
}

function isDirty(form) {
	if (is_new) {
		return true;
	}
	if (form.elements)
		for (var i = 0; i < form.elements.length; i++) {
			var control = form.elements[i];
			if (control.type != 'submit' && control.type != 'hidden' && control.type != 'button' && !control.disabled && control.getAttribute("ignoreModified") == null && getControlValue(control) != getControlDefaultValue(control))
				return true;
		}
	return false;
}

function getControlValue(control) {
	switch (control.type) {
		case 'checkbox':
			return control.checked;
		case 'select-one':
			for (var i = 0; i < control.options.length; i++)
				if (control.options[i].selected)
					return control.options[i].value;
		case 'select-multiple':
			var val = '';
			for (var i = 0; i < control.options.length; i++)
				if (control.options[i].selected)
					val += control.options[i].value + '\n';
			return val;
		default:
			return control.value;
	}
}

function getControlDefaultValue(control) {
	switch (control.type) {
		case 'checkbox':
			return control.defaultChecked;
		case 'select-one':
			for (var i = 0; i < control.options.length; i++)
				if (control.options[i].defaultSelected)
					return control.options[i].value;
			if (control.options[0])
				return control.options[0].value;
			else
				return null;
		case 'select-multiple':
			var val = '';
			for (var i = 0; i < control.options.length; i++)
				if (control.options[i].defaultSelected)
					val += control.options[i].value + '\n';
			return val;
		default:
			return control.defaultValue;
	}
}

function updateClock() {
	if (!stop) {
		sec++;
		if (sec == 60) {
			min++
			sec = 0;
		}
		if (min == 60) {
			hrs++;
			min = 0;
		}
	}
	if (document.getElementById("clockId") != null)
		document.getElementById("clockId").firstChild.nodeValue = (hrs < 10 ? "0" + hrs : hrs) + " hh " + (min < 10 ? "0" + min : min) + " mm " + (sec < 10 ? "0" + sec : sec ) + " ss";
}

function setHours(sender, id) {
	var inputs = sender.parentNode.parentNode.parentNode.getElementsByTagName("input");
	for (var i = 0; i < inputs.length; i++) {
		var input = inputs[i];
		if (input.name == ("hrs(" + id + ")"))
			input.value = hrs;
		if (input.name == ("mns(" + id + ")"))
			input.value = min;
		if (input.name == ("sec(" + id + ")"))
			input.value = sec;
	}
}
var canAddpanelNode = true;
function placeOnServicePanel(_id, _label, _name, _icon) {

	var panel = document.getElementById("servicePanel");
	// open panel
	var v = true;
	if (panel != null) {
		if (panel.className == 'closed') panel.className = 'norm';
		var elements = panel.childNodes;
		var ids = "";
		var needToAdd = true;
		if (elements != null && elements.length > 0) {
			var toRemove = null;
			for (var k = 0; k < elements.length; k++) {
				var em = elements[k];
				if (em.id == "_spi_" + _id) {
					// ��� ����, �������
					needToAdd = false;
					toRemove = em;

				} else {
					if (em.tagName == "LABEL") {
						var title = em.id;
						if (title != null && title.length > 0) {
							ids += title.substring("_spi_".length) + "*";
						}
					}
				}
			}
			if (toRemove != null) {
				panel.removeChild(toRemove);
				if (servicePanelSrc) {
					if (servicePanelSrc.length) {
						for (var j = 0; j < servicePanelSrc.length; j++) {
							if (servicePanelSrc[j].value == _id) {
								servicePanelSrc[j].checked = false;
								var headerChecker = document.getElementById('headerChecker');
								if (headerChecker && headerChecker.checked) {
									headerChecker.checked = false;
								}
							}
						}
					} else {
						servicePanelSrc.checked = false;
					}
				}
				v = false;
			}

		}
		if (needToAdd) {
			if ((ids + _id).length >= 2999) {
				if (canAddpanelNode) {
					alert(ERROR_COOKIE_IS_TOO_BIG);
					canAddpanelNode = false;
				}
				return false;
			} else {
				//��������� �� ������
				canAddpanelNode = true;
				var shortName = _name;
				if (_name != null && _name.length > 23) shortName = _name.substr(0, 20) + "...";

				var li = document.createElement("label");
				var fnc = function () {
					placeOnServicePanel(_id, _label);
				};
				if (window.attachEvent)
					li.attachEvent("onclick", fnc);
				else li.setAttribute("onclick", "placeOnServicePanel('" + _id + "','" + _label + "');");
				li.setAttribute("id", "_spi_" + _id);
				if (_label != null && _label != 'null' && _name != null) li.setAttribute("title", "[" + _label + "] " + _name);
				else if (_label != null && _label != 'null') li.setAttribute("title", _label);
				if (_icon != null) {
					li.style.backgroundImage = "URL(" + _icon + ")";
					li.style.marginLeft = "2px";
					li.style.backgroundPosition = "left center";
					li.style.backgroundRepeat = "no-repeat";
					li.style.paddingLeft = "18px";
				}// icon

				li.style.paddingTop = "1px";
				li.style.paddingBottom = "1px";
				li.style.fontFamily = "Verdana";
				li.style.fontSize = "11px";
				li.style.fontWeight = "bold";
				if (_name != null) {
					if (_label != null && _label.length > 0 && _label != 'null') {
						shortName = "[" + _label + "] " + shortName;
						li.setAttribute("htmlFor", _label);
					}
				} else {
					shortName = _label;
					li.setAttribute("htmlFor", _label);
				}
				var liText = document.createTextNode(shortName + " ");
				li.appendChild(liText);
				panel.appendChild(li);

				ids += _id + "*";

			}
		}
		panel.className = 'norm';
		setTabCookie("_selectedId", ids);
		if (ids.length == 0) closeServicePanel();
	}
	return v;
}

function closeServicePanel(target) {
	var _panel = document.getElementById("servicePanel");
	if (_panel != null) {
		_panel.className = 'closed';
		var elements = _panel.childNodes;
		if (elements != null && elements.length > 0) {
			for (var k = 0; k < elements.length; k++) {
				var em = elements[k];
				if (em.tagName == "LABEL") {
					_panel.removeChild(em);
				}

			}
		}
		var headerChecker = document.getElementById('headerChecker');
		if (headerChecker && headerChecker.checked) {
			headerChecker.checked = false;
		}
	}
	if (target) {
		if (target.length) {
			for (var j = 0; j < target.length; j++) {
				target[j].checked = false;
			}
		} else {
			target.checked = false;
		}
	}
	setTabCookie("_selectedId", "", "", "", "", "");
}

function hideServicePanel() {
	var _panel = document.getElementById("servicePanel");
	if (_panel != null) {
		if (_panel.className != 'hiddn') _panel.className = 'hiddn';
		else _panel.className = 'norm';
	}

}
function fillFormServicePanel(Sender) {


	if (Sender != null) if (Sender.length) {
		var idCookie = getTabCookie("_selectedId");

		for (var j = 0; j < Sender.length; j++) {

			if (idCookie != null && idCookie.indexOf(Sender[j].value + "*") > -1) {

				Sender[j].checked = true;
			}
		}
	} else {
		if (idCookie != null && idCookie.indexOf(Sender.value + "*") > -1) Sender.checked = true;
	}
}

function forrobots(Sender) {
	var idCookie = getTabCookie("_selectedId");
	if (idCookie != null && idCookie.length > 0) {
		return idCookie;
	} else {
		var addFor = "";
		if (Sender.length) {
			for (var j = 0; j < Sender.length; j++) {
				if (Sender[j].checked) {
					addFor += Sender[j].value + '*';
				}
			}
		} else {
			if (Sender.checked)
				addFor += Sender.value;
		}
		return addFor;
	}
}

	function forhuman() {
		var _panel = document.getElementById("servicePanel");
		var numbers = "";
		if (_panel != null) {
			var elements = _panel.childNodes;
			if (elements != null && elements.length > 0) {
				for (var k = 0; k < elements.length; k++) {
					var em = elements[k];
					if (em.tagName == "LABEL") {
						var htmlFor = em.getAttribute("htmlFor");
						var forAttr = em.getAttribute("for");
						if (htmlFor != null)
							numbers += htmlFor + ";";
						else
							numbers += forAttr + ";";
					}
				}
			}
		}
		return numbers;
	}

function _selectAll(Sender, target) {
	var idCookie = getTabCookie("_selectedId");
	if (Sender.checked) {
		if (target.length) {
			for (var j = 0; j < target.length; j++) {
				if (!target[j].checked) {
					target[j].click();
					target[j].checked = true;
				}
			}
		} else {
			target.click();
			target.checked = true;
		}
	} else {
		if (target.length) {
			for (var j = 0; j < target.length; j++) {
				if (idCookie != null && idCookie.indexOf(target[j].value + "*") > -1) {
					target[j].click();
				}
				target[j].checked = false;
			}
		} else {
			if (idCookie != null) if (idCookie.indexOf(target.value + "*") > -1) {
				target.click();
			}
			target.checked = false;
		}
		closeServicePanel(document.forms['taskListForm'].elements['SELTASK']);
	}

}

function Timer() {
	var source = getTabCookie("timer");
	this.othertimers = "";
	if (source != null) {
		var splitted = source.split("+");
		if (splitted.length > 2) {
			this.currentTask = splitted[0];
			this.state = splitted[1];
			this.time = splitted[2];
			if (splitted.length > 3) {
				for (var i = 3; i < splitted.length; i++) {
					this.othertimers = this.othertimers + splitted[i] + "+";
				}
			}
			if (this.othertimers.length > 0) {
				this.othertimers = this.othertimers.substring(0, this.othertimers.length - 1);
			}
		} else {
			this.currentTask = null;
			this.time = 0;
			this.state = "1";
			if (document.getElementById('clockPlay')) document.getElementById('clockPlay').style.display = 'inline';
			if (document.getElementById('clockPause'))      document.getElementById('clockPause').style.display = 'none';
		}
	} else {
		this.currentTask = null;
		this.time = 0;
		this.state = "1";
		if (document.getElementById('clockPlay')) document.getElementById('clockPlay').style.display = 'inline';
		if (document.getElementById('clockPause'))        document.getElementById('clockPause').style.display = 'none';

	}
}

Timer.prototype = {
	save: function() {
		var str = this.currentTask + "+" + this.state + "+" + this.time;
		if (this.othertimers.length > 0)
			str = str + "+" + this.othertimers;
		setTabCookie("timer", str);
	},
	getOther: function(task) {
		var spl = this.othertimers.split("+");
		if (spl != null) for (var i = 0; i < spl.length; i++) {
			var key = spl[i];
			i++; // state
			i++; // value
			var value = spl[i];
			if (key == task) return value;
		}
		return null;
	},
	set: function(task) {
		if (this.currentTask != null && task != this.currentTask) {
			if (this.time != null) {
				var seconds1 = 0;
				if (this.state == "0") {

					var startTime1 = this.time;
					var myTime1 = new Date();
					var timeNow1 = myTime1.getTime();
					seconds1 = Math.floor((timeNow1 - startTime1) / 1000);

				} else seconds1 = this.time;

				var oldTime = this.getOther(task);

				if ((oldTime == null || oldTime == 0) && this.othertimers.split("+").length < 17) {
					if (seconds1 > 0) {
						if (this.othertimers.length > 0) this.othertimers = this.currentTask + "+" + this.state + "+" + seconds1 + "+" + this.othertimers;
						else this.othertimers = this.currentTask + "+" + this.state + "+" + seconds1;
					}
				} else {

					var newothertimers = "";
					var count = 0;
					var spl = this.othertimers.split("+");
					if (spl != null) for (var i = 0; i < spl.length; i++) {
						var key = spl[i];
						i++;
						var sta = spl[i];
						i++;
						var value = spl[i];

						if (key != task && value > 60 && count < 9) newothertimers += key + "+" + sta + "+" + value + "+";
						count++;
					}
					newothertimers = this.currentTask + "+" + this.state + "+" + seconds1 + "+" + newothertimers;
					if (newothertimers[newothertimers.length - 1] == "+") newothertimers = newothertimers.substring(0, newothertimers.length - 1);
					this.othertimers = newothertimers;
				}

				this.time = oldTime == null ? 0 : oldTime;
				this.state = "1";
				if (document.getElementById('clockPlay'))           document.getElementById('clockPlay').style.display = 'inline';
				if (document.getElementById('clockPause'))       document.getElementById('clockPause').style.display = 'none';
			}
			this.currentTask = task;
		}

		// set for current task
		if (this.state != "0") {

			// time is off, now starts
			var seconds = this.time;
			var myTime = new Date();
			var timeNow = myTime.getTime();

			this.time = timeNow - seconds * 1000;
			this.state = "0";
			if (document.getElementById('clockPlay'))            document.getElementById('clockPlay').style.display = 'none';
			if (document.getElementById('clockPause'))            document.getElementById('clockPause').style.display = 'inline';
			this.currentTask = task;

		}

		this.save();

	},

	pause: function (task) {
		if (task == this.currentTask) {

			if (this.state == "0") {
				// time is on, now stops
				if (this.time != null) {
					var startTime = this.time;
					var myTime = new Date();
					var timeNow = myTime.getTime();
					var seconds = Math.floor((timeNow - startTime) / 1000);

					this.time = seconds;
					this.state = "1";
					if (document.getElementById('clockPlay')) document.getElementById('clockPlay').style.display = 'inline';
					if (document.getElementById('clockPause')) document.getElementById('clockPause').style.display = 'none';
					this.save();
				}
			}
		}
	},
	reset: function (task) {
		if (task == this.currentTask) {
			this.time = 0;
			this.state = "1";
			if (document.getElementById('clockPlay'))  document.getElementById('clockPlay').style.display = 'inline';
			if (document.getElementById('clockPause'))  document.getElementById('clockPause').style.display = 'none';
			this.save();
			document.getElementById("clockId").innerHTML = "00:00:00";
		}
	},

	submit: function (task) {
		if (task == this.currentTask) {
			var actualSecs = document.getElementById("actualBudgetSeconds");
			var actualMins = document.getElementById("actualBudgetMinutes");
			var actualHours = document.getElementById("actualBudgetHours");
			var sec = 0;
			if (this.state == "0") {
				var clockStart = this.time;
				var myTime = new Date();
				var timeNow = myTime.getTime();
				sec = clockStart != 0 ? Math.ceil((timeNow - clockStart) / 1000) : 0;

			} else {
				sec = this.time;
			}
			var oldsecs = actualSecs != null && actualSecs.value.length > 0 ? parseInt(actualSecs.value) : 0;
			var oldmins = (actualMins && actualMins.value.length > 0) ? parseInt(actualMins.value) : 0;
			var oldhours = (actualHours && actualHours.value.length > 0) ? parseInt(actualHours.value) : 0;
			sec = parseInt(sec) + parseInt(oldsecs + oldmins * 60 + oldhours * 3600);
			var hours = actualHours ? Math.floor(sec / 3600) : 0;
			sec = sec - hours * 3600;
			var mins = actualMins ? Math.floor(sec / 60) : 0;
			sec = sec - mins * 60;
			if (actualSecs) document.getElementById("actualBudgetSeconds").value = sec;
			if (actualMins) document.getElementById("actualBudgetMinutes").value = mins;
			if (actualHours) document.getElementById("actualBudgetHours").value = hours;
			this.reset(task); // was if (this.state=="1")  [#69997]
		}
	}

}
function stopTimer(task) {

	var timer = new Timer();
	timer.reset(task);
}

function setTimer(task) {
	var timer = new Timer();
	timer.set(task);
}

function pauseTimer(task) {
	var timer = new Timer();
	timer.pause(task);
}

function peekTimer(task) {

	var timer = new Timer();
	timer.submit(task);

}


function getTimer() {
	var timer = new Timer();

	if ("0" == timer.state) {
		var myTime = new Date();
		var timeNow = myTime.getTime();

		var sec = (timer.time != 0) ? Math.ceil((timeNow - timer.time) / 1000) : 0;
		var allSec = sec;
		var hours = Math.floor(sec / 3600);
		sec = sec - hours * 3600;
		var mins = Math.floor(sec / 60);
		sec = sec - mins * 60;
		var t = (hours < 10 ? "0" + hours : hours) + ":" + (mins < 10 ? "0" + mins : mins) + ":" + (sec < 10 ? "0" + sec : sec );
		var element = document.getElementById("clockId");
		if (element) {
			element.innerHTML = t;
			if (allSec < 10) element.className = 'red';
			else  element.className = '';
		}
		window.setTimeout('getTimer()', 1000);
	}

}
function __lookinside(element, ss) {
	var __tc = element.textContent || element.innerText;
	var result = false;
	if (element instanceof Element) {
		var input = element.getElementsByTagName("input")[0];
		if (input) {
			result = input.value.toUpperCase().indexOf(ss.toUpperCase()) > -1;
		}
	}
	if ((__tc && __tc.toUpperCase().indexOf(ss.toUpperCase()) > -1) || result) return true;
	if (element.childNodes && element.childNodes.length > 0) {
		for (var j = 0; j < element.childNodes.length; j++)
			if (__lookinside(element.childNodes[j], ss)) return true;
	}
	return false;
}

function __localsearch(Val) {
	var fieldName = Val.name.substring("search".length);
	var elm = Val.form.elements;
	var l = Val.value.length > 2;
	for (var i = 0; i < elm.length; i++) {
		if (elm[i].name == fieldName) {
			var pt = elm[i].parentNode;
			if (l) {
				pt.style.display = 'none';
				if (__lookinside(pt, Val.value)) pt.style.display = 'block';
			} else
				pt.style.display = 'block';
		}
	}
}

	function insertParentTaskUdf(content, popup, sourceId) {
		try {
			for (var i = 0; i < content.length; i++) {
				var item = content[i];
				
				var target;
				var targetId = "_" + sourceId;
				
				if (popup) {
					if (!window.opener) {
						console.error("insertParentTaskUdf: window.opener is null!");
						alert("ERROR: window.opener is null! Cannot access parent window.");
						return;
					}
					target = window.opener.document.getElementById(targetId);
				} else {
					target = document.getElementById(targetId);
				}
				
				if (!target) {
					console.error("insertParentTaskUdf: target element not found! ID =", targetId);
					alert("ERROR: Target element not found! ID = " + targetId);
					return;
				}
				
				target.style.display = 'block';
				var label = document.createElement("label");
				var value = item.value;
				var udfId = sourceId.substring("searchudflist(".length, sourceId.length-1);
				
				var taskNumber = value.substr(1, value.indexOf('_')-1);
				
				label.innerHTML = "<input type='checkbox' name='udflist("+udfId+")' value='"+taskNumber+"' checked/>&nbsp;" + item.label;
				
				var inputs = target.getElementsByTagName("input");
				var duplicate = false;
				for (var j=0;j!=inputs.length;++j) {
					if (!duplicate && taskNumber == inputs[j].value) {
						duplicate = true;
						break;
					}
				}
				
				if (!duplicate) {
					target.appendChild(label);
				}
			}
		
		if (popup) {
			console.log("insertParentTaskUdf: closing popup");
			closeServicePanel(document.forms['taskListForm'].elements['SELTASK']);
			// Логируем результат в родительское окно
			if (window.opener && window.opener.console) {
				window.opener.console.log("insertParentTaskUdf: COMPLETED! Added", content.length, "tasks to parent window");
				window.opener.console.log("insertParentTaskUdf: sourceId was", sourceId);
			}
			window.close();
		}
		
		console.log("insertParentTaskUdf: completed successfully");
	} catch(err) {
		console.error("insertParentTaskUdf: caught error =", err);
		showError("insertParentTaskUdf", err);
		alert("ERROR in insertParentTaskUdf: " + err.message);
	}
}


	function __operate(url, udffield) {
		try {
			var humanData = forhuman();
			
			$.ajax(url, {
				dataType: "json",
				data: {key: humanData, source: udffield, byTask: true},
				success: function (data) {
					insertParentTaskUdf(data, true, udffield);
				},
				error: function(xhr, status, error) {
					console.error("__operate: AJAX error, status =", status, "error =", error);
					alert("AJAX Error in __operate: " + status + " - " + error);
				}
			});
		} catch(err) {
			console.error("__operate: caught error =", err);
			alert("ERROR in __operate: " + err.message);
		}
		return false;
	}

function checkedListCheckBoxAll(id, checked) {
	var divCheckbox = document.getElementById(id);
	var checkboxs = divCheckbox.getElementsByTagName("input");
	var ids = [];
	var pos = 0;
	for (var i = 0; i != checkboxs.length; i++) {
		var children = checkboxs[i];
		if (children.type == "checkbox" && children.parentNode.style.display != 'none') {
			ids[pos++] = children.id;
		}
	}
	for (var i = 0; i != ids.length; i++) {
		var children = document.getElementById(ids[i]);
		if (children.type == "checkbox" && children.parentNode.style.display != 'none') {
			children.checked = checked;
			if (typeof children.onclick == "function") {
				children.onclick.apply(children);
			}
		}
	}
}

function searchInSelect(id) {
	var text = document.getElementById(id).value;
	var idSelect = "udf" + id.substring(id.indexOf("search_") + "search_".length, id.length);
	var selectElement = document.getElementById(idSelect);
	if (selectElement) {
		for (var i = 0; i != selectElement.length; ++i) {
			var option = selectElement.options[i];
			if (option.text.indexOf(text) != -1) {
				selectElement.selectedIndex = i;
				break;
			}
		}
	}
}

function changeView(view, number) {
	document.location.href = contextPath + "/task/" + number + "?thisframe=true&asView=" + view;
}

function showError(nameFunction, err) {
	var msg = "\nAn exception occurred in the script. Please send an email to support@trackstudio.com. \n" +
	          "\nBrowser Name: " + navigator.appName +
	          "\nBrowser Version: " + navigator.appVersion +
	          "\nVersion TS " + VERSION_TS +
	          "\nFunction: " + nameFunction +
	          "\nError message: " + err.message +
	          "\n\n if you don't want to get information about JS errors you will need to change a trackstudio.show.js.error=true property.";
	if (jsAlert) {
		alert(msg);
	} else {
		console.log(msg);
	}
}

//"participants_"
function moveToParticipate(id, value, key) {
	var udfId = id.substr(0, id.lastIndexOf("_"));
	var label = document.getElementById(id).parentNode;
	var div = document.getElementById("_searchudflist("+udfId+")");
	var size = div.children.length;
	for (var i=0;i!=size;i++) {
		var labelId = div.children[i].id;
		if (value &&  (key + udfId) == labelId) {
			div.insertBefore(label, div.children[i+1]);
			break;
		} else if (!value && "users_list_"+udfId == labelId) {
			div.insertBefore(label, div.children[i+1]);
			break;
		}
	}
}