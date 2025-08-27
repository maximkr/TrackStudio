var labl = document.getElementsByTagName("label");
var pageCookie = getTabCookie(encodeURI(top.document.title));

for (var j = 0; j < labl.length; j++) {
    if (labl[j].className.indexOf("expandable") > -1) {
        if (pageCookie == labl[j].htmlFor) labelShow(labl[j]);
        else labelHide(labl[j]);
    }

    if (labl[j].className.indexOf("openProperty") > -1) {
        var cookie = getTabCookie(labl[j].htmlFor);
        if (cookie == labl[j].htmlFor) {
            propertyShow(labl[j]);
        } else if (cookie == "false") {
            propertyHide(labl[j]);
        } else propertyShow(labl[j]);
    }
}