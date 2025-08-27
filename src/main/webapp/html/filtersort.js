function saveValue(Sender) {
    var value = '';
    for (m = 0; m < Sender.to.options.length; m++) {
        value += Sender.to.options[m].value + ";";
    }
    Sender.fields.value = value;

}

function saveValueTo(Sender, to) {
    var value = '';
    for (m = 0; m < Sender.elements[to].options.length; m++) {
        value += Sender.elements[to].options[m].value + ";";
    }
    Sender.elements["hidden" + to].value = value;
}

function addSelected(Sender) {
    for (j = 0; j < Sender.from.options.length; j++) {
        if (Sender.from.options[j].selected) {

            var option = document.createElement("OPTION");
            option.value = "\u00a0\u00a0\u00a0\u00a0\u00a0" + Sender.from.options[j].value;
            option.text = "\u00a0\u00a0\u00a0\u00a0\u00a0" + Sender.from.options[j].text;
            var elOptOld = Sender.from.options[j];
            try {
                Sender.to.add(option, null); // standards compliant; doesn't work in IE
            }
            catch(ex) {
                Sender.to.add(option, j); // IE only
            }
        }
    }

    for (j = (Sender.from.options.length - 1); j >= 0; j--) {
        if (Sender.from.options[j].selected) {
            Sender.from.remove(j);
        }
    }
    saveValue(Sender);
}


function removeSelected(Sender) {
    for (j = 0; j < Sender.to.options.length; j++) {
        if (Sender.to.options[j].selected) {
            var option = document.createElement("OPTION");
            option.value = Sender.to.options[j].value.substring(5);
            option.text = Sender.to.options[j].text.substring(5);

            var elOptOld = Sender.to.options[j];
            try {
                Sender.from.add(option, null); // standards compliant; doesn't work in IE
            }
            catch(ex) {
                Sender.from.add(option, j); // IE only
            }

            if (Sender.to.options[j].text.indexOf("\u2193") > -1 || Sender.to.options[j].text.indexOf("\u2191") > -1) {
                var n = Sender.to.options[j].text.charAt(2);

                for (m = 0; m < Sender.to.options.length; m++) {
                    if (Sender.to.options[m].text.indexOf("\u2193") > -1 || Sender.to.options[m].text.indexOf("\u2191") > -1) {
                        if (Sender.to.options[m].text.charAt(2) > n) {
                            var p = Sender.to.options[m].text.charAt(2);
                            Sender.to.options[m].text = Sender.to.options[m].text.substring(0, 2) + (p - 1) + ") " + Sender.to.options[m].text.substring(5);
                            Sender.to.options[m].value = Sender.to.options[m].value.substring(0, 2) + (p - 1) + ") " + Sender.to.options[m].value.substring(5);
                        }
                    }
                }

                Sender.to.options[j].text = "\u00a0\u00a0\u00a0\u00a0\u00a0" + Sender.to.options[j].text.substring(5);
                Sender.to.options[j].value = "\u00a0\u00a0\u00a0\u00a0\u00a0" + Sender.to.options[j].value.substring(5);
                k = Sender.counter.value;
                if (k != null && k.length > 0)
                    Sender.counter.value = k.substring(1);

            }
        }
    }

    var t = Sender.to.options.length;
    for (j = (t - 1); j >= 0; j--) {
        if (Sender.to.options[j].selected) {
            Sender.to.remove(j);
        }
    }
    saveValue(Sender);
}

function addSelectedItems(Sender, from, to) {
    var selectElement = new Array();
    var count = 0;
    for (var j = 0; j < Sender.elements[from].options.length; j++) {
        if (Sender.elements[from].options[j].selected && !Sender.elements[from].options[j].disabled) {
            selectElement[count] = Sender.elements[from].options[j].value;
            count++;
            var option = document.createElement("OPTION");
            option.value = Sender.elements[from].options[j].value;
            option.text = Sender.elements[from].options[j].text;
            var elOptOld = Sender.elements[from].options[j];

            try {
                Sender.elements[to].add(option, null); // standards compliant; doesn't work in IE
            }
            catch(ex) {
                Sender.elements[to].add(option, j); // IE only
            }
        }
    }

    var t = Sender.elements[from].options.length;
    for (j = 0; j != t; j++) {
        var valueElement = Sender.elements[from].options[j].value;
        for (var k = 0; k != count; k++) {
            if (valueElement == selectElement[k]) {
                Sender.elements[from].remove(j);
                j--;
                t--;
            }
        }
    }
    saveValueTo(Sender, to);
}


