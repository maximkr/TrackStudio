var HintClass = "hintTextbox";
var HintActiveClass = "hintActive";

// define a custom method on the string class to trim leading and training spaces
String.prototype.trim = function() {
    return this.replace(/^\s+|\s+$/g, '');
};

function initHintTextboxes() {
    var inputs = document.getElementsByTagName('input');
    for (var i = 0; i < inputs.length; i++) {
        var input = inputs[i];
        if (input.type != "text" && input.type != "password")
            continue;

        if (input.className.indexOf(HintClass) != -1) {
            input.hintText = input.title;
            input.className = HintClass;
            input.onfocus = onHintTextboxFocus;
            input.onblur = onHintTextboxBlur;
        }
    }
}

function onHintTextboxFocus() {
    var input = this;
    if (input.value.trim() == input.hintText) {
        input.value = "";
        input.className = HintActiveClass;
    }
}

function onHintTextboxBlur() {
    var input = this;
    if (input.value.trim().length == 0) {
        input.value = input.hintText;
        input.className = HintClass;
    }
}

window.onload = initHintTextboxes;