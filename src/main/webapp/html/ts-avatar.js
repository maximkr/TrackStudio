(function (window) {
    "use strict";

    if (!window) {
        return;
    }

    var TSAvatar = window.TSAvatar || {};

    TSAvatar.normalizeText = function (value) {
        var text = value === null || typeof value === "undefined" ? "" : value.toString();
        return text.replace(/\s+/g, " ").replace(/^\s+|\s+$/g, "");
    };

    TSAvatar.isLetterOrDigit = function (ch) {
        if (!ch) {
            return false;
        }
        return /[0-9A-Za-z]/.test(ch) || ch.toLowerCase() !== ch.toUpperCase();
    };

    TSAvatar.getAvatarInitial = function (fullName) {
        var text = TSAvatar.normalizeText(fullName);
        if (!text) {
            return "?";
        }
        var chars = Array.from(text);
        for (var i = 0; i < chars.length; i++) {
            if (TSAvatar.isLetterOrDigit(chars[i])) {
                return chars[i].toUpperCase();
            }
        }
        return chars[0] ? chars[0].toUpperCase() : "?";
    };

    TSAvatar.colorFromName = function (value) {
        var text = value || "";
        var hash = 0;
        for (var i = 0; i < text.length; i++) {
            hash = ((hash << 5) - hash) + text.charCodeAt(i);
            hash |= 0;
        }
        var absHash = Math.abs(hash);
        var hue = absHash % 360;
        var sat = 56 + (absHash % 12);
        var light = 40 + (absHash % 10);
        return "hsl(" + hue + ", " + sat + "%, " + light + "%)";
    };

    TSAvatar.applyPalette = function (node, seed, varPrefix) {
        if (!node || !node.style) {
            return;
        }
        var color = TSAvatar.colorFromName(seed || "");
        node.style.setProperty("--" + varPrefix + "-bg", color);
        node.style.setProperty("--" + varPrefix + "-fg", "#ffffff");
        node.style.setProperty("--" + varPrefix + "-border", "color-mix(in srgb, " + color + " 62%, #1f2937)");
    };

    window.TSAvatar = TSAvatar;
})(window);
