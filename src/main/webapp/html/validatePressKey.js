function validInteger(id, evt, value) {
    var charCode;
    if (!evt) {
        evt = event;
    }
    if (evt.charCode) {
        charCode = evt.charCode;
    } else if (evt.keyCode) {
        charCode = evt.keyCode;
    } else if (evt.which) {
        charCode = evt.which;
    } else {
        charCode = 0;
    }
    if (charCode == 8 || charCode == 46) {
        return true;
    }
    if (value.length == 0 && (charCode == 45 || charCode == 43)) {
        return true;
    }
    if (charCode<48 || charCode>57) {
        document.getElementById(id+"_error").style.display = "inline";
        document.getElementById(id+"_error").title = ERRROR_CORRECT_FIELDS;
        return false;
    }
    document.getElementById(id+"_error").style.display = "none";
    return true;
}

function validFloat(id, evt, value) {
    var charCode;
    if (!evt) {
        evt = event;
    }
    if (evt.charCode) {
        charCode = evt.charCode;
    } else if (evt.keyCode) {
        charCode = evt.keyCode;
    } else if (evt.which) {
        charCode = evt.which;
    } else {
        charCode = 0;
    }
    if (charCode == 8 || charCode == 46) {
        return true;
    }
    if (value.length == 0 && (charCode == 45 || charCode == 43)) {
        return true;
    } else {
        if (charCode == 45 && value.indexOf("E") == value.length) {
            return true;
        }
    }
    if (value.length != 0 && value.indexOf(".") == -1 && value.indexOf(",") == -1 && (charCode == 46 || charCode == 44)) {
        return true;
    }
    if (value.length != 0 && value.indexOf("E") == -1 && (charCode == 69)) {
        return true;
    }
    if (value.length != 0 && value.indexOf("E") != -1 && (charCode == 45 || charCode == 43)) {
        return true;
    }
    if (charCode<48 || charCode>57) {
        document.getElementById(id+"_error").style.display = "inline";
        document.getElementById(id+"_error").title = ERRROR_CORRECT_FIELDS;
        return false;
    }
    document.getElementById(id+"_error").style.display = "none";
    return true;
}