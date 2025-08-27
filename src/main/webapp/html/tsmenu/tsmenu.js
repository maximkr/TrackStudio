var altGrFix = true;

function constExpression(x) {
    return x;
}

function simplifyCSSExpression() {
    try {
        var ss,sl;
        ss = document.styleSheets;
        sl = ss.length

        for (var i = 0; i < sl; i++) {
            simplifyCSSBlock(ss[i]);
        }
    }
    catch (e) {
        //		alert("Got an error while processing css. The page should still work but might be a bit slower");
        //		throw exc;
    }
}

function simplifyCSSBlock(ss) {
    var rs, rl;

    for (var i = 0; i < ss.imports.length; i++)
        simplifyCSSBlock(ss.imports[i]);

    if (ss.cssText != null && ss.cssText.indexOf("expression(constExpression(") == -1)
        return;

    rs = ss.rules;
    rl = rs.length;
    for (var j = 0; j < rl; j++)
        simplifyCSSRule(rs[j]);

}

function simplifyCSSRule(r) {
    var str = r.style.cssText;
    var str2 = str;
    var lastStr;
    do {
        lastStr = str2;
        str2 = simplifyCSSRuleHelper(lastStr);
    } while (str2 != lastStr)

    if (str2 != str)
        r.style.cssText = str2;
}

function simplifyCSSRuleHelper(str) {
    var i, i2;
    i = str.indexOf("expression(constExpression(");
    if (i == -1) return str;
    i2 = str.indexOf("))", i);
    var hd = str.substring(0, i);
    var tl = str.substring(i2 + 2);
    var exp = str.substring(i + 27, i2);
    var val = eval(exp);
    return hd + val + tl;
}

function removeExpressions() {
    var all = document.all;
    var l = all.length;
    for (var i = 0; i < l; i++) {
        simplifyCSSRule(all[i]);
    }
}

if (/msie/i.test(navigator.userAgent) && window.attachEvent != null) {
    window.attachEvent("onload", function () {
        simplifyCSSExpression();
        removeExpressions();
    });
}

var ts_menu_keymap = new Array();
var ts_menu_ua = navigator.userAgent;
var ts_menu_is_opera = /opera [56789]|opera\/[56789]/i.test(ts_menu_ua);
var ts_menu_is_ie = !ts_menu_is_opera && /MSIE/.test(ts_menu_ua);
var ts_menu_is_ie50 = ts_menu_is_ie && /MSIE 5\.[01234]/.test(ts_menu_ua);
var ts_menu_is_webkit = /webkit/i.test(ts_menu_ua);
var ts_menu_is_ie6 = ts_menu_is_ie && /MSIE [6789]/.test(ts_menu_ua);
var ts_menu_is_ie7 = ts_menu_is_ie && /MSIE [789]/.test(ts_menu_ua);
var ts_menu_is_ieBox = ts_menu_is_ie && (document.compatMode == null || document.compatMode != "CSS1Compat");
var ts_menu_is_moz = !ts_menu_is_opera && !ts_menu_is_webkit && /gecko/i.test(ts_menu_ua);
var ts_menu_is_nn6 = !ts_menu_is_opera && /netscape.*6\./i.test(ts_menu_ua);

var tsMenuDefWidth = 100;
var tsMenuDefBorderLeft = 2;
var tsMenuDefBorderRight = 2;
var tsMenuDefBorderTop = 2;
var tsMenuDefBorderBottom = 2;
var tsMenuDefPaddingLeft = 1;
var tsMenuDefPaddingRight = 1;
var tsMenuDefPaddingTop = 1;
var tsMenuDefPaddingBottom = 1;

var tsMenuDefShadowLeft = 0;
var tsMenuDefShadowRight = 0;
var tsMenuDefShadowTop = 0;
var tsMenuDefShadowBottom = 0;

var tsMenuItemDefaultHeight = 18;
var tsMenuItemDefaultText = "Untitled";
var tsMenuItemDefaultHref = "javascript:void(0)";

var tsMenuSeparatorDefaultHeight = 6;

var tsMenuDefEmptyText = "Empty";