function removeSelectedItems(Sender, from, to) {
    var selectElement = new Array();
    var count = 0;
    for (var j = 0; j < Sender.elements[to].options.length; j++) {
        if (Sender.elements[to].options[j].selected && !Sender.elements[to].options[j].disabled) {
            selectElement[count] = Sender.elements[to].options[j].value;
            count++;
            var option = document.createElement("OPTION");
            var indxValue = Sender.elements[to].options[j].value.indexOf('(*');
            var indxText = Sender.elements[to].options[j].text.indexOf('(*');
            if (indxValue > -1) {
                option.value = Sender.elements[to].options[j].value.substr(0, indxValue);
            } else {
                option.value = Sender.elements[to].options[j].value;
            }
            if (indxText > -1) {
                option.text = Sender.elements[to].options[j].text.substr(0, indxText);
            } else {
                option.text = Sender.elements[to].options[j].text;
            }

            var elOptOld = Sender.elements[to].options[j];
            try {
                Sender.elements[from].add(option, null); // standards compliant; doesn't work in IE
            } catch(ex) {
                Sender.elements[from].add(option, j); // IE only
            }
        }
    }
    var t = Sender.elements[to].options.length;
    for (j = 0; j != t; j++) {
        var valueElement = Sender.elements[to].options[j].value;
        for (var k = 0; k != count; k++) {
            if (valueElement == selectElement[k]) {
                Sender.elements[to].remove(j);
                j--;
                t--;
            }
        }
    }
    saveValueTo(Sender, to);
}

function setOptions(Sender, from, to, opt) {
    for (j = 0; j < Sender.elements[to].options.length; j++) {
        if (Sender.elements[to].options[j].selected) {
            if (Sender.elements[to].options[j].text.indexOf(opt) == -1) {
                // not set yet
                if (Sender.elements[to].options[j].text.indexOf("(*") > -1) {
                    // some option set
                    var jindexText = Sender.elements[to].options[j].text.indexOf("(*");
                    var jindexValue = Sender.elements[to].options[j].value.indexOf("(*");
                    var eolText = Sender.elements[to].options[j].text.indexOf(")", jindexText);
                    var eolValue = Sender.elements[to].options[j].value.indexOf(")", jindexValue);
                    Sender.elements[to].options[j].text = Sender.elements[to].options[j].text.substring(0, eolText) + ", " + opt + ")";
                    Sender.elements[to].options[j].value = Sender.elements[to].options[j].value.substring(0, eolValue) + ", " + opt + ")";
                } else {
                    // none option set
                    Sender.elements[to].options[j].text = Sender.elements[to].options[j].text + " (* " + opt + ")";
                    Sender.elements[to].options[j].value = Sender.elements[to].options[j].value + " (* " + opt + ")";
                }

            } else {
                var text = Sender.elements[to].options[j].text;
                var val = Sender.elements[to].options[j].value;
                Sender.elements[to].options[j].text = deleteText(text, opt);
                Sender.elements[to].options[j].value = deleteText(val, opt);
            }
            Sender.elements[to].options[j].selected = true;
        }
    }
    saveValueTo(Sender, to);
}

function deleteText(text, opt) {
    opt = " " + opt;
    var intOpt = text.indexOf(opt);
    var sizeOpt = opt.length;
    if (text.charAt(intOpt + sizeOpt) == ",") {
        sizeOpt = sizeOpt + 1;
    }
    if (text.charAt(intOpt - 1) == ",") {
        intOpt = intOpt - 1;
        sizeOpt = sizeOpt + 1;
    }
    if (text.charAt(intOpt - 1) == "*" && text.charAt(intOpt + sizeOpt) == ")") {
        intOpt = intOpt - 2;
        sizeOpt = sizeOpt + 3;
    }
    return text.substr(0, intOpt) + text.substr(intOpt + sizeOpt, text.length);
}

