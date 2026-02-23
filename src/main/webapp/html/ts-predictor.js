(function (window, $) {
    "use strict";

    if (!$) {
        return;
    }

    var Predictor = {};

    Predictor.escapeRegExp = function (value) {
        var text = value === null || typeof value === "undefined" ? "" : value.toString();
        var escaped = "";
        var special = "\\^$.*+?()[]{}|";
        for (var i = 0; i < text.length; i++) {
            var ch = text.charAt(i);
            if (special.indexOf(ch) > -1) {
                escaped += "\\";
            }
            escaped += ch;
        }
        return escaped;
    };

    Predictor.trimText = function (value) {
        return (value === null || typeof value === "undefined" ? "" : value.toString()).replace(/^\s+|\s+$/g, "");
    };

    Predictor.decodeEntities = function (value) {
        // Intentionally decode via <textarea>. Do not replace with <div> (unsafe for future HTML handling).
        var area = document.createElement("textarea");
        area.innerHTML = value;
        return area.value;
    };

    Predictor.containsHtml = function (value) {
        var rawText = value === null || typeof value === "undefined" ? "" : value.toString();
        if (!rawText) {
            return false;
        }
        if (rawText.indexOf("<") > -1 && rawText.indexOf(">") > -1) {
            return true;
        }
        if (rawText.indexOf("&lt;") > -1 || rawText.indexOf("&gt;") > -1) {
            var decoded = Predictor.decodeEntities(rawText);
            return decoded.indexOf("<") > -1 && decoded.indexOf(">") > -1;
        }
        return false;
    };

    Predictor.rawValue = function (value) {
        if (value === null || typeof value === "undefined") {
            return "";
        }
        return Predictor.trimText(value.toString());
    };

    Predictor.isSearchRequestItem = function (value, labelHtml) {
        var rawValue = Predictor.rawValue(value);
        var rawLabel = labelHtml === null || typeof labelHtml === "undefined" ? "" : labelHtml.toString();
        if (!rawValue) {
            return true;
        }
        if (rawLabel && rawValue === rawLabel) {
            return true;
        }
        if (Predictor.containsHtml(rawValue)) {
            return true;
        }
        if (rawLabel && Predictor.containsHtml(rawLabel) && rawValue.charAt(0) !== "#" && rawValue.indexOf("_") === -1 && rawValue.indexOf("u-") !== 0) {
            return true;
        }
        if (rawValue.charAt(0) !== "#" && rawValue.indexOf("_") === -1 && rawValue.indexOf("u-") !== 0) {
            return true;
        }
        return false;
    };

    Predictor.key = function (value, labelHtml) {
        var rawValue = Predictor.rawValue(value);
        if (Predictor.isSearchRequestItem(rawValue, labelHtml)) {
            return "";
        }
        if (rawValue.charAt(0) === "#") {
            var taskSeparator = rawValue.indexOf("_");
            return taskSeparator > -1 ? rawValue.substring(0, taskSeparator) : rawValue;
        }
        var userSeparator = rawValue.indexOf("_u-");
        if (userSeparator > -1) {
            return rawValue.substring(0, userSeparator);
        }
        var separator = rawValue.indexOf("_");
        return separator > -1 ? rawValue.substring(0, separator) : rawValue;
    };

    Predictor.title = function (labelHtml, key) {
        var rawLabel = labelHtml || "";
        var text = rawLabel
            .replace(/<img\b[^>]*>/gi, " ")
            .replace(/<[^>]+>/g, " ");
        text = Predictor.trimText(Predictor.decodeEntities(text).replace(/\s+/g, " "));
        if (!key || !text) {
            return text;
        }
        var shortKey = key.charAt(0) === "#" ? key.substring(1) : key;
        if (!shortKey) {
            return text;
        }
        var trailingKey = new RegExp("\\s*\\[" + Predictor.escapeRegExp(shortKey) + "\\]\\s*$");
        return Predictor.trimText(text.replace(trailingKey, ""));
    };

    Predictor.isUserItem = function (rawValue) {
        var value = Predictor.rawValue(rawValue);
        if (!value) {
            return false;
        }
        return value.indexOf("_u-") > -1 || value.indexOf("u-") === 0;
    };

    Predictor.userAvatar = function (title) {
        var avatarUtils = window.TSAvatar || {};
        var name = Predictor.trimText(title);
        if (!name) {
            name = "user";
        }
        var color = typeof avatarUtils.colorFromName === "function"
            ? avatarUtils.colorFromName("search-user:" + name)
            : "hsl(205, 60%, 45%)";
        var avatar = $("<span class='ts-search-item__avatar' aria-hidden='true'></span>");
        avatar.text(typeof avatarUtils.getAvatarInitial === "function"
            ? avatarUtils.getAvatarInitial(name)
            : "?");
        if (avatar[0] && avatar[0].style) {
            avatar[0].style.setProperty("--ts-search-avatar-bg", color);
            avatar[0].style.setProperty("--ts-search-avatar-fg", "#ffffff");
            avatar[0].style.setProperty("--ts-search-avatar-border", "color-mix(in srgb, " + color + " 62%, #1f2937)");
        }
        return avatar;
    };

    Predictor.icons = function (labelHtml) {
        var icons = $("<span class='ts-search-item__icons' aria-hidden='true'></span>");
        var rawLabel = labelHtml || "";
        var imageRegex = /<img\b[^>]*\bsrc=['"]([^'"]+)['"][^>]*>/gi;
        var match;
        while ((match = imageRegex.exec(rawLabel)) !== null) {
            icons.append($("<img/>").attr("src", match[1]));
        }
        return icons;
    };

    Predictor.renderItem = function (ul, item, options) {
        var opts = options || {};
        var queryLabel = opts.queryLabel || "Search";
        var queryPrefix = opts.searchPrefix || (queryLabel + ": ");
        var inputSelector = opts.inputSelector || "#key";
        try {
            var isSearchRow = Predictor.isSearchRequestItem(item.value, item.label);
            var key = Predictor.key(item.value, item.label);
            var title = Predictor.title(item.label, key);
            if (isSearchRow && !title) {
                title = Predictor.trimText($(inputSelector).val());
            }
            var row = $("<a class='ts-search-item'></a>");
            if (isSearchRow) {
                row.addClass("ts-search-item--query");
                row.append($("<span class='ts-search-item__key ts-search-item__key--query'></span>").text(queryLabel));
            } else if (key) {
                row.append($("<span class='ts-search-item__key'></span>").text(key));
            } else {
                row.addClass("ts-search-item--no-key");
            }
            var main = $("<span class='ts-search-item__main'></span>");
            var icons = Predictor.icons(item.label);
            if (!isSearchRow && Predictor.isUserItem(item.value)) {
                icons.find("img[src*='arw.usr']").remove();
                if (!icons.find(".ts-search-item__avatar").length) {
                    icons.prepend(Predictor.userAvatar(title || key));
                }
            }
            main.append(icons);
            main.append($("<span class='ts-search-item__title'></span>").text(isSearchRow ? (queryPrefix + title) : title));
            row.append(main);
            return $("<li></li>")
                .data("item.autocomplete", item)
                .append(row)
                .appendTo(ul);
        } catch (e) {
            var fallbackTitle = Predictor.title(item && item.label ? item.label : "", "");
            if (!fallbackTitle) {
                fallbackTitle = Predictor.rawValue(item && item.value ? item.value : "");
            }
            if (!fallbackTitle) {
                fallbackTitle = Predictor.trimText(Predictor.decodeEntities(item && item.label ? item.label : "").replace(/<[^>]+>/g, " "));
            }
            return $("<li></li>")
                .data("item.autocomplete", item)
                .append($("<a class='ts-search-item ts-search-item--no-key'></a>")
                    .append($("<span class='ts-search-item__main'></span>")
                        .append($("<span class='ts-search-item__title'></span>").text(fallbackTitle))))
                .appendTo(ul);
        }
    };

    Predictor.extractToken = function (rawValue, marker) {
        var value = Predictor.rawValue(rawValue);
        if (!value) {
            return "";
        }
        if (marker === "u-") {
            return value.indexOf("u-") === 0 ? value.substring(2) : "";
        }
        var markerPos = value.indexOf(marker);
        if (markerPos > -1) {
            return value.substring(markerPos + marker.length);
        }
        return "";
    };

    Predictor.safeQuery = function (value) {
        var text = Predictor.rawValue(value);
        return encodeURIComponent(text.replace(/<(?:.|\n)*?>/gm, ""));
    };

    Predictor.safeToken = function (value) {
        return encodeURIComponent(Predictor.rawValue(value));
    };

    window.TSPredictor = Predictor;
})(window, window.jQuery);