var tsMenuDefUseAutoPosition = !ts_menu_is_nn6;

var tsMenuImagePath = "";

var tsMenuUseHover = true;
var tsMenuHideTime = 50;
var tsMenuShowTime = 250;
var overed = null;
var TSMenuHandler = {
    idCounter        :    0,
    idPrefix        :    "ts-menu-object-",
    all                :    {},
    getId            :    function () {
        return this.idPrefix + this.idCounter++;
    },
    overItem    :    function (oItem) {
        if (this.showTimeout != null)
            window.clearTimeout(this.showTimeout);
        if (this.hideTimeout != null)
            window.clearTimeout(this.hideTimeout);
        if (oItem){
            var jsItem = this.all[oItem.id];

            if (tsMenuShowTime <= 0)
                this._over(jsItem);
            else
            // I hate IE5.0 because the piece of shit crashes when using setTimeout with a function object
                this.showTimeout = window.setTimeout("TSMenuHandler._over(TSMenuHandler.all['" + jsItem.id + "'])", tsMenuShowTime);
            var root = jsItem;
            var m;
            if (root instanceof TSMenuBut)
                m = root.subMenu;
            else {
                m = jsItem.parentMenu;
                while (m.parentMenu != null && !(m.parentMenu instanceof TSMenuBar))
                    m = m.parentMenu;
            }
            if (overed!=null && overed!= m) {
                var tmOvered = overed;
                overed = null;
                tmOvered.hide();
            }

            if (m != null)
                overed = m;
        }
    },
    outItem    :    function (oItem) {
        if (this.showTimeout != null)
            window.clearTimeout(this.showTimeout);
        if (this.hideTimeout != null)
            window.clearTimeout(this.hideTimeout);
        if (oItem) {
            var jsItem = this.all[oItem.id];

            if (jsItem == null)return;
            if (tsMenuHideTime <= 0)
                this._out(jsItem);
            else
                this.hideTimeout = window.setTimeout("TSMenuHandler._out(TSMenuHandler.all['" + jsItem.id + "'])", tsMenuHideTime);
        }
        // if (overed == jsItem.id) overed=null;
    },
    blurMenu        :    function (oMenuItem) {
        window.setTimeout("TSMenuHandler.all[\"" + oMenuItem.id + "\"].subMenu.hide();", tsMenuHideTime);
    },
    _over    :    function (jsItem) {
        if (jsItem == null)return;
        if (jsItem.subMenu) {
            jsItem.parentMenu.hideAllSubs();
            jsItem.subMenu.show();
        }
        else
            jsItem.parentMenu.hideAllSubs();
    },
    _out    :    function (jsItem) {
        // find top most menu
        var root = jsItem;
        var m;
        if (root instanceof TSMenuBut)
            m = root.subMenu;
        else {
            m = jsItem.parentMenu;
            while (m.parentMenu != null && !(m.parentMenu instanceof TSMenuBar))
                m = m.parentMenu;
        }
        if (m != null)
            m.hide();
    },
    hideMenu    :    function (menu) {
        if (this.showTimeout != null)
            window.clearTimeout(this.showTimeout);
        if (this.hideTimeout != null)
            window.clearTimeout(this.hideTimeout);

        this.hideTimeout = window.setTimeout("TSMenuHandler.all['" + menu.id + "'].hide()", tsMenuHideTime);
    },
    showMenu    :    function (menu, src, dir) {
        if (this.showTimeout != null)
            window.clearTimeout(this.showTimeout);
        if (this.hideTimeout != null)
            window.clearTimeout(this.hideTimeout);
        if (arguments.length < 3)
            dir = "vertical";

        menu.show(src, dir);
    }
};

function TSMenu() {
    this._menuItems = [];
    this._subMenus = [];
    this.id = TSMenuHandler.getId();
    this.top = 0;
    this.left = 0;
    this.shown = false;
    this.parentMenu = null;
    this.selected = false;
    TSMenuHandler.all[this.id] = this;
}

TSMenu.prototype.width = tsMenuDefWidth;
TSMenu.prototype.emptyText = tsMenuDefEmptyText;
TSMenu.prototype.useAutoPosition = tsMenuDefUseAutoPosition;