function resetOptions(Sender, from, to) {
    for (j = 0; j < Sender.elements[to].options.length; j++) {
        if (Sender.elements[to].options[j].selected) {

            if (Sender.elements[to].options[j].text.indexOf("(*") > -1) {
                // some option set
                var jindexText = Sender.elements[to].options[j].text.indexOf("(*");
                var jindexValue = Sender.elements[to].options[j].value.indexOf("(*");


                Sender.elements[to].options[j].text = Sender.elements[to].options[j].text.substring(0, jindexText - 1);
                Sender.elements[to].options[j].value = Sender.elements[to].options[j].value.substring(0, jindexValue - 1);
            }
            Sender.elements[to].options[j].selected = true;
        }
    }
    saveValueTo(Sender, to);
}

function sortAsc(Sender) {
    for (j = 0; j < Sender.to.options.length; j++) {

        if (Sender.to.options[j].selected) {
            if (Sender.to.options[j].text.indexOf("\u2191") == -1) {
                if (Sender.to.options[j].text.indexOf("\u2193") > -1) {
                    Sender.to.options[j].text = "\u2191" + Sender.to.options[j].text.substring(1);
                    Sender.to.options[j].value = "+" + Sender.to.options[j].value.substring(1);
                } else {
                    var k = ".";
                    if (Sender.counter.value == null || Sender.counter.value.length == 0) Sender.counter.value = ".";
                    else {
                        k = Sender.counter.value + ".";
                        if (k.length < 10)   Sender.counter.value = k;
                    }
                    ;
                    if (k.length < 10) {
                        Sender.to.options[j].text = "\u2191(" + k.length + ") " + Sender.to.options[j].text.substring(5);
                        Sender.to.options[j].value = "+(" + k.length + ") " + Sender.to.options[j].value.substring(5);
                    }

                }
            }
            Sender.to.options[j].selected = true;
        }
    }
    saveValue(Sender);
}

function sortDesc(Sender) {
    for (var j = 0; j < Sender.to.options.length; j++) {

        if (Sender.to.options[j].selected) {
            if (Sender.to.options[j].text.indexOf("\u2193") == -1) {
                if (Sender.to.options[j].text.indexOf("\u2191") > -1) {
                    Sender.to.options[j].text = "\u2193" + Sender.to.options[j].text.substring(1);
                    Sender.to.options[j].value = "-" + Sender.to.options[j].value.substring(1);
                }
                else {
                    var k = ".";
                    if (Sender.counter.value == null || Sender.counter.value.length == 0) Sender.counter.value = k;
                    else {
                        k = Sender.counter.value + ".";

                        if (k.length < 10)   Sender.counter.value = k;
                    }
                    if (k.length < 10) {
                        Sender.to.options[j].text = "\u2193(" + k.length + ") " + Sender.to.options[j].text.substring(5);
                        Sender.to.options[j].value = "-(" + k.length + ") " + Sender.to.options[j].value.substring(5);
                    }
                }
            }
            Sender.to.options[j].selected = true;
        }
    }
    saveValue(Sender);
}


function sortNone(Sender) {
    for (j = 0; j < Sender.to.options.length; j++) {
        if (Sender.to.options[j].selected) {
            if (Sender.to.options[j].text.indexOf("\u2193") > -1 || Sender.to.options[j].text.indexOf("\u2191") > -1) {
                var n = Sender.to.options[j].text.charAt(2);

                for (m = 0; m < Sender.to.options.length; m++) {
                    if ((Sender.to.options[m].text.indexOf("\u2193") > -1 || Sender.to.options[m].text.indexOf("\u2191") > -1 ) && Sender.to.options[m].text.charAt(2) > n) {
                        var p = Sender.to.options[m].text.charAt(2);
                        Sender.to.options[m].text = Sender.to.options[m].text.substring(0, 2) + (p - 1) + ") " + Sender.to.options[m].text.substring(5);
                        Sender.to.options[m].value = Sender.to.options[m].value.substring(0, 2) + (p - 1) + ") " + Sender.to.options[m].value.substring(5);
                    }
                }


                Sender.to.options[j].text = "\u00a0\u00a0\u00a0\u00a0\u00a0" + Sender.to.options[j].text.substring(5);
                Sender.to.options[j].value = "\u00a0\u00a0\u00a0\u00a0\u00a0" + Sender.to.options[j].value.substring(5);
                k = Sender.counter.value;
                if (k != null && k.length > 0) Sender.counter.value = k.substring(1);
            }
        }
    }
    saveValue(Sender);
}    