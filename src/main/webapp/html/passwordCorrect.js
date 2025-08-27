var defaultColor = "#e0e0e0";
var simpleColor = "#FC0000";
var normalReliableColor = "#6762E5";
var regNumber = /\d/;
var regLetters  = /[A-Z]/;
var regLettersCapital  = /[a-z]/;

function  correctPassword(id, action) {
    if (action == "true") {
        var valuePassword = document.getElementById(id).value;
        var simple = document.getElementById("warn_"+id+"_simple");
        var normal = document.getElementById("warn_"+id+"_normal");
        var label = document.getElementById("warn_"+id+"_label");

        if (valuePassword.length == 0) {
            setTextAndColor(simple, normal, label, defaultColor,defaultColor, PASSWORD_CORRECT, "0");
        }
        if (valuePassword.length > 0 && valuePassword.length < 6) {
            setTextAndColor(simple, normal, label, simpleColor, defaultColor, SIMPLE_PASSWORD, "1");
        }
        if (valuePassword.length >= 6) {
            if (regNumber.exec(valuePassword) && regLetters.exec(valuePassword) && regLettersCapital.exec(valuePassword))
                setTextAndColor(simple, normal, label, normalReliableColor, defaultColor, REBIALE_PASSWORD, "2");
        }
    }
}

function correctPasswordValue(valuePassword) {
    return (valuePassword.length >= 6 && regNumber.exec(valuePassword) && regLetters.exec(valuePassword) && regLettersCapital.exec(valuePassword));
}

function setTextAndColor(simple, normal, label, color, defaultColor, text, number) {
    switch (number) {
        case (number = "0"):
            simple.style.backgroundColor = defaultColor;
            normal.style.backgroundColor = defaultColor;
            label.style.color = defaultColor;
            label.innerHTML = text;
            break ;
        case (number = "1"):
            simple.style.backgroundColor = color;
            normal.style.backgroundColor = defaultColor;
            label.style.color = color;
            label.innerHTML = text;
            break ;
        case (number = "2"):
            simple.style.backgroundColor = color;
            normal.style.backgroundColor = color;
            label.style.color = color;
            label.innerHTML = text;
            break ;
        default:
            break;
    }
}