TSMenu.prototype.borderLeft = tsMenuDefBorderLeft;
TSMenu.prototype.borderRight = tsMenuDefBorderRight;
TSMenu.prototype.borderTop = tsMenuDefBorderTop;
TSMenu.prototype.borderBottom = tsMenuDefBorderBottom;

TSMenu.prototype.paddingLeft = tsMenuDefPaddingLeft;
TSMenu.prototype.paddingRight = tsMenuDefPaddingRight;
TSMenu.prototype.paddingTop = tsMenuDefPaddingTop;
TSMenu.prototype.paddingBottom = tsMenuDefPaddingBottom;

TSMenu.prototype.shadowLeft = tsMenuDefShadowLeft;
TSMenu.prototype.shadowRight = tsMenuDefShadowRight;
TSMenu.prototype.shadowTop = tsMenuDefShadowTop;
TSMenu.prototype.shadowBottom = tsMenuDefShadowBottom;

TSMenu.prototype.add = function (menuItem) {
    this._menuItems[this._menuItems.length] = menuItem;
    if (menuItem.subMenu) {
        this._subMenus[this._subMenus.length] = menuItem.subMenu;
        menuItem.subMenu.parentMenu = this;
    }

    menuItem.parentMenu = this;
};

TSMenu.prototype.show = function (relObj, sDir) {
    if (this.useAutoPosition)
        this.position(relObj, sDir);

    var divElement = document.getElementById(this.id);
    if (divElement != null) {
        divElement.style.left = this.left + "px";
        divElement.style.top = this.top + "px";
        divElement.style.visibility = "visible";
    }
    this.shown = true;
    if (this.parentMenu)
        this.parentMenu.show();
};

TSMenu.prototype.hide = function () {
    this.hideAllSubs();
    var divElement = document.getElementById(this.id);
    if (divElement != null) {
        divElement.style.visibility = "hidden";
        this.shown = false;
    }
};

TSMenu.prototype.hideAllSubs = function () {
    for (var i = 0; i < this._subMenus.length; i++) {
        if (this._subMenus[i].shown)
            this._subMenus[i].hide();
    }
};
TSMenu.prototype.toString = function () {
    var top = this.top + this.borderTop + this.paddingTop;
    var str = "<div id='" + this.id + "' class='ts-menu' style='" +

        (this.width > 0 ?
            "width:" + (!ts_menu_is_ieBox ?
                this.width - this.borderLeft - this.paddingLeft - this.borderRight - this.paddingRight :
                this.width) + "px;" : " width: auto") +
        (this.useAutoPosition ?
            "left:" + this.left + "px;" + "top:" + this.top + "px;" :
            "") +
        (ts_menu_is_ie50 ? "filter: none;" : "") +
        "'" +
        " onmouseover='try {frameLoaded.length;}catch(ex){return;};TSMenuHandler.overItem()'" +
        " onmouseout='try {frameLoaded.length;}catch(ex){return;};TSMenuHandler.outItem(this)'"+
        ">";

    if (this._menuItems.length == 0) {
        str += "<span class='ts-menu-empty'>" + this.emptyText + "<\/span>";
    }
    else {
        // loop through all menuItems
        for (var i = 0; i < this._menuItems.length; i++) {
            var mi = this._menuItems[i];
            str += mi;
            if (!this.useAutoPosition) {
                if (mi.subMenu && !mi.subMenu.useAutoPosition)
                    mi.subMenu.top = top - mi.subMenu.borderTop - mi.subMenu.paddingTop;
                top += mi.height;
            }
        }

    }

    str += "<\/div>";

    for (var i = 0; i < this._subMenus.length; i++) {
        this._subMenus[i].left = this.left + this.width - this._subMenus[i].borderLeft;
        str += this._subMenus[i];
    }

    return str;
};

// TSMenu.prototype.position defined later
function TSMenuItem(sText, sHref, osMenu, sblank, oimage, oactionChar, sTitle) {
    this.text = sText; //|| tsMenuItemDefaultText;
    this.title = "";
    if (sTitle) {
        this.title = sTitle;
    }
    this.href = (sHref == null || sHref == "") ? tsMenuItemDefaultHref : sHref;
    this.subMenu = osMenu;
    if (osMenu)
        osMenu.parentMenuItem = this;
    this.id = TSMenuHandler.getId();
    TSMenuHandler.all[this.id] = this;
    this.blank = sblank;
    this.image = oimage;
    this.actionChar = oactionChar
    if (oactionChar && oactionChar != "")
        ts_menu_keymap[oactionChar] = sHref;
}
;
TSMenuItem.prototype.height = tsMenuItemDefaultHeight;
TSMenuItem.prototype.toString = function () {
    var common = (this.subMenu ? "<img class='arrow' src=\"" + tsMenuImagePath + "ico.expand.gif\">" : "") +
        (this.image != null ? "<img  hspace='0' vspace='0' border='0' align='middle' src=\"" + this.image + "\">&nbsp;" : "") +
        (this.text != null ? this.text : "");
    return    "<a " +
        " id='" + this.id + "'" +
        " title='" + this.title + "'" +
        " href=\"" + this.href + "\"" + (this.blank ? " target=\"_blank\"" : "") +
        " onmouseover='try {frameLoaded.length;}catch(ex){return;};TSMenuHandler.overItem(this)'" +
        (tsMenuUseHover ? " onmouseout='try {frameLoaded.length;}catch(ex){return;};TSMenuHandler.outItem(this)'" : "") +
        /*(this.subMenu ? " unselectable='on' tabindex='-1'" : "") +*/
        ">" +  common + "<\/a>";
};


function TSMenuSeparator() {
    this.id = TSMenuHandler.getId();
    TSMenuHandler.all[this.id] = this;
}
;
TSMenuSeparator.prototype.height = tsMenuSeparatorDefaultHeight;
TSMenuSeparator.prototype.toString = function () {
    return    "<a class='sep'" +
        " id='" + this.id + "'" +
        (tsMenuUseHover ?
            " onmouseover='try {frameLoaded.length;}catch(ex){return;};TSMenuHandler.overItem(this)'" +
                " onmouseout='try {frameLoaded.length;}catch(ex){return;};TSMenuHandler.outItem(this)'"
            :
            "") +
        "></a>";
};

function TSMenuBar() {
    this._parentConstructor = TSMenu;
    this._parentConstructor();
}
TSMenuBar.prototype = new TSMenu;
TSMenuBar.prototype.toString = function () {
    var str = "";

    // loop through all menuButtons
    for (var i = 0; i < this._menuItems.length; i++)
        str += this._menuItems[i];
    //str += "<\/div>";
    for (var i = 0; i < this._subMenus.length; i++)
        str += this._subMenus[i];
    //str += '<div class="menuBottom">';
    return str;
};

function TSMenuBut(sText, sHref, osMenu, oImage, oactionChar, sTitle) {
    this._parentConstructor = TSMenuItem;
    this._parentConstructor(sText, sHref, osMenu, null, oImage, oactionChar, sTitle);
    this.selected = false;
}
TSMenuBut.prototype = new TSMenuItem;
TSMenuBut.prototype.toString = function () {
    return    "<a" + (this.text != null ? (this.selected ? " class='menubut selected'" : " class='menubut'" ) : " class='empty'") +
        " id='" + this.id + "'" +
        " href='" + this.href + "'" +
        (tsMenuUseHover ?
            (" onmouseover='try {frameLoaded.length;}catch(ex){return;};TSMenuHandler.overItem(this)'" +
                " onmouseout='try {frameLoaded.length;}catch(ex){return;};TSMenuHandler.outItem(this)'") :
            (
                " onfocus='try {frameLoaded.length;}catch(ex){return;};TSMenuHandler.overItem(this)'" +
                    (this.subMenu ?
                        " onblur='try {frameLoaded.length;}catch(ex){return;};TSMenuHandler.blurMenu(this)'" :
                        ""
                        )
                )) +
        ">" +
        (this.image != null ? "<img alt='" + (this.text != null ? this.text : "") + "' hspace='0' vspace='0' border='0' align='middle' src=\"" + this.image + "\">&nbsp;" : "") +
        (this.text != null ? this.text : "") +
        (this.subMenu ? "<img vspace='0' hspace='0' border='0' src='"+tsMenuImagePath + "collapse.gif'>" : "") +
        "<\/a>";
};


/* Position functions */

function getInnerLeft(el) {
    if (el == null) return 0;
    if (ts_menu_is_ieBox && el == document.body || !ts_menu_is_ieBox && el == document.documentElement) return 0;
    return getLeft(el) + getBorderLeft(el);
}

function getLeft(el) {
    if (el == null) return 0;
    return el.offsetLeft + getInnerLeft(el.offsetParent);
}

function getInnerTop(el) {
    if (el == null) return 0;
    if (ts_menu_is_ieBox && el == document.body || !ts_menu_is_ieBox && el == document.documentElement) return 0;
    return getTop(el) + getBorderTop(el);
}

function getTop(el) {
    if (el == null) return 0;
    return el.offsetTop + getInnerTop(el.offsetParent);
}

function getBorderLeft(el) {
    return ts_menu_is_ie ?
        el.clientLeft :
        parseInt(window.getComputedStyle(el, null).getPropertyValue("border-left-width"));
}

function getBorderTop(el) {
    return ts_menu_is_ie ?
        el.clientTop :
        parseInt(window.getComputedStyle(el, null).getPropertyValue("border-top-width"));
}

function opera_getLeft(el) {
    if (el == null) return 0;
    return el.offsetLeft + opera_getLeft(el.offsetParent);
}

function opera_getTop(el) {
    if (el == null) return 0;
    return el.offsetTop + opera_getTop(el.offsetParent);
}

function getOuterRect(el) {
    return {
        left:    (ts_menu_is_opera ? opera_getLeft(el) : getLeft(el)),
        top:    (ts_menu_is_opera ? opera_getTop(el) : getTop(el)),
        width:    el.offsetWidth,
        height:    el.offsetHeight
    };
}

// mozilla bug! scrollbars not included in innerWidth/height
function getDocumentRect(el) {
    return {
        left:    0,
        top:    0,
        width:    (ts_menu_is_ie ?
            (ts_menu_is_ieBox ? document.body.clientWidth : document.documentElement.clientWidth) :
            window.innerWidth
            ),
        height:    (ts_menu_is_ie ?
            (ts_menu_is_ieBox ? document.body.clientHeight : document.documentElement.clientHeight) :
            window.innerHeight
            )
    };
}

function getScrollPos(el) {
    return {
        left:    (ts_menu_is_ie ?
            (ts_menu_is_ieBox ? document.body.scrollLeft : document.documentElement.scrollLeft) :
            window.pageXOffset
            ),
        top:    (ts_menu_is_ie ?
            (ts_menu_is_ieBox ? document.body.scrollTop : document.documentElement.scrollTop) :
            window.pageYOffset
            )
    };
}

/* end position functions */
TSMenu.prototype.position = function (relEl, sDir) {
    var dir = sDir;
    // find parent item rectangle, piRect
    var piRect;
    if (!relEl) {
        var pi = this.parentMenuItem;
        if (!this.parentMenuItem)
            return;

        relEl = document.getElementById(pi.id);
        if (dir == null)
            dir = pi instanceof TSMenuBut ? "vertical" : "horizontal";

        piRect = getOuterRect(relEl);
    }
    else if (relEl.left != null && relEl.top != null && relEl.width != null && relEl.height != null) {    // got a rect
        piRect = relEl;
    }
    else
        piRect = getOuterRect(relEl);

    var menuEl = document.getElementById(this.id);
    var menuRect = getOuterRect(menuEl);
    var docRect = getDocumentRect();
    var scrollPos = getScrollPos();
    var pMenu = this.parentMenu;

    if (dir == "vertical") {
        if (piRect.left + menuRect.width - scrollPos.left <= docRect.width)
            this.left = piRect.left;
        else if (docRect.width >= menuRect.width)
            this.left = docRect.width + scrollPos.left - menuRect.width;
        else
            this.left = scrollPos.left;

        if (piRect.top + piRect.height + menuRect.height <= docRect.height + scrollPos.top)
            this.top = piRect.top + piRect.height;
        else if (piRect.top - menuRect.height >= scrollPos.top)
            this.top = piRect.top - menuRect.height;
        else if (docRect.height >= menuRect.height)
            this.top = docRect.height + scrollPos.top - menuRect.height;
        else
            this.top = scrollPos.top;
    }
    else {
        if (piRect.top + menuRect.height - this.borderTop - this.paddingTop <= docRect.height + scrollPos.top)
            this.top = piRect.top - this.borderTop - this.paddingTop;
        else if (piRect.top + piRect.height - menuRect.height + this.borderTop + this.paddingTop >= 0)
            this.top = piRect.top + piRect.height - menuRect.height + this.borderBottom + this.paddingBottom + this.shadowBottom;
        else if (docRect.height >= menuRect.height)
            this.top = docRect.height + scrollPos.top - menuRect.height;
        else
            this.top = scrollPos.top;

        var pMenuPaddingLeft = pMenu ? pMenu.paddingLeft : 0;
        var pMenuBorderLeft = pMenu ? pMenu.borderLeft : 0;
        var pMenuPaddingRight = pMenu ? pMenu.paddingRight : 0;
        var pMenuBorderRight = pMenu ? pMenu.borderRight : 0;

        if (piRect.left + piRect.width + menuRect.width + pMenuPaddingRight +
            pMenuBorderRight - this.borderLeft + this.shadowRight <= docRect.width + scrollPos.left)
            this.left = piRect.left + piRect.width + pMenuPaddingRight + pMenuBorderRight - this.borderLeft;
        else if (piRect.left - menuRect.width - pMenuPaddingLeft - pMenuBorderLeft + this.borderRight + this.shadowRight >= 0)
            this.left = piRect.left - menuRect.width - pMenuPaddingLeft - pMenuBorderLeft + this.borderRight + this.shadowRight;
        else if (docRect.width >= menuRect.width)
            this.left = docRect.width + scrollPos.left - menuRect.width;
        else
            this.left = scrollPos.left;
    }
}

function addEvent(el, evname, func) {
    if (el.addEventListener)
        el.addEventListener(evname, func, true);
    else if (el.attachEvent)el.attachEvent("on" + evname, func);
    else el["on" + evname] = func;
}

function stopEvent(ev) {
    if (ts_menu_is_ie) {
        ev.cancelBubble = true;
        ev.returnValue = false;
    }
    else {
        ev.preventDefault();
        ev.stopPropagation();
    }
}

function documentKeyPress(ev) {
    if (ev.ctrlKey && ev.keyCode == 13) {
        try {
            var form = (ev.srcElement || ev.target).form;
            form.submit();
        } catch (e) {
        }
    }

    var key = String.fromCharCode((ts_menu_is_ie || ts_menu_is_opera) ? ev.keyCode : ev.charCode).toUpperCase();
    var url = ts_menu_keymap[key];
    // fix for altGr key
    if (typeof url != "undefined" && ((!altGrFix && ev.altKey && ev.ctrlKey && !ev.shiftKey) || (altGrFix && ev.altKey && !ev.ctrlKey && ev.shiftKey))) {
        // Fix javascript call
        // Reformat javascript:copyToClipboard(\"value\") -> javascript:copyToClipboard("value")
        var idx;
        idx = url.indexOf("javascript:copyToClipboard(\\");
        if (idx != -1) {
            var newUrl = "javascript:copyToClipboard(\"";
            idx = newUrl.indexOf("(");
            if (idx != -1) {
                newUrl += url.substring(idx + 3, url.length - 3);
                newUrl += "\")";
                url = newUrl;
            }
        }
        document.location = url;
        stopEvent(ev);
    }
}

addEvent(self.document, (ts_menu_is_ie || ts_menu_is_opera) ? "keydown" : "keypress", documentKeyPress);